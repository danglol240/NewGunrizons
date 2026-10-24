"""Extract Twist Space Technology machine data from a TST source checkout into tools/data/tst_machines.json, for
tools/gen_tst_quests.py: MetaTileEntity IDs, English names, the full tooltip, hatches, the special blocks of the
structure, and the controller recipe (machine, voltage, research item, ingredients).

usage: extract_tst.py TST_SOURCE_DIR OUT.json
"""
import json
import pathlib
import re
import sys

ROOT = pathlib.Path(sys.argv[1])
OUT = sys.argv[2]
J = ROOT / "src/main/java"
FILES = {p: p.read_text(encoding="utf8") for p in J.rglob("*.java")}
BY_STEM = {p.stem: s for p, s in FILES.items()}
LOADER = BY_STEM["MachineLoader"]
LANG = {}
for line in open(next((ROOT / "src/main/resources").rglob("en_US.lang")), encoding="utf8"):
    if "=" in line and not line.startswith("#"):
        k, v = line.rstrip("\n").split("=", 1)
        LANG[k] = v
MODID = "TwistSpaceTechnology"


def clean(t):
    t = re.sub(r"§.", "", t)
    t = re.sub(r"\{\\\w+\}", "", t)
    return re.sub(r"\s+", " ", t.replace("%%", "%").replace("\\n", " ")).strip()


# ------------------------------------------------------------------ TST items and blocks (GTCMItemList entries)
REGISTRY = {"MetaItem01": "MetaItem01", "MetaItemFlask": "MetaItemFlask"}
for name in ("MetaBlock01", "MetaBlockCasing01", "MetaBlockCasing02", "SpaceTimeOscillator", "SpaceTimeConstraintor",
             "SpaceTimeMerger", "StabilisationFieldGenerator", "EnergySustainmentMatrix"):
    REGISTRY[name] = name
REGISTRY["PhotonControllerUpgrade"] = "PhotonControllerUpgrades"


def lang_variant(field, meta):
    for k, v in LANG.items():
        if k.endswith(f".{field}.{meta}.name"):
            return clean(v)
    return None


TST_ITEMS = {}  # GTCMItemList name -> (registry id, meta, English name)
for s in (BY_STEM.get("ItemRegister", ""), BY_STEM.get("BlockRegister", "")):
    for m in re.finditer(r"GTCMItemList\.(\w+)\s*\.set\(\s*(?:TstBlocks\.)?(\w+)\.register\w*\(\s*(\d+)", s):
        item, field, meta = m.group(1), m.group(2), int(m.group(3))
        if field in REGISTRY:
            TST_ITEMS[item] = (f"{MODID}:{REGISTRY[field]}", meta, lang_variant(field, meta))


# ------------------------------------------------------------------ ingredient names
SPECIAL = {"sBlockCasingsDyson": "Dyson Swarm Casing", "solderPlasma": "solder plasma", "instance": None}


def human(ident):
    ident = ident.split(".")[-1]
    if ident in SPECIAL:
        return SPECIAL[ident]
    if ident in TST_ITEMS and TST_ITEMS[ident][2]:
        return TST_ITEMS[ident][2]
    if ident.startswith("eM_"):
        return "eM " + human(ident[3:])
    s = ident.replace("_", " ")
    s = re.sub(r"(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ", s)
    return re.sub(r"\s+", " ", s).strip()


PREFIX = {"plate": "Plate", "plateDense": "Dense Plate", "plateDouble": "Double Plate", "plateSuperdense":
          "Superdense Plate", "stick": "Rod", "stickLong": "Long Rod", "frameGt": "Frame Box", "gearGt": "Gear",
          "gearGtSmall": "Small Gear", "rotor": "Rotor", "ring": "Ring", "bolt": "Bolt", "screw": "Screw",
          "foil": "Foil", "wireFine": "Fine Wire", "spring": "Spring", "springSmall": "Small Spring", "lens": "Lens",
          "dust": "Dust", "ingot": "Ingot", "block": "Block", "nanite": "Nanites", "circuit": "Circuit",
          "pipeHuge": "Huge Pipe", "pipeLarge": "Large Pipe", "pipeMedium": "Pipe", "gem": "Gem",
          "gemExquisite": "Exquisite Gem", "ingotHot": "Hot Ingot", "round": "Round", "plateQuintuple":
          "Quintuple Plate", "plateQuadruple": "Quadruple Plate", "plateTriple": "Triple Plate", "cell": "Cell"}


