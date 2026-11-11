"""Polygon mesh toolkit for the NewGunrizons vehicles (Wavefront OBJ + one texture atlas per vehicle).

Authoring space ("vehicle space"): X to the vehicle's right, Y up, Z forward (towards the nose), y = 0 on the ground.
One unit is 1/16 block at layout scale 1. The OBJ is written in Java model space, the space the renderer draws in:
(-X, -Y, -Z), with the triangle winding flipped to keep faces pointing outwards.

Groups: geometry goes into named groups (hull, w0.., t0.., g0.., r0..). Faces painted with an emissive material
(glow, light, tail) go into "<group>_glow", which the game draws full bright.

Materials are painted procedurally into the atlas. "paint" and "paint2" get three tiles (one per projection axis) so
panel lines and weathering follow the surface; most others one tile. Texture coordinates are planar projections in a
cube of side S around the hull centre (same texel density everywhere), except tyres and wheel caps, which carry their
own coordinates.
"""
import math
from contextlib import contextmanager

import numpy as np
from PIL import Image, ImageFilter
from scipy.spatial import ConvexHull

EMISSIVE = {"glow", "light", "tail", "core"}


def _v(p):
    return np.asarray(p, dtype=float)


def rot_matrix(rx=0.0, ry=0.0, rz=0.0):
    """Rotation in degrees, applied X then Y then Z (extrinsic)."""
    a, b, c = (math.radians(t) for t in (rx, ry, rz))
    mx = np.array([[1, 0, 0], [0, math.cos(a), -math.sin(a)], [0, math.sin(a), math.cos(a)]])
    my = np.array([[math.cos(b), 0, math.sin(b)], [0, 1, 0], [-math.sin(b), 0, math.cos(b)]])
    mz = np.array([[math.cos(c), -math.sin(c), 0], [math.sin(c), math.cos(c), 0], [0, 0, 1]])
    return mz @ my @ mx


# ============================================================================ polygons
def _area2(poly):
    return sum(poly[i][0] * poly[(i + 1) % len(poly)][1] - poly[(i + 1) % len(poly)][0] * poly[i][1]
               for i in range(len(poly)))


def ccw(poly):
    return list(poly) if _area2(poly) > 0 else list(reversed(poly))


def earclip(poly):
    """Triangulate a simple CCW polygon; returns index triples."""
    idx = list(range(len(poly)))
    out = []

    def inside(p, a, b, c):
        def s(p1, p2, p3):
            return (p1[0] - p3[0]) * (p2[1] - p3[1]) - (p2[0] - p3[0]) * (p1[1] - p3[1])
        d1, d2, d3 = s(p, a, b), s(p, b, c), s(p, c, a)
        return not ((d1 < 0 or d2 < 0 or d3 < 0) and (d1 > 0 or d2 > 0 or d3 > 0))

    guard = 0
    while len(idx) > 3 and guard < 10000:
        guard += 1
        for k in range(len(idx)):
            i0, i1, i2 = idx[k - 1], idx[k], idx[(k + 1) % len(idx)]
            a, b, c = poly[i0], poly[i1], poly[i2]
            if (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]) <= 1e-9:
                continue
            if any(inside(poly[j], a, b, c) for j in idx if j not in (i0, i1, i2)):
                continue
            out.append((i0, i1, i2))
            idx.pop(k)
            break
        else:
            break
    if len(idx) == 3:
        out.append(tuple(idx))
    return out


def offset_poly(poly, d):
    """Move every edge of a CCW polygon inwards by d (miter joins)."""
    n = len(poly)
    out = []
    for i in range(n):
        p0, p1, p2 = _v(poly[i - 1]), _v(poly[i]), _v(poly[(i + 1) % n])
        e1, e2 = p1 - p0, p2 - p1
        n1 = np.array([-e1[1], e1[0]]) / (np.linalg.norm(e1) or 1)
        n2 = np.array([-e2[1], e2[0]]) / (np.linalg.norm(e2) or 1)
        m = n1 + n2
        k = 1 + float(n1 @ n2)
        out.append(tuple(p1 + m * d / max(k, 0.3)))
    return out


def superellipse(rx, ry, n, p=2.0, phase=0.0):
    """CCW points of |x/rx|^p + |y/ry|^p = 1 (p=2 ellipse, larger p squarer)."""
    pts = []
    for k in range(n):
        a = 2 * math.pi * k / n + phase
        c, s = math.cos(a), math.sin(a)
        pts.append((rx * math.copysign(abs(c) ** (2 / p), c), ry * math.copysign(abs(s) ** (2 / p), s)))
    return pts


