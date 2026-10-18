package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.parts.ItemPart;

import cpw.mods.fml.common.registry.GameRegistry;

/** Weapon-class mechanisms (LV-HV guns) and themed technology modules (EV-MAX guns). */
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
    public static Item QuantumEntanglementCore;
    public static Item GravitonFieldEmitter;
    public static Item ChronoStabilizer;
    public static Item SingularityContainmentUnit;
    public static Item DimensionalRiftEmitter;
    public static Item RealityEngine;

    // weapon chassis, made only in the Extreme Crafting Table (Avaritia)
    public static Item CrystalMatrixChassis;
    public static Item NeutroniumChassis;
    public static Item InfinityChassis;
    public static Item SpacetimeChassis;
    public static Item EternalChassis;

    private Parts() {}

    public static void init() {
        PistolSlide = part("PistolSlide", "pistol_slide", "Used in every pistol");
        BlowbackBolt = part("BlowbackBolt", "blowback_bolt", "Used in every SMG");
        GasPistonSystem = part("GasPistonSystem", "gas_piston_system", "Used in assault rifles and carbines");
        PrecisionBoltAction = part(
            "PrecisionBoltAction",
            "precision_bolt_action",
            "Used in sniper and marksman rifles");
        PumpActionAssembly = part("PumpActionAssembly", "pump_action_assembly", "Used in every shotgun");
        BeltFeedMechanism = part("BeltFeedMechanism", "belt_feed_mechanism", "Used in machine guns and heavy weapons");

        SmartTargetingModule = part("SmartTargetingModule", "smart_targeting_module", "EV: XR-7, HX-9");
        ElectromagneticCoil = part("ElectromagneticCoil", "electromagnetic_coil", "IV: Coilgun, Gauss Rifle, Railgun");
        SuperconductingRail = part(
            "SuperconductingRail",
            "superconducting_rail",
            "IV: Railgun; UHV: Antimatter Cannon");
        FocusingLensArray = part("FocusingLensArray", "focusing_lens_array", "LuV: Laser Rifle; ZPM: Plasma Rifle");
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
        QuantumEntanglementCore = part(
            "QuantumEntanglementCore",
            "quantum_entanglement_core",
            "UEV: Quantum Phase Rifle");
        GravitonFieldEmitter = part("GravitonFieldEmitter", "graviton_field_emitter", "UIV: Graviton Cannon");
        ChronoStabilizer = part("ChronoStabilizer", "chrono_stabilizer", "UMV: Chrono Rifle");
        SingularityContainmentUnit = part(
            "SingularityContainmentUnit",
            "singularity_containment_unit",
            "UMV: Singularity Cannon");
        DimensionalRiftEmitter = part("DimensionalRiftEmitter", "dimensional_rift_emitter", "UXV: Dimensional Cutter");
        RealityEngine = part("RealityEngine", "reality_engine", "MAX: Reality Eraser");

        CrystalMatrixChassis = part(
            "CrystalMatrixChassis",
            "crystal_matrix_chassis",
            "UV weapon frame (Extreme Crafting Table)");
        NeutroniumChassis = part("NeutroniumChassis", "neutronium_chassis", "UHV weapon frame (Extreme Crafting Table)");
        InfinityChassis = part("InfinityChassis", "infinity_chassis", "UEV-UIV weapon frame (Extreme Crafting Table)");
        SpacetimeChassis = part("SpacetimeChassis", "spacetime_chassis", "UMV-UXV weapon frame (Extreme Crafting Table)");
        EternalChassis = part("EternalChassis", "eternal_chassis", "MAX weapon frame (Extreme Crafting Table)");
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
