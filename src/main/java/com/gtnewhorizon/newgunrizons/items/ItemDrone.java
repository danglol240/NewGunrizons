package com.gtnewhorizon.newgunrizons.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityDrone;

import gregtech.api.enums.GTValues;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

/**
 * A combat drone, carried as an item and deployed with a right click. It flies on its own battery, which a GregTech
 * Battery Buffer or Charger of its tier recharges, and fires either real rounds loaded from the owner's inventory
 * (LV-IV) or energy from its battery (LuV and up). See {@link EntityDrone} for how it behaves in flight.
 */
public class ItemDrone extends Item implements ShotProfile, ISpecialElectricItem, IElectricItemManager {

    public static final String TAG_CHARGE = "ngCharge";
    public static final String TAG_ROUNDS = "ngRounds";

    /** Ticks of 1 A at the drone's voltage that fill its battery. */
    private static final long CHARGE_TICKS = 600L;

    private final String name;
    private final int tier;
    private final DroneLayout layout;
    private final float renderScale;
    private final float maxHealth;
    private final float speed;
    private final float range;
    private final float damage;
    private final int fireInterval;
    private final int burst;
    private final float spread;
    private final float projectileSpeed;
    private final int magazine;
    private final Item ammo;
    private final String sound;
    private final WeaponBehavior behavior;
    private final ProjectileStyle style;
    private final int color;
    private final float scale;
    private final String role;

    private ItemDrone(Builder b) {
        this.name = b.name;
        this.tier = b.tier;
        this.layout = b.layout;
        this.renderScale = b.renderScale;
        this.maxHealth = b.maxHealth;
        this.speed = b.speed;
        this.range = b.range;
        this.damage = b.damage;
        this.fireInterval = b.fireInterval;
        this.burst = b.burst;
        this.spread = b.spread;
        this.projectileSpeed = b.projectileSpeed;
        this.magazine = b.magazine;
        this.ammo = b.ammo;
        this.sound = b.sound;
        this.behavior = b.behavior;
        this.style = b.style;
        this.color = b.color;
        this.scale = b.scale;
        this.role = b.role;
        this.setMaxStackSize(1);
        this.setUnlocalizedName("newgunrizons_" + name);
        this.setTextureName(NewGunrizonsMod.MODID + ":drone_" + name.toLowerCase());
        this.setCreativeTab(NewGunrizonsMod.HighTierTab);
    }

    // ---------------- stats ----------------

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public DroneLayout getLayout() {
        return layout;
    }

