package com.gtnewhorizon.newgunrizons.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.registry.Guns;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HighTierTab extends CreativeTabs {

    public HighTierTab(int id, String label) {
        super(id, label);
    }

    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Guns.Railgun != null ? Guns.Railgun : Items.nether_star;
    }
}
