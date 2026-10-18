package com.gtnewhorizon.newgunrizons.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.items.ItemDrone;
import com.gtnewhorizon.newgunrizons.weapon.Hitscan;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

/**
 * A deployed combat drone. It follows its owner, engages hostile mobs (and whatever attacks or is attacked by the
 * owner) within its weapon range, and holds a stand-off position above its target.
 * <p>
 * It is a machine, not a pet: it runs on its battery (flying drains it, energy weapons draw from it), carries a
 * limited magazine, flies home and docks into the owner's inventory when the battery runs low or the magazine is
 * empty, comes down where it is if it flies out of radio range or its owner leaves, and can be shot down. It never
 * fires through its owner. Right-click it to recall it.
 */
public class EntityDrone extends EntityLiving implements IEntityAdditionalSpawnData {

    private static final String TAG_DRONE = "drone";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_CHARGE = "charge";
    private static final String TAG_ROUNDS = "rounds";
    private static final double LOW_BATTERY = 0.08D;
    private static final int OWNER_MISSING_LIMIT = 200;
    private static final int BURST_GAP = 2;

    private ItemDrone drone;
    private String ownerName = "";
    private EntityPlayer owner;
    private double charge;
    private int rounds;

    private EntityLivingBase target;
    private int retargetIn;
    private int cooldown;
    private int shotsInBurst;
    private int ownerMissing;
    private boolean returning;
    private float orbitPhase;

    /** Client-side rotor animation. */
    public float rotorAngle, prevRotorAngle;

    public EntityDrone(World world) {
        super(world);
        this.setSize(0.8F, 0.5F);
        this.experienceValue = 0;
        this.orbitPhase = this.rand.nextFloat() * (float) Math.PI * 2.0F;
    }

    public EntityDrone(World world, ItemDrone drone, EntityPlayer owner, double charge, int rounds) {
        this(world);
        this.drone = drone;
        this.owner = owner;
        this.ownerName = owner.getCommandSenderName();
        this.charge = charge;
        this.rounds = rounds;
        applyStats();
        Vec3 look = owner.getLookVec();
        this.setLocationAndAngles(
            owner.posX + look.xCoord * 1.5D,
            owner.posY + owner.getEyeHeight() + 0.2D,
            owner.posZ + look.zCoord * 1.5D,
            owner.rotationYaw,
            0.0F);
        this.motionY = 0.25D;
    }

    private void applyStats() {
        if (drone == null) return;
        float s = drone.getRenderScale();
        this.setSize(1.6F * s, 0.7F * s);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(drone.getMaxHealth());
        this.setHealth(this.getMaxHealth());
    }

    public ItemDrone getDrone() {
        return drone;
    }

    /** The owning player while they are in this world (server side), else null. */
    public EntityPlayer getOwner() {
        return owner;
    }

    public boolean isOwnedBy(Entity entity) {
        return entity instanceof EntityPlayer && ownerName.equals(entity.getCommandSenderName());
    }

    // ---------------- behaviour ----------------

    @Override
    protected boolean isAIEnabled() {
        return false;
    }

    @Override
    protected void updateEntityActionState() {
        // flight is driven by think(); no vanilla wandering
    }

    @Override
    public void onLivingUpdate() {
        if (!worldObj.isRemote) think();
        super.onLivingUpdate();
        if (worldObj.isRemote) {
            prevRotorAngle = rotorAngle;
            rotorAngle += 47.0F;
        }
    }

    private void think() {
        if (drone == null || isDead) {
            setDead();
            return;
        }
        if (owner == null || owner.isDead || owner.worldObj != worldObj) {
            owner = worldObj.getPlayerEntityByName(ownerName);
        }
        if (owner == null) {
            // owner gone: hover, then come down and wait to be picked up
            motionX *= 0.8D;
            motionZ *= 0.8D;
            motionY = Math.max(motionY - 0.02D, -0.1D);
            if (++ownerMissing > OWNER_MISSING_LIMIT) dropAsItem();
            return;
        }
        ownerMissing = 0;

        charge -= drone.getHoverDrain();
        if (charge <= 0.0D) {
            charge = 0.0D;
            tell(EnumChatFormatting.RED + "battery depleted, it came down.");
            dropAsItem();
            return;
        }
        double toOwner = getDistanceToEntity(owner);
        if (toOwner > drone.getRange() * 2.0D + 16.0D) {
            tell(EnumChatFormatting.RED + "lost signal and landed.");
            dropAsItem();
            return;
        }
        if (!returning && charge < drone.getCapacity() * LOW_BATTERY) {
            returning = true;
            tell("battery low, returning.");
        }
        if (!returning && drone.usesRounds() && rounds <= 0) {
            returning = true;
            tell("out of ammunition, returning.");
        }
        if (returning) {
            target = null;
            flyTo(owner.posX, owner.posY + owner.height + 0.4D, owner.posZ, 1.0D);
            if (toOwner < 1.8D) dock();
            return;
        }

        if (--retargetIn <= 0 || !isValidTarget(target)) {
            target = findTarget();
            retargetIn = 10;
        }
        if (target != null) {
            engage();
        } else {
            double a = ticksExisted * 0.03D + orbitPhase;
            flyTo(owner.posX + Math.cos(a) * 2.5D, owner.posY + owner.height + 1.0D, owner.posZ + Math.sin(a) * 2.5D, 0.6D);
            faceMovement();
        }
    }

