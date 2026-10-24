package com.gtnewhorizon.newgunrizons.registry;

import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.CommonProxy;
import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.items.ItemVehicleComponent;
import com.gtnewhorizon.newgunrizons.items.ItemVehicleComponent.Kind;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.recipe.Scanning;

/**
 * Vehicle recipes, GregTech style: every vehicle is assembled in the Vehicle Assembly Hangar from five parts of its
 * tier (chassis, armour, engine, controls, drive) plus the weapon hardware of that tier. The parts are made like
 * GregTech components: in an Assembler up to IV, and from LuV on in an Assembly Line, researched from the part of
 * the tier below.
 */
public final class VehicleRecipes {

    private static final Logger LOG = LogManager.getLogger("NewGunrizons");
    private static final String[] T = { "", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV",
        "UMV", "UXV", "MAX" };
    /** The signature metal of each tier, as used by the guns and drones. */
    private static final Materials[] METAL = { null, Materials.Steel, Materials.Aluminium, Materials.StainlessSteel,
        Materials.Titanium, Materials.TungstenSteel, Materials.HSSS, Materials.NaquadahAlloy, Materials.Neutronium,
        Materials.CosmicNeutronium, Materials.Infinity, Materials.TranscendentMetal, Materials.SpaceTime,
        Materials.Universium, Materials.MagMatter };

    private static int partRecipes, vehicleRecipes;

    private VehicleRecipes() {}

    public static void init() {
        for (int t = 1; t <= ItemVehicleComponent.TIERS; t++) {
            chassis(t);
            armor(t);
            engine(t);
            control(t);
            drive(t);
        }
        for (ItemVehicle v : Vehicles.all()) vehicle(v);
        LOG.info("Registered {} vehicle part recipes and {} vehicle recipes", partRecipes, vehicleRecipes);
    }

    // ---------------- parts ----------------

    private static void chassis(int t) {
        Materials m = METAL[t];
        part(
            Kind.CHASSIS,
            t,
            solder(t),
            first(plate(m, 8), plate(Materials.Steel, 8)),
            first(of(OrePrefixes.frameGt, m, 4), of(OrePrefixes.stickLong, m, 4), plate(m, 4)),
            first(of(OrePrefixes.stickLong, m, 4), of(OrePrefixes.stick, m, 8), plate(m, 4)),
            first(of(OrePrefixes.gearGt, m, 2), plate(m, 2)),
            circuit(t, 1));
    }

    private static void armor(int t) {
        Materials m = METAL[t];
        Materials next = METAL[Math.min(ItemVehicleComponent.TIERS, t + 1)];
        part(
            Kind.ARMOR,
            t,
            solder(t),
            first(of(OrePrefixes.plateDense, m, 2), plate(m, 8)),
            first(plate(next, 2), plate(m, 4)),
            first(of(OrePrefixes.screw, m, 8), of(OrePrefixes.bolt, m, 8), plate(m, 2)),
            t >= 7 ? first(itemList("Field_Generator_" + T[t], 1), circuit(t, 1)) : null);
    }

    private static void engine(int t) {
        Materials m = METAL[t];
        FluidStack lube = Materials.Lubricant.getFluid(250L * t);
        if (t <= 4) {
            part(
                Kind.ENGINE,
                t,
                lube,
                itemList("Electric_Piston_" + T[t], 4),
                first(of(OrePrefixes.gearGt, m, 2), plate(m, 4)),
                first(of(OrePrefixes.pipeMedium, m, 2), of(OrePrefixes.pipeMedium, Materials.Steel, 2)),
                first(of(OrePrefixes.rotor, m, 1), plate(m, 2)),
                circuit(t, 1));
        } else if (t <= 6) {
            part(
                Kind.ENGINE,
                t,
                lube,
                itemList("Electric_Motor_" + T[t], 4),
                first(of(OrePrefixes.gearGt, m, 2), plate(m, 4)),
                first(of(OrePrefixes.plateDense, m, 2), plate(m, 8)),
                circuit(t, 2));
        } else {
            part(
                Kind.ENGINE,
                t,
                lube,
                itemList("Field_Generator_" + T[t], 2),
                itemList("Electric_Motor_" + T[t], 2),
                first(of(OrePrefixes.plateDense, m, 2), plate(m, 8)),
                core(t),
                circuit(t, 2));
        }
    }

    private static void control(int t) {
        Materials m = METAL[t];
        part(
            Kind.CONTROL,
            t,
            solder(t),
            circuit(t, 2),
            itemList("Sensor_" + T[t], 1),
            itemList("Emitter_" + T[t], 1),
            first(itemList("Cover_Screen", 1), plate(Materials.Glass, 2)),
            first(plate(m, 2), plate(Materials.Steel, 2)));
    }

