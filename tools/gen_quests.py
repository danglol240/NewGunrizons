"""Generate the NewGunrizons quest line for GTNH's BetterQuesting (DefaultQuests format).

Writes quests/DefaultQuests/{QuestLines,Quests}/NewGunrizons-<id>/ plus QuestLinesOrder-append.txt into the repo.
Install: copy both folders into config/betterquesting/DefaultQuests/ on the server (and in single player), append
the line from QuestLinesOrder-append.txt to config/betterquesting/DefaultQuests/QuestLinesOrder.txt, then run
/bq_admin default load as an operator.

usage: gen_quests.py REPO_ROOT
"""
import base64
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
ERAS = [
    # tier, roof casing, power advice
    ("LV", "LV Machine Casing", "1 năng lượng LV (32 EU/t) là đủ"),
    ("MV", "MV Machine Casing", "hatch MV"),
    ("HV", "HV Machine Casing", "hatch HV"),
    ("EV", "EV Machine Casing", "hatch EV"),
    ("IV", "IV Machine Casing", "hatch IV"),
    ("LuV", "LuV Machine Casing", "hatch LuV"),
    ("ZPM", "ZPM Machine Casing", "hatch ZPM"),
    ("UV", "UV Machine Casing", "hatch UV"),
    ("UHV", "UHV Machine Casing", "hatch UHV (2A trở lên nên dùng multi-amp)"),
    ("UEV", "UEV Machine Casing", "multi-amp hoặc laser hatch UEV"),
    ("UIV", "UIV Machine Casing", "multi-amp hoặc laser hatch UIV"),
    ("UMV", "UMV Machine Casing", "multi-amp hoặc laser hatch UMV"),
    ("UXV", "UXV Machine Casing", "multi-amp hoặc laser hatch UXV"),
    ("MAX", "MAX Machine Casing", "laser hatch MAX"),
]

STRUCTURE = (
    "[note]Cấu trúc (nhìn từ phía trước, controller ở giữa):[/note]\n"
    "Đầu nhà máy (lát 0), 3x3:\n"
    "  C C C\n  C [K] C\n  C C C\n"
    "Mỗi đoạn phía sau (lát 1, 2, 3...), 3x3:\n"
    "  T T T   <- mái: 3 Machine Casing đúng tier của đoạn\n"
    "  G . G   <- 2 kính bất kỳ, giữa là không khí (đường lắp ráp)\n"
    "  C C C   <- sàn: Heat Proof Machine Casing\n"
    "C = Heat Proof Machine Casing (gắn hatch ở đây), K = controller.\n"
    "Đoạn 1 lợp LV casing, đoạn 2 lợp MV, ... đoạn 14 lợp MAX. Số đoạn liên tiếp = thời kỳ của nhà máy: "
    "nó chỉ chạy công thức tới tier đó. Mỗi đoạn +4 parallel.\n"
    "Mẹo: dùng Structure Lib Hologram Projector trên controller để hiện khung, chuột phải với stack size = số đoạn muốn xây."
)

q("factory", "§6§lMunitions Factory", machine(FACTORY_ID), retrieval(machine(FACTORY_ID)), [], 0, 0,
  "Mọi khẩu súng, linh kiện, băng đạn và đạn của NewGunrizons chỉ được làm trong Munitions Factory.\n\n"
  "Chế controller trên bàn chế tạo: 2 mạch LV, Robot Arm LV, 2 Conveyor LV, Hull LV, 2 Piston LV.\n\n"
  + STRUCTURE + "\n\nHatch cần có: 1 Energy, 1 Maintenance, 1 Input Bus, 1 Output Bus (Input Hatch nếu dùng soldering).",
  main=True)

q("mech", "Weapon Mechanisms", ng("PistolSlide"), retrieval(ng("PistolSlide")), ["factory"], 48, 0,
  "Súng LV-HV lắp từ nòng, thân, báng và bộ cơ khí theo loại súng: Pistol Slide (súng lục), Blowback Bolt (SMG), "
  "Gas Piston (súng trường), Precision Bolt (bắn tỉa), Pump Action (shotgun), Belt Feed (súng máy). "
  "Tất cả làm trong nhà máy, mỗi loại có mạch lập trình riêng.")

q("lvgun", "Chemical Firearms (LV)", ng("M1911"), retrieval(ng("M1911")), ["mech"], 96, 0,
  "Thời kỳ LV: súng thế chiến (M1911, Kar98K, Mosin, Garand, STG44...). Băng đạn và đạn cũng ra từ nhà máy.")

