package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.CommonProxy;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/**
 * Munitions Factory recipes for the weapon-class mechanisms, the EV-MAX technology modules, the EV-MAX weapons and
 * their magazines, cells and ammo. Tiers follow the weapon design: EV rail-assisted rifles, IV electromagnetic, LuV
 * laser, ZPM plasma, UV particle, UHV antimatter, UEV quantum, UIV gravity, UMV spacetime, UXV dimensional and MAX.
 * <p>
 * Each weapon is built from the modules that match how it works, and every module needs the one before it, so the
 * weapons form one progression. From UEV on, the modules are crafted in Avaritia's Extreme Crafting Table when it is
 * installed (see {@link ExtremeRecipes}); without it they come from the factory like the rest.
 * <p>
 * Registered in post-init so the tiered circuit ore-dictionary entries added by GregTech and the GTNH core mod are
 * already present.
 */
public final class HighTierRecipes {

    private static final int EUT_LV = 30;
    private static final int EUT_MV = 120;
    private static final int EUT_EV = 1_920;
    private static final int EUT_IV = 7_680;
    private static final int EUT_LUV = 30_720;
    private static final int EUT_ZPM = 122_880;
    private static final int EUT_UV = 491_520;
    private static final int EUT_UHV = 1_966_080;
    private static final int EUT_UEV = 7_864_320;
    private static final int EUT_UIV = 31_457_280;
    private static final int EUT_UMV = 125_829_120;
    private static final int EUT_UXV = 503_316_480;
    private static final int EUT_MAX = 2_013_265_920;

    private static final int TIER_UEV = 10;
    private static final int TIER_UIV = 11;
    private static final int TIER_UMV = 12;
    private static final int TIER_UXV = 13;
    private static final int TIER_MAX = 14;

    private HighTierRecipes() {}

    public static void init() {
        if (!Loader.isModLoaded("gregtech")) return;
        addMechanisms();
        addModules();
        addEndgameModules();
        addChassis();
        addEV();
        addIV();
        addLuV();
        addZPM();
        addUV();
        addUHV();
        addUEV();
        addUIV();
        addUMV();
        addUXV();
        addMAX();
    }

    // ==================== weapon-class mechanisms (LV-MV) ====================

    private static void addMechanisms() {
        Materials st = Materials.Steel;
        part(
            Parts.PistolSlide,
            1,
            EUT_LV,
            200,
            21,
            plate(st, 2),
            of(OrePrefixes.spring, st, 1),
            of(OrePrefixes.screw, st, 2));
        part(Parts.BlowbackBolt, 1, EUT_LV, 200, 22, stick(st, 2), of(OrePrefixes.spring, st, 2), plate(st, 1));
        part(
            Parts.GasPistonSystem,
            1,
            EUT_LV,
            240,
            23,
            of(OrePrefixes.pipeSmall, st, 1),
            stick(st, 2),
            of(OrePrefixes.spring, st, 1));
        part(
            Parts.PrecisionBoltAction,
            1,
            EUT_MV,
            300,
            24,
            stick(Materials.StainlessSteel, 2),
            of(OrePrefixes.screw, Materials.StainlessSteel, 2),
            of(OrePrefixes.spring, st, 1),
            new ItemStack(CommonProxy.PrecisionLens));
        part(
            Parts.PumpActionAssembly,
            1,
            EUT_LV,
            240,
            25,
            plate(st, 2),
            stick(st, 2),
            of(OrePrefixes.gearGtSmall, st, 1));
        part(
            Parts.BeltFeedMechanism,
            1,
            EUT_MV,
            300,
            26,
            of(OrePrefixes.gearGtSmall, st, 2),
            plate(st, 2),
            of(OrePrefixes.spring, st, 2));
    }

    // ==================== technology modules (EV-UHV) ====================

