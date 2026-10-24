"""Models and layouts of the LV-MAX vehicles.

For every vehicle this writes
  models/vehicle/vehicle_<name>.geo.json        hull
  models/vehicle/vehicle_<name>_t<i>.geo.json   turret i (turns with its gunner's yaw), optional
  models/vehicle/vehicle_<name>_g<i>.geo.json   gun i (turns with yaw and pitch), optional
  textures/models/vehicle_<name>*.png
  vehicles/vehicle_<name>.json                  layout read by the game on both sides (seats, turrets, rotors...)
  textures/items/vehicle_<name>.png             32x32 item icon

Model space is Minecraft Java model space: Y points down (negative Y is up), the nose points towards -Z, and y = 0
is the ground under the wheels. One model unit is 1/16 block divided by the vehicle's scale.

usage: vehicle_designs.py [--preview DIR] [NAME ...]
"""
import json
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import gunforge  # noqa: E402
from gunforge import Gun, ASSETS  # noqa: E402

gunforge.TIERS.setdefault("LV", {"glow": (255, 210, 120), "core": (255, 245, 220), "metal": (150, 155, 165)})
gunforge.TIERS.setdefault("MV", {"glow": (255, 140, 60), "core": (255, 220, 190), "metal": (190, 200, 210)})
gunforge.TIERS.setdefault("HV", {"glow": (255, 220, 60), "core": (255, 245, 200), "metal": (200, 205, 215)})

CUSTOM = {
    "olive": (92, 102, 64), "sand": (178, 160, 114), "drab": (74, 78, 60), "tire": (30, 30, 33),
    "navy": (44, 58, 92), "white2": (214, 220, 230), "gunm": (62, 66, 76),
}


class VGun(Gun):
    """Gun with a few vehicle paints on top of the weapon materials."""

    def __init__(self, name, tier, w=512, h=512, paint=None):
        super().__init__(name, tier, tex_w=w, tex_h=h, subdir="vehicle")
        self.paint = paint

    def _paint(self, px, x0, y0, w, h, mat):
        if mat == "paint":
            mat, col = "x", self.paint
        elif mat in CUSTOM:
            col = CUSTOM[mat]
        else:
            return super()._paint(px, x0, y0, w, h, mat)
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w):
                lx, ly = x - x0, y - y0
                n = self.rnd.uniform(-6, 6)
                c = list(col)
                if mat == "tire" and (lx + ly) % 3 == 0:
                    c = [v + 14 for v in c]
                if mat == "sand" and self.rnd.random() < 0.08:
                    c = [v * 0.85 for v in c]
                if lx == 0 or ly == 0:
                    c = [v * 1.18 + 5 for v in c]
                elif lx == w - 1 or ly == h - 1:
                    c = [v * 0.72 for v in c]
                px[x, y] = tuple(max(0, min(255, int(v + n))) for v in c) + (255,)


