package com.gtnewhorizon.newgunrizons.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;

import gregtech.api.enums.GTValues;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

/**
 * Magazine of an energy weapon that can also be recharged with EU. Its stored charge is its shot count times the
 * energy of one shot, plus a partial shot being charged, so charging it in a GregTech Battery Buffer or Charger of
 * its tier refills its shots; it can still be refilled with charges like any magazine. It never gives energy back.
 */
public class ItemEnergyCell extends ItemMagazine implements ISpecialElectricItem, IElectricItemManager {

    private static final String TAG_PARTIAL = "ngPartialEU";
    /** Ticks of 1 A at the cell's voltage that one shot costs. */
    private static final long TICKS_PER_SHOT = 8L;

    private final int tier;
    private final double euPerShot;

    ItemEnergyCell(int ammo, int tier) {
        super(ammo);
        this.tier = Math.max(1, Math.min(GTValues.V.length - 1, tier));
        this.euPerShot = (double) GTValues.V[this.tier] * TICKS_PER_SHOT;
    }

    public int getEnergyTier() {
        return tier;
    }

    public double getEuPerShot() {
        return euPerShot;
    }

    /** Tooltip line for the attachment information provider. */
    public String describe(ItemStack stack) {
        return "Charge: " + ItemInstance.getAmmo(stack) + "/" + getAmmo() + " shots";
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add("Recharge in a " + GTValues.VN[tier] + " Battery Buffer or Charger");
        tooltip.add(String.format("%,.0f EU per shot", euPerShot));
    }

    private static double partial(ItemStack stack) {
        return stack.stackTagCompound == null ? 0.0D : stack.stackTagCompound.getDouble(TAG_PARTIAL);
    }

    // ---------------- IC2 electric item ----------------

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        return this;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return getAmmo() * euPerShot;
    }

    @Override
    public int getTier(ItemStack stack) {
        return tier;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return GTValues.V[tier];
    }

    @Override
    public double charge(ItemStack stack, double amount, int chargerTier, boolean ignoreTransferLimit,
        boolean simulate) {
        if (stack == null || amount <= 0.0D || chargerTier < tier) return 0.0D;
        int shots = ItemInstance.getAmmo(stack);
        double room = (getAmmo() - shots) * euPerShot - partial(stack);
        if (room <= 0.0D) return 0.0D;
        double accepted = Math.min(amount, room);
        if (!ignoreTransferLimit) accepted = Math.min(accepted, getTransferLimit(stack));
        if (!simulate) {
            double total = partial(stack) + accepted;
            int gained = (int) Math.floor(total / euPerShot);
            ItemInstance.setAmmo(stack, Math.min(getAmmo(), shots + gained));
            if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setDouble(TAG_PARTIAL, total - gained * euPerShot);
        }
        return accepted;
    }

    @Override
    public double discharge(ItemStack stack, double amount, int dischargerTier, boolean ignoreTransferLimit,
        boolean externally, boolean simulate) {
        return 0.0D;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return ItemInstance.getAmmo(stack) * euPerShot + partial(stack);
    }

    @Override
    public boolean canUse(ItemStack stack, double amount) {
        return false;
    }

    @Override
    public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
        return false;
    }

    @Override
    public void chargeFromArmor(ItemStack stack, EntityLivingBase entity) {}

    @Override
    public String getToolTip(ItemStack stack) {
        return null;
    }
}
