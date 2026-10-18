"""Generate the NewGunrizons quest line for GTNH's BetterQuesting (DefaultQuests format).

Writes quests/DefaultQuests/{QuestLines,Quests}/NewGunrizons-<id>/ plus QuestLinesOrder-append.txt into the repo.
Install: copy both folders into config/betterquesting/DefaultQuests/ on the server (and in single player), append
the line from QuestLinesOrder-append.txt to config/betterquesting/DefaultQuests/QuestLinesOrder.txt, then run
/bq_admin default load as an operator.

usage: gen_quests.py REPO_ROOT
"""
import base64
import math
import json
import re
import shutil
import sys
import uuid
from pathlib import Path

ROOT = Path(sys.argv[1])
OUT = ROOT / "quests/DefaultQuests"
NS = uuid.UUID("6e657767-756e-7269-7a6f-6e7351756573")  # fixed namespace: ids stay the same between runs
FACTORY_ID = 17920
BAY_ID = 17921


def qid(key):
    u = uuid.uuid5(NS, key)
    hi = int.from_bytes(u.bytes[:8], "big", signed=True)
    lo = int.from_bytes(u.bytes[8:], "big", signed=True)
    return hi, lo, base64.urlsafe_b64encode(u.bytes).decode()


def file_prefix(name):
    return re.sub(r"[^A-Za-z0-9]", "", re.sub(r"§.", "", name))[:16]


def item(id_, damage=0, count=1):
    return {"Count:3": count, "Damage:2": damage, "OreDict:8": "", "id:8": id_}


def ng(name, count=1):
    return item("newgunrizons:" + name, 0, count)


def machine(meta):
    return item("gregtech:gt.blockmachines", meta)


def retrieval(*items):
    return {"0:10": {"autoConsume:1": 0, "consume:1": 0, "groupDetect:1": 0, "ignoreNBT:1": 1, "index:3": 0,
                     "partialMatch:1": 1, "requireOnlyOneItem:1": 0,
                     "requiredItems:9": {f"{i}:10": it for i, it in enumerate(items)},
                     "taskID:8": "bq_standard:retrieval"}}


def checkbox():
    return {"0:10": {"index:3": 0, "taskID:8": "bq_standard:checkbox"}}


QUESTS = []  # (key, name, icon, tasks, prereq keys, x, y, desc, main)


def q(key, name, icon, tasks, prereqs, x, y, desc, main=False):
    QUESTS.append((key, name, icon, tasks, prereqs, x, y, desc, main))


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
    "[note]Cấu trúc (controller ở giữa mặt trước):[/note]\n"
    "Đầu nhà máy (lát 0), 3x3:\n"
    "  C C C\n  C [K] C\n  C C C\n"
    "Mỗi đoạn phía sau (lát 1, 2, 3...), 3x3:\n"
    "  F F F   <- mái: 3 Assembly Frame của tier đoạn đó\n"
    "  G . G   <- 2 kính, giữa để trống (đường lắp ráp)\n"
    "  C C C   <- sàn: Heat Proof Machine Casing\n"
    "C = Heat Proof Machine Casing (gắn hatch), K = controller.\n"
    "Đoạn 1 lợp frame LV (thép), đoạn 2 frame MV (nhôm)... đoạn 14 frame MAX (magmatter). "
    "Machine Casing GT cùng tier cũng dùng làm mái được.\n"
    "Số đoạn liên tiếp = thời kỳ nhà máy (tier công thức cao nhất). Mỗi đoạn +4 parallel.\n"
    "Mẹo: Hologram Projector, stack size = số đoạn."
)