CIRCUIT = {"Primitive": "ULV", "Basic": "LV", "Good": "MV", "Advanced": "HV", "Data": "EV", "Elite": "IV",
           "Master": "LuV", "Ultimate": "ZPM", "Superconductor": "UV", "SuperconductorUHV": "UHV", "Infinite": "UHV",
           "Bio": "UEV", "Optical": "UIV", "Exotic": "UMV", "Cosmic": "UXV", "Transcendent": "MAX"}


def circuit(mat):
    return f"{CIRCUIT.get(mat, mat)} Circuit"


def evaluate(expr):
    e = re.sub(r"(?<=\d)_(?=\d)", "", expr.strip())
    e = re.sub(r"(?<=\d)[Ll]\b", "", e)
    e = re.sub(r"\(int\)|\(long\)", "", e)
    if re.fullmatch(r"[\d\s*+\-/()]+", e):
        try:
            v = eval(e)  # noqa: S307 - digits and arithmetic only
            return int(v)
        except Exception:
            return None
    return None


def split_args(s):
    out, depth, cur = [], 0, ""
    for ch in s:
        if ch in "([{":
            depth += 1
        elif ch in ")]}":
            depth -= 1
        if ch == "," and depth == 0:
            out.append(cur)
            cur = ""
        else:
            cur += ch
    if cur.strip():
        out.append(cur)
    return [re.sub(r"//[^\n]*", "", a).strip() for a in out if re.sub(r"//[^\n]*", "", a).strip()]


def call_args(s, start):
    """Text between the parenthesis that opens at s[start] and its partner."""
    depth = 0
    for i in range(start, len(s)):
        if s[i] == "(":
            depth += 1
        elif s[i] == ")":
            depth -= 1
            if depth == 0:
                return s[start + 1:i]
    return ""


def ingredient(a):
    a = a.strip()
    m = re.fullmatch(r"([^?]+)\?(.+?):(.+)", a, re.S)  # config ternary: the enabled branch
    if m and a.count("?") == 1:
        a = m.group(2).strip()
    m = re.fullmatch(r"new\s+Object\s*\[\s*\]\s*\{\s*(.+),\s*([\w*\s]+)\}", a, re.S)
    if m:
        n = evaluate(m.group(2))
        inner = ingredient(m.group(1))
        return (n, inner[1]) if inner else None
    m = re.fullmatch(r"(?:\w+\.)*getCircuits?\(\s*Materials\.(\w+)\s*,\s*([^)]+)\)", a)
    if m:
        return evaluate(m.group(2)), circuit(m.group(1))
    m = re.fullmatch(r"OrePrefixes\.circuit\.get\(\s*Materials\.(\w+)\s*\)", a)
    if m:
        return 1, circuit(m.group(1))
    m = re.fullmatch(r"GTOreDictUnificator\.get\(\s*OrePrefixes\.(\w+)\s*,\s*Materials\.(\w+)\s*,\s*([^)]+)\)", a)
    if m:
        p, mat, n = m.groups()
        if p == "circuit":
            return evaluate(n), circuit(mat)
        if p.startswith("wireGt") or p.startswith("cableGt"):
            return evaluate(n), f"{human(mat)} {int(p[6:])}x {'Wire' if p.startswith('wire') else 'Cable'}"
        return evaluate(n), f"{human(mat)} {PREFIX.get(p, human(p))}"
    m = re.fullmatch(r"OrePrefixes\.(\w+)\.get\(\s*Materials\.(\w+)\s*\)", a)
    if m:
        return 1, f"{human(m.group(2))} {PREFIX.get(m.group(1), human(m.group(1)))}"
    m = re.fullmatch(r"Materials\.(\w+)\.getNanite\(\s*([^)]+)\)", a)
    if m:
        return evaluate(m.group(2)), f"{human(m.group(1))} Nanites"
    m = re.fullmatch(r"GTUtility\.copyAmount\w*\(\s*([^,]+),\s*(.+)\)", a, re.S)
    if m:
        inner = ingredient(m.group(2))
        return (evaluate(m.group(1)), inner[1]) if inner else None
    m = re.fullmatch(r"(?:\w+\.)*getModItem\(\s*[\w.]+\s*,\s*\"(\w+)\"\s*,\s*([^,)]+)", a)
    if m:
        return evaluate(m.group(2)), human(m.group(1))
    m = re.fullmatch(r"new\s+ItemStack\(\s*([\w.]+)\s*(?:,\s*([^,)]+))?.*\)", a, re.S)
    if m:
        return evaluate(m.group(2) or "1"), human(m.group(1))
    m = re.fullmatch(r"([\w.]+)\.get\(\s*([^,)]+)(?:,[^)]*)?\)", a)
    if m:
        return evaluate(m.group(2)), human(m.group(1))
    return None


