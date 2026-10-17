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
 * Munitions Factory recipes for the weapon-class mechanisms, the IV-UHV technology modules, the IV-UHV weapons and
 * their magazines/cells and ammo.
 * <p>
 * Each high-tier weapon is built from the modules that match how it works (coils for the coilgun, superconducting
 * rails for the railgun, lens arrays for the laser...), and later modules reuse earlier ones, so the weapons form a
 * progression rather than a list of reskins.
 * <p>
 * Registered in post-init so the tiered circuit ore-dictionary entries added by GregTech and the GTNH core mod are
 * already present.
 */
public final class HighTierRecipes {

    private static final int EUT_LV = 30;
    private static final int EUT_MV = 120;
    private static final int EUT_IV = 7_680;
    private static final int EUT_LUV = 30_720;
    private static final int EUT_ZPM = 122_880;
    private static final int EUT_UV = 491_520;
    private static final int EUT_UHV = 1_966_080;

    private HighTierRecipes() {}

    public static void init() {
        if (!Loader.isModLoaded("gregtech")) return;
        addMechanisms();
        addModules();
        addIV();
        addLuV();
        addZPM();
        addUV();
        addUHV();
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

    // ==================== technology modules (IV-UHV) ====================

    private static void addModules() {
        part(
            Parts.SmartTargetingModule,
            1,
            EUT_IV,
            400,
            1,
            ItemList.Sensor_IV.get(1),
            circuit(Materials.IV, 1),
            of(OrePrefixes.lens, Materials.Diamond, 1),
            plate(Materials.TungstenSteel, 2));
        part(
            Parts.ElectromagneticCoil,
            1,
            EUT_LUV,
            400,
            2,
            stick(Materials.SteelMagnetic, 4),
            of(OrePrefixes.wireFine, Materials.Platinum, 16),
            plate(Materials.HSSS, 2),
            circuit(Materials.LuV, 1));
        part(
            Parts.SuperconductingRail,
            1,
            EUT_LUV,
            600,
            3,
            of(OrePrefixes.stickLong, Materials.HSSS, 2),
            plate(Materials.Iridium, 2),
            ItemList.Field_Generator_LuV.get(1),
            circuit(Materials.LuV, 1));
        part(
            Parts.FocusingLensArray,
            1,
            EUT_ZPM,
            500,
            4,
            of(OrePrefixes.lens, Materials.Ruby, 2),
            of(OrePrefixes.lens, Materials.Diamond, 1),
            ItemList.Emitter_ZPM.get(1),
            plate(Materials.NaquadahAlloy, 2));
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

    // ==================== IV: advanced military ====================

    private static void addIV() {
        gun(
            Guns.XR7,
            EUT_IV,
            1200,
            3,
            new ItemStack(CommonProxy.GunBarrelTungstenSteel),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            new ItemStack(CommonProxy.GunStockCarbon),
            new ItemStack(Parts.GasPistonSystem),
            new ItemStack(Parts.SmartTargetingModule));
        gun(
            Guns.HX9,
            EUT_IV,
            1200,
            4,
            new ItemStack(CommonProxy.GunBarrelTungstenSteel, 2),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            new ItemStack(CommonProxy.GunStockCarbon),
            new ItemStack(Parts.BeltFeedMechanism),
            new ItemStack(Parts.SmartTargetingModule),
            ItemList.Electric_Motor_IV.get(1));
    }

    // ==================== LuV: electromagnetic ====================

    private static void addLuV() {
        Materials m = Materials.HSSS;
        gun(
            Guns.Coilgun,
            EUT_LUV,
            1200,
            1,
            new ItemStack(Parts.ElectromagneticCoil, 2),
            new ItemStack(Parts.BlowbackBolt),
            plate(m, 4),
            circuit(Materials.LuV, 1));
        gun(
            Guns.GaussRifle,
            EUT_LUV,
            1400,
            2,
            new ItemStack(Parts.ElectromagneticCoil, 4),
            new ItemStack(Parts.PrecisionBoltAction),
            stickLong(m, 2),
            circuit(Materials.LuV, 2));
        gun(
            Guns.Railgun,
            EUT_LUV,
            1600,
            3,
            new ItemStack(Parts.SuperconductingRail, 2),
            new ItemStack(Parts.ElectromagneticCoil, 2),
            new ItemStack(Parts.SmartTargetingModule),
            plateDense(m, 2),
            circuit(Materials.LuV, 2));

        ammo(
            Bullets.FerroSlug,
            16,
            EUT_LUV,
            1,
            plate(m, 1),
            GTOreDictUnificator.get(OrePrefixes.stick, Materials.SteelMagnetic, 2));
        magazine(Magazines.CoilgunMag, m, EUT_LUV, 11);
        magazine(Magazines.GaussRifleMag, m, EUT_LUV, 12);
        magazine(Magazines.RailgunMag, m, EUT_LUV, 13);
    }

    // ==================== ZPM: directed energy ====================

    private static void addZPM() {
        Materials m = Materials.NaquadahAlloy;
        gun(
            Guns.LaserRifle,
            EUT_ZPM,
            1200,
            4,
            new ItemStack(Parts.FocusingLensArray, 2),
            new ItemStack(Parts.SmartTargetingModule),
            plate(m, 6),
            circuit(Materials.ZPM, 2));
        gun(
            Guns.PlasmaRifle,
            EUT_ZPM,
            1400,
            5,
            new ItemStack(Parts.PlasmaContainmentChamber),
            new ItemStack(Parts.FocusingLensArray),
            new ItemStack(Parts.GasPistonSystem),
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

        ammo(
            Bullets.LaserCharge,
            16,
            EUT_ZPM,
            2,
            plate(m, 1),
            GTOreDictUnificator.get(OrePrefixes.lens, Materials.Ruby, 1));
        ammo(
            Bullets.PlasmaCannonCharge,
            2,
            EUT_ZPM,
            3,
            plate(m, 2),
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1));
        magazine(Magazines.LaserRifleCell, m, EUT_ZPM, 14);
        magazine(Magazines.PlasmaRifleCell, m, EUT_ZPM, 15);
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

        ammo(
            Bullets.ParticleCharge,
            16,
            EUT_UV,
            4,
            plate(m, 1),
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1));
        magazine(Magazines.ParticleBeamCell, m, EUT_UV, 16);
        magazine(Magazines.NeutronRifleCell, m, EUT_UV, 17);
    }

    // ==================== UHV: exotic ====================

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
            new ItemStack(Parts.GasPistonSystem),
            plate(m, 4),
            circuit(Materials.UHV, 2));

        ammo(
            Bullets.ExoticCharge,
            16,
            EUT_UHV,
            5,
            plate(m, 1),
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1));
        ammo(
            Bullets.AntimatterCharge,
            1,
            EUT_UHV,
            6,
            plate(m, 2),
            GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 1));
        magazine(Magazines.ExoticMatterCell, m, EUT_UHV, 18);
    }

    // ==================== helpers ====================

    private static ItemStack of(OrePrefixes prefix, Materials m, int n) {
        return GTOreDictUnificator.get(prefix, m, n);
    }

    private static ItemStack plate(Materials m, int n) {
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

    private static boolean valid(Item output, ItemStack... inputs) {
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
        if (!valid(gun, parts)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(withCircuit(programmed, parts))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
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

    private static void magazine(Item magazine, Materials m, int eut, int programmed) {
        ItemStack plates = plate(m, 3);
        if (!valid(magazine, plates)) return;
        GTValues.RA.stdBuilder()
            .itemInputs(plates, GTUtility.getIntegratedCircuit(programmed))
            .itemOutputs(new ItemStack(magazine))
            .duration(200)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }
}