# ============================================================================ mesh
class Mesh:
    def __init__(self):
        self.groups = {}
        self.cur = "hull"
        self._xf = [(np.eye(3), np.zeros(3))]
        self._mirror = False

    # ---------------------------------------------------------------- state
    @contextmanager
    def group(self, name):
        old = self.cur
        self.cur = name
        try:
            yield
        finally:
            self.cur = old

    @contextmanager
    def at(self, pos=(0, 0, 0), rot=(0, 0, 0)):
        """Place the following geometry at pos, rotated by rot (degrees, X then Y then Z)."""
        m, t = self._xf[-1]
        r = rot_matrix(*rot)
        self._xf.append((m @ r, m @ _v(pos) + t))
        try:
            yield
        finally:
            self._xf.pop()

    @contextmanager
    def mirrored(self):
        """Everything built inside also appears mirrored across x = 0."""
        old = self._mirror
        self._mirror = True
        try:
            yield
        finally:
            self._mirror = old

    # ---------------------------------------------------------------- primitives
    def tri(self, p, mat, uv=None, n=None):
        m, t = self._xf[-1]
        p = [m @ _v(q) + t for q in p]
        n = None if n is None else [m @ _v(q) for q in n]
        self._add(p, mat, uv, n)
        if self._mirror:
            f = np.array([-1.0, 1, 1])
            p2 = [p[0] * f, p[2] * f, p[1] * f]
            n2 = None if n is None else [n[0] * f, n[2] * f, n[1] * f]
            uv2 = None if uv is None else [uv[0], uv[2], uv[1]]
            self._add(p2, mat, uv2, n2)

    def _add(self, p, mat, uv, n):
        e = np.cross(p[1] - p[0], p[2] - p[0])
        if np.linalg.norm(e) < 1e-9:
            return
        self.groups.setdefault(self.cur, []).append((p, mat, uv, n))

    def quad(self, a, b, c, d, mat, uv=None, n=None, hint=None):
        if hint is not None:
            A, B, D = _v(a), _v(b), _v(d)
            if np.cross(B - A, D - A) @ _v(hint) < 0:
                a, b, c, d = d, c, b, a
                uv = None if uv is None else uv[::-1]
                n = None if n is None else n[::-1]
        self.tri([a, b, c], mat, None if uv is None else [uv[0], uv[1], uv[2]],
                 None if n is None else [n[0], n[1], n[2]])
        self.tri([a, c, d], mat, None if uv is None else [uv[0], uv[2], uv[3]],
                 None if n is None else [n[0], n[2], n[3]])

    def hull(self, pts, mat):
        """Convex hull of the points (flat shaded)."""
        pts = np.array(pts, dtype=float)
        h = ConvexHull(pts)
        c = pts[h.vertices].mean(axis=0)
        for s in h.simplices:
            a, b, d = pts[s[0]], pts[s[1]], pts[s[2]]
            if np.cross(b - a, d - a) @ ((a + b + d) / 3 - c) < 0:
                b, d = d, b
            self.tri([a, b, d], mat)

    def box(self, x0, x1, y0, y1, z0, z1, mat, bevel=0.0):
        x0, x1 = min(x0, x1), max(x0, x1)
        y0, y1 = min(y0, y1), max(y0, y1)
        z0, z1 = min(z0, z1), max(z0, z1)
        b = min(bevel, (x1 - x0) / 2.5, (y1 - y0) / 2.5, (z1 - z0) / 2.5)
        if b <= 0.01:
            P = [(x, y, z) for x in (x0, x1) for y in (y0, y1) for z in (z0, z1)]
            faces = [(0, 1, 3, 2), (4, 6, 7, 5), (0, 4, 5, 1), (2, 3, 7, 6), (0, 2, 6, 4), (1, 5, 7, 3)]
            ctr = np.array([(x0 + x1) / 2, (y0 + y1) / 2, (z0 + z1) / 2])
            for f in faces:
                q = [P[i] for i in f]
                self.quad(*q, mat, hint=np.mean(q, axis=0) - ctr)
            return
        pts = []
        for x in (x0, x1):
            for y in (y0, y1):
                for z in (z0, z1):
                    sx, sy, sz = (1 if x == x0 else -1), (1 if y == y0 else -1), (1 if z == z0 else -1)
                    pts += [(x + sx * b, y + sy * b, z), (x + sx * b, y, z + sz * b), (x, y + sy * b, z + sz * b)]
        self.hull(pts, mat)

    def slab(self, poly_xz, y0, y1, mat, bevel=0.0):
        """Convex footprint polygon (x, z) extruded from y0 to y1, top edge optionally chamfered."""
        pts = [(x, y0, z) for x, z in poly_xz]
        if bevel > 0:
            inner = offset_poly(ccw(poly_xz), bevel)
            pts += [(x, y1 - bevel, z) for x, z in poly_xz] + [(x, y1, z) for x, z in inner]
        else:
            pts += [(x, y1, z) for x, z in poly_xz]
        self.hull(pts, mat)

    def prism(self, profile_zy, x0, x1, mat, bevel=0.0, cap_mat=None):
        """Side profile (z, y), possibly concave, extruded across x0..x1. bevel chamfers both outer edges."""
        prof = ccw(profile_zy)
        if bevel > 0:
            inner = offset_poly(prof, bevel)
            secs = [(x0, inner), (x0 + bevel, prof), (x1 - bevel, prof), (x1, inner)]
        else:
            secs = [(x0, prof), (x1, prof)]
        n = len(prof)
        for (xa, pa), (xb, pb) in zip(secs, secs[1:]):
            for i in range(n):
                j = (i + 1) % n
                a, b = (xa, pa[i][1], pa[i][0]), (xa, pa[j][1], pa[j][0])
                c, d = (xb, pb[j][1], pb[j][0]), (xb, pb[i][1], pb[i][0])
                self.quad(a, d, c, b, mat)
        cm = cap_mat or mat
        for x, poly, flip in ((secs[0][0], secs[0][1], True), (secs[-1][0], secs[-1][1], False)):
            for i0, i1, i2 in earclip(poly):
                pa, pb, pc = ((x, poly[k][1], poly[k][0]) for k in (i0, i1, i2))
                # profile is CCW in (z, y); seen from +x that is clockwise
                self.tri([pa, pb, pc] if flip else [pa, pc, pb], cm)

    def loft(self, rings, mat, caps=(True, True), smooth=True, closed=True, cap_mat=None):
        """Surface through rings of 3D points (same count each). Orientation is fixed to face outwards."""
        R = [np.array(r, dtype=float) for r in rings]
        n = len(R[0])
        allc = np.vstack(R).mean(axis=0)
        jn = n if closed else n - 1
        # orientation test on the middle band
        mid = len(R) // 2 - (1 if len(R) % 2 == 0 else 0)
        mid = max(0, min(mid, len(R) - 2))
        vote = 0.0
        cring = R[mid].mean(axis=0)
        for j in range(jn):
            a, b, c = R[mid][j], R[mid][(j + 1) % n], R[mid + 1][(j + 1) % n]
            vote += np.cross(b - a, c - a) @ ((a + b + c) / 3 - cring)
        flip = vote < 0
        normals = None
        if smooth:
            normals = np.zeros((len(R), n, 3))
            for i in range(len(R) - 1):
                for j in range(jn):
                    a, b, c, d = R[i][j], R[i][(j + 1) % n], R[i + 1][(j + 1) % n], R[i + 1][j]
                    fn = np.cross(b - a, d - a) + np.cross(c - b, a - b) * 0
                    fn = fn if not flip else -fn
                    for (ii, jj) in ((i, j), (i, (j + 1) % n), (i + 1, (j + 1) % n), (i + 1, j)):
                        normals[ii, jj] += fn
            normals /= np.maximum(np.linalg.norm(normals, axis=2, keepdims=True), 1e-9)
        for i in range(len(R) - 1):
            for j in range(jn):
                j2 = (j + 1) % n
                q = [R[i][j], R[i][j2], R[i + 1][j2], R[i + 1][j]]
                nn = None if normals is None else [normals[i, j], normals[i, j2], normals[i + 1, j2], normals[i + 1, j]]
                if flip:
                    q = q[::-1]
                    nn = None if nn is None else nn[::-1]
                self.quad(*q, mat, n=nn)
        cm = cap_mat or mat
        for end, on in ((0, caps[0]), (len(R) - 1, caps[1])):
            if not on or not closed:
                continue
            ring = R[end]
            c = ring.mean(axis=0)
            away = c - (R[1] if end == 0 else R[-2]).mean(axis=0)
            for j in range(n):
                a, b = ring[j], ring[(j + 1) % n]
                if np.cross(a - c, b - c) @ away < 0:
                    a, b = b, a
                self.tri([c, a, b], cm)

    # ---------------------------------------------------------------- shapes built on loft
    @staticmethod
    def frame(axis):
        """Two unit vectors perpendicular to axis."""
        a = _v(axis) / np.linalg.norm(axis)
        helper = np.array([0, 1.0, 0]) if abs(a[1]) < 0.9 else np.array([1.0, 0, 0])
        u = np.cross(helper, a)
        u /= np.linalg.norm(u)
        return a, u, np.cross(a, u)

    def cyl(self, p0, p1, r0, r1=None, mat="dark", n=12, caps=(True, True), smooth=True, cap_mat=None):
        p0, p1 = _v(p0), _v(p1)
        r1 = r0 if r1 is None else r1
        _, u, w = self.frame(p1 - p0)
        rings = []
        for p, r in ((p0, r0), (p1, r1)):
            rings.append([p + r * (math.cos(2 * math.pi * k / n) * u + math.sin(2 * math.pi * k / n) * w)
                          for k in range(n)])
        self.loft(rings, mat, caps, smooth, cap_mat=cap_mat)

    def pipe(self, p0, p1, sections, mat, n=12, smooth=True, caps=(True, True)):
        """Lathe along p0->p1: sections are (t, radius) with t from 0 to 1."""
        p0, p1 = _v(p0), _v(p1)
        _, u, w = self.frame(p1 - p0)
        rings = []
        for t, r in sections:
            c = p0 + (p1 - p0) * t
            rings.append([c + r * (math.cos(2 * math.pi * k / n) * u + math.sin(2 * math.pi * k / n) * w)
                          for k in range(n)])
        self.loft(rings, mat, caps, smooth)

    def body(self, sections, mat, n=16, caps=(True, True), smooth=True, cap_mat=None):
        """Fuselage along Z: sections are (z, cx, cy, rx, ry, p) superellipse cross sections."""
        rings = []
        for z, cx, cy, rx, ry, p in sections:
            rings.append([(cx + x, cy + y, z) for x, y in superellipse(max(rx, 0.05), max(ry, 0.05), n, p)])
        self.loft(rings, mat, caps, smooth, cap_mat=cap_mat)

    def tube(self, pts, r, mat, n=6):
        """Round bar along a polyline (rails, roll bars, hoses)."""
        pts = [_v(p) for p in pts]
        for a, b in zip(pts, pts[1:]):
            self.cyl(a, b, r, mat=mat, n=n)
        for p in pts[1:-1]:
            self.sphere(p, r, mat, 6, 4)

    def sphere(self, c, r, mat, n=12, m=8, ry=None):
        c = _v(c)
        ry = r if ry is None else ry
        rings = []
        for i in range(1, m):
            t = math.pi * i / m
            rings.append([c + np.array([r * math.sin(t) * math.cos(2 * math.pi * k / n), -ry * math.cos(t),
                                        r * math.sin(t) * math.sin(2 * math.pi * k / n)]) for k in range(n)])
        self.loft(rings, mat, (False, False))
        for ring, pole in ((rings[0], c - np.array([0, ry, 0])), (rings[-1], c + np.array([0, ry, 0]))):
            for k in range(n):
                a, b = ring[k], ring[(k + 1) % n]
                nrm = [(q - c) / max(np.linalg.norm(q - c), 1e-9) for q in (pole, a, b)]
                if np.cross(a - pole, b - pole) @ (pole - c) < 0:
                    a, b = b, a
                    nrm = [nrm[0], nrm[2], nrm[1]]
                self.tri([pole, a, b], mat, n=nrm)

    def dome(self, c, rx, ry, rz, mat, n=16, m=6):
        """Upper half ellipsoid on the plane y = c.y (canopies, cupolas)."""
        c = _v(c)
        rings = []
        for i in range(m + 1):
            t = (math.pi / 2) * i / m
            k = math.cos(t)
            y = math.sin(t)
            rings.append([c + np.array([rx * k * math.cos(2 * math.pi * j / n), ry * y,
                                        rz * k * math.sin(2 * math.pi * j / n)]) for j in range(n)])
        rings[-1] = [c + np.array([0, ry, 0]) + 0.01 * np.array([math.cos(2 * math.pi * j / n), 0,
                                                                 math.sin(2 * math.pi * j / n)]) for j in range(n)]
        self.loft(rings, mat, (True, False))

    def torus(self, c, axis, R, r, mat, n=32, m=8, rr=None):
        c = _v(c)
        a, u, w = self.frame(axis)
        rr = r if rr is None else rr
        for i in range(n):
            ring = []
            for i2 in (i, i + 1):
                t = 2 * math.pi * i2 / n
                d = math.cos(t) * u + math.sin(t) * w
                ring.append([c + d * (R + r * math.cos(2 * math.pi * k / m)) + a * rr * math.sin(2 * math.pi * k / m)
                             for k in range(m)])
            cen = c + (math.cos(2 * math.pi * (i + .5) / n) * u + math.sin(2 * math.pi * (i + .5) / n) * w) * R
            for k in range(m):
                k2 = (k + 1) % m
                q = [ring[0][k], ring[0][k2], ring[1][k2], ring[1][k]]
                nrm = [(p - (c + (p - c - a * ((p - c) @ a)) / max(np.linalg.norm(p - c - a * ((p - c) @ a)), 1e-9) * R))
                       for p in q]
                nrm = [x / max(np.linalg.norm(x), 1e-9) for x in nrm]
                if np.cross(q[1] - q[0], q[3] - q[0]) @ ((q[0] + q[2]) / 2 - cen) < 0:
                    q, nrm = q[::-1], nrm[::-1]
                self.quad(*q, mat, n=nrm)

    def wheel(self, r, w, tire="rubber", rim="rim", n=20, rim_ratio=0.62, hub=True, side=1):
        """Tyre at the origin, axle along X, outer face towards +x*side. Tyres and caps carry their own UVs."""
        hw = w / 2
        prof = [(-hw, 0.80), (-hw * 0.82, 0.95), (-hw * 0.5, 1.0), (hw * 0.5, 1.0), (hw * 0.82, 0.95), (hw, 0.80)]
        rings = []
        for x, k in prof:
            rings.append([(x, r * k * math.cos(2 * math.pi * j / n), r * k * math.sin(2 * math.pi * j / n))
                          for j in range(n)])
        # tread band with explicit uv: u around, v across
        R = [np.array(q) for q in rings]
        for i in range(len(R) - 1):
            v0, v1 = i / (len(R) - 1), (i + 1) / (len(R) - 1)
            for j in range(n):
                j2 = (j + 1) % n
                u0, u1 = j / n, (j + 1) / n
                q = [R[i][j], R[i][j2], R[i + 1][j2], R[i + 1][j]]
                uv = [(u0, v0), (u1, v0), (u1, v1), (u0, v1)]
                nrm = [np.array([0, p[1], p[2]]) / max(np.hypot(p[1], p[2]), 1e-9) for p in q]
                if np.cross(q[1] - q[0], q[3] - q[0]) @ np.array([0, q[0][1], q[0][2]]) < 0:
                    q, uv, nrm = q[::-1], uv[::-1], nrm[::-1]
                self.quad(*q, tire, uv=uv, n=nrm)
        # side caps with the rim drawing, slightly dished
        for sx in (-1, 1):
            ring = R[0] if sx < 0 else R[-1]
            depth = 0.35 if (sx == side and hub) else 0.0
            c = np.array([sx * (hw - depth), 0, 0])
            for j in range(n):
                a, b = ring[j], ring[(j + 1) % n]
                uv = [(0.5, 0.5), (0.5 + a[2] / (2 * r * 0.8), 0.5 + a[1] / (2 * r * 0.8)),
                      (0.5 + b[2] / (2 * r * 0.8), 0.5 + b[1] / (2 * r * 0.8))]
                pts = [c, a, b]
                if np.cross(a - c, b - c) @ np.array([sx, 0, 0]) < 0:
                    pts, uv = [c, b, a], [uv[0], uv[2], uv[1]]
                self.tri(pts, rim, uv=uv)
        if hub:
            self.cyl((side * (hw - 0.4), 0, 0), (side * (hw + 0.6), 0, 0), r * 0.18, r * 0.13, mat="steel", n=8)

    # ---------------------------------------------------------------- export
    def bounds(self, groups=None):
        pts = [p for g, tris in self.groups.items() if groups is None or g in groups for t in tris for p in t[0]]
        a = np.array(pts)
        return a.min(axis=0), a.max(axis=0)