class V:
    """A vehicle being designed: hull, turrets and the layout the game reads."""

    def __init__(self, name, tier, scale, width, height, paint=(92, 102, 64)):
        self.name, self.tier, self.scale = name, tier, scale
        self.hull = VGun("vehicle_" + name, tier, paint=paint)
        self.paint = paint
        self.layout = {"scale": scale, "width": width, "height": height, "seats": [], "turrets": [], "mounts": [],
                       "rotors": [], "pads": [], "exhausts": []}
        self.parts = []

    def box(self, *a):
        self.hull.box(*a)

    def mirror(self, x0, y0, z0, x1, y1, z1, mat):
        self.hull.box(x0, y0, z0, x1, y1, z1, mat)
        self.hull.box(-x1, y0, z0, -x0, y1, z1, mat)

    def seat(self, x, y, z, hidden=False):
        self.layout["seats"].append({"x": x, "y": y, "z": z, "hidden": hidden})

    def turret(self, x, y, z, seat, turret_fn, gun_fn, gun_at=(0, 0, 0), length=10, min_pitch=-60, max_pitch=30):
        i = len(self.layout["turrets"])
        t = {"x": x, "y": y, "z": z, "seat": seat, "gx": gun_at[0], "gy": gun_at[1], "gz": gun_at[2], "length": length,
             "minPitch": min_pitch, "maxPitch": max_pitch, "turret": turret_fn is not None, "gun": gun_fn is not None}
        if turret_fn:
            g = VGun(f"vehicle_{self.name}_t{i}", self.tier, 128, 128, self.paint)
            turret_fn(g)
            self.parts.append(g)
        if gun_fn:
            g = VGun(f"vehicle_{self.name}_g{i}", self.tier, 128, 128, self.paint)
            gun_fn(g)
            self.parts.append(g)
        self.layout["turrets"].append(t)

    def mount(self, x, y, z):
        """Fixed weapon mount firing along the vehicle's nose."""
        self.layout["mounts"].append({"x": x, "y": y, "z": z})

    def rotor(self, x, y, z, r):
        self.layout["rotors"].append({"x": x, "y": y, "z": z, "r": r})

    def pad(self, x, y, z, r):
        """Hover emitter: a glowing disc drawn under the vehicle."""
        self.layout["pads"].append({"x": x, "y": y, "z": z, "r": r})

    def exhaust(self, x, y, z, r):
        """Engine nozzle facing backwards: a flame drawn when the vehicle moves."""
        self.layout["exhausts"].append({"x": x, "y": y, "z": z, "r": r})

    # ---------------- reusable parts ----------------
    def wheel(self, x, z, r, w, mat="tire"):
        for sx in (x, -x):
            self.box(sx - w / 2, -2 * r, z - r, sx + w / 2, 0, z + r, mat)
            o = sx + (w / 2 if sx > 0 else -w / 2 - 1)
            self.box(o, -r - r / 2, z - r / 2, o + 1, -r + r / 2, z + r / 2, "steel")

    def track(self, x_in, x_out, z0, z1, h):
        for a, b in ((x_in, x_out), (-x_out, -x_in)):
            self.box(a, -h, z0, b, 0, z1, "tire")
            for z in range(int(z0) + 3, int(z1) - 2, 6):
                self.box(a - 1 if a < 0 else b, -h + 2, z, a if a < 0 else b + 1, -2, z + 4, "steel")

    def lights(self, x, y, z, w=2, h=2):
        self.mirror(x - w / 2, y - h / 2, z - 1, x + w / 2, y + h / 2, z, "glow")


# ======================================================================= gun parts
def mg_gun(len_=14, mat="dark"):
    def f(g):
        g.box(-1, -1, -6, 1, 1, 3, mat)
        g.box(-0.5, -0.5, -len_, 0.5, 0.5, -6, "metal")
        g.box(1, -1, -3, 3, 2, 0, "drab")
    return f


def pintle(h=3):
    def f(g):
        g.box(-1, -h, -1, 1, 0, 1, "steel")
        g.box(-3, -h - 1, -1, 3, -h, 1, "dark")
    return f


def cannon(len_, bore=1, mantlet=3, mat="metal"):
    def f(g):
        g.box(-mantlet, -mantlet + 0.5, -2, mantlet, mantlet - 0.5, 1, "paint")
        g.box(-bore, -bore, -len_, bore, bore, -2, mat)
        g.box(-bore - 0.5, -bore - 0.5, -len_ - 2, bore + 0.5, bore + 0.5, -len_, "dark")
    return f


def beam_gun(len_, w=2, rings=3):
    def f(g):
        g.box(-w, -w, -len_ + 2, w, w, 2, "armor")
        i = max(0.5, w - 1)
        g.box(-i, -i, -len_, i, i, -len_ + 2, "lens")
        for k in range(rings):
            z = -len_ + 4 + k * (len_ - 6) // max(1, rings)
            g.box(-w - 1, -w - 1, z, w + 1, w + 1, z + 1, "ring")
    return f


def dome(r, h, mat="paint", top="armor"):
    def f(g):
        g.box(-r, -h, -r, r, 0, r, mat)
        g.box(-r + 1, -h - 1, -r + 1, r - 1, -h, r - 1, top)
    return f


