package com.gtnewhorizon.newgunrizons.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityVehicle;
import com.gtnewhorizon.newgunrizons.vehicle.FuelType;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleLayout;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleMode;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleWeapon;

import gregtech.api.enums.GTValues;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

/**
 * A vehicle, carried as an item and parked with a right click on the ground. The stack keeps the vehicle's energy,
 * hull integrity and loaded rounds, so a vehicle picked up and parked again is in the same state. Electric vehicles
 * charge like any GregTech battery of their tier.
 */
public class ItemVehicle extends Item implements ISpecialElectricItem, IElectricItemManager {

    public static final String TAG_CHARGE = "ngCharge";
    public static final String TAG_HEALTH = "ngHealth";
    public static final String TAG_AMMO = "ngAmmo";

    /** Ticks of 1 A at the vehicle's voltage that fill its tank or battery. */
    private static final long CAPACITY_TICKS = 6000L;

    private final String name;
    private final int tier;
    private final String role;
    private final VehicleMode mode;
    private final FuelType fuel;
    private final Item core;
    private final float maxHealth;
    private final float armor;
    private final float speed;
    private final float turnRate;
    private final List<VehicleWeapon> weapons;
    private VehicleLayout layout;

    private ItemVehicle(Builder b) {
        this.name = b.name;
        this.tier = b.tier;
        this.role = b.role;
        this.mode = b.mode;
        this.fuel = b.fuel;
        this.core = b.core;
        this.maxHealth = b.maxHealth;
        this.armor = b.armor;
        this.speed = b.speed;
        this.turnRate = b.turnRate;
        this.weapons = Collections.unmodifiableList(new ArrayList<>(b.weapons));
        this.setMaxStackSize(1);
        this.setUnlocalizedName("newgunrizons_" + name);
        this.setTextureName(NewGunrizonsMod.MODID + ":vehicle_" + name.toLowerCase());
        this.setCreativeTab(NewGunrizonsMod.HighTierTab);
    }

    // ---------------- stats ----------------

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public VehicleMode getMode() {
        return mode;
    }

    public FuelType getFuel() {
        return fuel;
    }

    /** Item that refills the whole battery at once (a tier core), or null. */
    public Item getCore() {
        return core;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    /** Fraction of incoming damage the hull stops. */
    public float getArmor() {
        return armor;
    }

    /** Top speed in blocks per tick. */
    public float getSpeed() {
        return speed;
    }

    /** Degrees per tick at full steering. */
    public float getTurnRate() {
        return turnRate;
    }

    public List<VehicleWeapon> getWeapons() {
        return weapons;
    }

    public VehicleLayout getLayout() {
        if (layout == null) layout = VehicleLayout.load(name);
        return layout;
    }

    public double getCapacity() {
        return (double) GTValues.V[tier] * CAPACITY_TICKS;
    }

    /** EU per tick while the vehicle is driven and moving (or airborne). */
    public double getDrain(boolean moving) {
        double v = GTValues.V[tier];
        if (mode.flies) return v / 6.0D;
        return moving ? v / 8.0D : v / 64.0D;
    }

    public double getShotCost(VehicleWeapon w) {
        return w.usesRounds() ? 0.0D : GTValues.V[tier] * w.energyPerShot;
    }

    // ---------------- stack state ----------------

    public static double getCharge(ItemStack stack, ItemVehicle v) {
        return stack.stackTagCompound == null ? 0.0D : stack.stackTagCompound.getDouble(TAG_CHARGE);
    }

    public static void setCharge(ItemStack stack, double charge) {
        tag(stack).setDouble(TAG_CHARGE, Math.max(0.0D, charge));
    }

    /** Hull integrity; a fresh vehicle is at full health. */
    public float getHealth(ItemStack stack) {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey(TAG_HEALTH)) return maxHealth;
        return stack.stackTagCompound.getFloat(TAG_HEALTH);
    }

    public static void setHealth(ItemStack stack, float health) {
        tag(stack).setFloat(TAG_HEALTH, health);
    }

    public int[] getAmmo(ItemStack stack) {
        int[] a = stack.stackTagCompound == null ? new int[0] : stack.stackTagCompound.getIntArray(TAG_AMMO);
        int[] out = new int[weapons.size()];
        System.arraycopy(a, 0, out, 0, Math.min(a.length, out.length));
        return out;
    }

    public static void setAmmo(ItemStack stack, int[] ammo) {
        tag(stack).setIntArray(TAG_AMMO, ammo);
    }

    private static NBTTagCompound tag(ItemStack stack) {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        return stack.stackTagCompound;
    }

