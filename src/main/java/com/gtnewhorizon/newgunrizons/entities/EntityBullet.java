package com.gtnewhorizon.newgunrizons.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ShotProfile;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior.FieldKind;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;
import com.gtnewhorizon.newgunrizons.weapon.HitEffects;

import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

public class EntityBullet extends EntityProjectile {

    private static final String TAG_ENTITY_ITEM = "entityItem";
    private static final String TAG_DAMAGE = "damage";
    private static final String TAG_EXPLOSION_RADIUS = "explosionRadius";

    private static final float DEFAULT_DAMAGE = 6.0F;
    private static final float BLEEDING_COEFFICIENT = 1.0F;
    private static final double PARTICLE_BROADCAST_RANGE = 100.0;
    private static final double PHASE_SAMPLE = 0.25D;
    private static final int MAX_PHASE_TICKS = 200;

    private static final Set<Block> PASS_THROUGH_BLOCKS = Collections.unmodifiableSet(
        new HashSet<>(
            Arrays.asList(
                Blocks.grass,
                Blocks.air,
                Blocks.tallgrass,
                Blocks.leaves,
                Blocks.leaves2,
                Blocks.fire,
                Blocks.hay_block,
                Blocks.double_plant,
                Blocks.web,
                Blocks.wheat)));

    /** Weapon or drone that fired this; drives its behaviour and look. */
    @Getter
    private ShotProfile source;
    private float damage = DEFAULT_DAMAGE;
    private float explosionRadius;

    // server-side piercing / field bookkeeping
    private final Set<Integer> hitEntities = new HashSet<>();
    private int pierced;
    private boolean altMode;
    private boolean fieldSpawned;
    /** Distance flown inside solid blocks by a phasing projectile (both sides). */
    private double phasedDistance;

    // client-side render bookkeeping
    /** Where the projectile entered the world (the shooter's eye line). */
    public double spawnX, spawnY, spawnZ;
    /** Where its visual effect starts: the shooter's muzzle, set on first render. */
    public double originX, originY, originZ;
    public boolean hasOrigin;

    public EntityBullet(World world) {
        super(world);
    }

    public EntityBullet(ShotProfile source, World world, EntityLivingBase thrower, float speed, float gravityVelocity,
        float inaccuracy, float damage, float explosionRadius) {
        super(world, thrower, speed, gravityVelocity, inaccuracy);
        this.source = source;
        this.damage = damage;
        this.explosionRadius = explosionRadius;
    }

    public void setAltMode(boolean altMode) {
        this.altMode = altMode;
    }

    private WeaponBehavior behavior() {
        return source != null ? source.getBehavior() : WeaponBehavior.NONE;
    }

    @Override
    public void onUpdate() {
        // Phasing projectiles ignore blocks, but only for so many solid blocks along their path.
        int phase = behavior().getPhaseBlocks();
        if (phase > 0 && !this.isDead && this.ticksExisted <= MAX_PHASE_TICKS) {
            double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            int steps = Math.max(1, (int) Math.ceil(speed / PHASE_SAMPLE));
            for (int i = 1; i <= steps; i++) {
                double f = (double) i / steps;
                double px = posX + motionX * f, py = posY + motionY * f, pz = posZ + motionZ * f;
                Block block = worldObj
                    .getBlock(MathHelper.floor_double(px), MathHelper.floor_double(py), MathHelper.floor_double(pz));
                if (block.getMaterial()
                    .blocksMovement()) {
                    phasedDistance += speed / steps;
                    if (phasedDistance > phase) {
                        setPosition(px, py, pz);
                        if (!worldObj.isRemote)
                            spawnField(px - motionX / speed, py - motionY / speed, pz - motionZ / speed);
                        setDead();
                        return;
                    }
                }
            }
        }
        super.onUpdate();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (hitEntities.contains(entity.getEntityId())) return false;
        // never the drone's owner, nor a drone of whoever fired
        EntityLivingBase shooter = getThrower();
        if (shooter instanceof EntityDrone && entity == ((EntityDrone) shooter).getOwner()) return false;
        if (entity instanceof EntityDrone && ((EntityDrone) entity).isOwnedBy(shooter)) return false;
        return true;
    }

