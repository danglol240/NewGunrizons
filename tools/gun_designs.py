"""Designs for the EV-MAX weapons. Each fits inside its base gun's envelope so the
base factory's hand/attachment positioning still applies.

usage: gun_designs.py [NAME ...]        (no names = build all)
       gun_designs.py --preview DIR [NAME ...]
"""
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from gunforge import Gun  # noqa: E402


def xr7():  # EV rail-assisted smart rifle, SCAR-H envelope (cx -1.5)
    g = Gun("XR7", "EV")
    cx, by = -1.5, -14.5
    g.tube(cx, by, -76, -60, 1, "metal")
    g.tube(cx, by, -79, -76, 2, "frame")  # muzzle brake
    g.box(cx - 1, by - 1, -80, cx + 1, by + 1, -79, "glow")
    g.box(-4.5, -19, -60, 1.5, -11.2, -28, "armor")  # handguard shell
    g.box(-4, -11.2, -58, 1, -10.2, -30, "rail")  # under rail
    g.box(-4, -20, -28, 1, -10.5, 6, "frame")  # receiver
    g.box(-4.5, -19, -26, 1.5, -12, -6, "armor")
    g.box(-4, -21, -42, 1, -20, -14, "dark")  # smart optic housing
    g.box(-3.5, -21, -44, 0.5, -19, -42, "lens")
    g.box(1.5, -17, -24, 2.5, -13, -16, "glow")  # sensor panel
    g.box(-5.5, -17, -24, -4.5, -13, -16, "glow")
    g.box(-4, -10.5, -26, 1, -6, -10, "armor")  # magwell
    g.box(-3, -6, -10, 0, -4.5, -4, "dark")  # trigger guard
    g.box(-4, -10.5, -6, 1, 8, 1, "grip")
    g.box(-4, -19.4, 6, 1, -15.5, 32, "armor")  # stock
    g.box(-3.5, -15.5, 12, 0.5, -11, 16, "dark")
    g.box(-4, -11, 12, 1, -8, 30, "frame")
    g.box(-4.5, -14, 18, 1.5, -11, 26, "glow")  # power cell
    g.box(-4.5, -18, 30, 1.5, 1, 37, "armor")  # butt
    return g


def hx9():  # EV rail-assisted heavy rifle, Mk48 envelope (cx 2.5)
    g = Gun("HX9", "EV")
    cx, by = 2.5, -17
    g.tube(cx, by, -50, -28, 1, "metal")
    g.box(cx - 1, by - 1, -51, cx + 1, by + 1, -50, "glow")
    g.box(0, -20, -46, 5, -14, -28, "frame")  # cooling jacket
    g.rings(cx, by, -45, -30, 5, 3, depth=2)
    g.box(0, -20.6, -28, 5, -11.6, 16, "frame")  # receiver
    g.box(-1, -19.5, -24, 0, -12.5, 12, "armor")  # side plates
    g.box(5, -19.5, -24, 6, -12.5, 12, "armor")
    g.box(5.5, -18, -10, 6.5, -14, -2, "glow")  # power cell
    g.box(1, -23, -14, 4, -21, 2, "armor")  # carry handle
    g.box(1.5, -21, -14, 3.5, -20.6, -12, "dark")
    g.box(1.5, -21, 0, 3.5, -20.6, 2, "dark")
    g.box(1, -9, -2, 4, -7.5, 4, "dark")  # trigger guard
    g.box(0.5, -11.6, 4, 4.5, -2, 12, "grip")
    g.box(0, -18, 16, 5, -10, 40, "armor")  # stock
    g.box(0.5, -10, 26, 4.5, -7.7, 40, "frame")
    g.box(-0.5, -16, 22, 0.5, -12, 34, "glow")
    g.box(0, -18.5, 40, 5, -7.5, 43, "grip")
    return g


