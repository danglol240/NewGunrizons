"""Models of the LV-MAX combat drones (written to models/drone/, textures to textures/models/drone_<name>.png).

Model space is Minecraft Java model space: Y points down (negative Y is up), the nose points towards -Z. The body is
centred on the origin; the rotors themselves are not part of the model: DroneRenderer draws them spinning on top of
the motor shafts, at the hub positions given by the layout (keep LAYOUTS in sync with DroneLayout.java).

usage: drone_designs.py [--preview DIR] [NAME ...]
"""
import math
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import gunforge  # noqa: E402
from gunforge import Gun  # noqa: E402

gunforge.TIERS.setdefault("LV", {"glow": (220, 225, 235), "core": (255, 255, 255), "metal": (150, 155, 165)})
gunforge.TIERS.setdefault("MV", {"glow": (255, 140, 60), "core": (255, 220, 190), "metal": (190, 200, 210)})
gunforge.TIERS.setdefault("HV", {"glow": (255, 220, 60), "core": (255, 245, 200), "metal": (200, 205, 215)})

# rotor count, hub distance from centre, angle of the first arm (degrees), rotor radius
LAYOUTS = {"QUAD": (4, 9.9, 45.0, 5.5), "HEX": (6, 10.0, 30.0, 4.6), "OCTO": (8, 11.0, 22.5, 4.0)}
HUB_TOP = -4.2  # top of the motor shafts; the rotors spin just above


def arms(g, layout, ducted):
    n, r, off, rotor = LAYOUTS[layout]
    for k in range(n):
        a = math.radians(off + 360.0 * k / n)
        hx, hz = r * math.cos(a), r * math.sin(a)
        steps = int(r / 0.8)
        for i in range(2, steps):  # stepped arm out to the motor, overlapping so it reads as one beam
            x, z = hx * i / steps, hz * i / steps
            g.box(x - 0.5, -2.5, z - 0.5, x + 0.5, -1.5, z + 0.5, "metal")
        g.box(hx - 1, -3.5, hz - 1, hx + 1, -1.5, hz + 1, "metal")  # motor can
        g.box(hx - 0.5, HUB_TOP, hz - 0.5, hx + 0.5, -3.5, hz + 0.5, "steel")  # shaft
        if ducted:  # square duct around the rotor, glowing rim
            d = rotor + 0.5
            g.box(hx - d, -4.8, hz - d, hx + d, -3.8, hz - d + 1, "armor")
            g.box(hx - d, -4.8, hz + d - 1, hx + d, -3.8, hz + d, "armor")
            g.box(hx - d, -4.8, hz - d + 1, hx - d + 1, -3.8, hz + d - 1, "glow")
            g.box(hx + d - 1, -4.8, hz - d + 1, hx + d, -3.8, hz + d - 1, "glow")


def body(g, w, l, shell, top, skids):
    g.box(-w / 2, -3, -l / 2, w / 2, -1, l / 2, shell)  # fuselage
    g.box(-w / 2 + 1, -4, -l / 2 + 1, w / 2 - 1, -3, l / 2 - 1, top)  # battery pack / armour lid
    g.box(-1, -2.5, -l / 2 - 1, 1, -1.5, -l / 2, "lens")  # nose camera
    g.box(-w / 2 - 0.5, -2.5, l / 2 - 2, -w / 2, -1.5, l / 2 - 1, "glow")  # navigation lights
    g.box(w / 2, -2.5, l / 2 - 2, w / 2 + 0.5, -1.5, l / 2 - 1, "glow")
    if skids:
        for sx in (-w / 2, w / 2 - 1):
            g.box(sx, -1, -l / 2 + 1, sx + 1, 2, -l / 2 + 2, "dark")  # legs
            g.box(sx, -1, l / 2 - 2, sx + 1, 2, l / 2 - 1, "dark")
            g.box(sx, 2, -l / 2, sx + 1, 3, l / 2, "dark")  # skid


def weapon(g, kind):
    if kind == "pistol":
        g.box(-1, -1, -2, 1, 0.5, 2, "frame")
        g.box(-0.5, -0.5, -6, 0.5, 0.5, -2, "metal")
    elif kind == "smg":
        g.box(-1, -1, -3, 1, 0.8, 2, "frame")
        g.box(-0.5, -0.5, -7, 0.5, 0.5, -3, "metal")
        g.box(-0.5, 0.8, -2, 0.5, 2.8, 0, "dark")  # magazine
    elif kind == "rifle":
        g.box(-1, -1, -3, 1, 1, 3, "frame")
        g.box(-0.5, -0.5, -10, 0.5, 0.5, -3, "metal")
        g.box(-1, -0.6, -11, 1, 0.6, -10, "dark")  # muzzle brake
        g.box(-0.5, 1, -2, 0.5, 3, 1, "dark")
    elif kind == "twin":
        for x in (-2.5, 2.5):
            g.box(x - 1, -1, -3, x + 1, 1, 3, "frame")
            g.box(x - 0.5, -0.5, -11, x + 0.5, 0.5, -3, "metal")
        g.box(-3.5, -1.5, -1, 3.5, -1, 1, "dark")  # mount
        g.box(-1, 0, -1, 1, 4, 2, "warn")  # ammo box
    elif kind == "rail":
        g.box(-2, -0.5, -13, -1, 0.5, 2, "metal")
        g.box(1, -0.5, -13, 2, 0.5, 2, "metal")
        g.box(-1, -0.2, -12, 1, 0.2, 1, "glow")
        for z in (-11, -7, -3):
            g.box(-2.5, -1, z, 2.5, 1, z + 1, "ring")
    elif kind == "emitter":
        g.box(-1.5, -1, -8, 1.5, 1.5, 2, "armor")
        g.box(-1, -0.6, -9, 1, 1, -8, "lens")
        for z in (-6, -3):
            g.box(-2, -1.5, z, 2, 2, z + 1, "ring")
    elif kind == "orb":
        g.box(-2, -0.5, -3, 2, 3, 1, "glow")
        g.box(-2.5, -1, -4, 2.5, 3.5, -3, "ring")
        g.box(-2.5, -1, 1, 2.5, 3.5, 2, "ring")
        g.box(-0.5, 0.5, -7, 0.5, 1.5, -4, "metal")
    elif kind == "rift":
        g.box(-0.5, -1, -14, 0.5, 2, 2, "cosmic")
        g.box(-0.5, 2, -13, 0.5, 3, 1, "glow")
        g.box(-2, -1, -1, 2, 0, 3, "dark")