q("factory", "§6§lMunitions Factory", machine(FACTORY_ID), retrieval(machine(FACTORY_ID), frame(0, 3)), [],
  *pos(0, "era", True),
  "Mọi khẩu súng, linh kiện, băng đạn và đạn của NewGunrizons chỉ được làm trong Munitions Factory.\n\n"
  "Controller: 2 mạch LV, Robot Arm LV, 2 Conveyor LV, Hull LV, 2 Piston LV.\n"
  "Assembly Frame làm trong Assembler: 6 plate + 1 frame box của vật liệu tier + 1 mạch tier.\n\n"
  + FACTORY_GUIDE + "\n\nHatch: 1 Energy, 1 Maintenance, 1 Input Bus, 1 Output Bus (+ Input Hatch cho soldering).",
  main=True)

POWER = {"UHV": "nên dùng multi-amp hatch", "UEV": "multi-amp hoặc laser hatch", "UIV": "laser hatch",
         "UMV": "laser hatch", "UXV": "laser hatch", "MAX": "laser hatch"}
for i in range(1, 14):
    tier = TIERS[i]
    q(f"era_{tier}", f"Factory Era: {tier}", frame(i), retrieval(frame(i, 3)), [f"era_{TIERS[i - 1]}" if i > 1 else "factory"],
      *pos(i, "era"),
      f"Nối đoạn {i + 1} ra sau đoạn {i}: mái 3 {tier} {MATERIALS[i]} Assembly Frame, 2 kính hai bên, "
      f"sàn 3 Heat Proof Machine Casing. Đổi Energy Hatch lên {tier}"
      + (f" ({POWER[tier]})" if tier in POWER else "") + ".\n"
      f"Nhà máy cần đủ đoạn liên tiếp LV -> {tier} để chạy công thức {tier}.")

PARTS = [  # tier -> (quest name, items, description)
    ("Weapon Mechanisms", ["PistolSlide"],
     "Súng LV-HV lắp từ nòng, thân, báng và bộ cơ khí theo loại súng: Pistol Slide (súng lục), Blowback Bolt "
     "(SMG), Gas Piston (súng trường), Precision Bolt (bắn tỉa), Pump Action (shotgun), Belt Feed (súng máy)."),
    ("Blowback Bolt", ["BlowbackBolt"], "Bộ khóa nòng cho SMG thời MV."),
    ("Gas Piston System", ["GasPistonSystem"], "Hệ trích khí cho súng trường quân đội."),
    ("Smart Targeting", ["SmartTargetingModule"], "Module ngắm thông minh cho súng ray hỗ trợ EV."),
    ("Electromagnetic Coil", ["ElectromagneticCoil", "SuperconductingRail"], "Cuộn từ và ray siêu dẫn cho súng điện từ."),
    ("Focusing Lens Array", ["FocusingLensArray"], "Dãy thấu kính hội tụ cho laser."),
    ("Plasma Containment", ["PlasmaContainmentChamber"], "Buồng giam plasma."),
    ("Crystal Matrix Chassis", ["CrystalMatrixChassis", "ParticleAcceleratorSegment"],
     "Từ UV, súng cần khung làm ở Extreme Crafting Table (Avaritia): Crystal Matrix Weapon Chassis "
     "(thỏi Crystal Matrix + Diamond Lattice). Sau đó lắp trong nhà máy."),
    ("Neutronium Chassis", ["NeutroniumChassis", "AntimatterTrap"],
     "Neutronium Weapon Chassis (thỏi Neutronium + Neutronium Gear) ở Extreme Crafting Table."),
    ("Infinity Chassis", ["InfinityChassis", "QuantumEntanglementCore"],
     "Infinity Weapon Chassis và các module UEV+ chỉ làm ở Extreme Crafting Table."),
    ("Graviton Field Emitter", ["GravitonFieldEmitter"], "Module trường graviton (Extreme Crafting Table)."),
    ("Spacetime Chassis", ["SpacetimeChassis", "ChronoStabilizer"],
     "Spacetime Weapon Chassis (từ Infinity Chassis) và Chrono Stabilizer."),
    ("Dimensional Rift Emitter", ["DimensionalRiftEmitter"], "Module xé không gian (Extreme Crafting Table)."),
    ("Eternal Chassis", ["EternalChassis", "RealityEngine"], "Khung vĩnh cửu và Reality Engine cho vũ khí MAX."),
]
for i, (name, items, desc) in enumerate(PARTS):
    tier = TIERS[i]
    q(f"part_{tier}", name, ng(items[0]), retrieval(*[ng(x) for x in items]),
      ["factory" if i == 0 else f"era_{tier}"], *pos(i, "part"), desc)

