package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityShellCasing;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.model.JsonModel;

public class EntityShellRenderer extends Render {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(
        NewGunrizonsMod.MODID,
        "textures/effect/shell.png");

    private static final ModelBase DEFAULT_MODEL = new JsonModel("misc/modelshell");

    public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
        EntityShellCasing entityShellCasing = (EntityShellCasing) entity;
        ItemWeapon weapon = entityShellCasing.getWeapon();
        if (weapon != null) {
            GL11.glPushMatrix();
            this.bindTexture(DEFAULT_TEXTURE);
            GL11.glTranslated(x, y, z);
            float fov = Minecraft.getMinecraft().gameSettings.fovSetting;
            float scale = (fov * 0.001F - 0.02F) * 0.3F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(entityShellCasing.getXRotation(), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(entityShellCasing.getYRotation(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entityShellCasing.getZRotation(), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            DEFAULT_MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return DEFAULT_TEXTURE;
    }

}