GUNS = {  # tier -> (quest name, gun, extra items, description)
    "MV": ("Advanced Firearms (MV)", "MP40", [], "SMG, shotgun, súng lục hiện đại."),
    "HV": ("Military Grade (HV)", "M4A1", [], "AK, M4, G36, P90, Dragunov và các súng quân đội."),
    "EV": ("Rail-Assisted (EV)", "XR7", ["SmartTargetingModule"],
           "XR-7 và HX-9 có ray hỗ trợ: đạn xuyên thêm 1 mục tiêu. Cần Smart Targeting Module."),
    "IV": ("Electromagnetic (IV)", "Railgun", ["ElectromagneticCoil"],
           "Coilgun, Gauss Rifle, Railgun: đạn xuyên nhiều mob, mỗi mob sau giảm damage."),
    "LuV": ("Laser (LuV)", "LaserRifle", ["LaserRifleCell"],
            "Laser bắn trúng ngay lập tức. Bắn liên tục sẽ quá nhiệt (thanh nhiệt trên HUD).\n"
            "Cell năng lượng sạc lại được: bỏ vào Battery Buffer hoặc Charger LuV."),
    "ZPM": ("Plasma (ZPM)", "PlasmaCannon", ["PlasmaRifle"],
            "Plasma đốt cháy, đẩy lùi, xuyên 30% giáp. Plasma Cannon nổ."),
    "UV": ("Particle (UV)", "ParticleBeamRifle", ["CrystalMatrixChassis"],
           "Từ UV trở đi, súng cần khung (chassis) làm ở Extreme Crafting Table của Avaritia, rồi mới lắp trong nhà máy.\n"
           "Crystal Matrix Weapon Chassis: thỏi Crystal Matrix + Diamond Lattice."),
    "UHV": ("Antimatter (UHV)", "AntimatterCannon", ["NeutroniumChassis"],
            "Antimatter gây phóng xạ GT. Khung: Neutronium Weapon Chassis (thỏi Neutronium + Neutronium Gear)."),
    "UEV": ("Quantum (UEV)", "QuantumPhaseRifle", ["InfinityChassis", "QuantumEntanglementCore"],
            "Đạn lượng tử xuyên qua tối đa 12 block. Module UEV+ cũng chỉ làm ở Extreme Crafting Table."),
    "UIV": ("Gravity (UIV)", "GravitonCannon", ["GravitonFieldEmitter"],
            "Tạo hố trọng lực hút mob lại rồi nghiền. Ngồi (sneak) khi bắn để đẩy văng."),
    "UMV": ("Spacetime (UMV)", "ChronoRifle", ["SpacetimeChassis", "SingularityCannon"],
            "Chrono Rifle đóng băng mục tiêu; Singularity Cannon mở hố đen 6 giây hút mob và item."),
    "UXV": ("Dimensional (UXV)", "DimensionalCutter", ["DimensionalRiftEmitter"],
            "Chém xuyên không gian: trúng mọi thứ trên đường thẳng 50 block, xuyên tường và giáp."),
    "MAX": ("§d§lReality Eraser (MAX)", "RealityEraser", ["EternalChassis", "RealityEngine"],
            "Xóa mục tiêu khỏi thực tại. Boss và người chơi nhận 1000 damage. Hồi chiêu 30 giây."),
}

prev_era, prev_gun = "factory", "lvgun"
for i, (tier, casing, power) in enumerate(ERAS[1:], start=1):
    y = i * 36
    era_key = f"era_{tier}"
    q(era_key, f"Factory Era {i + 1}: {tier}", machine(FACTORY_ID),
      checkbox(), [prev_era], 0, y,
      f"Nối thêm đoạn {i + 1} vào sau đoạn {i}: mái 3 {casing}, 2 kính hai bên, sàn 3 Heat Proof Machine Casing.\n"
      f"Năng lượng: {power}. Nhà máy cần đủ đoạn liên tiếp từ LV tới {tier} mới chạy được công thức {tier}.\n"
      "Đánh dấu khi đã xây xong.")
    name, gun, extra, desc = GUNS[tier]
    gun_key = f"gun_{tier}"
    q(gun_key, name, ng(gun), retrieval(*([ng(gun)] + [ng(e) for e in extra])), [era_key, prev_gun], 96, y, desc,
      main=tier in ("IV", "UV", "UEV", "MAX"))
    prev_era, prev_gun = era_key, gun_key

