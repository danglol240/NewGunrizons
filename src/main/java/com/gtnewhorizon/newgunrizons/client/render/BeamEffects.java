package com.gtnewhorizon.newgunrizons.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.items.ShotProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Short-lived hitscan beams (lasers, dimensional rifts, erasure beams). Added when the server reports a shot, drawn
 * from the shooter's muzzle to where the beam ended, and faded out over a few ticks.
 */
public class BeamEffects {

    private static final List<Beam> BEAMS = new ArrayList<>();
    private static final int MAX_BEAMS = 64;
    private static int nextSeed;

    private static final class Beam {

        final ShotProfile weapon;
        final double[] start;
        final double[] end;
        final long born;
        final int duration;
        final int seed;

        Beam(ShotProfile weapon, double[] start, double[] end, long born, int duration, int seed) {
            this.weapon = weapon;
            this.start = start;
            this.end = end;
            this.born = born;
            this.duration = duration;
            this.seed = seed;
        }
    }

    public static void add(ShotProfile weapon, EntityLivingBase shooter, double sx, double sy, double sz, double ex,
        double ey, double ez) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        double[] eye = { sx, sy, sz };
        double[] end = { ex, ey, ez };
        double[] dir = FxDraw.norm(FxDraw.sub(end, eye));
        // players fire from the muzzle of the gun in their hands; drones from their own body
        double[] start = dir == null || !(shooter instanceof EntityPlayer) ? eye : FxDraw.muzzle(shooter, eye, dir);
        int duration;
        switch (weapon.getProjectileStyle()) {
            case RIFT:
                duration = 14;
                break;
            case ERASE:
                duration = 24;
                break;
            default:
                duration = 6;
                break;
        }
        synchronized (BEAMS) {
            if (BEAMS.size() >= MAX_BEAMS) BEAMS.remove(0);
            BEAMS.add(new Beam(weapon, start, end, mc.theWorld.getTotalWorldTime(), duration, nextSeed++));
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            synchronized (BEAMS) {
                BEAMS.clear();
            }
            return;
        }
        double now = mc.theWorld.getTotalWorldTime() + event.partialTicks;
        double[] cam = { RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ };
        synchronized (BEAMS) {
            if (BEAMS.isEmpty()) return;
            FxDraw.begin();
            try {
                Iterator<Beam> it = BEAMS.iterator();
                while (it.hasNext()) {
                    Beam beam = it.next();
                    double age = (now - beam.born) / beam.duration;
                    if (age >= 1.0D || age < -1.0D) {
                        it.remove();
                        continue;
                    }
                    draw(beam, FxDraw.sub(beam.start, cam), FxDraw.sub(beam.end, cam), Math.max(0.0D, age), now);
                }
            } finally {
                FxDraw.end();
            }
        }
    }

    private static void draw(Beam beam, double[] a, double[] b, double age, double t) {
        int rgb = beam.weapon.getProjectileColor();
        float r = FxDraw.red(rgb), g = FxDraw.green(rgb), bl = FxDraw.blue(rgb);
        float s = beam.weapon.getProjectileScale();
        float fade = (float) (1.0D - age);
        ProjectileStyle style = beam.weapon.getProjectileStyle();
        if (style == ProjectileStyle.RIFT) {
            rift(beam, a, b, r, g, bl, s, fade);
        } else if (style == ProjectileStyle.ERASE) {
            erase(a, b, r, g, bl, s, fade, age);
        } else {
            float flicker = 0.8F + 0.2F * (float) Math.sin(t * 2.7D);
            FxDraw.ribbon(a, b, 0.16D * s, r, g, bl, 0.55F * fade * flicker, 0.45F * fade * flicker);
            FxDraw.ribbon(a, b, 0.04D * s, FxDraw.WHITE, FxDraw.WHITE, FxDraw.WHITE, fade, 0.8F * fade);
            FxDraw.glow(a, 0.15D * s, r, g, bl, 0.9F * fade);
            FxDraw.glow(b, 0.3D * s, r, g, bl, 0.9F * fade);
        }
    }

    /** A jagged tear in space along the beam with shards flaking off it. */
    private static void rift(Beam beam, double[] a, double[] b, float r, float g, float bl, float s, float fade) {
        double[] span = FxDraw.sub(b, a);
        double len = FxDraw.length(span);
        if (len < 1.0E-3D) return;
        double[] d = FxDraw.mul(span, 1.0D / len);
        double[][] basis = FxDraw.basis(d);
        Random rnd = new Random(beam.seed * 7919L);
        int n = Math.max(2, (int) (len / 1.2D));
        double[][] pts = new double[n + 1][];
        for (int i = 0; i <= n; i++) {
            double k = len * i / n;
            double jag = (i == 0 || i == n) ? 0.0D : 0.28D * s;
            double[] off = FxDraw.add(
                FxDraw.mul(basis[0], (rnd.nextDouble() - 0.5D) * 2 * jag),
                FxDraw.mul(basis[1], (rnd.nextDouble() - 0.5D) * 2 * jag));
            pts[i] = FxDraw.add(FxDraw.add(a, FxDraw.mul(d, k)), off);
        }
        FxDraw.ribbon(a, b, 0.6D * s, r, g, bl, 0.15F * fade, 0.15F * fade);
        for (int i = 0; i < n; i++) {
            FxDraw.ribbon(pts[i], pts[i + 1], 0.16D * s, r, g, bl, 0.7F * fade, 0.7F * fade);
            FxDraw.ribbon(pts[i], pts[i + 1], 0.05D * s, FxDraw.WHITE, FxDraw.WHITE, FxDraw.WHITE, fade, fade);
        }
        for (int i = 0; i < n; i += 2) {
            double[] c = pts[i];
            double[] tip = FxDraw.add(
                c,
                FxDraw.add(
                    FxDraw.mul(basis[0], (rnd.nextDouble() - 0.5D) * s),
                    FxDraw.mul(basis[1], (rnd.nextDouble() - 0.5D) * s)));
            FxDraw.line(c, tip, 0.65F, 0.25F, 1.0F, 0.8F * fade, 1.5F);
        }
        FxDraw.glow(b, 0.6D * s, r, g, bl, 0.8F * fade);
    }

    /** Wide white-gold beam with rings expanding out of it. */
    private static void erase(double[] a, double[] b, float r, float g, float bl, float s, float fade, double age) {
        double width = (0.7D - 0.3D * age) * s;
        FxDraw.ribbon(a, b, width * 1.8D, r, g, bl, 0.25F * fade, 0.25F * fade);
        FxDraw.ribbon(a, b, width, r, g, bl, 0.7F * fade, 0.7F * fade);
        FxDraw.ribbon(a, b, width * 0.3D, FxDraw.WHITE, FxDraw.WHITE, FxDraw.WHITE, fade, fade);
        double[] span = FxDraw.sub(b, a);
        double len = FxDraw.length(span);
        double[] d = FxDraw.norm(span);
        if (d != null) {
            for (double k = 3.0D; k < len; k += 5.0D) {
                FxDraw.ring(FxDraw.add(a, FxDraw.mul(d, k)), d, (0.3D + age * 1.4D) * s, r, g, bl, 0.8F * fade, 2.0F);
            }
        }
        GL11.glLineWidth(1.0F);
        FxDraw.glow(b, (1.0D + age * 2.0D) * s, r, g, bl, 0.9F * fade);
        FxDraw.glow(b, 0.5D * s, FxDraw.WHITE, FxDraw.WHITE, FxDraw.WHITE, fade);
    }
}
