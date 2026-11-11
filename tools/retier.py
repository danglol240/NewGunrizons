"""Apply the 13-tier design to the EV-MAX weapons: tier, damage, projectile effect, firing mechanics (WeaponBehavior)
and tooltip of every high-tier gun factory, the rechargeable-cell flags of the energy cells, and the tier tint of
their magazine/ammo textures. Idempotent: re-running replaces what an earlier run wrote.

usage: retier.py REPO_ROOT
"""
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import gen_hightier as gh  # noqa: E402  (reads REPO_ROOT from argv)

HT = gh.JAVA / "items/factories/guns/hightier"

TIER_RGB = {
    "EV": 0xFFB03C, "IV": 0x28C8FF, "LuV": 0xFF46BE, "ZPM": 0xFF781E, "UV": 0x6EA0FF, "UHV": 0xB446FF,
    "UEV": 0x505AFF, "UIV": 0x28E6C8, "UMV": 0x5AFF6E, "UXV": 0xDC50FF, "MAX": 0xFFCD46,
}
gh.TIER_COLOR.update({k: ((v >> 16) & 255, (v >> 8) & 255, v & 255) for k, v in TIER_RGB.items()})
TIER_INDEX = {"EV": 4, "IV": 5, "LuV": 6, "ZPM": 7, "UV": 8, "UHV": 9, "UEV": 10, "UIV": 11, "UMV": 12, "UXV": 13,
              "MAX": 14}


class G:
    def __init__(self, name, tier, typ, damage, style, scale, ammo, behavior="", effects=(), gravity=None):
        self.name, self.tier, self.typ, self.damage = name, tier, typ, damage
        self.style, self.scale, self.ammo = style, scale, ammo
        self.behavior, self.effects, self.gravity = behavior, list(effects), gravity


GUNS = [
    G("XR7", "EV", "Rail-assisted smart rifle", 16, "TRACER", 1.0, "ScarH magazines",
      ".pierce(1, 0.5F)", ["Rail-assisted: pierces 1 extra target"]),
    G("HX9", "EV", "Rail-assisted heavy rifle", 20, "TRACER", 1.4, "Mk48 belts",
      ".pierce(1, 0.5F)", ["Rail-assisted: pierces 1 extra target"]),
    G("Coilgun", "IV", "Electromagnetic PDW", 30, "RAIL", 0.6, "Coilgun Slug Magazine",
      ".pierce(1, 0.6F)", ["Pierces 1 extra target"]),
    G("GaussRifle", "IV", "Gauss marksman rifle", 80, "RAIL", 1.0, "Gauss Rifle Magazine",
      ".pierce(3, 0.75F)", ["Pierces up to 3 extra targets, -25% damage each"]),
    G("Railgun", "IV", "Handheld railgun", 120, "RAIL", 1.6, "Railgun Magazine (tungsten slugs)",
      ".pierce(6, 0.8F)", ["Pierces up to 6 extra targets, -20% damage each"], gravity=0.0),
    G("LaserRifle", "LuV", "Laser rifle", 60, "LASER", 1.0, "Laser Rifle Cell",
      ".hitscan(96.0F).heat(7.0F, 100.0F, 2.5F)", ["Hitscan: instant hit, no drop, 96 m", "Overheats under sustained fire"],
      gravity=0.0),
    G("PlasmaRifle", "ZPM", "Plasma rifle", 50, "PLASMA", 1.0, "Plasma Rifle Cell",
      ".burn(4).knockback(0.6F).armorPiercing(0.3F)", ["Sets targets on fire, knockback", "Armor penetration 30%"],
      gravity=0.0),
    G("PlasmaCannon", "ZPM", "Plasma cannon", 150, "PLASMA", 2.2, "Plasma Cannon Charge",
      ".burn(6).knockback(2.0F).armorPiercing(0.3F)", ["Plasma blast: fire and heavy knockback",
                                                       "Armor penetration 30%"]),
    G("ParticleBeamRifle", "UV", "Particle accelerator rifle", 200, "HELIX", 1.0, "Particle Beam Cell",
      ".pierce(4, 0.85F).armorPiercing(0.75F)", ["Pierces up to 4 extra targets", "Armor penetration 75%"],
      gravity=0.0),
    G("NeutronRifle", "UV", "Neutron support weapon", 70, "PULSE", 1.0, "Neutron Rifle Cell",
      ".pierce(2, 0.7F).armorPiercing(0.5F)", ["Pierces up to 2 extra targets", "Armor penetration 50%"],
      gravity=0.0),
    G("AntimatterCannon", "UHV", "Antimatter cannon", 400, "ORB_RING", 1.8, "Antimatter Charge",
      ".radiation(3).armorPiercing(0.5F)", ["Annihilation blast", "Leaves targets irradiated",
                                            "Armor penetration 50%"]),
    G("ExoticMatterRifle", "UHV", "Exotic matter rifle", 110, "SHARDS", 1.0, "Exotic Matter Cell",
      ".radiation(1).armorPiercing(0.4F)", ["Irradiates targets", "Armor penetration 40%"], gravity=0.0),
    G("QuantumPhaseRifle", "UEV", "Quantum phase rifle", 350, "PHASE", 1.0, "Quantum Cell",
      ".phase(12).armorPiercing(0.6F)", ["Phases through up to 12 blocks", "Armor penetration 60%"], gravity=0.0),
    G("GravitonCannon", "UIV", "Graviton cannon", 400, "GRAVITON", 1.2, "Graviton Core",
      ".field(FieldKind.GRAVITY_WELL, 7.0F, 60, 8.0F).altField(FieldKind.REPULSE, 7.0F, 10, 0.0F)",
      ["Creates a gravity well that pulls mobs in", "Sneak while firing: repulse"], gravity=0.0),
    G("ChronoRifle", "UMV", "Time manipulation rifle", 450, "CHRONO", 1.0, "Time Crystal",
      ".chrono(120).field(FieldKind.CHRONO, 4.0F, 100, 0.0F)",
      ["Freezes the target's time (6 s)", "Leaves a 90% slow time field"], gravity=0.0),
    G("SingularityCannon", "UMV", "Singularity launcher", 250, "SINGULARITY", 1.6, "Singularity Charge",
      ".field(FieldKind.SINGULARITY, 10.0F, 120, 12.0F)",
      ["Opens a singularity for 6 s", "Pulls in mobs and items, crushing them"], gravity=0.0),
    G("DimensionalCutter", "UXV", "Dimensional cutter", 600, "RIFT", 1.0, "Dimensional Core",
      ".hitscan(50.0F).throughBlocks().pierce(64, 1.0F).armorPiercing(1.0F)",
      ["Cuts through space: hits everything on a 50 m line", "Ignores walls and armor"], gravity=0.0),
    G("RealityEraser", "MAX", "Reality manipulation", 1000, "ERASE", 1.0, "Infinity Catalyst",
      ".hitscan(96.0F).eraser().armorPiercing(1.0F).heat(100.0F, 100.0F, 0.1667F)",
      ["Erases the target from reality", "Bosses and players take 1000 damage instead", "30 s cooldown"],
      gravity=0.0),
]