GUNS = [  # (quest name, gun registry name, emblem index, description)
    ("Chemical Firearms", "M1911", 0, "Súng thế chiến: M1911, Kar98K, Mosin, Garand, STG44..."),
    ("Advanced Firearms", "MP40", 1, "SMG, shotgun, súng lục hiện đại."),
    ("Military Grade", "M4A1", 2, "AK, M4, G36, P90, Dragunov và các súng quân đội."),
    ("Rail-Assisted", "XR7", 3, "XR-7 và HX-9 có ray hỗ trợ: đạn xuyên thêm 1 mục tiêu."),
    ("Electromagnetic", "Railgun", 4, "Coilgun, Gauss Rifle, Railgun: đạn xuyên nhiều mob, mỗi mob sau giảm damage."),
    ("Laser", "LaserRifle", 5, "Bắn trúng tức thì, quá nhiệt khi bắn liên tục. Cell sạc lại trong Battery Buffer LuV."),
    ("Plasma", "PlasmaCannon", 6, "Plasma đốt cháy, đẩy lùi, xuyên 30% giáp. Plasma Cannon nổ."),
    ("Particle", "ParticleBeamRifle", 7, "Chùm hạt xuyên giáp và xuyên nhiều mob."),
    ("Antimatter", "AntimatterCannon", 8, "Phản vật chất gây phóng xạ GT."),
    ("Quantum", "QuantumPhaseRifle", 9, "Đạn lượng tử xuyên qua tối đa 12 block."),
    ("Gravity", "GravitonCannon", 10, "Hố trọng lực hút mob lại rồi nghiền. Sneak khi bắn để đẩy văng."),
    ("Spacetime", "ChronoRifle", 11, "Chrono Rifle đóng băng mục tiêu; Singularity Cannon mở hố đen 6 giây."),
    ("Dimensional", "DimensionalCutter", 12, "Chém mọi thứ trên đường thẳng 50 block, xuyên tường và giáp."),
    ("§d§lReality Eraser", "RealityEraser", 13, "Xóa mục tiêu khỏi thực tại. Boss và người chơi nhận 1000 damage. "
                                                 "Hồi chiêu 30 giây."),
]
for i, (name, gun, em, desc) in enumerate(GUNS):
    tier = TIERS[i]
    main_ = i == 13
    q(f"gun_{tier}", f"{name} ({tier})", emblem(em), retrieval(ng(gun)),
      [f"part_{tier}"] + ([f"gun_{TIERS[i - 1]}"] if i else []), *pos(i, "gun", main_), desc, main=main_)

COMPLEX_GUIDE = (
    "[note]Phần gốc:[/note]\n"
    "- Sân tròn bán kính 3 bằng Solid Steel Machine Casing (gắn hatch, tối thiểu 30 casing).\n"
    "- Xưởng 5x5 cao 3 trên sân: viền Solid Steel, hàng giữa kính, lòng 3x3 trống. "
    "Controller giữa tường trước, tầng dưới.\n\n"
    "[note]Mỗi tier thêm:[/note]\n"
    "- 1 tầng tháp chồng lên: hàng dưới 4 góc là Assembly Frame tier đó, giữa là kính; hàng trên 1 vòng kín "
    "Assembly Frame.\n"
    "- 1 vòng bãi đáp trên mặt đất (cao độ của sân): Landing Pad của tier đó ở bán kính 4 + 2 x tier tính từ "
    "tâm tháp, với 1 Assembly Frame ở 4 hướng Bắc/Đông/Nam/Tây.\n"
    "Landing Pad làm trong Assembler: 4 plate + 1 frame box vật liệu tier -> 8 pad.\n"
    "Số tầng liên tiếp = tier drone cao nhất. Hologram Projector: stack size = số tier."
)
q("bay", "§b§lDrone Assembly Complex", machine(BAY_ID),
  retrieval(machine(BAY_ID), pad(0, ring_pads(1)), frame(0, 24)), ["gun_LV"], *pos(0, "storey", True),
  "Tháp lắp drone, lớn dần theo từng thời kỳ. Controller: 2 mạch LV, Sensor LV, 2 Motor LV, Hull LV, "
  "2 Robot Arm LV.\n\n" + COMPLEX_GUIDE, main=True)
