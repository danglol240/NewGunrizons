package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Untextured, additive effect drawing shared by projectiles, hitscan beams and gravity fields. Coordinates are
 * camera-relative (the camera sits at the origin), so camera-facing shapes can use the position itself as the view
 * direction. Drawing is immediate mode with the shader program unbound and restored afterwards, which keeps
 * Iris/Angelica shader packs intact.
 */
public final class FxDraw {

    public static final float WHITE = 1.0F;

    /** Muzzle position relative to the shooter's eye: right, up and forward, for first and third person. */
    private static final double[] MUZZLE_FIRST_PERSON = { 0.26D, -0.24D, 0.55D };
    private static final double[] MUZZLE_THIRD_PERSON = { 0.38D, -0.48D, 0.60D };

    private static int savedProgram;

    private FxDraw() {}

    /** Sets up additive untextured drawing; pair with {@link #end()}. */
    public static void begin() {
        savedProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(0);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        additive();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDepthMask(false);
    }

    public static void end() {
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GL20.glUseProgram(savedProgram);
    }

    public static void additive() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    /** Normal alpha blending, for dark shapes (black holes) that additive blending cannot show. */
    public static void translucent() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * World position of the muzzle of a shooter aiming along dir from the eye position eye; first-person offsets
     * when the shooter is the local player in first person.
     */
    public static double[] muzzle(EntityLivingBase shooter, double[] eye, double[] dir) {
        double[] right = norm(cross(dir, new double[] { 0, 1, 0 }));
        if (right == null) return eye;
        double[] up = cross(right, dir);
        Minecraft mc = Minecraft.getMinecraft();
        boolean firstPerson = shooter != null && shooter == mc.renderViewEntity && mc.gameSettings.thirdPersonView == 0;
        double[] o = firstPerson ? MUZZLE_FIRST_PERSON : MUZZLE_THIRD_PERSON;
        return add(eye, add(add(mul(right, o[0]), mul(up, o[1])), mul(dir, o[2])));
    }

    // ---------------- primitives ----------------

    /** Camera-facing strip from a to b; alpha goes from alphaA at a to alphaB at b. */
    public static void ribbon(double[] a, double[] b, double width, float r, float g, float bl, float alphaA,
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
    public static void glow(double[] c, double radius, float r, float g, float b, float alpha) {
        disc(c, radius, r, g, b, alpha, 0.0F);
    }

    /** Camera-facing disc with the given alpha at the centre and at the rim. */
    public static void disc(double[] c, double radius, float r, float g, float b, float alphaCentre, float alphaRim) {
        double[] view = norm(c);
        if (view == null) return;
        double[][] basis = basis(view);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(r, g, b, alphaCentre);
        GL11.glVertex3d(c[0], c[1], c[2]);
        GL11.glColor4f(r, g, b, alphaRim);
        for (int i = 0; i <= 24; i++) {
            double a = Math.PI * 2 * i / 24.0D;
            double[] q = add(c, add(mul(basis[0], Math.cos(a) * radius), mul(basis[1], Math.sin(a) * radius)));
            GL11.glVertex3d(q[0], q[1], q[2]);
        }
        GL11.glEnd();
    }

    /** Flat ring band between two radii around c, perpendicular to axis, coloured from inner to outer. */
    public static void annulus(double[] c, double[] axis, double inner, double outer, double spin, float[] innerRgba,
        float[] outerRgba, int segments, boolean striped) {
        double[][] basis = basis(axis);
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            double a = spin + Math.PI * 2 * i / segments;
            double[] u = add(mul(basis[0], Math.cos(a)), mul(basis[1], Math.sin(a)));
            float k = striped ? 0.55F + 0.45F * (float) Math.sin(a * 5.0D) : 1.0F;
            GL11.glColor4f(innerRgba[0], innerRgba[1], innerRgba[2], innerRgba[3] * k);
            double[] p = add(c, mul(u, inner));
            GL11.glVertex3d(p[0], p[1], p[2]);
            GL11.glColor4f(outerRgba[0], outerRgba[1], outerRgba[2], outerRgba[3] * k);
            p = add(c, mul(u, outer));
            GL11.glVertex3d(p[0], p[1], p[2]);
        }
        GL11.glEnd();
    }

    /** Circle of the given radius around c, perpendicular to axis. */
    public static void ring(double[] c, double[] axis, double radius, float r, float g, float b, float alpha,
        float lineWidth) {
        double[][] basis = basis(axis);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(r, g, b, alpha);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 32; i++) {
            double a = Math.PI * 2 * i / 32.0D;
            double[] q = add(c, add(mul(basis[0], Math.cos(a) * radius), mul(basis[1], Math.sin(a) * radius)));
            GL11.glVertex3d(q[0], q[1], q[2]);
        }
        GL11.glEnd();
    }

    /** Straight line from a to b. */
    public static void line(double[] a, double[] b, float r, float g, float bl, float alpha, float lineWidth) {
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(r, g, bl, alpha);
        GL11.glVertex3d(a[0], a[1], a[2]);
        GL11.glVertex3d(b[0], b[1], b[2]);
        GL11.glEnd();
    }

    /** Clock face around c, facing the camera: rim, twelve ticks and two hands turning with t. */
    public static void clock(double[] c, double radius, double t, float r, float g, float b, float alpha) {
        double[] view = norm(c);
        if (view == null) return;
        double[][] basis = basis(view);
        ring(c, view, radius, r, g, b, alpha, 2.0F);
        for (int i = 0; i < 12; i++) {
            double a = Math.PI * 2 * i / 12.0D;
            double[] u = add(mul(basis[0], Math.cos(a)), mul(basis[1], Math.sin(a)));
            line(add(c, mul(u, radius * 0.8D)), add(c, mul(u, radius)), r, g, b, alpha, i % 3 == 0 ? 3.0F : 1.5F);
        }
        double big = -t * 0.12D, small = -t * 0.01D;
        line(c, add(c, add(mul(basis[0], Math.cos(big) * radius * 0.85D), mul(basis[1], Math.sin(big) * radius * 0.85D))),
            WHITE, WHITE, WHITE, alpha, 2.5F);
        line(c, add(c, add(mul(basis[0], Math.cos(small) * radius * 0.5D), mul(basis[1], Math.sin(small) * radius * 0.5D))),
            r, g, b, alpha, 3.0F);
    }

    // ---------------- vector maths ----------------

    /** Two unit vectors perpendicular to n and to each other. */
    public static double[][] basis(double[] n) {
        double[] helper = Math.abs(n[1]) < 0.9D ? new double[] { 0, 1, 0 } : new double[] { 1, 0, 0 };
        double[] u = norm(cross(n, helper));
        double[] v = cross(n, u);
        return new double[][] { u, v };
    }

    public static double[] add(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    public static double[] sub(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    public static double[] mul(double[] a, double k) {
        return new double[] { a[0] * k, a[1] * k, a[2] * k };
    }

    public static double[] cross(double[] a, double[] b) {
        return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }

    public static double length(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    public static double[] norm(double[] a) {
        double l = length(a);
        return l < 1e-9D ? null : mul(a, 1.0D / l);
    }

    public static float red(int rgb) {
        return ((rgb >> 16) & 255) / 255.0F;
    }

    public static float green(int rgb) {
        return ((rgb >> 8) & 255) / 255.0F;
    }

    public static float blue(int rgb) {
        return (rgb & 255) / 255.0F;
    }
}
