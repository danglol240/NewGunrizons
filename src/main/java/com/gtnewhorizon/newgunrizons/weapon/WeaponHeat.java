package com.gtnewhorizon.newgunrizons.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;

/**
 * Heat of weapons that overheat (and the cooldown of the Reality Eraser, which is the same thing with one shot to
 * lock it). Stored on the weapon stack as the heat at a world time, so it cools down while not being looked at: the
 * current heat is the stored value minus the cooling since then. Reaching the maximum locks the weapon until it has
 * cooled down completely.
 * <p>
 * Both sides run the same arithmetic: the client to stop firing and show the gauge, the server to refuse shots.
 */
public final class WeaponHeat {

    private static final String TAG_HEAT = "ngHeat";
    private static final String TAG_TIME = "ngHeatTime";
    private static final String TAG_LOCKED = "ngHeatLock";

    private WeaponHeat() {}

    /** Current heat, 0 when the weapon does not overheat. */
    public static float get(ItemStack stack, long worldTime) {
        WeaponBehavior b = behavior(stack);
        if (b == null || !b.hasHeat() || stack.stackTagCompound == null) return 0.0F;
        NBTTagCompound tag = stack.stackTagCompound;
        long elapsed = Math.max(0L, worldTime - tag.getLong(TAG_TIME));
        return Math.max(0.0F, tag.getFloat(TAG_HEAT) - elapsed * b.getCoolingPerTick());
    }

    /** Heat as a fraction of the maximum. */
    public static float fraction(ItemStack stack, long worldTime) {
        WeaponBehavior b = behavior(stack);
        if (b == null || !b.hasHeat()) return 0.0F;
        return Math.min(1.0F, get(stack, worldTime) / b.getMaxHeat());
    }

    /** True while an overheated weapon is still cooling down. */
    public static boolean isLocked(ItemStack stack, long worldTime) {
        WeaponBehavior b = behavior(stack);
        if (b == null || !b.hasHeat() || stack.stackTagCompound == null) return false;
        return stack.stackTagCompound.getBoolean(TAG_LOCKED) && get(stack, worldTime) > 0.0F;
    }

    /** Seconds until a locked weapon can fire again. */
    public static float secondsLeft(ItemStack stack, long worldTime) {
        WeaponBehavior b = behavior(stack);
        if (b == null || !b.hasHeat() || b.getCoolingPerTick() <= 0.0F) return 0.0F;
        return get(stack, worldTime) / b.getCoolingPerTick() / 20.0F;
    }

    /** Adds one shot's heat; locks the weapon when it reaches the maximum. */
    public static void addShot(ItemStack stack, long worldTime) {
        WeaponBehavior b = behavior(stack);
        if (b == null || !b.hasHeat()) return;
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        float heat = get(stack, worldTime) + b.getHeatPerShot();
        boolean locked = heat >= b.getMaxHeat();
        NBTTagCompound tag = stack.stackTagCompound;
        tag.setFloat(TAG_HEAT, Math.min(heat, b.getMaxHeat()));
        tag.setLong(TAG_TIME, worldTime);
        tag.setBoolean(TAG_LOCKED, locked);
    }

    private static WeaponBehavior behavior(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemWeapon)) return null;
        return ((ItemWeapon) stack.getItem()).getBehavior();
    }
}