    // ---------------- use ----------------

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        VehicleLayout l = getLayout();
        double px = x + 0.5D, py = y + 1.0D, pz = z + 0.5D;
        if (side == 0) py = y - l.height;
        double hw = l.width / 2.0D;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(px - hw, py + 0.05D, pz - hw, px + hw, py + l.height, pz + hw);
        if (!world.getCollidingBoundingBoxes(player, box)
            .isEmpty()) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "Not enough room here: the "
                        + new ItemStack(this).getDisplayName()
                        + " needs "
                        + fmt(l.width)
                        + " x "
                        + fmt(l.width)
                        + " x "
                        + fmt(l.height)
                        + " blocks of free space."));
            return true;
        }
        EntityVehicle v = new EntityVehicle(world, this, stack, player);
        float yaw = MathHelper.wrapAngleTo180_float(player.rotationYaw);
        v.setLocationAndAngles(px, py, pz, yaw, 0.0F);
        if (world.spawnEntityInWorld(v)) {
            world.playSoundAtEntity(player, "random.anvil_land", 0.4F, 1.4F);
            if (!player.capabilities.isCreativeMode) stack.stackSize = 0;
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        double charge = getCharge(stack, this);
        tooltip.add(EnumChatFormatting.GOLD + "Tier: " + GTValues.VN[tier] + EnumChatFormatting.GRAY + " - " + role);
        tooltip.add(
            "Seats: " + getLayout().seats.size()
                + ", hull "
                + fmt(getHealth(stack))
                + "/"
                + fmt(maxHealth)
                + ", armour "
                + Math.round(armor * 100)
                + "%");
        tooltip.add("Top speed: " + Math.round(speed * 72.0F) + " km/h (" + modeName() + ")");
        tooltip.add(
            String
                .format("Energy: %.0f%% of %,.0f EU (%s)", 100.0D * charge / getCapacity(), getCapacity(), fuel.label));
        int[] ammo = getAmmo(stack);
        for (int i = 0; i < weapons.size(); i++) {
            VehicleWeapon w = weapons.get(i);
            String trigger = w.trigger == VehicleWeapon.Trigger.PRIMARY ? "LMB" : "RMB";
            String seat = w.turret >= 0 && getLayout().turrets.get(w.turret).seat > 0 ? ", gunner seat" : "";
            tooltip.add(
                EnumChatFormatting.AQUA + w.name
                    + EnumChatFormatting.GRAY
                    + ": "
                    + fmt(w.damage)
                    + " damage ("
                    + trigger
                    + seat
                    + ")"
                    + (w.usesRounds() ? ", " + ammo[i] + "/" + w.magazine + " " + new ItemStack(w.ammo).getDisplayName()
                        : ""));
        }
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Right click a block: park. Right click the vehicle: get in.");
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Sneak + right click the empty vehicle: pick it up.");
        if (fuel.combustion || fuel.plasma) {
            tooltip.add(
                EnumChatFormatting.DARK_GRAY + "Refuel: right click it with a cell of "
                    + (fuel.plasma ? "plasma" : "diesel or another combustion fuel")
                    + ".");
        }
        if (fuel.electric) {
            tooltip
                .add(EnumChatFormatting.DARK_GRAY + "Charge in a " + GTValues.VN[tier] + " Battery Buffer or Charger.");
        }
        if (core != null) {
            tooltip.add(
                EnumChatFormatting.DARK_GRAY + "A "
                    + new ItemStack(core).getDisplayName()
                    + " refills the whole battery.");
        }
        tooltip.add(EnumChatFormatting.DARK_GRAY + "Repair: right click it with plates.");
    }

    private String modeName() {
        switch (mode) {
            case HOVER:
                return "hover";
            case AIR:
                return "flight";
            case SPACE:
                return "spaceflight";
            default:
                return "ground";
        }
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
        return 1.0D - Math.min(1.0D, getCharge(stack, this) / getCapacity());
    }

    // ---------------- IC2 electric item: the vehicle's battery ----------------

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
        return fuel.electric ? getCapacity() : 0.0D;
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
        if (!fuel.electric || stack == null || amount <= 0.0D || chargerTier < tier) return 0.0D;
        double charge = getCharge(stack, this);
        double accepted = Math.min(amount, getCapacity() - charge);
        if (!ignoreTransferLimit) accepted = Math.min(accepted, getTransferLimit(stack));
        if (accepted <= 0.0D) return 0.0D;
        if (!simulate) setCharge(stack, charge + accepted);
        return accepted;
    }

    @Override
    public double discharge(ItemStack stack, double amount, int dischargerTier, boolean ignoreTransferLimit,
        boolean externally, boolean simulate) {
        return 0.0D;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return getCharge(stack, this);
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
        private String role = "";
        private VehicleMode mode = VehicleMode.GROUND;
        private FuelType fuel = FuelType.EU;
        private Item core;
        private float maxHealth = 40.0F, armor = 0.2F, speed = 0.4F, turnRate = 4.0F;
        private final List<VehicleWeapon> weapons = new ArrayList<>();

        public Builder(String name, int tier) {
            this.name = name;
            this.tier = tier;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder drive(VehicleMode mode, float speed, float turnRate) {
            this.mode = mode;
            this.speed = speed;
            this.turnRate = turnRate;
            return this;
        }

        public Builder fuel(FuelType fuel, Item core) {
            this.fuel = fuel;
            this.core = core;
            return this;
        }

        public Builder hull(float maxHealth, float armor) {
            this.maxHealth = maxHealth;
            this.armor = armor;
            return this;
        }

        public Builder weapon(VehicleWeapon w) {
            weapons.add(w);
            return this;
        }

        public ItemVehicle build() {
            return new ItemVehicle(this);
        }
    }
}
