package com.gtnewhorizon.newgunrizons.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityVehicle;
import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleLayout;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleMode;

/**
 * Draws a vehicle: the hull, every turret turned to where its gunner looks (gun pitched separately), spinning rotor
 * discs, glowing hover emitters and engine flames that grow with the throttle.
 */
public class VehicleRenderer extends Render {

    /** Meshes by vehicle; an empty entry marks a vehicle without a mesh. */
    private static final Map<String, VehicleMesh> MESHES = new HashMap<>();
    private static final float UNIT = VehicleLayout.UNIT;
    /** Engine and emitter colour per tier (LV..MAX), matching the models' glow paint. */
    private static final int[] GLOW = { 0xFFD278, 0xFFD278, 0xFF8C3C, 0xFFDC3C, 0xFFB03C, 0x28C8FF, 0xFF46BE, 0xFF781E,
        0x6EA0FF, 0xB446FF, 0x505AFF, 0x28E6C8, 0x5AFF6E, 0xDC50FF, 0xFFCD46 };

    public VehicleRenderer() {
        this.shadowSize = 1.2F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(entity instanceof EntityVehicle)) return;
        EntityVehicle v = (EntityVehicle) entity;
        ItemVehicle item = v.getVehicle();
        if (item == null) return;
        VehicleLayout l = item.getLayout();
        VehicleMode mode = item.getMode();

        float yaw = v.prevRotationYaw
            + MathHelper.wrapAngleTo180_float(v.rotationYaw - v.prevRotationYaw) * partialTicks;
        float throttle = v.prevThrottle + (v.throttle - v.prevThrottle) * partialTicks;
        float pitch = mode == VehicleMode.SPACE
            ? v.prevRotationPitch + (v.rotationPitch - v.prevRotationPitch) * partialTicks
            : mode == VehicleMode.AIR && !v.onGround ? throttle * 4.0F : 0.0F;
        float roll = v.prevRoll + (v.roll - v.prevRoll) * partialTicks;
        float t = v.ticksExisted + partialTicks;
        double bob = mode == VehicleMode.HOVER || mode.flies && !v.onGround ? Math.sin(t * 0.12D) * 0.05D : 0.0D;
        String base = "vehicle_" + item.getName()
            .toLowerCase();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + bob, z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
        float s = l.scale;
        GL11.glScalef(-s, -s, s);
        VehicleMesh mesh = mesh(base);
        if (mesh != null) {
            VehicleMesh.setLight(v.getBrightnessForRender(partialTicks));
            float travel = v.prevWheelTravel + (v.wheelTravel - v.prevWheelTravel) * partialTicks;
            float steer = v.prevSteer + (v.steer - v.prevSteer) * partialTicks;
            float rotor = v.prevRotorAngle + (v.rotorAngle - v.prevRotorAngle) * partialTicks;
            float[][] aims = new float[l.turrets.size()][];
            for (int i = 0; i < aims.length; i++) {
                VehicleLayout.Turret tr = l.turrets.get(i);
                EntityPlayer gunner = v.occupant(tr.seat);
                if (gunner == null && tr.seat != 0) gunner = v.occupant(0);
                float aimYaw = yaw, aimPitch = 0.0F;
                if (gunner != null) {
                    aimYaw = gunner.prevRotationYaw + (gunner.rotationYaw - gunner.prevRotationYaw) * partialTicks;
                    aimPitch = gunner.prevRotationPitch
                        + (gunner.rotationPitch - gunner.prevRotationPitch) * partialTicks;
                    aimPitch = MathHelper.clamp_float(aimPitch, tr.minPitch, tr.maxPitch) - pitch;
                }
                aims[i] = new float[] { MathHelper.wrapAngleTo180_float(aimYaw - yaw), aimPitch };
            }
            drawMesh(mesh, l, travel, steer, rotor, aims);
        }

