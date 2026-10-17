"""Shared toolkit for building NewGunrizons weapon models procedurally.

A model is a list of axis-aligned cubes authored in Minecraft Java model space
(Y points down, the barrel points towards -Z), each tagged with a material.
`Gun.save()` packs the cubes' box-UV footprints into a texture atlas, paints
every face in the material's style using the tier palette, and writes the
Bedrock .geo.json that JsonModel loads plus the matching PNG.

Cube sizes are whole numbers because JsonModel rounds them; positions may be
fractional.
"""
import json
import random
from pathlib import Path

from PIL import Image

ASSETS = Path(__file__).resolve().parent.parent / "src/main/resources/assets/newgunrizons"

# Shared, tier-independent colours
FRAME = (52, 56, 74)  # graphite frame
DARK = (30, 32, 44)
GRIP = (36, 34, 46)
ARMOR = (226, 232, 242)  # pearl-white shell
WHITE = (246, 248, 252)
STEEL = (170, 182, 200)
WARN = (255, 206, 48)

# Per-tier accent: glow colour, glow core, and the tier's signature metal
TIERS = {
    "EV": {"glow": (255, 176, 60), "core": (255, 236, 200), "metal": (150, 150, 170)},  # titanium
    "IV": {"glow": (40, 200, 255), "core": (210, 245, 255), "metal": (112, 116, 150)},  # tungstensteel
    "LuV": {"glow": (255, 70, 190), "core": (255, 215, 240), "metal": (178, 132, 226)},  # HSS-S
    "ZPM": {"glow": (255, 120, 30), "core": (255, 225, 190), "metal": (74, 60, 96)},  # naquadah alloy
    "UV": {"glow": (110, 160, 255), "core": (225, 235, 255), "metal": (206, 208, 220)},  # neutronium
    "UHV": {"glow": (180, 70, 255), "core": (240, 215, 255), "metal": (44, 34, 64)},  # cosmic neutronium
    "UEV": {"glow": (80, 90, 255), "core": (215, 220, 255), "metal": (36, 40, 70)},  # infinity
    "UIV": {"glow": (40, 230, 200), "core": (205, 255, 245), "metal": (40, 56, 64)},  # transcendent metal
    "UMV": {"glow": (90, 255, 110), "core": (225, 255, 225), "metal": (40, 58, 44)},  # spacetime
    "UXV": {"glow": (220, 80, 255), "core": (250, 220, 255), "metal": (26, 14, 40)},  # magnetohydrodynamic matter
    "MAX": {"glow": (255, 205, 70), "core": (255, 250, 225), "metal": (236, 232, 220)},  # universium
}


def _clamp(v):
    return max(0, min(255, int(v)))


