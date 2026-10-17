package com.gtnewhorizon.newgunrizons.client.render;

import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
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
    private static final float WHITE = 1.0F;
    private static final double MAX_BEAM = 48.0D;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityBullet)) return;
        EntityBullet bullet = (EntityBullet) entity;
        ItemWeapon weapon = bullet.getWeapon();
        if (weapon == null || weapon.getProjectileStyle() == null
            || weapon.getProjectileStyle() == ProjectileStyle.NONE) {
            return;
        }
        if (!bullet.hasOrigin) {
            bullet.originX = bullet.posX;
            bullet.originY = bullet.posY;
            bullet.originZ = bullet.posZ;
            bullet.hasOrigin = true;
        }

        double[] pos = { x, y, z };
        double[] dir = norm(new double[] { bullet.motionX, bullet.motionY, bullet.motionZ });
        if (dir == null) dir = norm(
            new double[] { bullet.posX - bullet.prevPosX, bullet.posY - bullet.prevPosY,
                bullet.posZ - bullet.prevPosZ });
        if (dir == null) return;

        int rgb = weapon.getProjectileColor();
        float r = ((rgb >> 16) & 255) / 255.0F, g = ((rgb >> 8) & 255) / 255.0F, b = (rgb & 255) / 255.0F;
        float scale = weapon.getProjectileScale();
        double t = bullet.ticksExisted + partialTicks;

        int savedProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(0);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glDepthMask(false);

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
                default:
                    break;
            }
        } finally {
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL20.glUseProgram(savedProgram);
        }
    }

    // ---------------- styles ----------------

    private static void tracer(double[] p, double[] d, float r, float g, float b, float s) {
        double[] tail = add(p, mul(d, -2.5D * s));
        ribbon(tail, p, 0.10D * s, r, g, b, 0.0F, 0.55F);
        ribbon(tail, p, 0.03D * s, WHITE, WHITE, WHITE, 0.0F, 0.9F);
    }

    private static void rail(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double len = 6.0D * s;
        double[] tail = add(p, mul(d, -len));
        ribbon(tail, p, 0.22D * s, r, g, b, 0.0F, 0.45F);
        ribbon(tail, p, 0.06D * s, WHITE, WHITE, WHITE, 0.1F, 1.0F);
        // fading coil rings left behind along the path
        for (int i = 1; i <= 5; i++) {
            double k = i * len / 6.0D;
            float a = 0.7F * (1.0F - i / 6.0F);
            ring(add(p, mul(d, -k)), d, (0.14D + 0.03D * i) * s, r, g, b, a, 2.0F);
        }
        glow(p, 0.22D * s, r, g, b, 0.8F);
    }

    private static void laser(EntityBullet bullet, double[] p, double[] d, double x, double y, double z, float r,
        float g, float b, float s, double t) {
        // the muzzle end is where the bolt was first seen, in camera-relative space
        double[] start = { x + (bullet.originX - bullet.posX), y + (bullet.originY - bullet.posY),
            z + (bullet.originZ - bullet.posZ) };
        double[] span = sub(p, start);
        double len = length(span);
        if (len > MAX_BEAM) start = add(p, mul(d, -MAX_BEAM));
        float flicker = 0.75F + 0.25F * (float) Math.sin(t * 2.7D);
        ribbon(start, p, 0.14D * s, r, g, b, 0.35F * flicker, 0.45F * flicker);
        ribbon(start, p, 0.035D * s, WHITE, WHITE, WHITE, 0.8F, 1.0F);
        glow(p, 0.18D * s, r, g, b, 0.9F);
    }

    private static void plasma(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double radius = (0.22D + 0.04D * Math.sin(t * 1.3D)) * s;
        ribbon(add(p, mul(d, -1.6D * s)), p, radius * 1.3D, r, g, b, 0.0F, 0.5F);
        glow(p, radius * 2.2D, r, g, b, 0.45F);
        glow(p, radius, WHITE, WHITE, WHITE, 0.9F);
    }

    private static void helix(double[] p, double[] d, float r, float g, float b, float s, double t) {
        double len = 4.0D * s;
        ribbon(add(p, mul(d, -len)), p, 0.05D * s, WHITE, WHITE, WHITE, 0.0F, 1.0F);
        double[][] basis = basis(d);
        double radius = 0.16D * s;
        for (int strand = 0; strand < 2; strand++) {
            GL11.glLineWidth(2.5F);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int i = 0; i <= 48; i++) {
                double k = len * i / 48.0D;
                double a = k * 5.0D + t * 0.9D + strand * Math.PI;
                double[] q = add(
                    add(p, mul(d, -k)),
                    add(mul(basis[0], Math.cos(a) * radius), mul(basis[1], Math.sin(a) * radius)));
                GL11.glColor4f(r, g, b, (float) (0.95D * (1.0D - k / len)));
                GL11.glVertex3d(q[0], q[1], q[2]);
            }
            GL11.glEnd();
        }
        glow(p, 0.2D * s, r, g, b, 0.85F);
    }

    private static void pulse(double[] p, double[] d, float r, float g, float b, float s, double t) {
        ribbon(add(p, mul(d, -2.0D * s)), p, 0.12D * s, r, g, b, 0.0F, 0.5F);
        glow(p, 0.26D * s, WHITE, WHITE, WHITE, 0.8F);
        glow(p, 0.5D * s, r, g, b, 0.4F);
        // three shock rings expanding out of the pulse as it travels
        for (int i = 0; i < 3; i++) {
            double phase = ((t * 0.12D) + i / 3.0D) % 1.0D;
            ring(
                add(p, mul(d, -phase * 1.5D * s)),
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
        ribbon(add(p, mul(d, -2.5D * s)), p, 0.3D * s, r, g, b, 0.0F, 0.35F);
        glow(p, 0.55D * s, r, g, b, 0.5F);
        glow(p, 0.22D * s, WHITE, WHITE, WHITE, 0.9F);
        double[][] basis = basis(d);
        // two containment rings spinning on different axes
        double a1 = t * 0.35D, a2 = -t * 0.27D;
        double[] axis1 = norm(add(mul(d, Math.cos(a1)), mul(basis[0], Math.sin(a1))));
        double[] axis2 = norm(add(mul(basis[0], Math.cos(a2)), mul(basis[1], Math.sin(a2))));
        ring(p, axis1, 0.6D * s, r, g, b, 0.9F, 3.0F);
        ring(p, axis2, 0.48D * s, WHITE, g * 0.6F + 0.4F, b * 0.6F + 0.4F, 0.8F, 2.0F);
    }

    private static void shards(EntityBullet bullet, double[] p, double[] d, float r, float g, float b, float s,
        double t) {
        Random rnd = new Random(bullet.getEntityId() * 31L + (long) (t * 2.0D));
        double[][] basis = basis(d);
        ribbon(add(p, mul(d, -2.0D * s)), p, 0.04D * s, WHITE, WHITE, WHITE, 0.0F, 0.9F);
        for (int i = 0; i < 9; i++) {
            double back = rnd.nextDouble() * 2.5D * s;
            double off = 0.1D + rnd.nextDouble() * 0.35D * s;
            double ang = rnd.nextDouble() * Math.PI * 2;
            double[] c = add(
                add(p, mul(d, -back)),
                add(mul(basis[0], Math.cos(ang) * off), mul(basis[1], Math.sin(ang) * off)));
            double[] tip = add(c, mul(d, -(0.2D + rnd.nextDouble() * 0.4D) * s));
            boolean purple = (i & 1) == 0;
            ribbon(c, tip, 0.06D * s, purple ? 0.65F : r, purple ? 0.25F : g, purple ? 1.0F : b, 0.9F, 0.2F);
        }
        glow(p, 0.3D * s, r, g, b, 0.7F);
    }

    // ---------------- primitives ----------------

    /** Camera-facing strip from a to b; alpha goes from alphaA at a to alphaB at b. */
    private static void ribbon(double[] a, double[] b, double width, float r, float g, float bl, float alphaA,
        float alphaB) {
        double[] dir = sub(b, a);
        double[] mid = mul(add(a, b), 0.5D);
        double[] side = norm(cross(dir, mid));
        if (side == null) return;
        double[] o = mul(side, width / 2.0D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(r, g, bl, alphaA);
        GL11.glVertex3d(a[0] - o[0], a[1] - o[1], a[2] - o[2]);
        GL11.glVertex3d(a[0] + o[0], a[1] + o[1], a[2] + o[2]);
        GL11.glColor4f(r, g, bl, alphaB);
        GL11.glVertex3d(b[0] + o[0], b[1] + o[1], b[2] + o[2]);
        GL11.glVertex3d(b[0] - o[0], b[1] - o[1], b[2] - o[2]);
        GL11.glEnd();
    }

    /** Camera-facing disc with a bright centre fading to transparent at the rim. */
    private static void glow(double[] c, double radius, float r, float g, float b, float alpha) {
        double[] view = norm(c);
        if (view == null) return;
        double[][] basis = basis(view);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(r, g, b, alpha);
        GL11.glVertex3d(c[0], c[1], c[2]);
        GL11.glColor4f(r, g, b, 0.0F);
        for (int i = 0; i <= 16; i++) {
            double a = Math.PI * 2 * i / 16.0D;
            double[] q = add(c, add(mul(basis[0], Math.cos(a) * radius), mul(basis[1], Math.sin(a) * radius)));
            GL11.glVertex3d(q[0], q[1], q[2]);
        }
        GL11.glEnd();
    }

    /** Circle of the given radius around c, perpendicular to axis. */
    private static void ring(double[] c, double[] axis, double radius, float r, float g, float b, float alpha,
        float lineWidth) {
        double[][] basis = basis(axis);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(r, g, b, alpha);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 24; i++) {
            double a = Math.PI * 2 * i / 24.0D;
            double[] q = add(c, add(mul(basis[0], Math.cos(a) * radius), mul(basis[1], Math.sin(a) * radius)));
            GL11.glVertex3d(q[0], q[1], q[2]);
        }
        GL11.glEnd();
    }

    /** Two unit vectors perpendicular to n and to each other. */
    private static double[][] basis(double[] n) {
        double[] helper = Math.abs(n[1]) < 0.9D ? new double[] { 0, 1, 0 } : new double[] { 1, 0, 0 };
        double[] u = norm(cross(n, helper));
        double[] v = cross(n, u);
        return new double[][] { u, v };
    }

    private static double[] add(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    private static double[] sub(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    private static double[] mul(double[] a, double k) {
        return new double[] { a[0] * k, a[1] * k, a[2] * k };
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }

    private static double length(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    private static double[] norm(double[] a) {
        double l = length(a);
        return l < 1e-9D ? null : mul(a, 1.0D / l);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return DEFAULT_TEXTURE;
    }
}
