"""Models and layouts of the LV-MAX vehicles (polygon meshes, see meshforge.py).

For every vehicle this writes
  models/vehicle/vehicle_<name>.obj      all parts as OBJ groups: hull, w<i> wheels, r<i> rotors, t<i> turrets,
                                         g<i> guns, and <part>_glow for the full-bright faces
  textures/models/vehicle_<name>.png     the atlas
  vehicles/vehicle_<name>.json           layout read by the game on both sides (seats, wheels, turrets, rotors...)

Authoring space: X to the vehicle's right, Y up, Z forward, y = 0 on the ground, 16 units per block at scale 1.
Layout positions are written in Java model space (-X, -Y, -Z) like the meshes. Turret, gun, wheel and rotor groups
are built around their own pivot.

A seat point sits about 5 units under the top of the cushion (the game puts the rider's feet 0.45 blocks under it).

usage: vehicle_designs.py [NAME ...]
"""
import json
import math
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from meshforge import Mesh, Style, export  # noqa: E402

ASSETS = Path(__file__).resolve().parent.parent / "src/main/resources/assets/newgunrizons"


def J(p):
    return {"x": round(-p[0], 3), "y": round(-p[1], 3), "z": round(-p[2], 3)}


class V:
    def __init__(self, name, tier, style, width, height, scale=1.0):
        self.name, self.tier, self.style = name, tier, style
        self.m = Mesh()
        self.layout = {"scale": scale, "width": width, "height": height, "seats": [], "wheels": [], "turrets": [],
                       "mounts": [], "rotors": [], "pads": [], "exhausts": []}

    def seat(self, x, y, z, hidden=False):
        self.layout["seats"].append(dict(J((x, y, z)), hidden=hidden))

    def wheel(self, x, y, z, r, w, steer=False, build=None, **kw):
        """A wheel that spins with the distance driven (and turns with the steering when steer)."""
        i = len(self.layout["wheels"])
        with self.m.group(f"w{i}"):
            if build:
                build(self.m, 1 if x > 0 else -1)
            else:
                self.m.wheel(r, w, side=1 if x > 0 else -1, **kw)
        self.layout["wheels"].append(dict(J((x, y, z)), r=r, steer=steer))

    def turret(self, pos, seat, build_turret, build_gun, gun_at=(0, 0, 0), length=10, min_pitch=-60, max_pitch=30):
        i = len(self.layout["turrets"])
        if build_turret:
            with self.m.group(f"t{i}"):
                build_turret(self.m)
        if build_gun:
            with self.m.group(f"g{i}"):
                build_gun(self.m)
        t = J(pos)
        g = J(gun_at)
        self.layout["turrets"].append(dict(t, seat=seat, gx=g["x"], gy=g["y"], gz=g["z"], length=length,
                                           minPitch=min_pitch, maxPitch=max_pitch, turret=build_turret is not None,
                                           gun=build_gun is not None))

    def mount(self, x, y, z):
        self.layout["mounts"].append(J((x, y, z)))

    def rotor(self, x, y, z, r, build=None):
        i = len(self.layout["rotors"])
        if build:
            with self.m.group(f"r{i}"):
                build(self.m, i)
        self.layout["rotors"].append(dict(J((x, y, z)), r=r, model=build is not None))

    def pad(self, x, y, z, r):
        self.layout["pads"].append(dict(J((x, y, z)), r=r))

    def exhaust(self, x, y, z, r):
        """Engine nozzle facing backwards (towards -Z)."""
        self.layout["exhausts"].append(dict(J((x, y, z)), r=r))

    # ---------------------------------------------------------------- shared details
    def headlight(self, x, y, z, r=1.4, guard=True):
        m = self.m
        m.cyl((x, y, z - 1.2), (x, y, z), r + 0.35, mat="dark", n=10)
        m.cyl((x, y, z), (x, y, z + 0.25), r, mat="light", n=10)
        if guard:
            m.tube([(x - r - 0.6, y - r, z + 0.2), (x - r - 0.6, y + r + 0.4, z + 1.4),
                    (x + r + 0.6, y + r + 0.4, z + 1.4), (x + r + 0.6, y - r, z + 0.2)], 0.22, "dark")

    def taillight(self, x, y, z, w=1.6, h=1.0):
        self.m.box(x - w / 2, x + w / 2, y - h / 2, y + h / 2, z - 0.3, z + 0.1, "tail")


# ============================================================================ helpers
def band(m, path, x0, x1, thick, mat, period=24.0):
    """Closed belt (tank track) along a (z, y) path, from x0 to x1; texture repeats every `period` units."""
    pts = [(float(z), float(y)) for z, y in path]
    n = len(pts)
    # outward normals of the loop (path is CCW in the z-y plane)
    area = sum(pts[i][0] * pts[(i + 1) % n][1] - pts[(i + 1) % n][0] * pts[i][1] for i in range(n))
    sgn = 1 if area > 0 else -1
    s = 0.0
    for i in range(n):
        (za, ya), (zb, yb) = pts[i], pts[(i + 1) % n]
        L = math.hypot(zb - za, yb - ya)
        if L < 1e-6:
            continue
        nz, ny = sgn * (yb - ya) / L, -sgn * (zb - za) / L
        u0 = (s % period) / period
        u1 = u0 + L / period
        s += L
        ao, bo = (za + nz * thick, ya + ny * thick), (zb + nz * thick, yb + ny * thick)
        # outer (tread), inner, two sides
        segs = [((x0, ya, za), (x1, ya, za), (x1, yb, zb), (x0, yb, zb))]
        out = (0, ny, nz)
        m.quad((x0, ao[1], ao[0]), (x1, ao[1], ao[0]), (x1, bo[1], bo[0]), (x0, bo[1], bo[0]), mat,
               uv=[(u0, 0), (u0, 1), (u1, 1), (u1, 0)], hint=out)
        m.quad(*segs[0], mat, uv=[(u0, 0.1), (u0, 0.9), (u1, 0.9), (u1, 0.1)], hint=(0, -ny, -nz))
        for x in (x0, x1):
            q = [(x, ya, za), (x, yb, zb), (x, bo[1], bo[0]), (x, ao[1], ao[0])]
            m.quad(*q, "dark", hint=(x - (x0 + x1) / 2, 0, 0))


def sprocket(r, w, teeth=11):
    def build(m, side):
        poly = []
        for k in range(teeth * 2):
            a = math.pi * k / teeth
            rr = r if k % 2 == 0 else r * 0.82
            poly.append((rr * math.cos(a), rr * math.sin(a)))
        m.prism(poly, -w / 2, w / 2, "dark")
        m.cyl((side * w / 2, 0, 0), (side * (w / 2 + 1), 0, 0), r * 0.55, mat="paint", n=12)
        m.cyl((side * (w / 2 + 1), 0, 0), (side * (w / 2 + 1.6), 0, 0), r * 0.22, mat="steel", n=8)
    return build


def road_wheel(r, w):
    def build(m, side):
        m.wheel(r, w, tire="rubber", rim="rim", n=18, hub=True, side=side)
    return build


# ============================================================================ guns
def pintle_mg(m):
    """M60-style machine gun on a pintle cradle, muzzle towards +z."""
    m.box(-1.2, 1.2, -1.4, 1.6, -6, 4, "dark", 0.2)  # receiver
    m.box(-1.0, 1.0, 1.6, 2.2, -4, 3, "dark")  # feed cover
    m.cyl((0, 0.3, 4), (0, 0.3, 16), 0.55, mat="black", n=8)  # barrel
    m.cyl((0, 0.3, 4), (0, 0.3, 8), 0.9, mat="dark", n=8)  # gas cylinder area
    m.cyl((0, 0.3, 16), (0, 0.3, 17.6), 0.8, mat="dark", n=8)  # flash hider
    m.box(-0.3, 0.3, 1.6, 2.8, 14.5, 15.2, "dark")  # front sight
    m.box(1.2, 4.2, -3.2, 0.4, -2, 2.5, "olive", 0.2)  # ammo can
    m.tube([(-1, -0.4, -6), (-1.3, -0.6, -8.5)], 0.3, "black")  # spade grips
    m.tube([(1, -0.4, -6), (1.3, -0.6, -8.5)], 0.3, "black")
    m.box(-0.6, 0.6, -1.2, 0.2, -1.5, 1.5, "steel")  # cradle pin


def hmg(m, length=26):
    """M2-style heavy machine gun, muzzle towards +z."""
    m.box(-1.6, 1.6, -1.8, 1.8, -7, 3, "dark", 0.25)
    m.box(-1.3, 1.3, 1.8, 2.6, -5, 2, "dark")
    m.cyl((0, 0, 3), (0, 0, 9), 1.15, mat="vent", n=10)  # perforated jacket
    m.cyl((0, 0, 3), (0, 0, length), 0.6, mat="black", n=8)
    m.cyl((0, 0, length - 1.2), (0, 0, length), 0.85, mat="dark", n=8)
    m.tube([(-1.1, 0, -7), (-1.1, -0.6, -9)], 0.3, "black")
    m.tube([(1.1, 0, -7), (1.1, -0.6, -9)], 0.3, "black")
    m.box(1.6, 5.0, -3.5, 0.8, -3, 2.5, "olive", 0.2)


