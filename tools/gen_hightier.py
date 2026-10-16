"""Generate high-tier (IV..UHV) weapons for NewGunrizons from existing, already-tuned guns.

Each new gun is a copy of a base gun factory (so all first/third-person positioning
stays correct for that model) with a new name, stats, info text, tier-tinted
texture and, from LuV up, its own magazine/charge and ammo.
"""
import re
import sys
from pathlib import Path

from PIL import Image

ROOT = Path(sys.argv[1])
JAVA = ROOT / "src/main/java/com/gtnewhorizon/newgunrizons"
RES = ROOT / "src/main/resources/assets/newgunrizons"
TEX = RES / "textures/models"
LANG = RES / "lang/en_US.lang"
PKG = "com.gtnewhorizon.newgunrizons.items.factories.guns.hightier"

TIER_COLOR = {
    "IV": (70, 110, 250),
    "LuV": (225, 90, 200),
    "ZPM": (40, 210, 225),
    "UV": (80, 210, 80),
    "UHV": (235, 60, 60),
}

# ---- ammo -------------------------------------------------------------------
# name, display, tier, kind ("bullet" copies BMG50, "rocket" copies SMAWRocket), source texture
BULLETS = [
    ("FerroSlug", "Ferromagnetic Slug", "LuV", "bullet", "bullet.png"),
    ("LaserCharge", "Laser Charge", "ZPM", "bullet", "bullet.png"),
    ("PlasmaCannonCharge", "Plasma Cannon Charge", "ZPM", "rocket", "smaw.png"),
    ("ParticleCharge", "Particle Charge", "UV", "bullet", "bullet.png"),
    ("ExoticCharge", "Exotic Matter Charge", "UHV", "bullet", "bullet.png"),
    ("AntimatterCharge", "Antimatter Charge", "UHV", "rocket", "smaw.png"),
]

# name, display, tier, base magazine, ammo capacity, bullet, source texture
MAGAZINES = [
    ("CoilgunMag", "50rnd Coilgun Slug Magazine", "LuV", "FNP90Mag", 50, "FerroSlug", "p90mag.png"),
    ("GaussRifleMag", "8rnd Gauss Rifle Magazine", "LuV", "AS50Mag", 8, "FerroSlug", "natomag1.png"),
    ("RailgunMag", "6rnd Railgun Magazine", "LuV", "M107BMag", 6, "FerroSlug", "gunmetaltexture.png"),
    ("LaserRifleCell", "60-shot Laser Rifle Cell", "ZPM", "M8A7Mag", 60, "LaserCharge", "ak12.png"),
    ("PlasmaRifleCell", "80-shot Plasma Rifle Cell", "ZPM", "M41AMag", 80, "LaserCharge", "m41amag.png"),
    ("ParticleBeamCell", "10-shot Particle Beam Cell", "UV", "HecateIIMag", 10, "ParticleCharge", "natomag1.png"),
    ("NeutronRifleCell", "150-shot Neutron Rifle Cell", "UV", "M249Mag", 150, "ParticleCharge", "m249.png"),
    ("ExoticMatterCell", "40-shot Exotic Matter Cell", "UHV", "MXMag", 40, "ExoticCharge", "mxmag.png"),
]