def fluid(a):
    a = a.strip()
    m = re.fullmatch(r"Materials\.(\w+)\.get(Molten|Fluid|Plasma|Gas)\(\s*(.+)\)", a, re.S)
    if m:
        kind = {"Molten": "molten ", "Plasma": "", "Fluid": "", "Gas": ""}[m.group(2)]
        tail = " plasma" if m.group(2) == "Plasma" else ""
        return evaluate(m.group(3)), f"{kind}{human(m.group(1))}{tail}"
    m = re.fullmatch(r"new\s+FluidStack\(\s*([\w.]+)\s*,\s*(.+)\)", a, re.S)
    if m:
        n = m.group(1).split(".")[-1]
        return evaluate(m.group(2)), "solder plasma" if n == "solderPlasma" else human(n)
    m = re.fullmatch(r"(?:\w+\.)*getFluidStack\(\s*\"(\w+)\"\s*,\s*(.+)\)", a, re.S)
    if m:
        return evaluate(m.group(2)), human(m.group(1))
    return None


def fmt(items):
    out = {}
    for it in items:
        if not it or not it[1]:
            continue
        n, name = it
        if name.startswith("Integrated Circuit") or name == "Programmed Circuit":
            continue
        name = name[0].upper() + name[1:]
        if name in out and n is not None and out[name] is not None:
            out[name] += n
        elif name not in out:
            out[name] = n
    return [[n, name] for name, n in out.items()]


# ------------------------------------------------------------------ controller recipes
def recipes(item):
    pat = r"(?:GTCMItemList\.)?%s\.get\(" % item
    hits = []
    for p, s in FILES.items():
        if p.name in ("MachineLoader.java", "GTCMItemList.java"):
            continue
        for mm in re.finditer(r"itemOutputs\(\s*" + pat, s):
            a = max(s.rfind("stdBuilder()", 0, mm.start()), s.rfind("builder()", 0, mm.start()))
            chain = s[a:s.find(";", s.find("addTo(", mm.end()))]
            eut = re.search(r"\.eut\(((?:[^()]|\([^()]*\))*)\)", chain)
            mp = re.search(r"addTo\(([\w.]+)", chain)
            r = {"eut": eut.group(1).strip() if eut else None, "map": mp.group(1) if mp else None}
            i = chain.find(".itemInputs(")
            r["inputs"] = fmt(ingredient(x) for x in split_args(call_args(chain, i + 11))) if i >= 0 else []
            i = chain.find(".fluidInputs(")
            r["fluids"] = fmt(fluid(x) for x in split_args(call_args(chain, i + 12))) if i >= 0 else []
            res = re.search(r"RESEARCH_ITEM\s*,\s*(.+?)\)\s*\n\s*\.", chain, re.S)
            if res:
                ri = ingredient(res.group(1))
                r["research"] = ri[1] if ri else None
            hits.append(r)
        for mm in re.finditer(r"addResearchableAssemblylineRecipe\(", s):
            args = split_args(call_args(s, mm.end() - 1))
            if len(args) >= 10 and re.match(pat, args[7].replace("GTCMItemList.", "GTCMItemList.")):
                inner = re.search(r"\{(.*)\}", args[5], re.S)
                fl = re.search(r"\{(.*)\}", args[6], re.S)
                ri = ingredient(args[0])
                hits.append({"eut": args[9], "map": "researchableAssemblyLine",
                             "research": ri[1] if ri else None,
                             "inputs": fmt(ingredient(x) for x in split_args(inner.group(1))) if inner else [],
                             "fluids": fmt(fluid(x) for x in split_args(fl.group(1))) if fl else []})
        for mm in re.finditer(r"(addCraftingRecipe|addShapedRecipe|GameRegistry\.addRecipe)\(\s*" + pat, s):
            args = split_args(call_args(s, s.find("(", mm.start())))
            keys = []
            for x in args:
                inner = re.search(r"\{(.*)\}", x, re.S)
                if inner:
                    parts = split_args(inner.group(1))
                    for j in range(len(parts) - 1):
                        if re.fullmatch(r"'.'", parts[j]):
                            ing = ingredient(parts[j + 1])
                            if ing:
                                keys.append([None, ing[1]])
            hits.append({"eut": "craft", "map": None, "inputs": fmt(keys), "fluids": []})
        if "Thaumcraft" in str(p):
            for mm in re.finditer(r"addInfusionCraftingRecipe\w*\(", s):
                args = split_args(call_args(s, mm.end() - 1))
                if len(args) >= 6 and re.match(pat, args[1]):
                    comps = re.search(r"\{(.*)\}", args[-1], re.S)
                    centre = ingredient(args[4])
                    ins = ([[1, centre[1]]] if centre else []) + \
                        (fmt(ingredient(x) for x in split_args(comps.group(1))) if comps else [])
                    hits.append({"eut": "infusion", "map": None, "research": args[0].strip('"'), "inputs": ins,
                                 "fluids": []})
    return hits