# ============================================================================ texture
def _noise(w, h, cell, rnd, octaves=3):
    """Smooth value noise in [0, 1] with features about `cell` pixels wide."""
    out = np.zeros((h, w))
    amp, tot = 1.0, 0.0
    for o in range(octaves):
        c = max(1.0, cell / (2 ** o))
        gw, gh = int(w / c) + 2, int(h / c) + 2
        g = Image.fromarray((rnd.random((gh, gw)) * 255).astype(np.uint8))
        g = g.resize((int(gw * c), int(gh * c)), Image.BICUBIC).crop((0, 0, w, h))
        out += np.asarray(g, dtype=float) / 255 * amp
        tot += amp
        amp *= 0.5
    return out / tot


class Style:
    """Colours and finish of one vehicle."""

    def __init__(self, paint, paint2=None, camo=None, wear=0.5, glow=(255, 200, 80), core=(255, 245, 220),
                 scifi=False, rim=None, seat=(70, 62, 48), canvas=(118, 112, 78), marking=None, panel=1.0):
        self.paint, self.paint2 = paint, paint2 or tuple(int(c * 0.8) for c in paint)
        self.camo, self.wear, self.glow, self.core = camo, wear, glow, core
        self.scifi, self.rim, self.seat, self.canvas = scifi, rim or paint, seat, canvas
        self.marking, self.panel = marking, panel


