package com.gtnewhorizon.newgunrizons.weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;
import com.gtnewhorizon.newgunrizons.network.BeamMessage;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;
import com.gtnewhorizon.newgunrizons.util.RayCast;

import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * Instant-hit beams (lasers, the Dimensional Cutter, the Reality Eraser). Traced on the server from the shooter's eye
 * along their aim; the beam stops at the first solid block unless it cuts through blocks, and at the first entity
 * unless it pierces. Every client in range is then told to draw the beam.
 */
public final class Hitscan {

    private static final double INACCURACY_SPREAD = 0.0075D;
    private static final float HITBOX_EXPANSION = 0.3F;
    private static final double BROADCAST_RANGE = 128.0D;

    private Hitscan() {}

    public static void fire(ItemWeapon weapon, EntityLivingBase shooter, float damage, float inaccuracy) {
        World world = shooter.worldObj;
        if (world.isRemote) return;
        WeaponBehavior b = weapon.getBehavior();
        Random rnd = shooter.getRNG();

        Vec3 eye = Vec3.createVectorHelper(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
        Vec3 look = shooter.getLookVec();
        look = Vec3
            .createVectorHelper(
                look.xCoord + rnd.nextGaussian() * INACCURACY_SPREAD * inaccuracy,
                look.yCoord + rnd.nextGaussian() * INACCURACY_SPREAD * inaccuracy,
                look.zCoord + rnd.nextGaussian() * INACCURACY_SPREAD * inaccuracy)
            .normalize();
        double range = b.getHitscanRange();
        Vec3 end = eye.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

        MovingObjectPosition blockHit = null;
        if (!b.isThroughBlocks()) {
            blockHit = RayCast.rayCastBlocks(
                world,
                Vec3.createVectorHelper(eye.xCoord, eye.yCoord, eye.zCoord),
                Vec3.createVectorHelper(end.xCoord, end.yCoord, end.zCoord),
                (block, meta) -> !EntityBullet.isPassThrough(block) && block.canCollideCheck(meta, false));
            if (blockHit != null) end = blockHit.hitVec;
        }

        // entities crossed by the beam, nearest first
        List<Entity> crossed = new ArrayList<>();
        Map<Entity, Vec3> where = new HashMap<>();
        AxisAlignedBB area = AxisAlignedBB
            .getBoundingBox(
                Math.min(eye.xCoord, end.xCoord),
                Math.min(eye.yCoord, end.yCoord),
                Math.min(eye.zCoord, end.zCoord),
                Math.max(eye.xCoord, end.xCoord),
                Math.max(eye.yCoord, end.yCoord),
                Math.max(eye.zCoord, end.zCoord))
            .expand(1.0D, 1.0D, 1.0D);
        for (Object o : world.getEntitiesWithinAABBExcludingEntity(shooter, area)) {
            Entity e = (Entity) o;
            if (!e.canBeCollidedWith()) continue;
            MovingObjectPosition hit = e.boundingBox.expand(HITBOX_EXPANSION, HITBOX_EXPANSION, HITBOX_EXPANSION)
                .calculateIntercept(eye, end);
            if (hit == null) continue;
            crossed.add(e);
            where.put(e, hit.hitVec);
        }
        final Vec3 from = eye;
        crossed.sort((a, c) -> Double.compare(from.distanceTo(where.get(a)), from.distanceTo(where.get(c))));

        int maxTargets = 1 + b.getPierce();
        float dmg = damage;
        int hits = 0;
        for (Entity e : crossed) {
            if (hits >= maxTargets) break;
            HitEffects.hit(e, null, shooter, weapon, dmg, look);
            blood(e, where.get(e), dmg);
            dmg *= b.getPierceFalloff();
            hits++;
            if (hits >= maxTargets && b.getPierce() == 0) {
                end = where.get(e); // a non-piercing beam stops at its target
                blockHit = null;
            }
        }

        NetworkRegistry.TargetPoint around = new NetworkRegistry.TargetPoint(
            shooter.dimension,
            shooter.posX,
            shooter.posY,
            shooter.posZ,
            BROADCAST_RANGE);
        if (blockHit != null) {
            NewGunrizonsMod.CHANNEL.sendToAllAround(
                new BlockHitMessage(blockHit.blockX, blockHit.blockY, blockHit.blockZ, blockHit.sideHit),
                around);
        }
        NewGunrizonsMod.CHANNEL.sendToAllAround(
            new BeamMessage(
                shooter.getEntityId(),
                Item.getIdFromItem(weapon),
                eye.xCoord,
                eye.yCoord,
                eye.zCoord,
                end.xCoord,
                end.yCoord,
                end.zCoord),
            around);
    }

    private static void blood(Entity target, Vec3 at, float damage) {
        if (at == null) return;
        int count = Math.max(4, Math.min(60, (int) (damage / 4.0F)));
        NewGunrizonsMod.CHANNEL.sendToAllAround(
            new SpawnParticleMessage(SpawnParticleMessage.ParticleType.BLOOD, count, at.xCoord, at.yCoord, at.zCoord),
            new NetworkRegistry.TargetPoint(target.dimension, at.xCoord, at.yCoord, at.zCoord, BROADCAST_RANGE));
    }
}