for i in range(1, 14):
    tier = TIERS[i]
    q(f"storey_{tier}", f"Complex Storey: {tier}", pad(i), retrieval(pad(i, ring_pads(i + 1)), frame(i, 24)),
      [("bay" if i == 1 else f"storey_{TIERS[i - 1]}"), f"gun_{tier}"], *pos(i, "storey"),
      f"Thêm tầng {tier} cho tháp (20 {tier} Assembly Frame) và vòng bãi đáp bán kính {4 + 2 * (i + 1)}: "
      f"{ring_pads(i + 1)} {tier} {MATERIALS[i]} Landing Pad + 4 frame làm cột mốc.")

DRONES = [
    ("ScoutDrone", "Quadcopter đầu tiên, bắn 9mm. Nạp đạn: cầm drone, sneak + chuột phải."),
    ("AssaultDrone", "SMG .45, bắn loạt 3 viên."),
    ("GunshipDrone", "Hexacopter, súng trường 5.56."),
    ("HeavyDrone", "Octocopter 2 súng máy 7.62, đạn xuyên 1 mục tiêu."),
    ("RailDrone", "Railgun, dùng Ferromagnetic Slug, xuyên 3 mục tiêu."),
    ("LaserDrone", "Từ LuV drone bắn bằng pin, không cần đạn. Laser bắn trúng tức thì."),
    ("PlasmaDrone", "Plasma đốt cháy và đẩy lùi."),
    ("ParticleDrone", "Chùm hạt xuyên giáp, xuyên 3 mob."),
    ("AntimatterDrone", "Đạn phản vật chất gây phóng xạ."),
    ("QuantumDrone", "Đạn xuyên 8 block tường."),
    ("GravitonDrone", "Tạo hố trọng lực nhỏ."),
    ("ChronoDrone", "Đóng băng thời gian của mục tiêu."),
    ("RiftDrone", "Tia rạn không gian chém mọi thứ trên đường thẳng."),
    ("SentinelDrone", "Hộ vệ tối thượng."),
]
DRONE_INTRO = (
    "\n\n[note]Cách dùng drone:[/note]\n"
    "- Drone ra khỏi máy với pin rỗng: sạc trong Battery Buffer hoặc Charger đúng tier.\n"
    "- Chuột phải: thả drone. Chuột phải vào drone: thu về.\n"
    "- Drone đi theo bạn, bắn mob thù địch và kẻ đang đánh bạn, không bắn xuyên qua bạn.\n"
    "- Hết pin hoặc hết đạn: tự bay về túi đồ. Bay quá xa: mất sóng và hạ cánh tại chỗ.\n"
    "- Có thể bị bắn rơi: rơi ra drone với pin cạn."
)
for i, (name, desc) in enumerate(DRONES):
    tier = TIERS[i]
    main_ = i in (0, 13)
    q(f"drone_{name}", f"{tier} Drone", ng(name), retrieval(ng(name)),
      [("bay" if i == 0 else f"storey_{tier}")] + ([f"drone_{DRONES[i - 1][0]}"] if i else []),
      *pos(i, "drone", main_), desc + (DRONE_INTRO if i == 0 else ""), main=main_)

