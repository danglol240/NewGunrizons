#!/usr/bin/env python3
"""Generate the "NewGunrizons: Arsenal" BetterQuesting quest line in GTNH's split DefaultQuests format.

Output: quests/DefaultQuests/{QuestLines,Quests}/<LineDir>/... plus quests/QuestLinesOrder.append.txt

IDs are fixed (derived from a namespace UUID) so re-running the script yields the same files.
Run:  python3 tools/quests/generate_quests.py
"""
import base64
import json
import struct
import uuid
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "quests" / "DefaultQuests"
NAMESPACE = uuid.UUID("6a1b7f30-2c0e-4c5e-9a57-0b4d8f2e9e11")

LINE_NAME = "NewGunrizons: Arsenal"
LINE_DESC = ("Firearms from the NewGunrizons mod. Every weapon is built in a GregTech Assembler; "
             "this line follows them from LV shotgun shells to the IV Laser Rifle.")


def ids(key):
    u = uuid.uuid5(NAMESPACE, key)
    high, low = struct.unpack(">qq", u.bytes)
    return high, low


def b64(high, low):
    return base64.urlsafe_b64encode(struct.pack(">qq", high, low)).decode()


def dir_name(name, high, low):
    clean = "".join(c for c in name if c.isalnum())[:16]
    return f"{clean}-{b64(high, low)}"


def stack(item_id, count=1, damage=0):
    return {"Count:3": count, "Damage:2": damage, "OreDict:8": "", "id:8": item_id}


QUESTS = [
    {
        "key": "shells", "name": "Twelve Gauge", "icon": "newgunrizons:ShotgunShell", "x": 0, "y": 0,
        "task": ("newgunrizons:ShotgunShell", 16), "pre": [], "main": True,
        "desc": ("Every gun needs ammo. 12 Gauge Shotgun Shells are made in an LV Assembler from "
                 "1 Brass Plate and 2 Gunpowder Dust (Programmed Circuit 8), giving 8 shells.\n\n"
                 "[note]For now the Plasma Rifle and Laser Rifle also load these shells. "
                 "Dedicated energy cells are planned.[/note]"),
        "reward": [("newgunrizons:ShotgunShell", 16)],
    },
    {
        "key": "doom", "name": "Rip and Tear", "icon": "newgunrizons:DoomShotgun", "x": 72, "y": 0,
        "task": ("newgunrizons:DoomShotgun", 1), "pre": ["shells"], "main": True,
        "desc": ("The Doom Shotgun is a pump-action shotgun firing 8 pellets per shell, 9 shells per load.\n\n"
                 "MV Assembler: 6 Steel Plates, 2 Steel Rods, 4 Steel Screws, 1 MV Electric Piston, "
                 "2 MV Circuits and 144 L Soldering Alloy.\n\n"
                 "Left-click to fire, right-click to aim, R to reload one shell at a time."),
        "reward": [("newgunrizons:ShotgunShell", 32)],
    },
    {
        "key": "plasma", "name": "Contained Plasma", "icon": "newgunrizons:PlasmaRifle", "x": 144, "y": 0,
        "task": ("newgunrizons:PlasmaRifle", 1), "pre": ["doom"], "main": True,
        "desc": ("A Plasma Rifle holds 30 shots and keeps its charge inside a Field Generator.\n\n"
                 "EV Assembler: 8 Titanium Plates, 1 EV Electric Motor, 2 EV Emitters, "
                 "1 EV Field Generator, 2 EV Circuits and 576 L Soldering Alloy."),
        "reward": [("newgunrizons:ShotgunShell", 64)],
    },
    {
        "key": "laser", "name": "Focused Light", "icon": "newgunrizons:LaserRifle", "x": 216, "y": 0,
        "task": ("newgunrizons:LaserRifle", 1), "pre": ["plasma"], "main": True,
        "desc": ("The Laser Rifle fires a focused beam in semi, burst or full auto.\n\n"
                 "IV Assembler: 8 Tungstensteel Plates, 2 IV Emitters, 1 IV Sensor, 1 Ruby Lens, "
                 "2 IV Circuits and 1152 L Soldering Alloy."),
        "reward": [("newgunrizons:ShotgunShell", 64)],
    },
]