    private static void addModules() {
        part(
            Parts.SmartTargetingModule,
            1,
            EUT_EV,
            400,
            1,
            ItemList.Sensor_EV.get(1),
            circuit(Materials.EV, 1),
            of(OrePrefixes.lens, Materials.Diamond, 1),
            plate(Materials.Titanium, 2));
        part(
            Parts.ElectromagneticCoil,
            1,
            EUT_IV,
            400,
            2,
            stick(Materials.SteelMagnetic, 4),
            of(OrePrefixes.wireFine, Materials.Platinum, 16),
            plate(Materials.TungstenSteel, 2),
            circuit(Materials.IV, 1));
        part(
            Parts.SuperconductingRail,
            1,
            EUT_IV,
            600,
            3,
            stickLong(Materials.TungstenSteel, 2),
            plate(Materials.Iridium, 2),
            ItemList.Field_Generator_IV.get(1),
            circuit(Materials.IV, 1));
        part(
            Parts.FocusingLensArray,
            1,
            EUT_LUV,
            500,
            4,
            of(OrePrefixes.lens, Materials.Ruby, 2),
            of(OrePrefixes.lens, Materials.Diamond, 1),
            ItemList.Emitter_LuV.get(1),
            plate(Materials.HSSS, 2));
        part(
            Parts.PlasmaContainmentChamber,
            1,
            EUT_ZPM,
            600,
            5,
            ItemList.Field_Generator_ZPM.get(1),
            plate(Materials.Osmiridium, 4),
            plateDense(Materials.NaquadahAlloy, 1),
            circuit(Materials.ZPM, 1));
        part(
            Parts.ParticleAcceleratorSegment,
            1,
            EUT_UV,
            600,
            6,
            ItemList.Field_Generator_UV.get(1),
            new ItemStack(Parts.ElectromagneticCoil, 2),
            plate(Materials.Neutronium, 4),
            circuit(Materials.UV, 1));
        part(
            Parts.NeutronEmitter,
            1,
            EUT_UV,
            600,
            7,
            ItemList.Emitter_UV.get(1),
            plate(Materials.Neutronium, 2),
            of(OrePrefixes.dust, Materials.Naquadria, 2),
            circuit(Materials.UV, 1));
        part(
            Parts.AntimatterTrap,
            1,
            EUT_UHV,
            800,
            8,
            ItemList.Field_Generator_UHV.get(2),
            new ItemStack(Parts.PlasmaContainmentChamber, 1),
            plateDense(Materials.CosmicNeutronium, 1),
            circuit(Materials.UHV, 2));
        part(
            Parts.ExoticMatterStabilizer,
            1,
            EUT_UHV,
            800,
            9,
            ItemList.Sensor_UHV.get(1),
            ItemList.Field_Generator_UHV.get(1),
            plate(Materials.CosmicNeutronium, 4),
            circuit(Materials.UHV, 1));
    }

    // ==================== technology modules (UEV-MAX) ====================

    /** A UEV+ module's ingredients; crafted in the Extreme Crafting Table if present, else in the factory. */
    static final class EndgameModule {

        final Item output;
        final int eut;
        final ItemStack plate, circuit, field, emitter, sensor, previous, key, extra;

        EndgameModule(Item output, int eut, ItemStack plate, ItemStack circuit, ItemStack field, ItemStack emitter,
            ItemStack sensor, ItemStack previous, ItemStack key, ItemStack extra) {
            this.output = output;
            this.eut = eut;
            this.plate = plate;
            this.circuit = circuit;
            this.field = field;
            this.emitter = emitter;
            this.sensor = sensor;
            this.previous = previous;
            this.key = key;
            this.extra = extra;
        }

        boolean complete() {
            return valid(output, plate, circuit, field, emitter, sensor, previous, key, extra);
        }
    }

