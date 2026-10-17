package com.gtnewhorizon.newgunrizons.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.items.WeaponBehavior.FieldKind;
import com.gtnewhorizon.newgunrizons.weapon.HitEffects;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

/**
 * Short-lived field left by the UIV-UMV weapons: a gravity well that drags mobs in, a repulse blast, a singularity
 * that swallows mobs and loose items, or a time field that slows everything inside. Lives a few seconds at most and is
 * never saved with the world, so it cannot be left behind to lag a server.
 */
public class EntityGravityField extends Entity implements IEntityAdditionalSpawnData {

    private static final float MAX_RADIUS = 12.0F;
    private static final int MAX_TICKS = 200;
    private static final int DAMAGE_INTERVAL = 10;

    private FieldKind kind = FieldKind.NONE;
    private float radius = 1.0F;
    private int lifetime = 1;
    private float damagePerSecond;
    private EntityLivingBase owner;

    public EntityGravityField(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.noClip = true;
        this.ignoreFrustumCheck = true;
    }

    public EntityGravityField(World world, double x, double y, double z, FieldKind kind, float radius, int ticks,
        float damagePerSecond, EntityLivingBase owner) {
        this(world);
        this.setPosition(x, y, z);
        this.kind = kind;
        this.radius = Math.min(MAX_RADIUS, Math.max(1.0F, radius));
        this.lifetime = Math.min(MAX_TICKS, Math.max(1, ticks));
        this.damagePerSecond = damagePerSecond;
        this.owner = owner;
    }

    public FieldKind getKind() {
        return kind;
    }

    public float getRadius() {
        return radius;
    }

    public int getLifetime() {
        return lifetime;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        this.lastTickPosX = this.prevPosX = this.posX;
        this.lastTickPosY = this.prevPosY = this.posY;
        this.lastTickPosZ = this.prevPosZ = this.posZ;
        ++this.ticksExisted;
        if (this.ticksExisted > this.lifetime) {
            if (!worldObj.isRemote) collapse();
            this.setDead();
            return;
        }
        if (worldObj.isRemote) {
            clientParticles();
            return;
        }
        switch (kind) {
            case GRAVITY_WELL:
                pull(0.12D, false);
                damageCore(2.0D);
                break;
            case REPULSE:
                if (this.ticksExisted == 1) repulse();
                break;
            case SINGULARITY:
                pull(0.16D, true);
                damageCore(radius / 2.0D);
                break;
            case CHRONO:
                slowTime();
                break;
            default:
                this.setDead();
                break;
        }
    }

    private List<?> around(Class<?> type, double reach) {
        return worldObj.getEntitiesWithinAABB(
            type,
            AxisAlignedBB
                .getBoundingBox(posX - reach, posY - reach, posZ - reach, posX + reach, posY + reach, posZ + reach));
    }

    private boolean affects(Entity e) {
        return e != owner && e != this && !e.isDead;
    }

