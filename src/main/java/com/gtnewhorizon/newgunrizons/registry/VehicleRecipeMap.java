package com.gtnewhorizon.newgunrizons.registry;

import java.util.Collections;

import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

/** Recipe map of the Vehicle Assembly Hangar. The recipe's special value is the hangar era (bays) it needs. */
public final class VehicleRecipeMap {

    public static final RecipeMap<RecipeMapBackend> VEHICLES = RecipeMapBuilder.of("newgunrizons.recipe.vehicles")
        .maxIO(12, 1, 1, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ASSEMBLE)
        .neiSpecialInfoFormatter(info -> {
            int bays = info.recipe.mSpecialValue;
            String era = bays >= 1 && bays < GTValues.VN.length ? GTValues.VN[bays] : "?";
            return Collections.singletonList("Needs " + bays + " hangar bays (" + era + ")");
        })
        .build();

    private VehicleRecipeMap() {}
}