# ============================================================================ LV jeep
def jeep():
    st = Style(paint=(84, 92, 58), paint2=(66, 72, 46), wear=0.6, rim=(84, 92, 58), seat=(56, 58, 40),
               marking=(222, 222, 206))
    v = V("FieldJeep", "LV", st, 2.2, 2.0)
    m = v.m
    R, W = 6.5, 5.0
    for z, steer in ((17, True), (-16, False)):
        for x in (12.5, -12.5):
            v.wheel(x, R, z, R, W, steer=steer)
    # frame, axles, springs
    with m.mirrored():
        m.box(5, 7, 4.5, 7.5, -26, 27, "dark")
        m.box(8, 11, 5.5, 7, 13, 21, "dark")
        m.box(8, 11, 5.5, 7, -20, -12, "dark")
    m.cyl((-11, R, 17), (11, R, 17), 0.9, mat="dark", n=8)
    m.cyl((-11, R, -16), (11, R, -16), 1.1, mat="dark", n=8)
    m.pipe((0, R, -19), (0, R, -13), [(0, 1.0), (0.2, 2.6), (0.8, 2.6), (1, 1.0)], "dark", n=10)  # differential
    # hood and grille
    m.prism([(8, 11), (27, 11), (27.6, 17.4), (26.6, 18.6), (8, 19.2)], -10.5, 10.5, "paint", bevel=0.6)
    m.box(-9, 9, 11.5, 17, 27.4, 27.9, "vent")
    for x in (-7.2, 7.2):
        v.headlight(x, 14.6, 27.9, 1.5, guard=False)
    m.box(-10, 10, 17.4, 18, 26.8, 27.8, "paint")
    with m.mirrored():
        m.box(10.2, 15.6, 15.1, 16.0, 9, 26.8, "paint", 0.2)  # front fender
        m.box(10.2, 15.6, 11.0, 16.0, 26.0, 26.9, "paint", 0.2)  # fender apron
        m.box(11.0, 15.0, 15.6, 16.6, 22, 24.5, "dark")  # blackout light housing
        m.box(11.6, 14.4, 15.8, 16.8, 24.5, 24.8, "light")
        m.box(9.0, 10.4, 15.0, 16.0, 9, 26, "paint")  # inner fender
    # tub
    tub_side = [(-27, 8), (8.5, 8), (8.5, 17.5), (4.5, 17.5), (1.5, 15.2), (-8.5, 15.2), (-11.5, 17.5), (-27, 17.5)]
    with m.mirrored():
        m.prism(tub_side, 12.0, 13.6, "paint", bevel=0.35)
        m.box(7.0, 12.0, 8.5, 13.2, -21, -11, "paint", 0.4)  # rear wheel well
    m.box(-13.6, 13.6, 8.0, 17.5, -27.6, -26.0, "paint", 0.35)  # rear panel
    m.box(-12.0, 12.0, 8.0, 9.2, -26, 8.5, "paint2")  # floor
    m.box(-12.0, 12.0, 8.0, 19.4, 7.4, 8.6, "paint", 0.3)  # firewall / dash
    m.box(-11.0, 11.0, 16.2, 18.6, 6.0, 7.6, "dark", 0.3)  # dashboard
    for x in (-8, -5.5, -3):
        m.cyl((x, 17.6, 6.0), (x, 17.6, 5.7), 0.7, mat="steel", n=8)  # gauges
    # windshield, raked back
    with m.at((0, 19.2, 8.4), (-14, 0, 0)):
        with m.mirrored():
            m.box(11.0, 12.6, 0, 11.5, -0.6, 0.6, "paint", 0.2)
        m.box(-12.6, 12.6, 10.4, 11.6, -0.6, 0.6, "paint", 0.2)
        m.box(-12.6, 12.6, 0, 1.0, -0.6, 0.6, "paint", 0.2)
        m.box(-11.0, 11.0, 1.0, 10.4, -0.15, 0.15, "glass")
        m.box(-0.4, 0.4, 1.0, 10.4, -0.4, 0.4, "paint")
        with m.mirrored():
            m.box(4.0, 4.6, 10.4, 11.0, 0.5, 1.4, "dark")  # wiper motors
    # seats
    for x in (-6, 6):
        m.box(x - 3.2, x + 3.2, 9.2, 12.8, -5.5, 1.5, "seat", 0.6)
        with m.at((x, 12.0, -5.5), (-10, 0, 0)):
            m.box(-3.0, 3.0, 0, 8.0, -1.6, 0, "seat", 0.6)
    m.box(-10, 10, 9.2, 12.8, -25.6, -19.0, "seat", 0.6)  # rear bench
    m.box(-10, 10, 12.8, 19, -26.6, -25.4, "seat", 0.5)
    # steering wheel
    with m.at((-6, 19.6, 3.6), (52, 0, 0)):
        m.torus((0, 0, 0), (0, 1, 0), 2.7, 0.33, "black", n=16, m=6)
        m.tube([(0, 0, 0), (0, 0, 2.6)], 0.25, "black")
        m.tube([(0, 0, 0), (2.6, 0, 0)], 0.2, "black")
        m.tube([(0, 0, 0), (-2.6, 0, 0)], 0.2, "black")
    m.tube([(-6, 18.9, 4.4), (-6, 16.2, 7.2)], 0.35, "black")
    m.tube([(-3.5, 9.2, -0.5), (-3.5, 12.5, -1.5)], 0.3, "black")  # gear lever
    # roll bar
    m.tube([(-12.6, 17.5, -12), (-12.6, 31, -12), (12.6, 31, -12), (12.6, 17.5, -12)], 0.55, "dark")
    m.tube([(-12.6, 31, -12), (-12.6, 17.5, -25.5)], 0.5, "dark")
    m.tube([(12.6, 31, -12), (12.6, 17.5, -25.5)], 0.5, "dark")
    # bumpers, hooks, spare wheel, jerrycan, lights
    m.box(-15, 15, 8.6, 11.6, 27.2, 29.6, "dark", 0.3)
    with m.mirrored():
        m.torus((9, 10.1, 30.2), (1, 0, 0), 0.9, 0.3, "steel", 10, 6)
        m.box(13.6, 14.4, 16.0, 19.5, 6.0, 7.0, "dark")  # mirror arm
        m.box(13.2, 15.4, 19.5, 22.0, 6.2, 6.8, "steel")  # mirror
    m.box(-14, 14, 8.6, 10.6, -29.4, -27.6, "dark", 0.3)
    with m.at((4, 15.5, -29.8), (0, 90, 0)):
        m.wheel(R, W, side=1)
    m.box(-12.5, -8.5, 10.8, 18.6, -30.0, -27.6, "paint2", 0.5)  # jerrycan
    m.box(-11.3, -9.7, 18.6, 19.6, -29.4, -28.4, "dark")
    for x in (-11.8, 11.8):
        v.taillight(x, 15.8, -27.7)
    m.cyl((12.8, 17.5, -26.4), (12.8, 52, -26.4), 0.18, 0.1, mat="black", n=6)  # whip antenna
    m.box(11.6, 14.0, 13.8, 17.4, -27.0, -25.0, "dark")  # radio mount
    # pintle post
    m.cyl((0, 9.2, -15), (0, 25.6, -15), 0.8, mat="dark", n=8)
    m.cyl((0, 9.2, -15), (0, 10, -15), 2.2, mat="dark", n=10)

    def cradle(mm):
        mm.cyl((0, -0.8, 0), (0, 0.4, 0), 1.3, mat="dark", n=10)
        mm.box(-1.2, 1.2, 0.4, 2.0, -0.8, 0.8, "dark")
    v.turret((0, 25.6, -15), 1, cradle, pintle_mg, gun_at=(0, 2.4, 0), length=18, min_pitch=-60, max_pitch=25)
    v.seat(-6, 7.8, -2)
    v.seat(0, 9.5, -22)
    v.seat(6, 7.8, -2)
    return v