    public float getRenderScale() {
        return renderScale;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getSpeed() {
        return speed;
    }

    public float getRange() {
        return range;
    }

    public float getDamage() {
        return damage;
    }

    public int getFireInterval() {
        return fireInterval;
    }

    public int getBurst() {
        return burst;
    }

    public float getSpread() {
        return spread;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    /** Rounds the drone carries; 0 for energy weapons. */
    public int getMagazine() {
        return magazine;
    }

    public boolean usesRounds() {
        return magazine > 0 && ammo != null;
    }

    public Item getAmmo() {
        return ammo;
    }

    public String getSound() {
        return sound;
    }

    /** Battery capacity in EU. */
    public double getCapacity() {
        return (double) GTValues.V[tier] * CHARGE_TICKS;
    }

    /** EU drained per tick while flying. */
    public double getHoverDrain() {
        return GTValues.V[tier] / 16.0D;
    }

    /** EU per shot for energy weapons. */
    public double getShotCost() {
        return usesRounds() ? 0.0D : GTValues.V[tier] * 4.0D;
    }

    @Override
    public WeaponBehavior getBehavior() {
        return behavior;
    }

    @Override
    public ProjectileStyle getProjectileStyle() {
        return style;
    }

    @Override
    public int getProjectileColor() {
        return color;
    }

    @Override
    public float getProjectileScale() {
        return scale;
    }

    // ---------------- stack state ----------------

    public static double getStoredCharge(ItemStack stack) {
        return stack.stackTagCompound == null ? 0.0D : stack.stackTagCompound.getDouble(TAG_CHARGE);
    }

    public static void setStoredCharge(ItemStack stack, double charge) {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setDouble(TAG_CHARGE, Math.max(0.0D, charge));
    }

    public static int getRounds(ItemStack stack) {
        return stack.stackTagCompound == null ? 0 : stack.stackTagCompound.getInteger(TAG_ROUNDS);
    }

    public static void setRounds(ItemStack stack, int rounds) {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setInteger(TAG_ROUNDS, Math.max(0, rounds));
    }

    // ---------------- use ----------------

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;
        if (player.isSneaking()) {
            reload(stack, player);
            return stack;
        }
        if (getStoredCharge(stack) < getCapacity() * 0.1D) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "Battery too low to take off: charge it in a "
                        + GTValues.VN[tier]
                        + " Battery Buffer or Charger."));
            return stack;
        }
        EntityDrone drone = new EntityDrone(world, this, player, getStoredCharge(stack), getRounds(stack));
        if (world.spawnEntityInWorld(drone)) {
            world.playSoundAtEntity(player, "random.click", 0.6F, 1.6F);
            stack.stackSize = 0;
        }
        return stack;
    }

    /** Loads compatible rounds from the player's inventory into the drone's magazine. */
    private void reload(ItemStack stack, EntityPlayer player) {
        if (!usesRounds()) {
            player.addChatMessage(new ChatComponentText("This drone fires from its battery; it takes no rounds."));
            return;
        }
        int rounds = getRounds(stack);
        int missing = magazine - rounds;
        ItemStack[] inv = player.inventory.mainInventory;
        for (int i = 0; i < inv.length && missing > 0; i++) {
            ItemStack s = inv[i];
            if (s == null || s.getItem() != ammo) continue;
            int take = Math.min(missing, s.stackSize);
            s.stackSize -= take;
            if (s.stackSize <= 0) inv[i] = null;
            missing -= take;
            rounds += take;
        }
        setRounds(stack, rounds);
        player.inventoryContainer.detectAndSendChanges();
        player.addChatMessage(
            new ChatComponentText(
                "Magazine: " + rounds + "/" + magazine + (rounds < magazine ? " (no more " + ammoName() + ")" : "")));
    }

    private String ammoName() {
        return ammo == null ? "rounds" : new ItemStack(ammo).getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        double charge = getStoredCharge(stack);
        tooltip.add(EnumChatFormatting.GOLD + "Tier: " + GTValues.VN[tier] + EnumChatFormatting.GRAY + " - " + role);
        tooltip.add(
            "Damage: " + fmt(damage)
                + (burst > 1 ? " x" + burst + " burst" : "")
                + ", range "
                + fmt(range)
                + " m");
        tooltip.add(String.format("Battery: %.0f%% (%,.0f EU)", 100.0D * charge / getCapacity(), charge));
        if (usesRounds()) {
            tooltip.add("Magazine: " + getRounds(stack) + "/" + magazine + " " + ammoName());
        } else {
            tooltip.add(String.format("Energy weapon: %,.0f EU per shot", getShotCost()));
        }
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Right click: deploy. Right click the drone: recall.");
        if (usesRounds()) tooltip.add(EnumChatFormatting.DARK_GRAY + "Sneak + right click: load rounds.");
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Recharge in a " + GTValues.VN[tier] + " Battery Buffer or Charger.");
    }

    private static String fmt(float v) {
        return v == (int) v ? Integer.toString((int) v) : String.format("%.1f", v);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - Math.min(1.0D, getStoredCharge(stack) / getCapacity());
    }

    // ---------------- IC2 electric item: the drone's battery ----------------

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
        return getCapacity();
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
        double charge = getStoredCharge(stack);
        double accepted = Math.min(amount, getCapacity() - charge);
        if (!ignoreTransferLimit) accepted = Math.min(accepted, getTransferLimit(stack));
        if (accepted <= 0.0D) return 0.0D;
        if (!simulate) setStoredCharge(stack, charge + accepted);
        return accepted;
    }

    @Override
    public double discharge(ItemStack stack, double amount, int dischargerTier, boolean ignoreTransferLimit,
        boolean externally, boolean simulate) {
        return 0.0D;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return getStoredCharge(stack);
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

    // ---------------- builder ----------------

    public static final class Builder {

        private final String name;
        private final int tier;
        private DroneLayout layout = DroneLayout.QUAD;
        private float renderScale = 0.5F;
        private float maxHealth = 10.0F;
        private float speed = 0.5F;
        private float range = 16.0F;
        private float damage = 4.0F;
        private int fireInterval = 20;
        private int burst = 1;
        private float spread = 2.0F;
        private float projectileSpeed = 6.0F;
        private int magazine;
        private Item ammo;
        private String sound = "random.bow";
        private WeaponBehavior behavior = WeaponBehavior.NONE;
        private ProjectileStyle style = ProjectileStyle.TRACER;
        private int color = 0xFFD070;
        private float scale = 0.5F;
        private String role = "";

        public Builder(String name, int tier) {
            this.name = name;
            this.tier = tier;
        }

        public Builder frame(DroneLayout layout, float renderScale) {
            this.layout = layout;
            this.renderScale = renderScale;
            return this;
        }

        public Builder flight(float maxHealth, float speed, float range) {
            this.maxHealth = maxHealth;
            this.speed = speed;
            this.range = range;
            return this;
        }

        public Builder gun(float damage, int fireInterval, int burst, float spread, float projectileSpeed) {
            this.damage = damage;
            this.fireInterval = fireInterval;
            this.burst = burst;
            this.spread = spread;
            this.projectileSpeed = projectileSpeed;
            return this;
        }

        public Builder rounds(int magazine, Item ammo) {
            this.magazine = magazine;
            this.ammo = ammo;
            return this;
        }

        public Builder sound(String sound) {
            if (sound != null) this.sound = sound;
            return this;
        }

        public Builder behavior(WeaponBehavior behavior) {
            this.behavior = behavior;
            return this;
        }

        public Builder look(ProjectileStyle style, int color, float scale) {
            this.style = style;
            this.color = color;
            this.scale = scale;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public ItemDrone build() {
            return new ItemDrone(this);
        }
    }
}
