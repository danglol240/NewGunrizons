"""Preview renderer for NewGunrizons .geo.json models.

Mirrors how JsonModel + Minecraft 1.7.10 ModelRenderer/ModelBox build a model
(Y-down Java space, pivots, Z->Y->X bone rotation, box-UV layout, integer cube
sizes), then draws textured quads per texel with a painter's algorithm.

usage: render_geo.py MODEL.geo.json TEXTURE.png OUT.png [--views side,three_quarter,top,front]
"""
import argparse
import json
import math

from PIL import Image, ImageDraw


def rot_x(a):
    c, s = math.cos(a), math.sin(a)
    return [[1, 0, 0], [0, c, -s], [0, s, c]]


def rot_y(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, 0, s], [0, 1, 0], [-s, 0, c]]


def rot_z(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, -s, 0], [s, c, 0], [0, 0, 1]]


def mat_mul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(3)) for j in range(3)] for i in range(3)]


def mat_vec(m, v):
    return [sum(m[i][k] * v[k] for k in range(3)) for i in range(3)]


class Bone:
    def __init__(self, name, pivot, rot):
        self.name = name
        self.point = [pivot[0], -pivot[1], pivot[2]]  # Java rotation point
        d = math.pi / 180
        self.ax, self.ay, self.az = -rot[0] * d, -rot[1] * d, rot[2] * d
        self.boxes = []
        self.children = []


def load(path):
    geo = json.load(open(path))["minecraft:geometry"][0]
    desc = geo.get("description", {})
    tw, th = desc.get("texture_width", 64), desc.get("texture_height", 64)
    bones, parents = {}, {}
    for b in geo.get("bones", []):
        pivot = b.get("pivot", [0, 0, 0])
        bone = Bone(b["name"], pivot, b.get("rotation", [0, 0, 0]))
        for c in b.get("cubes", []):
            o, s = c.get("origin", [0, 0, 0]), c.get("size", [1, 1, 1])
            uv = c.get("uv", [0, 0])
            if not isinstance(uv, list):
                uv = [0, 0]
            off = [o[0] - pivot[0], pivot[1] - o[1] - s[1], o[2] - pivot[2]]
            size = [round(s[0]), round(s[1]), round(s[2])]
            bone.boxes.append((off, size, round(uv[0]), round(uv[1]), c.get("mirror", False), c.get("inflate", 0.0)))
        bones[b["name"]] = bone
        if "parent" in b:
            parents[b["name"]] = b["parent"]
    for child, parent in parents.items():
        if parent in bones:
            c, p = bones[child], bones[parent]
            c.point = [c.point[i] - p.point[i] for i in range(3)]
            p.children.append(c)
    roots = [b for n, b in bones.items() if n not in parents or parents[n] not in bones]
    return roots, tw, th


def box_quads(off, size, u, v, mirror, inflate):
    """Faces of a 1.7.10 ModelBox: (4 corners in local space, uv rect (u0,v0,u1,v1))."""
    dx, dy, dz = size
    x0, y0, z0 = off[0] - inflate, off[1] - inflate, off[2] - inflate
    x1, y1, z1 = off[0] + dx + inflate, off[1] + dy + inflate, off[2] + dz + inflate
    if mirror:
        x0, x1 = x1, x0
    p = {
        "000": (x0, y0, z0), "100": (x1, y0, z0), "110": (x1, y1, z0), "010": (x0, y1, z0),
        "001": (x0, y0, z1), "101": (x1, y0, z1), "111": (x1, y1, z1), "011": (x0, y1, z1),
    }
    # Same vertex order and UV rects as net.minecraft.client.model.ModelBox
    quads = [
        ([p["101"], p["100"], p["110"], p["111"]], (u + dz + dx, v + dz, u + dz + dx + dz, v + dz + dy)),
        ([p["000"], p["001"], p["011"], p["010"]], (u, v + dz, u + dz, v + dz + dy)),
        ([p["101"], p["001"], p["000"], p["100"]], (u + dz, v, u + dz + dx, v + dz)),
        ([p["110"], p["010"], p["011"], p["111"]], (u + dz + dx, v + dz, u + dz + dx + dx, v)),
        ([p["100"], p["000"], p["010"], p["110"]], (u + dz, v + dz, u + dz + dx, v + dz + dy)),
        ([p["001"], p["101"], p["111"], p["011"]], (u + dz + dx + dz, v + dz, u + dz + dx + dz + dx, v + dz + dy)),
    ]
    if mirror:
        quads = [([q[1], q[0], q[3], q[2]], uvr) for q, uvr in quads]
    return quads


def collect(bone, parent_m, parent_t, out):
    m = mat_mul(parent_m, mat_mul(rot_z(bone.az), mat_mul(rot_y(bone.ay), rot_x(bone.ax))))
    t = [parent_t[i] + mat_vec(parent_m, bone.point)[i] for i in range(3)]
    for box in bone.boxes:
        for corners, uvr in box_quads(*box):
            world = [[t[i] + mat_vec(m, c)[i] for i in range(3)] for c in corners]
            out.append((world, uvr))
    for ch in bone.children:
        collect(ch, m, t, out)


VIEWS = {
    # yaw (around Y), pitch (around X) applied to the Java-space model; Y-down
    "side": (math.radians(90), 0.0),
    "side_left": (math.radians(-90), 0.0),
    "three_quarter": (math.radians(135), math.radians(25)),  # from above (Y is down)
    "top": (math.radians(90), math.radians(-89)),
    "front": (0.0, 0.0),
}