class Gun:
    def __init__(self, name, tier, tex_w=512, tex_h=256, seed=1707):
        self.name = name
        self.tier = TIERS[tier]
        self.tw, self.th = tex_w, tex_h
        self.cubes = []
        self.rnd = random.Random(seed)

    # ---------------- geometry ----------------
    def box(self, x0, y0, z0, x1, y1, z1, mat):
        if x1 <= x0 or y1 <= y0 or z1 <= z0:
            raise ValueError(f"{self.name}: inverted cube {(x0, y0, z0, x1, y1, z1)}")
        # JsonModel rounds sizes; thin parts (< 0.5) become one pixel thick
        dx, dy, dz = max(1, round(x1 - x0)), max(1, round(y1 - y0)), max(1, round(z1 - z0))
        self.cubes.append(((x0, y0, z0), (dx, dy, dz), mat))

    def tube(self, cx, cy, z0, z1, half, mat, half_y=None):
        """Square-section tube centred on (cx, cy) along Z."""
        hy = half if half_y is None else half_y
        self.box(cx - half, cy - hy, z0, cx + half, cy + hy, z1, mat)

    def rings(self, cx, cy, z0, z1, step, half, depth=2, mat="ring", collar=True):
        """Evenly spaced rings around an axis, optionally with a dark collar behind each."""
        z = z0
        while z + depth <= z1:
            self.tube(cx, cy, z, z + depth, half, mat)
            if collar:
                self.tube(cx, cy, z + depth, z + depth + 1, half - 1, "dark")
            z += step

    def fins(self, x0, y0, x1, y1, z0, z1, step, mat="metal"):
        z = z0
        while z + 1 <= z1:
            self.box(x0, y0, z, x1, y1, z + 1, mat)
            z += step

    # ---------------- texture ----------------
    def _paint(self, px, x0, y0, w, h, mat):
        t = self.tier
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w):
                lx, ly = x - x0, y - y0
                n = self.rnd.uniform(-7, 7)
                edge_hi = lx == 0 or ly == 0
                edge_lo = lx == w - 1 or ly == h - 1
                emissive = mat in ("glow", "ring", "lens")
                if mat == "glow":
                    k = min(1.0, (0.5 - abs((ly + 0.5) / h - 0.5)) * 2.2)
                    c = [t["glow"][i] + (t["core"][i] - t["glow"][i]) * k for i in range(3)]
                    n *= 0.3
                elif mat == "lens":
                    k = min(1.0, (0.5 - abs((lx + 0.5) / w - 0.5)) + (0.5 - abs((ly + 0.5) / h - 0.5)))
                    c = [t["glow"][i] * 0.6 + (t["core"][i] - t["glow"][i] * 0.6) * k for i in range(3)]
                    n *= 0.2
                elif mat == "ring":
                    band = abs((lx + 0.5) / w - 0.5) < 0.2 or abs((ly + 0.5) / h - 0.5) < 0.2
                    c = list(t["core"] if band and min(w, h) > 2 else (t["glow"] if band else DARK))
                    n *= 0.3
                elif mat == "armor":
                    seam = (w > 6 and lx % 10 == 5) or (h > 6 and ly == h // 2)
                    if seam:
                        c = list(t["glow"])
                        n *= 0.2
                    else:
                        shade = 1.0 - 0.14 * (ly / max(1, h - 1))
                        c = [v * shade for v in ARMOR]
                        c[2] = min(255, c[2] + 6)
                        n *= 0.35
                elif mat == "metal":
                    c = list(t["metal"])
                    if w > 12 and lx % 12 == 11:
                        c = [v * 0.72 for v in c]
                elif mat == "cosmic":  # dark field with scattered stars (cosmic neutronium)
                    c = list(t["metal"])
                    if self.rnd.random() < 0.06:
                        c = list(WHITE if self.rnd.random() < 0.7 else t["glow"])
                    n *= 0.4
                elif mat == "rail":
                    c = list(DARK if lx % 3 == 0 else STEEL)
                elif mat == "vent":
                    c = list(GRIP if ly % 2 == 0 else FRAME)
                elif mat == "warn":
                    c = list(WARN if ((lx + ly) // 2) % 2 == 0 else GRIP)
                elif mat == "grip":
                    c = [v + (14 if (lx + ly) % 4 == 0 else 0) for v in GRIP]
                else:
                    c = list({"frame": FRAME, "dark": DARK, "steel": STEEL, "white": WHITE}[mat])
                    if mat == "frame" and w > 12 and lx % 12 == 11:
                        c = [v * 0.72 for v in c]
                if not emissive:
                    if edge_hi and mat != "armor":
                        c = [v * 1.22 + 6 for v in c]
                    elif edge_lo:
                        c = [v * (0.86 if mat == "armor" else 0.7) for v in c]
                px[x, y] = (_clamp(c[0] + n), _clamp(c[1] + n), _clamp(c[2] + n), 255)

    # ---------------- output ----------------
    def save(self):
        img = Image.new("RGBA", (self.tw, self.th), (0, 0, 0, 0))
        px = img.load()
        order = sorted(range(len(self.cubes)), key=lambda i: -(self.cubes[i][1][2] + self.cubes[i][1][1]))
        sx = sy = shelf_h = 0
        uv = {}
        for i in order:
            (dx, dy, dz), mat = self.cubes[i][1], self.cubes[i][2]
            w, h = 2 * (dx + dz), dz + dy
            if w > self.tw:
                raise ValueError(f"{self.name}: cube too long for the atlas")
            if sx + w > self.tw:
                sx, sy, shelf_h = 0, sy + shelf_h, 0
            if sy + h > self.th:
                raise ValueError(f"{self.name}: texture atlas overflow")
            uv[i] = (sx, sy)
            self._paint(px, sx + dz, sy, dx, dz, mat)
            self._paint(px, sx + dz + dx, sy, dx, dz, mat)
            self._paint(px, sx, sy + dz, dz, dy, mat)
            self._paint(px, sx + dz, sy + dz, dx, dy, mat)
            self._paint(px, sx + dz + dx, sy + dz, dz, dy, mat)
            self._paint(px, sx + 2 * dz + dx, sy + dz, dx, dy, mat)
            sx += w
            shelf_h = max(shelf_h, h)
        cubes = [
            {"origin": [round(o[0], 3), round(-(o[1] + s[1]), 3), round(o[2], 3)], "size": list(s), "uv": list(uv[i])}
            for i, (o, s, _) in enumerate(self.cubes)
        ]
        key = self.name.lower()
        geo = {
            "format_version": "1.12.0",
            "minecraft:geometry": [{
                "description": {
                    "identifier": f"geometry.{key}",
                    "texture_width": self.tw,
                    "texture_height": self.th,
                    "visible_bounds_width": 4,
                    "visible_bounds_height": 4,
                    "visible_bounds_offset": [0, 1, 0],
                },
                "bones": [{"name": "root", "pivot": [0, 0, 0], "cubes": cubes}],
            }],
        }
        model = ASSETS / "models/weapon" / f"{key}.geo.json"
        tex = ASSETS / "textures/models" / f"{key}.png"
        model.write_text(json.dumps(geo, indent=2) + "\n")
        img.save(tex)
        return model, tex, len(self.cubes), sy + shelf_h