# ------------------------------------------------------------------ tooltip and structure
CONSTS = {}
for s in FILES.values():
    for m in re.finditer(r'(\w+)\s*=\s*(?:\w+\.)*(?:tr|texter|getTranslation|translate)\(\s*"([^"]+)"(?:\s*,\s*"([^"]+)")?', s):
        name, a, b = m.groups()
        if b and b in LANG:
            CONSTS[name] = LANG[b]
        elif a in LANG:
            CONSTS[name] = LANG[a]
        elif b:
            CONSTS[name] = a


def resolve(arg):
    m = re.search(r'tr\(\s*"([^"]+)"', arg)
    if m and m.group(1) in LANG:
        return LANG[m.group(1)]
    m = re.search(r'texter\(\s*"([^"]+)"', arg)
    if m:
        return m.group(1)
    m = re.search(r'"([^"]{8,})"', arg)
    if m and not re.match(r"^[\w.]+$", m.group(1)):
        return m.group(1)
    for ident in re.findall(r"\b([A-Za-z_]\w*)\b", arg):
        if ident in CONSTS:
            return CONSTS[ident]
    return None


CONFIG = {m.group(1): m.group(2).strip().rstrip("FfLlDd")
          for m in re.finditer(r"public static \w+ (\w+)\s*=\s*([^;]+);", BY_STEM.get("Config", ""))}


def fill(text, arg, src=""):
    """Substitutes %s/%d placeholders with the values passed to the translation (config defaults, constants of the
    machine's class or literals), or 'N' when they cannot be worked out."""
    if not text or "%" not in text:
        return text
    local = {}
    for m in re.finditer(r"\b(\w+)\s*=\s*(?:Config\.)?([\w.]+)\s*;", src):
        v = m.group(2).rstrip("FfLlDd")
        local[m.group(1)] = CONFIG.get(v, v if re.fullmatch(r"[\d.]+", v) else None)
    values = []
    for tok in re.findall(r"[\w.]+", arg.split('"')[-1]):
        tok = tok.split(".")[-1]
        v = CONFIG.get(tok) or local.get(tok) or (tok if re.fullmatch(r"\d+(\.\d+)?", tok) else None)
        if v:
            values.append(v)
    def sub(_):
        return values.pop(0) if values else "N"
    return re.sub(r"%(?:\d\$)?[sd]|%\.\d+f", sub, text)


HATCHES = [("addInputBus", "Input Bus"), ("addInputHatch", "Input Hatch"), ("addOutputBus", "Output Bus"),
           ("addOutputHatch", "Output Hatch"), ("addEnergyHatch", "Energy Hatch"), ("addDynamoHatch", "Dynamo Hatch"),
           ("addMaintenanceHatch", "Maintenance Hatch"), ("addMufflerHatch", "Muffler Hatch")]


