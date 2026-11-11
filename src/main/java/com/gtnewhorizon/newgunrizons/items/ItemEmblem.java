package com.gtnewhorizon.newgunrizons.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Flat weapon emblems (one per metadata) for icons in places that cannot draw the 3D weapon models, such as the quest
 * book. Not obtainable and not in a creative tab. Textures come from tools/gen_emblems.py.
 */
public class ItemEmblem extends Item {

    /** Keep in sync with EMBLEMS in tools/gen_emblems.py. */
    public static final String[] NAMES = { "M1911", "MP40", "M4A1", "XR7", "Railgun", "LaserRifle", "PlasmaCannon",
        "ParticleBeamRifle", "AntimatterCannon", "QuantumPhaseRifle", "GravitonCannon", "ChronoRifle",
        "DimensionalCutter", "RealityEraser" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemEmblem() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setUnlocalizedName("newgunrizons_Emblem");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[NAMES.length];
        for (int i = 0; i < NAMES.length; i++) {
            icons[i] = register.registerIcon(NewGunrizonsMod.MODID + ":emblem_" + NAMES[i].toLowerCase());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[Math.max(0, Math.min(icons.length - 1, meta))];
    }
}