ENERGY_CELLS = {
    "LaserRifleCell": "LuV", "PlasmaRifleCell": "ZPM", "ParticleBeamCell": "UV", "NeutronRifleCell": "UV",
    "ExoticMatterCell": "UHV", "QuantumCell": "UEV", "GravitonCore": "UIV", "TimeCrystal": "UMV",
    "DimensionalCore": "UXV", "InfinityCatalyst": "MAX",
}

# texture re-tints for the earlier generated magazines and ammo: name -> (tier, source texture)
RETINT = {
    "FerroSlug": ("IV", "bullet.png"), "LaserCharge": ("LuV", "bullet.png"), "PlasmaCharge": ("ZPM", "bullet.png"),
    "PlasmaCannonCharge": ("ZPM", "smaw.png"), "ParticleCharge": ("UV", "bullet.png"),
    "ExoticCharge": ("UHV", "bullet.png"), "AntimatterCharge": ("UHV", "smaw.png"),
    "CoilgunMag": ("IV", "p90mag.png"), "GaussRifleMag": ("IV", "natomag1.png"),
    "RailgunMag": ("IV", "gunmetaltexture.png"), "LaserRifleCell": ("LuV", "ak12.png"),
    "PlasmaRifleCell": ("ZPM", "m41amag.png"), "ParticleBeamCell": ("UV", "natomag1.png"),
    "NeutronRifleCell": ("UV", "m249.png"), "ExoticMatterCell": ("UHV", "mxmag.png"),
}


def remove_call(src, call):
    """Remove every `.call(...)` (with balanced parentheses) from src."""
    while True:
        k = src.find(f".{call}(")
        if k < 0:
            return src
        close = gh.matching_paren(src, src.index("(", k))
        start = k
        while start > 0 and src[start - 1] in " \n":
            start -= 1
        src = src[:start] + src[close + 1:]


def ensure_import(src, cls):
    line = f"import {cls};\n"
    if line in src:
        return src
    anchor = "import com.gtnewhorizon.newgunrizons.items.ItemWeapon;\n"
    assert anchor in src
    return src.replace(anchor, anchor + line, 1)


