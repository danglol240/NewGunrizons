"""Shared writer for BetterQuesting DefaultQuests quest lines (GTNH format), used by gen_quests.py and
gen_tst_quests.py."""
import base64
import json
import re
import shutil
import uuid


class QuestLine:
    def __init__(self, namespace, name, desc, icon):
        self.ns = uuid.UUID(namespace)  # fixed namespace: ids stay the same between runs
        self.name, self.desc, self.icon = name, desc, icon
        self.quests = []  # (key, name, icon, tasks, prereq keys, x, y, desc, main)

    def q(self, key, name, icon, tasks, prereqs, x, y, desc, main=False):
        self.quests.append((key, name, icon, tasks, prereqs, x, y, desc, main))

    def qid(self, key):
        u = uuid.uuid5(self.ns, key)
        hi = int.from_bytes(u.bytes[:8], "big", signed=True)
        lo = int.from_bytes(u.bytes[8:], "big", signed=True)
        return hi, lo, base64.urlsafe_b64encode(u.bytes).decode()

    def quest_json(self, key, name, icon, tasks, prereqs, desc, main):
        hi, lo, _ = self.qid(key)
        pre = {}
        for i, p in enumerate(prereqs):
            phi, plo, _ = self.qid(p)
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

    def write(self, out):
        """Writes QuestLines/, Quests/ and QuestLinesOrder-append.txt into OUT (replacing it)."""
        keys = [k for k, *_ in self.quests]
        dup = {k for k in keys if keys.count(k) > 1}
        assert not dup, dup
        lhi, llo, lb64 = self.qid("questline")
        folder = f"{file_prefix(self.name)}-{lb64}"
        if out.exists():
            shutil.rmtree(out)
        line_dir = out / "QuestLines" / folder
        quest_dir = out / "Quests" / folder
        line_dir.mkdir(parents=True)
        quest_dir.mkdir(parents=True)
        (line_dir / "QuestLine.json").write_text(json.dumps({
            "properties:10": {"betterquesting:10": {
                "bg_image:8": "", "bg_size:3": 256, "desc:8": self.desc, "icon:10": self.icon,
                "name:8": self.name, "visibility:8": "UNLOCKED"}},
            "questLineIDHigh:4": lhi, "questLineIDLow:4": llo}, indent=2, ensure_ascii=False))
        for key, name, icon, tasks, prereqs, x, y, desc, main_ in self.quests:
            hi, lo, b64 = self.qid(key)
            fname = f"{file_prefix(name)}-{b64}.json"
            (line_dir / fname).write_text(json.dumps(
                {"questIDHigh:4": hi, "questIDLow:4": lo, "sizeX:3": 32 if main_ else 24,
                 "sizeY:3": 32 if main_ else 24, "x:3": x, "y:3": y},
                indent=2))
            (quest_dir / fname).write_text(json.dumps(
                self.quest_json(key, name, icon, tasks, prereqs, desc, main_), indent=2, ensure_ascii=False))
        (out / "QuestLinesOrder-append.txt").write_text(f"{lb64}: {self.name}\n")
        print(f"{len(self.quests)} quests in {folder}")
        return folder


def file_prefix(name):
    return re.sub(r"[^A-Za-z0-9]", "", re.sub(r"§.", "", name))[:16]


def item(id_, damage=0, count=1):
    return {"Count:3": count, "Damage:2": damage, "OreDict:8": "", "id:8": id_}


def machine(meta):
    return item("gregtech:gt.blockmachines", meta)


def retrieval_task(items, index=0, any_one=False, optional=False):
    return {"autoConsume:1": 0, "consume:1": 0, "groupDetect:1": 0, "ignoreNBT:1": 1, "index:3": index,
            "partialMatch:1": 1, "requireOnlyOneItem:1": 1 if any_one else 0,
            "requiredItems:9": {f"{i}:10": it for i, it in enumerate(items)},
            "taskID:8": "bq_standard:optional_retrieval" if optional else "bq_standard:retrieval"}


def retrieval(*items, any_one=False):
    return {"0:10": retrieval_task(items, 0, any_one)}


def tasks(*task_list):
    """Several tasks in one quest, indexed in order."""
    out = {}
    for i, t in enumerate(task_list):
        t = dict(t)
        t["index:3"] = i
        out[f"{i}:10"] = t
    return out


def checkbox():
    return {"0:10": {"index:3": 0, "taskID:8": "bq_standard:checkbox"}}