def coilgun():  # IV PDW, P90 bullpup envelope (cx 2.5)
    g = Gun("Coilgun", "IV")
    cx = 2.5
    g.tube(cx, -13, -12, -6, 1.5, "metal")  # muzzle
    g.tube(cx, -13, -13, -12, 1, "glow")
    g.box(0, -17, -6, 5, -3, 14, "armor")  # front shroud
    g.box(0.5, -22, -6, 4.5, -17, 12, "frame")  # optic block
    g.box(1, -21, -7, 4, -18, -6, "lens")
    g.box(1, -6, 6, 4, -0.6, 12, "grip")  # thumbhole grip
    g.box(0.5, -3, -6, 4.5, -1, 6, "frame")
    # exposed coil section: barrel core wrapped by energy rings between two spines
    g.tube(cx, -10, 14, 40, 1.5, "metal")
    for z in range(15, 39, 4):
        g.tube(cx, -10, z, z + 2, 3.5, "ring")
    g.box(0, -17, 14, 5, -14, 42, "armor")  # upper spine
    g.box(0.5, -5, 14, 4.5, -2, 42, "frame")  # lower spine / mag slot
    g.box(-0.5, -16, 16, 0, -15, 40, "glow")
    g.box(0, -17, 40, 5, -2, 45, "armor")  # butt
    g.box(0.5, -16, 44, 4.5, -3, 45, "grip")
    return g


def gauss_rifle():  # IV marksman, AS50 envelope (cx 2.5)
    g = Gun("GaussRifle", "IV")
    cx, by = 2.5, -16.75
    g.tube(cx, by, -60, -28, 1, "metal")
    g.rings(cx, by, -58, -44, 4, 2.5, depth=2)
    g.tube(cx, by, -63, -60, 2, "armor")
    g.box(cx - 1, by - 1, -64, cx + 1, by + 1, -63, "glow")
    g.box(0.5, -19.5, -44, 4.5, -14.5, -28, "armor")  # handguard
    g.box(0.3, -20.2, -28, 4.7, -14.3, 8, "frame")  # receiver
    g.box(0.5, -14.3, -8, 4.5, -10.5, 8, "frame")
    g.box(1, -21.2, -24, 4, -20.2, 2, "rail")
    g.box(4.7, -19, -26, 5.7, -16, -10, "glow")  # capacitors
    g.box(-0.7, -19, -26, 0.3, -16, -10, "glow")
    g.box(1, -9, 0, 4, -7.5, 6, "dark")
    g.box(1, -10.5, 6, 4, 0.5, 14, "grip")
    g.box(0.5, -19.5, 14, 4.5, -15.5, 33, "armor")  # stock
    g.box(1, -15.5, 18, 4, -12, 22, "dark")
    g.box(0.5, -12, 18, 4.5, -10.5, 33, "frame")
    g.box(1, -15, 24, 4, -12, 30, "glow")
    g.box(0.3, -19.5, 32, 4.7, -4.5, 35, "armor")
    return g


