package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.parts.ItemPart;

import cpw.mods.fml.common.registry.GameRegistry;

/** Weapon-class mechanisms (LV-EV guns) and themed modules (IV-UHV guns). */
public final class Parts {

    // weapon-class mechanisms
    public static Item PistolSlide;
    public static Item BlowbackBolt;
    public static Item GasPistonSystem;
    public static Item PrecisionBoltAction;
    public static Item PumpActionAssembly;
    public static Item BeltFeedMechanism;

    // high-tier modules
    public static Item SmartTargetingModule;
    public static Item ElectromagneticCoil;
    public static Item SuperconductingRail;
    public static Item FocusingLensArray;
    public static Item PlasmaContainmentChamber;
    public static Item ParticleAcceleratorSegment;
    public static Item NeutronEmitter;
    public static Item AntimatterTrap;
    public static Item ExoticMatterStabilizer;

    private Parts() {}

    public static void init() {
        PistolSlide = part("PistolSlide", "pistol_slide", "Used in every pistol");
        BlowbackBolt = part("BlowbackBolt", "blowback_bolt", "Used in every SMG");
        GasPistonSystem = part("GasPistonSystem", "gas_piston_system", "Used in assault rifles and carbines");
        PrecisionBoltAction = part("PrecisionBoltAction", "precision_bolt_action", "Used in sniper and marksman rifles");
        PumpActionAssembly = part("PumpActionAssembly", "pump_action_assembly", "Used in every shotgun");
        BeltFeedMechanism = part("BeltFeedMechanism", "belt_feed_mechanism", "Used in machine guns and heavy weapons");

        SmartTargetingModule = part("SmartTargetingModule", "smart_targeting_module", "IV: XR-7, HX-9");
        ElectromagneticCoil = part("ElectromagneticCoil", "electromagnetic_coil", "LuV: Coilgun, Gauss Rifle, Railgun");
        SuperconductingRail = part("SuperconductingRail", "superconducting_rail", "LuV: Railgun");
        FocusingLensArray = part("FocusingLensArray", "focusing_lens_array", "ZPM: Laser Rifle, Plasma Rifle");
        PlasmaContainmentChamber = part(
            "PlasmaContainmentChamber",
            "plasma_containment_chamber",
            "ZPM: Plasma Rifle, Plasma Cannon; UHV: Antimatter Cannon");
        ParticleAcceleratorSegment = part(
            "ParticleAcceleratorSegment",
            "particle_accelerator_segment",
            "UV: Particle Beam Rifle; UHV: Exotic Matter Rifle");
        NeutronEmitter = part("NeutronEmitter", "neutron_emitter", "UV: Neutron Rifle");
        AntimatterTrap = part("AntimatterTrap", "antimatter_trap", "UHV: Antimatter Cannon");
        ExoticMatterStabilizer = part("ExoticMatterStabilizer", "exotic_matter_stabilizer", "UHV: Exotic Matter Rifle");
    }

    private static Item part(String name, String texture, String usage) {
        Item item = new ItemPart(name, texture, usage);
        GameRegistry.registerItem(item, name);
        return item;
    }

    /** The mechanism a LV-EV weapon needs, chosen from its weapon class. */
    public static Item mechanismFor(Item gun) {
        if (!(gun instanceof ItemWeapon)) return null;
        ItemWeapon weapon = (ItemWeapon) gun;
        CreativeTabs tab = weapon.getHomeTab();
        if (tab == NewGunrizonsMod.PistolsTab) return PistolSlide;
        if (tab == NewGunrizonsMod.SMGTab) return BlowbackBolt;
        if (tab == NewGunrizonsMod.SnipersTab) return PrecisionBoltAction;
        if (tab == NewGunrizonsMod.ShotgunsTab) return weapon.getPellets() > 1 ? PumpActionAssembly : BeltFeedMechanism;
        return GasPistonSystem;
    }
}