        int glow = GLOW[Math.min(GLOW.length - 1, item.getTier())];
        effects(l, v.prevRotorAngle + (v.rotorAngle - v.prevRotorAngle) * partialTicks, throttle, t, glow);
        GL11.glPopMatrix();
    }

    /**
     * Draws a vehicle with its turrets facing forward, shrunk to fit a hall of the given size (blocks). Used by the
     * Vehicle Assembly Hangar to show the vehicle being built.
     */
    public static void renderStatic(ItemVehicle item, double x, double y, double z, float yaw, float maxWidth,
        float maxHeight, float maxLength, int brightness) {
        VehicleLayout l = item.getLayout();
        float fit = Math
            .min(1.0F, Math.min(maxWidth / Math.max(0.1F, l.width * 1.6F), maxHeight / Math.max(0.1F, l.height)));
        fit = Math.min(fit, maxLength / Math.max(0.1F, l.width * 2.2F));
        String base = "vehicle_" + item.getName()
            .toLowerCase();
        VehicleMesh mesh = mesh(base);
        if (mesh == null) return;
        VehicleMesh.setLight(brightness);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(-l.scale * fit, -l.scale * fit, l.scale * fit);
        drawMesh(mesh, l, 0.0F, 0.0F, 0.0F, new float[l.turrets.size()][]);
        GL11.glPopMatrix();
    }

    private static VehicleMesh mesh(String base) {
        if (!MESHES.containsKey(base)) MESHES.put(base, VehicleMesh.load(base));
        VehicleMesh mesh = MESHES.get(base);
        if (mesh != null) Minecraft.getMinecraft().renderEngine
            .bindTexture(new ResourceLocation(NewGunrizonsMod.MODID, "textures/models/" + base + ".png"));
        return mesh;
    }

    /**
     * Hull, wheels (rolled by the distance driven, front ones steered), modelled rotors and turrets. Called in the
     * vehicle's model frame (after the -s, -s, s scale); mesh coordinates are model units.
     */
    private static void drawMesh(VehicleMesh mesh, VehicleLayout l, float travel, float steer, float rotor,
        float[][] aims) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glScalef(UNIT, UNIT, UNIT);
        mesh.render("hull");
        for (int i = 0; i < l.wheels.size(); i++) {
            VehicleLayout.Point w = l.wheels.get(i);
            GL11.glPushMatrix();
            GL11.glTranslatef(w.x, w.y, w.z);
            if (w.steer) GL11.glRotatef(steer, 0.0F, 1.0F, 0.0F);
            double spin = Math.toDegrees(travel / Math.max(0.01D, w.r * UNIT * l.scale)) % 360.0D;
            GL11.glRotatef((float) spin, 1.0F, 0.0F, 0.0F);
            mesh.render("w" + i);
            GL11.glPopMatrix();
        }
        for (int i = 0; i < l.rotors.size(); i++) {
            VehicleLayout.Point r = l.rotors.get(i);
            if (!r.model) continue;
            GL11.glPushMatrix();
            GL11.glTranslatef(r.x, r.y, r.z);
            GL11.glRotatef((i % 2 == 0 ? rotor : -rotor) * 1.5F, 0.0F, 1.0F, 0.0F);
            mesh.render("r" + i);
            GL11.glPopMatrix();
        }
        for (int i = 0; i < l.turrets.size(); i++) {
            VehicleLayout.Turret tr = l.turrets.get(i);
            float[] aim = aims[i];
            GL11.glPushMatrix();
            GL11.glTranslatef(tr.x, tr.y, tr.z);
            if (aim != null) GL11.glRotatef(aim[0], 0.0F, 1.0F, 0.0F);
            if (tr.hasTurretModel) mesh.render("t" + i);
            GL11.glTranslatef(tr.gx, tr.gy, tr.gz);
            if (aim != null) GL11.glRotatef(aim[1], 1.0F, 0.0F, 0.0F);
            if (tr.hasGunModel) mesh.render("g" + i);
            GL11.glPopMatrix();
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /** Rotors, hover emitters and engine flames, drawn untextured in model space. */
    private static void effects(VehicleLayout l, float rotorAngle, float throttle, float t, int glow) {
        float r = ((glow >> 16) & 255) / 255.0F, g = ((glow >> 8) & 255) / 255.0F, b = (glow & 255) / 255.0F;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        for (int i = 0; i < l.rotors.size(); i++) {
            VehicleLayout.Point p = l.rotors.get(i);
            float y = p.y * UNIT, rad = p.r * UNIT, hx = p.x * UNIT, hz = p.z * UNIT;
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glColor4f(0.15F, 0.15F, 0.17F, 0.12F + 0.18F * throttle);
            GL11.glVertex3f(hx, y, hz);
            for (int k = 0; k <= 24; k++) {
                double a = Math.PI * 2 * k / 24.0D;
                GL11.glVertex3d(hx + Math.cos(a) * rad, y, hz + Math.sin(a) * rad);
            }
            GL11.glEnd();
            if (p.model) continue;
            double a = Math.toRadians((i % 2 == 0 ? rotorAngle : -rotorAngle) * 1.5D);
            double cx = Math.cos(a), sz = Math.sin(a), w = 1.0F * UNIT;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.1F, 0.1F, 0.12F, 0.85F);
            for (int blade = 0; blade < 3; blade++) {
                double ba = a + blade * Math.PI * 2 / 3;
                double bx = Math.cos(ba), bz = Math.sin(ba), px = -bz * w, pz = bx * w;
                GL11.glVertex3d(hx - px, y, hz - pz);
                GL11.glVertex3d(hx + px, y, hz + pz);
                GL11.glVertex3d(hx + bx * rad + px, y, hz + bz * rad + pz);
                GL11.glVertex3d(hx + bx * rad - px, y, hz + bz * rad - pz);
            }
            GL11.glEnd();
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        float pulse = 0.75F + 0.25F * (float) Math.sin(t * 0.3D);
        for (VehicleLayout.Point p : l.pads) {
            float y = p.y * UNIT + 0.02F, rad = p.r * UNIT * 1.4F;
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glColor4f(r, g, b, 0.55F * pulse);
            GL11.glVertex3f(p.x * UNIT, y, p.z * UNIT);
            GL11.glColor4f(r, g, b, 0.0F);
            for (int k = 0; k <= 20; k++) {
                double a = Math.PI * 2 * k / 20.0D;
                GL11.glVertex3d(p.x * UNIT + Math.cos(a) * rad, y, p.z * UNIT + Math.sin(a) * rad);
            }
            GL11.glEnd();
        }
        for (VehicleLayout.Point p : l.exhausts) {
            float rad = p.r * UNIT, len = (3.0F + 14.0F * throttle) * UNIT * (0.85F + 0.15F * pulse);
            float cx = p.x * UNIT, cy = p.y * UNIT, cz = p.z * UNIT;
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glColor4f(r, g, b, 0.0F);
            GL11.glVertex3f(cx, cy, cz + len);
            GL11.glColor4f(Math.min(1.0F, r + 0.4F), Math.min(1.0F, g + 0.4F), Math.min(1.0F, b + 0.4F), 0.8F);
            for (int k = 0; k <= 16; k++) {
                double a = Math.PI * 2 * k / 16.0D;
                GL11.glVertex3d(cx + Math.cos(a) * rad, cy + Math.sin(a) * rad, cz);
            }
            GL11.glEnd();
        }
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

    /** Seats are invisible: the vehicle draws everything. */
    public static class Seat extends Render {

        @Override
        public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {}

        @Override
        protected ResourceLocation getEntityTexture(Entity entity) {
            return null;
        }
    }
}
