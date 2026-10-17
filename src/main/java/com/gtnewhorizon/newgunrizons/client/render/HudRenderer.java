package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.client.input.KeyBindings;
import com.gtnewhorizon.newgunrizons.items.ItemMagazine;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;
import com.gtnewhorizon.newgunrizons.weapon.WeaponAttachmentAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponHeat;
import com.gtnewhorizon.newgunrizons.weapon.WeaponState;

public class HudRenderer {

    public enum StatusBarPosition {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT;
    }

    private static final int STATUS_BAR_BOTTOM_OFFSET = 15;
    private static final int STATUS_BAR_TOP_OFFSET = 10;
    private static final float AMMO_COUNTER_SCALE = 1.5F;

    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFF00;
    private static final int COLOR_RED = 0xFF0000;

    private final StatusBarPosition statusBarPosition;

    public HudRenderer() {
        this.statusBarPosition = StatusBarPosition.BOTTOM_RIGHT;
    }

    public boolean renderWeaponHud(ScaledResolution resolution, ItemStack itemStack,
        ItemWeaponInstance weaponInstance) {
        ItemWeapon weaponItem = (ItemWeapon) itemStack.getItem();
        String crosshair = weaponItem != null ? weaponItem.getCrosshair(weaponInstance) : null;
        if (crosshair == null) {
            return false;
        }

        Minecraft mc = Minecraft.getMinecraft();

        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;
        mc.entityRenderer.setupOverlayRendering();

        mc.renderEngine.bindTexture(new ResourceLocation(crosshair));
        GL11.glPushAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        if (isInModifyingState(weaponInstance)) {
            renderAttachmentModeHints(fontRender, width, height);
        } else {
            renderStatusMessage(fontRender, width, height, getDefaultWeaponMessage(weaponInstance), COLOR_YELLOW);
            renderHeat(fontRender, width, height, itemStack, mc.theWorld.getTotalWorldTime());
        }

        GL11.glPopAttrib();
        return true;
    }

    public void renderMagazineHud(ScaledResolution resolution, ItemStack itemStack) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRender = mc.fontRenderer;
        mc.entityRenderer.setupOverlayRendering();