# ======================================================================= designs
def jeep():
    v = V("FieldJeep", "LV", 2.0, 2.2, 2.0, paint=(96, 104, 66))
    v.wheel(8, -9, 3.5, 3)
    v.wheel(8, 9, 3.5, 3)
    v.box(-7, -6, -14, 7, -4, 14, "dark")
    v.box(-7, -10, -14, 7, -6, 14, "paint")
    v.box(-6, -11, -14, 6, -10, -4, "paint")
    v.box(-5, -10, -15, 5, -6, -14, "dark")
    v.lights(4.5, -8.5, -14, 2, 2)
    v.box(-7, -16, -4, 7, -11, -3, "lens")
    v.box(-7, -18, 4, -6, -10, 5, "steel")
    v.box(6, -18, 4, 7, -10, 5, "steel")
    v.box(-7, -19, 4, 7, -18, 5, "steel")
    v.box(-5, -13, -2, -1, -10, 2, "grip")
    v.box(1, -13, -2, 5, -10, 2, "grip")
    v.box(-5, -12, 7, 5, -10, 12, "grip")
    v.box(-3, -12, 14, 3, -6, 16, "tire")
    v.mirror(7, -11, -10, 8, -10, -6, "paint")  # fenders
    v.mirror(7, -11, 6, 8, -10, 12, "paint")
    v.seat(-3, -10, 0)
    v.seat(0, -12, 9)
    v.seat(3, -10, 0)
    v.turret(0, -19, 4.5, 1, pintle(3), mg_gun(14), gun_at=(0, -4, 0), length=14)
    return v


def truck():
    v = V("OffroadTruck", "MV", 2.0, 2.6, 2.8, paint=(110, 112, 72))
    for z in (-13, 5, 13):
        v.wheel(9, z, 4, 3.5)
    v.box(-8, -8, -20, 8, -5, 20, "dark")
    v.box(-9, -20, -20, 9, -8, -8, "paint")
    v.box(-8, -19, -21, 8, -14, -20, "lens")
    v.box(-7, -12, -21, 7, -8, -20, "dark")
    v.lights(6, -10, -20, 2, 2)
    v.box(-9, -14, -7, 9, -8, 20, "paint")
    v.box(-9, -16, -7, -8, -14, 20, "paint")
    v.box(8, -16, -7, 9, -14, 20, "paint")
    v.box(-8, -22, -4, 8, -14, 14, "sand")
    for z in (-3, 4, 11):
        v.box(-8.5, -23, z, 8.5, -22, z + 1, "drab")
    v.box(-2, -22, -16, 2, -20, -12, "steel")
    v.seat(-4, -12, -14, hidden=True)
    v.seat(4, -12, -14, hidden=True)
    v.seat(0, -22, -14)
    v.seat(0, -14, 16)
    v.turret(0, -22, -14, 2, pintle(2), mg_gun(16), gun_at=(0, -3, 0), length=16)
    return v


def apc():
    v = V("ArmoredAPC", "HV", 2.0, 2.8, 2.4, paint=(120, 116, 82))
    for z in (-15, -5, 5, 15):
        v.wheel(10, z, 3.5, 3)
    v.box(-9, -7, -21, 9, -4, 21, "dark")
    v.box(-10, -16, -18, 10, -7, 21, "paint")
    v.box(-10, -13, -22, 10, -7, -18, "paint")  # sloped front, two steps
    v.box(-9, -15, -20, 9, -13, -18, "paint")
    v.box(-4, -15, -21, 4, -13, -20, "lens")
    v.lights(7, -10, -22, 2, 2)
    v.mirror(10, -14, -16, 11, -8, 18, "drab")  # side armour
    v.box(-6, -15, 21, 6, -8, 22, "drab")  # rear door
    v.box(-3, -17, 0, 3, -16, 6, "steel")
    for z in (-10, 8):
        v.mirror(6, -18, z, 8, -16, z + 3, "dark")  # smoke launchers
    v.seat(-4, -10, -14, hidden=True)
    v.seat(0, -10, -4, hidden=True)
    v.seat(-4, -10, 10, hidden=True)
    v.seat(4, -10, 10, hidden=True)
    v.turret(0, -16, -2, 1, dome(4, 4, "paint", "drab"), mg_gun(20, "gunm"), gun_at=(0, -3, -2), length=20)
    return v