    private static void drive(int t) {
        Materials m = METAL[t];
        FluidStack lube = Materials.Lubricant.getFluid(250L * t);
        String kind = ItemVehicleComponent.driveName(t);
        if (kind.startsWith("Wheel")) {
            Materials rubber = t == 1 ? Materials.Rubber : t == 2 ? Materials.Silicone : Materials.StyreneButadieneRubber;
            part(
                Kind.DRIVE,
                t,
                lube,
                first(plate(rubber, 8), plate(Materials.Rubber, 8)),
                first(of(OrePrefixes.gearGt, m, 4), plate(m, 4)),
                first(of(OrePrefixes.stickLong, m, 2), of(OrePrefixes.stick, m, 4)),
                circuit(t, 1));
        } else if (kind.startsWith("Track")) {
            part(
                Kind.DRIVE,
                t,
                lube,
                plate(m, 12),
                first(of(OrePrefixes.gearGt, m, 6), plate(m, 6)),
                first(of(OrePrefixes.stickLong, m, 4), of(OrePrefixes.stick, m, 8)),
                itemList("Conveyor_Module_" + T[t], 2),
                circuit(t, 1));
        } else if (kind.startsWith("Hover")) {
            part(
                Kind.DRIVE,
                t,
                solder(t),
                itemList("Field_Generator_" + T[t], 2),
                itemList("Emitter_" + T[t], 4),
                plate(m, 4),
                circuit(t, 1));
        } else if (kind.startsWith("Tiltrotor")) {
            part(
                Kind.DRIVE,
                t,
                lube,
                first(of(OrePrefixes.rotor, m, 4), plate(m, 8)),
                itemList("Electric_Motor_" + T[t], 2),
                first(of(OrePrefixes.stickLong, m, 4), of(OrePrefixes.stick, m, 8)),
                circuit(t, 1));
        } else {
            part(
                Kind.DRIVE,
                t,
                lube,
                itemList("Electric_Pump_" + T[t], 2),
                itemList("Field_Generator_" + T[t], 1),
                first(of(OrePrefixes.plateDense, m, 2), plate(m, 8)),
                first(of(OrePrefixes.pipeLarge, m, 2), plate(m, 4)),
                circuit(t, 1));
        }
    }

    /** Assembler up to IV, Assembly Line (researched from the same part one tier lower) from LuV on. */
    private static void part(Kind kind, int t, FluidStack fluid, ItemStack... inputs) {
        if (hasMissing(inputs, kind)) return;
        List<ItemStack> in = new ArrayList<>();
        for (ItemStack s : inputs) {
            if (s == null) continue;
            if (s.getItem() == null) return;
            in.add(s);
        }
        ItemStack out = component(kind, t, 1);
        long eut = eut(t);
        if (t <= 5) {
            GTValues.RA.stdBuilder()
                .itemInputs(in.toArray(new ItemStack[0]))
                .fluidInputs(fluid)
                .itemOutputs(out)
                .duration(200 + 100 * t)
                .eut(eut)
                .addTo(RecipeMaps.assemblerRecipes);
        } else {
            GTValues.RA.stdBuilder()
                .metadata(RESEARCH_ITEM, component(kind, t - 1, 1))
                .metadata(SCANNING, new Scanning(1200, eut(t - 1)))
                .itemInputs(in.toArray(new ItemStack[0]))
                .fluidInputs(fluid, Materials.SolderingAlloy.getMolten(144L * t))
                .itemOutputs(out)
                .duration(400 + 100 * t)
                .eut(eut)
                .addTo(AssemblyLine);
        }
        partRecipes++;
    }

    /** A null in the input list is optional only for the armour's extra slot; anything else missing skips the recipe. */
    private static boolean hasMissing(ItemStack[] inputs, Kind kind) {
        int nulls = countNulls(inputs);
        if (nulls == 0) return false;
        if (kind == Kind.ARMOR && nulls == 1 && inputs[inputs.length - 1] == null) return false;
        LOG.warn("Skipping a vehicle part recipe ({}): an ingredient is not registered in this pack", kind);
        return true;
    }

    private static int countNulls(ItemStack[] a) {
        int n = 0;
        for (ItemStack s : a) if (s == null) n++;
        return n;
    }

    // ---------------- vehicles ----------------

    private static void vehicle(ItemVehicle v) {
        int t = v.getTier();
        ItemStack[] weapon = weapon(t);
        int big = t >= 8 ? 2 : 1;
        ItemStack[] inputs = { component(Kind.CHASSIS, t, big), component(Kind.ARMOR, t, 2 + t / 2),
            component(Kind.ENGINE, t, big), component(Kind.CONTROL, t, 1), component(Kind.DRIVE, t, big),
            weapon[0], weapon[1], circuit(t, 2), first(plate(METAL[t], 8), plate(Materials.Steel, 8)) };
        for (ItemStack s : inputs) {
            if (s == null || s.getItem() == null) {
                LOG.warn("Skipping the {} recipe: an ingredient is not registered in this pack", v.getName());
                return;
            }
        }
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .fluidInputs(Materials.SolderingAlloy.getMolten(144L * Math.min(16, t * 2)))
            .itemOutputs(new ItemStack((Item) v))
            .duration(900 + 150 * t)
            .eut(eut(t))
            .specialValue(t)
            .addTo(VehicleRecipeMap.VEHICLES);
        vehicleRecipes++;
    }

