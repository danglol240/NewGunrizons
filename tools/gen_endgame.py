"""Generate the UEV-MAX weapons (Quantum, Gravity, Spacetime, Dimensional, Ultimate) the same way gen_hightier.py
generated the IV-UHV ones: each gun is a copy of an already-tuned base gun factory with its own name, model,
magazine/cell and ammo. Run once; afterwards run retier.py to apply the final stats, effects and info text, and
gun_designs.py to build the models.

usage: gen_endgame.py REPO_ROOT
"""
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import gen_hightier as gh  # noqa: E402  (reads REPO_ROOT from argv)

gh.TIER_COLOR.update({
    "UEV": (80, 90, 255),
    "UIV": (40, 230, 200),
    "UMV": (90, 255, 110),
    "UXV": (220, 80, 255),
    "MAX": (255, 205, 70),
})

BULLETS = [
    ("QuantumCharge", "Quantum Charge", "UEV", "bullet", "bullet.png"),
    ("GravitonCharge", "Graviton Charge", "UIV", "bullet", "bullet.png"),
    ("ChronoCharge", "Chrono Charge", "UMV", "bullet", "bullet.png"),
    ("SingularityCharge", "Singularity Charge", "UMV", "rocket", "smaw.png"),
    ("DimensionalCharge", "Dimensional Charge", "UXV", "bullet", "bullet.png"),
    ("InfinityCharge", "Infinity Charge", "MAX", "bullet", "bullet.png"),
]

MAGAZINES = [
    ("QuantumCell", "Quantum Cell", "UEV", "NATOMag1", 24, "QuantumCharge", "natomag1.png"),
    ("GravitonCore", "Graviton Core", "UIV", "Mag75rnd762x39", 12, "GravitonCharge", "gunmetaltexture.png"),
    ("TimeCrystal", "Time Crystal", "UMV", "L115Mag", 10, "ChronoCharge", "ak12.png"),
    ("DimensionalCore", "Dimensional Core", "UXV", "DragunovMag", 8, "DimensionalCharge", "gunmetaltexture.png"),
    ("InfinityCatalyst", "Infinity Catalyst", "MAX", "SV98Mag", 3, "InfinityCharge", "ak12.png"),
]

# same layout as gen_hightier.GUNS; base model name last
GUNS = [
    ("QuantumPhaseRifle", "Quantum Phase Rifle", "UEV", "assault/AUGA3Factory", "auga3.png", 350.0, 0.075, None,
     ("Magazines.NATOMag1", "Magazines.QuantumCell"), "Quantum phase rifle", "Quantum Cell", "auga3"),
    ("GravitonCannon", "Graviton Cannon", "UIV", "lmg/RPK74MFactory", "rpk74m.png", 400.0, 0.05, None,
     ("Magazines.Mag75rnd762x39", "Magazines.GravitonCore"), "Graviton cannon", "Graviton Core", "rpk74m"),
    ("ChronoRifle", "Chrono Rifle", "UMV", "sniper/L96Factory", "l96.png", 450.0, 0.06, None,
     ("Magazines.L115Mag", "Magazines.TimeCrystal"), "Time manipulation rifle", "Time Crystal", "l96"),
    ("SingularityCannon", "Singularity Cannon", "UMV", "special/SMAWFactory", "smaw.png", 250.0, None, 0.0,
     ("Bullets.SMAWRocket", "Bullets.SingularityCharge"), "Singularity launcher", "Singularity Charge", "smaw"),
    ("DimensionalCutter", "Dimensional Cutter", "UXV", "sniper/DragonuvFactory", "dragunov.png", 600.0, 0.06, None,
     ("Magazines.DragunovMag", "Magazines.DimensionalCore"), "Dimensional cutter", "Dimensional Core", "dragunov"),
    ("RealityEraser", "Reality Eraser", "MAX", "sniper/SV98Factory", "sv98.png", 1000.0, 0.025, None,
     ("Magazines.SV98Mag", "Magazines.InfinityCatalyst"), "Reality manipulation", "Infinity Catalyst", "sv98"),
]


