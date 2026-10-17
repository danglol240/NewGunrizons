"""Procedurally build the LuV Railgun model (.geo.json) and its pixel texture.

The model is authored in Minecraft Java model space (Y down, barrel towards -Z)
inside the same envelope as the M107 model, so the M107 hand/attachment
positioning in RailgunFactory still fits. Cube sizes are whole numbers because
JsonModel rounds them.

usage: gen_railgun.py REPO_ROOT
"""
import json
import random
import sys
from pathlib import Path

from PIL import Image

ROOT = Path(sys.argv[1])
MODEL_OUT = ROOT / "src/main/resources/assets/newgunrizons/models/weapon/railgun.geo.json"
TEX_OUT = ROOT / "src/main/resources/assets/newgunrizons/textures/models/railgun.png"
TW, TH = 512, 256
rnd = random.Random(1707)

# palette
GUNMETAL = (52, 56, 74)  # graphite frame
DARK = (30, 32, 44)
STEEL = (170, 182, 200)
HSSS = (178, 132, 226)  # HSS-S has a violet sheen in GregTech
COPPER = (238, 136, 54)
GRIP = (36, 34, 46)
GLOW = (255, 64, 214)  # LuV accent
GLOW_CORE = (255, 214, 250)
WARN = (255, 206, 48)
ARMOR = (226, 232, 242)  # pearl-white armour shell
WHITE = (246, 248, 252)
SEAM = (255, 92, 222)  # glowing seam lines on the armour

cubes = []  # (name, (x0,y0,z0), (dx,dy,dz), material)


def box(name, x0, y0, z0, x1, y1, z1, mat):
    """Add a cube by its Java-space extents (y down)."""
    dx, dy, dz = round(x1 - x0), round(y1 - y0), round(z1 - z0)
    assert dx > 0 and dy > 0 and dz > 0, name
    cubes.append((name, (x0, y0, z0), (dx, dy, dz), mat))


# ---------------- geometry (Java space) ----------------
CX = -1.5  # gun centre line in X, same as M107
# barrel assembly: twin rails with an exposed glowing core between them
box("rail_left", -5, -17, -140, -3, -12, -80, "hsss")
box("rail_right", 0, -17, -140, 2, -12, -80, "hsss")
box("core", -3, -15, -138, 0, -14, -80, "glow")
box("rail_floor", -3, -12, -140, 0, -11, -80, "dark")
# top bridges between the rails leave windows onto the glowing core
for i, z in enumerate(range(-140, -80, 12)):
    box(f"bridge_{i}", -3, -17, z, 0, -16, z + 4, "dark")
# electromagnetic coils: chunky copper rings with a dark collar
for i, z in enumerate(range(-132, -86, 12)):
    box(f"coil_{i}", -8, -20, z, 5, -9, z + 3, "ring")
    box(f"collar_{i}", -7, -19, z + 3, 4, -10, z + 4, "dark")
# glowing slits on the outer rail faces between the coils
for i, z in enumerate(range(-127, -87, 12)):
    box(f"slit_l_{i}", -5.5, -15, z, -4.5, -14, z + 6, "glow")
    box(f"slit_r_{i}", 1.5, -15, z, 2.5, -14, z + 6, "glow")
# muzzle: emitter block with four prongs and a bright aperture
box("muzzle", -7, -19, -144, 4, -10, -140, "armor")
for i, (x0, y0) in enumerate([(-7, -19), (2, -19), (-7, -12), (2, -12)]):
    box(f"prong_{i}", x0, y0, -150, x0 + 2, y0 + 2, -144, "hsss")
box("muzzle_glow", -3, -16, -145, 0, -13, -144, "glow")

# receiver
box("receiver", -5, -15.7, -80, 2, -10.9, 8, "gunmetal")
box("receiver_top", -4, -16.7, -80, 1, -15.7, 8, "armor")
box("top_rail", -3.5, -17.7, -62, 0.5, -15.7, -6, "rail")
box("cap_bank", -4.5, -10.9, -78, 1.5, -7, -48, "dark")
for i, z in enumerate(range(-76, -50, 6)):
    box(f"cap_glow_{i}", -5, -10, z, 2, -8, z + 3, "glow")
box("side_cell_l", -6, -16, -44, -5, -12, -14, "glow")
box("side_cell_r", 2, -16, -44, 3, -12, -14, "glow")
box("vent_l", -6, -17, -74, -5, -12, -56, "vent")
box("vent_r", 2, -17, -74, 3, -12, -56, "vent")
box("shell_l", -6, -16.7, -46, -5, -11.7, -44, "armor")
box("shell_r", 2, -16.7, -46, 3, -11.7, -44, "armor")
box("shell_lb", -6, -16.7, -14, -5, -11.7, 6, "armor")
box("shell_rb", 2, -16.7, -14, 3, -11.7, 6, "armor")
box("magwell", -4.5, -10.9, -34, 1.5, -3, -16, "armor")
box("trigger_guard", -3, -4, -16, 0, -2.5, -6, "dark")
box("trigger", -2, -9, -12, -1, -5, -11, "steel")
box("grip", -4, -10.9, -8, 1, 8, 0, "grip")
box("grip_cap", -4.5, 6, -9, 1.5, 9, 1, "armor")
box("warning", -6, -16.5, -10, -5, -14.5, 4, "warn")