def railgun():  # IV anti-materiel, M107 envelope (cx -1.5)
    g = Gun("Railgun", "IV")
    g.box(-5, -17, -140, -3, -12, -80, "metal")
    g.box(0, -17, -140, 2, -12, -80, "metal")
    g.box(-3, -15, -138, 0, -14, -80, "glow")
    g.box(-3, -12, -140, 0, -11, -80, "dark")
    for z in range(-140, -80, 12):
        g.box(-3, -17, z, 0, -16, z + 4, "dark")
    for z in range(-132, -86, 12):
        g.box(-8, -20, z, 5, -9, z + 3, "ring")
        g.box(-7, -19, z + 3, 4, -10, z + 4, "dark")
    for z in range(-127, -87, 12):
        g.box(-5.5, -15, z, -4.5, -14, z + 6, "glow")
        g.box(1.5, -15, z, 2.5, -14, z + 6, "glow")
    g.box(-7, -19, -144, 4, -10, -140, "armor")
    for x0, y0 in [(-7, -19), (2, -19), (-7, -12), (2, -12)]:
        g.box(x0, y0, -150, x0 + 2, y0 + 2, -144, "metal")
    g.box(-3, -16, -145, 0, -13, -144, "glow")
    g.box(-5, -15.7, -80, 2, -10.9, 8, "frame")
    g.box(-4, -16.7, -80, 1, -15.7, 8, "armor")
    g.box(-3.5, -17.7, -62, 0.5, -15.7, -6, "rail")
    g.box(-4.5, -10.9, -78, 1.5, -7, -48, "dark")
    for z in range(-76, -50, 6):
        g.box(-5, -10, z, 2, -8, z + 3, "glow")
    g.box(-6, -16, -44, -5, -12, -14, "glow")
    g.box(2, -16, -44, 3, -12, -14, "glow")
    g.box(-6, -17, -74, -5, -12, -56, "vent")
    g.box(2, -17, -74, 3, -12, -56, "vent")
    g.box(-6, -16.7, -46, -5, -11.7, -44, "armor")
    g.box(2, -16.7, -46, 3, -11.7, -44, "armor")
    g.box(-6, -16.7, -14, -5, -11.7, 6, "armor")
    g.box(2, -16.7, -14, 3, -11.7, 6, "armor")
    g.box(-4.5, -10.9, -34, 1.5, -3, -16, "armor")
    g.box(-3, -4, -16, 0, -2.5, -6, "dark")
    g.box(-2, -9, -12, -1, -5, -11, "steel")
    g.box(-4, -10.9, -8, 1, 8, 0, "grip")
    g.box(-4.5, 6, -9, 1.5, 9, 1, "armor")
    g.box(-6, -16.5, -10, -5, -14.5, 4, "warn")
    g.box(-4, -17, 8, 1, -14, 40, "armor")
    g.box(-4, -4, 14, 1, -1, 40, "frame")
    g.box(-3.5, -14, 18, 0.5, -4, 22, "dark")
    g.box(-5, -13, 26, 2, -5, 36, "dark")
    g.box(-5.5, -11, 28, 2.5, -7, 34, "glow")
    g.box(-4.5, -18, 40, 1.5, 0, 44, "armor")
    return g


def laser_rifle():  # LuV directed-energy rifle, M8A7 envelope (cx -1.5)
    g = Gun("LaserRifle", "LuV")
    cx, by = -1.5, -11.9
    g.tube(cx, by, -72, -60, 1, "lens")
    g.tube(cx, by, -74, -72, 2, "ring")
    g.box(-4, -20.5, -60, 1, -6, -28, "armor")  # heat-sink core
    g.fins(-5, -19.5, 2, -7, -58, -30, 3, "metal")
    g.box(-4.5, -21, -28, 1.5, -5, 0, "armor")  # receiver
    g.box(1.5, -16, -24, 2.5, -12, -10, "glow")  # lens chamber windows
    g.box(-5.5, -16, -24, -4.5, -12, -10, "glow")
    g.box(-4, -5, -22, 1, -3, -12, "frame")
    g.box(-3, -4, -14, 0, -2.5, -8, "dark")
    g.box(-4, -5, -8, 1, 8, 0, "grip")
    g.box(-4, -14.2, 0, 1, -11, 26, "armor")  # stock
    g.box(-3.5, -11, 4, 0.5, -4, 8, "dark")
    g.box(-4, -4, 4, 1, -1, 26, "frame")
    g.box(-4.5, -10, 12, 1.5, -6, 22, "glow")
    g.box(-4.5, -14.5, 26, 1.5, 0.3, 30, "armor")
    return g


