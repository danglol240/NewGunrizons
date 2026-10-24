package com.gtnewhorizon.newgunrizons.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityDrone;
import com.gtnewhorizon.newgunrizons.items.DroneLayout;
import com.gtnewhorizon.newgunrizons.items.ItemDrone;
import com.gtnewhorizon.newgunrizons.model.JsonModel;

/**
 * Draws a drone: its body model, tilted into the direction it is flying like a real multirotor, with spinning rotor
 * blades and their blur disc drawn on every motor.
 */
public class DroneRenderer extends Render {

    private static final Map<ItemDrone, JsonModel> MODELS = new HashMap<>();
    private static final float UNIT = 1.0F / 16.0F;
    private static final float MAX_TILT = 22.0F;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityDrone)) return;
        EntityDrone drone = (EntityDrone) entity;
        ItemDrone item = drone.getDrone();
        if (item == null) return;

        float bodyYaw = drone.prevRotationYaw + (drone.rotationYaw - drone.prevRotationYaw) * partialTicks;
        // tilt into the horizontal velocity, seen from the drone's own heading
        double vx = drone.posX - drone.prevPosX, vz = drone.posZ - drone.prevPosZ;
        double rad = Math.toRadians(bodyYaw);
        double forward = -vx * Math.sin(rad) + vz * Math.cos(rad);
        double side = vx * Math.cos(rad) + vz * Math.sin(rad);
        float pitch = (float) Math.max(-MAX_TILT, Math.min(MAX_TILT, forward * 90.0D));
        float roll = (float) Math.max(-MAX_TILT, Math.min(MAX_TILT, side * 90.0D));
        float rotor = drone.prevRotorAngle + (drone.rotorAngle - drone.prevRotorAngle) * partialTicks;
        float hover = (float) Math.sin((drone.ticksExisted + partialTicks) * 0.15D) * 0.03F;

        render(item, x, y + drone.height * 0.5D + hover, z, bodyYaw, pitch, roll, rotor, 1.0F);
    }

    /**
     * Draws a drone model centred on (x, y, z). Also used by the Drone Assembly Complex to show the drone being built.
     *
     * @param rotorSpeed 0 = blades only, 1 = full blur disc
     */
    public static void render(ItemDrone item, double x, double y, double z, float yaw, float pitch, float roll,
        float rotorAngle, float rotorSpeed) {
        JsonModel model = MODELS.get(item);
        if (model == null) {
            model = new JsonModel(
                "drone/drone_" + item.getName()
                    .toLowerCase());
            MODELS.put(item, model);
        }
        float s = item.getRenderScale();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(-s, -s, s);
        Minecraft.getMinecraft().renderEngine.bindTexture(
            new ResourceLocation(
                NewGunrizonsMod.MODID,
                "textures/models/drone_" + item.getName()
                    .toLowerCase() + ".png"));
        model.render(Minecraft.getMinecraft().thePlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, UNIT);
        rotors(item.getLayout(), rotorAngle, rotorSpeed);
        GL11.glPopMatrix();
    }

    private static void rotors(DroneLayout layout, float angle, float speed) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float y = DroneLayout.ROTOR_Y * UNIT;
        float r = layout.rotorRadius * UNIT;
        float w = 0.5F * UNIT;
        for (int i = 0; i < layout.rotors; i++) {
            float hx = layout.hubX(i) * UNIT, hz = layout.hubZ(i) * UNIT;
            // neighbouring rotors turn opposite ways, as on a real multirotor
            double a = Math.toRadians((i % 2 == 0 ? angle : -angle) + i * 37.0F);
            if (speed > 0.0F) {
                GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                GL11.glColor4f(0.15F, 0.15F, 0.17F, 0.10F * speed);
                GL11.glVertex3f(hx, y, hz);
                GL11.glColor4f(0.15F, 0.15F, 0.17F, 0.28F * speed);
                for (int k = 0; k <= 20; k++) {
                    double t = Math.PI * 2 * k / 20.0D;
                    GL11.glVertex3d(hx + Math.cos(t) * r, y, hz + Math.sin(t) * r);
                }
                GL11.glEnd();
            }
            double cx = Math.cos(a), sz = Math.sin(a);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.12F, 0.12F, 0.14F, speed > 0.0F ? 0.55F : 0.95F);
            for (int blade = 0; blade < 2; blade++) {
                double bx = blade == 0 ? cx : -sz, bz = blade == 0 ? sz : cx;
                double px = -bz * w, pz = bx * w;
                GL11.glVertex3d(hx - bx * r - px, y, hz - bz * r - pz);
                GL11.glVertex3d(hx - bx * r + px, y, hz - bz * r + pz);
                GL11.glVertex3d(hx + bx * r + px, y, hz + bz * r + pz);
                GL11.glVertex3d(hx + bx * r - px, y, hz + bz * r - pz);
            }
            GL11.glEnd();
        }
        GL11.glPopAttrib();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
