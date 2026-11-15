#!/usr/bin/env bash
# Installs the "NewGunrizons: Arsenal" quest line into a GTNH instance or server (Linux/macOS).
# Usage: bash install-quests.sh /path/to/gtnh   (the folder that contains config/ and mods/)
set -euo pipefail
INSTANCE="${1:?Usage: bash install-quests.sh /path/to/gtnh}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DST="$INSTANCE/config/betterquesting/DefaultQuests"
[ -d "$DST" ] || { echo "Not found: $DST"; exit 1; }

BACKUP="$DST.backup-$(date +%Y%m%d-%H%M%S)"
cp -a "$DST" "$BACKUP" && echo "Backup: $BACKUP"

# Merge (do not replace) the quest line into the existing GTNH quest database.
cp -a "$HERE/DefaultQuests/QuestLines/." "$DST/QuestLines/"
cp -a "$HERE/DefaultQuests/Quests/." "$DST/Quests/"

LINE="$(tr -d '\r\n' < "$HERE/QuestLinesOrder.append.txt")"
grep -qxF "$LINE" "$DST/QuestLinesOrder.txt" || printf '%s\n' "$LINE" >> "$DST/QuestLinesOrder.txt"

echo "Done. In game (single player or as server OP), wait a few seconds after joining, then run:"
echo "    /bq_admin default load"