# name, display, tier, base factory (relative), base gun texture, damage, fire rate, explosion,
# ammo replacement (old ref -> new ref) or None, type text, ammo text
GUNS = [
    ("XR7", "XR-7 Smart Rifle", "IV", "assault/ScarHFactory", "scar.png", 16.0, None, None,
     None, "Smart assault rifle", "ScarH magazines"),
    ("HX9", "HX-9 Heavy Rifle", "IV", "lmg/Mk48MOD1Factory", "mk48mod1.png", 20.0, None, None,
     None, "Heavy support rifle", "Mk48 belts"),
    ("Coilgun", "Coilgun", "LuV", "smg/FNP90Factory", "ak12.png", 24.0, None, None,
     ("Magazines.FNP90Mag", "Magazines.CoilgunMag"), "Electromagnetic PDW", "50rnd Coilgun Slug Magazine"),
    ("GaussRifle", "Gauss Rifle", "LuV", "sniper/AS50Factory", "as50.png", 75.0, None, None,
     ("Magazines.AS50Mag", "Magazines.GaussRifleMag"), "Electromagnetic marksman rifle", "8rnd Gauss Rifle Magazine"),
    ("Railgun", "Railgun", "LuV", "sniper/M107Factory", "m107.png", 120.0, 0.35, None,
     ("Magazines.M107BMag", "Magazines.RailgunMag"), "Electromagnetic anti-materiel rifle", "6rnd Railgun Magazine"),
    ("LaserRifle", "Laser Rifle", "ZPM", "assault/M8A7Factory", "m8a7.png", 32.0, None, None,
     ("Magazines.M8A7Mag", "Magazines.LaserRifleCell"), "Directed-energy rifle", "60-shot Laser Rifle Cell"),
    ("PlasmaRifle", "Plasma Rifle", "ZPM", "special/M41AFactory", "m41a.png", 38.0, None, None,
     ("Magazines.M41AMag", "Magazines.PlasmaRifleCell"), "Plasma rifle", "80-shot Plasma Rifle Cell"),
    ("PlasmaCannon", "Plasma Cannon", "ZPM", "special/SMAWFactory", "smaw.png", 140.0, None, 4.0,
     ("Bullets.SMAWRocket", "Bullets.PlasmaCannonCharge"), "Plasma cannon", "Plasma Cannon Charge"),
    ("ParticleBeamRifle", "Particle Beam Rifle", "UV", "sniper/HecateIIFactory", "hecateii.png", 220.0, None, None,
     ("Magazines.HecateIIMag", "Magazines.ParticleBeamCell"), "Particle beam rifle", "10-shot Particle Beam Cell"),
    ("NeutronRifle", "Neutron Rifle", "UV", "lmg/M249Factory", "m249.png", 55.0, None, None,
     ("Magazines.M249Mag", "Magazines.NeutronRifleCell"), "Neutron support weapon", "150-shot Neutron Rifle Cell"),
    ("AntimatterCannon", "Antimatter Cannon", "UHV", "special/SMAWFactory", "smaw.png", 400.0, None, 5.0,
     ("Bullets.SMAWRocket", "Bullets.AntimatterCharge"), "Antimatter cannon", "Antimatter Charge"),
    ("ExoticMatterRifle", "Exotic Matter Rifle", "UHV", "assault/MXFactory", "mx.png", 90.0, None, None,
     ("Magazines.MXMag", "Magazines.ExoticMatterCell"), "Exotic matter rifle", "40-shot Exotic Matter Cell"),
]


def tint(src: str, dst: str, tier: str) -> None:
    color = TIER_COLOR[tier]
    img = Image.open(TEX / src).convert("RGBA")
    px = img.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
            tr, tg, tb = (min(255, int(c * (0.35 + lum * 1.1))) for c in color)
            k = 0.6
            px[x, y] = (int(r * (1 - k) + tr * k), int(g * (1 - k) + tg * k), int(b * (1 - k) + tb * k), a)
    img.save(TEX / dst)


def find_block(text: str, start_pat: str, end_token: str) -> str:
    i = text.index(start_pat)
    j = text.index(end_token, i) + len(end_token)
    return text[i:j]


def matching_paren(text: str, open_idx: int) -> int:
    depth, i, in_str = 0, open_idx, False
    while i < len(text):
        c = text[i]
        if in_str:
            if c == "\\":
                i += 1
            elif c == '"':
                in_str = False
        elif c == '"':
            in_str = True
        elif c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError("unbalanced")


def fmt_float(v: float) -> str:
    return f"{v:.1f}F" if v != int(v) else f"{v:.1f}F"