def plasma_rifle():  # ZPM plasma rifle, M41A envelope (cx 2.5)
    g = Gun("PlasmaRifle", "ZPM")
    g.box(0.7, -14.7, -31, 4.3, -8, -16, "armor")  # front shroud
    g.box(1.2, -8, -29, 3.8, -3, -18, "glow")  # plasma bulb
    g.rings(2.5, -5.5, -28, -18, 4, 2.0, depth=1, collar=False)
    g.box(1, -14, -34, 4, -9, -31, "ring")  # emitter
    g.box(1, -15.7, -28, 4, -14.7, -16, "vent")
    g.box(0.5, -19.5, -16, 4.5, -6, 20, "frame")  # receiver
    g.box(1, -20.5, -14, 4, -19.5, 10, "rail")
    g.box(-0.5, -19, -14, 5.5, -14, 8, "armor")
    g.box(5, -13.5, -14, 6, -11.5, 8, "glow")  # coolant lines
    g.box(-1, -13.5, -14, 0, -11.5, 8, "glow")
    g.box(1, -6, -4, 4, 3.5, 4, "grip")
    g.box(0.5, -16.5, 20, 4.5, -9, 31, "armor")  # stock
    g.box(0.5, -9, 26, 4.5, -2.5, 31, "frame")
    return g


def _launcher(g, mats):
    """SMAW-envelope shoulder launcher body shared by both cannons (tube cx 1.5, cy -20.75)."""
    g.box(-2, -24.5, -24, 5, -17, 64, mats["tube"])
    g.box(-1, -24, -27, 4, -17.5, -24, "metal")
    g.box(0, -23, -28, 3, -18.5, -27, "glow")
    g.box(-1.5, -24, 64, 4.5, -17.5, 69, "vent")
    g.box(-1, -17, -4, 3, -3.3, 0, "grip")  # front grip
    g.box(-1, -17, 4, 4, -8.8, 12, "frame")  # trigger housing
    g.box(-0.5, -8.8, 8, 3.5, 0.7, 12, "grip")
    g.box(6, -25, 4, 9, -20, 12, "frame")  # side optic
    g.box(6.5, -24.5, 3, 8.5, -20.5, 4, "lens")


def plasma_cannon():  # ZPM, SMAW envelope
    g = Gun("PlasmaCannon", "ZPM")
    _launcher(g, {"tube": "armor"})
    for z in (-22, -14, 28, 40, 52):
        g.box(-3, -25.5, z, 6, -16, z + 3, "ring")
    g.box(0, -25.5, -10, 3, -24.5, 60, "glow")  # top plasma conduit
    g.box(5, -22, 16, 6, -19, 60, "glow")
    return g


def antimatter_cannon():  # UHV, SMAW envelope
    g = Gun("AntimatterCannon", "UHV")
    _launcher(g, {"tube": "frame"})
    g.box(-3, -25.5, -26, 6, -16, -8, "armor")  # containment chamber
    g.box(6, -23, -22, 7, -19, -12, "glow")
    g.box(-4, -23, -22, -3, -19, -12, "glow")
    for z in (-6, 16, 30, 44):
        g.box(-3, -25.5, z, 6, -16, z + 2, "ring")
    g.box(-2, -25.5, 18, 5, -24.5, 62, "cosmic")
    g.box(6, -24, 20, 8, -19, 40, "glow")  # antimatter canister
    g.box(5.5, -24.5, 19, 8.5, -18.5, 20, "metal")
    g.box(5.5, -24.5, 40, 8.5, -18.5, 41, "metal")
    g.box(-2.5, -25, 50, 5.5, -16.5, 64, "armor")  # rear capacitor
    return g