# ============================================================================ EV tank
def tank():
    st = Style(paint=(78, 88, 58), paint2=(62, 70, 46), camo=[(96, 80, 54), (40, 42, 34)], wear=0.7,
               rim=(78, 88, 58))
    v = V("BattleTank", "EV", st, 3.4, 2.6)
    m = v.m
    # running gear
    for x in (22, -22):
        for z in (-33, -22.5, -12, -1.5, 9, 19.5, 30):
            v.wheel(x, 6.8, z, 5.6, 9.5, build=road_wheel(5.6, 9.5))
        v.wheel(x, 12.5, -45, 6.2, 9, build=sprocket(6.2, 9))
        v.wheel(x, 12, 44.5, 5.4, 9, build=road_wheel(5.4, 9))
    track = [(-45, 5.6), (-40, 1.2), (34, 0.2), (40, 1.2), (49.4, 10.5), (48.5, 15), (44.5, 17.4), (25, 19.0),
             (0, 19.4), (-25, 19.2), (-44, 18.7), (-50.8, 14.5), (-50.8, 10.5)]
    with m.mirrored():
        band(m, [(z, y) for z, y in track], 16.4, 27.6, 1.3, "track")
        for z in (-25, 0, 25):
            m.cyl((16.6, 18.0, z), (21.0, 18.0, z), 1.6, mat="dark", n=10)
        # side skirts
        m.prism([(-46, 13), (40, 13), (47, 15.5), (48, 22.5), (-49, 22.5), (-49, 15)], 27.6, 29.0, "paint", 0.3)
        for z in range(-40, 44, 11):
            m.box(28.9, 29.2, 13.6, 22.0, z, z + 0.4, "dark")
    # hull
    m.prism([(-49, 7), (38, 4.5), (51, 13), (51, 17.5), (-49, 17.5)], -16.4, 16.4, "paint", 0.5)
    m.prism([(-51, 17), (52, 17), (52, 18.4), (33, 23.6), (-47, 23.6), (-51, 21)], -28.6, 28.6, "paint", 0.7)
    m.box(-24, 24, 23.6, 24.2, -45, -22, "vent")  # engine deck grilles
    for z in (-41, -33.5, -26):
        m.box(-22, 22, 24.2, 24.6, z, z + 1, "paint2")
    m.box(-5, 5, 23.6, 24.4, -20, -14, "paint2", 0.3)  # engine access
    m.box(-20, 20, 12, 20, -51.6, -50.8, "vent")  # rear exhaust grille
    for x in (-24, 24):
        v.taillight(x, 19.5, -51.1, 2.2, 1.2)
    # driver's hatch and periscopes
    m.box(-4.5, 4.5, 22.8, 24.4, 27, 34, "paint", 0.5)
    for x in (-3, 0, 3):
        m.box(x - 0.9, x + 0.9, 23.5, 24.8, 33.2, 34.2, "glass")
    with m.mirrored():
        m.box(19, 26, 18.4, 21.2, 45, 48.6, "paint2", 0.3)  # front fender lights
        v.headlight(22.5, 20.4, 49.5, 1.1)
        m.box(18, 27, 20.8, 23.8, -44, -32, "paint2", 0.5)  # stowage boxes
        m.torus((10, 16.2, 52.4), (1, 0, 0), 1.3, 0.45, "dark", 10, 6)  # tow eyes
        m.box(9.4, 10.6, 15.6, 18.2, 51, 52.2, "dark")
    m.tube([(-27, 24.2, -46), (27, 24.2, -46)], 0.6, "dark")  # tow cable
    v.seat(0, 17, 31, hidden=True)

    def turret(mm):
        foot = [(-5, 22), (5, 22), (19.5, 11), (21, -10), (18, -24), (-18, -24), (-21, -10), (-19.5, 11)]
        top = [(-5, 19), (5, 19), (17, 9.5), (18.5, -9), (16, -22), (-16, -22), (-18.5, -9), (-17, 9.5)]
        mm.hull([(x, 0, z) for x, z in foot] + [(x, 10.5, z) for x, z in top], "paint")
        mm.slab([(-17, 8), (17, 8), (18, -9), (15.6, -21.5), (-15.6, -21.5), (-18, -9)], 10.5, 11.3, "paint2", 0.4)
        # bustle rack
        mm.box(-17, 17, 2.2, 2.8, -33, -24, "dark")
        for x in (-16.5, -8, 0, 8, 16.5):
            mm.tube([(x, 2.8, -24), (x, 8.5, -24.2), (x, 8.5, -32.6), (x, 2.8, -32.8)], 0.3, "dark")
        mm.tube([(-16.5, 8.5, -32.6), (16.5, 8.5, -32.6)], 0.3, "dark")
        mm.box(-14, -3, 2.8, 7.4, -31.6, -25.2, "canvas", 0.8)  # rolled tarp and boxes
        mm.box(2, 9, 2.8, 6.8, -31.6, -25.6, "paint2", 0.4)
        # commander cupola with MG, loader hatch, sights
        mm.cyl((9, 11.3, -8), (9, 13.6, -8), 4.2, 3.8, mat="paint", n=14)
        for k in range(6):
            a = math.radians(30 + k * 52)
            mm.box(9 + 3.9 * math.cos(a) - 0.6, 9 + 3.9 * math.cos(a) + 0.6, 12.0, 13.0,
                   -8 + 3.9 * math.sin(a) - 0.6, -8 + 3.9 * math.sin(a) + 0.6, "glass")
        mm.cyl((9, 13.6, -8), (9, 14.4, -8), 3.4, mat="paint2", n=14)
        with mm.at((9, 16.2, -6.4)):
            mm.box(-0.8, 0.8, -1.8, 0, -0.6, 0.6, "dark")
            hmg(mm, 16)
        mm.box(-13, -5, 11.3, 12.3, -14, -6, "paint2", 0.4)  # loader hatch
        mm.box(-11.4, -6.6, 12.3, 15.2, -1.6, 1.0, "dark", 0.3)  # loader's MG shield
        mm.box(10.5, 16.5, 11.3, 15.8, 7.5, 12.5, "paint", 0.5)  # gunner's primary sight
        mm.box(11.2, 15.8, 12.0, 15.0, 12.5, 12.8, "glass")
        mm.box(-2, 2, 11.3, 13.2, -21, -19, "dark")  # wind sensor
        mm.cyl((0, 13.2, -20), (0, 17.6, -20), 0.2, mat="black", n=6)
        for x in (-14, 14):
            mm.cyl((x, 11.3, -19), (x, 50, -19), 0.18, 0.08, mat="black", n=6)
        with mm.mirrored():
            for k in range(3):  # smoke grenade launchers
                mm.cyl((19 + k * 0.2, 6.5 + k * 1.6, 3 + k * 0.4), (20.4 + k * 0.2, 7.5 + k * 1.6, 6.5 + k * 0.4),
                       0.9, mat="dark", n=8)
            mm.box(19.2, 21.2, 0.5, 9.0, -1, 3, "paint2", 0.3)
        mm.box(-6.5, 6.5, 0.8, 9.5, 18, 21.5, "paint", 0.5)  # mantlet housing

    def gun(mm):
        mm.box(-5.4, 5.4, -3.8, 3.6, -3, 4.2, "paint", 0.6)  # mantlet
        mm.box(-1.2, 1.2, 3.6, 5.4, -1, 2.5, "dark")  # coax port
        mm.cyl((3.0, -1.2, 3.8), (3.0, -1.2, 6), 0.45, mat="black", n=6)
        mm.pipe((0, 0, 4.2), (0, 0, 58), [(0, 2.1), (0.04, 2.1), (0.04, 1.7), (0.3, 1.7), (0.33, 1.95), (0.36, 2.4),
                                          (0.47, 2.4), (0.5, 1.95), (0.52, 1.6), (0.97, 1.45), (1.0, 1.55)],
                "paint2", n=14)
        for z in (14, 24, 44):
            mm.cyl((0, 0, z), (0, 0, z + 0.5), 1.9 if z < 30 else 1.7, mat="dark", n=14)
        mm.cyl((0, 0, 58), (0, 0, 58.2), 1.0, mat="black", n=10, caps=(False, True))
        mm.box(-0.5, 0.5, 1.5, 2.6, 56.2, 57.6, "dark")  # muzzle reference sensor

    v.turret((0, 23.6, -2), 0, turret, gun, gun_at=(0, 6, 19), length=58, min_pitch=-25, max_pitch=10)
    v.seat(9, 31, -10)
    return v


# ============================================================================ MV truck
def truck():
    st = Style(paint=(98, 104, 70), paint2=(78, 84, 56), wear=0.65, rim=(98, 104, 70), canvas=(122, 116, 82),
               seat=(60, 58, 44), marking=(222, 222, 206))
    v = V("OffroadTruck", "MV", st, 2.6, 2.8)
    m = v.m
    R, W = 8.0, 6.5
    for z, steer in ((28, True), (-14, False), (-30, False)):
        for x in (13.5, -13.5):
            v.wheel(x, R, z, R, W, steer=steer)
    with m.mirrored():
        m.box(6, 8.4, 8, 11, -44, 41, "dark")  # frame rails
        m.box(9, 12, 7, 9, 22, 34, "dark")  # springs
        m.box(9, 12, 7, 9, -36, -8, "dark")
    for z in (28, -14, -30):
        m.cyl((-12, R, z), (12, R, z), 1.2, mat="dark", n=8)
        m.pipe((0, R, z - 3), (0, R, z + 3), [(0, 1.2), (0.25, 3.0), (0.75, 3.0), (1, 1.2)], "dark", n=10)
    # bonnet, grille, fenders
    m.prism([(24, 15), (42, 15), (42.8, 25), (41.2, 27), (24, 27.6)], -9.5, 9.5, "paint", bevel=0.7)
    m.box(-8, 8, 16, 25, 42.6, 43.2, "vent")
    m.box(-0.5, 0.5, 16, 25.4, 42.9, 43.4, "paint")
    with m.mirrored():
        m.prism([(17, 15.5), (19, 22), (23, 25.5), (33, 26.2), (39.5, 24.5), (42.5, 20), (42.8, 15.5),
                 (40, 15.5), (38.5, 20.5), (34, 23.4), (23, 23.4), (20, 20), (19.5, 15.5)], 9.5, 17.2, "paint", 0.5)
        v.headlight(13.4, 21.5, 42.5, 1.7)
        m.box(10.2, 16.6, 25.4, 26.2, 36, 39, "dark")  # step plate
    m.box(-17.5, 17.5, 11, 15.5, 43, 46, "dark", 0.4)  # bumper
    m.cyl((-8, 13.2, 47.2), (8, 13.2, 47.2), 1.8, mat="dark", n=10)  # winch
    with m.mirrored():
        m.box(7.5, 9.5, 11.6, 14.8, 46, 48.6, "dark")
        m.torus((14, 12.5, 46.6), (1, 0, 0), 1.0, 0.35, "steel", 10, 6)
    # cab
    cab = [(6, 14), (24.2, 14), (24.6, 27.4), (22.6, 40.5), (7.5, 40.5), (6, 39)]
    m.prism(cab, -15.4, 15.4, "paint", bevel=0.9)
    ang = math.degrees(math.atan2(2.0, 13.1))
    with m.at((0, 28.3, 24.45), (-ang, 0, 0)):
        with m.mirrored():
            m.box(1.0, 13.6, 0.3, 10.8, -0.1, 0.25, "glass")
            m.box(5.5, 6.2, 10.8, 11.6, 0.1, 1.1, "dark")  # wiper motors
    with m.mirrored():
        m.box(15.3, 15.6, 29.4, 38.2, 10, 21, "glass")  # door windows
        m.box(15.3, 15.7, 15.5, 28.5, 21.6, 22.0, "dark")  # door seam
        m.box(15.3, 15.7, 15.5, 38.6, 9.4, 9.8, "dark")
        m.box(15.4, 16.2, 25.4, 26.4, 18, 20.5, "steel")  # handle
        m.tube([(15.4, 33, 22.5), (19.5, 33, 24), (19.5, 36, 24)], 0.3, "dark")  # mirror arm
        m.box(18.6, 20.4, 33, 39, 23.6, 24.4, "steel")
        m.box(14.0, 16.4, 12.4, 14, 14, 22, "dark")  # step
        m.cyl((11.5, 12.5, 2), (11.5, 12.5, 16), 3.6, mat="paint2", n=14)  # fuel tanks
        m.box(8.2, 11, 11, 14, 4, 6, "dark")
    m.box(-11, 11, 40.5, 41.4, 9, 20, "paint2", 0.3)  # roof panel
    m.torus((0, 41.6, 14), (0, 1, 0), 5.6, 0.7, "dark", 20, 6)  # ring mount
    m.cyl((15.9, 22, 22.5), (15.9, 45, 22.5), 0.9, mat="black", n=8)  # exhaust stack
    m.cyl((15.9, 45, 22.5), (15.9, 45.6, 22.5), 1.3, mat="black", n=8)
    # cargo bed
    m.box(-16.4, 16.4, 17, 19.4, -44, 5, "paint2", 0.3)
    with m.mirrored():
        m.box(15.0, 16.4, 19.4, 27.4, -44, 5, "paint", 0.3)
        for z in range(-42, 5, 8):
            m.box(16.3, 16.7, 19.4, 27.4, z, z + 0.9, "paint2")
        for z in (-36, -20, -4):
            m.box(16.0, 16.6, 27.4, 40.5, z, z + 1, "dark")  # canvas straps
        m.box(14.6, 18, 9.5, 17, -40, -38.8, "black")  # mud flaps
    m.box(-16.4, 16.4, 19.4, 27.4, -45.2, -44, "paint", 0.3)  # tailgate
    m.box(-16.4, 16.4, 19.4, 31, 4.8, 6.0, "paint", 0.3)  # headboard
    m.body([(z, 0, 29.5, 15.6, 12.2, 3.2) for z in (-44.4, -44, 4.2, 4.6)], "canvas", n=24)
    for z in (-34, -18, -2):
        m.body([(z, 0, 29.5, 15.9, 12.5, 3.2), (z + 0.8, 0, 29.5, 15.9, 12.5, 3.2)], "canvas", n=24)
    for x in (-13.6, 13.6):
        v.taillight(x, 15.5, -44.6, 2.4, 1.2)
    m.box(-12, -4, 14.4, 16.6, -44.6, -43.8, "dark")  # bumper
    m.box(4, 12, 14.4, 16.6, -44.6, -43.8, "dark")
    m.cyl((-14, 40.5, 8), (-14, 70, 8), 0.2, 0.1, mat="black", n=6)

    def ring(mm):
        mm.box(-4.2, 4.2, -0.6, 0.6, -1, 1, "dark")
        mm.box(-1.2, 1.2, 0.6, 3.6, -1, 1, "dark")
        mm.box(-6, 6, 1.0, 9, 4.0, 4.8, "paint2", 0.3)  # gun shield
        mm.box(-1.4, 1.4, 4.0, 6.6, 3.8, 5.0, "glass")
    v.turret((0, 41.6, 14), 2, ring, hmg, gun_at=(0, 5, 1), length=26)
    v.seat(-7, 16, 15, hidden=True)
    v.seat(7, 16, 15, hidden=True)
    v.seat(0, 36, 12)
    v.seat(0, 21, -22)
    return v


