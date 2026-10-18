package com.gtnewhorizon.newgunrizons.client.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.items.ShotProfile;
import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;

/**
 * Draws projectiles of weapons that declare a {@link ProjectileStyle}. Classic firearms keep invisible bullets.
 * <p>
 * Everything is drawn untextured with additive blending in camera-relative space (the camera is at the origin), using
 * immediate-mode GL and with the active shader program unbound, like the laser sight renderer, so Iris/Angelica
 * shader packs are left intact.
 */
public class EntityBulletRenderer extends Render {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(
        NewGunrizonsMod.MODID,
        "textures/effect/bullet44.png");
    private static final float WHITE = FxDraw.WHITE;
    private static final double MAX_BEAM = 48.0D;

    /** Distance flown since the muzzle for the projectile being drawn; tails are clamped to it. */
    private static double travelled = Double.MAX_VALUE;
    /** Camera-relative position of the muzzle for the projectile being drawn. */
    private static double[] originRel = { 0, 0, 0 };

    // muzzle position relative to the projectile's spawn point (already 0.16 right / 0.1 below the eye):
    // right, up and forward offsets for the shooter's own first-person view and for everyone else
    private static final double[] MUZZLE_FIRST_PERSON = { 0.10D, -0.14D, 0.55D };
    private static final double[] MUZZLE_THIRD_PERSON = { 0.22D, -0.38D, 0.60D };

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityBullet)) return;
        EntityBullet bullet = (EntityBullet) entity;
        ShotProfile weapon = bullet.getSource();
        if (weapon == null || weapon.getProjectileStyle() == null
            || weapon.getProjectileStyle() == ProjectileStyle.NONE) {
            return;
        }
        if (!bullet.hasOrigin) initOrigin(bullet);

        double[] pos = { x, y, z };
        double[] dir = FxDraw.norm(new double[] { bullet.motionX, bullet.motionY, bullet.motionZ });
        if (dir == null) dir = FxDraw.norm(
            new double[] { bullet.posX - bullet.prevPosX, bullet.posY - bullet.prevPosY,
                bullet.posZ - bullet.prevPosZ });
        if (dir == null) return;

        int rgb = weapon.getProjectileColor();
        float r = ((rgb >> 16) & 255) / 255.0F, g = ((rgb >> 8) & 255) / 255.0F, b = (rgb & 255) / 255.0F;
        float scale = weapon.getProjectileScale();
        double t = bullet.ticksExisted + partialTicks;
        originRel = new double[] { x + (bullet.originX - bullet.posX), y + (bullet.originY - bullet.posY),
            z + (bullet.originZ - bullet.posZ) };
        travelled = Math.sqrt(
            (bullet.posX - bullet.originX) * (bullet.posX - bullet.originX)
                + (bullet.posY - bullet.originY) * (bullet.posY - bullet.originY)
                + (bullet.posZ - bullet.originZ) * (bullet.posZ - bullet.originZ));

        FxDraw.begin();
        try {
            switch (weapon.getProjectileStyle()) {
                case TRACER:
                    tracer(pos, dir, r, g, b, scale);
                    break;
                case RAIL:
                    rail(pos, dir, r, g, b, scale, t);
                    break;
                case LASER:
                    laser(bullet, pos, dir, x, y, z, r, g, b, scale, t);
                    break;
                case PLASMA:
                    plasma(pos, dir, r, g, b, scale, t);
                    break;
                case HELIX:
                    helix(pos, dir, r, g, b, scale, t);
                    break;
                case PULSE:
                    pulse(pos, dir, r, g, b, scale, t);
                    break;
                case ORB_RING:
                    orbRing(pos, dir, r, g, b, scale, t);
                    break;
                case SHARDS:
                    shards(bullet, pos, dir, r, g, b, scale, t);
                    break;
                case PHASE:
                    phase(pos, dir, r, g, b, scale, t);
                    break;
                case GRAVITON:
                    graviton(pos, dir, r, g, b, scale, t);
                    break;
                case CHRONO:
                    chrono(pos, dir, r, g, b, scale, t);
                    break;
                case SINGULARITY:
                    singularity(pos, dir, r, g, b, scale, t);
                    break;
                default:
                    break;
            }
        } finally {
            FxDraw.end();
        }
    }

    // ---------------- styles ----------------

    private static void tracer(double[] p, double[] d, float r, float g, float b, float s) {
        double[] tail = tailStart(p, d, 2.5D * s);
        FxDraw.ribbon(tail, p, 0.10D * s, r, g, b, 0.0F, 0.55F);
        FxDraw.ribbon(tail, p, 0.03D * s, WHITE, WHITE, WHITE, 0.0F, 0.9F);
    }

    private static void rail(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double[] tail = tailStart(p, d, 6.0D * s);
        double len = FxDraw.length(FxDraw.sub(p, tail));
        FxDraw.ribbon(tail, p, 0.22D * s, r, g, b, 0.0F, 0.45F);
        FxDraw.ribbon(tail, p, 0.06D * s, WHITE, WHITE, WHITE, 0.1F, 1.0F);
        // fading coil rings left behind along the path
        for (int i = 1; i <= 5; i++) {
            double k = i * len / 6.0D;
            float a = 0.7F * (1.0F - i / 6.0F);
            FxDraw.ring(FxDraw.add(p, FxDraw.mul(d, -k)), d, (0.14D + 0.03D * i) * s, r, g, b, a, 2.0F);
        }
        FxDraw.glow(p, 0.22D * s, r, g, b, 0.8F);
    }

    private static void laser(EntityBullet bullet, double[] p, double[] d, double x, double y, double z, float r,
        float g, float b, float s, double t) {
        // the muzzle end is where the bolt was first seen, in camera-relative space
        double[] start = originRel;
        double[] span = FxDraw.sub(p, start);
        double len = FxDraw.length(span);
        if (len > MAX_BEAM) start = FxDraw.add(p, FxDraw.mul(d, -MAX_BEAM));
        float flicker = 0.75F + 0.25F * (float) Math.sin(t * 2.7D);
        FxDraw.ribbon(start, p, 0.14D * s, r, g, b, 0.35F * flicker, 0.45F * flicker);
        FxDraw.ribbon(start, p, 0.035D * s, WHITE, WHITE, WHITE, 0.8F, 1.0F);
        FxDraw.glow(p, 0.18D * s, r, g, b, 0.9F);
    }

    private static void plasma(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double radius = (0.22D + 0.04D * Math.sin(t * 1.3D)) * s;
        FxDraw.ribbon(tailStart(p, d, 1.6D * s), p, radius * 1.3D, r, g, b, 0.0F, 0.5F);
        FxDraw.glow(p, radius * 2.2D, r, g, b, 0.45F);
        FxDraw.glow(p, radius, WHITE, WHITE, WHITE, 0.9F);
    }

    private static void helix(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double len = tail(4.0D * s);
        if (len < 0.05D) return;
        FxDraw.ribbon(FxDraw.add(p, FxDraw.mul(d, -len)), p, 0.05D * s, WHITE, WHITE, WHITE, 0.0F, 1.0F);
        double[][] basis = FxDraw.basis(d);
        double radius = 0.16D * s;
        for (int strand = 0; strand < 2; strand++) {
            GL11.glLineWidth(2.5F);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int i = 0; i <= 48; i++) {
                double k = len * i / 48.0D;
                double a = k * 5.0D + t * 0.9D + strand * Math.PI;
                double[] q = FxDraw.add(
                    FxDraw.add(p, FxDraw.mul(d, -k)),
                    FxDraw.add(FxDraw.mul(basis[0], Math.cos(a) * radius), FxDraw.mul(basis[1], Math.sin(a) * radius)));
                GL11.glColor4f(r, g, b, (float) (0.95D * (1.0D - k / len)));
                GL11.glVertex3d(q[0], q[1], q[2]);
            }
            GL11.glEnd();
        }
        FxDraw.glow(p, 0.2D * s, r, g, b, 0.85F);
    }

    private static void pulse(double[] p, double[] d, float r, float g, float b, float s, double t) {
        FxDraw.ribbon(tailStart(p, d, 2.0D * s), p, 0.12D * s, r, g, b, 0.0F, 0.5F);
        FxDraw.glow(p, 0.26D * s, WHITE, WHITE, WHITE, 0.8F);
        FxDraw.glow(p, 0.5D * s, r, g, b, 0.4F);
        // three shock rings expanding out of the pulse as it travels
        for (int i = 0; i < 3; i++) {
            double phase = ((t * 0.12D) + i / 3.0D) % 1.0D;
            FxDraw.ring(
                FxDraw.add(p, FxDraw.mul(d, -Math.min(phase * 1.5D * s, travelled))),
                d,
                (0.15D + phase * 0.6D) * s,
                r,
                g,
                b,
                (float) (0.8D * (1 - phase)),
                2.0F);
        }
    }

    private static void orbRing(double[] p, double[] d, float r, float g, float b, float s, double t) {
        FxDraw.ribbon(tailStart(p, d, 2.5D * s), p, 0.3D * s, r, g, b, 0.0F, 0.35F);
        FxDraw.glow(p, 0.55D * s, r, g, b, 0.5F);
        FxDraw.glow(p, 0.22D * s, WHITE, WHITE, WHITE, 0.9F);
        double[][] basis = FxDraw.basis(d);
        // two containment rings spinning on different axes
        double a1 = t * 0.35D, a2 = -t * 0.27D;
        double[] axis1 = FxDraw.norm(FxDraw.add(FxDraw.mul(d, Math.cos(a1)), FxDraw.mul(basis[0], Math.sin(a1))));
        double[] axis2 = FxDraw
            .norm(FxDraw.add(FxDraw.mul(basis[0], Math.cos(a2)), FxDraw.mul(basis[1], Math.sin(a2))));
        FxDraw.ring(p, axis1, 0.6D * s, r, g, b, 0.9F, 3.0F);
        FxDraw.ring(p, axis2, 0.48D * s, WHITE, g * 0.6F + 0.4F, b * 0.6F + 0.4F, 0.8F, 2.0F);
    }

    private static void shards(EntityBullet bullet, double[] p, double[] d, float r, float g, float b, float s,
        double t) {
        Random rnd = new Random(bullet.getEntityId() * 31L + (long) (t * 2.0D));
        double[][] basis = FxDraw.basis(d);
        FxDraw.ribbon(tailStart(p, d, 2.0D * s), p, 0.04D * s, WHITE, WHITE, WHITE, 0.0F, 0.9F);
        for (int i = 0; i < 9; i++) {
            double back = rnd.nextDouble() * tail(2.5D * s);
            double off = 0.1D + rnd.nextDouble() * 0.35D * s;
            double ang = rnd.nextDouble() * Math.PI * 2;
            double[] c = FxDraw.add(
                FxDraw.add(p, FxDraw.mul(d, -back)),
                FxDraw.add(FxDraw.mul(basis[0], Math.cos(ang) * off), FxDraw.mul(basis[1], Math.sin(ang) * off)));
            double[] tip = FxDraw.add(c, FxDraw.mul(d, -(0.2D + rnd.nextDouble() * 0.4D) * s));
            boolean purple = (i & 1) == 0;
            FxDraw.ribbon(c, tip, 0.06D * s, purple ? 0.65F : r, purple ? 0.25F : g, purple ? 1.0F : b, 0.9F, 0.2F);
        }
        FxDraw.glow(p, 0.3D * s, r, g, b, 0.7F);
    }

    private static void phase(double[] p, double[] d, float r, float g, float b, float s, double t) {
        float flicker = Math.sin(t * 5.0D) > 0 ? 1.0F : 0.55F;
        double[] tail = tailStart(p, d, 3.0D * s);
        FxDraw.ribbon(tail, p, 0.12D * s, r, g, b, 0.0F, 0.6F * flicker);
        FxDraw.ribbon(tail, p, 0.04D * s, WHITE, WHITE, WHITE, 0.0F, flicker);
        // out-of-phase ghosts drifting around the bolt
        double[][] basis = FxDraw.basis(d);
        for (int i = 0; i < 3; i++) {
            double a = t * 0.7D + i * Math.PI * 2 / 3.0D;
            double off = (0.18D + 0.08D * Math.sin(t * 1.9D + i)) * s;
            double[] o = FxDraw.add(FxDraw.mul(basis[0], Math.cos(a) * off), FxDraw.mul(basis[1], Math.sin(a) * off));
            double[] gp = FxDraw.add(FxDraw.add(p, FxDraw.mul(d, -0.3D * s * (i + 1))), o);
            FxDraw.ribbon(FxDraw.add(gp, FxDraw.mul(d, -tail(1.2D * s))), gp, 0.06D * s, r, g, b, 0.0F, 0.35F);
        }
        FxDraw.glow(p, 0.28D * s, r, g, b, 0.8F * flicker);
    }

    private static void graviton(double[] p, double[] d, float r, float g, float b, float s, double t) {
        FxDraw.ribbon(tailStart(p, d, 2.0D * s), p, 0.08D * s, r, g, b, 0.0F, 0.5F);
        FxDraw.glow(p, 0.6D * s, r, g, b, 0.45F);
        FxDraw.translucent();
        FxDraw.disc(p, 0.2D * s, 0.0F, 0.0F, 0.0F, 1.0F, 0.85F);
        FxDraw.additive();
        double[] view = FxDraw.norm(p);
        if (view == null) return;
        // rings falling into the core
        for (int i = 0; i < 3; i++) {
            double phase = ((t * 0.09D) + i / 3.0D) % 1.0D;
            FxDraw.ring(p, view, (0.25D + (1.0D - phase) * 0.55D) * s, r, g, b, (float) (0.9D * phase), 2.0F);
        }
    }

    private static void chrono(double[] p, double[] d, float r, float g, float b, float s, double t) {
        FxDraw.ribbon(tailStart(p, d, 2.5D * s), p, 0.1D * s, r, g, b, 0.0F, 0.55F);
        FxDraw.ribbon(tailStart(p, d, 2.5D * s), p, 0.03D * s, WHITE, WHITE, WHITE, 0.0F, 0.9F);
        FxDraw.glow(p, 0.3D * s, r, g, b, 0.7F);
        FxDraw.clock(p, 0.32D * s, t * 6.0D, r, g, b, 0.9F);
    }

    private static void singularity(double[] p, double[] d, float r, float g, float b, float s, double t) {
        FxDraw.ribbon(tailStart(p, d, 2.0D * s), p, 0.2D * s, r, g, b, 0.0F, 0.3F);
        FxDraw.glow(p, 0.7D * s, r, g, b, 0.4F);
        double[][] basis = FxDraw.basis(d);
        double[] axis = FxDraw.norm(FxDraw.add(d, FxDraw.mul(basis[1], 0.8D)));
        FxDraw.annulus(
            p,
            axis,
            0.24D * s,
            0.55D * s,
            t * 0.3D,
            new float[] { WHITE, WHITE, WHITE, 0.9F },
            new float[] { r, g, b, 0.0F },
            32,
            true);
        FxDraw.translucent();
        FxDraw.disc(p, 0.2D * s, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        FxDraw.additive();
        FxDraw.ring(p, FxDraw.norm(p), 0.21D * s, WHITE, WHITE, WHITE, 0.8F, 1.5F);
    }

    /** Tail length clamped so it never extends behind the muzzle (or through the shooter's view). */
    private static double tail(double wanted) {
        return Math.min(wanted, travelled);
    }

    /**
     * Back end of a tail of the wanted length behind p; once the projectile is closer to the muzzle than that, the
     * tail runs straight back to the muzzle so the effect visibly leaves the barrel.
     */
    private static double[] tailStart(double[] p, double[] d, double wanted) {
        return wanted < travelled ? FxDraw.add(p, FxDraw.mul(d, -wanted)) : originRel;
    }

    /** Puts the effect's start point at the shooter's muzzle instead of the eye line the projectile flies along. */
    private static void initOrigin(EntityBullet bullet) {
        double sx = bullet.spawnX, sy = bullet.spawnY, sz = bullet.spawnZ;
        if (sx == 0 && sy == 0 && sz == 0) {
            sx = bullet.posX;
            sy = bullet.posY;
            sz = bullet.posZ;
        }
        double[] origin = { sx, sy, sz };
        double[] dir = FxDraw.norm(new double[] { bullet.motionX, bullet.motionY, bullet.motionZ });
        if (dir != null) {
            double[] right = FxDraw.norm(FxDraw.cross(dir, new double[] { 0, 1, 0 }));
            if (right != null && bullet.getThrower() instanceof EntityPlayer) {
                double[] up = FxDraw.cross(right, dir);
                Minecraft mc = Minecraft.getMinecraft();
                boolean firstPerson = bullet.getThrower() != null && bullet.getThrower() == mc.renderViewEntity
                    && mc.gameSettings.thirdPersonView == 0;
                double[] o = firstPerson ? MUZZLE_FIRST_PERSON : MUZZLE_THIRD_PERSON;
                origin = FxDraw.add(
                    origin,
                    FxDraw.add(FxDraw.add(FxDraw.mul(right, o[0]), FxDraw.mul(up, o[1])), FxDraw.mul(dir, o[2])));
            }
        }
        bullet.originX = origin[0];
        bullet.originY = origin[1];
        bullet.originZ = origin[2];
        bullet.hasOrigin = true;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return DEFAULT_TEXTURE;
    }
}
