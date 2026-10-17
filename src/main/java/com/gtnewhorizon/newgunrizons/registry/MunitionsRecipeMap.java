package com.gtnewhorizon.newgunrizons.registry;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

/** Recipe map of the Munitions Factory; every NewGunrizons recipe lives here. */
public final class MunitionsRecipeMap {

    public static final RecipeMap<RecipeMapBackend> MUNITIONS = RecipeMapBuilder.of("newgunrizons.recipe.munitions")
        .maxIO(9, 2, 1, 0)
        .minInputs(1, 0)
        .slotOverlays(
            (index, isFluid, isOutput, isSpecial) -> !isFluid && !isOutput ? GTUITextures.OVERLAY_SLOT_CIRCUIT : null)
        .progressBar(GTUITextures.PROGRESSBAR_ASSEMBLE)
        .build();

    private MunitionsRecipeMap() {}
}
