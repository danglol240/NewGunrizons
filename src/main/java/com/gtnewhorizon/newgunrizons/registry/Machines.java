package com.gtnewhorizon.newgunrizons.registry;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.machines.MTEDroneAssemblyBay;
import com.gtnewhorizon.newgunrizons.machines.MTEMunitionsFactory;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;

/** Registers the Munitions Factory and Drone Assembly Complex controllers and their crafting recipes. */
public final class Machines {

    private static final Logger LOG = LogManager.getLogger("NewGunrizons");
    /** Outside GregTech's listed IDs and below Twist Space Technology's 19000+ range. */
    private static final int DEFAULT_FACTORY_ID = 17920;
    private static final int DEFAULT_DRONE_BAY_ID = 17921;
    private static final int SEARCH_WINDOW = 64;

    private static int configuredFactoryId = DEFAULT_FACTORY_ID;
    private static int configuredDroneBayId = DEFAULT_DRONE_BAY_ID;
    public static ItemStack munitionsFactory;
    public static ItemStack droneAssemblyBay;

    private Machines() {}

    public static void loadConfig(File file) {
        Configuration config = new Configuration(file);
        configuredFactoryId = config.getInt(
            "munitionsFactoryId",
            "machines",
            DEFAULT_FACTORY_ID,
            1,
            32765,
            "GregTech MetaTileEntity ID of the Munitions Factory controller. Change it only if another mod uses it;"
                + " changing it in an existing world turns placed factories into a different machine.");
        configuredDroneBayId = config.getInt(
            "droneAssemblyBayId",
            "machines",
            DEFAULT_DRONE_BAY_ID,
            1,
            32765,
            "GregTech MetaTileEntity ID of the Drone Assembly Complex controller. Same rules as munitionsFactoryId.");
        if (config.hasChanged()) config.save();
    }

    public static void register() {
        int factoryId = freeId(configuredFactoryId, "Munitions Factory", "munitionsFactoryId");
        if (factoryId > 0) {
            munitionsFactory = new MTEMunitionsFactory(
                factoryId,
                "multimachine.newgunrizons.munitionsfactory",
                "Munitions Factory").getStackForm(1);
            GTModHandler.addCraftingRecipe(
                munitionsFactory.copy(),
                GTModHandler.RecipeBits.BITSD,
                new Object[] { "CRC", "AHA", "CPC", 'C', OrePrefixes.circuit.get(Materials.LV), 'R',
                    ItemList.Robot_Arm_LV, 'A', ItemList.Conveyor_Module_LV, 'H', ItemList.Hull_LV, 'P',
                    ItemList.Electric_Piston_LV });
        }

        int bayId = freeId(configuredDroneBayId, "Drone Assembly Complex", "droneAssemblyBayId");
        if (bayId > 0) {
            droneAssemblyBay = new MTEDroneAssemblyBay(
                bayId,
                "multimachine.newgunrizons.droneassemblybay",
                "Drone Assembly Complex").getStackForm(1);
            GTModHandler.addCraftingRecipe(
                droneAssemblyBay.copy(),
                GTModHandler.RecipeBits.BITSD,
                new Object[] { "CSC", "MHM", "CRC", 'C', OrePrefixes.circuit.get(Materials.LV), 'S', ItemList.Sensor_LV,
                    'M', ItemList.Electric_Motor_LV, 'H', ItemList.Hull_LV, 'R', ItemList.Robot_Arm_LV });
        }
    }

    /** The configured ID, or the next free one after it; -1 if none is free nearby. */
    private static int freeId(int configured, String machine, String key) {
        int id = configured;
        int limit = Math.min(GregTechAPI.METATILEENTITIES.length, configured + SEARCH_WINDOW);
        while (id < limit && GregTechAPI.METATILEENTITIES[id] != null) id++;
        if (id >= limit) {
            LOG.error(
                "No free MetaTileEntity ID in {}..{}; the {} is not registered. Set machines.{} in newgunrizons.cfg.",
                configured,
                limit - 1,
                machine,
                key);
            return -1;
        }
        if (id != configured) {
            LOG.warn(
                "MetaTileEntity ID {} is taken; the {} uses {} instead. Set machines.{}={} in newgunrizons.cfg to keep it stable.",
                configured,
                machine,
                id,
                key,
                id);
        }
        return id;
    }
}