    /** Accelerates entities towards the centre, harder the closer they get. */
    private void pull(double strength, boolean items) {
        for (Object o : around(Entity.class, radius)) {
            Entity e = (Entity) o;
            boolean living = e instanceof EntityLivingBase;
            boolean loose = items && (e instanceof EntityItem || e instanceof EntityXPOrb);
            if (!affects(e) || !(living || loose)) continue;
            double dx = posX - e.posX, dy = posY - (e.posY + e.height / 2.0D), dz = posZ - e.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > radius || dist < 0.3D) continue;
            double k = strength * (loose ? 1.6D : 1.0D) * (0.5D + 0.5D * (1.0D - dist / radius)) / dist;
            e.motionX += dx * k;
            e.motionY += dy * k + 0.02D;
            e.motionZ += dz * k;
            e.fallDistance = 0.0F;
            e.velocityChanged = true;
            if (living && kind == FieldKind.SINGULARITY) {
                ((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 2));
            }
        }
    }

    /** Damage over time to everything close to the centre. */
    private void damageCore(double reach) {
        if (damagePerSecond <= 0.0F || this.ticksExisted % DAMAGE_INTERVAL != 0) return;
        float amount = damagePerSecond * DAMAGE_INTERVAL / 20.0F;
        for (Object o : around(EntityLivingBase.class, reach)) {
            EntityLivingBase e = (EntityLivingBase) o;
            if (!affects(e)) continue;
            e.attackEntityFrom(new HitEffects.GunDamageSource(this, owner, true), amount);
            e.hurtResistantTime = 0;
        }
    }

    private void repulse() {
        for (Object o : around(Entity.class, radius)) {
            Entity e = (Entity) o;
            if (!affects(e) || !(e instanceof EntityLivingBase || e instanceof EntityItem)) continue;
            double dx = e.posX - posX, dy = e.posY + e.height / 2.0D - posY, dz = e.posZ - posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > radius) continue;
            Vec3 away = dist < 1.0E-3D ? Vec3.createVectorHelper(0, 1, 0)
                : Vec3.createVectorHelper(dx / dist, dy / dist, dz / dist);
            HitEffects.push(e, away, 0.8D + 2.2D * (1.0D - dist / radius));
        }
        worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 1.5F, 1.6F);
    }

    /** Freezes living things and drags projectiles to a crawl inside the field. */
    private void slowTime() {
        for (Object o : around(Entity.class, radius)) {
            Entity e = (Entity) o;
            if (!affects(e)) continue;
            double dx = e.posX - posX, dy = e.posY - posY, dz = e.posZ - posZ;
            if (dx * dx + dy * dy + dz * dz > radius * radius) continue;
            if (e instanceof EntityLivingBase) {
                if (this.ticksExisted % 10 == 1) HitEffects.freeze((EntityLivingBase) e, 30);
                e.motionX *= 0.1D;
                e.motionZ *= 0.1D;
                e.velocityChanged = true;
            } else if (e instanceof IProjectile && !(e instanceof EntityBullet)) {
                e.motionX *= 0.6D;
                e.motionY *= 0.6D;
                e.motionZ *= 0.6D;
                e.velocityChanged = true;
            }
        }
    }

    /** End of a gravity well: what is still caught in the centre is crushed. */
    private void collapse() {
        if (kind == FieldKind.GRAVITY_WELL && damagePerSecond > 0.0F) {
            for (Object o : around(EntityLivingBase.class, 3.0D)) {
                EntityLivingBase e = (EntityLivingBase) o;
                if (!affects(e)) continue;
                e.attackEntityFrom(new HitEffects.GunDamageSource(this, owner, true), damagePerSecond * 2.0F);
                e.hurtResistantTime = 0;
            }
            worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 1.0F, 0.6F);
        }
    }

    private void clientParticles() {
        if (rand.nextInt(2) != 0) return;
        double a = rand.nextDouble() * Math.PI * 2, b = rand.nextDouble() * Math.PI - Math.PI / 2;
        double r = radius * (0.6D + rand.nextDouble() * 0.4D);
        double x = posX + Math.cos(a) * Math.cos(b) * r, y = posY + Math.sin(b) * r, z = posZ + Math.sin(a) * Math.cos(b) * r;
        switch (kind) {
            case GRAVITY_WELL:
            case SINGULARITY:
                // portal particles drift towards the point they are given relative to
                worldObj.spawnParticle("portal", posX, posY, posZ, x - posX, y - posY, z - posZ);
                break;
            case CHRONO:
                worldObj.spawnParticle("happyVillager", x, y, z, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double distanceSq) {
        return distanceSq < 128.0D * 128.0D;
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.setDead(); // never persists across a reload
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return false;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeByte(kind.ordinal());
        buffer.writeFloat(radius);
        buffer.writeInt(lifetime);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        int k = buffer.readByte();
        this.kind = k >= 0 && k < FieldKind.values().length ? FieldKind.values()[k] : FieldKind.NONE;
        this.radius = buffer.readFloat();
        this.lifetime = buffer.readInt();
    }
}