def render(quads, tex, view, size=900):
    """Z-buffered rasterizer: each face is an affine parallelogram in orthographic view."""
    import numpy as np

    yaw, pitch = VIEWS[view]
    rm = mat_mul(rot_x(pitch), rot_y(yaw))
    T = np.asarray(tex.convert("RGBA"), dtype=np.float32)
    th, tw = T.shape[0], T.shape[1]
    light = np.array([0.4, -0.8, -0.45])
    light /= np.linalg.norm(light)
    faces = []
    for world, uvr in quads:
        pts = np.array([mat_vec(rm, w) for w in world], dtype=np.float64)
        n = np.cross(pts[1] - pts[0], pts[3] - pts[0])
        nl = np.linalg.norm(n)
        if nl == 0:
            continue
        n /= nl
        if n[2] > 1e-6:
            continue
        faces.append((pts, uvr, 0.55 + 0.45 * max(0.0, float(-n @ light))))
    if not faces:
        return Image.new("RGB", (size, size // 2), (38, 42, 50))
    allp = np.concatenate([f[0] for f in faces])
    minx, maxx, miny, maxy = allp[:, 0].min(), allp[:, 0].max(), allp[:, 1].min(), allp[:, 1].max()
    w, h = size, int(size * 0.55)
    scale = 0.92 * min(w / (maxx - minx + 1e-6), h / (maxy - miny + 1e-6))
    ox = (w - (maxx - minx) * scale) / 2 - minx * scale
    oy = (h - (maxy - miny) * scale) / 2 - miny * scale
    ss = 2  # supersample
    W, H = w * ss, h * ss
    color = np.zeros((H, W, 3), dtype=np.float32)
    color[:] = (38, 42, 50)
    depth = np.full((H, W), np.inf)
    for pts, (u0, v0, u1, v1), shade in faces:
        sp = np.stack([(pts[:, 0] * scale + ox) * ss, (pts[:, 1] * scale + oy) * ss], axis=1)
        a, b, d = sp[0], sp[1], sp[3]
        e1, e2 = b - a, d - a  # fu along 0->1, fv along 0->3
        det = e1[0] * e2[1] - e1[1] * e2[0]
        if abs(det) < 1e-9:
            continue
        x0, x1 = int(max(0, np.floor(sp[:, 0].min()))), int(min(W - 1, np.ceil(sp[:, 0].max())))
        y0, y1 = int(max(0, np.floor(sp[:, 1].min()))), int(min(H - 1, np.ceil(sp[:, 1].max())))
        if x1 < x0 or y1 < y0:
            continue
        ys, xs = np.mgrid[y0:y1 + 1, x0:x1 + 1]
        rx, ry = xs + 0.5 - a[0], ys + 0.5 - a[1]
        fu = (rx * e2[1] - ry * e2[0]) / det
        fv = (e1[0] * ry - e1[1] * rx) / det
        m = (fu >= 0) & (fu < 1) & (fv >= 0) & (fv < 1)
        if not m.any():
            continue
        z = pts[0, 2] + fu * (pts[1, 2] - pts[0, 2]) + fv * (pts[3, 2] - pts[0, 2])
        tu = np.clip(np.floor(u1 + (u0 - u1) * fu), 0, tw - 1).astype(int)
        tv = np.clip(np.floor(v0 + (v1 - v0) * fv), 0, th - 1).astype(int)
        texel = T[tv, tu]
        m &= texel[..., 3] > 10
        sub_d = depth[y0:y1 + 1, x0:x1 + 1]
        m &= z < sub_d
        sub_d[m] = z[m]
        rgb = texel[..., :3]
        glow = ((rgb.max(axis=-1) > 200) & (rgb.max(axis=-1) - rgb.min(axis=-1) > 70))[..., None]  # saturated brights are emissive
        lit = np.where(glow, rgb, rgb * shade)
        sub_c = color[y0:y1 + 1, x0:x1 + 1]
        sub_c[m] = lit[m]
    img = Image.fromarray(np.clip(color, 0, 255).astype(np.uint8))
    return img.resize((w, h), Image.LANCZOS)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("model")
    ap.add_argument("texture")
    ap.add_argument("out")
    ap.add_argument("--views", default="side,three_quarter,top")
    a = ap.parse_args()
    roots, tw, th = load(a.model)
    tex = Image.open(a.texture).convert("RGBA")
    if tex.size != (tw, th):
        tex = tex.resize((tw, th), Image.NEAREST)
    quads = []
    ident = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]
    for r in roots:
        collect(r, ident, [0, 0, 0], quads)
    xs = [p[0] for q, _ in quads for p in q]
    ys = [p[1] for q, _ in quads for p in q]
    zs = [p[2] for q, _ in quads for p in q]
    print(f"java-space bounds x[{min(xs):.1f},{max(xs):.1f}] y[{min(ys):.1f},{max(ys):.1f}] z[{min(zs):.1f},{max(zs):.1f}]")
    imgs = [render(quads, tex, v) for v in a.views.split(",")]
    sheet = Image.new("RGB", (imgs[0].width, sum(i.height for i in imgs)))
    y = 0
    for im in imgs:
        sheet.paste(im, (0, y))
        y += im.height
    sheet.save(a.out)


if __name__ == "__main__":
    main()
