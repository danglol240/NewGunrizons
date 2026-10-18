"""Generate the NewGunrizons quest line for GTNH's BetterQuesting (DefaultQuests format).

Writes quests/DefaultQuests/{QuestLines,Quests}/NewGunrizons-<id>/ plus QuestLinesOrder-append.txt into the repo.
Install: copy both folders into config/betterquesting/DefaultQuests/ on the server (and in single player), append
the line from QuestLinesOrder-append.txt to config/betterquesting/DefaultQuests/QuestLinesOrder.txt, then run
/bq_admin default load as an operator.

usage: gen_quests.py REPO_ROOT
"""
import math
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from bq import QuestLine, item, machine, retrieval  # noqa: E402

ROOT = Path(sys.argv[1])
OUT = ROOT / "quests/DefaultQuests"
FACTORY_ID = 17920
BAY_ID = 17921


def ng(name, count=1):
    return item("newgunrizons:" + name, 0, count)


LINE = QuestLine("6e657767-756e-7269-7a6f-6e7351756573", "NewGunrizons",
                 "Weapons from LV to MAX made in the Munitions Factory, Avaritia components, and combat drones from "
                 "the Drone Assembly Complex.", ng("SentinelDrone"))
q = LINE.q


# ---------------------------------------------------------------- content
# Layout: one column per tier (LV left ... MAX right), one row per kind of quest, so every tier reads top to bottom
# (factory era -> components -> weapon -> drone complex storey -> drone) and every row left to right.
TIERS = ["LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX"]
MATERIALS = ["Steel", "Aluminium", "Stainless Steel", "Titanium", "Tungstensteel", "HSS-S", "Naquadah Alloy",
             "Neutronium", "Cosmic Neutronium", "Infinity", "Transcendent Metal", "Spacetime", "Universium",
             "Magmatter"]
COL, ROW = 56, 64
ROWS = {"era": 0, "part": 1, "gun": 2, "storey": 3, "drone": 4}


def pos(tier_index, row, main=False):
    # main quests are drawn bigger (32 instead of 24): shift them so their centres stay on the grid
    off = -4 if main else 0
    return tier_index * COL + off, ROWS[row] * ROW + off


def frame(tier_index, n=1):
    return item("newgunrizons:TierFrame", tier_index, n)


def pad(tier_index, n=1):
    return item("newgunrizons:LandingPad", tier_index, n)


def emblem(i):
    return item("newgunrizons:Emblem", i)


def ring_pads(tier):
    r = 4 + 2 * tier
    cells = sum(1 for x in range(-r - 1, r + 2) for z in range(-r - 1, r + 2)
                if round(math.sqrt(x * x + z * z)) == r)
    return cells - 4  # four of them are frame beacons


FACTORY_GUIDE = (
    "[note]Layout (controller in the middle of the front face):[/note]\n"
    "Head (slice 0), 3x3:\n"
    "  C C C\n  C [K] C\n  C C C\n"
    "Each segment behind it (slice 1, 2, 3...), 3x3:\n"
    "  F F F   <- roof: 3 Assembly Frames of the segment's tier\n"
    "  G . G   <- 2 glass, empty in the middle (the assembly lane)\n"
    "  C C C   <- floor: Heat Proof Machine Casings\n"
    "Back cap, right behind the last segment, 3x3:\n"
    "  C C C\n  C C C\n  C C C\n"
    "C = Heat Proof Machine Casing (hatches go here), K = controller. The smallest factory is 3x3x3.\n"
    "Segment 1 is roofed with LV (Steel) frames, segment 2 with MV (Aluminium)... segment 14 with MAX (Magmatter). "
    "The GregTech Machine Casing of the same tier also works as roof.\n"
    "The number of consecutive segments is the factory's era (highest recipe tier). Each segment adds 4 parallels.\n"
    "Tip: hold a Hologram Projector with the stack size set to the number of segments."
)

q("factory", "§6§lMunitions Factory", machine(FACTORY_ID), retrieval(machine(FACTORY_ID), frame(0, 3)), [],
  *pos(0, "era", True),
  "Every NewGunrizons weapon, part, magazine and round is made in the Munitions Factory, and nowhere else.\n\n"
  "Controller: 2 LV circuits, LV Robot Arm, 2 LV Conveyors, LV Hull, 2 LV Pistons.\n"
  "Assembly Frames: Assembler, 6 plates + 1 frame box of the tier's material + 1 circuit of the tier "
  "(or by hand: 6 plates, frame box, circuit, hammer and wrench).\n\n"
  + FACTORY_GUIDE + "\n\nHatches: 1 Energy, 1 Maintenance, 1 Input Bus, 1 Output Bus (+ an Input Hatch for solder).",
  main=True)

POWER = {"UHV": "a multi-amp hatch is recommended", "UEV": "multi-amp or laser hatch", "UIV": "laser hatch",
         "UMV": "laser hatch", "UXV": "laser hatch", "MAX": "laser hatch"}