def quest_json(q, by_key):
    high, low = ids("quest:" + q["key"])
    props = {
        "autoClaim:1": 0, "completion_animation:8": "default", "completion_particle:8": "default",
        "confetti_icon:10": stack("minecraft:stick", 0), "countAsQuest:1": 1, "desc:8": q["desc"],
        "globalShare:1": 0, "icon:10": stack(q["icon"]), "isMain:1": 1 if q["main"] else 0,
        "isSilent:1": 0, "lockedProgress:1": 0, "name:8": q["name"],
        "notification_duration:5": -1.0, "notification_effect:3": -1, "notification_fade_in:5": -1.0,
        "notification_fade_out:5": -1.0, "notification_icon_offset_y:3": -2147483648,
        "notification_icon_scale:5": -1.0, "notification_pos_x:3": -2147483648,
        "notification_pos_y:3": -2147483648, "notification_show_icon:8": "default",
        "notification_style:8": "default", "notification_subtitle:8": "", "notification_subtitle_scale:5": -1.0,
        "notification_title:8": "", "notification_title_scale:5": -1.0, "particle_count:3": -1,
        "questLogic:8": "AND", "repeatTime:3": -1, "repeat_relative:1": 1, "simultaneous:1": 0,
        "snd_complete:8": "random.levelup", "snd_update:8": "random.levelup", "taskLogic:8": "AND",
        "visibility:8": "NORMAL",
    }
    data = {}
    if q["pre"]:
        pre = {}
        for i, k in enumerate(q["pre"]):
            ph, pl = ids("quest:" + k)
            pre[f"{i}:10"] = {"questIDHigh:4": ph, "questIDLow:4": pl}
        data["preRequisites:9"] = pre
    data["properties:10"] = {"betterquesting:10": props}
    data["questIDHigh:4"] = high
    data["questIDLow:4"] = low
    data["rewards:9"] = {"0:10": {
        "ignoreDisabled:1": 0, "index:3": 0, "rewardID:8": "bq_standard:item",
        "rewards:9": {f"{i}:10": stack(item, n) for i, (item, n) in enumerate(q["reward"])},
    }}
    item, count = q["task"]
    data["tasks:9"] = {"0:10": {
        "autoConsume:1": 0, "consume:1": 0, "groupDetect:1": 0, "ignoreNBT:1": 1, "index:3": 0,
        "partialMatch:1": 1, "requireOnlyOneItem:1": 0,
        "requiredItems:9": {"0:10": stack(item, count)}, "taskID:8": "bq_standard:retrieval",
    }}
    return high, low, data


def main():
    lh, ll = ids("line:arsenal")
    line_dir = dir_name(LINE_NAME, lh, ll)
    ql_dir = OUT / "QuestLines" / line_dir
    q_dir = OUT / "Quests" / line_dir
    ql_dir.mkdir(parents=True, exist_ok=True)
    q_dir.mkdir(parents=True, exist_ok=True)

    line = {
        "properties:10": {"betterquesting:10": {
            "bg_image:8": "", "bg_size:3": 256, "desc:8": LINE_DESC,
            "icon:10": stack("newgunrizons:DoomShotgun"), "name:8": LINE_NAME, "visibility:8": "NORMAL",
        }},
        "questLineIDHigh:4": lh, "questLineIDLow:4": ll,
    }
    write(ql_dir / "QuestLine.json", line)

    by_key = {q["key"]: q for q in QUESTS}
    for q in QUESTS:
        high, low, data = quest_json(q, by_key)
        fname = dir_name(q["name"], high, low) + ".json"
        write(q_dir / fname, data)
        write(ql_dir / fname, {"questIDHigh:4": high, "questIDLow:4": low,
                               "sizeX:3": 24, "sizeY:3": 24, "x:3": q["x"], "y:3": q["y"]})

    (ROOT / "quests" / "QuestLinesOrder.append.txt").write_text(f"{b64(lh, ll)}: {LINE_NAME}\n", encoding="utf-8")
    print("Wrote quest line", line_dir)


def write(path, obj):
    path.write_text(json.dumps(obj, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