        renderStatusMessage(fontRender, width, height, getDefaultMagazineMessage(itemStack), COLOR_RED);
    }

    public boolean renderGrenadeHud(ScaledResolution resolution) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRender = mc.fontRenderer;
        mc.entityRenderer.setupOverlayRendering();

        StatusMessageManager.Message message = StatusMessageManager.INSTANCE.nextMessage();
        if (message == null) {
            return false;
        }

        int color = COLOR_WHITE;
        String messageText = message.getMessage();
        if (message.isAlert()) {
            color = COLOR_YELLOW;
        }

        int stringWidth = fontRender.getStringWidth(messageText);
        int x = width - stringWidth - 5;
        int y = getStatusBarYPosition(height, fontRender.FONT_HEIGHT);

        fontRender.drawStringWithShadow(messageText, x, y, color);
        return true;
    }

    /** Heat gauge above the ammo counter for weapons that overheat (or have a cooldown). */
    private void renderHeat(FontRenderer fontRender, int width, int height, ItemStack itemStack, long worldTime) {
        ItemWeapon weapon = (ItemWeapon) itemStack.getItem();
        if (!weapon.getBehavior()
            .hasHeat()) return;
        String text;
        int color;
        if (WeaponHeat.isLocked(itemStack, worldTime)) {
            text = StatCollector.translateToLocalFormatted(
                "gui.newgunrizons.overheated",
                String.format("%.1f", WeaponHeat.secondsLeft(itemStack, worldTime)));
            color = COLOR_RED;
        } else {
            int percent = Math.round(WeaponHeat.fraction(itemStack, worldTime) * 100.0F);
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < 10; i++) bar.append(i < percent / 10 ? '|' : '.');
            text = StatCollector.translateToLocalFormatted("gui.newgunrizons.heat", bar.toString(), percent);
            color = percent >= 75 ? COLOR_YELLOW : COLOR_WHITE;
        }
        int scaledHeight = (int) (fontRender.FONT_HEIGHT * AMMO_COUNTER_SCALE);
        int y = getStatusBarYPosition(height, scaledHeight) - fontRender.FONT_HEIGHT - 3;
        fontRender.drawStringWithShadow(text, width - fontRender.getStringWidth(text) - 5, y, color);
    }

    private void renderAttachmentModeHints(FontRenderer fontRender, int width, int height) {
        String changeScopeMessage = StatCollector.translateToLocalFormatted(
            "gui.attachmentMode.changeScope",
            Keyboard.getKeyName(KeyBindings.upArrowKey.getKeyCode()));
        fontRender.drawStringWithShadow(changeScopeMessage, width / 2 - 40, 60, COLOR_WHITE);

        String changeBarrelRig = StatCollector.translateToLocalFormatted(
            "gui.attachmentMode.changeBarrelRig",
            Keyboard.getKeyName(KeyBindings.leftArrowKey.getKeyCode()));
        fontRender.drawStringWithShadow(changeBarrelRig, 10, height / 2 - 10, COLOR_WHITE);

        String changeUnderBarrelRig = StatCollector.translateToLocalFormatted(
            "gui.attachmentMode.changeUnderBarrelRig",
            Keyboard.getKeyName(KeyBindings.downArrowKey.getKeyCode()));
        fontRender.drawStringWithShadow(changeUnderBarrelRig, 10, height - 40, COLOR_WHITE);
    }

    private void renderStatusMessage(FontRenderer fontRender, int width, int height, String defaultMessage,
        int alertColor) {
        int color = COLOR_WHITE;
        String messageText;

        StatusMessageManager.Message message = StatusMessageManager.INSTANCE.nextMessage();
        if (message != null) {
            messageText = message.getMessage();
            if (message.isAlert()) {
                color = alertColor;
            }
        } else {
            messageText = defaultMessage;
        }

        int stringWidth = fontRender.getStringWidth(messageText);
        int scaledStringWidth = (int) (stringWidth * AMMO_COUNTER_SCALE);
        int scaledHeight = (int) (fontRender.FONT_HEIGHT * AMMO_COUNTER_SCALE);
        int x = width - scaledStringWidth - 5;
        int y = getStatusBarYPosition(height, scaledHeight);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glScalef(AMMO_COUNTER_SCALE, AMMO_COUNTER_SCALE, 1.0F);
        fontRender.drawStringWithShadow(messageText, 0, 0, color);
        GL11.glPopMatrix();
    }

    private String getDefaultMagazineMessage(ItemStack itemStack) {
        ItemMagazine magazine = (ItemMagazine) itemStack.getItem();
        return StatCollector
            .translateToLocalFormatted("gui.ammoCounter", ItemInstance.getAmmo(itemStack) + "/" + magazine.getAmmo());
    }

    private String getDefaultWeaponMessage(ItemWeaponInstance weaponInstance) {
        ItemMagazine magazine = (ItemMagazine) WeaponAttachmentAspect
            .getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
        int totalCapacity;
        if (magazine != null) {
            totalCapacity = magazine.getAmmo();
        } else {
            totalCapacity = weaponInstance.getWeapon()
                .getAmmoCapacity();
        }

        if (weaponInstance.getWeapon()
            .getAmmoCapacity() == 0 && totalCapacity == 0) {
            return StatCollector.translateToLocalFormatted("gui.noMagazine");
        }
        return StatCollector.translateToLocalFormatted(
            "gui.ammoCounter",
            weaponInstance.getWeapon()
                .getCurrentAmmo() + "/"
                + totalCapacity);
    }

    private boolean isInModifyingState(ItemWeaponInstance weaponInstance) {
        return weaponInstance.getState() == WeaponState.MODIFYING
            || weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT;
    }

    private int getStatusBarYPosition(int height, int textHeight) {
        switch (statusBarPosition) {
            case BOTTOM_RIGHT:
            case BOTTOM_LEFT:
                return height - STATUS_BAR_BOTTOM_OFFSET - textHeight;
            default:
                return STATUS_BAR_TOP_OFFSET;
        }
    }
}
