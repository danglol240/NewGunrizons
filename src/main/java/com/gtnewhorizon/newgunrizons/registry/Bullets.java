package com.gtnewhorizon.newgunrizons.registry;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemBullet;
import com.gtnewhorizon.newgunrizons.model.BedrockModel;

public class Bullets {

    public static ItemBullet ShotgunShell;

    public static void init() {
        ShotgunShell = new ItemBullet.Builder().withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("ShotgunShell")
            .withModel(new BedrockModel("ammo/shotgunshell"))
            .withTextureName("ShotgunShell")
            .build();
    }
}