    /** Holds a stand-off position above and to the side of the target, inside the owner's leash, and fires. */
    private void engage() {
        double dx = posX - target.posX, dz = posZ - target.posZ;
        double h = Math.sqrt(dx * dx + dz * dz);
        if (h < 0.1D) {
            dx = 1.0D;
            dz = 0.0D;
            h = 1.0D;
        }
        double standoff = MathHelper.clamp_double(drone.getRange() * 0.4D, 4.0D, 10.0D);
        double tx = target.posX + dx / h * standoff;
        double tz = target.posZ + dz / h * standoff;
        double ty = target.posY + target.height + 2.5D;
        // stay within weapon range of the owner
        double ox = tx - owner.posX, oz = tz - owner.posZ;
        double leash = Math.sqrt(ox * ox + oz * oz);
        if (leash > drone.getRange()) {
            tx = owner.posX + ox / leash * drone.getRange();
            tz = owner.posZ + oz / leash * drone.getRange();
        }
        flyTo(tx, ty, tz, 0.8D);
        face(target.posX - posX, target.posZ - posZ);
        fire();
    }

    private void fire() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (getDistanceToEntity(target) > drone.getRange() || !canEntityBeSeen(target)) return;
        if (drone.usesRounds() ? rounds <= 0 : charge < drone.getShotCost()) return;
        Vec3 from = Vec3.createVectorHelper(posX, posY + height * 0.3D, posZ);
        double flight = from.distanceTo(Vec3.createVectorHelper(target.posX, target.posY, target.posZ))
            / Math.max(1.0D, drone.getProjectileSpeed());
        Vec3 aim = Vec3.createVectorHelper(
            target.posX + target.motionX * flight - from.xCoord,
            target.posY + target.height * 0.6D - from.yCoord,
            target.posZ + target.motionZ * flight - from.zCoord);
        if (ownerInLineOfFire(from, aim)) return;

        if (drone.getBehavior()
            .isHitscan()) {
            Hitscan.fire(drone, this, from, aim.normalize(), drone.getDamage(), drone.getSpread());
        } else {
            EntityBullet bullet = new EntityBullet(
                drone,
                worldObj,
                this,
                drone.getProjectileSpeed(),
                0.0F,
                drone.getSpread(),
                drone.getDamage(),
                0.0F);
            bullet.setLocationAndAngles(from.xCoord, from.yCoord, from.zCoord, rotationYaw, 0.0F);
            bullet.setThrowableHeading(
                aim.xCoord,
                aim.yCoord,
                aim.zCoord,
                drone.getProjectileSpeed(),
                drone.getSpread());
            worldObj.spawnEntityInWorld(bullet);
        }
        if (drone.usesRounds()) rounds--;
        else charge -= drone.getShotCost();
        worldObj.playSoundAtEntity(this, drone.getSound(), 1.2F, 0.95F + rand.nextFloat() * 0.15F);

