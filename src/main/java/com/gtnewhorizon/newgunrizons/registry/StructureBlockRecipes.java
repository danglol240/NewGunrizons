package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.blocks.BlockTiered;
import com.gtnewhorizon.newgunrizons.blocks.NGBlocks;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

/**
 * Assembler recipes for the tiered structure blocks: each tier's Assembly Frame and Landing Pads are made of that
 * tier's signature material (the material named in {@link BlockTiered#MATERIALS}).
 */
public final class StructureBlockRecipes {

    private StructureBlockRecipes() {}

    private static Materials[][] materials() {
        // signature material first, then fallbacks if a pack lacks its plates
        return new Materials[][] { { Materials.Steel }, { Materials.Aluminium }, { Materials.StainlessSteel },
            { Materials.Titanium }, { Materials.TungstenSteel }, { Materials.HSSS }, { Materials.NaquadahAlloy },
            { Materials.Neutronium }, { Materials.CosmicNeutronium, Materials.Neutronium },
            { Materials.Infinity, Materials.CosmicNeutronium }, { Materials.TranscendentMetal, Materials.Infinity },
            { Materials.SpaceTime, Materials.TranscendentMetal }, { Materials.Universium, Materials.SpaceTime },
            { Materials.MagMatter, Materials.Eternity, Materials.Universium } };
    }

    public static void init() {
        if (NGBlocks.TierFrame == null) return;
        Materials[][] mats = materials();
        for (int i = 0; i < BlockTiered.TIERS; i++) {
            int tier = i + 1;
            ItemStack plate = null, frameBox = null;
            for (Materials m : mats[i]) {
                plate = HighTierRecipes.first(plate, HighTierRecipes.plate(m, 1));
                frameBox = HighTierRecipes.first(frameBox, HighTierRecipes.of(OrePrefixes.frameGt, m, 1));
            }
            if (plate == null) continue;
            if (frameBox == null) frameBox = HighTierRecipes.copy(plate, 2);
            ItemStack circuit = tier >= 10 ? HighTierRecipes.circuitTier(tier, 1) : circuit(tier);
            long eut = 30L << (2 * (tier - 1));
            if (circuit != null) {
                GTValues.RA.stdBuilder()
                    .itemInputs(
                        HighTierRecipes.copy(plate, 6),
                        frameBox.copy(),
                        circuit,
                        GTUtility.getIntegratedCircuit(4))
                    .itemOutputs(new ItemStack(NGBlocks.TierFrame, 2, i))
                    .duration(200)
                    .eut(eut)
                    .addTo(RecipeMaps.assemblerRecipes);
            }
            GTValues.RA.stdBuilder()
                .itemInputs(HighTierRecipes.copy(plate, 4), frameBox.copy(), GTUtility.getIntegratedCircuit(5))
                .itemOutputs(new ItemStack(NGBlocks.LandingPad, 8, i))
                .duration(160)
                .eut(eut)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }

    private static ItemStack circuit(int tier) {
        Materials[] tiers = { null, Materials.LV, Materials.MV, Materials.HV, Materials.EV, Materials.IV, Materials.LuV,
            Materials.ZPM, Materials.UV, Materials.UHV };
        return HighTierRecipes.of(OrePrefixes.circuit, tiers[tier], 1);
    }
}