def tank():
    v = V("BattleTank", "EV", 2.0, 3.4, 2.6, paint=(92, 98, 66))
    v.track(10, 14, -26, 26, 8)
    v.box(-10, -10, -26, 10, -4, 26, "paint")
    v.box(-14, -12, -24, 14, -10, 26, "paint")  # fenders over the tracks
    v.box(-10, -14, -20, 10, -12, 24, "paint")
    v.box(-9, -12, -28, 9, -9, -26, "paint")  # glacis
    v.lights(11, -11, -24, 2, 1)
    v.box(-6, -16, 16, 6, -14, 24, "vent")  # engine deck
    v.box(-12, -14, 18, -10, -12, 26, "dark")
    v.seat(0, -10, -18, hidden=True)
    v.seat(0, -22, 4)

    def turret(g):
        g.box(-9, -8, -8, 9, 0, 10, "paint")
        g.box(-7, -9, -6, 7, -8, 8, "drab")
        g.box(-3, -11, 2, 3, -9, 6, "steel")  # commander hatch
        g.box(-10, -6, 4, -9, -2, 10, "dark")  # stowage
        g.box(9, -6, 4, 10, -2, 10, "dark")
        g.box(5, -11, -2, 6, -8, -1, "steel")  # antenna base
        g.box(5.25, -20, -1.75, 5.75, -11, -1.25, "steel")
    v.turret(0, -14, 2, 0, turret, cannon(28, 1, 3), gun_at=(0, -4, -8), length=30, min_pitch=-25, max_pitch=10)
    return v


def hover():
    v = V("HoverJeep", "IV", 2.0, 2.6, 1.6, paint=(40, 64, 120))
    v.box(-8, -8, -18, 8, -3, 18, "paint")
    v.box(-6, -10, -14, 6, -8, 14, "armor")
    v.box(-7, -7, -21, 7, -3, -18, "paint")
    v.box(-5, -13, -8, 5, -10, 4, "lens")  # canopy
    v.mirror(8, -6, -14, 10, -3, 14, "dark")
    v.mirror(8, -5, -12, 9, -4, 12, "glow")
    v.lights(5, -5, -21, 2, 1)
    v.box(-4, -9, 18, 4, -5, 20, "glow")
    for x, z in ((6, -12), (-6, -12), (6, 12), (-6, 12)):
        v.pad(x, -2, z, 4)
        v.box(x - 2, -3, z - 2, x + 2, -2, z + 2, "glow")
    v.seat(-3, -8, -4, hidden=False)
    v.seat(3, -8, -4)
    v.seat(0, -9, 8)
    v.turret(0, -10, 8, 0, dome(3, 2, "armor", "metal"), beam_gun(22, 1, 3), gun_at=(0, -2, -1), length=22,
             min_pitch=-30, max_pitch=15)
    v.exhaust(0, -7, 20, 3)
    return v