# skeleton stock with a power pack
box("stock_top", -4, -17, 8, 1, -14, 40, "armor")
box("stock_bottom", -4, -4, 14, 1, -1, 40, "gunmetal")
box("stock_strut", -3.5, -14, 18, 0.5, -4, 22, "dark")
box("power_pack", -5, -13, 26, 2, -5, 36, "dark")
box("power_glow", -5.5, -11, 28, 2.5, -7, 34, "glow")
box("butt", -4.5, -18, 40, 1.5, 0, 44, "armor")

# ---------------- texture ----------------
img = Image.new("RGBA", (TW, TH), (0, 0, 0, 0))
px = img.load()


def clamp(v):
    return max(0, min(255, int(v)))


def paint_rect(x0, y0, w, h, mat):
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w):
            lx, ly = x - x0, y - y0
            edge_hi = lx == 0 or ly == 0
            edge_lo = lx == w - 1 or ly == h - 1
            n = rnd.uniform(-7, 7)
            if mat == "glow":
                core = 0.5 - abs((ly + 0.5) / h - 0.5)
                c = [GLOW[i] + (GLOW_CORE[i] - GLOW[i]) * min(1, core * 2.2) for i in range(3)]
                n *= 0.3
            elif mat == "copper":
                base = COPPER if (lx + ly) % 3 else (226, 150, 84)  # wound-wire look
                c = list(base)
            elif mat == "rail":
                c = list(DARK if lx % 3 == 0 else STEEL)  # picatinny teeth
            elif mat == "vent":
                c = list(GRIP if ly % 2 == 0 else GUNMETAL)
            elif mat == "ring":
                band = abs((lx + 0.5) / w - 0.5) < 0.2 or abs((ly + 0.5) / h - 0.5) < 0.2
                c = list(GLOW_CORE if band and min(w, h) > 2 else (GLOW if band else DARK))
                n *= 0.3
            elif mat == "armor":
                seam = (w > 6 and lx % 10 == 5) or (h > 6 and ly == h // 2)
                if seam:
                    c = list(SEAM)
                    n *= 0.2
                else:
                    shade = 1.0 - 0.14 * (ly / max(1, h - 1))  # soft top-lit gradient
                    c = [v * shade for v in ARMOR]
                    c[2] = min(255, c[2] + 6)
                    n *= 0.35
            elif mat == "warn":
                c = list(WARN if ((lx + ly) // 2) % 2 == 0 else GRIP)
            else:
                base = {"gunmetal": GUNMETAL, "dark": DARK, "steel": STEEL, "hsss": HSSS, "grip": GRIP, "armor": ARMOR, "white": WHITE}[mat]
                c = list(base)
                if mat in ("gunmetal", "hsss", "armor") and w > 12 and lx % 12 == 11:
                    c = [v * 0.72 for v in c]  # panel seams
                if mat == "grip" and (lx + ly) % 4 == 0:
                    c = [v + 14 for v in c]  # stippling
            if edge_hi and mat not in ("glow", "ring", "armor"):
                c = [v * 1.22 + 6 for v in c]
            elif edge_lo and mat == "armor":
                c = [v * 0.86 for v in c]
            elif edge_lo and mat not in ("glow", "ring"):
                c = [v * 0.7 for v in c]
            px[x, y] = (clamp(c[0] + n), clamp(c[1] + n), clamp(c[2] + n), 255)


# shelf-pack each cube's box-UV footprint: width 2*(dx+dz), height dz+dy
placed = []
order = sorted(range(len(cubes)), key=lambda i: -(cubes[i][2][2] + cubes[i][2][1]))
sx, sy, shelf_h = 0, 0, 0
uv_of = {}
for i in order:
    _, _, (dx, dy, dz), mat = cubes[i]
    w, h = 2 * (dx + dz), dz + dy
    if sx + w > TW:
        sx, sy, shelf_h = 0, sy + shelf_h, 0
    assert sy + h <= TH, "texture atlas overflow"
    uv_of[i] = (sx, sy)
    # top + bottom faces (row of height dz), then the four sides (row of height dy)
    paint_rect(sx + dz, sy, dx, dz, mat)
    paint_rect(sx + dz + dx, sy, dx, dz, mat)
    paint_rect(sx, sy + dz, dz, dy, mat)
    paint_rect(sx + dz, sy + dz, dx, dy, mat)
    paint_rect(sx + dz + dx, sy + dz, dz, dy, mat)
    paint_rect(sx + 2 * dz + dx, sy + dz, dx, dy, mat)
    sx += w
    shelf_h = max(shelf_h, h)

# ---------------- geo.json (Bedrock, Y up) ----------------
bed_cubes = []
for i, (name, (x0, y0, z0), (dx, dy, dz), _) in enumerate(cubes):
    bed_cubes.append({
        "origin": [round(x0, 3), round(-(y0 + dy), 3), round(z0, 3)],
        "size": [dx, dy, dz],
        "uv": list(uv_of[i]),
    })

geo = {
    "format_version": "1.12.0",
    "minecraft:geometry": [{
        "description": {
            "identifier": "geometry.railgun",
            "texture_width": TW,
            "texture_height": TH,
            "visible_bounds_width": 4,
            "visible_bounds_height": 4,
            "visible_bounds_offset": [0, 1, 0],
        },
        "bones": [{"name": "root", "pivot": [0, 0, 0], "cubes": bed_cubes}],
    }],
}
MODEL_OUT.write_text(json.dumps(geo, indent=2) + "\n")
img.save(TEX_OUT)
print(f"{len(cubes)} cubes, atlas used up to y={sy + shelf_h}/{TH}")
