package com.gtnewhorizon.newgunrizons.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;

/**
 * The parts every vehicle is assembled from, one of each kind per tier (LV = 1 ... MAX = 14): chassis, armour plating,
 * engine, control system and a drive (wheels, tracks, hover emitters, rotors or thrusters, depending on the vehicle
 * of that tier). Damage value = kind * 16 + tier.
 */
public class ItemVehicleComponent extends Item {

    public enum Kind {
        CHASSIS,
        ARMOR,
        ENGINE,
        CONTROL,
        DRIVE
    }

    public static final int TIERS = 14;
    private static final String[] TIER_NAMES = { "", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV",
        "UIV", "UMV", "UXV", "MAX" };
    private static final String[] ENGINES = { "", "Diesel Engine", "Diesel Engine", "Turbodiesel Engine",
        "Multifuel Engine", "Electric Drive", "Lift Fan Drive", "Plasma Thruster Core", "Particle Drive",
        "Antimatter Reactor", "Quantum Drive", "Graviton Drive", "Spacetime Drive", "Dimensional Drive",
        "Reality Drive" };
    private static final String[] CONTROLS = { "", "Driver Console", "Driver Console", "Driver Console",
        "Fire Control Computer", "Fire Control Computer", "Avionics Suite", "Avionics Suite", "Starship Bridge Core",
        "Starship Bridge Core", "Starship Bridge Core", "Starship Bridge Core", "Starship Bridge Core",
        "Starship Bridge Core", "Starship Bridge Core" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemVehicleComponent() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("newgunrizons_VehicleComponent");
        setCreativeTab(NewGunrizonsMod.HighTierTab);
    }

    public static int meta(Kind kind, int tier) {
        return kind.ordinal() * 16 + tier;
    }

    public static Kind kindOf(int meta) {
        int k = meta / 16;
        return k >= 0 && k < Kind.values().length ? Kind.values()[k] : Kind.CHASSIS;
    }

    public static int tierOf(int meta) {
        return Math.max(1, Math.min(TIERS, meta % 16));
    }

    /** What moves a vehicle of this tier. */
    public static String driveName(int tier) {
        switch (tier) {
            case 1:
            case 2:
            case 3:
                return "Wheel Set";
            case 4:
                return "Track Set";
            case 5:
            case 11:
                return "Hover Emitter Array";
            case 6:
                return "Tiltrotor Set";
            default:
                return "Thruster Pack";
        }
    }

    public static String nameOf(Kind kind, int tier) {
        String t = TIER_NAMES[tier] + " ";
        switch (kind) {
            case CHASSIS:
                return t + (tier >= 7 ? "Spaceframe" : "Vehicle Chassis");
            case ARMOR:
                return t + (tier >= 7 ? "Hull Plating" : "Armour Plating");
            case ENGINE:
                return t + ENGINES[tier];
            case CONTROL:
                return t + CONTROLS[tier];
            default:
                return t + driveName(tier);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int m = stack.getItemDamage();
        return nameOf(kindOf(m), tierOf(m));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.newgunrizons_VehicleComponent." + stack.getItemDamage();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + "Vehicle part, " + GTValues.VN[tierOf(stack.getItemDamage())]);
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Assembled into vehicles in the Vehicle Assembly Hangar");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (Kind k : Kind.values()) {
            for (int t = 1; t <= TIERS; t++) list.add(new ItemStack(item, 1, meta(k, t)));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[Kind.values().length * 16];
        for (Kind k : Kind.values()) {
            for (int t = 1; t <= TIERS; t++) {
                icons[meta(k, t)] = register.registerIcon(
                    NewGunrizonsMod.MODID + ":vehicle_part_"
                        + k.name()
                            .toLowerCase()
                        + "_"
                        + t);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (icons == null || meta < 0 || meta >= icons.length || icons[meta] == null) return itemIcon;
        return icons[meta];
    }
}