def patch_gun(g: G) -> None:
    path = HT / f"{g.name}Factory.java"
    src = path.read_text()
    src = remove_call(src, "withProjectile")
    src = remove_call(src, "withBehavior")
    if TIER_INDEX[g.tier] >= TIER_INDEX["IV"]:
        src = remove_call(src, "withShellCasingEjectEnabled")
    src = ensure_import(src, "com.gtnewhorizon.newgunrizons.items.ProjectileStyle")
    src = ensure_import(src, "com.gtnewhorizon.newgunrizons.items.WeaponBehavior")
    if "FieldKind." in g.behavior:
        src = ensure_import(src, "com.gtnewhorizon.newgunrizons.items.WeaponBehavior.FieldKind")
    name_call = f'.withName("{g.name}")'
    assert src.count(name_call) == 1, g.name
    insert = f"\n            .withProjectile(ProjectileStyle.{g.style}, 0x{TIER_RGB[g.tier]:06X}, {g.scale}F)"
    if g.behavior:
        insert += f"\n            .withBehavior(new WeaponBehavior(){g.behavior})"
    if TIER_INDEX[g.tier] >= TIER_INDEX["IV"]:
        insert += "\n            .withShellCasingEjectEnabled(false)"  # slugs and energy leave no casings
    src = src.replace(name_call, name_call + insert, 1)
    src, n = re.subn(r"\.withSpawnEntityDamage\([^)]*\)", f".withSpawnEntityDamage({float(g.damage)}F)", src, count=1)
    assert n == 1, g.name
    if g.gravity is not None:
        src, n = re.subn(r"\.withSpawnEntityGravityVelocity\([^)]*\)",
                         f".withSpawnEntityGravityVelocity({g.gravity}F)", src, count=1)
        if n == 0:
            src = src.replace(name_call, name_call + f"\n            .withSpawnEntityGravityVelocity({g.gravity}F)", 1)

    expl = re.search(r"\.withSpawnEntityExplosionRadius\(([0-9.]+)F\)", src)
    lines = [f"Tier: {g.tier}", f"Type: {g.typ}", f"Damage: {g.damage:g}"]
    if expl and float(expl.group(1)) > 0:
        lines.append(f"Blast radius: {float(expl.group(1)):g}")
    lines += g.effects
    lines.append(f"Ammo: {g.ammo}")
    if any(g.ammo.startswith(c) for c in ("Laser Rifle Cell", "Plasma Rifle Cell", "Particle Beam Cell",
                                         "Neutron Rifle Cell", "Exotic Matter Cell", "Quantum Cell", "Graviton Core",
                                         "Time Crystal", "Dimensional Core", "Infinity Catalyst")):
        lines.append(f"Cells recharge in a {g.tier} Battery Buffer")
    body = ",\n                    ".join('"' + line.replace('"', '\\"') + '"' for line in lines)
    info = f"(\n                (stack) -> Arrays.asList(\n                    {body}))"
    k = src.index(".withInformationProvider(")
    open_idx = src.index("(", k)
    close_idx = gh.matching_paren(src, open_idx)
    src = src[:open_idx] + info + src[close_idx + 1:]
    path.write_text(src)


def patch_cells() -> None:
    path = gh.JAVA / "registry/Magazines.java"
    text = path.read_text()
    for name, tier in ENERGY_CELLS.items():
        head = f"        {name} = (new ItemMagazine.Builder())"
        start = text.index(head)
        end = text.index(".build(ItemMagazine.class);", start)
        block = text[start:end]
        block = re.sub(r"\n\s*\.withEnergyTier\(\d+\)", "", block)
        block, n = re.subn(r"(\.withAmmo\(\d+\))", rf"\1\n            .withEnergyTier({TIER_INDEX[tier]})", block,
                           count=1)
        assert n == 1, name
        text = text[:start] + block + text[end:]
    # the plasma rifle fires plasma, not laser charges
    start = text.index("        PlasmaRifleCell = (new ItemMagazine.Builder())")
    end = text.index(".build(ItemMagazine.class);", start)
    text = text[:start] + text[start:end].replace("Bullets.LaserCharge", "Bullets.PlasmaCharge") + text[end:]
    path.write_text(text)


def add_plasma_charge() -> None:
    bpath = gh.JAVA / "registry/Bullets.java"
    btext = bpath.read_text()
    if "PlasmaCharge = (" in btext:
        return
    bmg = gh.find_block(btext, "        BMG50 = (new ItemBullet.Builder())", ".build(ItemBullet.class);")
    nb = bmg.replace("BMG50 = ", "PlasmaCharge = ", 1).replace('.withName("BMG50")', '.withName("PlasmaCharge")', 1)
    nb = nb.replace('"Bullet.png"', '"PlasmaCharge.png"', 1)
    btext = gh.add_fields(btext, "ItemBullet", ["PlasmaCharge"])
    bpath.write_text(gh.insert_before_last_brace_of_init(btext, "\n" + nb + "\n"))
    with gh.LANG.open("a") as f:
        f.write("item.newgunrizons_PlasmaCharge.name=Plasma Charge\n")


def main() -> None:
    add_plasma_charge()
    for g in GUNS:
        patch_gun(g)
    patch_cells()
    for name, (tier, src) in RETINT.items():
        gh.tint(src, f"{name.lower()}.png", tier)
    print("retiered", len(GUNS), "guns and", len(ENERGY_CELLS), "cells")


if __name__ == "__main__":
    main()
