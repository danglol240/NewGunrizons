package com.gtnewhorizon.newgunrizons.registry;

import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.items.factories.DoomShotgunFactory;
import com.gtnewhorizon.newgunrizons.items.factories.LaserRifleFactory;
import com.gtnewhorizon.newgunrizons.items.factories.PlasmaRifleFactory;

public class Guns {

    public static Item DoomShotgun;
    public static Item PlasmaRifle;
    public static Item LaserRifle;

    public static void init() {
        DoomShotgun = new DoomShotgunFactory().createGun();
        PlasmaRifle = new PlasmaRifleFactory().createGun();
        LaserRifle = new LaserRifleFactory().createGun();
    }
}