class Painter:
    """Paints the material tiles. A tile maps the cube [c - S/2, c + S/2] of one projection to its pixels."""

    BIG = {"paint", "paint2", "armor", "cosmic"}

    def __init__(self, style, S, center, seed=7):
        self.st, self.S, self.c = style, S, center
        self.rnd = np.random.default_rng(seed)

    def tile_size(self, mat):
        if mat in ("paint", "paint2", "armor", "cosmic"):
            return 256
        if mat in ("rubber", "rim", "track", "dark", "steel", "canvas"):
            return 128
        return 64

    # pixel -> world coordinate along a tile axis
    def coords(self, n, axis_index):
        return self.c[axis_index] - self.S / 2 + (np.arange(n) + 0.5) / n * self.S

    def paint_tile(self, mat, proj, n):
        st, rnd = self.st, self.rnd
        img = np.zeros((n, n, 3))
        # u axis / v axis world indices per projection; image rows go top (high v) to bottom
        ua, va = {"x": (2, 1), "y": (2, 0), "z": (0, 1), None: (2, 1)}[proj]
        U = self.coords(n, ua)[None, :].repeat(n, 0)
        V = self.coords(n, va)[::-1][:, None].repeat(n, 1)
        upx = n / self.S  # pixels per unit
        noise = _noise(n, n, 6 * upx, rnd)
        fine = rnd.random((n, n))

        def base(col):
            return np.ones((n, n, 1)) * np.array(col, dtype=float)[None, None, :]

        if mat in ("paint", "paint2", "armor"):
            col = {"paint": st.paint, "paint2": st.paint2, "armor": (226, 230, 236)}[mat]
            img = base(col)
            if st.camo and mat == "paint":
                big = _noise(n, n, 13 * upx, rnd, 2)
                big2 = _noise(n, n, 9 * upx, rnd, 2)
                for k, cc in enumerate(st.camo):
                    m = (big > 0.56) if k == 0 else (big2 < 0.38)
                    m = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.ModeFilter(5))) > 127
                    img[m] = cc
            img *= (0.94 + 0.12 * noise)[..., None]
            img *= (0.985 + 0.03 * fine)[..., None]
            # panel lines and rivets
            if st.panel > 0:
                step_u = 16 if mat != "armor" else 12
                rng = np.random.default_rng(int(abs(hash((mat, proj)))) % 2 ** 32)
                lines_u = np.arange(-200, 200, step_u) + rng.uniform(-3, 3)
                lines_v = np.arange(-200, 200, 11 if proj == "x" else 14) + rng.uniform(-2, 2)
                gap = 0.55 / upx * max(1, upx / 3)
                mask = np.zeros((n, n), bool)
                for lu in lines_u:
                    mask |= np.abs(U - lu) < max(0.25, 0.6 / upx)
                for lv in lines_v[::2]:
                    mask |= np.abs(V - lv) < max(0.25, 0.6 / upx)
                img[mask] *= 0.72 if not st.scifi else 0.8
                if not st.scifi and proj in ("x", "z"):
                    riv = np.zeros((n, n), bool)
                    for lu in lines_u:
                        near = np.abs(U - (lu + 1.2)) < 0.35
                        riv |= near & (np.mod(V, 2.5) < 0.5)
                    img[riv] *= 1.12
                del gap
            if st.scifi:
                # soft vertical sheen
                img *= (0.92 + 0.12 * (V - V.min()) / max(1e-9, V.max() - V.min()))[..., None]
            # weathering: dust and grime near the ground, streaks
            if st.wear > 0 and proj in ("x", "z"):
                h = np.clip((V - (self.c[1] - self.S / 2)) / 18.0, 0, 1)
                dust = np.array([150, 132, 98], dtype=float)
                k = (1 - h) ** 2 * 0.55 * st.wear
                img = img * (1 - k[..., None]) + dust * k[..., None]
                streak = _noise(n, 1, 1.2 * upx, rnd, 1)[0]
                s = (streak > 0.72) * (0.10 * st.wear)
                img *= (1 - s[None, :, None] * (0.6 + 0.4 * noise[..., None]))
            if st.wear > 0:
                chips = (fine > 1 - 0.012 * st.wear) & (noise > 0.55)
                img[chips] = img[chips] * 0.55 + np.array([60, 58, 52]) * 0.45
            if st.marking and mat == "paint" and proj == "x":
                # small identification star / chevron near the middle of the side
                cx, cy = self.c[2] - 2, self.c[1] + 2
                d = np.hypot(U - cx, V - cy)
                ang = np.arctan2(V - cy, U - cx)
                star = d < (2.4 + 1.1 * np.cos(5 * ang))
                img[star] = st.marking
        elif mat == "dark":
            img = base((48, 50, 54)) * (0.9 + 0.2 * noise)[..., None]
            img *= (0.95 + 0.08 * fine)[..., None]
        elif mat == "steel":
            img = base((150, 154, 160)) * (0.88 + 0.2 * noise)[..., None]
            img *= (0.94 + 0.1 * _noise(n, n, 1.0, rnd, 1)[..., None])
        elif mat == "chrome":
            img = base((200, 205, 214)) * (0.8 + 0.3 * ((U + V) % 9 < 4.5))[..., None] * (0.95 + 0.05 * fine)[..., None]
        elif mat == "black":
            img = base((24, 25, 28)) * (0.9 + 0.2 * noise)[..., None]
        elif mat == "gold":
            img = base((222, 180, 84)) * (0.85 + 0.25 * noise)[..., None]
        elif mat == "seat":
            img = base(st.seat) * (0.85 + 0.2 * noise)[..., None]
            img *= (0.9 + 0.12 * (np.mod(U, 1.5) < 0.75))[..., None]
        elif mat == "canvas":
            img = base(st.canvas) * (0.85 + 0.25 * noise)[..., None]
            weave = ((np.mod(U * 3, 1) < 0.5) ^ (np.mod(V * 3, 1) < 0.5))
            img *= (0.95 + 0.08 * weave)[..., None]
            folds = 0.9 + 0.1 * np.sin(U * 0.9 + 3 * noise)
            img *= folds[..., None]
        elif mat == "glass":
            g = (V - V.min()) / max(1e-9, V.max() - V.min())
            img = base((34, 48, 62)) * (0.8 + 0.5 * g)[..., None]
            band_ = np.abs(np.mod(U * 0.5 + V, 16) - 5)
            k = np.clip(1.6 - band_ / 1.5, 0, 1) * 0.45
            img = img * (1 - k[..., None]) + np.array([170, 196, 214]) * k[..., None]
        elif mat == "glow":
            img = base(st.glow) * (0.85 + 0.15 * noise)[..., None]
            core = np.abs(np.mod(V, 3) - 1.5) < 0.5
            img[core] = img[core] * 0.4 + np.array(st.core) * 0.6
        elif mat == "core":
            img = base(st.core)
        elif mat == "light":
            img = base((255, 246, 214)) * (0.9 + 0.1 * noise)[..., None]
        elif mat == "tail":
            img = base((220, 40, 30)) * (0.85 + 0.15 * noise)[..., None]
        elif mat == "vent":
            img = base((40, 42, 46)) * np.where(np.mod(V, 1.6) < 0.8, 0.55, 1.15)[..., None]
        elif mat == "hazard":
            img = np.where((np.mod(U + V, 4) < 2)[..., None], np.array([230, 186, 40.0]), np.array([30, 30, 32.0]))
        elif mat == "track":
            u = (np.arange(n) + 0.5) / n
            Uu, Vv = np.meshgrid(u, u[::-1])
            link = np.mod(Uu * 8, 1.0)
            img = base((58, 56, 52)) * np.where(link < 0.16, 0.45, np.where(link < 0.3, 1.25, 1.0))[..., None]
            img *= np.where(np.abs(Vv - 0.5) < 0.06, 0.6, 1.0)[..., None]
            img *= np.where((np.abs(Vv - 0.5) < 0.12) & (link > 0.4) & (link < 0.75), 1.35, 1.0)[..., None]
            img *= (0.85 + 0.25 * noise)[..., None]
            img = img * 0.8 + np.array([120, 100, 70]) * 0.2 * noise[..., None]
        elif mat == "cosmic":
            img = base(st.paint2) * (0.7 + 0.4 * _noise(n, n, 20 * upx, rnd, 3))[..., None]
            stars = fine > 0.985
            img[stars] = (255, 255, 255)
            stars2 = (fine > 0.97) & ~stars
            img[stars2] = st.glow
        elif mat == "brass":
            img = base((176, 140, 70)) * (0.85 + 0.25 * noise)[..., None]
        elif mat == "olive":
            img = base((88, 92, 64)) * (0.88 + 0.2 * noise)[..., None]
        elif mat == "rubber":
            # tread: u around the tyre, v across
            u = (np.arange(n) + 0.5) / n
            v = ((np.arange(n) + 0.5) / n)[::-1]
            Uu, Vv = np.meshgrid(u, v)
            img = base((30, 30, 32)) * (0.9 + 0.15 * noise)[..., None]
            side = (Vv < 0.18) | (Vv > 0.82)
            lug = (np.mod(Uu * 40 + np.where(Vv < 0.5, 0, 0.5), 1) < 0.45) & ~side & (np.abs(Vv - 0.5) > 0.04)
            img[lug] *= 1.7
            img[side] *= 0.8
        elif mat == "rim":
            u = (np.arange(n) + 0.5) / n
            Uu, Vv = np.meshgrid(u, u[::-1])
            x, y = (Uu - 0.5) * 2, (Vv - 0.5) * 2
            d = np.hypot(x, y)
            a = np.arctan2(y, x)
            img = base((28, 28, 30)) * (0.9 + 0.12 * noise)[..., None]  # sidewall
            rimr = 0.78
            rim = d < rimr
            rc = np.array(st.rim, dtype=float)
            img[rim] = rc * 0.9
            img[(d < rimr) & (d > rimr - 0.07)] = rc * 0.6
            dish = d < 0.55
            img[dish] = rc * 0.75
            spokes = (np.abs(np.mod(a * 6 / (2 * np.pi), 1) - 0.5) < 0.18) & (d < rimr - 0.07) & (d > 0.25)
            img[spokes] = rc * 1.05
            bolts = np.zeros_like(d, bool)
            for k in range(8):
                bx, by = 0.36 * math.cos(2 * math.pi * k / 8), 0.36 * math.sin(2 * math.pi * k / 8)
                bolts |= np.hypot(x - bx, y - by) < 0.05
            img[bolts] = (180, 176, 160)
            img[d < 0.2] = rc * 0.6
            img[d < 0.12] = (150, 150, 145)
            img *= (0.95 + 0.08 * fine)[..., None]
        else:
            raise KeyError(mat)
        return np.clip(img, 0, 255)