def make_gun(spec) -> str:
    name, display, tier, base, _tex, dmg, rate, expl, repl, typ, ammo = spec
    base_path = JAVA / "items/factories/guns" / (base + ".java")
    src = base_path.read_text()
    base_cls = Path(base).name
    src = re.sub(r"^package [^;]+;", f"package {PKG};", src, count=1, flags=re.M)
    src = src.replace(f"class {base_cls}", f"class {name}Factory")
    src = re.sub(r'\.withName\("[^"]+"\)', f'.withName("{name}")', src, count=1)
    src = re.sub(r'\.withTextureName\("[^"]+"\)', f'.withTextureName("{name}")', src, count=1)
    src = re.sub(r"\.withSpawnEntityDamage\([^)]*\)", f".withSpawnEntityDamage({fmt_float(dmg)})", src, count=1)
    if rate is not None:
        src = re.sub(r"\.withFireRate\([^)]*\)", f".withFireRate({rate}F)", src, count=1)
    if expl is not None:
        src = re.sub(r"\.withSpawnEntityExplosionRadius\([^)]*\)",
                     f".withSpawnEntityExplosionRadius({fmt_float(expl)})", src, count=1)
    src = re.sub(r"\.withCreativeTab\([^)]*\)", ".withCreativeTab(NewGunrizonsMod.HighTierTab)", src, count=1)
    if repl:
        old, new = repl
        src = re.sub(re.escape(old) + r"\b", new, src)
    # info text
    fire = "Semi" if "withMaxShots(1)" in src else ("Auto/Burst/Semi" if "Integer.MAX_VALUE, 3, 1" in src else "Auto")
    lines = [f"Tier: {tier}", f"Type: {typ}", f"Damage: {dmg:g}", f"Ammo: {ammo}", f"Fire Rate: {fire}"]
    if expl:
        lines.insert(3, f"Blast radius: {expl:g}")
    body = ",\n                    ".join(f'"{l}"' for l in lines)
    info = f"(\n                (stack) -> Arrays.asList(\n                    {body}))"
    if ".withInformationProvider(" in src:
        k = src.index(".withInformationProvider(")
        open_idx = src.index("(", k)
        close_idx = matching_paren(src, open_idx)
        src = src[:open_idx] + info + src[close_idx + 1:]
    else:
        src = src.replace(".withCreativeTab(NewGunrizonsMod.HighTierTab)",
                          ".withCreativeTab(NewGunrizonsMod.HighTierTab)\n            .withInformationProvider" + info, 1)
        if "import java.util.Arrays;" not in src:
            src = src.replace(f"package {PKG};\n", f"package {PKG};\n\nimport java.util.Arrays;\n", 1)
    if "import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;" not in src:
        src = src.replace(f"package {PKG};\n", f"package {PKG};\n\nimport com.gtnewhorizon.newgunrizons.NewGunrizonsMod;\n", 1)
    if repl and repl[1].startswith("Bullets.") and "registry.Bullets;" not in src:
        src = src.replace("import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;",
                          "import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;\nimport com.gtnewhorizon.newgunrizons.registry.Bullets;", 1)
    if repl and repl[1].startswith("Magazines.") and "registry.Magazines;" not in src:
        src = src.replace("import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;",
                          "import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;\nimport com.gtnewhorizon.newgunrizons.registry.Magazines;", 1)
    return src


def insert_before_last_brace_of_init(text: str, addition: str) -> str:
    # init() is the last method in Bullets/Magazines/Guns: insert before its closing brace,
    # i.e. before the final "    }\n}" of the file.
    idx = text.rstrip().rfind("    }")
    return text[:idx] + addition + text[idx:]


def add_fields(text: str, type_name: str, names) -> str:
    decl = "".join(f"    public static {type_name} {n};\n" for n in names)
    m = re.search(r"public class \w+ \{\n\n?", text)
    return text[:m.end()] + decl + text[m.end():]


