package com.gtnewhorizon.newgunrizons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizon.newgunrizons.client.handlers.ClientTickHandler;
import com.gtnewhorizon.newgunrizons.client.handlers.WeaponHudHandler;
import com.gtnewhorizon.newgunrizons.client.handlers.WeaponInputHandler;
import com.gtnewhorizon.newgunrizons.client.handlers.WeaponRenderHandler;
import com.gtnewhorizon.newgunrizons.client.input.KeyBindings;
import com.gtnewhorizon.newgunrizons.client.particle.ParticleManager;
import com.gtnewhorizon.newgunrizons.client.render.BeamEffects;
import com.gtnewhorizon.newgunrizons.client.render.EntityBulletRenderer;
import com.gtnewhorizon.newgunrizons.client.render.EntityGrenadeRenderer;
import com.gtnewhorizon.newgunrizons.client.render.EntityShellRenderer;
import com.gtnewhorizon.newgunrizons.client.render.GravityFieldRenderer;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.entities.EntityGravityField;
import com.gtnewhorizon.newgunrizons.entities.EntityGrenade;
import com.gtnewhorizon.newgunrizons.entities.EntityShellCasing;
import com.gtnewhorizon.newgunrizons.network.BeamMessage;
import com.gtnewhorizon.newgunrizons.network.BeamMessageHandler;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessageHandler;
import com.gtnewhorizon.newgunrizons.network.ExplosionMessage;
import com.gtnewhorizon.newgunrizons.network.ExplosionMessageHandler;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessageHandler;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(Object mod, FMLPreInitializationEvent event) {
        super.init(mod, event);

        MinecraftForge.EVENT_BUS.register(new WeaponHudHandler());

        KeyBindings.init();

        WeaponInputHandler inputHandler = new WeaponInputHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(inputHandler);
        MinecraftForge.EVENT_BUS.register(inputHandler);

        ClientTickHandler tickHandler = new ClientTickHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(tickHandler);

        WeaponRenderHandler renderHandler = new WeaponRenderHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(renderHandler);
        MinecraftForge.EVENT_BUS.register(renderHandler);

        RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new EntityBulletRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityShellCasing.class, new EntityShellRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new EntityGrenadeRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityGravityField.class, new GravityFieldRenderer());
        MinecraftForge.EVENT_BUS.register(new BeamEffects());
    }

    @Override
    protected void registerClientMessageHandlers(SimpleNetworkWrapper channel) {
        channel.registerMessage(new SpawnParticleMessageHandler(), SpawnParticleMessage.class, 18, Side.CLIENT);
        channel.registerMessage(new BlockHitMessageHandler(), BlockHitMessage.class, 19, Side.CLIENT);
        channel.registerMessage(new ExplosionMessageHandler(), ExplosionMessage.class, 21, Side.CLIENT);
        channel.registerMessage(new BeamMessageHandler(), BeamMessage.class, 22, Side.CLIENT);
    }

    @Override
    public void onWeaponFireEffects(EntityLivingBase player, float smokeOffsetX, float smokeOffsetY,
        boolean silencerOn) {
        ParticleManager.spawnSmokeParticle(player, smokeOffsetX, smokeOffsetY);
    }

    @Override
    public boolean isLocalPlayer(EntityLivingBase player) {
        return Minecraft.getMinecraft().thePlayer == player;
    }

    @Override
    public void registerItem(String name, Item item, IItemRenderer renderer) {
        super.registerItem(name, item, renderer);
        if (renderer != null) {
            MinecraftForgeClient.registerItemRenderer(item, renderer);
        }
        if (item.getCreativeTab() == null) {
            codechicken.nei.api.API.hideItem(new ItemStack(item));
        }
    }
}