def particle_beam_rifle():  # UV, Hecate II envelope (cx 2.5)
    g = Gun("ParticleBeamRifle", "UV")
    cx, by = 2.5, -14.8
    g.tube(cx, by, -52, -24, 1, "metal")
    for z in range(-52, -26, 5):
        g.tube(cx, by, z, z + 2, 2, "ring")
        g.tube(cx, by, z + 2, z + 4, 1.5, "metal")
    g.tube(cx, by, -55, -52, 1.5, "armor")
    g.box(1.5, -15.8, -56, 3.5, -13.8, -55, "glow")
    g.box(0.5, -16.3, -24, 4.5, -11.5, 12, "armor")  # receiver
    g.box(1, -17.3, -20, 4, -16.3, 6, "rail")
    g.box(1, -11.5, -24, 4, -10.5, -12, "frame")
    g.box(4.5, -16, -20, 5.5, -13, -4, "glow")
    g.box(-0.5, -16, -20, 0.5, -13, -4, "glow")
    g.box(1, -11.5, -4, 4, -7.5, 10, "frame")
    g.box(1.5, -8, 4, 3.5, -6.6, 10, "dark")
    g.box(1, -11.5, 10, 4, 0.5, 15, "grip")
    g.box(1, -16, 15, 4, -12, 33, "armor")  # stock
    g.box(1.5, -12, 18, 3.5, -8, 21, "dark")
    g.box(1, -8, 20, 4, -6.2, 33, "frame")
    g.box(0.5, -15, 23, 4.5, -12, 31, "glow")
    g.box(1, -16, 33, 4, -6.2, 36, "armor")
    return g


def neutron_rifle():  # UV, M249 envelope (cx 2.5)
    g = Gun("NeutronRifle", "UV")
    cx, by = 2.5, -17
    g.tube(cx, by, -38, -36, 1, "glow")
    g.box(-0.5, -20, -36, 5.5, -14, -24, "frame")  # emitter drum
    g.rings(cx, by, -35, -25, 4, 3.5, depth=1, collar=False)
    g.box(0, -19.2, -24, 5, -11.6, 18, "armor")  # receiver
    g.fins(-1, -18, 6, -13, -22, -12, 2, "metal")
    g.box(0.5, -23.3, -12, 4.5, -19.2, 4, "frame")  # reactor housing
    g.box(1, -22.5, -10, 4, -21.5, 2, "glow")
    g.box(1, -9, 0, 4, -7.5, 6, "dark")
    g.box(0.5, -11.6, 6, 4.5, -1.9, 15, "grip")
    g.box(0, -18, 18, 5, -10, 40, "armor")  # stock
    g.box(0.5, -10, 28, 4.5, -7.7, 40, "frame")
    g.box(-0.5, -16, 22, 0.5, -12, 34, "glow")
    g.box(5, -16, 22, 6, -12, 34, "glow")
    g.box(0, -18.5, 40, 5, -7.7, 43, "grip")
    return g


def exotic_matter_rifle():  # UHV, MX envelope (cx 2.5)
    g = Gun("ExoticMatterRifle", "UHV")
    g.box(1.5, -18.8, -42, 3.5, -16.8, -30, "metal")  # split prongs
    g.box(1.5, -12.8, -42, 3.5, -10.8, -30, "metal")
    g.box(2, -16, -41, 3, -14, -30, "glow")  # floating core
    for z in (-38, -34):
        g.tube(2.5, -14.8, z, z + 1, 3, "ring")
    g.box(-0.3, -18.8, -30, 5.3, -11.8, 18, "armor")  # receiver
    g.box(-1.3, -17, -26, -0.3, -13, -6, "cosmic")
    g.box(5.3, -17, -26, 6.3, -13, -6, "cosmic")
    g.box(0.3, -11.8, -6, 4.7, -9.4, 18, "frame")
    g.box(0.5, -9.4, -2, 4.5, -7, 8, "armor")  # magwell
    g.box(1.5, -8, 4, 3.5, -6.5, 9, "dark")
    g.box(0.5, -9.4, 9, 4.5, 0.2, 16, "grip")
    g.box(0.5, -18.5, 18, 4.5, -12, 37, "armor")  # stock
    g.box(1, -12, 22, 4, -9.3, 26, "dark")
    g.box(0.5, -9.3, 24, 4.5, -4, 37, "frame")
    g.box(0, -16, 26, 5, -13, 34, "glow")
    g.box(0.5, -17.5, 37, 4.5, -4, 40, "armor")
    return g