def main() -> None:
    java, out_dir = gh.JAVA, gh.JAVA / "items/factories/guns/hightier"
    lang = ["", "# UEV-MAX weapons"]

    bpath = java / "registry/Bullets.java"
    btext = bpath.read_text()
    bmg = gh.find_block(btext, "        BMG50 = (new ItemBullet.Builder())", ".build(ItemBullet.class);")
    rocket = gh.find_block(btext, "        SMAWRocket = (new ItemBullet.Builder())", ".build(ItemBullet.class);")
    add = "\n        // ---- UEV-MAX ammo ----\n"
    for name, display, tier, kind, srctex in BULLETS:
        block, old = (bmg, "BMG50") if kind == "bullet" else (rocket, "SMAWRocket")
        oldtex = '"Bullet.png"' if kind == "bullet" else '"SMAW.png"'
        nb = block.replace(f"{old} = ", f"{name} = ", 1).replace(f'.withName("{old}")', f'.withName("{name}")', 1)
        assert oldtex in nb, (name, oldtex)
        add += nb.replace(oldtex, f'"{name}.png"', 1) + "\n"
        gh.tint(srctex, f"{name.lower()}.png", tier)
        lang.append(f"item.newgunrizons_{name}.name={display}")
    btext = gh.add_fields(btext, "ItemBullet", [b[0] for b in BULLETS])
    bpath.write_text(gh.insert_before_last_brace_of_init(btext, add))

    mpath = java / "registry/Magazines.java"
    mtext = mpath.read_text()
    add = "\n        // ---- UEV-MAX cells ----\n"
    for name, display, tier, base, cap, bullet, srctex in MAGAZINES:
        block = gh.find_block(mtext, f"        {base} = (new ItemMagazine.Builder())", ".build(ItemMagazine.class);")
        nb = block.replace(f"{base} = ", f"{name} = ", 1)
        nb = re.sub(r"\.withAmmo\(\d+\)", f".withAmmo({cap})", nb, count=1)
        nb = re.sub(r"\.withCompatibleBullet\([^)]*\)", f".withCompatibleBullet(Bullets.{bullet})", nb, count=1)
        nb = nb.replace(f'.withName("{base}")', f'.withName("{name}")', 1)
        nb = re.sub(r'(new JsonModel\("[^"]+"\), )"[^"]+"', rf'\1"{name}.png"', nb, count=1)
        assert f'"{name}.png"' in nb and f'withName("{name}")' in nb, name
        add += nb + "\n"
        gh.tint(srctex, f"{name.lower()}.png", tier)
        lang.append(f"item.newgunrizons_{name}.name={display}")
    mtext = gh.add_fields(mtext, "ItemMagazine", [m[0] for m in MAGAZINES])
    mpath.write_text(gh.insert_before_last_brace_of_init(mtext, add))

    gpath = java / "registry/Guns.java"
    gtext = gpath.read_text()
    imports = "".join(f"import {gh.PKG}.{g[0]}Factory;\n" for g in GUNS)
    gtext = gtext.replace("\npublic class Guns", "\n" + imports + "\npublic class Guns", 1)
    gtext = gh.add_fields(gtext, "Item", [g[0] for g in GUNS])
    add = "\n        // UEV-MAX weapons\n" + "".join(f"        {g[0]} = new {g[0]}Factory().createGun();\n" for g in GUNS)
    gpath.write_text(gh.insert_before_last_brace_of_init(gtext, add))

    for spec in GUNS:
        src = gh.make_gun(spec[:11])
        name, base_model = spec[0], spec[11]
        old_model = f'.withModel(new JsonModel("weapon/{base_model}"))'
        assert src.count(old_model) == 1, (name, src.count(old_model))
        src = src.replace(old_model, f'.withModel(new JsonModel("weapon/{name.lower()}"))')
        src, n = re.subn(r"\.withMaxShots\([^)]*\)", ".withMaxShots(1)", src, count=1)
        assert n == 1, name
        (out_dir / f"{name}Factory.java").write_text(src)
        lang.append(f"item.{name}.name={spec[1]}")

    with gh.LANG.open("a") as f:
        f.write("\n".join(lang) + "\n")
    print("generated", len(GUNS), "guns,", len(MAGAZINES), "cells,", len(BULLETS), "ammo types")


if __name__ == "__main__":
    main()