def main() -> None:
    out_dir = JAVA / "items/factories/guns/hightier"
    out_dir.mkdir(parents=True, exist_ok=True)
    lang_lines = ["", "# High-tier weapons (IV-UHV)", "itemGroup.HighTier_tab=NewGunrizons High-Tier Weapons"]

    # bullets
    bpath = JAVA / "registry/Bullets.java"
    btext = bpath.read_text()
    bmg = find_block(btext, "        BMG50 = (new ItemBullet.Builder())", ".build(ItemBullet.class);")
    rocket = find_block(btext, "        SMAWRocket = (new ItemBullet.Builder())", ".build(ItemBullet.class);")
    add = "\n        // ---- High-tier ammo (LuV-UHV) ----\n"
    for name, display, tier, kind, srctex in BULLETS:
        block = bmg if kind == "bullet" else rocket
        old = "BMG50" if kind == "bullet" else "SMAWRocket"
        oldtex = '"Bullet.png"' if kind == "bullet" else '"SMAW.png"'
        nb = block.replace(f"{old} = ", f"{name} = ", 1).replace(f'.withName("{old}")', f'.withName("{name}")', 1)
        assert oldtex in nb, (name, oldtex)
        nb = nb.replace(oldtex, f'"{name}.png"', 1)
        add += nb + "\n"
        tint(srctex, f"{name.lower()}.png", tier)
        lang_lines.append(f"item.newgunrizons_{name}.name={display}")
    btext = add_fields(btext, "ItemBullet", [b[0] for b in BULLETS])
    btext = insert_before_last_brace_of_init(btext, add)
    bpath.write_text(btext)

    # magazines
    mpath = JAVA / "registry/Magazines.java"
    mtext = mpath.read_text()
    add = "\n        // ---- High-tier magazines and cells (LuV-UHV) ----\n"
    for name, display, tier, base, cap, bullet, srctex in MAGAZINES:
        block = find_block(mtext, f"        {base} = (new ItemMagazine.Builder())", ".build(ItemMagazine.class);")
        nb = block.replace(f"{base} = ", f"{name} = ", 1)
        nb = re.sub(r"\.withAmmo\(\d+\)", f".withAmmo({cap})", nb, count=1)
        nb = re.sub(r"\.withCompatibleBullet\([^)]*\)", f".withCompatibleBullet(Bullets.{bullet})", nb, count=1)
        nb = nb.replace(f'.withName("{base}")', f'.withName("{name}")', 1)
        nb = re.sub(r'(new JsonModel\("[^"]+"\), )"[^"]+"', rf'\1"{name}.png"', nb, count=1)
        assert f'"{name}.png"' in nb and f'withName("{name}")' in nb, name
        add += nb + "\n"
        tint(srctex, f"{name.lower()}.png", tier)
        lang_lines.append(f"item.newgunrizons_{name}.name={display}")
    mtext = add_fields(mtext, "ItemMagazine", [m[0] for m in MAGAZINES])
    mtext = insert_before_last_brace_of_init(mtext, add)
    mpath.write_text(mtext)

    # guns
    gpath = JAVA / "registry/Guns.java"
    gtext = gpath.read_text()
    imports = "".join(f"import {PKG}.{g[0]}Factory;\n" for g in GUNS)
    gtext = gtext.replace("\npublic class Guns", "\n" + imports + "\npublic class Guns", 1)
    gtext = add_fields(gtext, "Item", [g[0] for g in GUNS])
    add = "\n        // High-tier weapons (IV-UHV)\n" + "".join(
        f"        {g[0]} = new {g[0]}Factory().createGun();\n" for g in GUNS)
    gtext = insert_before_last_brace_of_init(gtext, add)
    gpath.write_text(gtext)
    for spec in GUNS:
        (out_dir / f"{spec[0]}Factory.java").write_text(make_gun(spec))
        tint(spec[4], f"{spec[0].lower()}.png", spec[2])
        lang_lines.append(f"item.{spec[0]}.name={spec[1]}")

    with LANG.open("a") as f:
        f.write("\n".join(lang_lines) + "\n")
    print("generated", len(GUNS), "guns,", len(MAGAZINES), "magazines,", len(BULLETS), "ammo types")


if __name__ == "__main__":
    main()
