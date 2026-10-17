package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.CommonProxy;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.registry.Parts;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * Draws the Munitions Factory's assembly scene. One work piece per segment travels from the back of the lane to the
 * controller. For a weapon, its parts (barrel, receiver, stock and its class mechanism) start spread apart and
 * spinning, close in on each other as they travel, and become the finished weapon with a burst of welding sparks.
 * Other products grow from small to full size. Client only; called from the controller's renderTESR.
 */
public final class FactoryAssemblyRenderer {

    /** Lane speed in blocks per tick. */
    private static final double SPEED = 0.02D;
    /** Fraction of the lane after which the parts have become the finished product. */
    private static final double ASSEMBLED_AT = 0.7D;
    private static final float PRODUCT_SCALE = 1.1F;
    private static final float PART_SCALE = 0.7F;

    /** Where each part starts relative to the work piece: {side, up, along the lane}. */
    private static final double[][] PART_OFFSETS = { { 0.0D, 0.32D, -0.25D }, { -0.3D, -0.05D, 0.15D },
        { 0.3D, -0.05D, 0.15D }, { 0.0D, -0.3D, 0.0D } };

    private static EntityItem dummy;

    private FactoryAssemblyRenderer() {}

    public static void render(IGregTechTileEntity tile, ItemStack product, int segments, double x, double y, double z,
        float partialTicks) {
        if (tile == null || !tile.isActive()) return;
        World world = tile.getWorld();
        if (world == null) return;

        ForgeDirection front = tile.getFrontFacing();
        ForgeDirection back = front.getOpposite();
        double[] lane = { back.offsetX, back.offsetY, back.offsetZ };
        double[] up = back.offsetY == 0 ? new double[] { 0, 1, 0 } : new double[] { 1, 0, 0 };
        double[] side = { lane[1] * up[2] - lane[2] * up[1], lane[2] * up[0] - lane[0] * up[2],
            lane[0] * up[1] - lane[1] * up[0] };

        if (dummy == null || dummy.worldObj != world) {
            dummy = new EntityItem(world, 0, 0, 0, product.copy());
        }
        ItemStack[] parts = partsOf(product);
        double time = (world.getTotalWorldTime() % 1_000_000L) + partialTicks;

        boolean wasInFrame = RenderItem.renderInFrame;
        RenderItem.renderInFrame = true;
        try {
            for (int k = 0; k < segments; k++) {
                double travelled = (time * SPEED + k) % segments;
                double progress = travelled / segments; // 0 at the back of the lane, 1 at the controller
                double s = segments + 0.5D - travelled;
                double[] c = { x + 0.5D + lane[0] * s, y + 0.5D + lane[1] * s, z + 0.5D + lane[2] * s };

                if (parts != null && progress < ASSEMBLED_AT) {
                    double spread = 1.0D - progress / ASSEMBLED_AT; // 1 = apart, 0 = joined
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i] == null) continue;
                        double[] o = PART_OFFSETS[i % PART_OFFSETS.length];
                        double wobble = Math.sin(time * 0.15D + i * 1.7D) * 0.05D * spread;
                        double px = c[0] + (side[0] * o[0] + up[0] * (o[1] + wobble) + lane[0] * o[2]) * spread;
                        double py = c[1] + (side[1] * o[0] + up[1] * (o[1] + wobble) + lane[1] * o[2]) * spread;
                        double pz = c[2] + (side[2] * o[0] + up[2] * (o[1] + wobble) + lane[2] * o[2]) * spread;
                        float spin = (float) (time * 4.0D * spread + i * 90.0D);
                        drawItem(parts[i], px, py, pz, front, spin, PART_SCALE);
                    }
                    // the moment the parts join: a burst of sparks
                    if (spread < 0.08D && world.rand.nextInt(3) == 0) {
                        sparks(world, tile, lane, s, 3);
                    }
                } else {
                    double grow = parts == null ? Math.min(1.0D, 0.35D + progress) : 1.0D;
                    drawItem(product, c[0], c[1] - 0.15D, c[2], front, 0.0F, (float) (PRODUCT_SCALE * grow));
                    if (world.rand.nextInt(120) == 0) {
                        sparks(world, tile, lane, s, 1);
                    }
                }
            }
        } finally {
            RenderItem.renderInFrame = wasInFrame;
        }
    }

    /** The parts a weapon is assembled from, or null for products shown growing instead. */
    private static ItemStack[] partsOf(ItemStack product) {
        Item item = product.getItem();
        if (!(item instanceof ItemWeapon)) return null;
        Item mechanism = Parts.mechanismFor(item);
        if (mechanism == null) mechanism = Parts.SmartTargetingModule;
        return new ItemStack[] { stack(CommonProxy.GunBarrelSteel), stack(CommonProxy.GunReceiverSteel),
            stack(CommonProxy.GunStockCarbon), stack(mechanism) };
    }

    private static ItemStack stack(Item item) {
        return item == null ? null : new ItemStack(item);
    }

    private static void drawItem(ItemStack stack, double px, double py, double pz, ForgeDirection front, float spin,
        float scale) {
        dummy.setEntityItemStack(stack);
        dummy.hoverStart = 0.0F;
        dummy.age = 0;
        GL11.glPushMatrix();
        GL11.glTranslated(px, py, pz);
        GL11.glRotatef(yawFor(front) + spin, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(scale, scale, scale);
        RenderManager.instance.renderEntityWithPosYaw(dummy, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        GL11.glPopMatrix();
    }

    private static void sparks(World world, IGregTechTileEntity tile, double[] lane, double s, int count) {
        for (int i = 0; i < count; i++) {
            world.spawnParticle(
                "fireworksSpark",
                tile.getXCoord() + 0.5D + lane[0] * s,
                tile.getYCoord() + 0.6D + lane[1] * s,
                tile.getZCoord() + 0.5D + lane[2] * s,
                (world.rand.nextDouble() - 0.5D) * 0.1D,
                0.05D + world.rand.nextDouble() * 0.05D,
                (world.rand.nextDouble() - 0.5D) * 0.1D);
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