    public void onImpact(MovingObjectPosition position) {
        if (worldObj.isRemote) {
            // visual copy: stop where it hits instead of flying through walls until the server removes it
            this.setDead();
            return;
        }
        if (source == null) {
            this.setDead();
            return;
        }
        WeaponBehavior b = behavior();

        if (explosionRadius > 0.0F) {
            handleExplosiveImpact(position);
            HitEffects.blast(
                worldObj,
                position.hitVec.xCoord,
                position.hitVec.yCoord,
                position.hitVec.zCoord,
                explosionRadius,
                b,
                getThrower());
            spawnField(position.hitVec.xCoord, position.hitVec.yCoord, position.hitVec.zCoord);
            this.setDead();
            return;
        }

        if (position.entityHit != null) {
            Entity target = position.entityHit;
            handleEntityHit(target);
            hitEntities.add(target.getEntityId());
            if (pierced < b.getPierce()) {
                // keep flying, weaker for every target it has gone through
                pierced++;
                damage *= b.getPierceFalloff();
                return;
            }
            spawnField(target.posX, target.posY + target.height / 2.0D, target.posZ);
            this.setDead();
            return;
        }

        spawnBlockHitParticles(position);
        double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) + 1.0E-6D;
        spawnField(
            position.hitVec.xCoord - motionX / speed * 0.5D,
            position.hitVec.yCoord - motionY / speed * 0.5D,
            position.hitVec.zCoord - motionZ / speed * 0.5D);
        this.setDead();
    }

    /** Leaves the weapon's gravity/time field (if any) at the impact point. */
    private void spawnField(double x, double y, double z) {
        WeaponBehavior b = behavior();
        FieldKind kind = b.getField(altMode);
        if (kind == FieldKind.NONE || fieldSpawned) return;
        fieldSpawned = true;
        worldObj.spawnEntityInWorld(
            new EntityGravityField(
                worldObj,
                x,
                y,
                z,
                kind,
                b.getFieldRadius(altMode),
                b.getFieldTicks(altMode),
                b.getFieldDamage(altMode),
                getThrower()));
    }

    private void handleExplosiveImpact(MovingObjectPosition position) {
        Explosion.createServerSideExplosion(
            worldObj,
            this,
            position.hitVec.xCoord,
            position.hitVec.yCoord,
            position.hitVec.zCoord,
            explosionRadius,
            false,
            true);
    }

    private void handleEntityHit(Entity target) {
        double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) + 1.0E-6D;
        Vec3 dir = Vec3.createVectorHelper(motionX / speed, motionY / speed, motionZ / speed);
        HitEffects.hit(target, this, getThrower(), source, damage, dir);
        target.prevRotationYaw -= 0.3F;

        spawnBloodParticles(target);
    }

    private void spawnBlockHitParticles(MovingObjectPosition position) {
        NetworkRegistry.TargetPoint broadcastPoint = new NetworkRegistry.TargetPoint(
            dimension,
            posX,
            posY,
            posZ,
            PARTICLE_BROADCAST_RANGE);
        NewGunrizonsMod.CHANNEL.sendToAllAround(
            new BlockHitMessage(position.blockX, position.blockY, position.blockZ, position.sideHit),
            broadcastPoint);
    }

    @Override
    protected void onWaterImpact(double x, double y, double z) {
        if (source == null) return;
        NetworkRegistry.TargetPoint broadcastPoint = new NetworkRegistry.TargetPoint(
            dimension,
            x,
            y,
            z,
            PARTICLE_BROADCAST_RANGE);
        NewGunrizonsMod.CHANNEL.sendToAllAround(
            new SpawnParticleMessage(SpawnParticleMessage.ParticleType.WATER_SPLASH, 8, x, y, z),
            broadcastPoint);
    }

    private void spawnBloodParticles(Entity target) {
        double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) + 1.0;
        double hitX = target.posX - motionX / speed;
        double hitY = target.posY - motionY / speed;
        double hitZ = target.posZ - motionZ / speed;
        int count = (int) (getParticleCount() * BLEEDING_COEFFICIENT);

        NetworkRegistry.TargetPoint broadcastPoint = new NetworkRegistry.TargetPoint(
            target.dimension,
            posX,
            posY,
            posZ,
            PARTICLE_BROADCAST_RANGE);
        NewGunrizonsMod.CHANNEL.sendToAllAround(
            new SpawnParticleMessage(SpawnParticleMessage.ParticleType.BLOOD, count, hitX, hitY, hitZ),
            broadcastPoint);
    }

    private int getParticleCount() {
        float x = damage - 30.0F;
        return (int) (-0.11 * x * x + 100.0);
    }

    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.source instanceof Item ? Item.getIdFromItem((Item) this.source) : -1);
        buffer.writeFloat(this.damage);
        buffer.writeFloat(this.explosionRadius);
        // Exact velocity: the vanilla spawn packet clamps each component to +-3.9 blocks/tick, which bends the
        // client-side flight path of fast projectiles away from the real one.
        buffer.writeDouble(this.motionX);
        buffer.writeDouble(this.motionY);
        buffer.writeDouble(this.motionZ);
        buffer.writeInt(this.thrower != null ? this.thrower.getEntityId() : -1);
    }

    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        Item sourceItem = Item.getItemById(buffer.readInt());
        this.source = sourceItem instanceof ShotProfile ? (ShotProfile) sourceItem : null;
        this.damage = buffer.readFloat();
        this.explosionRadius = buffer.readFloat();
        this.motionX = buffer.readDouble();
        this.motionY = buffer.readDouble();
        this.motionZ = buffer.readDouble();
        int throwerId = buffer.readInt();
        if (throwerId >= 0 && this.worldObj != null) {
            Entity shooter = this.worldObj.getEntityByID(throwerId);
            if (shooter instanceof EntityLivingBase) {
                this.thrower = (EntityLivingBase) shooter;
            }
        }
        this.spawnX = this.posX;
        this.spawnY = this.posY;
        this.spawnZ = this.posZ;
    }

    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        Item item = Item.getItemById(tagCompound.getInteger(TAG_ENTITY_ITEM));
        if (item instanceof ShotProfile) {
            this.source = (ShotProfile) item;
        }
        this.damage = tagCompound.getFloat(TAG_DAMAGE);
        this.explosionRadius = tagCompound.getFloat(TAG_EXPLOSION_RADIUS);
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TAG_ENTITY_ITEM, this.source instanceof Item ? Item.getIdFromItem((Item) this.source) : -1);
        tagCompound.setFloat(TAG_DAMAGE, this.damage);
        tagCompound.setFloat(TAG_EXPLOSION_RADIUS, this.explosionRadius);
    }

    public boolean canCollideWithBlock(Block block, int metadata) {
        if (behavior().getPhaseBlocks() > 0) return false;
        return !PASS_THROUGH_BLOCKS.contains(block) && super.canCollideWithBlock(block, metadata);
    }

    /** Blocks every shot passes through (foliage, grass, webs...). */
    public static boolean isPassThrough(Block block) {
        return PASS_THROUGH_BLOCKS.contains(block);
    }
}