def quantum_phase_rifle():  # UEV, AUG A3 bullpup envelope (cx -1.5)
    g = Gun("QuantumPhaseRifle", "UEV")
    cx, by = -1.5, -10
    g.tube(cx, by, -50, -34, 1, "glow")  # phase emitter core
    for z in (-49, -45, -41, -37):
        g.tube(cx, by, z, z + 2, 3, "ring")  # floating phase rings
    g.box(cx - 1, by - 5, -52, cx + 1, by - 4, -34, "metal")  # emitter prongs
    g.box(cx - 1, by + 4, -52, cx + 1, by + 5, -34, "metal")
    g.box(-4.5, -15, -34, 1.5, -5, -8, "armor")  # forward shell
    g.box(1.5, -12, -30, 2.5, -10, -12, "glow")
    g.box(-5.5, -12, -30, -4.5, -10, -12, "glow")
    g.box(-3.5, -18, -26, 0.5, -15, -10, "dark")  # phase optic
    g.box(-3, -17.5, -27, 0, -15.5, -26, "lens")
    g.box(-3.5, -5, -29, 0.5, 5, -24, "grip")  # foregrip
    g.box(-3, -5, -8, 0, -3.5, -2, "dark")  # trigger guard
    g.box(-3.5, -5, -2, 0.5, 9, 4, "grip")
    g.box(-4.5, -15, -8, 1.5, -4, 36, "frame")  # bullpup body
    g.box(-5, -16, -6, 2, -13, 30, "armor")
    g.box(1.5, -12, 6, 2.5, -6, 24, "cosmic")  # quantum core windows
    g.box(-5.5, -12, 6, -4.5, -6, 24, "cosmic")
    g.box(2, -10, 9, 3, -8, 21, "glow")
    g.box(-6, -10, 9, -5, -8, 21, "glow")
    g.fins(-3, -17, 0, -16, 0, 28, 4, "metal")
    g.box(-4, -4, 26, 1, 6, 36, "frame")
    g.box(-5, -16, 36, 2, 6, 39, "armor")  # butt
    return g


def graviton_cannon():  # UIV, RPK-74M envelope (cx -1.5)
    g = Gun("GravitonCannon", "UIV")
    cx, by = -1.5, -11
    g.tube(cx, by, -66, -40, 5, "armor")  # graviton projector drum
    for z in (-64, -57, -50, -43):
        g.tube(cx, by, z, z + 2, 6, "ring")
    g.tube(cx, by, -70, -66, 4, "frame")
    g.tube(cx, by, -71, -70, 2.5, "lens")
    for x0, y0, x1, y1 in ((cx - 0.5, by - 6, cx + 0.5, by - 5), (cx - 0.5, by + 5, cx + 0.5, by + 6),
                           (cx + 5, by - 0.5, cx + 6, by + 0.5), (cx - 6, by - 0.5, cx - 5, by + 0.5)):
        g.box(x0, y0, -90, x1, y1, -70, "metal")  # field prongs
        g.box(x0, y0, -92, x1, y1, -90, "glow")
    g.tube(cx, by, -86, -82, 1.5, "glow")  # held singularity
    g.box(-4.5, -17, -40, 1.5, -6, 6, "frame")  # receiver
    g.box(-4, -20, -36, 1, -17, -10, "armor")  # core housing
    g.box(-3.5, -21, -32, 0.5, -20, -14, "glow")
    g.box(1.5, -14, -36, 2.5, -12, 0, "glow")  # coolant lines
    g.box(-5.5, -14, -36, -4.5, -12, 0, "glow")
    g.box(-3, -6, -6, 0, -4.5, 0, "dark")
    g.box(-3.5, -6, -2, 0.5, 9, 4, "grip")
    g.box(-4, -13, 6, 1, -7, 34, "armor")  # stock
    g.box(-3.5, -7, 14, 0.5, 3, 34, "frame")
    g.box(1, -11, 14, 2, -9, 30, "glow")
    g.box(-4.5, -14, 34, 1.5, 4, 37, "grip")
    return g