    /** The gun hardware of a tier: the same barrels, mechanisms and modules the drones use. */
    private static ItemStack[] weapon(int t) {
        switch (t) {
            case 1:
                return new ItemStack[] { new ItemStack(CommonProxy.GunBarrelSteel, 2),
                    new ItemStack(CommonProxy.FiringMechanism) };
            case 2:
                return new ItemStack[] { new ItemStack(CommonProxy.GunBarrelStainless, 2),
                    new ItemStack(Parts.BlowbackBolt) };
            case 3:
                return new ItemStack[] { new ItemStack(CommonProxy.GunBarrelTitanium, 2),
                    new ItemStack(Parts.GasPistonSystem) };
            case 4:
                return new ItemStack[] { new ItemStack(CommonProxy.GunBarrelTungstenSteel, 4),
                    new ItemStack(Parts.BeltFeedMechanism, 2) };
            case 5:
                return new ItemStack[] { new ItemStack(Parts.ElectromagneticCoil, 2),
                    new ItemStack(Parts.SuperconductingRail) };
            case 6:
                return new ItemStack[] { new ItemStack(Parts.FocusingLensArray, 2),
                    new ItemStack(Parts.SmartTargetingModule) };
            case 7:
                return new ItemStack[] { new ItemStack(Parts.PlasmaContainmentChamber, 2),
                    new ItemStack(Parts.FocusingLensArray) };
            case 8:
                return new ItemStack[] { new ItemStack(Parts.ParticleAcceleratorSegment, 2),
                    new ItemStack(Parts.SmartTargetingModule, 2) };
            case 9:
                return new ItemStack[] { new ItemStack(Parts.AntimatterTrap, 2),
                    new ItemStack(Parts.ExoticMatterStabilizer) };
            case 10:
                return new ItemStack[] { new ItemStack(Parts.QuantumEntanglementCore, 2),
                    new ItemStack(Parts.InfinityChassis) };
            case 11:
                return new ItemStack[] { new ItemStack(Parts.GravitonFieldEmitter, 2),
                    new ItemStack(Parts.InfinityChassis) };
            case 12:
                return new ItemStack[] { new ItemStack(Parts.ChronoStabilizer, 2),
                    new ItemStack(Parts.SpacetimeChassis) };
            case 13:
                return new ItemStack[] { new ItemStack(Parts.DimensionalRiftEmitter, 2),
                    new ItemStack(Parts.SpacetimeChassis) };
            default:
                return new ItemStack[] { new ItemStack(Parts.RealityEngine, 2), new ItemStack(Parts.EternalChassis) };
        }
    }

    /** The exotic core of an engine tier (the same item refuels the vehicle from UV on). */
    private static ItemStack core(int t) {
        Item[] cores = { null, null, null, null, null, null, null, Parts.PlasmaContainmentChamber,
            Parts.ParticleAcceleratorSegment, Parts.AntimatterTrap, Parts.QuantumEntanglementCore,
            Parts.GravitonFieldEmitter, Parts.ChronoStabilizer, Parts.DimensionalRiftEmitter, Parts.RealityEngine };
        return cores[t] == null ? null : new ItemStack(cores[t]);
    }

    // ---------------- helpers ----------------

    public static ItemStack component(Kind kind, int tier, int n) {
        if (Vehicles.Component == null || tier < 1) return null;
        return new ItemStack(Vehicles.Component, n, ItemVehicleComponent.meta(kind, tier));
    }

    private static long eut(int t) {
        return 30L << (2 * (t - 1));
    }

    private static FluidStack solder(int t) {
        return Materials.SolderingAlloy.getMolten(72L * t);
    }

    private static ItemStack itemList(String name, int n) {
        try {
            ItemList entry = ItemList.valueOf(name);
            return entry.hasBeenSet() ? entry.get(n) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static ItemStack of(OrePrefixes prefix, Materials m, int n) {
        return HighTierRecipes.of(prefix, m, n);
    }

    private static ItemStack plate(Materials m, int n) {
        return HighTierRecipes.plate(m, n);
    }

    private static ItemStack circuit(int tier, int n) {
        if (tier >= 10) return HighTierRecipes.circuitTier(tier, n);
        Materials[] tiers = { null, Materials.LV, Materials.MV, Materials.HV, Materials.EV, Materials.IV, Materials.LuV,
            Materials.ZPM, Materials.UV, Materials.UHV };
        return of(OrePrefixes.circuit, tiers[tier], n);
    }

    private static ItemStack first(ItemStack... options) {
        return HighTierRecipes.first(options);
    }
}
