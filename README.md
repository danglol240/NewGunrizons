# NewGunrizons

A Minecraft 1.7.10 firearms mod for the [GT New Horizons](https://github.com/GTNewHorizons) modpack. NewGunrizons is a heavily modified fork of [**Vic's Modern Warfare**](https://github.com/vic4games/modern-warfare) (also known as Modern Warfare Mod / MWC), rebuilt from the ground up to integrate with GregTech and the GTNH ecosystem.

> **This branch targets GTNH 2.9.0 (stable).**
> Built against GTNHLib 0.11.54, NotEnoughItems 2.8.171-GTNH and GregTech 5.09.54.265, the versions shipped with GTNH 2.9.0. Tested target for shaders: Angelica 2.2.43.

## Current content (1.1.0)

The mod is being rebuilt on a Bedrock-model animation system. Only the weapons below have been migrated so far; the larger catalogue further down describes the pre-migration version and is being brought back gradually.

| Item | Tier | GregTech Assembler recipe |
|---|---|---|
| 12 Gauge Shotgun Shell (x8) | LV | 1 Brass Plate, 2 Gunpowder Dust, Programmed Circuit 8 |
| Doom Shotgun | MV | 6 Steel Plate, 2 Steel Rod, 4 Steel Screw, 1 MV Electric Piston, 2 MV Circuit, 144 L Soldering Alloy |
| Plasma Rifle | EV | 8 Titanium Plate, 1 EV Electric Motor, 2 EV Emitter, 1 EV Field Generator, 2 EV Circuit, 576 L Soldering Alloy |
| Laser Rifle | IV | 8 Tungstensteel Plate, 2 IV Emitter, 1 IV Sensor, 1 Ruby Lens, 2 IV Circuit, 1152 L Soldering Alloy |

Recipes are registered only when GregTech is installed. The Plasma and Laser Rifles currently load 12 Gauge shells until energy cells are added.

### Quest line

`quests/` contains a **"NewGunrizons: Arsenal"** quest line in GTNH's BetterQuesting format (4 quests, LV to IV). To add it to a GTNH instance:

1. Back up your world.
2. Windows: `powershell -ExecutionPolicy Bypass -File quests\install-quests.ps1 -Instance "C:\path\to\GTNH\.minecraft"`
   Linux/macOS/server: `bash quests/install-quests.sh /path/to/gtnh`
3. Join the world, wait a few seconds, then run `/bq_admin default load` (as OP on a server).

The scripts back up `config/betterquesting/DefaultQuests` first and merge the new line into it. Regenerate the files with `python3 tools/quests/generate_quests.py`.

## Shader Compatibility

NewGunrizons should be compatible with Angelica's shader pipeline. Muzzle flash effects use Angelica's dynamic lighting system for real-time illumination when firing. Scope rendering works correctly with both shaders enabled and disabled.

## What Changed from Vic's Modern Warfare

This is not a simple config tweak — the mod has been extensively restructured and refactored.

- **Full refactor** of the internal codebase.
- **Everything unrelated to weapons was cut**.
- **Bug fixes and enhancements** for the original mod.
- **GregTech crafting integration** — all items are crafted exclusively through GT machines (Assembler, Forming Press). No vanilla crafting table recipes.
- **Tiered progression** — weapons are gated behind GregTech voltage tiers (LV through IV), requiring progressively advanced components
- **Custom component system** — gun barrels, receivers, stocks, firing mechanisms, bullet casings, and precision lenses are intermediate crafting ingredients made in GT machines

## Pre-migration catalogue (being restored)

### Weapons

Over **100 firearms** spanning multiple eras and categories:

| Category | Count | Examples |
|----------|-------|---------|
| Assault Rifles & Carbines | 33 | AK-47, M4A1, G36, AUG, SCAR-H, AN-94 |
| Pistols & Machine Pistols | 20 | M1911, Glock-18, Desert Eagle, Luger P08, Makarov PM |
| SMGs & PDWs | 18 | MP5, P90, KRISS Vector, PPSh-41, MP-40 |
| Sniper & Marksman Rifles | 21 | AWP, Dragunov SVD, Barrett M107, Kar98K, Mosin Nagant |
| Shotguns & Heavy Weapons | 12 | Remington 870, SPAS-12, Saiga 12, M249 SAW, Mk153 SMAW |

![Weapons Showcase](screenshots/weapons_showcase.png)

### Attachments

- **Scopes & Sights** — ACOG, EOTech holographic, Reflex, Kobra, Leupold, PSO-1, OKP-7, PU scope, and more
- **Suppressors** — caliber-specific silencers for 9mm through .50 BMG
- **Grips** — angled grip, stubby grip, vertical foregrip, bipod
- **Lasers** — tactical laser sights

<!-- TODO: Add attachments screenshot -->
![Attachments](screenshots/attachments.png)

### Ammunition System

- **Realistic caliber system** — each weapon uses its specific ammunition type
- **Magazine-based reloading** — most weapons use detachable magazines that must be loaded with the correct bullet type
- **Three casing tiers** — small (pistol/SMG), medium (rifle), and large (.50 BMG) casings as crafting intermediates

![Combat](screenshots/combat.png)

### GregTech Integration

All crafting is done through GregTech machines — no crafting table recipes exist.

**Component crafting:**
- **Assembler** — gun barrels (plate + stick), stocks (wood/plastic/carbon), precision lenses, scopes, grips, suppressors, ammo, magazines, gun assembly
- **Forming Press** — receivers (plates), bullet casings (brass/steel), firing mechanisms, weapon part kits

**Weapon tiers:**

| Tier | EU/t | Components | Example Weapons |
|------|------|------------|-----------------|
| LV (30) | Steel barrel + Steel receiver + Wood stock | Luger P08, Kar98K, M1 Garand, STG-44 |
| MV (120) | Stainless barrel + Steel receiver + Plastic stock | MP-40, Glock-18, Desert Eagle, Remington 870 |
| HV (480) | Titanium barrel + Stainless receiver + Plastic stock | AK-47, M4A1, MP5, Dragunov |
| EV (1920) | TungstenSteel barrel + Titanium receiver + Carbon stock | SCAR-H, AK-12, Barrett M107, M249 |
| IV (7680) | TungstenSteel barrel + Titanium receiver + Carbon stock | M41A, SMAW |

![GregTech Crafting](screenshots/gt_crafting1.png)![GregTech Crafting](screenshots/gt_crafting2.png)![GregTech Crafting](screenshots/gt_crafting3.png)

## Building

```bash
# Build the mod jar
./gradlew build

# Output jar will be in build/libs/
```

## Controls

| Key         | Action |
|-------------|--------|
| Right Click | Aim Down Sights |
| Left Click  | Fire |
| R           | Reload |
| R-Shift     | Selective Fire (Semi/Burst/Auto) |
| M           | Attachment Mode |
| L           | Switch Laser |
| ] / [       | Zoom In / Out (while scoped) |

## Credits

- **Original mod**: [Vic's Modern Warfare](https://github.com/vic4games/modern-warfare) by Vic4Games — the foundation this mod was built upon
- **GT New Horizons team** — for the modpack and build toolchain

## License

This mod is a derivative work of Vic's Modern Warfare. Please refer to the original mod's license terms for redistribution and modification rights.
