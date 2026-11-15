package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/**
 * GregTech Assembler recipes for every item the mod currently registers.
 * <p>
 * Only touch this class after checking {@code Loader.isModLoaded("gregtech")}: it references
 * GregTech classes directly, and GregTech is a compile-only dependency.
 * <p>
 * Tier layout (matches the "NewGunrizons: Arsenal" quest line):
 * <ul>
 * <li>LV - 12 Gauge Shotgun Shells</li>
 * <li>MV - Doom Shotgun</li>
 * <li>EV - Plasma Rifle</li>
 * <li>IV - Laser Rifle</li>
 * </ul>
 */
public final class GTRecipes {

    private GTRecipes() {}

    public static void init() {
        addAmmoRecipes();
        addWeaponRecipes();
    }

    private static void addAmmoRecipes() {
        // 8x 12 Gauge Shotgun Shell: brass case + gunpowder.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Brass, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gunpowder, 2),
                GTUtility.getIntegratedCircuit(8))
            .itemOutputs(new ItemStack(Bullets.ShotgunShell, 8))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void addWeaponRecipes() {
        // MV: Doom Shotgun - steel body, MV piston for the pump action.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Steel, 4),
                ItemList.Electric_Piston_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 2))
            .itemOutputs(new ItemStack(Guns.DoomShotgun))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(30 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.assemblerRecipes);

        // EV: Plasma Rifle - titanium body, field generator to contain the plasma.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 8),
                ItemList.Electric_Motor_EV.get(1),
                ItemList.Emitter_EV.get(2),
                ItemList.Field_Generator_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2))
            .itemOutputs(new ItemStack(Guns.PlasmaRifle))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(60 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        // IV: Laser Rifle - tungstensteel body, ruby lens focuses the beam.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 8),
                ItemList.Emitter_IV.get(2),
                ItemList.Sensor_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Ruby, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2))
            .itemOutputs(new ItemStack(Guns.LaserRifle))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
            .duration(90 * 20)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