        if (++shotsInBurst >= drone.getBurst()) {
            shotsInBurst = 0;
            cooldown = drone.getFireInterval();
        } else {
            cooldown = BURST_GAP;
        }
    }

    /** True when the owner stands between the drone and its target. */
    private boolean ownerInLineOfFire(Vec3 from, Vec3 aim) {
        double len = aim.lengthVector();
        if (len < 1.0E-3D) return false;
        double ox = owner.posX - from.xCoord, oy = owner.posY + owner.height * 0.5D - from.yCoord,
            oz = owner.posZ - from.zCoord;
        double along = (ox * aim.xCoord + oy * aim.yCoord + oz * aim.zCoord) / len;
        if (along < 0.0D || along > len) return false;
        double px = ox - aim.xCoord / len * along, py = oy - aim.yCoord / len * along,
            pz = oz - aim.zCoord / len * along;
        return px * px + py * py + pz * pz < 1.5D * 1.5D;
    }

    private EntityLivingBase findTarget() {
        // first whatever is fighting the owner
        EntityLivingBase attacker = owner.getAITarget();
        if (isValidTarget(attacker)) return attacker;
        EntityLivingBase attacked = owner.getLastAttacker();
        if (isValidTarget(attacked) && owner.ticksExisted - owner.getLastAttackerTime() < 200) return attacked;

        float r = drone.getRange();
        List<?> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(r, r, r));
        EntityLivingBase best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Object o : list) {
            EntityLivingBase e = (EntityLivingBase) o;
            if (!(e instanceof IMob) || !isValidTarget(e)) continue;
            double d = getDistanceSqToEntity(e);
            if (d < bestDistance) {
                bestDistance = d;
                best = e;
            }
        }
        return best;
    }

    private boolean isValidTarget(EntityLivingBase e) {
        return e != null && e.isEntityAlive()
            && e != this
            && e != owner
            && !(e instanceof EntityDrone && ((EntityDrone) e).ownerName.equals(ownerName))
            && getDistanceToEntity(e) <= drone.getRange() * 1.5D
            && canEntityBeSeen(e);
    }

    private void flyTo(double x, double y, double z, double speedFactor) {
        double max = drone.getSpeed() * speedFactor;
        double vx = (x - posX) * 0.12D, vy = (y - posY) * 0.12D, vz = (z - posZ) * 0.12D;
        double v = Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (v > max) {
            vx *= max / v;
            vy *= max / v;
            vz *= max / v;
        }
        motionX += (vx - motionX) * 0.25D;
        motionY += (vy - motionY) * 0.25D;
        motionZ += (vz - motionZ) * 0.25D;
        if (isCollidedHorizontally) motionY += 0.12D; // climb over obstacles
    }

    private void face(double dx, double dz) {
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        this.rotationYaw = this.rotationYawHead = this.renderYawOffset = yaw;
    }

    private void faceMovement() {
        if (motionX * motionX + motionZ * motionZ > 0.001D) face(motionX, motionZ);
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        // flight: no gravity, air drag only
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.91D;
        this.motionY *= 0.91D;
        this.motionZ *= 0.91D;
        this.prevLimbSwingAmount = this.limbSwingAmount = 0.0F;
    }

    // ---------------- docking / dropping ----------------

    private ItemStack toStack() {
        ItemStack stack = new ItemStack(drone);
        ItemDrone.setStoredCharge(stack, charge);
        ItemDrone.setRounds(stack, rounds);
        return stack;
    }

    private void dock() {
        ItemStack stack = toStack();
        if (!owner.inventory.addItemStackToInventory(stack)) entityDropItem(stack, 0.5F);
        worldObj.playSoundAtEntity(this, "random.pop", 0.6F, 1.4F);
        setDead();
    }

    private void dropAsItem() {
        entityDropItem(toStack(), 0.3F);
        setDead();
    }

    private void tell(String message) {
        if (owner != null) {
            owner.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GRAY + "[" + new ItemStack(drone).getDisplayName() + "] " + message));
        }
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (!worldObj.isRemote && isOwnedBy(player) && drone != null) {
            this.owner = player;
            dock();
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isOwnedBy(source.getEntity())) return false; // no friendly fire from the owner
        if (source == DamageSource.fall || source == DamageSource.inWall || source == DamageSource.drown) return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!worldObj.isRemote && drone != null) {
            // shot down: the wreck can be recovered, with a dead battery
            charge = 0.0D;
            tell(EnumChatFormatting.RED + "was shot down.");
            worldObj.createExplosion(this, posX, posY, posZ, 0.0F, false);
            entityDropItem(toStack(), 0.3F);
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {}

    @Override
    protected void fall(float distance) {}

    @Override
    protected void updateFallState(double distance, boolean onGround) {}

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "random.anvil_land";
    }

    @Override
    protected String getDeathSound() {
        return "random.break";
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    // ---------------- persistence / sync ----------------

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger(TAG_DRONE, drone != null ? Item.getIdFromItem(drone) : -1);
        tag.setString(TAG_OWNER, ownerName);
        tag.setDouble(TAG_CHARGE, charge);
        tag.setInteger(TAG_ROUNDS, rounds);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        float health = tag.hasKey("HealF") ? tag.getFloat("HealF") : -1.0F;
        Item item = Item.getItemById(tag.getInteger(TAG_DRONE));
        this.drone = item instanceof ItemDrone ? (ItemDrone) item : null;
        applyStats();
        super.readEntityFromNBT(tag);
        if (health > 0.0F) setHealth(health);
        this.ownerName = tag.getString(TAG_OWNER);
        this.charge = tag.getDouble(TAG_CHARGE);
        this.rounds = tag.getInteger(TAG_ROUNDS);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(drone != null ? Item.getIdFromItem(drone) : -1);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        Item item = Item.getItemById(buffer.readInt());
        this.drone = item instanceof ItemDrone ? (ItemDrone) item : null;
        if (drone != null) {
            float s = drone.getRenderScale();
            this.setSize(1.6F * s, 0.7F * s);
        }
    }
}
