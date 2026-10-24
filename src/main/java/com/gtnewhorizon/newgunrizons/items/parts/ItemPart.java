package com.gtnewhorizon.newgunrizons.items.parts;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

/** A crafting intermediate made in the Munitions Factory. */
public class ItemPart extends Item {

    private final String usage;

    /**
     * @param name    registry/unlocalized name, e.g. "GasPistonSystem"
     * @param texture icon file name under textures/items, e.g. "gas_piston_system"
     * @param usage   one-line tooltip saying which weapons use it
     */
    public ItemPart(String name, String texture, String usage) {
        this.usage = usage;
        this.setMaxStackSize(64);
        this.setUnlocalizedName("newgunrizons_" + name);
        this.setTextureName(NewGunrizonsMod.MODID + ":" + texture);
        this.setCreativeTab(NewGunrizonsMod.gunsTab);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + usage);
    }
}
