package com.gtnewhorizon.newgunrizons.registry;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.machines.MTEMunitionsFactory;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;

/** Registers the Munitions Factory controller and its crafting recipe. */
public final class Machines {

    private static final Logger LOG = LogManager.getLogger("NewGunrizons");
    /** Outside GregTech's listed IDs and below Twist Space Technology's 19000+ range. */
    private static final int DEFAULT_FACTORY_ID = 17920;
    private static final int SEARCH_WINDOW = 64;

    private static int configuredFactoryId = DEFAULT_FACTORY_ID;
    public static ItemStack munitionsFactory;

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
        if (config.hasChanged()) config.save();
    }

    public static void register() {
        int id = configuredFactoryId;
        int limit = Math.min(GregTechAPI.METATILEENTITIES.length, configuredFactoryId + SEARCH_WINDOW);
        while (id < limit && GregTechAPI.METATILEENTITIES[id] != null) id++;
        if (id >= limit) {
            LOG.error(
                "No free MetaTileEntity ID in {}..{}; the Munitions Factory is not registered. Set machines.munitionsFactoryId in newgunrizons.cfg.",
                configuredFactoryId,
                limit - 1);
            return;
        }
        if (id != configuredFactoryId) {
            LOG.warn(
                "MetaTileEntity ID {} is taken; the Munitions Factory uses {} instead. Set machines.munitionsFactoryId={} in newgunrizons.cfg to keep it stable.",
                configuredFactoryId,
                id,
                id);
        }
        munitionsFactory = new MTEMunitionsFactory(
            id,
            "multimachine.newgunrizons.munitionsfactory",
            "Munitions Factory").getStackForm(1);

        GTModHandler.addCraftingRecipe(
            munitionsFactory.copy(),
            GTModHandler.RecipeBits.BITSD,
            new Object[] { "CRC", "AHA", "CPC", 'C', OrePrefixes.circuit.get(Materials.LV), 'R', ItemList.Robot_Arm_LV,
                'A', ItemList.Conveyor_Module_LV, 'H', ItemList.Hull_LV, 'P', ItemList.Electric_Piston_LV });
    }
}
