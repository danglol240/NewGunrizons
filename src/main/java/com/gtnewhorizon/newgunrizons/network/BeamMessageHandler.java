package com.gtnewhorizon.newgunrizons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.client.render.BeamEffects;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BeamMessageHandler implements IMessageHandler<BeamMessage, IMessage> {

    @Override
    public IMessage onMessage(BeamMessage message, MessageContext ctx) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return null;
        Item item = Item.getItemById(message.getWeaponId());
        if (!(item instanceof ItemWeapon)) return null;
        Entity shooter = mc.theWorld.getEntityByID(message.getShooterId());
        BeamEffects.add(
            (ItemWeapon) item,
            shooter instanceof EntityLivingBase ? (EntityLivingBase) shooter : null,
            message.getStartX(),
            message.getStartY(),
            message.getStartZ(),
            message.getEndX(),
            message.getEndY(),
            message.getEndZ());
        return null;
    }
}