# ============================================================================ HV APC
def apc():
    st = Style(paint=(156, 140, 98), paint2=(126, 112, 78), wear=0.7, rim=(126, 112, 78), seat=(60, 58, 44))
    v = V("ArmoredAPC", "HV", st, 2.8, 2.4)
    m = v.m
    R, W = 7.2, 6.4
    for i, z in enumerate((31, 18, -14, -27)):
        for x in (16.5, -16.5):
            v.wheel(x, R, z, R, W, steer=i < 2)
    m.hull([(x, y, z) for x in (-13, 13) for y, z in ((7, -40), (7, 33), (16, 46), (16, -45))], "paint2")
    up = []
    for s in (-1, 1):
        up += [(s * 20.2, 16.5, -45), (s * 20.2, 16.5, 41), (s * 19.4, 20.5, 48.2), (s * 16.5, 31, -44),
               (s * 16.5, 31, 27), (s * 14, 25.5, 47.5)]
    m.hull(up, "paint")
    with m.mirrored():
        for z0, z1 in ((10, 40), (-36, -6)):  # bolted add-on armour over the wheels
            m.hull([(20.2, 16.6, z0), (20.2, 16.6, z1), (21.4, 17.3, z0 + 0.8), (21.4, 17.3, z1 - 0.8),
                    (17.6, 27.6, z0), (17.6, 27.6, z1), (18.9, 27.6, z0 + 0.8), (18.9, 27.6, z1 - 0.8)], "paint2")
            for z in range(z0 + 3, z1, 6):
                m.box(20.6, 21.6, 18.5, 19.3, z - 0.4, z + 0.4, "steel")
        m.box(14, 19, 17.4, 21.6, 47.4, 48.6, "dark")  # light guard base
        v.headlight(16.5, 23.2, 46.6, 1.2)
        for k in range(4):  # smoke launchers
            m.cyl((14.8, 31 + k * 0.2, 20 - k * 2.4), (15.6, 33.8, 21.8 - k * 2.4), 0.8, mat="dark", n=8)
        m.box(19.8, 21.6, 22, 26, -44, -30, "dark", 0.3)  # stowage rail box
        m.torus((10, 12, 47), (1, 0, 0), 1.1, 0.4, "dark", 10, 6)
        v.taillight(16.5, 24, -45.2, 2.0, 1.0)
    m.hull([(x, y, z) for x in (-13.6, 13.6) for y, z in ((21.6, 48.6), (27.6, 47.2), (21.6, 48.0), (27.4, 46.6))],
           "paint2")  # trim vane
    # ramp, hatches, vision blocks, exhaust
    m.box(-11, 11, 9, 29, -45.6, -44.8, "paint2", 0.4)
    m.box(-2, 2, 18, 22, -45.9, -45.4, "glass")
    for x in (-9.6, 9.6):
        m.cyl((x, 12, -46), (x, 26, -46), 0.7, mat="dark", n=8)
    m.box(-12, -5, 31, 32.2, 30, 38.5, "paint2", 0.4)  # driver hatch
    for x in (-12, -8.5, -5):
        m.box(x - 1, x + 1, 30.6, 32.4, 38.6, 39.6, "glass")
    m.box(-12, 12, 31, 32, -38, -20, "paint2", 0.3)  # troop hatches
    m.box(-0.5, 0.5, 31, 32.2, -38, -20, "dark")
    m.box(19.0, 19.6, 22, 28, 30, 38, "vent")  # engine exhaust on the right
    for x in (-15, 15):
        m.cyl((x, 31, -40), (x, 62, -40), 0.2, 0.08, mat="black", n=6)
    m.box(-6, 6, 31, 32.6, 12, 19, "dark", 0.3)  # turret ring plate

    def rws(mm):
        mm.cyl((0, 0, 0), (0, 1.4, 0), 5.2, mat="paint2", n=16)
        mm.box(-4.6, 4.6, 1.4, 4.0, -4.4, 3.6, "paint", 0.4)
        mm.box(-6.8, -3.6, 1.4, 8.6, -2.6, 4.2, "paint", 0.5)  # sight housing
        mm.box(-6.4, -4.0, 5.0, 8.0, 4.2, 4.5, "glass")
        mm.box(3.6, 7.4, 1.4, 6.8, -4, 2, "paint2", 0.4)  # ammo box
    v.turret((0, 32.6, 15), 1, rws, hmg, gun_at=(0, 6.4, 1.6), length=26, min_pitch=-60, max_pitch=20)
    v.seat(-8.5, 15, 35, hidden=True)
    v.seat(0, 15, 14, hidden=True)
    v.seat(-7, 13, -20, hidden=True)
    v.seat(7, 13, -20, hidden=True)
    return v


# ============================================================================ sci-fi parts
def laser_gun(length=16, r=0.9, twin=False):
    def build(mm):
        mm.box(-2.4, 2.4, -2.0, 2.0, -3.4, 3.2, "armor", 0.6)
        for x in ((-1.4, 1.4) if twin else (0,)):
            mm.cyl((x, 0, 3.2), (x, 0, length), r, mat="dark", n=10)
            for z in (6, 9.5, 13):
                if z < length - 2:
                    mm.cyl((x, 0, z), (x, 0, z + 1.0), r + 0.5, mat="glow", n=10)
            mm.cyl((x, 0, length), (x, 0, length + 0.6), r * 0.7, mat="core", n=10)
    return build


def beam_cannon(length, r=2.0, coils=5, fork=False):
    def build(mm):
        mm.box(-r - 2.6, r + 2.6, -r - 2.0, r + 2.0, -4, 4, "armor", 0.8)
        mm.pipe((0, 0, 3.5), (0, 0, length), [(0, r * 1.2), (0.1, r), (0.9, r * 0.85), (1, r * 1.1)], "paint2",
                n=14)
        for k in range(coils):
            z = 6 + (length - 10) * k / max(1, coils - 1)
            mm.torus((0, 0, z), (0, 0, 1), r * 1.25, 0.55, "glow", 16, 6)
            mm.cyl((0, 0, z - 1), (0, 0, z + 1), r * 1.1, mat="dark", n=14)
        if fork:
            with mm.mirrored():
                mm.hull([(r * 0.6, -0.8, length - 6), (r * 0.6, 0.8, length - 6), (r * 2.4, -0.6, length + 4),
                         (r * 2.4, 0.6, length + 4), (r * 1.4, -0.8, length - 7), (r * 1.4, 0.8, length - 7)], "armor")
        mm.cyl((0, 0, length - 0.2), (0, 0, length + 0.5), r * 0.75, mat="core", n=14)
    return build


def turret_dome(r=4.5, h=3.0, mat="armor", ring="glow"):
    def build(mm):
        mm.cyl((0, 0, 0), (0, 0.8, 0), r + 0.6, mat="dark", n=16)
        mm.dome((0, 0.8, 0), r, h, r, mat, n=16, m=4)
        if ring:
            mm.torus((0, 0.9, 0), (0, 1, 0), r + 0.3, 0.35, ring, 20, 5)
    return build


def engine(v, x, y, z, r, length=8.0, mat="dark"):
    """Engine bell pointing backwards from z, glowing inside; registers the flame."""
    m = v.m
    m.pipe((x, y, z), (x, y, z - length), [(0, r * 0.75), (0.3, r * 0.9), (0.7, r), (1, r * 1.12)], mat, n=16)
    m.cyl((x, y, z - length - 0.05), (x, y, z - length + 0.6), r * 0.92, mat="glow", n=16)
    m.cyl((x, y, z - length - 0.1), (x, y, z - length + 0.6), r * 0.45, mat="core", n=12)
    v.exhaust(x, y, z - length, r * 0.9)


def greebles(m, x0, x1, z0, z1, y, count, seed, mats=("paint2", "dark", "armor"), hmax=1.6):
    import random
    rnd = random.Random(seed)
    for _ in range(count):
        w, d = rnd.uniform(1, 4), rnd.uniform(1, 5)
        x, z = rnd.uniform(x0, x1 - w), rnd.uniform(z0, z1 - d)
        h = rnd.uniform(0.4, hmax)
        m.box(x, x + w, y, y + h, z, z + d, rnd.choice(mats), 0.2)