def chrono_rifle():  # UMV, L96 envelope (cx -1.5)
    g = Gun("ChronoRifle", "UMV")
    cx, by = -1.5, -14
    g.tube(cx, by, -130, -44, 1, "metal")
    g.tube(cx, by, -136, -130, 2, "armor")
    g.box(cx - 1, by - 1, -137, cx + 1, by + 1, -136, "glow")
    for z in (-124, -112, -100):  # chrono field rings
        g.tube(cx, by, z, z + 2, 4, "ring")
        g.tube(cx, by, z + 2, z + 3, 3, "dark")
    g.box(cx - 2, by - 3, -90, cx + 2, by + 3, -70, "armor")  # time crystal chamber
    g.box(cx + 2, by - 2, -88, cx + 3, by + 2, -72, "glow")
    g.box(cx - 3, by - 2, -88, cx - 2, by + 2, -72, "glow")
    g.box(-4.5, -12, -70, 1.5, -6, -44, "frame")  # handguard
    g.box(-4.5, -17, -44, 1.5, -5, -8, "armor")  # receiver
    g.box(-3.5, -21, -38, 0.5, -17, -14, "dark")  # chrono scope
    g.box(-3, -20.5, -39, 0, -17.5, -38, "lens")
    g.box(0.5, -21, -30, 1.5, -17, -22, "ring")  # dial
    g.box(1.5, -14, -40, 2.5, -12, -12, "glow")
    g.box(-5.5, -14, -40, -4.5, -12, -12, "glow")
    g.box(-3, -5, -8, 0, -3.5, -2, "dark")
    g.box(-3.5, -5, -4, 0.5, 8, 4, "grip")
    g.box(-4.5, -14, -8, 1.5, -6, 38, "armor")  # stock
    g.box(-4, -6, 12, 1, 3.5, 36, "frame")
    g.box(1.5, -12, 14, 2.5, -8, 30, "glow")  # time crystal
    g.box(-4.5, -15, 38, 1.5, 4, 41, "grip")
    return g


def singularity_cannon():  # UMV, SMAW envelope
    g = Gun("SingularityCannon", "UMV")
    _launcher(g, {"tube": "frame"})
    g.box(-3, -26, -26, 6, -16, -12, "cosmic")  # event-horizon containment
    for z in (-24, -18):
        g.box(-4, -27, z, 7, -15, z + 2, "ring")
    for z in (4, 20, 36, 52):  # accretion stabilisers
        g.box(-3, -25.5, z, 6, -16, z + 2, "ring")
    g.box(0, -25.5, -10, 3, -24.5, 60, "glow")
    g.box(6, -23, 20, 7, -19, 44, "glow")
    g.box(-2.5, -25, 56, 5.5, -16.5, 64, "armor")
    return g


def dimensional_cutter():  # UXV, Dragunov envelope (cx -1.5)
    g = Gun("DimensionalCutter", "UXV")
    cx, by = -1.5, -11.2
    g.tube(cx, by, -132, -60, 1, "metal")  # blade spine
    g.box(cx - 0.5, by + 1, -128, cx + 0.5, by + 4, -62, "glow")  # rift edge
    g.box(cx - 0.5, by - 4, -126, cx + 0.5, by - 1, -62, "cosmic")
    g.box(cx - 0.5, by - 1, -137, cx + 0.5, by + 1, -132, "glow")  # tip
    g.tube(cx, by, -62, -58, 4, "ring")  # guard
    g.box(-4.5, -16, -58, 1.5, -8, -44, "armor")
    g.box(-4.5, -16, -44, 1.5, -6, -8, "frame")  # receiver
    g.box(1.5, -14, -40, 2.5, -9, -12, "cosmic")
    g.box(-5.5, -14, -40, -4.5, -9, -12, "cosmic")
    g.box(-3.5, -17, -38, 0.5, -16, -14, "dark")
    g.box(-2.5, -19, -34, -0.5, -17, -18, "glow")  # rift crystal
    g.box(-3, -6, -10, 0, -4.5, -4, "dark")
    g.box(-3.5, -6, -4, 0.5, 8, 4, "grip")
    g.box(-4, -13, 4, 1, -8, 38, "armor")  # stock
    g.box(-3.5, -8, 20, 0.5, 6, 36, "frame")
    g.box(1, -12, 8, 2, -10, 36, "glow")
    g.box(-4.5, -13, 38, 1.5, 8, 41, "cosmic")
    return g


