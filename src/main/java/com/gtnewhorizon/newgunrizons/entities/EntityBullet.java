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
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;

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

    @Getter
    private ItemWeapon weapon;
    private float damage = DEFAULT_DAMAGE;
    private float explosionRadius;

    // client-side render bookkeeping
    /** Where the projectile entered the world (the shooter's eye line). */
    public double spawnX, spawnY, spawnZ;
    /** Where its visual effect starts: the shooter's muzzle, set on first render. */
    public double originX, originY, originZ;
    public boolean hasOrigin;

    public EntityBullet(World world) {
        super(world);
    }

    public EntityBullet(ItemWeapon weapon, World world, EntityLivingBase thrower, float speed, float gravityVelocity,
        float inaccuracy, float damage, float explosionRadius) {
        super(world, thrower, speed, gravityVelocity, inaccuracy);
        this.weapon = weapon;
        this.damage = damage;
        this.explosionRadius = explosionRadius;
    }

    public void onImpact(MovingObjectPosition position) {
        if (worldObj.isRemote || weapon == null) return;

        if (explosionRadius > 0.0F) {
            handleExplosiveImpact(position);
        } else if (position.entityHit != null) {
            handleEntityHit(position.entityHit);
        } else {
            spawnBlockHitParticles(position);
        }

        this.setDead();
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
        DamageSource source = getThrower() == null ? new DamageSource("generic")
            : DamageSource.causeThrownDamage(this, getThrower());
        target.attackEntityFrom(source, damage);
        target.hurtResistantTime = 0;
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
        if (weapon == null) return;
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
        buffer.writeInt(Item.getIdFromItem(this.weapon));
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
        this.weapon = (ItemWeapon) Item.getItemById(buffer.readInt());
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
        if (item instanceof ItemWeapon) {
            this.weapon = (ItemWeapon) item;
        }
        this.damage = tagCompound.getFloat(TAG_DAMAGE);
        this.explosionRadius = tagCompound.getFloat(TAG_EXPLOSION_RADIUS);
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TAG_ENTITY_ITEM, Item.getIdFromItem(this.weapon));
        tagCompound.setFloat(TAG_DAMAGE, this.damage);
        tagCompound.setFloat(TAG_EXPLOSION_RADIUS, this.explosionRadius);
    }

    public boolean canCollideWithBlock(Block block, int metadata) {
        return !PASS_THROUGH_BLOCKS.contains(block) && super.canCollideWithBlock(block, metadata);
    }
}
