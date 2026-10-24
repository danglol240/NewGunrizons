package com.gtnewhorizon.newgunrizons.client.handlers;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.input.KeyBindings;
import com.gtnewhorizon.newgunrizons.entities.EntityVehicle;
import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.network.VehicleControlMessage;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleLayout;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleWeapon;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregtech.api.enums.GTValues;

/**
 * Vehicle controls and HUD. While the local player sits in a vehicle, the mouse buttons fire the seat's weapons
 * instead of breaking or using blocks, and the movement keys, jump and the descend key are sent to the server every
 * tick (and applied at once to a vehicle the player drives).
 */
public class VehicleClientHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null || mc.theWorld == null) return;
        EntityVehicle v = EntityVehicle.of(player);
        if (v == null || v.getVehicle() == null) return;
        int seat = v.seatOf(player);
        if (seat < 0) return;
        boolean free = mc.currentScreen == null && mc.inGameHasFocus;
        float forward = player.movementInput.moveForward;
        float strafe = player.movementInput.moveStrafe;
        boolean up = player.movementInput.jump;
        boolean down = free && KeyBindings.vehicleDownKey.getIsKeyPressed();
        boolean primary = free && Mouse.isButtonDown(0);
        boolean secondary = free && Mouse.isButtonDown(1);
        v.setInput(seat, forward, strafe, up, down, primary, secondary);
        NewGunrizonsMod.CHANNEL.sendToServer(new VehicleControlMessage(forward, strafe, up, down, primary, secondary));
    }

    /** No block breaking, block use or hand weapons while riding: the mouse fires the vehicle's guns. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouse(MouseEvent event) {
        if (event.button != 0 && event.button != 1) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && EntityVehicle.of(mc.thePlayer) != null) event.setCanceled(true);
    }

    /** Riders of enclosed seats are hidden inside the hull. */
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        EntityPlayer p = event.entityPlayer;
        EntityVehicle v = EntityVehicle.of(p);
        if (v == null || v.getVehicle() == null) return;
        int seat = v.seatOf(p);
        List<VehicleLayout.Point> seats = v.getVehicle()
            .getLayout().seats;
        if (seat >= 0 && seat < seats.size() && seats.get(seat).hidden) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityVehicle v = EntityVehicle.of(mc.thePlayer);
        if (v == null || v.getVehicle() == null) return;
        ItemVehicle item = v.getVehicle();
        int seat = v.seatOf(mc.thePlayer);
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        FontRenderer f = mc.fontRenderer;
        int x = 6, y = res.getScaledHeight() / 2 - 40;
        GL11.glPushMatrix();
        f.drawStringWithShadow(
            new ItemStack(item).getDisplayName() + " §7(" + GTValues.VN[item.getTier()] + ")",
            x,
            y,
            0xFFE8A0);
        y += 11;
        bar(
            x,
            y,
            v.getHealth() / item.getMaxHealth(),
            0xFF50D060,
            "Hull " + Math.round(v.getHealth()) + "/" + Math.round(item.getMaxHealth()),
            f);
        y += 11;
        bar(
            x,
            y,
            v.getChargeFraction(),
            0xFF40B8FF,
            item.getFuel().label + " " + Math.round(v.getChargeFraction() * 100) + "%",
            f);
        y += 13;
        double vx = v.posX - v.prevPosX, vy = v.posY - v.prevPosY, vz = v.posZ - v.prevPosZ;
        int kmh = (int) Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz) * 72.0D);
        f.drawStringWithShadow(kmh + " km/h  §7Y " + (int) v.posY, x, y, 0xFFFFFF);
        y += 11;
        int[] ammo = v.getSyncedAmmo();
        List<VehicleWeapon> weapons = item.getWeapons();
        for (int i = 0; i < weapons.size(); i++) {
            VehicleWeapon w = weapons.get(i);
            int ws = w.turret >= 0 ? item.getLayout().turrets.get(w.turret).seat : 0;
            if (ws != seat && !(seat == 0 && v.occupant(ws) == null)) continue;
            String key = w.trigger == VehicleWeapon.Trigger.PRIMARY ? "LMB" : "RMB";
            String amount = w.usesRounds() ? (i < ammo.length ? ammo[i] : 0) + "/" + w.magazine : "energy";
            f.drawStringWithShadow("§b" + key + "§f " + w.name + " §7" + amount, x, y, 0xFFFFFF);
            y += 10;
        }
        y += 3;
        String hint = seat == 0 ? (item.getMode().flies
            ? "W/S/A/D move, Space up, " + KeyBindings.keyName(KeyBindings.vehicleDownKey) + " down, Shift exit"
            : "W/S drive, A/D steer, Shift exit") : "Mouse aims the turret, Shift exit";
        f.drawStringWithShadow("§8" + hint, x, y, 0xFFFFFF);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void bar(int x, int y, float fill, int color, String label, FontRenderer f) {
        int w = 90;
        Gui.drawRect(x, y, x + w, y + 9, 0x90000000);
        Gui.drawRect(x + 1, y + 1, x + 1 + Math.round((w - 2) * Math.max(0.0F, Math.min(1.0F, fill))), y + 8, color);
        f.drawStringWithShadow(label, x + 3, y + 1, 0xFFFFFF);
    }
}