# ---------------------------------------------------------------- writer
LINE_NAME = "NewGunrizons: Arms & Drones"
LINE_DESC = ("Súng từ LV tới MAX trong Munitions Factory, linh kiện Avaritia, và drone chiến đấu từ "
             "Drone Assembly Complex.")


def quest_json(key, name, icon, tasks, prereqs, desc, main):
    hi, lo, _ = qid(key)
    pre = {}
    for i, p in enumerate(prereqs):
        phi, plo, _ = qid(p)
        pre[f"{i}:10"] = {"questIDHigh:4": phi, "questIDLow:4": plo}
    return {
        "preRequisites:9": pre,
        "properties:10": {"betterquesting:10": {
            "autoClaim:1": 0, "completion_animation:8": "default", "completion_particle:8": "default",
            "confetti_icon:10": item("minecraft:stick", 0, 0), "countAsQuest:1": 1, "desc:8": desc,
            "globalShare:1": 0, "icon:10": icon, "isMain:1": 1 if main else 0, "isSilent:1": 0,
            "lockedProgress:1": 0, "name:8": name, "notification_duration:5": -1.0, "notification_effect:3": -1,
            "notification_fade_in:5": -1.0, "notification_fade_out:5": -1.0,
            "notification_icon_offset_y:3": -2147483648, "notification_icon_scale:5": -1.0,
            "notification_pos_x:3": -2147483648, "notification_pos_y:3": -2147483648,
            "notification_show_icon:8": "default", "notification_style:8": "default",
            "notification_subtitle:8": "", "notification_subtitle_scale:5": -1.0, "notification_title:8": "",
            "notification_title_scale:5": -1.0, "particle_count:3": -1, "questLogic:8": "AND",
            "repeatTime:3": -1, "repeat_relative:1": 1, "simultaneous:1": 0, "snd_complete:8": "random.levelup",
            "snd_update:8": "random.levelup", "taskLogic:8": "AND", "visibility:8": "ALWAYS"}},
        "questIDHigh:4": hi,
        "questIDLow:4": lo,
        "rewards:9": {},
        "tasks:9": tasks,
    }


def main():
    lhi, llo, lb64 = qid("questline")
    folder = f"{file_prefix(LINE_NAME)}-{lb64}"
    if OUT.exists():
        shutil.rmtree(OUT)
    line_dir = OUT / "QuestLines" / folder
    quest_dir = OUT / "Quests" / folder
    line_dir.mkdir(parents=True)
    quest_dir.mkdir(parents=True)
    (line_dir / "QuestLine.json").write_text(json.dumps({
        "properties:10": {"betterquesting:10": {
            "bg_image:8": "", "bg_size:3": 256, "desc:8": LINE_DESC, "icon:10": item("newgunrizons:Emblem", 13),
            "name:8": LINE_NAME, "visibility:8": "UNLOCKED"}},
        "questLineIDHigh:4": lhi, "questLineIDLow:4": llo}, indent=2, ensure_ascii=False))
    for key, name, icon, tasks, prereqs, x, y, desc, main_ in QUESTS:
        hi, lo, b64 = qid(key)
        fname = f"{file_prefix(name)}-{b64}.json"
        (line_dir / fname).write_text(json.dumps(
            {"questIDHigh:4": hi, "questIDLow:4": lo, "sizeX:3": 32 if main_ else 24, "sizeY:3": 32 if main_ else 24,
             "x:3": x, "y:3": y},
            indent=2))
        (quest_dir / fname).write_text(json.dumps(
            quest_json(key, name, icon, tasks, prereqs, desc, main_), indent=2, ensure_ascii=False))
    (OUT / "QuestLinesOrder-append.txt").write_text(f"{lb64}: {LINE_NAME}\n")
    print(f"{len(QUESTS)} quests in {folder}")


if __name__ == "__main__":
    main()
