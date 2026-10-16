package com.gtnewhorizon.newgunrizons.registry;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemBullet;
import com.gtnewhorizon.newgunrizons.model.JsonModel;

public class Bullets {

    public static ItemBullet FerroSlug;
    public static ItemBullet LaserCharge;
    public static ItemBullet PlasmaCannonCharge;
    public static ItemBullet ParticleCharge;
    public static ItemBullet ExoticCharge;
    public static ItemBullet AntimatterCharge;
    public static ItemBullet ShotgunShell;
    public static ItemBullet ShotgunShell410;
    public static ItemBullet Magnum44Ammo;
    public static ItemBullet Bullet762x39;
    public static ItemBullet Bullet556x39;
    public static ItemBullet Bullet556x45;
    public static ItemBullet Bullet762x54;
    public static ItemBullet Bullet762x51;
    public static ItemBullet Bullet45ACP;
    public static ItemBullet Bullet9mm;
    public static ItemBullet Bullet357;
    public static ItemBullet BMG50;
    public static ItemBullet Bullet57x28;
    public static ItemBullet Bullet46x30;
    public static ItemBullet Carbine30;
    public static ItemBullet Bullet65x39;
    public static ItemBullet Bullet10x24;
    public static ItemBullet Bullet10mm;
    public static ItemBullet Bullet300Blackout;
    public static ItemBullet SMAWRocket;
    public static ItemBullet Bullet50;
    public static ItemBullet Bullet9x39mm;
    public static ItemBullet BulletSpringfield3006;
    public static ItemBullet Bullet545x39;
    public static ItemBullet Bullet762x25;
    public static ItemBullet Bullet8mm;
    public static ItemBullet Bullet308;
    public static ItemBullet Bullet455;
    public static ItemBullet Bullet380200;
    public static ItemBullet Bullet762x21;
    public static ItemBullet Bullet792x33;
    public static ItemBullet Bullet792x57;
    public static ItemBullet Bullet303British;

    public static void init() {
        ShotgunShell = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("ShotgunShell")
            .withModel(new JsonModel("ammo/shotgunshell"), "ShotgunShell.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        ShotgunShell410 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("ShotgunShell410")
            .withModel(new JsonModel("ammo/shotgunshell"), "ShotgunShell.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet9x39mm = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet9x39mm")
            .withModel(new JsonModel("ammo/bulletbig"), "gold.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet8mm = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet8mm")
            .withModel(new JsonModel("ammo/bulletbig"), "gold.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        SMAWRocket = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("SMAWRocket")
            .withMaxStackSize(1)
            .withModel(new JsonModel("ammo/smawrocket"), "SMAW.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.8F, -2.1F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withTextureName("Dummy.png")

            .build(ItemBullet.class);
        Bullet10x24 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet10x24")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet762x21 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet762x21")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet303British = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet303British")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet792x33 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet792x33")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet792x57 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet792x57")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Magnum44Ammo = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Magnum44Ammo")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet455 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet455")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet380200 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet380200")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Carbine30 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Carbine30")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.6D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.5D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        BulletSpringfield3006 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("BulletSpringfield3006")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.6D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.5D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet357 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet357")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.6D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.5D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet50 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet50AE")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet44.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.6D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.5D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet9mm = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet9mm")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet10mm = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet10mm")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet45ACP = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet45ACP")
            .withModel(new JsonModel("ammo/bullet44"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.3D, 0.3D, 0.3D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet762x39 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet762x39")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.6D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet46x30 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet46x30")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.6D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet57x28 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet57x28")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.6D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet556x39 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet556x39")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.3D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.45D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.1D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet545x39 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet545x39")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.3D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.45D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.1D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet762x25 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet762x25")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.3D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.45D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.1D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet556x45 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet556x45")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.4D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.5D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet762x54 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet762x54")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.5D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.6D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.3D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet762x51 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet762x51")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.45D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.55D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.25D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet308 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet308")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.45D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.55D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.25D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet300Blackout = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet300Blackout")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.45D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.55D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.25D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        BMG50 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("BMG50")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.7D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.7D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        Bullet65x39 = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("Bullet65x39")
            .withModel(new JsonModel("ammo/bulletbig"), "Bullet.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.45D, 0.45D, 0.45D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.55D, 0.55D, 0.55D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);

        // ---- High-tier ammo (LuV-UHV) ----
        FerroSlug = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("FerroSlug")
            .withModel(new JsonModel("ammo/bulletbig"), "FerroSlug.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.7D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.7D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        LaserCharge = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("LaserCharge")
            .withModel(new JsonModel("ammo/bulletbig"), "LaserCharge.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.7D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.7D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        PlasmaCannonCharge = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("PlasmaCannonCharge")
            .withMaxStackSize(1)
            .withModel(new JsonModel("ammo/smawrocket"), "PlasmaCannonCharge.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.8F, -2.1F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withTextureName("Dummy.png")

            .build(ItemBullet.class);
        ParticleCharge = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("ParticleCharge")
            .withModel(new JsonModel("ammo/bulletbig"), "ParticleCharge.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.7D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.7D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        ExoticCharge = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("ExoticCharge")
            .withModel(new JsonModel("ammo/bulletbig"), "ExoticCharge.png")

            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.0F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.4D, 0.7D, 0.4D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.29F, 0.7F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.7D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.45F, 0.4F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(1.2D, 1.4D, 1.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemBullet.class);
        AntimatterCharge = (new ItemBullet.Builder()).withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withName("AntimatterCharge")
            .withMaxStackSize(1)
            .withModel(new JsonModel("ammo/smawrocket"), "AntimatterCharge.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -1.2F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.29F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.0D, 0.0D, 0.0D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.8F, -2.1F);
                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withTextureName("Dummy.png")

            .build(ItemBullet.class);
    }
}