def reality_eraser():  # MAX, SV-98 envelope (cx -1.5)
    g = Gun("RealityEraser", "MAX")
    cx, by = -1.5, -14
    g.tube(cx, by, -120, -44, 1, "glow")
    for z in range(-118, -50, 12):  # gilded shroud segments
        g.tube(cx, by, z, z + 8, 2.5, "armor")
        g.tube(cx, by, z + 8, z + 10, 3, "ring")
    g.tube(cx, by, -126, -120, 3, "armor")
    g.box(cx - 1, by - 1, -127, cx + 1, by + 1, -126, "lens")
    for dx, dy in ((-3.5, -3.5), (2.5, -3.5), (-3.5, 2.5), (2.5, 2.5)):  # crown
        g.box(cx + dx, by + dy, -130, cx + dx + 1, by + dy + 1, -126, "glow")
    g.box(-4.5, -18, -44, 1.5, -6, -8, "armor")  # receiver
    g.box(1.5, -15, -36, 2.5, -9, -16, "glow")  # infinity catalyst windows
    g.box(-5.5, -15, -36, -4.5, -9, -16, "glow")
    g.fins(-3, -21, 0, -18, -42, -12, 4, "metal")  # crest
    g.box(-3, -6, -8, 0, -4.5, -2, "dark")
    g.box(-3.5, -6, -4, 0.5, 8.5, 4, "grip")
    g.box(-4.5, -15, -8, 1.5, -7, 38, "armor")  # stock
    g.box(-4, -7, 12, 1, 6, 36, "metal")
    g.box(1.5, -13, 12, 2.5, -9, 32, "glow")
    g.box(-5.5, -13, 12, -4.5, -9, 32, "glow")
    g.box(-4.5, -15, 38, 1.5, 7, 41, "ring")
    return g


DESIGNS = {
    "XR7": xr7, "HX9": hx9, "Coilgun": coilgun, "GaussRifle": gauss_rifle, "Railgun": railgun,
    "LaserRifle": laser_rifle, "PlasmaRifle": plasma_rifle, "PlasmaCannon": plasma_cannon,
    "ParticleBeamRifle": particle_beam_rifle, "NeutronRifle": neutron_rifle,
    "AntimatterCannon": antimatter_cannon, "ExoticMatterRifle": exotic_matter_rifle,
    "QuantumPhaseRifle": quantum_phase_rifle, "GravitonCannon": graviton_cannon, "ChronoRifle": chrono_rifle,
    "SingularityCannon": singularity_cannon, "DimensionalCutter": dimensional_cutter, "RealityEraser": reality_eraser,
}


def main(argv):
    preview = None
    if argv[:1] == ["--preview"]:
        preview, argv = Path(argv[1]), argv[2:]
    names = argv or list(DESIGNS)
    for n in names:
        model, tex, count, used = DESIGNS[n]().save()
        print(f"{n}: {count} cubes, atlas rows used {used}")
        if preview:
            import subprocess
            subprocess.run([sys.executable, str(Path(__file__).with_name("render_geo.py")), str(model), str(tex),
                            str(preview / f"{n}.png"), "--views", "side,three_quarter"], check=True)


if __name__ == "__main__":
    main(sys.argv[1:])
