"""Generate a detailed BetterQuesting quest line for Twist Space Technology (TST 0.8, GTNH 2.9).

Reads tools/data/tst_machines.json (made by tools/extract_tst.py from the TST source) and writes
quests/TwistSpaceTechnology/DefaultQuests/{QuestLines,Quests}/<line>-<id>/ plus QuestLinesOrder-append.txt.

Each machine quest shows the full tooltip (what the machine does, its modes and bonuses), the hatches it takes,
the special blocks of its structure (as an optional checklist), and how its controller is made: the machine,
voltage, research and every ingredient. The layout is one column per tier, Steam on the left to MAX on the right:
a tier overview card on top, the multiblocks below it, then hatches and modules at the bottom.

usage: gen_tst_quests.py REPO_ROOT
"""
import json
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from bq import QuestLine, checkbox, item, machine, retrieval, retrieval_task, tasks  # noqa: E402

ROOT = Path(sys.argv[1])
OUT = ROOT / "quests/TwistSpaceTechnology/DefaultQuests"
DATA = {m["id"]: m for m in json.loads((Path(__file__).resolve().parent / "data/tst_machines.json").read_text())}

TIERS = ["Steam", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX"]
TIER_COLOUR = ["§6", "§7", "§b", "§6", "§5", "§9", "§d", "§c", "§3", "§4", "§a", "§2", "§e", "§1", "§c§l"]
COL, ROW = 52, 40
HEADER_Y = 0
MACHINES_Y = 56

# controllers whose recipe has no GregTech voltage: tier chosen from their ingredients
TIER_OVERRIDE = {
    19024: "LV", 19032: "Steam", 19033: "Steam", 19076: "Steam", 19022: "Steam",
    19034: "LV", 19073: "LV", 19074: "LV",
    19018: "IV", 19070: "IV", 19071: "IV", 19075: "IV", 19061: "IV",
    19047: "LuV", 19051: "LuV", 19064: "LuV", 19036: "ZPM",
    19050: "IV", 19052: "IV", 19063: "IV", 19066: "IV",
}
NAME_OVERRIDE = {19018: "Elven Workshop", 19024: "Super Clean Room", 19046: "Ball Lightning", 19049: "Mega Array",
                 19042: "Space Apiary Module MK-I", 19043: "Space Apiary Module MK-II",
                 19044: "Space Apiary Module MK-III", 19045: "Space Apiary Module MK-IV"}
DESC_OVERRIDE = {
    19024: ["A larger clean room for TST: works like the GregTech Cleanroom, with a bigger, freely sized room."],
    19029: ["A huge computation array: provides computation for TecTech research and computation-hungry machines."],
}
EXTRA_NOTES = {
    19003: "Several TST controllers (Indistinct Tentacle, Deployed Nano Core, Strange Matter Aggregator, Super Water "
           "Purifier) are made in the Miracle Top, so it is worth building early in UMV.",
    19013: "The Dyson Sphere line: the Launch Site sends modules into orbit, the Ray Receiving Station turns the "
           "sphere's light into EU, and the Artificial Star burns antimatter for power.",
    19066: "Put single block machines into the controller like the GregTech Processing Array; TST's version takes "
           "up to 64 of them.",
    19049: "Put machines in an input bus and right-click the controller with a screwdriver to set it up.",
    19042: "Space Apiary modules are Space Elevator modules: they need a Space Elevator with a motor of high enough "
           "tier, and a queen bee in the controller.",
}
SKIP = {19019, 19500, 19298, 18978, 18959, 18958, 18848, 18849, 18850, 18846}
MAIN = {19003, 19015, 19016, 19028, 19038, 19054, 19056, 19059}

GROUPS = [  # (key, name, ids, tier, description)
    ("buffered", "Buffered Energy Hatches", list(range(18984, 18998)), "LV",
     "Energy hatches with an internal buffer, LV to MAX. They smooth out power for machines that draw in bursts."),
    ("dual", "Dual Input Buffers", list(range(18980, 18984)), "IV",
     "Items and fluids through one hatch, IV to UV. Pair them with ME pattern providers for crafting setups."),
    ("solidify", "Solidifier Hatches", list(range(18793, 18798)), "IV",
     "Output hatches that solidify molten outputs straight into ingots, plates and parts, IV to UHV."),
    ("mana", "Mana Hatch", [18979], "IV", "Feeds Botania mana into TST's magic machines (Elven Workshop)."),
    ("imprint", "Imprint Circuit Hatches", [18791, 18792], "LuV",
     "Hold the circuit imprint for circuit assembly lines: T1 at LuV, T2 at UHV."),
    ("pattern", "Pattern Access Hatch", [18847], "LuV",
     "Lets an ME network read the crafting patterns stored in a TST machine."),
    ("air", "Infinite Air Hatch", [18999], "UV", "Supplies unlimited air to the machine it is attached to."),
    ("lasernode", "Laser Smart Nodes", [18960, 18961], "UHV",
     "Laser pipe nodes that route laser energy around corners (the focused node over long distances)."),
    ("apiary", "Space Apiary Modules", [19042, 19043, 19044, 19045], "UHV", None),
    ("stocking", "Super Stocking Input (ME)", [18851, 18852], "UEV",
     "A stocking input bus and hatch that read straight from ME storage cells. Made in the Space Assembler."),
    ("wirelessdata", "Wireless Optical Connectors", [18956, 18957], "UMV",
     "Send optical data between machines without cables: one master connector, any number of slaves."),
    ("wirelessenergy", "Wireless Energy Hatches", [18798, 18799], "UMV",
     "Legendary (UMV) and Harmonious (UXV) wireless energy hatches draw huge amperage from the wireless EU network."),
    ("wirelessdynamo", "Infinite Wireless Dynamo Hatch", [18998], "UXV",
     "Sends all of a generator's output into the wireless EU network."),
    ("exec", "Execution Core Module", [18843], "UV", "The basic execution core of a modularized machine."),
    ("parallel", "Parallel Controller Modules", list(range(18800, 18816)), "UV",
     "Static and dynamic parallel modules, T1 (UV) to T8. Static ones add a fixed parallel; dynamic ones "
     "(made from a static one with a screen and a data orb) can be tuned."),
    ("speed", "Speed Controller Modules", list(range(18816, 18832)), "UV",
     "Static and dynamic speed modules, T1 (UV) to T8."),
    ("power", "Power Consumption Modules", list(range(18832, 18840)), "UV",
     "Lower the power draw of a modularized machine, T1 (UV) to T8."),
    ("lspoc", "Low Speed Perfect Overclock Module", [18840], "UIV",
     "Gives a modularized machine perfect overclocks, at a lower speed."),
    ("poc", "Perfect Overclock Module", [18841], "UMV", "Gives a modularized machine perfect overclocks."),
    ("advexec", "Advanced Execution Core Module", [18844], "UMV", "A faster execution core."),
    ("perfexec", "Perfect Execution Core Module", [18845], "UXV", "The best execution core."),
    ("spoc", "Singularity Perfect Overclock Module", [18842], "MAX", "The ultimate overclock module."),
]
GROUPED = {i for g in GROUPS for i in g[2]}
MODULE_INTRO = ("[note]Modularized machines[/note]\nTST's modularized machines (Plasma Forge MK-II, Large Neutron "
                "Oscillator, Indistinct Tentacle MK-II, Mass Fabricator Genesis, Strange Matter Aggregator...) take "
                "module hatches. Execution cores run the recipes; parallel, speed, power and overclock modules "
                "change how. Place them in the hatch positions shown in the structure preview.\n\n")

PREREQS = {
    19014: [19013], 19015: [19014],
    19028: [19003], 19038: [19003], 19059: [19003], 19078: [19003],
    19056: [19028], 19074: [19073],
    19047: [19018], 19064: [19047], 19071: [19070], 19075: [19071],
}
RECIPE_KIND = {
    "assembler": "Assembler", "assemblerRecipes": "Assembler", "RecipeMaps.assemblerRecipes": "Assembler",
    "AssemblyLine": "Assembly Line", "assemblyLine": "Assembly Line", "GTRecipeConstants.AssemblyLine":
    "Assembly Line", "researchableAssemblyLine": "Assembly Line (TecTech research)",
    "MiracleTopRecipeMap": "Miracle Top", "GTCMRecipe.MiracleTopRecipeMap": "Miracle Top",
    "GTCMRecipe.AssemblyLineWithoutResearchRecipeMap": "TST Assembly Line (no research needed)",
    "GoodGeneratorRecipeMaps.preciseAssemblerRecipes": "Precise Auto-Assembler MT-3662",
    "SA": "Space Assembler (Space Elevator module)", "SpaceAssembler": "Space Assembler (Space Elevator module)",
}


def recipe(m):
    return m["recipes"][0] if m["recipes"] else None


def recipe_tier(r):
    if not r or r["eut"] in ("craft", "infusion"):
        return None
    t = re.search(r"RECIPE_(\w+)", r["eut"] or "")
    if t:
        return t.group(1)
    if (r["eut"] or "").replace("_", "").isdigit() and int(r["eut"].replace("_", "")) >= 2_000_000_000:
        return "MAX"
    return None


def tier_of(m):
    return TIER_OVERRIDE.get(m["id"]) or (recipe_tier(recipe(m)) if recipe_tier(recipe(m)) in TIERS else "IV")


def amount(n, name):
    if n is None:
        return name
    if n >= 1000 and not name[:1].isdigit() and any(k in name.lower() for k in ("molten", "plasma", "fluid", "matter",
                                                                                   "honey", "dtsc", "water", "uu")):
        return f"{n:,} L {name}"
    return f"{n:,}x {name}" if n != 1 else name


def recipe_text(r):
    if not r:
        return []
    out = []
    if r["eut"] == "craft":
        out.append("[note]Controller recipe[/note]: Crafting Table")
        if r["inputs"]:
            out.append("Uses: " + ", ".join(name for _, name in r["inputs"]) + " (see NEI for the shape).")
        return out
    if r["eut"] == "infusion":
        out.append("[note]Controller recipe[/note]: Thaumcraft Infusion")
        if r.get("research"):
            key = re.sub(r"^[A-Z]{1,3}_", "", r["research"])
            out.append(f"Research: {key.replace('_', ' ').title()} (Thaumonomicon, TST tab).")
    else:
        kind = RECIPE_KIND.get(r["map"], (r["map"] or "GregTech machine").split(".")[-1])
        tier = recipe_tier(r)
        out.append(f"[note]Controller recipe[/note]: {kind}" + (f", {tier}" if tier else ""))
        if r.get("research"):
            out.append(f"Research item: {r['research']}")
    for n, name in r["inputs"][:18]:
        out.append(" - " + amount(n, name))
    if len(r["inputs"]) > 18:
        out.append(f" - ... and {len(r['inputs']) - 18} more (see NEI)")
    for n, name in r["fluids"]:
        out.append(" - " + amount(n, name))
    return out


def describe(m, tier):
    lines = []
    if m.get("type"):
        lines.append("§6Machine type: §a" + " / ".join(x.strip() for x in m["type"].split("|")) + "§r")
        lines.append("")
    info = DESC_OVERRIDE.get(m["id"]) or [s for s in m.get("info", []) if not s.startswith("-----")]
    lines += info[:16]
    if len(info) > 16:
        lines.append("(more in the controller's tooltip)")
    if m["id"] in EXTRA_NOTES:
        lines += ["", "[note]Tip[/note] " + EXTRA_NOTES[m["id"]]]
    lines.append("")
    if m.get("hatches"):
        lines.append("[note]Hatches[/note]: " + ", ".join(m["hatches"]))
    if m.get("structure") or m.get("blocks"):
        parts = list(m.get("structure", []))
        if m.get("blocks"):
            parts.append("the casings in the optional task")
        lines.append("[note]Structure[/note]: " + "; ".join(parts) + ".")
    lines.append("Use a Hologram Projector (or the NEI structure preview) to see and auto-build the structure.")
    lines.append("")
    lines += recipe_text(recipe(m))
    return "\n".join(lines).strip()


def machine_tasks(m):
    t = [retrieval_task([machine(m["id"])])]
    blocks = [item(reg, meta) for reg, meta in m.get("blocks", [])]
    if blocks:
        t.append(retrieval_task(blocks, optional=True))
    return tasks(*t)


LINE = QuestLine("7473742d-7175-6573-742d-6c696e650000", "Twist Space Tech Guide",
                 "§cTwist Space Technology§r, machine by machine: what each multiblock does, its hatches and special "
                 "blocks, and the full recipe of its controller. One column per tier, from Steam to MAX.",
                 machine(19003))

WELCOME = """[quest]Welcome to Twist Space Technology[/quest]

TST adds big, fast multiblocks to GTNH, mostly for the late game, plus hatches, modules and a few magic machines.

[note]How to read this book[/note]
- One column per tier, Steam on the left to MAX on the right. The card on top of each column lists that tier's TST machines.
- Every machine quest asks for its controller. Its description has the tooltip, the hatches, the structure's special blocks and the complete controller recipe.
- The optional task lists the casings of the structure: grab one of each to tick them off.
- Hatches and modules are at the bottom of the columns.

[note]Tips[/note]
- Use a Hologram Projector or the NEI structure preview to build the structures; most TST machines are too big to build by hand.
- Many machines switch mode with a screwdriver.
- Most numbers (parallels, speed bonuses) can be changed in config/TwistSpaceTechnology.cfg. After a TST update, reset that file and GregTech.lang.
- Some machines need Thaumcraft research, Botania mana or Blood Magic; they are marked in their recipe."""


def main():
    columns = {t: [] for t in TIERS}
    for m in DATA.values():
        if m["id"] in SKIP or m["id"] in GROUPED or not m.get("name"):
            continue
        columns[tier_of(m)].append(m)
    LINE.q("welcome", "§c§lWelcome to TST", machine(19003), checkbox(), [], -COL * 2 - 4, HEADER_Y - 4,
           WELCOME, main=True)
    used = [t for t in TIERS if columns[t] or any(g[3] == t for g in GROUPS)]
    for ti, t in enumerate(used):
        ms = sorted(columns[t], key=lambda m: m["id"])
        groups = [g for g in GROUPS if g[3] == t]
        colour = TIER_COLOUR[TIERS.index(t)]
        listing = [f" - {NAME_OVERRIDE.get(m['id'], m['name'])}" + (f": {m['type'].split('|')[0].strip()}"
                                                                   if m.get("type") else "") for m in ms]
        listing += [f" - {g[1]}" for g in groups]
        icon = machine(ms[0]["id"]) if ms else machine(groups[0][2][0]) if groups else machine(19003)
        LINE.q(f"tier_{t}", f"{colour}{t}§r Twist Tech", icon, checkbox(),
               ["welcome"] if ti == 0 else [f"tier_{used[ti - 1]}"], ti * COL, HEADER_Y,
               f"[note]{t}[/note]\nTST machines you can build at {t}:\n" + ("\n".join(listing) or " (none)"))
        for row, m in enumerate(ms):
            main_ = m["id"] in MAIN
            off = -4 if main_ else 0
            name = NAME_OVERRIDE.get(m["id"], m["name"])
            LINE.q(f"m{m['id']}", (colour + "§l" if main_ else "") + name, machine(m["id"]),
                   machine_tasks(m), [f"m{p}" for p in PREREQS.get(m["id"], [])],
                   ti * COL + off, MACHINES_Y + row * ROW + off, describe(m, t), main=main_)
    # hatches and modules sit right under their own column's machines, after a small gap
    rows = {t: 0 for t in TIERS}
    for key, name, ids, t, desc in GROUPS:
        ti = used.index(t)
        items = [machine(i) for i in ids if i in DATA]
        if desc is None:  # described from the first member's data
            m = DATA[ids[0]]
            desc = describe(m, t)
        if key in ("exec", "parallel", "speed", "power", "lspoc", "poc", "advexec", "perfexec", "spoc"):
            desc = MODULE_INTRO + desc
            r = recipe(DATA[ids[0]])
            if r:
                desc += "\n\n" + "\n".join(recipe_text(r))
        elif len(ids) == 1 or key in ("lasernode", "stocking", "wirelessdata", "wirelessenergy", "imprint"):
            r = recipe(DATA[ids[0]])
            if r:
                desc += "\n\n" + "\n".join(recipe_text(r))
        if len(items) > 1:
            desc += "\n\nAny one of these completes the quest."
        LINE.q(f"g_{key}", name, items[0], retrieval(*items, any_one=len(items) > 1), [],
               ti * COL, MACHINES_Y + len(columns[t]) * ROW + ROW // 2 + rows[t] * ROW, desc)
        rows[t] += 1
    LINE.write(OUT)
    for t in used:
        print(f"  {t}: {len(columns[t])} machines, {rows[t]} hatch/module quests")


if __name__ == "__main__":
    main()