def export(mesh, style, path_obj, path_png, uv_groups=None):
    """Write the OBJ (Java model space) and the atlas. Returns (triangle count, atlas size)."""
    lo, hi = mesh.bounds(["hull"] if "hull" in mesh.groups else None)
    center = (lo + hi) / 2
    S = float(max(hi - lo)) * 1.04 + 2
    painter = Painter(style, S, center)

    used = sorted({t[1] for g in mesh.groups.values() for t in g})
    # tiles: BIG materials get three projections, others one
    reqs = []
    for m in used:
        if m in ("paint", "paint2", "armor"):
            for p in ("x", "y", "z"):
                reqs.append(((m, p), painter.tile_size(m)))
        else:
            reqs.append(((m, None), painter.tile_size(m)))
    reqs.sort(key=lambda r: -r[1])
    W = 1024
    x = y = row_h = 0
    place = {}
    for key, size in reqs:
        if x + size > W:
            x, y, row_h = 0, y + row_h, 0
        place[key] = (x, y, size)
        x += size
        row_h = max(row_h, size)
    H = 1
    while H < y + row_h:
        H *= 2
    atlas = np.zeros((H, W, 3))
    for key, (tx, ty, size) in place.items():
        atlas[ty:ty + size, tx:tx + size] = painter.paint_tile(key[0], key[1], size)
    img = Image.fromarray(atlas.astype(np.uint8), "RGB")
    # a 256 colour palette keeps the jar small; the tiles are noisy enough to hide the banding
    img.quantize(256, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).save(path_png, optimize=True)

    def proj_uv(p, axis):
        ua, va = {"x": (2, 1), "y": (2, 0), "z": (0, 1)}[axis]
        return ((p[ua] - center[ua]) / S + 0.5, (p[va] - center[va]) / S + 0.5)

    V, VT, VN, out = [], [], [], []
    vcache, tcache, ncache = {}, {}, {}

    def idx(cache, lst, key):
        if key not in cache:
            lst.append(key)
            cache[key] = len(lst)
        return cache[key]

    tris = 0
    order = sorted(mesh.groups)
    for gname in order:
        tlist = mesh.groups[gname]
        buckets = {gname: [], gname + "_glow": []}
        for t in tlist:
            buckets[gname + "_glow" if t[1] in EMISSIVE else gname].append(t)
        for bname, items in buckets.items():
            if not items:
                continue
            out.append(f"g {bname}")
            for p, mat, uv, n in items:
                fn = np.cross(p[1] - p[0], p[2] - p[0])
                fn /= np.linalg.norm(fn)
                if uv is None:
                    axis = "xyz"[int(np.argmax(np.abs(fn)))]
                    key = (mat, axis) if (mat, axis) in place else (mat, None)
                    uvs = [proj_uv(q, axis) for q in p]
                else:
                    key = (mat, None)
                    uvs = uv
                tx, ty, size = place[key]
                ids = []
                for q, (u, v), nn in zip(p, uvs, n if n is not None else [fn, fn, fn]):
                    m = 0.5 / size
                    u = min(max(u, m), 1 - m)
                    v = min(max(v, m), 1 - m)
                    col, row = tx + u * size, ty + (1 - v) * size
                    jq = (round(-q[0], 4), round(-q[1], 4), round(-q[2], 4))
                    jt = (round(col / W, 6), round(1 - row / H, 6))
                    jn = (round(-nn[0], 4), round(-nn[1], 4), round(-nn[2], 4))
                    ids.append((idx(vcache, V, jq), idx(tcache, VT, jt), idx(ncache, VN, jn)))
                ids = [ids[0], ids[2], ids[1]]  # mirror flips the winding back
                out.append("f " + " ".join(f"{a}/{b}/{c}" for a, b, c in ids))
                tris += 1

    def fmt(x):
        s = f"{x:.4f}".rstrip("0").rstrip(".")
        return "0" if s in ("-0", "") else s

    lines = ["# NewGunrizons vehicle mesh, generated by tools/vehicle_designs.py"]
    lines += ["v " + " ".join(fmt(c) for c in q) for q in V]
    lines += ["vt " + " ".join(fmt(c) for c in q) for q in VT]
    lines += ["vn " + " ".join(fmt(c) for c in q) for q in VN]
    lines += out
    path_obj.write_text("\n".join(lines) + "\n")
    return tris, (W, H)