# ============================================================================ IV hover jeep
def hover():
    st = Style(paint=(38, 62, 118), paint2=(28, 40, 70), wear=0.15, glow=(40, 200, 255), core=(210, 245, 255),
               scifi=True, seat=(40, 44, 58))
    v = V("HoverJeep", "IV", st, 2.6, 1.6)
    m = v.m
    m.body([(-34, 0, 11, 9, 3.6, 3), (-30, 0, 10.5, 15.5, 5.8, 3.4), (-10, 0, 10, 17.5, 6.6, 3.6),
            (12, 0, 9.4, 16, 5.8, 3.2), (28, 0, 8.4, 11, 4.2, 2.6), (35, 0, 7.8, 5.4, 2.6, 2.3),
            (37, 0, 7.6, 1.4, 1.2, 2)], "paint", n=24)
    m.body([(-26, 0, 6.2, 12, 2.4, 3), (24, 0, 5.8, 10, 2.2, 3)], "paint2", n=16)  # belly plate
    with m.mirrored():
        m.body([(-30, 18.5, 6.6, 2.4, 2.4, 2.4), (-26, 18.5, 6.6, 3.4, 3.4, 2.6), (20, 18.5, 6.4, 3.2, 3.2, 2.6),
                (28, 18.5, 6.2, 1.8, 2.2, 2.2)], "paint2", n=14)
        m.body([(-27, 18.5, 6.6, 3.5, 3.5, 2.6), (-24, 18.5, 6.6, 3.5, 3.5, 2.6)], "armor", n=14)
        m.body([(16, 18.5, 6.4, 3.3, 3.3, 2.6), (19, 18.5, 6.4, 3.3, 3.3, 2.6)], "armor", n=14)
        m.box(21.4, 22.0, 5.8, 7.2, -22, 15, "glow")  # nacelle light strip
        m.hull([(13, 9, -14), (13, 9, 12), (13, 11, -12), (13, 11, 10), (18, 7.2, -12), (18, 7.2, 10),
                (18, 8.6, -12), (18, 8.6, 10)], "paint2")  # struts
        for z in (-20, 14):
            m.cyl((18.5, 2.2, z), (18.5, 3.4, z), 3.6, mat="dark", n=16)
            m.cyl((18.5, 2.0, z), (18.5, 2.3, z), 2.8, mat="glow", n=16)
            v.pad(18.5, 2.0, z, 3.8)
        m.box(3, 9, 9.2, 10.4, 33.6, 34.2, "light")  # headlight strips
        with m.at((4.5, 13.5, -29), (0, 0, -16)):  # tail fins
            m.prism([(-6, 0), (4, 0), (-2, 9), (-6, 9.5)], -0.5, 0.5, "paint", 0.2)
            m.box(-0.6, 0.6, 7.5, 9.0, -6, -2, "glow")
    # cockpit: seats on the deck, dash and a curved windscreen
    m.box(-11, 11, 15.6, 16.8, -12, 14, "paint2", 0.4)
    for x in (-5.5, 5.5):
        m.box(x - 3.2, x + 3.2, 16.4, 18.4, -4, 3, "seat", 0.7)
        with m.at((x, 18.0, -4), (-14, 0, 0)):
            m.box(-3.0, 3.0, 0, 7.5, -1.6, 0, "seat", 0.7)
    m.box(-9.5, 9.5, 16.4, 20.2, 7, 11.5, "dark", 0.7)
    m.box(-8, 8, 20.0, 20.4, 8, 10.6, "glow")
    arc = [math.radians(a) for a in range(-60, 61, 15)]
    bot = [(11 * math.sin(a), 20.2, 7 + 6 * math.cos(a)) for a in arc]
    top = [(12 * math.sin(a), 26.0, 3.6 + 6 * math.cos(a)) for a in arc]
    for i in range(len(arc) - 1):
        q = [bot[i], bot[i + 1], top[i + 1], top[i]]
        m.quad(*q, "glass")
        m.quad(*q[::-1], "glass")
    m.tube(top, 0.3, "dark")
    # rear thruster and deck
    m.pipe((0, 11, -33), (0, 11, -39), [(0, 4.6), (0.6, 4.4), (1, 4.8)], "dark", n=16)
    m.cyl((0, 11, -39.1), (0, 11, -38.4), 4.2, mat="glow", n=16)
    v.exhaust(0, 11, -39, 4)
    m.box(-8, 8, 15.4, 16.6, -26, -16, "armor", 0.5)
    for x in (-8, 8):
        v.taillight(x, 12.4, -33.8, 3.0, 0.8)

    def rail(mm):
        mm.cyl((0, 0, 0), (0, 1.2, 0), 4.6, mat="dark", n=16)
        mm.dome((0, 1.2, 0), 4.0, 2.4, 4.0, "armor", n=16, m=4)

    def railgun(mm):
        mm.box(-2.4, 2.4, -1.8, 2.0, -4, 4, "armor", 0.5)
        with mm.mirrored():
            mm.box(0.9, 1.9, -1.4, 1.4, 3.5, 22, "dark", 0.2)
            for z in (7, 11, 15, 19):
                mm.box(1.9, 2.3, -1.2, 1.2, z, z + 1.4, "glow")
        mm.box(-0.9, 0.9, -0.5, 0.5, 4, 21.5, "core")
    v.turret((0, 15.0, 21), 0, rail, railgun, gun_at=(0, 3.6, 0), length=22, min_pitch=-30, max_pitch=15)
    v.seat(-5.5, 13.4, -1)
    v.seat(5.5, 13.4, -1)
    v.seat(0, 12.4, -20)
    return v


# ============================================================================ LuV tiltrotor
def vtol():
    st = Style(paint=(112, 118, 124), paint2=(84, 90, 96), wear=0.3, glow=(255, 70, 190), core=(255, 215, 240),
               scifi=False, seat=(50, 52, 58), rim=(84, 90, 96))
    v = V("TiltrotorVTOL", "LuV", st, 4.0, 2.4)
    m = v.m
    m.body([(60, 0, 14, 0.8, 0.8, 2), (57, 0, 14.2, 5.5, 5.2, 2.2), (50, 0, 15.5, 9.5, 8.8, 2.4),
            (40, 0, 17, 11.6, 10.6, 2.6), (10, 0, 17.4, 12.2, 11.2, 2.9), (-18, 0, 17.6, 12, 11, 2.9),
            (-34, 0, 20.5, 9, 8, 2.6), (-50, 0, 24.4, 4.5, 4.2, 2.4), (-57, 0, 25.4, 1.8, 2, 2)], "paint", n=24)
    m.dome((0, 19.5, 45), 8.4, 6.6, 10.5, "glass", n=20, m=6)  # cockpit glazing
    m.body([(44, 0, 26.8, 0.3, 0.3, 2), (40, 0, 26.6, 4, 1.4, 2), (24, 0, 26.2, 4.8, 1.8, 2)], "paint2", n=12)
    with m.mirrored():
        m.body([(22, 12, 9, 2, 2, 2), (18, 12.4, 9, 4.4, 4.2, 2.4), (-14, 12.4, 9, 4.4, 4.2, 2.4),
                (-20, 12, 10, 2, 2.4, 2)], "paint2", n=14)  # sponsons
        m.cyl((13, 4.5, 4), (13, 8, 4), 0.6, mat="steel", n=6)
        with m.at((13, 3.2, 4), (0, 0, 0)):
            m.wheel(3.2, 2.4, side=1)
        # wing
        m.hull([(0, 29, -2), (0, 29, 14), (0, 31.4, 0), (0, 31.4, 11), (40, 29.4, -1), (40, 29.4, 10),
                (40, 30.8, 0.5), (40, 30.8, 8.5)], "paint")
        m.box(10, 30, 28.8, 29.2, -2.2, 0.2, "paint2")  # flaperons
        # nacelle, standing up (helicopter mode)
        m.pipe((42, 22, 4), (42, 43, 4), [(0, 2.6), (0.12, 4.6), (0.8, 4.8), (1, 3.6)], "paint", n=16)
        m.box(41.4, 42.6, 28, 36, 8.4, 9.0, "vent")
        m.box(46.4, 46.8, 30, 31.2, 2, 6, "glow")  # nav light
        # stub pylon with rocket pod
        m.box(14, 17, 4.4, 9.4, 0, 8, "paint2", 0.3)
        m.cyl((15.6, 2.4, -6), (15.6, 2.4, 12), 2.6, mat="paint2", n=12, cap_mat="vent")
    m.cyl((0, 4, 40), (0, 8, 40), 0.7, mat="steel", n=6)
    with m.at((0, 2.6, 40)):
        m.wheel(2.6, 2.0, side=1)
    # tail: H stabiliser
    m.hull([(-20, 26, -54), (20, 26, -54), (-20, 26, -46), (20, 26, -46), (-20, 27, -53), (20, 27, -53),
            (-20, 27, -47), (20, 27, -47)], "paint")
    with m.mirrored():
        m.prism([(-56, 22), (-46, 22), (-48, 38), (-55, 39)], 19.4, 20.6, "paint", 0.3)
        m.box(20.6, 20.8, 36, 37, -54, -52, "tail")
    # windows, doors, ramp, probe
    with m.mirrored():
        for z in (30, 22, 14):
            m.box(12.0, 12.6, 20, 23, z, z + 3, "glass")
    m.box(-12.2, -11.6, 9, 24, 24, 32, "paint2")
    m.box(-8, 8, 8.4, 20, -36, -35, "paint2")
    m.tube([(8, 22, 50), (9, 22, 62), (9, 22, 70)], 0.4, "steel")
    m.cyl((0, 6.5, 30), (0, 6.5, 36), 1.6, mat="dark", n=10)  # sensor ball mount
    for x in (-42, 42):
        m.cyl((x, 43, 4), (x, 44.2, 4), 1.4, mat="dark", n=10)

    def prop(mm, i):
        mm.pipe((0, -1.6, 0), (0, 2.2, 0), [(0, 2.4), (0.5, 2.2), (1, 0.6)], "paint2", n=12)
        for k in range(3):
            a = 120 * k + (0 if i % 2 == 0 else 60)
            with mm.at((0, 0.3, 0), (0, a, 0)):
                if True:
                    mm.hull([(1.5, -0.25, -1.4), (1.5, 0.25, -1.4), (1.5, -0.25, 1.2), (1.5, 0.25, 1.2),
                             (27, -0.15, -0.6), (27, 0.15, -0.6), (27, -0.15, 0.9), (27, 0.15, 0.9)], "dark")
                    mm.box(25, 27, -0.2, 0.2, -0.65, 0.95, "hazard")
    v.rotor(42, 45.4, 4, 27, prop)
    v.rotor(-42, 45.4, 4, 27, prop)
    v.mount(15.6, 2.4, 12.5)
    v.mount(-15.6, 2.4, 12.5)

    def chin(mm):
        mm.sphere((0, -2, 0), 3, "dark", 12, 8)

    def minigun(mm):
        mm.box(-1.6, 1.6, -1.6, 1.6, -3, 2, "dark", 0.3)
        for k in range(6):
            a = 2 * math.pi * k / 6
            mm.cyl((math.cos(a) * 0.9, math.sin(a) * 0.9, 2), (math.cos(a) * 0.9, math.sin(a) * 0.9, 12), 0.3,
                   mat="black", n=6)
        for z in (5, 11.5):
            mm.cyl((0, 0, z), (0, 0, z + 0.6), 1.5, mat="dark", n=10)
    v.turret((0, 6.5, 33), 1, chin, minigun, gun_at=(0, -2.4, 0), length=12, min_pitch=-10, max_pitch=70)
    v.seat(-3.5, 14.6, 46, hidden=True)
    v.seat(3.5, 14.6, 46, hidden=True)
    v.seat(-5, 13, 6, hidden=True)
    v.seat(5, 13, 6, hidden=True)
    return v