def drone(name, tier, layout, w, l, kind, shell="frame", top="dark", ducted=False, skids=True, extra=None):
    g = Gun(name, tier, tex_w=256, tex_h=256, subdir="drone")
    body(g, w, l, shell, top, skids)
    arms(g, layout, ducted)
    weapon(g, kind)
    if extra:
        extra(g)
    return g


def _antenna(g):
    g.box(2, -6, 3, 2.5, -4, 3.5, "steel")
    g.box(1.8, -6.5, 2.8, 2.7, -6, 3.7, "glow")


def _sensor_dome(g):
    g.box(-2, -5, -2, 2, -4, 2, "lens")


def _halo(g):
    g.box(-6, -6, -0.5, 6, -5.5, 0.5, "glow")
    g.box(-0.5, -6, -6, 0.5, -5.5, 6, "glow")


DESIGNS = {
    "ScoutDrone": lambda: drone("ScoutDrone", "LV", "QUAD", 6, 8, "pistol", extra=_antenna),
    "AssaultDrone": lambda: drone("AssaultDrone", "MV", "QUAD", 7, 9, "smg", shell="armor", extra=_antenna),
    "GunshipDrone": lambda: drone("GunshipDrone", "HV", "HEX", 8, 10, "rifle", shell="armor"),
    "HeavyDrone": lambda: drone("HeavyDrone", "EV", "OCTO", 10, 12, "twin", shell="armor", top="metal"),
    "RailDrone": lambda: drone("RailDrone", "IV", "QUAD", 8, 12, "rail", shell="armor", top="glow", ducted=True,
                               skids=False),
    "LaserDrone": lambda: drone("LaserDrone", "LuV", "QUAD", 8, 10, "emitter", shell="armor", top="glow",
                                ducted=True, skids=False, extra=_sensor_dome),
    "PlasmaDrone": lambda: drone("PlasmaDrone", "ZPM", "HEX", 9, 11, "orb", shell="armor", top="glow", ducted=True,
                                 skids=False),
    "ParticleDrone": lambda: drone("ParticleDrone", "UV", "HEX", 9, 12, "emitter", shell="armor", top="glow",
                                   ducted=True, skids=False, extra=_sensor_dome),
    "AntimatterDrone": lambda: drone("AntimatterDrone", "UHV", "OCTO", 10, 12, "orb", shell="cosmic", top="glow",
                                     ducted=True, skids=False),
    "QuantumDrone": lambda: drone("QuantumDrone", "UEV", "QUAD", 9, 11, "emitter", shell="cosmic", top="glow",
                                  ducted=True, skids=False, extra=_halo),
    "GravitonDrone": lambda: drone("GravitonDrone", "UIV", "HEX", 10, 12, "orb", shell="armor", top="glow",
                                   ducted=True, skids=False, extra=_halo),
    "ChronoDrone": lambda: drone("ChronoDrone", "UMV", "HEX", 10, 12, "emitter", shell="cosmic", top="glow",
                                 ducted=True, skids=False, extra=_halo),
    "RiftDrone": lambda: drone("RiftDrone", "UXV", "OCTO", 10, 13, "rift", shell="cosmic", top="glow", ducted=True,
                               skids=False, extra=_halo),
    "SentinelDrone": lambda: drone("SentinelDrone", "MAX", "OCTO", 11, 13, "emitter", shell="armor", top="glow",
                                   ducted=True, skids=False, extra=_halo),
}


def main(argv):
    preview = None
    if argv[:1] == ["--preview"]:
        preview, argv = Path(argv[1]), argv[2:]
    for n in argv or list(DESIGNS):
        g = DESIGNS[n]()
        g.name = "drone_" + n  # files: models/drone/drone_<name>.geo.json, textures/models/drone_<name>.png
        model, tex, count, used = g.save()
        print(f"{n}: {count} cubes, atlas rows used {used}")
        if preview:
            import subprocess
            subprocess.run([sys.executable, str(Path(__file__).with_name("render_geo.py")), str(model), str(tex),
                            str(preview / f"{n}.png"), "--views", "three_quarter"], check=True)


if __name__ == "__main__":
    main(sys.argv[1:])
