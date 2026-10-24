package com.gtnewhorizon.newgunrizons.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleLayout;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleMode;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleWeapon;
import com.gtnewhorizon.newgunrizons.weapon.Hitscan;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

/**
 * A parked or driven vehicle. The driver rides the vehicle itself; every other seat is an {@link EntityVehicleSeat}
 * created when someone climbs in. The driver steers with the movement keys, the climb and descend keys move flying
 * vehicles up and down, and each seat fires the weapons it controls with the mouse buttons (see
 * {@link com.gtnewhorizon.newgunrizons.client.handlers.VehicleClientHandler}).
 * <p>
 * The server runs the physics from the controls the riders send; the driver's own client runs the same physics so
 * the vehicle answers at once, and ignores the server's small corrections.
 */
public class EntityVehicle extends Entity implements IEntityAdditionalSpawnData {

    private static final int DW_HEALTH = 20;
    private static final int DW_CHARGE = 21;
    private static final int DW_AMMO = 22;
    private static final String TAG_VEHICLE = "vehicle";
    private static final String TAG_OWNER = "owner";
    private static final int MAX_SEATS = 8;

    private ItemVehicle vehicle;
    private String ownerName = "";
    private double charge;
    private int[] ammo = new int[0];
    private int[] cooldown = new int[0];
    private final EntityVehicleSeat[] seatEntities = new EntityVehicleSeat[MAX_SEATS];

    // controls: movement from the driver, triggers and aim per seat
    private float inForward, inStrafe;
    private boolean inUp, inDown;
    private final boolean[] fire1 = new boolean[MAX_SEATS];
    private final boolean[] fire2 = new boolean[MAX_SEATS];
    private int inputAge;
    private double speedForward;
    private boolean syncDirty;

    // client-side interpolation of server positions for vehicles we do not drive
    private int lerpSteps;
    private double lerpX, lerpY, lerpZ;
    private float lerpYaw, lerpPitch;

    /** Client-side animation state, read by the renderer. */
    public float rotorAngle, prevRotorAngle;
    public float roll, prevRoll;
    public float throttle, prevThrottle;