def tooltip(cls):
    s = BY_STEM.get(cls, "")
    i = s.find("createTooltip")
    seg = s[i:s.find("toolTipFinisher", i)] if i >= 0 else ""
    mt = re.search(r"addMachineType\(([^;]*?)\)\s*(?://[^\n]*\n\s*)*\.", seg, re.S)
    infos = [clean(t) for t in (fill(resolve(m.group(1)), m.group(1), s)
                                for m in re.finditer(r"\.addInfo\(((?:[^()]|\((?:[^()]|\([^()]*\))*\))*)\)", seg)) if t]
    infos = [x for x in infos if x and not x.lower().startswith("controller block")]
    hatches = [n for k, n in HATCHES if "." + k + "(" in seg]
    return clean(resolve(mt.group(1)) or "") if mt else "", infos, hatches


GT_BLOCKS = {f"sBlockCasings{n}": f"gregtech:gt.blockcasings{'' if n == 1 else n}" for n in range(1, 15)}
GT_BLOCKS.update({"sBlockCasingsNH": "gregtech:gt.blockcasingsNH", "sBlockCasingsTT": "tectech:gt.blockcasingsTT",
                  "sBlockCasingsSE": "gtnhintergalactic:gt.blockcasingsSE"})
for name, reg in REGISTRY.items():
    GT_BLOCKS[name] = f"{MODID}:{reg}"


def structure(cls):
    s = BY_STEM.get(cls, "")
    blocks = []
    for m in re.finditer(r"ofBlock\(\s*(?:[\w.]+\.)?(\w+)\s*,\s*(\d+)\s*\)", s):
        name, meta = m.group(1), int(m.group(2))
        if name in GT_BLOCKS and [GT_BLOCKS[name], meta] not in blocks:
            blocks.append([GT_BLOCKS[name], meta])
    extras = []
    if re.search(r"ofCoil|withCoil|HeatingCoil|ofCoils", s):
        extras.append("Heating Coils (the coil tier matters)")
    if re.search(r"chainAllGlasses|ofGlass|BorosilicateGlass|GlassTier|glassTier", s):
        extras.append("Tiered glass (limits the energy hatch tier)")
    for m in re.finditer(r"ofFrame\(\s*Materials\.(\w+)", s):
        t = f"{human(m.group(1))} Frame Boxes"
        if t not in extras:
            extras.append(t)
    if re.search(r"compactFusionCoil", s):
        extras.append("Compact Fusion Coils")
    if re.search(r"componentAssemblylineCasing|ComponentAssemblyLineCasing", s, re.I):
        extras.append("Component Assembly Line Casings (tier limits recipes)")
    if re.search(r"sBlockCasingsSE\b", s):
        extras.append("Space Elevator casings")
    if re.search(r"PhotonControllerUpgrade", s):
        extras.append("Photonic Intensifiers (optional speed upgrades)")
    if re.search(r"ParallelismCasing", s):
        extras.append("Parallelism casings (raise the parallel)")
    return blocks, extras


def machines():
    out = []
    for m in re.finditer(r"GTCMItemList\.(\w+)\s*\.set\(\s*(?:new\s+)?([\w.]+)\(\s*(?://[^\n]*\n\s*)*(\d+)\s*,\s*"
                         r"(?://[^\n]*\n\s*)*\"\w+\"\s*,\s*(?://[^\n]*\n\s*)*([^;]*?)\)\s*\)", LOADER, re.S):
        item, cls, id_, name_arg = m.groups()
        name = resolve(name_arg)
        out.append(dict(item=item, cls=cls.split(".")[-1], id=int(id_), name=clean(name) if name else None))
    return out


def main():
    out = []
    for m in machines():
        m["type"], m["info"], m["hatches"] = tooltip(m["cls"])
        m["recipes"] = recipes(m["item"])
        m["blocks"], m["structure"] = structure(m["cls"])
        out.append(m)
    json.dump(out, open(OUT, "w"), indent=1, ensure_ascii=False)
    print(len(out), "machines,", sum(1 for m in out if m["recipes"]), "with recipes,",
          sum(1 for m in out if m["name"]), "named")


if __name__ == "__main__":
    main()