def vtol():
    v = V("TiltrotorVTOL", "LuV", 2.5, 4.0, 2.4, paint=(220, 224, 232))
    v.box(-7, -14, -20, 7, -4, 18, "paint")
    v.box(-5, -12, -26, 5, -5, -20, "paint")
    v.box(-4, -13, -26, 4, -10, -16, "lens")
    v.box(-3, -12, 18, 3, -9, 34, "paint")  # tail boom
    v.box(-12, -14, 30, 12, -13, 34, "paint")  # tail plane
    v.box(-1, -22, 30, 1, -12, 34, "paint")
    v.box(-28, -14, -4, 28, -12, 4, "paint")  # wing
    for x in (-27, 27):
        v.box(x - 3, -17, -8, x + 3, -10, 8, "armor")  # nacelles
        v.box(x - 1, -20, -1, x + 1, -17, 1, "steel")
        v.rotor(x, -20.5, 0, 14)
    v.mirror(6, -13, -14, 7, -6, 6, "glow")
    v.mirror(12, -12, -6, 14, -10, 2, "dark")  # rocket pods
    v.mount(13, -11, -7)
    v.mount(-13, -11, -7)
    v.box(-4, -4, -16, 4, 0, -12, "dark")  # nose gear
    v.box(-6, -4, 8, -4, 0, 12, "dark")
    v.box(4, -4, 8, 6, 0, 12, "dark")
    v.seat(0, -9, -18, hidden=True)
    v.seat(0, -9, -10, hidden=True)
    v.seat(-3, -7, 4, hidden=True)
    v.seat(3, -7, 4, hidden=True)
    v.turret(0, -4, -20, 1, dome(2, 2, "dark", "metal"), beam_gun(12, 1, 2), gun_at=(0, 1, 0), length=12,
             min_pitch=-10, max_pitch=70)
    return v


def fighter():
    v = V("PlasmaFighter", "ZPM", 2.5, 4.4, 1.8, paint=(48, 44, 66))
    v.box(-5, -10, -30, 5, -4, 22, "paint")
    v.box(-3, -9, -36, 3, -5, -30, "paint")
    v.box(-3, -13, -22, 3, -10, -8, "lens")
    for k in range(6):  # swept delta wing, stepped
        z0 = -10 + k * 5
        span = 8 + k * 4
        v.mirror(5, -8, z0, span + 5, -6, z0 + 5, "paint")
    v.mirror(5, -8, 15, 30, -6, 18, "armor")
    v.mirror(29, -9, 6, 31, -5, 18, "glow")  # wingtip lights
    v.mirror(5, -15, 12, 6, -10, 22, "paint")  # twin fins
    v.mirror(3, -10, 22, 7, -4, 25, "metal")
    v.mirror(7, -9, 0, 9, -5, 22, "metal")  # engine nacelles
    v.exhaust(5, -7, 25, 3)
    v.exhaust(-5, -7, 25, 3)
    v.mirror(5, -11, -18, 6, -10, 10, "glow")
    v.mount(8, -6, -6)
    v.mount(-8, -6, -6)
    v.mirror(12, -6, -2, 14, -4, 6, "warn")  # missiles
    v.box(-1, -4, -20, 1, 0, -18, "dark")
    v.mirror(6, -4, 4, 7, 0, 6, "dark")
    v.seat(0, -9, -16, hidden=True)
    v.seat(0, -9, -10, hidden=True)
    return v


def cruiser():
    v = V("ParticleCruiser", "UV", 3.0, 5.0, 2.6, paint=(206, 210, 222))
    v.box(-6, -12, -32, 6, -4, 30, "paint")
    v.box(-4, -10, -40, 4, -6, -32, "armor")
    v.box(-2, -9, -44, 2, -7, -40, "lens")
    v.mirror(6, -10, -18, 16, -6, 24, "paint")  # side pods
    v.mirror(14, -11, -24, 18, -5, 26, "armor")
    v.mirror(16, -9, -20, 18, -7, 24, "glow")
    v.box(-4, -18, 4, 4, -12, 16, "paint")  # bridge
    v.box(-3, -17, 3, 3, -15, 4, "lens")
    for x in (-16, 0, 16):
        v.box(x - 3, -10, 26, x + 3, -5, 32, "metal")
        v.exhaust(x, -7.5, 32, 3)
    v.mirror(6, -13, -30, 7, -12, 28, "glow")
    v.mount(0, -8, -44)
    v.seat(0, -14, 8, hidden=True)
    v.seat(-3, -9, -10, hidden=True)
    v.seat(3, -9, -10, hidden=True)
    v.seat(0, -9, 18, hidden=True)
    v.turret(-11, -10, -6, 1, dome(3, 2, "armor", "metal"), beam_gun(14, 1, 2), gun_at=(0, -2, 0), length=14)
    v.turret(11, -10, -6, 2, dome(3, 2, "armor", "metal"), beam_gun(14, 1, 2), gun_at=(0, -2, 0), length=14)
    return v