# ============================================================================ ZPM fighter
def fighter():
    st = Style(paint=(58, 54, 76), paint2=(40, 38, 54), wear=0.15, glow=(255, 120, 30), core=(255, 225, 190),
               scifi=True, seat=(40, 40, 50))
    v = V("PlasmaFighter", "ZPM", st, 4.4, 1.8)
    m = v.m
    m.body([(62, 0, 11, 0.4, 0.4, 1.4), (54, 0, 11.4, 3.4, 2.6, 1.5), (40, 0, 12.4, 6.6, 4.6, 1.6),
            (22, 0, 13, 9, 5.4, 1.7), (0, 0, 12.8, 12, 5.2, 1.8), (-24, 0, 12.4, 12.4, 4.6, 1.8),
            (-38, 0, 12.2, 10, 4, 1.8)], "paint", n=16)
    m.dome((0, 15.2, 28), 4.4, 4.4, 12, "glass", n=18, m=6)
    m.body([(17, 0, 16.4, 4.6, 0.8, 2), (12, 0, 16.6, 4.8, 1.4, 2), (-6, 0, 16, 3.6, 1.6, 2), (-24, 0, 15, 2.2, 1.2, 2)],
           "paint2", n=12)  # dorsal spine
    with m.mirrored():
        # intakes
        m.hull([(7, 9.4, 22), (11, 9.4, 22), (7, 14.2, 22), (10.6, 13.8, 22), (8, 9.8, 4), (12.4, 9.8, 4),
                (8, 13.8, 4), (12.2, 13.6, 4)], "paint2")
        m.box(7.6, 10.6, 10.2, 13.4, 22, 22.4, "black")
        # wing: swept delta with a cranked tip
        m.hull([(10, 11.2, 18), (10, 13.0, 14), (10, 11.4, -30), (10, 12.6, -28), (50, 11.6, -20), (50, 12.0, -20),
                (52, 11.6, -30), (52, 12.0, -29)], "paint")
        m.box(49.6, 52.4, 11.0, 12.6, -30, -21, "paint2", 0.2)  # tip rail
        m.box(51.6, 52.2, 11.2, 12.4, -23, -20.6, "glow")
        for z in (-26, -14):
            m.box(14, 46, 12.1, 12.35, z, z + 0.3, "paint2")  # panel seams
        # canted tails
        with m.at((8, 15.5, -28), (0, 0, -22)):
            m.prism([(-12, 0), (2, 0), (-6, 13), (-11, 13.4)], -0.5, 0.5, "paint", 0.2)
            m.box(-0.6, 0.6, 11.5, 13.2, -10.5, -7, "glow")
        # stabilators
        m.hull([(12, 11.8, -30), (12, 12.6, -30), (12, 11.8, -40), (24, 12, -40), (24, 12.2, -44),
                (12, 12.4, -42)], "paint")
        # plasma cannon in the wing root
        m.cyl((12.5, 10.4, 6), (12.5, 10.4, 30), 1.2, mat="dark", n=10)
        for z in (14, 19, 24):
            m.torus((12.5, 10.4, z), (0, 0, 1), 1.6, 0.45, "glow", 12, 5)
        m.cyl((12.5, 10.4, 30), (12.5, 10.4, 30.6), 0.8, mat="core", n=10)
        # missile on a pylon
        m.box(25.2, 26.8, 9.0, 11.6, -12, -2, "paint2", 0.2)
        m.pipe((26, 7.4, -20), (26, 7.4, 6), [(0, 0.8), (0.05, 1.3), (0.8, 1.3), (0.93, 1.1), (1, 0.2)], "armor", n=10)
        m.cyl((26, 7.4, -1), (26, 7.4, 1), 1.35, mat="tail", n=10)
        for a in (0, 90, 180, 270):
            with m.at((26, 7.4, -18), (0, 0, a)):
                m.box(-0.15, 0.15, 1.2, 3.2, -2, 1.5, "paint2")
        # engines
        engine(v, 5.6, 12.4, -36, 3.8, 7, "dark")
        # gear
        m.cyl((10, 2.8, -10), (10, 10, -10), 0.6, mat="steel", n=6)
        with m.at((10, 2.4, -10)):
            m.wheel(2.4, 1.8, side=1)
    m.cyl((0, 2.8, 36), (0, 10, 36), 0.6, mat="steel", n=6)
    with m.at((0, 2.2, 36)):
        m.wheel(2.2, 1.6, side=1)
    m.box(-0.8, 0.8, 11.2, 11.8, 55, 57, "light")
    v.mount(12.5, 10.4, 30.6)
    v.mount(-12.5, 10.4, 30.6)
    v.seat(0, 9.6, 26, hidden=True)
    v.seat(0, 9.6, 16, hidden=True)
    return v


# ============================================================================ UV cruiser
def cruiser():
    st = Style(paint=(206, 210, 222), paint2=(150, 156, 172), wear=0.08, glow=(110, 160, 255),
               core=(225, 235, 255), scifi=True)
    v = V("ParticleCruiser", "UV", st, 5.0, 2.6)
    m = v.m
    m.body([(-56, 0, 16, 9, 6, 3.2), (-40, 0, 16, 13, 8, 3.4), (20, 0, 16, 12, 7.5, 3.4), (40, 0, 15.5, 8, 6, 3),
            (52, 0, 15, 5, 4, 2.6)], "paint", n=24)
    m.box(-10, 10, 22, 23.6, -48, 30, "armor", 0.6)
    # particle lance with accelerator coils
    m.cyl((0, 15, 50), (0, 15, 92), 2.0, mat="paint2", n=14)
    for k in range(7):
        z = 56 + k * 5.5
        m.torus((0, 15, z), (0, 0, 1), 3.4, 0.9, "glow" if k % 2 == 0 else "dark", 18, 6)
    m.cyl((0, 15, 92), (0, 15, 93), 2.6, mat="dark", n=14)
    m.cyl((0, 15, 93), (0, 15, 93.6), 1.4, mat="core", n=12)
    v.mount(0, 15, 93.6)
    # bridge tower
    m.hull([(x, y, z) for x in (-6, 6) for y, z in ((23.6, -30), (23.6, -6), (33, -26), (33, -10))], "armor")
    m.hull([(x, y, z) for x in (-4.5, 4.5) for y, z in ((33, -24), (33, -11), (37, -22), (37, -13))], "paint")
    m.box(-4.4, 4.4, 34, 35.4, -11.2, -10.8, "glass")
    with m.mirrored():
        m.box(6.0, 6.3, 26, 31, -27, -9, "glass")
    m.cyl((0, 37, -18), (0, 46, -18), 0.3, mat="steel", n=6)
    m.dome((0, 37, -18), 3, 1.0, 3, "steel", 12, 3)
    # side pods on pylons
    with m.mirrored():
        m.hull([(10, 13, -26), (10, 13, 6), (10, 18, -22), (10, 18, 2), (24, 13.5, -22), (24, 13.5, 2),
                (24, 16.5, -20), (24, 16.5, 0)], "paint2")
        m.body([(-46, 30, 15, 4, 4, 2.6), (-40, 30, 15, 7, 7, 3), (16, 30, 15, 7, 7, 3), (28, 30, 15, 4, 4, 2.4)],
               "paint", n=20)
        m.box(36.6, 37.4, 13.5, 16.5, -36, 14, "glow")
        m.box(23.4, 24.2, 13.5, 16.5, -36, 14, "glow")
        engine(v, 30, 15, -46, 4.8, 8)
        greebles(m, 10, 22, -46, 20, 23.6, 14, 3, hmax=1.4)
        # radiator fins
        for z in range(-40, -24, 4):
            m.box(12.8, 14, 6, 15, z, z + 1.4, "paint2")
    engine(v, 0, 16, -56, 6.4, 10)
    greebles(m, -9, 9, 8, 28, 23.6, 10, 4, hmax=1.2)
    # sensor array keel
    m.prism([(-30, 9), (30, 9), (22, 3), (-20, 3)], -1.2, 1.2, "paint2", 0.3)
    v.turret((-30, 22, 4), 1, turret_dome(4.0, 2.4), laser_gun(16, 0.9, twin=True), gun_at=(0, 3.2, 0), length=16)
    v.turret((30, 22, 4), 2, turret_dome(4.0, 2.4), laser_gun(16, 0.9, twin=True), gun_at=(0, 3.2, 0), length=16)
    v.seat(0, 26, -18, hidden=True)
    v.seat(-4, 12, 10, hidden=True)
    v.seat(4, 12, 10, hidden=True)
    v.seat(0, 12, -30, hidden=True)
    return v


