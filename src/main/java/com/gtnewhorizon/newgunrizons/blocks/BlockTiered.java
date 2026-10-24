package com.gtnewhorizon.newgunrizons.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Structure block with one variant per voltage tier (metadata 0 = LV ... 13 = MAX), each made of that tier's signature
 * material. Used to build the Munitions Factory and the Drone Assembly Complex.
 */
public class BlockTiered extends Block {

    public static final int TIERS = 14;
    /** Signature material of each tier, for names and tooltips. */
    public static final String[] MATERIALS = { "Steel", "Aluminium", "Stainless Steel", "Titanium", "Tungstensteel",
        "HSS-S", "Naquadah Alloy", "Neutronium", "Cosmic Neutronium", "Infinity", "Transcendent Metal", "Spacetime",
        "Universium", "Magmatter" };
    public static final String[] TIER_NAMES = { "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV",
        "UMV", "UXV", "MAX" };

    private final String texture;
    private final boolean distinctTop;
    @SideOnly(Side.CLIENT)
    private IIcon[] side, top;

    public BlockTiered(String name, String texture, boolean distinctTop) {
        super(Material.iron);
        this.texture = texture;
        this.distinctTop = distinctTop;
        this.setBlockName("newgunrizons." + name);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setStepSound(soundTypeMetal);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(NewGunrizonsMod.HighTierTab);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TIERS; i++) list.add(new ItemStack(item, 1, i));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        side = new IIcon[TIERS];
        top = new IIcon[TIERS];
        for (int i = 0; i < TIERS; i++) {
            String tier = TIER_NAMES[i].toLowerCase();
            side[i] = register.registerIcon(NewGunrizonsMod.MODID + ":" + texture + "_" + tier);
            top[i] = distinctTop ? register.registerIcon(NewGunrizonsMod.MODID + ":" + texture + "_top_" + tier)
                : side[i];
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int face, int meta) {
        int i = Math.max(0, Math.min(TIERS - 1, meta));
        return face == 1 ? top[i] : side[i];
    }
}
