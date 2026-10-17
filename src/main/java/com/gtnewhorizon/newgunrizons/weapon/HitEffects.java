package com.gtnewhorizon.newgunrizons.weapon;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;

import gregtech.api.util.GTUtility;

/**
 * Applies a weapon hit to an entity on the server: damage split into an armor-respecting and an armor-ignoring part,
 * then fire, knockback, radiation, time slow-down or erasure as the weapon's {@link WeaponBehavior} asks.
 */
public final class HitEffects {

    /** Damage dealt by the Reality Eraser to what it may not erase (bosses and players). */
    public static final float ERASER_FALLBACK_DAMAGE = 1000.0F;

    private HitEffects() {}

    /** Damage source shown as a shot; optionally ignoring armor. */
    public static final class GunDamageSource extends EntityDamageSourceIndirect {

        public GunDamageSource(Entity direct, Entity shooter, boolean ignoresArmor) {
            super("thrown", direct, shooter);
            this.setProjectile();
            if (ignoresArmor) this.setDamageBypassesArmor();
        }
    }

    /**
     * @param direct    the projectile, or the shooter for hitscan beams
     * @param direction normalised flight direction, used for knockback
     * @return whether the target took damage (or was erased)
     */
    public static boolean hit(Entity target, Entity direct, EntityLivingBase shooter, ItemWeapon weapon, float damage,
        Vec3 direction) {
        WeaponBehavior b = weapon.getBehavior();
        if (b.isEraser()) {
            if (canErase(target)) {
                erase(target);
                return true;
            }
            damage = ERASER_FALLBACK_DAMAGE;
        }
        float ignoring = b.isEraser() ? 1.0F : b.getArmorPiercing();
        Entity source = direct != null ? direct : shooter;
        boolean hurt = false;
        float normal = damage * (1.0F - ignoring);
        float piercing = damage * ignoring;
        if (normal > 0.0F) {
            hurt = target.attackEntityFrom(new GunDamageSource(source, shooter, false), normal);
            target.hurtResistantTime = 0;
        }
        if (piercing > 0.0F) {
            hurt |= target.attackEntityFrom(new GunDamageSource(source, shooter, true), piercing);
            target.hurtResistantTime = 0;
        }
        if (target instanceof EntityLivingBase) {
            applyStatus((EntityLivingBase) target, b, direction, 1.0F);
        }
        return hurt;
    }

    /** Fire, knockback and radiation for everything caught in an explosion, scaled down with distance. */
    public static void blast(World world, double x, double y, double z, float radius, WeaponBehavior b,
        EntityLivingBase shooter) {
        if (b.getKnockback() <= 0.0F && b.getBurnSeconds() <= 0 && b.getRadiation() <= 0) return;
        double reach = radius * 1.5D;
        List<?> list = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            AxisAlignedBB.getBoundingBox(x - reach, y - reach, z - reach, x + reach, y + reach, z + reach));
        for (Object o : list) {
            EntityLivingBase e = (EntityLivingBase) o;
            if (e == shooter) continue;
            double dx = e.posX - x, dy = e.posY + e.height / 2 - y, dz = e.posZ - z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > reach) continue;
            Vec3 away = dist < 1.0E-3D ? Vec3.createVectorHelper(0, 1, 0)
                : Vec3.createVectorHelper(dx / dist, dy / dist, dz / dist);
            applyStatus(e, b, away, (float) (1.0D - dist / reach));
        }
    }

    private static void applyStatus(EntityLivingBase target, WeaponBehavior b, Vec3 direction, float strength) {
        if (b.getBurnSeconds() > 0) {
            target.setFire(Math.max(1, Math.round(b.getBurnSeconds() * strength)));
        }
        if (b.getKnockback() > 0.0F && direction != null) {
            push(target, direction, b.getKnockback() * strength);
        }
        if (b.getRadiation() > 0) {
            GTUtility.applyRadioactivity(target, b.getRadiation(), 1);
        }
        if (b.getChronoTicks() > 0) {
            freeze(target, b.getChronoTicks());
        }
    }

    /** Adds velocity along direction (with a little lift) and makes sure players receive it. */
    public static void push(Entity target, Vec3 direction, double strength) {
        target.addVelocity(
            direction.xCoord * strength,
            0.1D * strength + direction.yCoord * strength * 0.5D,
            direction.zCoord * strength);
        target.velocityChanged = true;
        if (target instanceof EntityPlayerMP) {
            ((EntityPlayerMP) target).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(target));
        }
    }

    /** Near-total slow-down of movement, attacks and mining. */
    public static void freeze(EntityLivingBase target, int ticks) {
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, ticks, 6));
        target.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, ticks, 4));
        target.addPotionEffect(new PotionEffect(Potion.weakness.id, ticks, 3));
        target.motionX *= 0.1D;
        target.motionZ *= 0.1D;
    }

    private static boolean canErase(Entity target) {
        return target instanceof EntityLivingBase && !(target instanceof EntityPlayer)
            && !(target instanceof IBossDisplayData)
            && !(target instanceof EntityDragonPart);
    }

    private static void erase(Entity target) {
        World world = target.worldObj;
        if (world instanceof WorldServer) {
            double cx = target.posX, cy = target.posY + target.height / 2.0D, cz = target.posZ;
            ((WorldServer) world).func_147487_a(
                "fireworksSpark",
                cx,
                cy,
                cz,
                60,
                target.width / 2,
                target.height / 2,
                target.width / 2,
                0.08D);
            ((WorldServer) world)
                .func_147487_a("portal", cx, cy, cz, 80, target.width / 2, target.height / 2, target.width / 2, 0.5D);
            world.playSoundEffect(cx, cy, cz, "mob.endermen.portal", 2.0F, 0.5F);
        }
        target.setDead();
    }
}