# ---------------- drones
q("bay", "§b§lDrone Assembly Bay", machine(BAY_ID), retrieval(machine(BAY_ID)), ["factory"], 216, 0,
  "Nhà chứa 5x3x5 để lắp drone. Controller: 2 mạch LV, Sensor LV, 2 Motor LV, Hull LV, 2 Robot Arm LV... "
  "(xem NEI).\n\n"
  "[note]Cấu trúc (5 rộng, 3 cao, 5 sâu), controller giữa tường trước:[/note]\n"
  "Tường, mái viền và sàn viền: Solid Steel Machine Casing (gắn hatch ở đây, tối thiểu 20 casing).\n"
  "Giữa mái 3x3 và 2 bên hông tầng giữa: kính bất kỳ.\n"
  "Sàn giữa 3x3: Steel Gear Box Casing (bãi đáp).\n"
  "Lòng 3x3 tầng giữa: để trống.\n"
  "Tường sau: kín casing.\n\n"
  "Tier của Energy Hatch quyết định drone nào lắp được (hatch LV -> drone LV...).",
  main=True)

DRONES = [
    ("ScoutDrone", "LV", "Quadcopter đầu tiên, bắn 9mm. Phải nạp đạn: cầm drone, sneak + chuột phải để nạp 9mm từ túi đồ."),
    ("AssaultDrone", "MV", "SMG .45, bắn loạt 3 viên."),
    ("GunshipDrone", "HV", "Hexacopter, súng trường 5.56."),
    ("HeavyDrone", "EV", "Octocopter 2 súng máy 7.62, đạn xuyên 1 mục tiêu."),
    ("RailDrone", "IV", "Railgun, dùng Ferromagnetic Slug, xuyên 3 mục tiêu."),
    ("LaserDrone", "LuV", "Từ LuV drone bắn bằng pin, không cần đạn. Laser bắn trúng tức thì."),
    ("PlasmaDrone", "ZPM", "Plasma đốt cháy và đẩy lùi."),
    ("ParticleDrone", "UV", "Chùm hạt xuyên giáp, xuyên 3 mob."),
    ("AntimatterDrone", "UHV", "Đạn phản vật chất gây phóng xạ."),
    ("QuantumDrone", "UEV", "Đạn xuyên 8 block tường."),
    ("GravitonDrone", "UIV", "Tạo hố trọng lực nhỏ."),
    ("ChronoDrone", "UMV", "Đóng băng thời gian của mục tiêu."),
    ("RiftDrone", "UXV", "Tia rạn không gian chém mọi thứ trên đường thẳng."),
    ("SentinelDrone", "MAX", "Hộ vệ tối thượng."),
]
DRONE_INTRO = (
    "\n\n[note]Cách dùng drone:[/note]\n"
    "- Drone ra khỏi máy với pin rỗng: sạc trong Battery Buffer hoặc Charger đúng tier.\n"
    "- Chuột phải: thả drone. Chuột phải vào drone: thu về.\n"
    "- Drone đi theo bạn, tự bắn mob thù địch và kẻ đang đánh bạn, không bao giờ bắn xuyên qua bạn.\n"
    "- Hết pin hoặc hết đạn: tự bay về và nằm lại trong túi đồ. Bay quá xa: mất sóng và hạ cánh tại chỗ.\n"
    "- Có thể bị bắn rơi: rơi ra drone với pin cạn."
)
prev = "bay"
for i, (name, tier, desc) in enumerate(DRONES):
    key = f"drone_{name}"
    prereqs = [prev] + ([f"era_{tier}"] if tier != "LV" else [])
    q(key, f"{tier} Drone", ng(name), retrieval(ng(name)), prereqs, 216, 36 + i * 36,
      desc + (DRONE_INTRO if i == 0 else ""), main=i in (0, 5, 13))
    prev = key

# ---------------------------------------------------------------- writer
LINE_NAME = "NewGunrizons: Arms & Drones"
LINE_DESC = ("Súng từ LV tới MAX trong Munitions Factory, linh kiện Avaritia, và drone chiến đấu từ "
             "Drone Assembly Bay.")


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
            "bg_image:8": "", "bg_size:3": 256, "desc:8": LINE_DESC, "icon:10": ng("RealityEraser"),
            "name:8": LINE_NAME, "visibility:8": "UNLOCKED"}},
        "questLineIDHigh:4": lhi, "questLineIDLow:4": llo}, indent=2, ensure_ascii=False))
    for key, name, icon, tasks, prereqs, x, y, desc, main_ in QUESTS:
        hi, lo, b64 = qid(key)
        fname = f"{file_prefix(name)}-{b64}.json"
        (line_dir / fname).write_text(json.dumps(
            {"questIDHigh:4": hi, "questIDLow:4": lo, "sizeX:3": 24, "sizeY:3": 24, "x:3": x, "y:3": y},
            indent=2))
        (quest_dir / fname).write_text(json.dumps(
            quest_json(key, name, icon, tasks, prereqs, desc, main_), indent=2, ensure_ascii=False))
    (OUT / "QuestLinesOrder-append.txt").write_text(f"{lb64}: {LINE_NAME}\n")
    print(f"{len(QUESTS)} quests in {folder}")


if __name__ == "__main__":
    main()
