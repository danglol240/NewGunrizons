"""Contact sheet of every vehicle with its turrets in place (the in-game layout, guns pointing forward).

usage: preview_vehicles.py OUT.png
"""
import json
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).resolve().parent))
import render_geo  # noqa: E402
from gunforge import ASSETS  # noqa: E402

NAMES = ["FieldJeep", "OffroadTruck", "ArmoredAPC", "BattleTank", "HoverJeep", "TiltrotorVTOL", "PlasmaFighter",
         "ParticleCruiser", "AntimatterFrigate", "QuantumRingship", "GravityTank", "SpacetimeSkimmer",
         "DimensionalLeviathan", "GenesisArk"]
TIERS = ["LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX"]


def part(path, offset, atlas, ox):
    roots, tw, th = render_geo.load(path)
    quads = []
    for r in roots:
        render_geo.collect(r, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0, 0, 0], quads)
    out = []
    for world, (u0, v0, u1, v1) in quads:
        out.append(([[p[0] + offset[0], p[1] + offset[1], p[2] + offset[2]] for p in world],
                    (u0 + ox, v0, u1 + ox, v1)))
    return out


def vehicle(name):
    key = "vehicle_" + name.lower()
    lay = json.loads((ASSETS / "vehicles" / f"{key}.json").read_text())
    pieces = [(ASSETS / "models/vehicle" / f"{key}.geo.json", (0, 0, 0), ASSETS / "textures/models" / f"{key}.png")]
    for i, t in enumerate(lay["turrets"]):
        if t["turret"]:
            pieces.append((ASSETS / "models/vehicle" / f"{key}_t{i}.geo.json", (t["x"], t["y"], t["z"]),
                           ASSETS / "textures/models" / f"{key}_t{i}.png"))
        if t["gun"]:
            pieces.append((ASSETS / "models/vehicle" / f"{key}_g{i}.geo.json",
                           (t["x"] + t["gx"], t["y"] + t["gy"], t["z"] + t["gz"]),
                           ASSETS / "textures/models" / f"{key}_g{i}.png"))
    texs = [Image.open(p[2]).convert("RGBA") for p in pieces]
    atlas = Image.new("RGBA", (sum(t.width for t in texs), max(t.height for t in texs)), (0, 0, 0, 0))
    quads, ox = [], 0
    for (model, off, _), tex in zip(pieces, texs):
        atlas.paste(tex, (ox, 0))
        quads += part(model, off, atlas, ox)
        ox += tex.width
    return render_geo.render(quads, atlas, "three_quarter", size=600)


def main(out):
    imgs = [vehicle(n) for n in NAMES]
    w, h = imgs[0].width, imgs[0].height
    cols = 4
    sheet = Image.new("RGB", (w * cols, h * ((len(imgs) + cols - 1) // cols)), (30, 34, 42))
    d = ImageDraw.Draw(sheet)
    for i, im in enumerate(imgs):
        x, y = (i % cols) * w, (i // cols) * h
        sheet.paste(im, (x, y))
        d.text((x + 8, y + 6), f"{TIERS[i]}  {NAMES[i]}", fill=(255, 220, 120))
    sheet.save(out)


if __name__ == "__main__":
    main(sys.argv[1])
