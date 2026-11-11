package com.gtnewhorizon.newgunrizons.registry;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

/** Recipe map of the Drone Assembly Complex. */
public final class DroneRecipeMap {

    public static final RecipeMap<RecipeMapBackend> DRONES = RecipeMapBuilder.of("newgunrizons.recipe.drones")
        .maxIO(12, 1, 1, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ASSEMBLE)
        .build();

    private DroneRecipeMap() {}
}
