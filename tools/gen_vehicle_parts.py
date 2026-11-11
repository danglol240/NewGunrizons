"""16x16 icons of the vehicle parts (textures/items/vehicle_part_<kind>_<tier>.png): chassis, armour, engine,
control system and drive, tinted with each tier's metal and glow colours (tools/gunforge.py).

usage: gen_vehicle_parts.py [PREVIEW.png]
"""
import sys
from pathlib import Path

from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
import gunforge  # noqa: E402
import vehicle_designs  # noqa: E402,F401  (adds the LV/MV/HV palettes)

OUT = gunforge.ASSETS / "textures/items"
TIERS = ["LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX"]
OUTLINE = (20, 20, 28, 255)
DRIVE = {1: "wheel", 2: "wheel", 3: "wheel", 4: "track", 5: "hover", 6: "rotor", 11: "hover"}


def shade(c, k):
    return tuple(max(0, min(255, int(v * k))) for v in c) + (255,)


class Icon:
    def __init__(self, metal, glow):
        self.img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        self.p = self.img.load()
        self.metal, self.glow = metal, glow

    def px(self, x, y, c):
        if 0 <= x < 16 and 0 <= y < 16:
            self.p[x, y] = c

    def rect(self, x0, y0, x1, y1, c, lit=True):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                k = 1.0
                if lit:
                    k = 1.25 if (y == y0 or x == x0) else 0.7 if (y == y1 or x == x1) else 1.0
                self.px(x, y, shade(c, k))

    def outline(self):
        src = self.img.copy().load()
        for y in range(16):
            for x in range(16):
                if src[x, y][3] == 0 and any(
                        0 <= x + dx < 16 and 0 <= y + dy < 16 and src[x + dx, y + dy][3] > 0 and
                        src[x + dx, y + dy] != OUTLINE for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                    self.p[x, y] = OUTLINE


def chassis(i):
    for y in (4, 11):
        i.rect(1, y, 14, y + 1, i.metal)
    for x in (2, 7, 12):
        i.rect(x, 3, x + 1, 13, i.metal)
    i.rect(5, 7, 10, 8, i.glow, lit=False)


def armor(i):
    i.rect(2, 2, 13, 12, i.metal)
    for x in range(4, 12):
        i.px(x, 13, shade(i.metal, 0.6))
    for x in range(6, 10):
        i.px(x, 14, shade(i.metal, 0.5))
    i.rect(3, 6, 12, 6, i.glow, lit=False)
    for x, y in ((4, 4), (11, 4), (4, 10), (11, 10)):
        i.px(x, y, shade((210, 214, 224), 1.0))


def engine(i):
    i.rect(2, 6, 12, 13, i.metal)
    for x in (3, 6, 9):
        i.rect(x, 2, x + 2, 6, shade(i.metal, 0.85)[:3])
    i.rect(13, 8, 14, 11, (60, 62, 72))
    i.rect(4, 9, 10, 10, i.glow, lit=False)


def control(i):
    i.rect(1, 3, 14, 11, (54, 58, 70))
    i.rect(3, 5, 12, 9, i.glow, lit=False)
    for x in range(4, 12, 2):
        i.px(x, 7, shade(i.glow, 1.4))
    i.rect(6, 12, 9, 13, i.metal)
    i.rect(4, 14, 11, 14, i.metal)


def drive(i, kind):
    if kind == "wheel":
        for y in range(16):
            for x in range(16):
                d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
                if d < 7:
                    self_c = (34, 34, 38) if d > 4 else i.metal if d > 1.5 else i.glow
                    i.px(x, y, shade(self_c, 1.15 if x + y < 14 else 0.85))
    elif kind == "track":
        i.rect(1, 5, 14, 11, (40, 40, 46))
        for x in (3, 7, 11):
            for y in range(6, 11):
                for xx in range(x, x + 3):
                    if ((xx - x - 1) ** 2 + (y - 8) ** 2) <= 3:
                        i.px(xx, y, shade(i.metal, 1.0))
        for x in range(1, 15, 2):
            i.px(x, 4, shade(i.metal, 0.8))
            i.px(x, 12, shade(i.metal, 0.8))
    elif kind == "hover":
        i.rect(2, 4, 13, 8, i.metal)
        for y in range(9, 14):
            w = 6 - (y - 9)
            for x in range(8 - w, 8 + w):
                i.px(x, y, shade(i.glow, 1.0 - 0.12 * (y - 9)))
    elif kind == "rotor":
        i.rect(7, 2, 8, 13, i.metal)
        i.rect(2, 7, 13, 8, i.metal)
        i.rect(6, 6, 9, 9, (60, 62, 72))
        i.px(7, 7, shade(i.glow, 1.0))
        i.px(8, 8, shade(i.glow, 1.0))
    else:  # thruster
        i.rect(3, 2, 12, 8, i.metal)
        for y in range(9, 15):
            w = 4 + (y - 9) // 2
            for x in range(8 - w, 8 + w):
                if y < 11:
                    i.px(x, y, shade(i.metal, 0.75))
                else:
                    i.px(x, y, shade(i.glow, 1.1 - 0.1 * (y - 11)))


def main(argv):
    sheet = Image.new("RGBA", (16 * 14, 16 * 5), (36, 40, 50, 255))
    for t, name in enumerate(TIERS, start=1):
        pal = gunforge.TIERS[name]
        for k, kind in enumerate(("chassis", "armor", "engine", "control", "drive")):
            i = Icon(pal["metal"], pal["glow"])
            if kind == "drive":
                drive(i, DRIVE.get(t, "thruster"))
            else:
                globals()[kind](i)
            i.outline()
            i.img.save(OUT / f"vehicle_part_{kind}_{t}.png")
            sheet.alpha_composite(i.img, (16 * (t - 1), 16 * k))
    if argv:
        sheet.resize((sheet.width * 4, sheet.height * 4), Image.NEAREST).save(argv[0])


if __name__ == "__main__":
    main(sys.argv[1:])