    public EntityVehicle(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 1.0F);
        this.yOffset = 0.0F;
    }

    public EntityVehicle(World world, ItemVehicle vehicle, ItemStack stack, EntityPlayer owner) {
        this(world);
        this.vehicle = vehicle;
        this.ownerName = owner.getCommandSenderName();
        this.charge = ItemVehicle.getCharge(stack, vehicle);
        this.ammo = vehicle.getAmmo(stack);
        this.cooldown = new int[ammo.length];
        applyStats();
        setHealth(vehicle.getHealth(stack));
        syncDirty = true;
    }

    private void applyStats() {
        if (vehicle == null) return;
        VehicleLayout l = vehicle.getLayout();
        this.setSize(l.width, l.height);
        this.stepHeight = vehicle.getMode().stepHeight;
        if (ammo.length != vehicle.getWeapons()
            .size()) {
            int[] a = new int[vehicle.getWeapons()
                .size()];
            System.arraycopy(ammo, 0, a, 0, Math.min(a.length, ammo.length));
            ammo = a;
        }
        if (cooldown.length != ammo.length) cooldown = new int[ammo.length];
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(DW_HEALTH, 1.0F);
        this.dataWatcher.addObject(DW_CHARGE, 0.0F);
        this.dataWatcher.addObject(DW_AMMO, "");
    }

    // ---------------- accessors ----------------

    public ItemVehicle getVehicle() {
        return vehicle;
    }

    public float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(DW_HEALTH);
    }

    private void setHealth(float health) {
        this.dataWatcher.updateObject(DW_HEALTH, health);
    }

    /** Battery or tank fill, 0..1 (synced). */
    public float getChargeFraction() {
        return this.dataWatcher.getWatchableObjectFloat(DW_CHARGE);
    }

    /** Rounds left in each weapon, as shown in the HUD (synced). */
    public int[] getSyncedAmmo() {
        String s = this.dataWatcher.getWatchableObjectString(DW_AMMO);
        if (s == null || s.isEmpty()) return new int[0];
        String[] parts = s.split(",");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                out[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                out[i] = 0;
            }
        }
        return out;
    }

    public double getSpeedForward() {
        return speedForward;
    }

    /** The player in a seat (0 = driver), on either side. */
    public EntityPlayer occupant(int seat) {
        if (seat == 0) return riddenByEntity instanceof EntityPlayer ? (EntityPlayer) riddenByEntity : null;
        if (!worldObj.isRemote) {
            EntityVehicleSeat s = seat < MAX_SEATS ? seatEntities[seat] : null;
            return s != null && !s.isDead && s.riddenByEntity instanceof EntityPlayer ? (EntityPlayer) s.riddenByEntity
                : null;
        }
        for (Object o : worldObj.loadedEntityList) {
            if (o instanceof EntityVehicleSeat) {
                EntityVehicleSeat s = (EntityVehicleSeat) o;
                if (s.getVehicle() == this && s.getSeat() == seat && s.riddenByEntity instanceof EntityPlayer) {
                    return (EntityPlayer) s.riddenByEntity;
                }
            }
        }
        return null;
    }

    /** True when the entity sits in this vehicle, as driver or in a seat. */
    public boolean isOccupant(Entity e) {
        if (e == null) return false;
        if (e.ridingEntity == this) return true;
        return e.ridingEntity instanceof EntityVehicleSeat && ((EntityVehicleSeat) e.ridingEntity).getVehicle() == this;
    }

    /** Whether a shot from {@code shooter} should pass through {@code target} (its own vehicle or seat). */
    public static boolean carries(Entity target, Entity shooter) {
        if (target instanceof EntityVehicleSeat) return true;
        return target instanceof EntityVehicle && ((EntityVehicle) target).isOccupant(shooter);
    }

    /** The vehicle an entity sits in, or null. */
    public static EntityVehicle of(Entity rider) {
        if (rider == null) return null;
        if (rider.ridingEntity instanceof EntityVehicle) return (EntityVehicle) rider.ridingEntity;
        if (rider.ridingEntity instanceof EntityVehicleSeat) return ((EntityVehicleSeat) rider.ridingEntity).getVehicle();
        return null;
    }

    /** The seat index of a rider, -1 if not in this vehicle. */
    public int seatOf(Entity rider) {
        if (rider == null) return -1;
        if (rider.ridingEntity == this) return 0;
        if (rider.ridingEntity instanceof EntityVehicleSeat && ((EntityVehicleSeat) rider.ridingEntity).getVehicle() == this) {
            return ((EntityVehicleSeat) rider.ridingEntity).getSeat();
        }
        return -1;
    }

    /** Controls from a rider (server: from the network; client: the local driver). */
    public void setInput(int seat, float forward, float strafe, boolean up, boolean down, boolean primary,
        boolean secondary) {
        if (seat < 0 || seat >= MAX_SEATS) return;
        if (seat == 0) {
            this.inForward = MathHelper.clamp_float(forward, -1.0F, 1.0F);
            this.inStrafe = MathHelper.clamp_float(strafe, -1.0F, 1.0F);
            this.inUp = up;
            this.inDown = down;
            this.inputAge = 0;
        }
        this.fire1[seat] = primary;
        this.fire2[seat] = secondary;
    }

    // ---------------- seats ----------------

    /** World position of a seat (feet of the rider). */
    public Vec3 seatPosition(int seat) {
        VehicleLayout l = vehicle.getLayout();
        if (seat >= l.seats.size()) seat = 0;
        VehicleLayout.Point p = l.seats.get(seat);
        double[] v = l.toVehicle(p.x, p.y, p.z);
        double[] w = VehicleLayout.toWorld(v[0], v[1], v[2], rotationYaw);
        return Vec3.createVectorHelper(posX + w[0], posY + w[1], posZ + w[2]);
    }

    @Override
    public void updateRiderPosition() {
        if (riddenByEntity == null || vehicle == null) return;
        Vec3 p = seatPosition(0);
        riddenByEntity.setPosition(p.xCoord, p.yCoord - 0.45D + riddenByEntity.getYOffset(), p.zCoord);
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    private int freeSeat() {
        int n = Math.min(MAX_SEATS, vehicle.getLayout().seats.size());
        for (int i = 1; i < n; i++) {
            if (seatEntities[i] == null || seatEntities[i].isDead || seatEntities[i].riddenByEntity == null) return i;
        }
        return -1;
    }

    private boolean hasPassengers() {
        for (EntityVehicleSeat s : seatEntities) if (s != null && !s.isDead && s.riddenByEntity != null) return true;
        return false;
    }

    // ---------------- interaction ----------------

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (worldObj.isRemote || vehicle == null) return true;
        if (isOccupant(player)) return true;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && useItem(player, held)) return true;
        if (player.isSneaking()) {
            if (riddenByEntity != null || hasPassengers()) {
                tell(player, "Everyone has to get out first.");
            } else if (!ownerName.isEmpty() && !ownerName.equals(player.getCommandSenderName())
                && !player.capabilities.isCreativeMode) {
                    tell(player, "This vehicle belongs to " + ownerName + ".");
                } else {
                    pickUp(player);
                }
            return true;
        }
        if (riddenByEntity == null) {
            player.mountEntity(this);
            return true;
        }
        int seat = freeSeat();
        if (seat < 0) {
            tell(player, "All seats are taken.");
            return true;
        }
        EntityVehicleSeat s = new EntityVehicleSeat(worldObj, this, seat);
        Vec3 p = seatPosition(seat);
        s.setPosition(p.xCoord, p.yCoord, p.zCoord);
        if (worldObj.spawnEntityInWorld(s)) {
            seatEntities[seat] = s;
            player.mountEntity(s);
        }
        return true;
    }

    /** Ammunition, fuel, cores and repair plates. Returns true when the item was used. */
    private boolean useItem(EntityPlayer player, ItemStack held) {
        boolean creative = player.capabilities.isCreativeMode;
        List<VehicleWeapon> weapons = vehicle.getWeapons();
        for (int i = 0; i < weapons.size(); i++) {
            VehicleWeapon w = weapons.get(i);
            if (!w.usesRounds() || held.getItem() != w.ammo) continue;
            int take = Math.min(w.magazine - ammo[i], held.stackSize);
            if (take <= 0) {
                tell(player, w.name + " is fully loaded.");
                return true;
            }
            if (!creative) held.stackSize -= take;
            ammo[i] += take;
            syncDirty = true;
            tell(player, w.name + ": " + ammo[i] + "/" + w.magazine);
            worldObj.playSoundAtEntity(this, "random.click", 0.8F, 1.2F);
            consumed(player, held);
            return true;
        }
        double cap = vehicle.getCapacity();
        if (vehicle.getCore() != null && held.getItem() == vehicle.getCore()) {
            if (charge >= cap * 0.99D) {
                tell(player, "The battery is already full.");
                return true;
            }
            if (!creative) held.stackSize--;
            charge = cap;
            syncDirty = true;
            tell(player, "Battery refilled from the core.");
            worldObj.playSoundAtEntity(this, "random.levelup", 0.6F, 1.6F);
            consumed(player, held);
            return true;
        }
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(held);
        if (fluid != null) {
            long eu = vehicle.getFuel()
                .euPerLitre(fluid);
            if (eu <= 0L) {
                tell(player, "It cannot run on " + fluid.getLocalizedName() + " (" + vehicle.getFuel().label + ").");
                return true;
            }
            if (charge >= cap * 0.99D) {
                tell(player, "The tank is full.");
                return true;
            }
            charge = Math.min(cap, charge + (double) eu * fluid.amount);
            syncDirty = true;
            if (!creative) {
                ItemStack empty = FluidContainerRegistry.drainFluidContainer(held);
                held.stackSize--;
                if (empty != null && !player.inventory.addItemStackToInventory(empty)) {
                    player.dropPlayerItemWithRandomChoice(empty, false);
                }
            }
            tell(player, String.format("Refuelled: %.0f%%", 100.0D * charge / cap));
            worldObj.playSoundAtEntity(this, "game.neutral.swim", 0.5F, 1.2F);
            consumed(player, held);
            return true;
        }
        if (isPlate(held)) {
            float health = getHealth(), max = vehicle.getMaxHealth();
            if (health >= max) {
                tell(player, "The hull is intact.");
                return true;
            }
            if (!creative) held.stackSize--;
            setHealth(Math.min(max, health + max * 0.08F));
            tell(player, String.format("Hull: %.0f/%.0f", getHealth(), max));
            worldObj.playSoundAtEntity(this, "random.anvil_use", 0.5F, 1.3F);
            consumed(player, held);
            return true;
        }
        return false;
    }

    private static boolean isPlate(ItemStack stack) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            String n = OreDictionary.getOreName(id);
            if (n.startsWith("plate") && !n.startsWith("plateDense")) return true;
        }
        return false;
    }

    private static void consumed(EntityPlayer player, ItemStack held) {
        if (held.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        player.inventoryContainer.detectAndSendChanges();
    }

    private ItemStack toStack() {
        ItemStack stack = new ItemStack(vehicle);
        ItemVehicle.setCharge(stack, charge);
        ItemVehicle.setHealth(stack, getHealth());
        ItemVehicle.setAmmo(stack, ammo);
        return stack;
    }

    private void pickUp(EntityPlayer player) {
        ItemStack stack = toStack();
        if (!player.inventory.addItemStackToInventory(stack)) entityDropItem(stack, 0.5F);
        worldObj.playSoundAtEntity(this, "random.pop", 0.6F, 0.8F);
        setDead();
    }

    private void tell(EntityPlayer player, String message) {
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GRAY + "[" + new ItemStack(vehicle).getDisplayName() + "] " + message));
    }

    // ---------------- update ----------------

    private boolean drivenLocally() {
        return worldObj.isRemote && riddenByEntity instanceof EntityPlayer
            && com.gtnewhorizon.newgunrizons.NewGunrizonsMod.proxy.isLocalPlayer((EntityPlayer) riddenByEntity);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (vehicle == null) {
            if (!worldObj.isRemote) setDead();
            return;
        }
        prevRotorAngle = rotorAngle;
        prevRoll = roll;
        prevThrottle = throttle;
        EntityPlayer driver = occupant(0);

        if (worldObj.isRemote && !drivenLocally()) {
            if (lerpSteps > 0) {
                double k = 1.0D / lerpSteps;
                double x = posX + (lerpX - posX) * k, y = posY + (lerpY - posY) * k, z = posZ + (lerpZ - posZ) * k;
                rotationYaw += MathHelper.wrapAngleTo180_double(lerpYaw - rotationYaw) * k;
                rotationPitch += (lerpPitch - rotationPitch) * k;
                lerpSteps--;
                setPosition(x, y, z);
                setRotation(rotationYaw, rotationPitch);
            }
            animate(driver != null);
            return;
        }

        if (driver == null || ++inputAge > 20) {
            inForward = inStrafe = 0.0F;
            inUp = inDown = false;
            if (driver == null) fire1[0] = fire2[0] = false;
        }
        boolean hasEnergy = worldObj.isRemote ? getChargeFraction() > 0.0F : charge > 0.0D;
        boolean powered = driver != null && (hasEnergy || driver.capabilities.isCreativeMode);
        move(driver, powered);
        animate(driver != null);

        if (!worldObj.isRemote) {
            if (driver != null && !driver.capabilities.isCreativeMode) {
                boolean moving = Math.abs(inForward) > 0.01F || Math.abs(inStrafe) > 0.01F || inUp || inDown;
                if (moving || vehicle.getMode().flies && !onGround) {
                    charge = Math.max(0.0D, charge - vehicle.getDrain(moving));
                }
            }
            for (int i = 1; i < MAX_SEATS; i++) {
                if (seatEntities[i] != null && (seatEntities[i].isDead || seatEntities[i].riddenByEntity == null)) {
                    if (!seatEntities[i].isDead) seatEntities[i].setDead();
                    seatEntities[i] = null;
                    fire1[i] = fire2[i] = false;
                }
            }
            fireWeapons();
            ram(driver);
            if (syncDirty || ticksExisted % 10 == 0) sync();
        }
    }

    private void move(EntityPlayer driver, boolean powered) {
        VehicleMode mode = vehicle.getMode();
        float speed = vehicle.getSpeed();
        float fwd = powered ? inForward : 0.0F, strafe = powered ? inStrafe : 0.0F;
        if (!mode.flies) {
            if (Math.abs(fwd) > 0.01F || Math.abs(speedForward) > 0.02D) {
                float dir = speedForward < -0.01D ? -1.0F : 1.0F;
                rotationYaw -= strafe * vehicle.getTurnRate() * dir;
            } else if (Math.abs(strafe) > 0.01F) {
                rotationYaw -= strafe * vehicle.getTurnRate() * 0.6F; // pivot turn
            }
            double target = fwd > 0.0F ? fwd * speed : fwd * speed * 0.5D;
            double accel = speed * 0.04D;
            if (Math.abs(fwd) < 0.01F) speedForward *= onGround || mode == VehicleMode.HOVER ? 0.9D : 0.98D;
            else speedForward += MathHelper.clamp_double(target - speedForward, -accel, accel);
            if (mode == VehicleMode.GROUND && isInWater()) speedForward *= 0.85D;
            double rad = Math.toRadians(rotationYaw);
            motionX = -Math.sin(rad) * speedForward;
            motionZ = Math.cos(rad) * speedForward;
            if (mode == VehicleMode.GROUND) {
                motionY -= 0.08D;
                motionY *= 0.98D;
            } else {
                double gap = groundGap(5.0D);
                if (gap < 5.0D) motionY += (1.3D - gap) * 0.12D - motionY * 0.35D;
                else motionY = Math.max(motionY - 0.04D, -0.6D);
            }
        } else {
            if (driver != null) {
                float turn = MathHelper.wrapAngleTo180_float(driver.rotationYaw - rotationYaw);
                rotationYaw += MathHelper.clamp_float(turn, -vehicle.getTurnRate(), vehicle.getTurnRate());
                if (mode == VehicleMode.SPACE) {
                    float pitch = MathHelper.clamp_float(driver.rotationPitch, -60.0F, 60.0F);
                    rotationPitch += MathHelper.clamp_float(pitch - rotationPitch, -4.0F, 4.0F);
                }
            }
            if (powered) {
                double yaw = Math.toRadians(rotationYaw);
                double pitch = mode == VehicleMode.SPACE ? Math.toRadians(rotationPitch) : 0.0D;
                double fx = -Math.sin(yaw) * Math.cos(pitch), fy = -Math.sin(pitch), fz = Math.cos(yaw) * Math.cos(pitch);
                double rx = -Math.cos(yaw), rz = -Math.sin(yaw);
                double f = fwd > 0.0F ? fwd : fwd * 0.4F;
                double vert = inUp ? 1.0D : inDown ? -1.0D : 0.0D;
                double tx = (fx * f - rx * strafe * 0.5D) * speed;
                double ty = fy * f * speed + vert * speed * 0.5D;
                double tz = (fz * f - rz * strafe * 0.5D) * speed;
                double a = 0.06D;
                motionX += (tx - motionX) * a;
                motionY += (ty - motionY) * a * 2.0D;
                motionZ += (tz - motionZ) * a;
                speedForward = motionX * -Math.sin(yaw) + motionZ * Math.cos(yaw);
            } else {
                motionX *= 0.95D;
                motionZ *= 0.95D;
                if (!onGround) motionY = Math.max(motionY - 0.03D, -0.5D);
                if (rotationPitch != 0.0F) rotationPitch *= 0.9F;
                speedForward *= 0.95D;
            }
        }
        moveEntity(motionX, motionY, motionZ);
        if (isCollidedHorizontally) {
            speedForward *= 0.5D;
            if (mode.flies) {
                motionX *= 0.5D;
                motionZ *= 0.5D;
            }
        }
        if (onGround && !mode.flies && mode != VehicleMode.HOVER) motionY = 0.0D;
        rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);
    }

    /** Distance from the vehicle's bottom down to solid ground or a liquid surface, up to max. */
    private double groundGap(double max) {
        Vec3 from = Vec3.createVectorHelper(posX, posY + 0.2D, posZ);
        Vec3 to = Vec3.createVectorHelper(posX, posY - max, posZ);
        MovingObjectPosition hit = worldObj.func_147447_a(from, to, true, false, false);
        return hit == null ? max : Math.max(0.0D, posY - hit.hitVec.yCoord);
    }

    private void animate(boolean driven) {
        double vx = posX - prevPosX, vz = posZ - prevPosZ;
        float target = (float) Math.min(1.0D, Math.sqrt(vx * vx + vz * vz) / Math.max(0.05F, vehicle.getSpeed()));
        throttle += (Math.max(target, driven ? 0.25F : 0.0F) - throttle) * 0.1F;
        rotorAngle += 10.0F + 50.0F * throttle;
        float turn = MathHelper.wrapAngleTo180_float(rotationYaw - prevRotationYaw);
        float tilt = vehicle.getMode().flies ? turn * 4.0F : 0.0F;
        roll += (MathHelper.clamp_float(tilt, -25.0F, 25.0F) - roll) * 0.2F;
    }

    private void sync() {
        syncDirty = false;
        dataWatcher.updateObject(DW_CHARGE, (float) (charge / vehicle.getCapacity()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ammo.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(ammo[i]);
        }
        dataWatcher.updateObject(DW_AMMO, sb.toString());
    }

    // ---------------- weapons ----------------

    private void fireWeapons() {
        List<VehicleWeapon> weapons = vehicle.getWeapons();
        VehicleLayout l = vehicle.getLayout();
        for (int i = 0; i < weapons.size(); i++) {
            if (cooldown[i] > 0) {
                cooldown[i]--;
                continue;
            }
            VehicleWeapon w = weapons.get(i);
            int seat = w.turret >= 0 ? l.turrets.get(w.turret).seat : 0;
            EntityPlayer gunner = occupant(seat);
            int controls = seat;
            if (gunner == null && seat != 0) {
                gunner = occupant(0); // an empty gunner seat leaves the gun to the driver
                controls = 0;
            }
            if (gunner == null) continue;
            boolean pulled = w.trigger == VehicleWeapon.Trigger.PRIMARY ? fire1[controls] : fire2[controls];
            if (!pulled) continue;
            boolean creative = gunner.capabilities.isCreativeMode;
            double cost = vehicle.getShotCost(w);
            if (!creative) {
                if (w.usesRounds() ? ammo[i] <= 0 : charge < cost) {
                    cooldown[i] = 20;
                    worldObj.playSoundAtEntity(this, "random.click", 0.6F, 0.7F);
                    continue;
                }
            }
            float[] aim = aim(w, gunner);
            Vec3 dir = look(aim[0], aim[1]);
            Vec3 muzzle = muzzle(w, aim[0], dir);
            if (w.isHitscan()) {
                Hitscan.fire(w, gunner, muzzle, dir, w.damage, w.spread);
            } else {
                EntityBullet bullet = new EntityBullet(w, worldObj, gunner, w.speed, 0.0F, w.spread, w.damage, w.explosion);
                bullet.setLocationAndAngles(muzzle.xCoord, muzzle.yCoord, muzzle.zCoord, aim[0], aim[1]);
                bullet.setThrowableHeading(dir.xCoord, dir.yCoord, dir.zCoord, w.speed, w.spread);
                worldObj.spawnEntityInWorld(bullet);
            }
            worldObj.playSoundEffect(muzzle.xCoord, muzzle.yCoord, muzzle.zCoord, w.sound, 2.0F, 0.9F + rand.nextFloat() * 0.2F);
            if (!creative) {
                if (w.usesRounds()) ammo[i]--;
                else charge -= cost;
                syncDirty = true;
            }
            cooldown[i] = w.interval;
        }
    }

    /** Yaw and pitch a weapon points at: its gunner's view for turrets, the nose for fixed mounts. */
    public float[] aim(VehicleWeapon w, EntityLivingBase gunner) {
        if (w.turret >= 0) {
            VehicleLayout.Turret t = vehicle.getLayout().turrets.get(w.turret);
            float pitch = gunner == null ? 0.0F : MathHelper.clamp_float(gunner.rotationPitch, t.minPitch, t.maxPitch);
            return new float[] { gunner == null ? rotationYaw : gunner.rotationYaw, pitch };
        }
        float pitch = vehicle.getMode() == VehicleMode.SPACE ? rotationPitch
            : MathHelper.clamp_float(gunner == null ? 0.0F : gunner.rotationPitch, -15.0F, 25.0F);
        return new float[] { rotationYaw, pitch };
    }

    private static Vec3 look(float yaw, float pitch) {
        double y = Math.toRadians(yaw), p = Math.toRadians(pitch);
        return Vec3.createVectorHelper(-Math.sin(y) * Math.cos(p), -Math.sin(p), Math.cos(y) * Math.cos(p));
    }

    private Vec3 muzzle(VehicleWeapon w, float aimYaw, Vec3 dir) {
        VehicleLayout l = vehicle.getLayout();
        if (w.turret >= 0) {
            VehicleLayout.Turret t = l.turrets.get(w.turret);
            double[] base = l.toVehicle(t.x, t.y, t.z);
            double[] bw = VehicleLayout.toWorld(base[0], base[1], base[2], rotationYaw);
            double[] gun = l.toVehicle(t.gx, t.gy, t.gz);
            double[] gw = VehicleLayout.toWorld(gun[0], gun[1], gun[2], aimYaw);
            double len = t.length * l.scale * VehicleLayout.UNIT;
            return Vec3.createVectorHelper(
                posX + bw[0] + gw[0] + dir.xCoord * len,
                posY + bw[1] + gw[1] + dir.yCoord * len,
                posZ + bw[2] + gw[2] + dir.zCoord * len);
        }
        VehicleLayout.Point m = l.mounts.get(Math.min(w.mount, l.mounts.size() - 1));
        double[] v = l.toVehicle(m.x, m.y, m.z);
        double[] mw = VehicleLayout.toWorld(v[0], v[1], v[2], rotationYaw);
        return Vec3.createVectorHelper(
            posX + mw[0] + dir.xCoord * 0.6D,
            posY + mw[1] + dir.yCoord * 0.6D,
            posZ + mw[2] + dir.zCoord * 0.6D);
    }

    /** Ground vehicles moving fast run over what stands in front of them. */
    private void ram(EntityPlayer driver) {
        if (vehicle.getMode().flies || Math.abs(speedForward) < 0.25D) return;
        AxisAlignedBB box = boundingBox.expand(0.3D, 0.0D, 0.3D);
        for (Object o : worldObj.getEntitiesWithinAABBExcludingEntity(this, box)) {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase e = (EntityLivingBase) o;
            if (isOccupant(e)) continue;
            float damage = (float) (Math.abs(speedForward) * 24.0D);
            DamageSource src = driver != null ? new EntityDamageSource("newgunrizons.vehicle", driver)
                : DamageSource.generic;
            if (e.attackEntityFrom(src, damage)) {
                double rad = Math.toRadians(rotationYaw);
                e.addVelocity(-Math.sin(rad) * speedForward * 1.5D, 0.4D, Math.cos(rad) * speedForward * 1.5D);
            }
        }
    }

    // ---------------- damage ----------------

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (worldObj.isRemote || isDead || vehicle == null || isEntityInvulnerable()) return false;
        if (isOccupant(source.getEntity()) && !source.isExplosion()) return false;
        if (source == DamageSource.fall || source == DamageSource.inWall
            || source == DamageSource.drown
            || source == DamageSource.cactus) return false;
        float dealt = amount * (1.0F - vehicle.getArmor()) * (source.isFireDamage() ? 0.2F : 1.0F);
        if (dealt <= 0.0F) return false;
        setHealth(getHealth() - dealt);
        setBeenAttacked();
        worldObj.playSoundAtEntity(this, "random.anvil_land", 0.35F, 1.6F + rand.nextFloat() * 0.3F);
        if (getHealth() <= 0.0F) wreck();
        return true;
    }

    private void wreck() {
        if (riddenByEntity != null) riddenByEntity.mountEntity(null);
        for (EntityVehicleSeat s : seatEntities) {
            if (s != null && s.riddenByEntity != null) s.riddenByEntity.mountEntity(null);
            if (s != null) s.setDead();
        }
        worldObj.createExplosion(this, posX, posY + height * 0.5D, posZ, Math.min(6.0F, 1.5F + width), false);
        // the wreck can be recovered and repaired
        charge = 0.0D;
        ItemStack stack = toStack();
        ItemVehicle.setHealth(stack, 1.0F);
        entityDropItem(stack, 0.5F);
        setDead();
    }

    // ---------------- vanilla entity plumbing ----------------

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void fall(float distance) {}

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        if (drivenLocally()) {
            double dx = x - posX, dy = y - posY, dz = z - posZ;
            if (dx * dx + dy * dy + dz * dz < 36.0D) return; // our own prediction wins over small corrections
            setPosition(x, y, z);
            return;
        }
        lerpX = x;
        lerpY = y;
        lerpZ = z;
        lerpYaw = yaw;
        lerpPitch = pitch;
        lerpSteps = increments + 2;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        if (drivenLocally()) return;
        super.setVelocity(x, y, z);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        Item item = Item.getItemById(tag.getInteger(TAG_VEHICLE));
        this.vehicle = item instanceof ItemVehicle ? (ItemVehicle) item : null;
        this.ownerName = tag.getString(TAG_OWNER);
        this.charge = tag.getDouble(ItemVehicle.TAG_CHARGE);
        this.ammo = tag.getIntArray(ItemVehicle.TAG_AMMO);
        applyStats();
        setHealth(tag.hasKey(ItemVehicle.TAG_HEALTH) ? tag.getFloat(ItemVehicle.TAG_HEALTH) : 1.0F);
        syncDirty = true;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger(TAG_VEHICLE, vehicle != null ? Item.getIdFromItem(vehicle) : -1);
        tag.setString(TAG_OWNER, ownerName);
        tag.setDouble(ItemVehicle.TAG_CHARGE, charge);
        tag.setIntArray(ItemVehicle.TAG_AMMO, ammo);
        tag.setFloat(ItemVehicle.TAG_HEALTH, getHealth());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(vehicle != null ? Item.getIdFromItem(vehicle) : -1);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        Item item = Item.getItemById(buffer.readInt());
        this.vehicle = item instanceof ItemVehicle ? (ItemVehicle) item : null;
        applyStats();
    }
}
