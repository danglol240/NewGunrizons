package com.gtnewhorizon.newgunrizons.vehicle;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

/**
 * What a vehicle runs on. Every vehicle keeps its energy as EU; liquid fuels are burned into it when they are poured
 * in, at the fuel's GregTech generator value.
 */
public enum FuelType {

    /** Combustion fuels (diesel, gasoline, ...), poured in from cells or buckets. */
    DIESEL("Diesel", true, false, false),
    /** Combustion fuels or a GregTech charger. */
    HYBRID("Diesel / EU", true, true, false),
    /** A GregTech charger only. */
    EU("EU", false, true, false),
    /** Plasma (from cells) or a GregTech charger. */
    PLASMA("Plasma / EU", false, true, true);

    public final String label;
    public final boolean combustion;
    public final boolean electric;
    public final boolean plasma;

    FuelType(String label, boolean combustion, boolean electric, boolean plasma) {
        this.label = label;
        this.combustion = combustion;
        this.electric = electric;
        this.plasma = plasma;
    }

    /** EU released by one litre of the fluid, or 0 if this vehicle cannot burn it. */
    public long euPerLitre(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) return 0L;
        GTRecipe fuel = null;
        if (combustion) fuel = RecipeMaps.dieselFuels.getBackend()
            .findFuel(fluid);
        if (fuel == null && plasma) fuel = RecipeMaps.plasmaFuels.getBackend()
            .findFuel(fluid);
        return fuel == null ? 0L : Math.max(0L, fuel.mSpecialValue);
    }
}
