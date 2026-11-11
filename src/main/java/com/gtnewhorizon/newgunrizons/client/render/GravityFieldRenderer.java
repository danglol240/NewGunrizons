package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityGravityField;

/** Draws gravity wells, repulse blasts, singularities and time fields. */
public class GravityFieldRenderer extends Render {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        NewGunrizonsMod.MODID,
        "textures/effect/bullet44.png");
    private static final int GRAVITY = 0x28E6C8;
    private static final int TIME = 0x5AFF6E;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityGravityField)) return;
        EntityGravityField field = (EntityGravityField) entity;
        double t = field.ticksExisted + partialTicks;
        double age = Math.min(1.0D, t / Math.max(1, field.getLifetime()));
        double[] c = { x, y, z };
        double radius = field.getRadius();
        FxDraw.begin();
        try {
            switch (field.getKind()) {
                case GRAVITY_WELL:
                    well(c, radius, t, age);
                    break;
                case REPULSE:
                    repulse(c, radius, age);
                    break;
                case SINGULARITY:
                    singularity(c, radius, t, age);
                    break;
                case CHRONO:
                    chrono(c, radius, t, age);
                    break;
                default:
                    break;
            }
        } finally {
            FxDraw.end();
        }
    }

    private static float fadeInOut(double age) {
        return (float) Math.min(1.0D, Math.min(age * 8.0D, (1.0D - age) * 6.0D));
    }

    private static void well(double[] c, double radius, double t, double age) {
        float r = FxDraw.red(GRAVITY), g = FxDraw.green(GRAVITY), b = FxDraw.blue(GRAVITY);
        float a = fadeInOut(age);
        double[] view = FxDraw.norm(c);
        if (view == null) return;
        FxDraw.glow(c, 1.6D, r, g, b, 0.5F * a);
        for (int i = 0; i < 4; i++) {
            double phase = ((t * 0.05D) + i / 4.0D) % 1.0D;
            FxDraw.ring(c, view, 0.5D + (1.0D - phase) * radius * 0.9D, r, g, b, (float) (0.8D * phase) * a, 2.0F);
        }
        double[] up = { 0, 1, 0 };
        FxDraw.annulus(
            c,
            up,
            0.6D,
            radius * 0.5D,
            -t * 0.25D,
            new float[] { r, g, b, 0.5F * a },
            new float[] { r, g, b, 0.0F },
            48,
            true);
        FxDraw.translucent();
        FxDraw.disc(c, 0.55D, 0.0F, 0.0F, 0.0F, a, 0.85F * a);
        FxDraw.additive();
    }

    private static void repulse(double[] c, double radius, double age) {
        float r = FxDraw.red(GRAVITY), g = FxDraw.green(GRAVITY), b = FxDraw.blue(GRAVITY);
        float a = (float) (1.0D - age);
        double[] view = FxDraw.norm(c);
        if (view == null) return;
        FxDraw.glow(c, 1.0D + age * radius, r, g, b, 0.6F * a);
        for (int i = 0; i < 3; i++) {
            double k = Math.max(0.0D, age * 1.2D - i * 0.12D);
            FxDraw.ring(c, view, k * radius, r, g, b, a, 3.0F);
            FxDraw.ring(
                c,
                new double[] { 0, 1, 0 },
                k * radius,
                FxDraw.WHITE,
                FxDraw.WHITE,
                FxDraw.WHITE,
                0.6F * a,
                2.0F);
        }
    }

    private static void singularity(double[] c, double radius, double t, double age) {
        float r = FxDraw.red(TIME) * 0.6F + 0.4F, g = FxDraw.green(TIME), b = FxDraw.blue(TIME) * 0.6F + 0.4F;
        float a = fadeInOut(age);
        double core = 0.8D + 0.1D * Math.sin(t * 0.4D);
        double[] axis = FxDraw.norm(new double[] { 0.25D, 1.0D, 0.15D });
        FxDraw.glow(c, radius * 0.45D, r, g, b, 0.35F * a);
        FxDraw.annulus(
            c,
            axis,
            core * 1.05D,
            core * 3.0D,
            t * 0.2D,
            new float[] { FxDraw.WHITE, FxDraw.WHITE, 0.9F, 0.95F * a },
            new float[] { r, g, b, 0.0F },
            64,
            true);
        FxDraw.translucent();
        FxDraw.disc(c, core, 0.0F, 0.0F, 0.0F, a, a);
        FxDraw.additive();
        FxDraw.ring(c, FxDraw.norm(c), core * 1.03D, FxDraw.WHITE, FxDraw.WHITE, FxDraw.WHITE, 0.9F * a, 2.0F);
    }

    private static void chrono(double[] c, double radius, double t, double age) {
        float r = FxDraw.red(TIME), g = FxDraw.green(TIME), b = FxDraw.blue(TIME);
        float a = fadeInOut(age);
        double spin = t * 0.01D;
        double[][] axes = { { Math.cos(spin), 0, Math.sin(spin) }, { 0, 1, 0 },
            { -Math.sin(spin), 0.3D, Math.cos(spin) } };
        for (double[] axis : axes) {
            FxDraw.ring(c, FxDraw.norm(axis), radius, r, g, b, 0.55F * a, 2.0F);
        }
        FxDraw.glow(c, radius * 0.9D, r, g, b, 0.12F * a);
        FxDraw.clock(c, 1.2D, t, r, g, b, 0.9F * a);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