def frigate():
    v = V("AntimatterFrigate", "UHV", 3.5, 6.0, 3.0, paint=(62, 44, 88))
    v.box(-8, -14, -36, 8, -4, 34, "paint")
    v.box(-5, -12, -46, 5, -6, -36, "cosmic")
    v.box(-3, -10, -50, 3, -8, -46, "glow")
    v.mirror(8, -12, -24, 22, -6, 20, "paint")
    v.mirror(20, -14, -28, 24, -4, 24, "armor")
    v.mirror(22, -10, -20, 24, -8, 18, "glow")
    v.box(-5, -20, 6, 5, -14, 20, "armor")
    v.box(-4, -19, 5, 4, -17, 6, "lens")
    v.box(-2, -26, 12, 2, -20, 16, "metal")
    for x in (-20, -8, 8, 20):
        v.box(x - 3, -11, 30, x + 3, -5, 36, "metal")
        v.exhaust(x, -8, 36, 3)
    v.mirror(8, -15, -30, 9, -14, 30, "glow")
    v.mount(0, -9, -50)
    v.seat(0, -16, 10, hidden=True)
    for x, z in ((-4, -14), (4, -14), (-4, 0), (4, 0), (0, 24)):
        v.seat(x, -10, z, hidden=True)
    v.turret(-16, -12, -10, 1, dome(3, 2, "armor", "cosmic"), beam_gun(16, 1, 3), gun_at=(0, -2, 0), length=16)
    v.turret(16, -12, -10, 2, dome(3, 2, "armor", "cosmic"), beam_gun(16, 1, 3), gun_at=(0, -2, 0), length=16)
    return v


def ring_ship():
    v = V("QuantumRingship", "UEV", 3.0, 6.0, 2.6, paint=(40, 46, 96))
    v.box(-6, -14, -12, 6, -4, 12, "paint")
    v.box(-4, -16, -10, 4, -14, 10, "armor")
    v.box(-3, -12, -18, 3, -6, -12, "lens")
    v.box(-3, -12, 12, 3, -6, 18, "metal")
    import math
    for k in range(16):  # ring around the core
        a = 2 * math.pi * k / 16
        x, z = 26 * math.cos(a), 26 * math.sin(a)
        v.box(x - 3.5, -11, z - 3.5, x + 3.5, -7, z + 3.5, "ring" if k % 2 else "cosmic")
    for a in (0.25, 1.25, 2.25, 3.25):  # spokes
        ang = math.pi * a / 2
        for r in range(8, 24, 3):
            x, z = r * math.cos(ang), r * math.sin(ang)
            v.box(x - 1, -10, z - 1, x + 1, -8, z + 1, "metal")
    v.pad(0, -3, 0, 8)
    v.mount(0, -9, -18)
    v.exhaust(0, -9, 18, 3)
    v.seat(0, -12, -4, hidden=True)
    v.seat(-3, -10, 4, hidden=True)
    v.seat(3, -10, 4, hidden=True)
    v.seat(0, -10, 8, hidden=True)
    v.turret(0, -4, 0, 1, dome(3, 2, "cosmic", "glow"), beam_gun(14, 1, 3), gun_at=(0, 2, 0), length=14,
             min_pitch=-10, max_pitch=80)
    return v


