package com.gtnewhorizon.newgunrizons.client.input;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static KeyBinding reloadKey;
    public static KeyBinding attachmentKey;
    public static KeyBinding upArrowKey;
    public static KeyBinding downArrowKey;
    public static KeyBinding leftArrowKey;
    public static KeyBinding rightArrowKey;
    public static KeyBinding laserSwitchKey;
    public static KeyBinding nightVisionSwitchKey;
    public static KeyBinding addKey;
    public static KeyBinding subtractKey;
    public static KeyBinding fireModeKey;
    public static KeyBinding vehicleDownKey;

    public static void init() {
        reloadKey = new KeyBinding("key.reload", 19, "key.categories.newgunrizons");
        laserSwitchKey = new KeyBinding("key.laser", 38, "key.categories.newgunrizons");
        nightVisionSwitchKey = new KeyBinding("key.nightVision", 49, "key.categories.newgunrizons");
        attachmentKey = new KeyBinding("key.attachment", 50, "key.categories.newgunrizons");
        upArrowKey = new KeyBinding("key.scope", 200, "key.categories.newgunrizons");
        downArrowKey = new KeyBinding("key.recoil_fitter", 208, "key.categories.newgunrizons");
        leftArrowKey = new KeyBinding("key.silencer", 203, "key.categories.newgunrizons");
        rightArrowKey = new KeyBinding("key.texture_change", 205, "key.categories.newgunrizons");
        addKey = new KeyBinding("key.add", 27, "key.categories.newgunrizons");
        subtractKey = new KeyBinding("key.subtract", 26, "key.categories.newgunrizons");
        fireModeKey = new KeyBinding("key.fire_mode", 54, "key.categories.newgunrizons");
        ClientRegistry.registerKeyBinding(reloadKey);
        ClientRegistry.registerKeyBinding(attachmentKey);
        ClientRegistry.registerKeyBinding(upArrowKey);
        ClientRegistry.registerKeyBinding(downArrowKey);
        ClientRegistry.registerKeyBinding(leftArrowKey);
        ClientRegistry.registerKeyBinding(rightArrowKey);
        ClientRegistry.registerKeyBinding(laserSwitchKey);
        ClientRegistry.registerKeyBinding(nightVisionSwitchKey);
        ClientRegistry.registerKeyBinding(addKey);
        ClientRegistry.registerKeyBinding(subtractKey);
        ClientRegistry.registerKeyBinding(fireModeKey);
        vehicleDownKey = new KeyBinding("key.vehicle_down", 46, "key.categories.newgunrizons");
        ClientRegistry.registerKeyBinding(vehicleDownKey);
    }

    public static String keyName(KeyBinding key) {
        return org.lwjgl.input.Keyboard.getKeyName(key.getKeyCode());
    }
}
