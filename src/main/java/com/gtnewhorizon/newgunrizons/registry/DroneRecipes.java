package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.CommonProxy;
import com.gtnewhorizon.newgunrizons.items.ItemDrone;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

/**
 * Drone Assembly Complex recipes. A drone is built like a real one: a frame of the tier's metal, one motor and one
 * rotor
 * per arm, a flight controller (circuits), a sensor, a battery and its weapon. The weapon is the same hardware as the
 * guns of that tier (barrels and firing mechanisms up to EV, the technology modules from IV on).
 */
public final class DroneRecipes {

    private DroneRecipes() {}

    public static void init() {
        add(
            Drones.ScoutDrone,
            Materials.Steel,
            ItemList.Electric_Motor_LV,
            ItemList.Sensor_LV,
            ItemList.Battery_RE_LV_Lithium,
            new ItemStack(CommonProxy.GunBarrelSteel),
            new ItemStack(CommonProxy.FiringMechanism));
        add(
            Drones.AssaultDrone,
            Materials.Aluminium,
            ItemList.Electric_Motor_MV,
            ItemList.Sensor_MV,
            ItemList.Battery_RE_MV_Lithium,
            new ItemStack(CommonProxy.GunBarrelStainless),
            new ItemStack(Parts.BlowbackBolt));
        add(
            Drones.GunshipDrone,
            Materials.StainlessSteel,
            ItemList.Electric_Motor_HV,
            ItemList.Sensor_HV,
            ItemList.Battery_RE_HV_Lithium,
            new ItemStack(CommonProxy.GunBarrelTitanium),
            new ItemStack(Parts.GasPistonSystem));
        add(
            Drones.HeavyDrone,
            Materials.Titanium,
            ItemList.Electric_Motor_EV,
            ItemList.Sensor_EV,
            ItemList.Energy_LapotronicOrb,
            new ItemStack(CommonProxy.GunBarrelTungstenSteel, 2),
            new ItemStack(Parts.BeltFeedMechanism, 2));
        add(
            Drones.RailDrone,
            Materials.TungstenSteel,
            ItemList.Electric_Motor_IV,
            ItemList.Sensor_IV,
            ItemList.Energy_LapotronicOrb,
            new ItemStack(Parts.ElectromagneticCoil, 2),
            new ItemStack(Parts.SuperconductingRail));
        add(
            Drones.LaserDrone,
            Materials.HSSS,
            ItemList.Electric_Motor_LuV,
            ItemList.Sensor_LuV,
            ItemList.Energy_LapotronicOrb2,
            new ItemStack(Parts.FocusingLensArray),
            new ItemStack(Parts.SmartTargetingModule));
        add(
            Drones.PlasmaDrone,
            Materials.NaquadahAlloy,
            ItemList.Electric_Motor_ZPM,
            ItemList.Sensor_ZPM,
            ItemList.Energy_Module,
            new ItemStack(Parts.PlasmaContainmentChamber),
            new ItemStack(Parts.FocusingLensArray));
        add(
            Drones.ParticleDrone,
            Materials.Neutronium,
            ItemList.Electric_Motor_UV,
            ItemList.Sensor_UV,
            ItemList.Energy_Cluster,
            new ItemStack(Parts.ParticleAcceleratorSegment, 2),
            new ItemStack(Parts.SmartTargetingModule));
        add(
            Drones.AntimatterDrone,
            Materials.CosmicNeutronium,
            ItemList.Electric_Motor_UHV,
            ItemList.Sensor_UHV,
            ItemList.ZPM2,
            new ItemStack(Parts.AntimatterTrap),
            new ItemStack(Parts.ExoticMatterStabilizer));
        add(
            Drones.QuantumDrone,
            Materials.Infinity,
            ItemList.Electric_Motor_UEV,
            ItemList.Sensor_UEV,
            ItemList.ZPM3,
            new ItemStack(Parts.QuantumEntanglementCore),
            new ItemStack(Parts.InfinityChassis));
        add(
            Drones.GravitonDrone,
            Materials.TranscendentMetal,
            ItemList.Electric_Motor_UIV,
            ItemList.Sensor_UIV,
            ItemList.ZPM4,
            new ItemStack(Parts.GravitonFieldEmitter),
            new ItemStack(Parts.InfinityChassis));
        add(
            Drones.ChronoDrone,
            Materials.SpaceTime,
            ItemList.Electric_Motor_UMV,
            ItemList.Sensor_UMV,
            ItemList.ZPM5,
            new ItemStack(Parts.ChronoStabilizer),
            new ItemStack(Parts.SpacetimeChassis));
        add(
            Drones.RiftDrone,
            Materials.Universium,
            ItemList.Electric_Motor_UXV,
            ItemList.Sensor_UXV,
            ItemList.ZPM6,
            new ItemStack(Parts.DimensionalRiftEmitter),
            new ItemStack(Parts.SpacetimeChassis));
        add(
            Drones.SentinelDrone,
            Materials.MagMatter,
            ItemList.Electric_Motor_MAX,
            ItemList.Sensor_MAX,
            ItemList.ZPM6,
            new ItemStack(Parts.RealityEngine),
            new ItemStack(Parts.EternalChassis));
    }

    private static void add(ItemDrone drone, Materials metal, ItemList motor, ItemList sensor, ItemList battery,
        ItemStack weapon, ItemStack weaponPart) {
        if (drone == null) return;
        int tier = drone.getTier();
        int arms = drone.getLayout().rotors;
        ItemStack plates = first(
            HighTierRecipes.plate(metal, 4),
            HighTierRecipes.plate(Materials.Neutronium, 4),
            HighTierRecipes.plate(Materials.Steel, 4));
        ItemStack rotors = first(
            HighTierRecipes.of(OrePrefixes.rotor, metal, arms),
            HighTierRecipes.of(OrePrefixes.plate, metal, arms));
        ItemStack[] inputs = { plates, rotors, HighTierRecipes.item(motor, arms), HighTierRecipes.item(sensor, 1),
            first(HighTierRecipes.item(battery, 1), circuit(tier, 1)), circuit(tier, 2), weapon, weaponPart,
            first(
                HighTierRecipes.of(OrePrefixes.wireGt04, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 2)) };
        for (ItemStack s : inputs) {
            if (s == null || s.getItem() == null) return;
        }
        long eut = 30L << (2 * (tier - 1)); // 30, 120, 480 ... EU/t: the tier's recipe voltage
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .fluidInputs(Materials.SolderingAlloy.getMolten(144L * Math.min(16, tier * 2)))
            .itemOutputs(new ItemStack((Item) drone))
            .duration(600 + tier * 100)
            .eut(eut)
            .addTo(DroneRecipeMap.DRONES);
    }

    private static ItemStack circuit(int tier, int n) {
        if (tier >= 10) return HighTierRecipes.circuitTier(tier, n);
        Materials[] tiers = { null, Materials.LV, Materials.MV, Materials.HV, Materials.EV, Materials.IV, Materials.LuV,
            Materials.ZPM, Materials.UV, Materials.UHV };
        return HighTierRecipes.of(OrePrefixes.circuit, tiers[tier], n);
    }

    private static ItemStack first(ItemStack... options) {
        return HighTierRecipes.first(options);
    }
}
