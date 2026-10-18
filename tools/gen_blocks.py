"""Textures of the tiered structure blocks (16x16): Assembly Frames (frame_<tier>.png) and Landing Pads
(pad_<tier>.png sides, pad_top_<tier>.png top). Each tier is made of its signature material and lit in the tier's
accent colour (the same accents as the weapons, tools/gunforge.py).

usage: gen_blocks.py [PREVIEW.png]
"""
import random
import sys
from pathlib import Path

from PIL import Image

OUT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/newgunrizons/textures/blocks"

# tier: (material base colour, accent glow, sparkles?)
TIERS = [
    ("lv", (128, 130, 136), (220, 225, 235), False),  # steel
    ("mv", (150, 190, 215), (255, 140, 60), False),  # aluminium
    ("hv", (196, 198, 210), (255, 220, 60), False),  # stainless steel
    ("ev", (196, 160, 214), (255, 176, 60), False),  # titanium
    ("iv", (92, 92, 130), (40, 200, 255), False),  # tungstensteel
    ("luv", (110, 50, 96), (255, 70, 190), False),  # HSS-S
    ("zpm", (52, 46, 62), (255, 120, 30), False),  # naquadah alloy
    ("uv", (236, 236, 242), (110, 160, 255), False),  # neutronium
    ("uhv", (40, 38, 46), (180, 70, 255), True),  # cosmic neutronium
    ("uev", (226, 226, 236), (80, 90, 255), True),  # infinity
    ("uiv", (34, 46, 52), (40, 230, 200), True),  # transcendent metal
    ("umv", (20, 22, 40), (90, 255, 110), True),  # spacetime
    ("uxv", (16, 8, 26), (220, 80, 255), True),  # universium
    ("max", (230, 220, 240), (255, 205, 70), True),  # magmatter
]


def clamp(v):
    return max(0, min(255, int(v)))


def shade(c, k):
    return tuple(clamp(v * k) for v in c)


def base_plate(px, base, rnd, sparkle):
    for y in range(16):
        for x in range(16):
            n = rnd.uniform(-6, 6)
            c = [v + n for v in base]
            if x == 0 or y == 0:
                c = [v * 1.25 + 8 for v in c]
            elif x == 15 or y == 15:
                c = [v * 0.62 for v in c]
            if sparkle and rnd.random() < 0.05:
                c = [255, 255, 255] if rnd.random() < 0.7 else c
            px[x, y] = tuple(clamp(v) for v in c) + (255,)


def frame(base, glow, sparkle, seed):
    rnd = random.Random(seed)
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    base_plate(px, base, rnd, sparkle)
    # inner recessed panel
    for y in range(3, 13):
        for x in range(3, 13):
            r, g, b, _ = px[x, y]
            px[x, y] = (clamp(r * 0.8), clamp(g * 0.8), clamp(b * 0.8), 255)
    for i in range(3, 13):  # panel bevel
        px[i, 3] = shade(base, 0.55) + (255,)
        px[3, i] = shade(base, 0.55) + (255,)
        px[i, 12] = shade(base, 1.2) + (255,)
        px[12, i] = shade(base, 1.2) + (255,)
    for x, y in ((1, 1), (14, 1), (1, 14), (14, 14)):  # bolts
        px[x, y] = shade(base, 1.45) + (255,)
    for x in range(4, 12):  # glowing accent strip across the panel
        px[x, 7] = glow + (255,)
        px[x, 8] = shade(glow, 0.7) + (255,)
    return img


def pad_side(base, glow, sparkle, seed):
    rnd = random.Random(seed)
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    base_plate(px, shade(base, 0.85), rnd, sparkle)
    for x in range(16):  # lit edge under the deck
        px[x, 1] = glow + (255,)
        px[x, 2] = shade(glow, 0.55) + (255,)
    for y in range(4, 16, 4):  # vents
        for x in range(2, 14):
            if x % 3 != 0:
                px[x, y] = shade(base, 0.45) + (255,)
    return img


def pad_top(base, glow, sparkle, seed):
    rnd = random.Random(seed)
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    deck = shade(base, 0.7)
    base_plate(px, deck, rnd, sparkle)
    for y in range(16):  # anti-slip grid
        for x in range(16):
            if (x + y) % 4 == 0 and 0 < x < 15 and 0 < y < 15:
                r, g, b, _ = px[x, y]
                px[x, y] = (clamp(r * 0.75), clamp(g * 0.75), clamp(b * 0.75), 255)
    for i in range(1, 15):  # landing light strips along two edges, so a ring of pads shows a lit circle
        px[i, 1] = glow + (255,)
        px[1, i] = shade(glow, 0.8) + (255,)
    px[7, 7] = px[8, 8] = glow + (255,)
    px[7, 8] = px[8, 7] = shade(glow, 0.7) + (255,)
    return img


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    tiles = []
    for k, (name, base, glow, sparkle) in enumerate(TIERS):
        for kind, fn in (("frame", frame), ("pad", pad_side), ("pad_top", pad_top)):
            img = fn(base, glow, sparkle, 1000 * k + len(kind))
            img.save(OUT / f"{kind}_{name}.png")
            tiles.append(img)
    if len(sys.argv) > 1:
        sheet = Image.new("RGBA", (3 * 72, len(TIERS) * 72), (40, 44, 52, 255))
        for i, t in enumerate(tiles):
            big = t.resize((64, 64), Image.NEAREST)
            sheet.paste(big, ((i % 3) * 72 + 4, (i // 3) * 72 + 4))
        sheet.save(sys.argv[1])


if __name__ == "__main__":
    main()