# ============================================================================ UHV frigate
def frigate():
    st = Style(paint=(66, 50, 92), paint2=(40, 30, 58), wear=0.05, glow=(190, 80, 255), core=(245, 220, 255),
               scifi=True)
    v = V("AntimatterFrigate", "UHV", st, 6.0, 3.0)
    m = v.m
    # arrowhead hull in two layers
    lo = [(0, 86), (40, -50), (-40, -50)]
    m.hull([(x, 6, z) for x, z in [(0, 80), (34, -48), (-34, -48)]] + [(x, 15, z) for x, z in lo] +
           [(x, 21, z) for x, z in [(0, 74), (32, -46), (-32, -46)]], "paint")
    m.hull([(x, 21, z) for x, z in [(0, 54), (22, -44), (-22, -44)]] +
           [(x, 27, z) for x, z in [(0, 40), (17, -40), (-17, -40)]], "paint2")
    with m.mirrored():
        m.box(30, 31.2, 13.6, 15.4, -48, -22, "glow")  # edge lights
        for k in range(8):
            z = -40 + k * 13
            x = 40 * (86 - z) / 136 - 1.2
            m.box(x - 0.6, x + 0.4, 14.2, 14.8, z, z + 6, "glow")
    greebles(m, -14, 14, -36, 20, 27, 26, 9, mats=("paint", "dark", "armor"), hmax=2.2)
    # superstructure and bridge
    m.hull([(x, y, z) for x in (-9, 9) for y, z in ((27, -40), (27, -14), (40, -36), (40, -20))], "paint")
    m.box(-7, 7, 40, 44, -34, -22, "armor", 0.6)
    m.box(-6.4, 6.4, 41.2, 42.6, -21.9, -21.6, "glass")
    for x in (-5, 5):
        m.cyl((x, 44, -30), (x, 58, -30), 0.3, mat="steel", n=6)
    # antimatter containment: glowing core in a cage of rings
    m.sphere((0, 33, 8), 5.6, "core", 16, 10)
    for a in (0, 60, 120):
        with m.at((0, 33, 8), (0, a, 0)):
            m.torus((0, 0, 0), (1, 0, 0), 7.4, 0.7, "dark", 24, 6)
    m.torus((0, 33, 8), (0, 1, 0), 7.6, 0.9, "glow", 24, 6)
    m.cyl((0, 27, 8), (0, 28, 8), 7, mat="dark", n=18)
    # spinal antimatter cannon
    m.cyl((0, 10, 70), (0, 10, 98), 2.6, mat="paint2", n=14)
    for k in range(5):
        m.torus((0, 10, 74 + k * 5), (0, 0, 1), 3.8, 0.8, "glow", 16, 6)
    m.cyl((0, 10, 98), (0, 10, 98.6), 1.8, mat="core", n=12)
    v.mount(0, 10, 98.6)
    for x in (-27, -10, 10, 27):
        engine(v, x, 13, -48, 5.6 if abs(x) < 20 else 4.6, 9)
    m.prism([(-40, 6), (30, 6), (10, -1), (-34, -1)], -1.5, 1.5, "paint2", 0.3)  # keel
    v.turret((-21, 21, -12), 1, turret_dome(4.6, 2.6, "paint2"), laser_gun(18, 1.0, twin=True), gun_at=(0, 3.4, 0),
             length=18)
    v.turret((21, 21, -12), 2, turret_dome(4.6, 2.6, "paint2"), laser_gun(18, 1.0, twin=True), gun_at=(0, 3.4, 0),
             length=18)
    v.seat(0, 34, -28, hidden=True)
    for x, z in ((-6, 10), (6, 10), (-6, -10), (6, -10), (0, 30)):
        v.seat(x, 12, z, hidden=True)
    return v


# ============================================================================ UEV ringship
def ring_ship():
    st = Style(paint=(46, 54, 108), paint2=(28, 32, 64), wear=0.05, glow=(80, 110, 255), core=(215, 225, 255),
               scifi=True)
    v = V("QuantumRingship", "UEV", st, 6.0, 2.6)
    m = v.m
    y0 = 16
    m.body([(-34, 0, y0, 3, 3, 2.4), (-28, 0, y0, 8, 7, 2.6), (10, 0, y0, 10, 8.6, 2.8), (24, 0, y0, 8, 7, 2.6),
            (32, 0, y0, 4, 3.6, 2.2), (35, 0, y0, 1, 1, 2)], "paint", n=24)
    m.dome((0, y0 + 6.6, 14), 5, 3.6, 8, "glass", n=16, m=5)
    # the ring: armoured outer torus, glowing inner lining, four spokes
    m.torus((0, y0, 0), (0, 1, 0), 42, 3.8, "paint2", 64, 10, rr=4.6)
    m.torus((0, y0, 0), (0, 1, 0), 38.6, 1.4, "glow", 64, 6)
    for k in range(16):
        a = 2 * math.pi * (k + 0.5) / 16
        with m.at((0, y0, 0), (0, math.degrees(a), 0)):
            m.box(-2.2, 2.2, -3.4, 3.4, 44.6, 46.8, "armor", 0.4)
            m.box(-1.2, 1.2, -1.6, 1.6, 46.8, 47.2, "glow")
    for a in (45, 135, 225, 315):
        with m.at((0, y0, 0), (0, a, 0)):
            m.hull([(-1.4, -1.6, 7), (1.4, -1.6, 7), (-1.4, 1.6, 7), (1.4, 1.6, 7), (-1, -1, 39),
                    (1, -1, 39), (-1, 1, 39), (1, 1, 39)], "paint")
            m.box(-0.4, 0.4, 1.6, 2.0, 10, 38, "glow")
    # quantum core under a cage
    m.sphere((0, y0 + 10.5, -12), 3.6, "core", 14, 8)
    for a in (0, 90):
        with m.at((0, y0 + 10.5, -12), (0, a, 0)):
            m.torus((0, 0, 0), (1, 0, 0), 4.8, 0.45, "dark", 18, 5)
    m.cyl((0, y0 + 6, -12), (0, y0 + 7.4, -12), 4, mat="dark", n=14)
    engine(v, 0, y0, -34, 3.6, 6)
    with m.mirrored():
        engine(v, 7, y0 - 2, -26, 2.4, 5)
    m.cyl((0, y0, 35), (0, y0, 36), 0.8, mat="core", n=10)
    v.mount(0, y0, 36)
    v.pad(0, 7, 0, 9)
    m.cyl((0, 7.2, 0), (0, 8.4, 0), 6, mat="glow", n=20)
    v.turret((0, 7.4, 8), 1, None, laser_gun(14, 0.9), gun_at=(0, -1.6, 0), length=14, min_pitch=-10, max_pitch=80)
    m.sphere((0, 8.6, 8), 3.4, "dark", 14, 8)
    v.seat(0, y0 - 2, 12, hidden=True)
    v.seat(-4, y0 - 4, 0, hidden=True)
    v.seat(4, y0 - 4, 0, hidden=True)
    v.seat(0, y0 - 4, -12, hidden=True)
    return v


# ============================================================================ UIV gravity tank
def gravity_tank():
    st = Style(paint=(40, 58, 66), paint2=(26, 38, 44), wear=0.1, glow=(40, 230, 200), core=(205, 255, 245),
               scifi=True)
    v = V("GravityTank", "UIV", st, 4.4, 2.4)
    m = v.m
    pts = []
    for s in (-1, 1):
        pts += [(s * 24, 8, -46), (s * 24, 8, 40), (s * 18, 6, 52), (s * 22, 20, -48), (s * 22, 20, 30),
                (s * 16, 18, 52), (s * 14, 24, -46), (s * 14, 24, 22)]
    m.hull(pts, "paint")
    m.hull([(x, y, z) for x in (-12, 12) for y, z in ((24, -44), (24, 20), (25.4, -42), (25.4, 18))], "armor")
    with m.mirrored():
        m.box(23.4, 24.4, 12, 14, -42, 36, "glow")
        for z0, z1 in ((-44, -26), (22, 40)):  # graviton pods
            m.body([(z0, 28, 9, 3, 3, 2.4), (z0 + 3, 28, 9, 6, 6, 2.6), (z1 - 3, 28, 9, 6, 6, 2.6),
                    (z1, 28, 9, 3, 3, 2.4)], "paint2", n=18)
            for k in range(3):
                zz = z0 + 4 + k * (z1 - z0 - 8) / 2
                m.torus((28, 9, zz), (0, 0, 1), 6.2, 0.5, "glow", 18, 5)
            m.cyl((28, 2.6, (z0 + z1) / 2), (28, 3.4, (z0 + z1) / 2), 4.4, mat="glow", n=16)
            v.pad(28, 2.4, (z0 + z1) / 2, 5)
            m.hull([(23, 8, z0 + 4), (23, 8, z1 - 4), (23, 13, z0 + 5), (23, 13, z1 - 5), (26, 9, z0 + 5),
                    (26, 9, z1 - 5), (26, 12, z0 + 6), (26, 12, z1 - 6)], "paint")
        m.box(10, 16, 17.0, 17.6, 50.8, 51.6, "light")
        v.taillight(16, 18, -47.6, 4, 1)
    engine(v, 0, 14, -46, 4.4, 6)
    greebles(m, -12, 12, -42, -20, 25.4, 10, 5, hmax=1.4)
    v.seat(0, 13, 36, hidden=True)
    v.seat(0, 26, -6)
    v.seat(-8, 13, -14, hidden=True)
    v.seat(8, 13, -14, hidden=True)

    def turret(mm):
        foot = [(-6, 22), (6, 22), (17, 10), (18, -14), (13, -24), (-13, -24), (-18, -14), (-17, 10)]
        top = [(-4, 17), (4, 17), (13, 8), (14, -12), (10, -21), (-10, -21), (-14, -12), (-13, 8)]
        mm.hull([(x, 0, z) for x, z in foot] + [(x, 10, z) for x, z in top], "paint")
        mm.slab([(-12, 8), (12, 8), (13, -12), (9.5, -20), (-9.5, -20), (-13, -12)], 10, 11, "armor", 0.4)
        with mm.mirrored():
            mm.box(16.6, 17.6, 3, 5, -12, 8, "glow")
            mm.cyl((14, 11, -16), (14, 30, -16), 0.18, 0.08, mat="black", n=6)
        mm.box(6, 12, 11, 15, 2, 8, "paint2", 0.5)
        mm.box(6.6, 11.4, 11.8, 14.2, 8, 8.3, "glass")
    v.turret((0, 25.4, -4), 0, turret, beam_cannon(48, 2.6, 5, fork=True), gun_at=(0, 5.6, 20), length=50,
             min_pitch=-25, max_pitch=10)
    return v


