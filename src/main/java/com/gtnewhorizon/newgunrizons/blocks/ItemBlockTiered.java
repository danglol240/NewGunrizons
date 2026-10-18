package com.gtnewhorizon.newgunrizons.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

/** Item form of a {@link BlockTiered}: one name per tier. */
public class ItemBlockTiered extends ItemBlockWithMetadata {

    public ItemBlockTiered(Block block) {
        super(block, block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int i = Math.max(0, Math.min(BlockTiered.TIERS - 1, stack.getItemDamage()));
        tooltip.add(EnumChatFormatting.GOLD + BlockTiered.TIER_NAMES[i] + EnumChatFormatting.GRAY + " - "
            + BlockTiered.MATERIALS[i]);
    }
}
