package com.gtnewhorizon.newgunrizons.client.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityVehicle;
import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleLayout;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleMode;

/**
 * Draws a vehicle: the hull, every turret turned to where its gunner looks (gun pitched separately), spinning rotor
 * discs, glowing hover emitters and engine flames that grow with the throttle.
 */
public class VehicleRenderer extends Render {

    private static final Map<String, JsonModel> MODELS = new HashMap<>();
    private static final Set<String> MISSING = new HashSet<>();
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

        float yaw = v.prevRotationYaw + MathHelper.wrapAngleTo180_float(v.rotationYaw - v.prevRotationYaw) * partialTicks;
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
        draw(base);

        List<VehicleLayout.Turret> turrets = l.turrets;
        for (int i = 0; i < turrets.size(); i++) {
            VehicleLayout.Turret tr = turrets.get(i);
            EntityPlayer gunner = v.occupant(tr.seat);
            if (gunner == null && tr.seat != 0) gunner = v.occupant(0);
            float aimYaw = yaw, aimPitch = 0.0F;
            if (gunner != null) {
                aimYaw = gunner.prevRotationYaw + (gunner.rotationYaw - gunner.prevRotationYaw) * partialTicks;
                aimPitch = gunner.prevRotationPitch + (gunner.rotationPitch - gunner.prevRotationPitch) * partialTicks;
                aimPitch = MathHelper.clamp_float(aimPitch, tr.minPitch, tr.maxPitch) - pitch;
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(tr.x * UNIT, tr.y * UNIT, tr.z * UNIT);
            GL11.glRotatef(MathHelper.wrapAngleTo180_float(aimYaw - yaw), 0.0F, 1.0F, 0.0F);
            if (tr.hasTurretModel) draw(base + "_t" + i);
            GL11.glTranslatef(tr.gx * UNIT, tr.gy * UNIT, tr.gz * UNIT);
            GL11.glRotatef(aimPitch, 1.0F, 0.0F, 0.0F);
            if (tr.hasGunModel) draw(base + "_g" + i);
            GL11.glPopMatrix();
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
        float maxHeight, float maxLength) {
        VehicleLayout l = item.getLayout();
        float fit = Math.min(1.0F, Math.min(maxWidth / Math.max(0.1F, l.width * 1.6F), maxHeight / Math.max(0.1F, l.height)));
        fit = Math.min(fit, maxLength / Math.max(0.1F, l.width * 2.2F));
        String base = "vehicle_" + item.getName()
            .toLowerCase();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(-l.scale * fit, -l.scale * fit, l.scale * fit);
        draw(base);
        for (int i = 0; i < l.turrets.size(); i++) {
            VehicleLayout.Turret tr = l.turrets.get(i);
            GL11.glPushMatrix();
            GL11.glTranslatef(tr.x * UNIT, tr.y * UNIT, tr.z * UNIT);
            if (tr.hasTurretModel) draw(base + "_t" + i);
            GL11.glTranslatef(tr.gx * UNIT, tr.gy * UNIT, tr.gz * UNIT);
            if (tr.hasGunModel) draw(base + "_g" + i);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    private static void draw(String path) {
        if (MISSING.contains(path)) return;
        JsonModel model = MODELS.get(path);
        if (model == null) {
            if (VehicleRenderer.class.getResource("/assets/newgunrizons/models/vehicle/" + path + ".geo.json") == null) {
                MISSING.add(path);
                return;
            }
            model = new JsonModel("vehicle/" + path);
            MODELS.put(path, model);
        }
        Minecraft.getMinecraft().renderEngine
            .bindTexture(new ResourceLocation(NewGunrizonsMod.MODID, "textures/models/" + path + ".png"));
        model.render(Minecraft.getMinecraft().thePlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, UNIT);
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