    private static void addEndgameModules() {
        EndgameModule[] modules = {
            new EndgameModule(
                Parts.QuantumEntanglementCore,
                EUT_UEV,
                plate(Materials.Infinity, 1),
                circuitTier(TIER_UEV, 1),
                item(ItemList.Field_Generator_UEV, 1),
                item(ItemList.Emitter_UEV, 1),
                item(ItemList.Sensor_UEV, 1),
                new ItemStack(Parts.ExoticMatterStabilizer),
                first(plateDense(Materials.CosmicNeutronium, 1), plate(Materials.CosmicNeutronium, 4)),
                first(
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 1),
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUHV, 1))),
            new EndgameModule(
                Parts.GravitonFieldEmitter,
                EUT_UIV,
                first(plate(Materials.TranscendentMetal, 1), plate(Materials.Infinity, 1)),
                circuitTier(TIER_UIV, 1),
                item(ItemList.Field_Generator_UIV, 1),
                item(ItemList.Emitter_UIV, 1),
                item(ItemList.Sensor_UIV, 1),
                new ItemStack(Parts.QuantumEntanglementCore),
                plate(Materials.Infinity, 1),
                first(
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 1),
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 1))),
            new EndgameModule(
                Parts.ChronoStabilizer,
                EUT_UMV,
                first(plate(Materials.SpaceTime, 1), plate(Materials.TranscendentMetal, 1)),
                circuitTier(TIER_UMV, 1),
                item(ItemList.Field_Generator_UMV, 1),
                item(ItemList.Emitter_UMV, 1),
                item(ItemList.Sensor_UMV, 1),
                new ItemStack(Parts.GravitonFieldEmitter),
                first(item(ItemList.Timepiece, 1), plate(Materials.TranscendentMetal, 1)),
                first(
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 1),
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 1))),
            new EndgameModule(
                Parts.SingularityContainmentUnit,
                EUT_UMV,
                first(plate(Materials.SpaceTime, 1), plate(Materials.TranscendentMetal, 1)),
                circuitTier(TIER_UMV, 1),
                item(ItemList.Field_Generator_UMV, 1),
                item(ItemList.Emitter_UMV, 1),
                item(ItemList.Sensor_UMV, 1),
                new ItemStack(Parts.GravitonFieldEmitter),
                new ItemStack(Parts.AntimatterTrap),
                first(
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 1),
                    of(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 1))),
            new EndgameModule(
                Parts.DimensionalRiftEmitter,
                EUT_UXV,
                first(plate(Materials.Universium, 1), plate(Materials.SpaceTime, 1)),
                circuitTier(TIER_UXV, 1),
                item(ItemList.Field_Generator_UXV, 1),
                item(ItemList.Emitter_UXV, 1),
                item(ItemList.Sensor_UXV, 1),
                new ItemStack(Parts.ChronoStabilizer),
                new ItemStack(Parts.SingularityContainmentUnit),
                first(plate(Materials.BlackDwarfMatter, 1), plate(Materials.WhiteDwarfMatter, 1))),
            new EndgameModule(
                Parts.RealityEngine,
                EUT_MAX,
                first(plate(Materials.MagMatter, 1), plate(Materials.Eternity, 1), plate(Materials.Universium, 1)),
                circuitTier(TIER_MAX, 1),
                item(ItemList.Field_Generator_MAX, 1),
                item(ItemList.Emitter_MAX, 1),
                item(ItemList.Sensor_MAX, 1),
                new ItemStack(Parts.DimensionalRiftEmitter),
                first(plate(Materials.Eternity, 1), plate(Materials.Universium, 1)),
                first(plate(Materials.Universium, 1), plate(Materials.SpaceTime, 1))) };

        int programmed = 10;
        for (EndgameModule m : modules) {
            programmed++;
            if (!m.complete()) continue;
            if (ExtremeRecipes.addModule(m)) continue;
            part(
                m.output,
                1,
                m.eut,
                1200,
                programmed,
                m.previous.copy(),
                m.key.copy(),
                copy(m.field, 2),
                copy(m.emitter, 1),
                copy(m.sensor, 1),
                copy(m.plate, 8),
                copy(m.circuit, 2),
                copy(m.extra, 4));
        }
    }

    // ==================== weapon chassis (UV-MAX, Extreme Crafting Table) ====================

    private static void addChassis() {
        chassis(
            Parts.CrystalMatrixChassis,
            EUT_UV,
            31,
            plate(Materials.Neutronium, 1),
            circuit(Materials.UV, 1),
            ItemList.Field_Generator_UV.get(1),
            plate(Materials.Osmiridium, 1),
            ExtremeRecipes.resource(ExtremeRecipes.CRYSTAL_MATRIX_INGOT, 1),
            ExtremeRecipes.resource(ExtremeRecipes.DIAMOND_LATTICE, 1));
        chassis(
            Parts.NeutroniumChassis,
            EUT_UHV,
            32,
            plate(Materials.CosmicNeutronium, 1),
            circuit(Materials.UHV, 1),
            ItemList.Field_Generator_UHV.get(1),
            of(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 1),
            ExtremeRecipes.resource(ExtremeRecipes.NEUTRONIUM_INGOT, 1),
            ExtremeRecipes.resource(ExtremeRecipes.NEUTRONIUM_GEAR, 1));
        chassis(
            Parts.InfinityChassis,
            EUT_UEV,
            33,
            plate(Materials.Infinity, 1),
            circuitTier(TIER_UEV, 1),
            item(ItemList.Field_Generator_UEV, 1),
            first(
                of(OrePrefixes.wireGt02, Materials.SuperconductorUEV, 1),
                of(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 1)),
            ExtremeRecipes.resource(ExtremeRecipes.INFINITY_INGOT, 1),
            ExtremeRecipes.resource(ExtremeRecipes.INFINITY_CATALYST, 1));
        chassis(
            Parts.SpacetimeChassis,
            EUT_UMV,
            34,
            first(plate(Materials.SpaceTime, 1), plate(Materials.TranscendentMetal, 1)),
            circuitTier(TIER_UMV, 1),
            item(ItemList.Field_Generator_UMV, 1),
            first(plate(Materials.TranscendentMetal, 1), plate(Materials.Infinity, 1)),
            ExtremeRecipes.resource(ExtremeRecipes.INFINITY_INGOT, 1),
            new ItemStack(Parts.InfinityChassis));
        chassis(
            Parts.EternalChassis,
            EUT_MAX,
            35,
            first(plate(Materials.MagMatter, 1), plate(Materials.Eternity, 1), plate(Materials.Universium, 1)),
            circuitTier(TIER_MAX, 1),
            item(ItemList.Field_Generator_MAX, 1),
            first(plate(Materials.Universium, 1), plate(Materials.SpaceTime, 1)),
            first(ExtremeRecipes.resourceBlock(1, 1), ExtremeRecipes.resource(ExtremeRecipes.INFINITY_INGOT, 1)),
            new ItemStack(Parts.SpacetimeChassis));
    }

    /**
     * A chassis is made only in the Extreme Crafting Table. Without Avaritia the factory makes it from the GregTech
     * ingredients so the weapons stay obtainable.
     */
    private static void chassis(Item output, int eut, int programmed, ItemStack edge, ItemStack circuit,
        ItemStack field, ItemStack secondary, ItemStack ingot, ItemStack core) {
        if (ExtremeRecipes.available()) {
            if (valid(output, edge, circuit, field, secondary, ingot, core)) {
                ExtremeRecipes.addChassis(output, edge, circuit, field, secondary, ingot, core);
            }
            return;
        }
        // without Avaritia its ingots and catalysts are null; a previous chassis core still applies
        ItemStack[] parts = core == null
            ? new ItemStack[] { copy(edge, 16), copy(circuit, 4), copy(field, 4), copy(secondary, 8) }
            : new ItemStack[] { copy(edge, 16), copy(circuit, 4), copy(field, 4), copy(secondary, 8), core.copy() };
        part(output, 1, eut, 1600, programmed % 24 + 1, parts);
    }

    /** The Avaritia chassis a UV-MAX weapon is assembled on, or null below UV. */
    private static Item chassisFor(Item gun) {
        if (gun == null) return null;
        if (gun == Guns.ParticleBeamRifle || gun == Guns.NeutronRifle) return Parts.CrystalMatrixChassis;
        if (gun == Guns.AntimatterCannon || gun == Guns.ExoticMatterRifle) return Parts.NeutroniumChassis;
        if (gun == Guns.QuantumPhaseRifle || gun == Guns.GravitonCannon) return Parts.InfinityChassis;
        if (gun == Guns.ChronoRifle || gun == Guns.SingularityCannon || gun == Guns.DimensionalCutter) {
            return Parts.SpacetimeChassis;
        }
        if (gun == Guns.RealityEraser) return Parts.EternalChassis;
        return null;
    }

    // ==================== EV: rail-assisted ====================

    private static void addEV() {
        gun(
            Guns.XR7,
            EUT_EV,
            1200,
            3,
            new ItemStack(CommonProxy.GunBarrelTitanium),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            new ItemStack(CommonProxy.GunStockCarbon),
            new ItemStack(Parts.GasPistonSystem),
            new ItemStack(Parts.SmartTargetingModule));
        gun(
            Guns.HX9,
            EUT_EV,
            1200,
            4,
            new ItemStack(CommonProxy.GunBarrelTitanium, 2),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            new ItemStack(CommonProxy.GunStockCarbon),
            new ItemStack(Parts.BeltFeedMechanism),
            new ItemStack(Parts.SmartTargetingModule),
            ItemList.Electric_Motor_EV.get(1));
    }

    // ==================== IV: electromagnetic ====================

    private static void addIV() {
        Materials m = Materials.TungstenSteel;
        gun(
            Guns.Coilgun,
            EUT_IV,
            1200,
            1,
            new ItemStack(Parts.ElectromagneticCoil, 2),
            new ItemStack(Parts.BlowbackBolt),
            plate(m, 4),
            circuit(Materials.IV, 1));
        gun(
            Guns.GaussRifle,
            EUT_IV,
            1400,
            2,
            new ItemStack(Parts.ElectromagneticCoil, 4),
            new ItemStack(Parts.PrecisionBoltAction),
            stickLong(m, 2),
            circuit(Materials.IV, 2));
        gun(
            Guns.Railgun,
            EUT_IV,
            1600,
            3,
            new ItemStack(Parts.SuperconductingRail, 2),
            new ItemStack(Parts.ElectromagneticCoil, 2),
            new ItemStack(Parts.SmartTargetingModule),
            plateDense(m, 2),
            circuit(Materials.IV, 2));

        ammo(Bullets.FerroSlug, 16, EUT_IV, 1, plate(m, 1), stick(Materials.Tungsten, 2));
        magazine(Magazines.CoilgunMag, m, EUT_IV, 11, null);
        magazine(Magazines.GaussRifleMag, m, EUT_IV, 12, null);
        magazine(Magazines.RailgunMag, m, EUT_IV, 13, null);
    }

    // ==================== LuV: laser ====================

    private static void addLuV() {
        Materials m = Materials.HSSS;
        gun(
            Guns.LaserRifle,
            EUT_LUV,
            1200,
            4,
            new ItemStack(Parts.FocusingLensArray, 2),
            new ItemStack(Parts.SmartTargetingModule),
            plate(m, 6),
            circuit(Materials.LuV, 2));
        ammo(Bullets.LaserCharge, 16, EUT_LUV, 2, plate(m, 1), of(OrePrefixes.lens, Materials.Ruby, 1));
        magazine(Magazines.LaserRifleCell, m, EUT_LUV, 14, circuit(Materials.LuV, 1));
    }

    // ==================== ZPM: plasma ====================

    private static void addZPM() {
        Materials m = Materials.NaquadahAlloy;
        gun(
            Guns.PlasmaRifle,
            EUT_ZPM,
            1400,
            5,
            new ItemStack(Parts.PlasmaContainmentChamber),
            new ItemStack(Parts.FocusingLensArray),
            plate(m, 6),
            circuit(Materials.ZPM, 2));
        gun(
            Guns.PlasmaCannon,
            EUT_ZPM,
            1600,
            6,
            new ItemStack(Parts.PlasmaContainmentChamber, 3),
            new ItemStack(Parts.ElectromagneticCoil, 2),
            plateDense(m, 2),
            circuit(Materials.ZPM, 4));

        ammo(Bullets.PlasmaCharge, 16, EUT_ZPM, 7, plate(m, 1), of(OrePrefixes.dust, Materials.Naquadah, 1));
        ammo(Bullets.PlasmaCannonCharge, 2, EUT_ZPM, 3, plate(m, 2), of(OrePrefixes.dust, Materials.Naquadria, 1));
        magazine(Magazines.PlasmaRifleCell, m, EUT_ZPM, 15, circuit(Materials.ZPM, 1));
    }

    // ==================== UV: particle ====================

    private static void addUV() {
        Materials m = Materials.Neutronium;
        gun(
            Guns.ParticleBeamRifle,
            EUT_UV,
            1600,
            7,
            new ItemStack(Parts.ParticleAcceleratorSegment, 3),
            new ItemStack(Parts.PrecisionBoltAction),
            new ItemStack(Parts.SmartTargetingModule),
            plate(m, 4),
            circuit(Materials.UV, 2));
        gun(
            Guns.NeutronRifle,
            EUT_UV,
            1600,
            8,
            new ItemStack(Parts.NeutronEmitter, 2),
            new ItemStack(Parts.BeltFeedMechanism),
            ItemList.Electric_Motor_UV.get(1),
            plate(m, 4),
            circuit(Materials.UV, 2));

        ammo(Bullets.ParticleCharge, 16, EUT_UV, 4, plate(m, 1), of(OrePrefixes.dust, Materials.Naquadria, 1));
        magazine(Magazines.ParticleBeamCell, m, EUT_UV, 16, circuit(Materials.UV, 1));
        magazine(Magazines.NeutronRifleCell, m, EUT_UV, 17, circuit(Materials.UV, 1));
    }

    // ==================== UHV: antimatter ====================

    private static void addUHV() {
        Materials m = Materials.CosmicNeutronium;
        gun(
            Guns.AntimatterCannon,
            EUT_UHV,
            2000,
            9,
            new ItemStack(Parts.AntimatterTrap, 2),
            new ItemStack(Parts.PlasmaContainmentChamber),
            new ItemStack(Parts.SuperconductingRail),
            plateDense(m, 2),
            circuit(Materials.UHV, 4));
        gun(
            Guns.ExoticMatterRifle,
            EUT_UHV,
            1800,
            10,
            new ItemStack(Parts.ExoticMatterStabilizer, 2),
            new ItemStack(Parts.ParticleAcceleratorSegment),
            plate(m, 4),
            circuit(Materials.UHV, 2));

        ammo(Bullets.ExoticCharge, 16, EUT_UHV, 5, plate(m, 1), of(OrePrefixes.dust, Materials.Neutronium, 1));
        ammo(Bullets.AntimatterCharge, 1, EUT_UHV, 6, plate(m, 2), plateDense(Materials.Neutronium, 1));
        magazine(Magazines.ExoticMatterCell, m, EUT_UHV, 18, circuit(Materials.UHV, 1));
    }

    // ==================== UEV: quantum ====================

    private static void addUEV() {
        ItemStack plates = plate(Materials.Infinity, 6);
        gun(
            Guns.QuantumPhaseRifle,
            EUT_UEV,
            2400,
            11,
            new ItemStack(Parts.QuantumEntanglementCore, 2),
            new ItemStack(Parts.SmartTargetingModule),
            plates,
            circuitTier(TIER_UEV, 2));
        ammo(
            Bullets.QuantumCharge,
            16,
            EUT_UEV,
            8,
            plate(Materials.Infinity, 1),
            of(OrePrefixes.dust, Materials.CosmicNeutronium, 1));
        magazineOf(Magazines.QuantumCell, plate(Materials.Infinity, 3), EUT_UEV, 19, circuitTier(TIER_UEV, 1));
    }

    // ==================== UIV: gravity ====================

    private static void addUIV() {
        ItemStack metal = first(plate(Materials.TranscendentMetal, 1), plate(Materials.Infinity, 1));
        gun(
            Guns.GravitonCannon,
            EUT_UIV,
            2400,
            12,
            new ItemStack(Parts.GravitonFieldEmitter, 2),
            new ItemStack(Parts.QuantumEntanglementCore),
            copy(metal, 6),
            circuitTier(TIER_UIV, 2));
        ammo(Bullets.GravitonCharge, 16, EUT_UIV, 9, copy(metal, 1), plate(Materials.Infinity, 1));
        magazineOf(Magazines.GravitonCore, copy(metal, 3), EUT_UIV, 20, circuitTier(TIER_UIV, 1));
    }

    // ==================== UMV: spacetime ====================

    private static void addUMV() {
        ItemStack metal = first(plate(Materials.SpaceTime, 1), plate(Materials.TranscendentMetal, 1));
        gun(
            Guns.ChronoRifle,
            EUT_UMV,
            2400,
            13,
            new ItemStack(Parts.ChronoStabilizer, 2),
            new ItemStack(Parts.PrecisionBoltAction),
            copy(metal, 6),
            circuitTier(TIER_UMV, 2));
        gun(
            Guns.SingularityCannon,
            EUT_UMV,
            2400,
            14,
            new ItemStack(Parts.SingularityContainmentUnit, 2),
            new ItemStack(Parts.GravitonFieldEmitter),
            copy(metal, 8),
            circuitTier(TIER_UMV, 2));
        ammo(Bullets.ChronoCharge, 16, EUT_UMV, 10, copy(metal, 1), plate(Materials.TranscendentMetal, 1));
        ammo(Bullets.SingularityCharge, 1, EUT_UMV, 11, copy(metal, 2), of(OrePrefixes.dust, Materials.Neutronium, 4));
        magazineOf(Magazines.TimeCrystal, copy(metal, 3), EUT_UMV, 21, circuitTier(TIER_UMV, 1));
    }

    // ==================== UXV: dimensional ====================

    private static void addUXV() {
        ItemStack metal = first(plate(Materials.Universium, 1), plate(Materials.SpaceTime, 1));
        gun(
            Guns.DimensionalCutter,
            EUT_UXV,
            2400,
            15,
            new ItemStack(Parts.DimensionalRiftEmitter, 2),
            new ItemStack(Parts.ChronoStabilizer),
            copy(metal, 6),
            circuitTier(TIER_UXV, 2));
        ammo(Bullets.DimensionalCharge, 8, EUT_UXV, 12, copy(metal, 1), plate(Materials.SpaceTime, 1));
        magazineOf(Magazines.DimensionalCore, copy(metal, 3), EUT_UXV, 22, circuitTier(TIER_UXV, 1));
    }

    // ==================== MAX: ultimate ====================

    private static void addMAX() {
        ItemStack metal = first(
            plate(Materials.MagMatter, 1),
            plate(Materials.Eternity, 1),
            plate(Materials.Universium, 1));
        gun(
            Guns.RealityEraser,
            EUT_MAX,
            3000,
            16,
            new ItemStack(Parts.RealityEngine),
            new ItemStack(Parts.DimensionalRiftEmitter, 2),
            new ItemStack(Parts.SingularityContainmentUnit),
            copy(metal, 8),
            circuitTier(TIER_MAX, 4));
        ammo(
            Bullets.InfinityCharge,
            1,
            EUT_MAX,
            13,
            copy(metal, 2),
            first(plate(Materials.Eternity, 1), plate(Materials.Universium, 1)));
        magazineOf(Magazines.InfinityCatalyst, copy(metal, 3), EUT_MAX, 23, circuitTier(TIER_MAX, 1));
    }

    // ==================== helpers ====================

    static ItemStack of(OrePrefixes prefix, Materials m, int n) {
        return m == null ? null : GTOreDictUnificator.get(prefix, m, n);
    }

    static ItemStack plate(Materials m, int n) {
        return of(OrePrefixes.plate, m, n);
    }

    private static ItemStack plateDense(Materials m, int n) {
        return of(OrePrefixes.plateDense, m, n);
    }

    private static ItemStack stick(Materials m, int n) {
        return of(OrePrefixes.stick, m, n);
    }

    private static ItemStack stickLong(Materials m, int n) {
        return of(OrePrefixes.stickLong, m, n);
    }

    private static ItemStack circuit(Materials tier, int n) {
        return of(OrePrefixes.circuit, tier, n);
    }

    /** A circuit of a UEV+ tier: the ore-dictionary circuit if registered, else that tier's mainframe. */
    static ItemStack circuitTier(int tier, int n) {
        Materials[] tiers = { Materials.UEV, Materials.UIV, Materials.UMV, Materials.UXV, Materials.MAX };
        ItemList[] mainframes = { ItemList.Circuit_Biomainframe, ItemList.Circuit_OpticalMainframe,
            ItemList.Circuit_ExoticMainframe, ItemList.Circuit_CosmicMainframe,
            ItemList.Circuit_TranscendentMainframe };
        int k = tier - TIER_UEV;
        if (k < 0 || k >= tiers.length) return null;
        return first(circuit(tiers[k], n), item(mainframes[k], n));
    }

    /** An ItemList entry, or null when it has not been registered in this pack. */
    static ItemStack item(ItemList entry, int n) {
        return entry.hasBeenSet() ? entry.get(n) : null;
    }

    /** The first non-null stack. */
    static ItemStack first(ItemStack... options) {
        for (ItemStack s : options) {
            if (s != null && s.getItem() != null) return s;
        }
        return null;
    }

    static ItemStack copy(ItemStack stack, int n) {
        if (stack == null) return null;
        ItemStack c = stack.copy();
        c.stackSize = n;
        return c;
    }

    static boolean valid(Item output, ItemStack... inputs) {
        if (output == null) return false;
        for (ItemStack s : inputs) {
            if (s == null || s.getItem() == null) return false;
        }
        return true;
    }

    private static ItemStack[] withCircuit(int programmed, ItemStack... parts) {
        ItemStack[] inputs = new ItemStack[parts.length + 1];
        System.arraycopy(parts, 0, inputs, 0, parts.length);
        inputs[parts.length] = GTUtility.getIntegratedCircuit(programmed);
        return inputs;
    }

    private static void part(Item part, int count, int eut, int duration, int programmed, ItemStack... inputs) {
        if (!valid(part, inputs)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(withCircuit(programmed, inputs))
            .itemOutputs(new ItemStack(part, count))
            .duration(duration)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }

    private static void gun(Item gun, int eut, int duration, int programmed, ItemStack... parts) {
        Item frame = chassisFor(gun);
        if (frame != null) {
            ItemStack[] withFrame = new ItemStack[parts.length + 1];
            System.arraycopy(parts, 0, withFrame, 0, parts.length);
            withFrame[parts.length] = new ItemStack(frame);
            parts = withFrame;
        }
        if (!valid(gun, parts)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(withCircuit(programmed, parts))
            .fluidInputs(Materials.SolderingAlloy.getMolten(eut >= EUT_UEV ? 2304 : 1152))
            .itemOutputs(new ItemStack(gun))
            .duration(duration)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }

    private static void ammo(Item ammo, int count, int eut, int programmed, ItemStack... parts) {
        if (!valid(ammo, parts)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(withCircuit(programmed, parts))
            .itemOutputs(new ItemStack(ammo, count))
            .duration(100)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }

    /** Magazine or cell from three plates of the tier material; energy cells also need a tier circuit. */
    private static void magazine(Item magazine, Materials m, int eut, int programmed, ItemStack extra) {
        magazineOf(magazine, plate(m, 3), eut, programmed, extra);
    }

    private static void magazineOf(Item magazine, ItemStack plates, int eut, int programmed, ItemStack extra) {
        ItemStack[] parts = extra == null ? new ItemStack[] { plates } : new ItemStack[] { plates, extra };
        if (!valid(magazine, parts)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(withCircuit(programmed, parts))
            .itemOutputs(new ItemStack(magazine))
            .duration(200)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }
}