def gravity_tank():
    v = V("GravityTank", "UIV", 2.5, 4.4, 2.4, paint=(36, 52, 60))
    v.box(-12, -12, -28, 12, -5, 28, "paint")
    v.box(-10, -14, -24, 10, -12, 24, "armor")
    v.box(-8, -10, -32, 8, -6, -28, "paint")
    v.mirror(12, -11, -24, 15, -5, 24, "metal")
    v.mirror(14, -9, -20, 15, -7, 20, "glow")
    v.lights(8, -8, -32, 3, 1)
    for x, z in ((8, -18), (-8, -18), (8, 18), (-8, 18)):
        v.pad(x, -3, z, 5)
        v.box(x - 4, -5, z - 4, x + 4, -4, z + 4, "ring")
    v.seat(0, -12, -18, hidden=True)
    v.seat(0, -22, 4)
    v.seat(-6, -12, 12, hidden=True)
    v.seat(6, -12, 12, hidden=True)

    def turret(g):
        g.box(-9, -8, -9, 9, 0, 9, "paint")
        g.box(-7, -9, -7, 7, -8, 7, "armor")
        g.box(-10, -5, -4, -9, -3, 6, "glow")
        g.box(9, -5, -4, 10, -3, 6, "glow")
    v.turret(0, -14, 2, 0, turret, beam_gun(26, 2, 4), gun_at=(0, -4, -9), length=28, min_pitch=-25, max_pitch=10)
    v.exhaust(0, -9, 28, 4)
    return v


def skimmer():
    v = V("SpacetimeSkimmer", "UMV", 3.0, 5.0, 2.4, paint=(34, 58, 44))
    v.box(-5, -12, -40, 5, -5, 34, "paint")
    v.box(-3, -11, -46, 3, -6, -40, "armor")
    v.box(-3, -15, -24, 3, -12, -10, "lens")
    import math
    for k in range(20):  # oval spacetime ring around the waist
        a = 2 * math.pi * k / 20
        x, y = 20 * math.cos(a), -8.5 + 12 * math.sin(a)
        v.box(x - 2.5, y - 2.5, -4, x + 2.5, y + 2.5, 4, "ring" if k % 2 else "cosmic")
    v.mirror(5, -10, 10, 16, -7, 30, "paint")
    v.mirror(15, -12, 14, 17, -5, 32, "glow")
    v.mirror(3, -11, 34, 7, -6, 38, "metal")
    v.exhaust(5, -8.5, 38, 3)
    v.exhaust(-5, -8.5, 38, 3)
    v.mount(0, -8.5, -46)
    v.seat(0, -12, -18, hidden=True)
    v.seat(0, -10, -6, hidden=True)
    v.seat(-2, -10, 14, hidden=True)
    v.seat(2, -10, 14, hidden=True)
    v.turret(0, -12, 20, 1, dome(3, 2, "paint", "glow"), beam_gun(14, 1, 3), gun_at=(0, -2, 0), length=14)
    return v


def leviathan():
    v = V("DimensionalLeviathan", "UXV", 4.0, 8.0, 3.6, paint=(30, 18, 44))
    v.box(-9, -16, -40, 9, -4, 36, "paint")
    v.box(-6, -14, -50, 6, -6, -40, "cosmic")
    v.box(-2, -11, -56, 2, -9, -50, "glow")
    v.mirror(9, -14, -30, 26, -6, 24, "paint")
    v.mirror(24, -16, -34, 30, -4, 28, "cosmic")
    v.mirror(28, -12, -30, 30, -8, 24, "glow")
    v.box(-6, -24, 4, 6, -16, 22, "armor")
    v.box(-5, -23, 3, 5, -20, 4, "lens")
    v.mirror(9, -17, -36, 10, -16, 32, "glow")
    for x in (-22, -10, 0, 10, 22):
        v.box(x - 3, -12, 36, x + 3, -6, 42, "metal")
        v.exhaust(x, -9, 42, 3)
    v.mount(0, -10, -56)
    v.seat(0, -20, 10, hidden=True)
    for x, z in ((-5, -20), (5, -20), (-5, 0), (5, 0), (0, 28)):
        v.seat(x, -12, z, hidden=True)
    v.turret(-18, -14, -14, 1, dome(4, 2, "cosmic", "glow"), beam_gun(18, 1, 3), gun_at=(0, -2, 0), length=18)
    v.turret(18, -14, -14, 2, dome(4, 2, "cosmic", "glow"), beam_gun(18, 1, 3), gun_at=(0, -2, 0), length=18)
    v.turret(0, -24, 26, 3, dome(4, 2, "cosmic", "glow"), beam_gun(18, 1, 3), gun_at=(0, -2, 0), length=18)
    return v


