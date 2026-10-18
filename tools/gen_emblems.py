"""Flat 32x32 weapon emblems (textures/items/emblem_<name>.png), rendered from the weapons' own models.

Hand-held weapons are drawn by a 3D item renderer that some GUIs (the BetterQuesting quest book) cannot show, so
quests and other icons use these emblem items instead.

usage: gen_emblems.py [PREVIEW.png]
"""
import re
import sys
from pathlib import Path

from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
import render_geo as rg  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "src/main/resources/assets/newgunrizons"
FACTORIES = ROOT / "src/main/java/com/gtnewhorizon/newgunrizons/items/factories/guns"

# emblem name -> weapon whose model it shows (keep in sync with ItemEmblem.NAMES)
EMBLEMS = ["M1911", "MP40", "M4A1", "XR7", "Railgun", "LaserRifle", "PlasmaCannon", "ParticleBeamRifle",
           "AntimatterCannon", "QuantumPhaseRifle", "GravitonCannon", "ChronoRifle", "DimensionalCutter",
           "RealityEraser"]
SIZE = 32


def model_and_texture(name):
    for f in FACTORIES.rglob("*.java"):
        src = f.read_text()
        if f'.withName("{name}")' not in src:
            continue
        model = re.search(r'WeaponRenderer\.Builder\(\)\)\.withModel\(new JsonModel\("([^"]+)"\)\)', src).group(1)
        texture = re.search(r'\.withTextureName\("([^"]+)"\)', src).group(1).lower() + ".png"
        return ASSETS / "models" / (model + ".geo.json"), ASSETS / "textures/models" / texture
    raise KeyError(name)


def emblem(name):
    model, tex_path = model_and_texture(name)
    roots, tw, th = rg.load(model)
    tex = Image.open(tex_path).convert("RGBA")
    if tex.size != (tw, th):
        tex = tex.resize((tw, th), Image.NEAREST)
    quads = []
    for r in roots:
        rg.collect(r, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0, 0, 0], quads)
    img = rg.render(quads, tex, "side", size=900, transparent=True)
    img = img.crop(img.getbbox()).transpose(Image.FLIP_LEFT_RIGHT)  # barrel to the right
    img = img.rotate(38, resample=Image.BICUBIC, expand=True)  # aimed up-right, like items in hand
    img = img.crop(img.getbbox())
    k = (SIZE - 2) / max(img.width, img.height)
    img = img.resize((max(1, round(img.width * k)), max(1, round(img.height * k))), Image.LANCZOS)
    out = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    out.paste(img, ((SIZE - img.width) // 2, (SIZE - img.height) // 2), img)
    px = out.load()
    for y in range(SIZE):  # hard alpha, brightened a little for small sizes
        for x in range(SIZE):
            r, g, b, a = px[x, y]
            px[x, y] = (min(255, int(r * 1.15)), min(255, int(g * 1.15)), min(255, int(b * 1.15)), 255 if a > 110 else 0)
    seen = set()
    for y in range(SIZE):  # drop specks: tiny parts float apart once scaled down
        for x in range(SIZE):
            if px[x, y][3] == 0 or (x, y) in seen:
                continue
            group, todo = [], [(x, y)]
            seen.add((x, y))
            while todo:
                cx, cy = todo.pop()
                group.append((cx, cy))
                for nx, ny in ((cx + 1, cy), (cx - 1, cy), (cx, cy + 1), (cx, cy - 1)):
                    if 0 <= nx < SIZE and 0 <= ny < SIZE and (nx, ny) not in seen and px[nx, ny][3] > 0:
                        seen.add((nx, ny))
                        todo.append((nx, ny))
            if len(group) < 5:
                for gx, gy in group:
                    px[gx, gy] = (0, 0, 0, 0)
    solid = [[px[x, y][3] > 0 for x in range(SIZE)] for y in range(SIZE)]
    for y in range(SIZE):  # dark outline so the silhouette reads on any background
        for x in range(SIZE):
            if solid[y][x]:
                continue
            if any(0 <= x + dx < SIZE and 0 <= y + dy < SIZE and solid[y + dy][x + dx]
                   for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                px[x, y] = (18, 18, 24, 255)
    return out


def main():
    out_dir = ASSETS / "textures/items"
    imgs = []
    for n in EMBLEMS:
        img = emblem(n)
        img.save(out_dir / f"emblem_{n.lower()}.png")
        imgs.append(img)
    if len(sys.argv) > 1:
        sheet = Image.new("RGBA", (7 * 104, 2 * 104), (200, 180, 140, 255))
        for i, im in enumerate(imgs):
            big = im.resize((96, 96), Image.NEAREST)
            sheet.paste(big, ((i % 7) * 104 + 4, (i // 7) * 104 + 4), big)
        sheet.save(sys.argv[1])


if __name__ == "__main__":
    main()
