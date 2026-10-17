package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * Draws the Munitions Factory's assembly scene: copies of the product glide from the back of the production lane to
 * the controller, with welding sparks along the way. Client only; called from the controller's renderTESR.
 */
public final class FactoryAssemblyRenderer {

    /** Lane speed in blocks per tick. */
    private static final double SPEED = 0.025D;
    private static final float ITEM_SCALE = 1.15F;

    private static EntityItem dummy;

    private FactoryAssemblyRenderer() {}

    public static void render(IGregTechTileEntity tile, ItemStack product, int segments, double x, double y, double z,
        float partialTicks) {
        if (tile == null || !tile.isActive()) return;
        World world = tile.getWorld();
        if (world == null) return;

        ForgeDirection front = tile.getFrontFacing();
        ForgeDirection back = front.getOpposite();
        if (dummy == null || dummy.worldObj != world) {
            dummy = new EntityItem(world, 0, 0, 0, product.copy());
        }
        dummy.setEntityItemStack(product);
        dummy.hoverStart = 0.0F;
        dummy.age = 0;

        double time = (world.getTotalWorldTime() % 1_000_000L) + partialTicks;
        // the lane runs from just behind the head (0.5) to the end of the last segment (segments + 0.5)
        double length = segments;
        boolean wasInFrame = RenderItem.renderInFrame;
        RenderItem.renderInFrame = true;
        try {
            for (int k = 0; k < segments; k++) {
                double travelled = (time * SPEED + k) % length;
                double s = segments + 0.5D - travelled; // back -> front
                double px = x + 0.5D + back.offsetX * s;
                double py = y + 0.5D + back.offsetY * s;
                double pz = z + 0.5D + back.offsetZ * s;

                GL11.glPushMatrix();
                GL11.glTranslated(px, py - 0.15D, pz);
                GL11.glRotatef(yawFor(front), 0.0F, 1.0F, 0.0F);
                GL11.glScalef(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                RenderManager.instance.renderEntityWithPosYaw(dummy, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();

                // a welding spark now and then where the product passes a work station
                if (world.rand.nextInt(90) == 0) {
                    double wx = tile.getXCoord() + 0.5D + back.offsetX * s;
                    double wy = tile.getYCoord() + 0.75D + back.offsetY * s;
                    double wz = tile.getZCoord() + 0.5D + back.offsetZ * s;
                    world.spawnParticle(
                        "fireworksSpark",
                        wx,
                        wy,
                        wz,
                        (world.rand.nextDouble() - 0.5D) * 0.08D,
                        0.06D,
                        (world.rand.nextDouble() - 0.5D) * 0.08D);
                }
            }
        } finally {
            RenderItem.renderInFrame = wasInFrame;
        }
    }

    /** Turn the product side-on to the lane so it is seen in profile through the side windows. */
    private static float yawFor(ForgeDirection front) {
        switch (front) {
            case NORTH:
            case SOUTH:
                return 90.0F;
            default:
                return 0.0F;
        }
    }
}
