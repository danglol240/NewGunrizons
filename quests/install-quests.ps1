# Installs the "NewGunrizons: Arsenal" quest line into a GTNH instance (Windows PowerShell).
# Usage: powershell -ExecutionPolicy Bypass -File install-quests.ps1 -Instance "C:\path\to\GTNH\.minecraft"
param([Parameter(Mandatory = $true)][string]$Instance)

$ErrorActionPreference = "Stop"
$src = Join-Path $PSScriptRoot "DefaultQuests"
$dst = Join-Path $Instance "config\betterquesting\DefaultQuests"
if (-not (Test-Path $dst)) { throw "Not found: $dst - point -Instance at the folder that contains config\ and mods\." }

$backup = "$dst.backup-$(Get-Date -Format yyyyMMdd-HHmmss)"
Copy-Item $dst $backup -Recurse
Write-Host "Backup: $backup"

# Merge (do not replace) the quest line into the existing GTNH quest database.
Copy-Item (Join-Path $src "QuestLines\*") (Join-Path $dst "QuestLines") -Recurse -Force
Copy-Item (Join-Path $src "Quests\*") (Join-Path $dst "Quests") -Recurse -Force

$order = Join-Path $dst "QuestLinesOrder.txt"
$line = (Get-Content (Join-Path $PSScriptRoot "QuestLinesOrder.append.txt") -Raw).Trim()
if (-not (Select-String -Path $order -SimpleMatch $line -Quiet)) { Add-Content -Path $order -Value $line }

Write-Host "Done. In game (single player or as server OP), wait a few seconds after joining, then run:"
Write-Host "    /bq_admin default load"