for i in range(1, 14):
    tier = TIERS[i]
    q(f"era_{tier}", f"Factory Era: {tier}", frame(i), retrieval(frame(i, 3)),
      [f"era_{TIERS[i - 1]}" if i > 1 else "factory"], *pos(i, "era"),
      f"Add segment {i + 1} behind segment {i}: a roof of 3 {tier} {MATERIALS[i]} Assembly Frames, glass on both "
      f"sides, a floor of 3 Heat Proof Machine Casings, then move the back cap one block further back. "
      f"Upgrade the Energy Hatch to {tier}"
      + (f" ({POWER[tier]})" if tier in POWER else "") + ".\n"
      f"The factory needs every segment from LV to {tier} in a row to run {tier} recipes.")

PARTS = [  # tier -> (quest name, items, description)
    ("Weapon Mechanisms", ["PistolSlide"],
     "LV-HV guns are assembled from a barrel, receiver, stock and the mechanism of their weapon class: Pistol Slide "
     "(pistols), Blowback Bolt (SMGs), Gas Piston (rifles), Precision Bolt (snipers), Pump Action (shotguns), "
     "Belt Feed (machine guns)."),
    ("Blowback Bolt", ["BlowbackBolt"], "The bolt group of MV submachine guns."),
    ("Gas Piston System", ["GasPistonSystem"], "Gas-operated action of military rifles."),
    ("Smart Targeting", ["SmartTargetingModule"], "Targeting module of the EV rail-assisted rifles."),
    ("Electromagnetic Coil", ["ElectromagneticCoil", "SuperconductingRail"],
     "Coils and superconducting rails for electromagnetic weapons."),
    ("Focusing Lens Array", ["FocusingLensArray"], "Focusing optics for lasers."),
    ("Plasma Containment", ["PlasmaContainmentChamber"], "Holds the plasma of ZPM weapons."),
    ("Crystal Matrix Chassis", ["CrystalMatrixChassis", "ParticleAcceleratorSegment"],
     "From UV on, weapons are built on a chassis made only in Avaritia's Extreme Crafting Table: the Crystal "
     "Matrix Weapon Chassis (Crystal Matrix ingots + Diamond Lattice). The factory then assembles the weapon."),
    ("Neutronium Chassis", ["NeutroniumChassis", "AntimatterTrap"],
     "Neutronium Weapon Chassis (Neutronium ingots + Neutronium Gear), Extreme Crafting Table."),
    ("Infinity Chassis", ["InfinityChassis", "QuantumEntanglementCore"],
     "The Infinity Weapon Chassis and every UEV+ module are made only in the Extreme Crafting Table."),
    ("Graviton Field Emitter", ["GravitonFieldEmitter"], "Graviton field module (Extreme Crafting Table)."),
    ("Spacetime Chassis", ["SpacetimeChassis", "ChronoStabilizer"],
     "Spacetime Weapon Chassis (built on an Infinity Chassis) and the Chrono Stabilizer."),
    ("Dimensional Rift Emitter", ["DimensionalRiftEmitter"], "Space-cutting module (Extreme Crafting Table)."),
    ("Eternal Chassis", ["EternalChassis", "RealityEngine"], "The eternal frame and the Reality Engine of MAX weapons."),
]
for i, (name, items, desc) in enumerate(PARTS):
    tier = TIERS[i]
    q(f"part_{tier}", name, ng(items[0]), retrieval(*[ng(x) for x in items]),
      ["factory" if i == 0 else f"era_{tier}"], *pos(i, "part"), desc)

GUNS = [  # (quest name, gun registry name, emblem index, description)
    ("Chemical Firearms", "M1911", 0, "World war weapons: M1911, Kar98K, Mosin, Garand, STG44..."),
    ("Advanced Firearms", "MP40", 1, "SMGs, shotguns, modern pistols."),
    ("Military Grade", "M4A1", 2, "AK, M4, G36, P90, Dragunov and other military rifles."),
    ("Rail-Assisted", "XR7", 3, "The XR-7 and HX-9 have rail assistance: their rounds pierce one extra target."),
    ("Electromagnetic", "Railgun", 4,
     "Coilgun, Gauss Rifle, Railgun: rounds pierce several mobs, losing damage with each one."),
    ("Laser", "LaserRifle", 5,
     "Hits instantly and overheats under sustained fire. Its cells recharge in an LuV Battery Buffer."),
    ("Plasma", "PlasmaCannon", 6, "Plasma burns, knocks back and ignores 30% of armor. The Plasma Cannon explodes."),
    ("Particle", "ParticleBeamRifle", 7, "Particle beams pierce armor and several mobs."),
    ("Antimatter", "AntimatterCannon", 8, "Antimatter leaves its targets irradiated (GregTech radiation)."),
    ("Quantum", "QuantumPhaseRifle", 9, "Quantum rounds phase through up to 12 blocks."),
    ("Gravity", "GravitonCannon", 10, "A gravity well pulls mobs in and crushes them. Sneak while firing to repulse."),
    ("Spacetime", "ChronoRifle", 11,
     "The Chrono Rifle freezes its target; the Singularity Cannon opens a black hole for 6 seconds."),
    ("Dimensional", "DimensionalCutter", 12, "Cuts everything on a 50 m line, through walls and armor."),
    ("§d§lReality Eraser", "RealityEraser", 13,
     "Erases its target from reality. Bosses and players take 1000 damage instead. 30 second cooldown."),
]
for i, (name, gun, em, desc) in enumerate(GUNS):
    tier = TIERS[i]
    main_ = i == 13
    q(f"gun_{tier}", f"{name} ({tier})", emblem(em), retrieval(ng(gun)),
      [f"part_{tier}"] + ([f"gun_{TIERS[i - 1]}"] if i else []), *pos(i, "gun", main_), desc, main=main_)

