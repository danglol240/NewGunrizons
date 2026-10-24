package com.gtnewhorizon.newgunrizons;

import net.minecraft.creativetab.CreativeTabs;

import com.gtnewhorizon.newgunrizons.registry.DroneRecipes;
import com.gtnewhorizon.newgunrizons.registry.VehicleRecipes;
import com.gtnewhorizon.newgunrizons.registry.GTRecipes;
import com.gtnewhorizon.newgunrizons.registry.HighTierRecipes;
import com.gtnewhorizon.newgunrizons.registry.Machines;
import com.gtnewhorizon.newgunrizons.registry.StructureBlockRecipes;
import com.gtnewhorizon.newgunrizons.tabs.AmmoTab;
import com.gtnewhorizon.newgunrizons.tabs.AssaultRiflesTab;
import com.gtnewhorizon.newgunrizons.tabs.AttachmentsTab;
import com.gtnewhorizon.newgunrizons.tabs.GunsTab;
import com.gtnewhorizon.newgunrizons.tabs.HighTierTab;
import com.gtnewhorizon.newgunrizons.tabs.PistolsTab;
import com.gtnewhorizon.newgunrizons.tabs.SMGTab;
import com.gtnewhorizon.newgunrizons.tabs.ShotgunsTab;
import com.gtnewhorizon.newgunrizons.tabs.SnipersTab;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = NewGunrizonsMod.MODID, version = NewGunrizonsMod.VERSION, dependencies = "required-after:gregtech")
public class NewGunrizonsMod {

    public static final String MODID = "newgunrizons";
    public static final String VERSION = "1.0.0";

    public static final SimpleNetworkWrapper CHANNEL;

    public static CreativeTabs gunsTab;
    public static CreativeTabs AssaultRiflesTab;
    public static CreativeTabs PistolsTab;
    public static CreativeTabs SMGTab;
    public static CreativeTabs ShotgunsTab;
    public static CreativeTabs SnipersTab;
    public static CreativeTabs AmmoTab;
    public static CreativeTabs AttachmentsTab;
    public static CreativeTabs HighTierTab;

    @SidedProxy(
        serverSide = "com.gtnewhorizon.newgunrizons.CommonProxy",
        clientSide = "com.gtnewhorizon.newgunrizons.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.init(this, event);
        Machines.loadConfig(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        HighTierRecipes.init();
        DroneRecipes.init();
        VehicleRecipes.init();
        StructureBlockRecipes.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Machines.register();
        Machines.registerHangar();
        GTRecipes.init();
    }

    static {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        gunsTab = new GunsTab(CreativeTabs.getNextID(), "guns_tab");
        AssaultRiflesTab = new AssaultRiflesTab(CreativeTabs.getNextID(), "AssaultRifles_tab");
        PistolsTab = new PistolsTab(CreativeTabs.getNextID(), "Pistols_tab");
        SMGTab = new SMGTab(CreativeTabs.getNextID(), "SMG_tab");
        ShotgunsTab = new ShotgunsTab(CreativeTabs.getNextID(), "ShotgunsTab");
        SnipersTab = new SnipersTab(CreativeTabs.getNextID(), "SnipersTab");
        AmmoTab = new AmmoTab(CreativeTabs.getNextID(), "AmmoTab");
        AttachmentsTab = new AttachmentsTab(CreativeTabs.getNextID(), "AttachmentsTab");
        HighTierTab = new HighTierTab(CreativeTabs.getNextID(), "HighTier_tab");
    }
}
