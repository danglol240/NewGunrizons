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
 * Munitions Factory recipes for the IV-UHV weapons, their magazines/cells and ammo.
 * <p>
 * Registered in post-init so the tiered circuit ore-dictionary entries added by
 * GregTech and the GTNH core mod are already present.
 */
public final class HighTierRecipes {

    private static final int EUT_IV = 7_680;
    private static final int EUT_LUV = 30_720;
    private static final int EUT_ZPM = 122_880;
    private static final int EUT_UV = 491_520;
    private static final int EUT_UHV = 1_966_080;

    private HighTierRecipes() {}

    public static void init() {
        if (!Loader.isModLoaded("gregtech")) return;
        addIV();
        addLuV();
        addZPM();
        addUV();
        addUHV();
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
            new ItemStack(CommonProxy.FiringMechanismAdvanced),
            new ItemStack(CommonProxy.GunStockCarbon),
            ItemList.Sensor_IV.get(1),
            circuit(Materials.IV, 2));
        gun(
            Guns.HX9,
            EUT_IV,
            1200,
            4,
            new ItemStack(CommonProxy.GunBarrelTungstenSteel, 2),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            new ItemStack(CommonProxy.FiringMechanismAdvanced),
            new ItemStack(CommonProxy.GunStockCarbon),
            ItemList.Electric_Motor_IV.get(1),
            circuit(Materials.IV, 2));
    }

    // ==================== LuV: electromagnetic ====================

    private static void addLuV() {
        Materials m = Materials.HSSS;
        gun(
            Guns.Coilgun,
            EUT_LUV,
            1200,
            1,
            plate(m, 4),
            new ItemStack(CommonProxy.GunReceiverTitanium),
            ItemList.Electric_Motor_LuV.get(2),
            circuit(Materials.LuV, 2));
        gun(
            Guns.GaussRifle,
            EUT_LUV,
            1400,
            2,
            plate(m, 6),
            stickLong(m, 2),
            ItemList.Field_Generator_LuV.get(1),
            circuit(Materials.LuV, 2));
        gun(
            Guns.Railgun,
            EUT_LUV,
            1600,
            3,
            plateDense(m, 2),
            stickLong(m, 4),
            ItemList.Field_Generator_LuV.get(2),
            circuit(Materials.LuV, 4));

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
            plate(m, 6),
            ItemList.Emitter_ZPM.get(2),
            ItemList.Sensor_ZPM.get(1),
            circuit(Materials.ZPM, 2));
        gun(
            Guns.PlasmaRifle,
            EUT_ZPM,
            1400,
            5,
            plate(m, 6),
            ItemList.Emitter_ZPM.get(1),
            ItemList.Field_Generator_ZPM.get(1),
            circuit(Materials.ZPM, 2));
        gun(
            Guns.PlasmaCannon,
            EUT_ZPM,
            1600,
            6,
            plateDense(m, 2),
            ItemList.Field_Generator_ZPM.get(2),
            ItemList.Emitter_ZPM.get(1),
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
            plate(m, 6),
            ItemList.Field_Generator_UV.get(1),
            ItemList.Emitter_UV.get(1),
            circuit(Materials.UV, 2));
        gun(
            Guns.NeutronRifle,
            EUT_UV,
            1600,
            8,
            plate(m, 6),
            ItemList.Electric_Motor_UV.get(1),
            ItemList.Emitter_UV.get(1),
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
            plateDense(m, 2),
            ItemList.Field_Generator_UHV.get(2),
            ItemList.Emitter_UHV.get(1),
            circuit(Materials.UHV, 4));
        gun(
            Guns.ExoticMatterRifle,
            EUT_UHV,
            1800,
            10,
            plate(m, 6),
            ItemList.Field_Generator_UHV.get(1),
            ItemList.Sensor_UHV.get(1),
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

    private static ItemStack plate(Materials m, int n) {
        return GTOreDictUnificator.get(OrePrefixes.plate, m, n);
    }

    private static ItemStack plateDense(Materials m, int n) {
        return GTOreDictUnificator.get(OrePrefixes.plateDense, m, n);
    }

    private static ItemStack stickLong(Materials m, int n) {
        return GTOreDictUnificator.get(OrePrefixes.stickLong, m, n);
    }

    private static ItemStack circuit(Materials tier, int n) {
        return GTOreDictUnificator.get(OrePrefixes.circuit, tier, n);
    }

    private static boolean valid(Item output, ItemStack... inputs) {
        if (output == null) return false;
        for (ItemStack s : inputs) {
            if (s == null) return false;
        }
        return true;
    }

    private static void gun(Item gun, int eut, int duration, int programmed, ItemStack... parts) {
        if (!valid(gun, parts)) return;
        ItemStack[] inputs = new ItemStack[parts.length + 1];
        System.arraycopy(parts, 0, inputs, 0, parts.length);
        inputs[parts.length] = GTUtility.getIntegratedCircuit(programmed);
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
            .itemOutputs(new ItemStack(gun))
            .duration(duration)
            .eut(eut)
            .addTo(MunitionsRecipeMap.MUNITIONS);
    }

    private static void ammo(Item ammo, int count, int eut, int programmed, ItemStack... parts) {
        if (!valid(ammo, parts)) return;
        ItemStack[] inputs = new ItemStack[parts.length + 1];
        System.arraycopy(parts, 0, inputs, 0, parts.length);
        inputs[parts.length] = GTUtility.getIntegratedCircuit(programmed);
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
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