COMPLEX_GUIDE = (
    "[note]Base:[/note]\n"
    "- A round platform of radius 3 made of Solid Steel Machine Casings (hatches go here, at least 30 casings).\n"
    "- On it, a 5x5 hangar 3 high: Solid Steel edges, glass in the middle row, an empty 3x3 inside. "
    "The controller sits in the middle of the front wall, bottom row.\n\n"
    "[note]Each tier adds:[/note]\n"
    "- A storey on top of the tower: tier Assembly Frames on the 4 corners with glass between, then a full band "
    "of tier Assembly Frames.\n"
    "- A landing ring on the ground (platform height): that tier's Landing Pads at radius 4 + 2 x tier from the "
    "tower centre, with an Assembly Frame at north, east, south and west.\n"
    "Landing Pads: Assembler, 4 plates + 1 frame box of the tier's material -> 8 pads.\n"
    "Consecutive storeys = highest drone tier. Hologram Projector: stack size = number of tiers."
)
q("bay", "§b§lDrone Assembly Complex", machine(BAY_ID),
  retrieval(machine(BAY_ID), pad(0, ring_pads(1)), frame(0, 24)), ["gun_LV"], *pos(0, "storey", True),
  "A tower that builds drones and grows with every era. Controller: 2 LV circuits, LV Sensor, 2 LV Motors, "
  "LV Hull, 2 LV Robot Arms.\n\n" + COMPLEX_GUIDE, main=True)
for i in range(1, 14):
    tier = TIERS[i]
    q(f"storey_{tier}", f"Complex Storey: {tier}", pad(i), retrieval(pad(i, ring_pads(i + 1)), frame(i, 24)),
      [("bay" if i == 1 else f"storey_{TIERS[i - 1]}"), f"gun_{tier}"], *pos(i, "storey"),
      f"Add the {tier} storey to the tower (20 {tier} Assembly Frames) and the landing ring of radius "
      f"{4 + 2 * (i + 1)}: {ring_pads(i + 1)} {tier} {MATERIALS[i]} Landing Pads and 4 frames as beacons.")

DRONES = [
    ("ScoutDrone", "The first quadcopter, with a 9mm pistol. To load it: hold the drone, sneak + right click."),
    ("AssaultDrone", ".45 SMG, 3 round bursts."),
    ("GunshipDrone", "Hexacopter with a 5.56 rifle."),
    ("HeavyDrone", "Octocopter with twin 7.62 machine guns; rounds pierce one extra target."),
    ("RailDrone", "Railgun drone, fires Ferromagnetic Slugs, pierces 3 targets."),
    ("LaserDrone", "From LuV on, drones fire from their battery and need no ammo. Lasers hit instantly."),
    ("PlasmaDrone", "Plasma burns and knocks back."),
    ("ParticleDrone", "Particle beam that pierces armor and 3 mobs."),
    ("AntimatterDrone", "Antimatter rounds that irradiate."),
    ("QuantumDrone", "Rounds phase through 8 blocks of wall."),
    ("GravitonDrone", "Creates small gravity wells."),
    ("ChronoDrone", "Freezes its targets in time."),
    ("RiftDrone", "A rift beam that cuts everything on a line."),
    ("SentinelDrone", "The ultimate escort."),
]
DRONE_INTRO = (
    "\n\n[note]Using drones:[/note]\n"
    "- Drones leave the complex with an empty battery: charge them in a Battery Buffer or Charger of their tier.\n"
    "- Right click: deploy. Right click the drone: recall it.\n"
    "- A drone follows you and fights hostile mobs and whatever attacks you; it never fires through you.\n"
    "- Low battery or out of ammo: it flies back into your inventory. Too far away: it loses signal and lands.\n"
    "- It can be shot down: you get the drone back with an empty battery."
)
for i, (name, desc) in enumerate(DRONES):
    tier = TIERS[i]
    main_ = i in (0, 13)
    q(f"drone_{name}", f"{tier} Drone", ng(name), retrieval(ng(name)),
      [("bay" if i == 0 else f"storey_{tier}")] + ([f"drone_{DRONES[i - 1][0]}"] if i else []),
      *pos(i, "drone", main_), desc + (DRONE_INTRO if i == 0 else ""), main=main_)


if __name__ == "__main__":
    LINE.write(OUT)