# ============================================================================ UMV skimmer
def skimmer():
    st = Style(paint=(36, 64, 48), paint2=(22, 40, 30), wear=0.05, glow=(90, 255, 110), core=(225, 255, 225),
               scifi=True)
    v = V("SpacetimeSkimmer", "UMV", st, 5.0, 2.4)
    m = v.m
    y0 = 15
    m.body([(78, 0, y0, 0.3, 0.3, 2), (66, 0, y0, 3, 2.4, 2.2), (40, 0, y0 + 0.5, 6, 4.4, 2.4),
            (0, 0, y0 + 1, 8, 5.6, 2.6), (-40, 0, y0 + 0.5, 7, 5, 2.6), (-52, 0, y0, 5, 4, 2.4)], "paint", n=22)
    m.dome((0, y0 + 4.4, 26), 4, 3.4, 13, "glass", n=16, m=5)
    with m.mirrored():
        # forward-swept struts to the outrigger hulls
        m.hull([(6, y0 - 1, -24), (6, y0 + 1, -24), (6, y0 - 1, -6), (6, y0 + 1, -8), (24, y0 - 1.6, -10),
                (24, y0 + 0.4, -10), (24, y0 - 1.6, -2), (24, y0 + 0.4, -3)], "paint2")
        m.body([(-48, 26, y0 - 1, 2, 2, 2.4), (-42, 26, y0 - 1, 4, 4, 2.6), (24, 26, y0 - 1, 4, 4, 2.6),
                (40, 26, y0 - 1, 0.5, 0.5, 2)], "paint", n=16)
        m.box(29.8, 30.4, y0 - 2, y0, -40, 24, "glow")
        engine(v, 26, y0 - 1, -48, 3.2, 6)
    # tilted spacetime ring around the waist
    with m.at((0, y0, -6), (0, 0, 18)):
        m.torus((0, 0, 0), (0, 0, 1), 19, 2.2, "paint2", 48, 8, rr=2.8)
        m.torus((0, 0, 0), (0, 0, 1), 16.6, 0.8, "glow", 48, 6)
        for a in range(0, 360, 30):
            with m.at((0, 0, 0), (0, 0, a)):
                m.box(-1, 1, 18, 22, -3.2, 3.2, "armor", 0.3)
    engine(v, 0, y0, -52, 4.4, 7)
    m.cyl((0, y0, 78), (0, y0, 79), 0.6, mat="core", n=8)
    v.mount(0, y0, 79)
    v.turret((0, y0 + 5.6, -22), 1, turret_dome(3.4, 2.2, "paint2"), laser_gun(14, 0.8), gun_at=(0, 2.8, 0),
             length=14)
    v.seat(0, y0 - 2, 26, hidden=True)
    v.seat(0, y0 - 2, 12, hidden=True)
    v.seat(-3, y0 - 3, -10, hidden=True)
    v.seat(3, y0 - 3, -10, hidden=True)
    return v


# ============================================================================ UXV leviathan
def leviathan():
    st = Style(paint=(40, 26, 58), paint2=(30, 16, 46), wear=0.05, glow=(220, 80, 255), core=(250, 225, 255),
               scifi=True)
    v = V("DimensionalLeviathan", "UXV", st, 8.0, 3.6)
    m = v.m
    m.hull([(x, y, z) for x, y, z in [(0, 6, 70), (44, 6, -56), (-44, 6, -56), (0, 18, 84), (52, 16, -60),
                                       (-52, 16, -60), (0, 24, 66), (40, 24, -56), (-40, 24, -56)]], "paint")
    m.hull([(x, y, z) for x, y, z in [(0, 24, 40), (28, 24, -52), (-28, 24, -52), (0, 31, 26), (20, 31, -48),
                                       (-20, 31, -48)]], "cosmic")
    m.hull([(x, y, z) for x in (-12, 12) for y, z in ((31, -50), (31, -14), (44, -44), (44, -22))], "paint")
    m.box(-10, 10, 44, 48, -42, -24, "paint2", 0.8)
    m.box(-9, 9, 45.4, 46.8, -23.9, -23.6, "glass")
    # forked rift emitter with the rift between the prongs
    with m.mirrored():
        m.hull([(4, 10, 60), (4, 22, 60), (12, 10, 56), (12, 22, 56), (6, 13, 106), (6, 18, 106), (10, 14, 104),
                (10, 17, 104)], "paint2")
        m.box(5.6, 6.2, 14, 17, 70, 102, "glow")
        m.box(48, 49.4, 14.6, 16.6, -58, -20, "glow")
    m.box(-5, 5, 15, 16, 66, 104, "core")
    v.mount(0, 15.5, 104)
    greebles(m, -24, 24, -46, 10, 31, 30, 11, mats=("paint", "paint2", "dark"), hmax=2.6)
    m.prism([(-54, 6), (40, 6), (14, -3), (-46, -3)], -2, 2, "paint2", 0.4)  # ventral keel
    for x in (-30, -15, 0, 15, 30):
        engine(v, x, 14, -58, 5.6 if x else 7, 10)
    for x in (-8, 8):
        m.cyl((x, 48, -36), (x, 64, -36), 0.4, mat="steel", n=6)
    v.turret((-30, 20, -20), 1, turret_dome(5, 3, "cosmic"), laser_gun(20, 1.1, twin=True), gun_at=(0, 3.8, 0),
             length=20)
    v.turret((30, 20, -20), 2, turret_dome(5, 3, "cosmic"), laser_gun(20, 1.1, twin=True), gun_at=(0, 3.8, 0),
             length=20)
    v.turret((0, 31, 12), 3, turret_dome(5, 3, "cosmic"), laser_gun(20, 1.1, twin=True), gun_at=(0, 3.8, 0),
             length=20)
    v.seat(0, 38, -32, hidden=True)
    for x, z in ((-8, 20), (8, 20), (-8, 0), (8, 0), (0, -30)):
        v.seat(x, 12, z, hidden=True)
    return v


# ============================================================================ MAX ark
def ark():
    st = Style(paint=(238, 232, 216), paint2=(196, 168, 108), wear=0.0, glow=(255, 205, 70), core=(255, 250, 225),
               scifi=True)
    v = V("GenesisArk", "MAX", st, 8.0, 4.0)
    m = v.m
    y0 = 20
    m.body([(-64, 0, y0, 10, 4, 2.4), (-56, 0, y0, 26, 7, 2.6), (-20, 0, y0 + 0.5, 46, 9, 2.4),
            (20, 0, y0 + 0.5, 34, 8.4, 2.3), (52, 0, y0, 14, 6, 2.2), (70, 0, y0, 3, 2.4, 2)], "paint", n=32)
    m.body([(-50, 0, y0 + 6, 12, 5, 2.4), (-10, 0, y0 + 7, 14, 6.4, 2.6), (30, 0, y0 + 6, 8, 4, 2.4),
            (44, 0, y0 + 5, 2, 1.2, 2)], "paint", n=20)
    m.dome((0, y0 + 11.4, 22), 5, 3, 10, "glass", n=16, m=5)
    with m.mirrored():
        for k, x in enumerate((14, 30)):
            m.box(x - 0.4, x + 0.4, y0 + 8.4, y0 + 9.6, -40 + k * 8, 18 - k * 14, "gold")
    # halo rings
    for z, R, tilt in ((-30, 50, 0), (10, 44, 0)):
        with m.at((0, y0 + 2, z), (0, 0, 0)):
            m.torus((0, 0, 0), (0, 0, 1), R, 2.6, "paint", 72, 8, rr=3.6)
            m.torus((0, 0, 0), (0, 0, 1), R - 3.2, 0.9, "glow", 72, 6)
            for a in range(0, 360, 20):
                with m.at((0, 0, 0), (0, 0, a)):
                    m.box(-1.2, 1.2, R + 2, R + 4.2, -1.6, 1.6, "gold", 0.3)
    with m.mirrored():
        for z, R in ((-30, 50), (10, 44)):
            m.hull([(10, y0, z - 2), (10, y0, z + 2), (10, y0 + 3, z - 2), (10, y0 + 3, z + 2), (R - 3, y0 + 1, z - 1.4),
                    (R - 3, y0 + 1, z + 1.4), (R - 3, y0 + 3, z - 1.4), (R - 3, y0 + 3, z + 1.4)], "paint2")
    m.sphere((0, y0 + 2, -10), 6, "core", 18, 10)
    m.torus((0, y0 + 2, -10), (0, 1, 0), 7.6, 1.0, "gold", 24, 6)
    for x in (-16, 0, 16):
        engine(v, x, y0, -62, 5.4 if x else 6.6, 8, "gold")
    m.cyl((0, y0, 70), (0, y0, 71), 2, mat="core", n=12)
    v.mount(0, y0, 71)
    v.turret((-28, y0 + 8, -22), 1, turret_dome(4.4, 2.8, "paint", "gold"), beam_cannon(18, 1.2, 3),
             gun_at=(0, 3.6, 0), length=18)
    v.turret((28, y0 + 8, -22), 2, turret_dome(4.4, 2.8, "paint", "gold"), beam_cannon(18, 1.2, 3),
             gun_at=(0, 3.6, 0), length=18)
    v.seat(0, y0 + 4, 20, hidden=True)
    v.seat(-8, y0 - 4, 0, hidden=True)
    v.seat(8, y0 - 4, 0, hidden=True)
    v.seat(0, y0 - 4, -30, hidden=True)
    return v


DESIGNS = {f.__name__: f for f in (jeep, truck, apc, tank, hover, vtol, fighter, cruiser, frigate, ring_ship,
                                   gravity_tank, skimmer, leviathan, ark)}
NAMES = {}


def build(fn):
    v = fn()
    key = v.name.lower()
    tris, size = export(v.m, v.style, ASSETS / "models/vehicle" / f"vehicle_{key}.obj",
                        ASSETS / "textures/models" / f"vehicle_{key}.png")
    lay = ASSETS / "vehicles" / f"vehicle_{key}.json"
    lay.write_text(json.dumps(v.layout, indent=1) + "\n")
    print(f"{v.name}: {tris} triangles, atlas {size[0]}x{size[1]}, groups {sorted(v.m.groups)[:6]}...")
    return v


def main(argv):
    for f in DESIGNS.values():
        if argv and f().name not in argv:
            continue
        build(f)


if __name__ == "__main__":
    main(sys.argv[1:])