def ark():
    v = V("GenesisArk", "MAX", 4.0, 8.0, 4.0, paint=(236, 230, 214))
    v.box(-8, -16, -36, 8, -4, 32, "paint")
    v.box(-5, -14, -46, 5, -6, -36, "armor")
    v.box(-2, -11, -50, 2, -9, -46, "glow")
    import math
    for ring, (rz, rad) in enumerate(((-12, 22), (12, 26))):
        for k in range(20):
            a = 2 * math.pi * k / 20
            x, y = rad * math.cos(a), -10 + rad * 0.7 * math.sin(a)
            v.box(x - 2.5, y - 2.5, rz - 3, x + 2.5, y + 2.5, rz + 3, "ring" if (k + ring) % 2 else "armor")
    v.box(-5, -22, 0, 5, -16, 16, "armor")
    v.box(-4, -21, -1, 4, -19, 0, "lens")
    v.mirror(8, -12, 16, 20, -8, 30, "paint")
    v.mirror(18, -14, 18, 22, -6, 32, "glow")
    for x in (-12, 0, 12):
        v.box(x - 3, -13, 32, x + 3, -7, 38, "metal")
        v.exhaust(x, -10, 38, 3)
    v.mount(0, -10, -50)
    v.seat(0, -18, 6, hidden=True)
    v.seat(-4, -10, -20, hidden=True)
    v.seat(4, -10, -20, hidden=True)
    v.seat(0, -10, 22, hidden=True)
    v.turret(-14, -16, 24, 1, dome(3, 2, "armor", "glow"), beam_gun(16, 1, 3), gun_at=(0, -2, 0), length=16)
    v.turret(14, -16, 24, 2, dome(3, 2, "armor", "glow"), beam_gun(16, 1, 3), gun_at=(0, -2, 0), length=16)
    return v


DESIGNS = {f().name: f for f in (jeep, truck, apc, tank, hover, vtol, fighter, cruiser, frigate, ring_ship,
                                 gravity_tank, skimmer, leviathan, ark)}


def icon(model, tex, out):
    """32x32 item icon rendered from the hull model."""
    sys.path.insert(0, str(Path(__file__).resolve().parent))
    import render_geo
    from PIL import Image
    roots, tw, th = render_geo.load(model)
    t = Image.open(tex).convert("RGBA")
    quads = []
    for r in roots:
        render_geo.collect(r, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0, 0, 0], quads)
    img = render_geo.render(quads, t, "three_quarter", size=400, transparent=True)
    bbox = img.getbbox()
    img = img.crop(bbox)
    side = max(img.width, img.height)
    sq = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    sq.paste(img, ((side - img.width) // 2, (side - img.height) // 2))
    sq.resize((32, 32), Image.LANCZOS).save(out)


def main(argv):
    preview = None
    if argv[:1] == ["--preview"]:
        preview, argv = Path(argv[1]), argv[2:]
    for name in argv or list(DESIGNS):
        v = DESIGNS[name]()
        model, tex, count, used = v.hull.save()
        for p in v.parts:
            p.save()
        lay = ASSETS / "vehicles" / f"vehicle_{name.lower()}.json"
        lay.parent.mkdir(parents=True, exist_ok=True)
        lay.write_text(json.dumps(v.layout, indent=1) + "\n")
        icon(model, tex, ASSETS / "textures/items" / f"vehicle_{name.lower()}.png")
        print(f"{name}: {count} cubes, atlas rows {used}, {len(v.parts)} turret parts")
        if preview:
            preview.mkdir(parents=True, exist_ok=True)
            subprocess.run([sys.executable, str(Path(__file__).with_name("render_geo.py")), str(model), str(tex),
                            str(preview / f"{name}.png"), "--views", "three_quarter,side"], check=True,
                           stdout=subprocess.DEVNULL)


if __name__ == "__main__":
    main(sys.argv[1:])
