package com.gtnewhorizon.newgunrizons.registry;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemMagazine;
import com.gtnewhorizon.newgunrizons.model.JsonModel;

public class Magazines {

    public static ItemMagazine CoilgunMag;
    public static ItemMagazine GaussRifleMag;
    public static ItemMagazine RailgunMag;
    public static ItemMagazine LaserRifleCell;
    public static ItemMagazine PlasmaRifleCell;
    public static ItemMagazine ParticleBeamCell;
    public static ItemMagazine NeutronRifleCell;
    public static ItemMagazine ExoticMatterCell;
    public static ItemMagazine Magazine762x39;
    public static ItemMagazine PMAG762x39;
    public static ItemMagazine Mag75rnd762x39;
    public static ItemMagazine AK12Mag;
    public static ItemMagazine RPK74MMag;
    public static ItemMagazine AK74MMag;
    public static ItemMagazine AKS74UMag;
    public static ItemMagazine NATOMag1;
    public static ItemMagazine NATO20rnd;
    public static ItemMagazine NATO40rnd;
    public static ItemMagazine NATOMag2;
    public static ItemMagazine NATOFamasMag;
    public static ItemMagazine NATOG36Mag;
    public static ItemMagazine DragunovMag;
    public static ItemMagazine FALMag;
    public static ItemMagazine M110Mag;
    public static ItemMagazine M14DMRMag;
    public static ItemMagazine Glock21Mag;
    public static ItemMagazine G18Mag;
    public static ItemMagazine NATODrum100;
    public static ItemMagazine M9BerettaMag;
    public static ItemMagazine MP40Mag;
    public static ItemMagazine MP5KMag;
    public static ItemMagazine DeagleMag;
    public static ItemMagazine AS50Mag;
    public static ItemMagazine FNP90Mag;
    public static ItemMagazine M107BMag;
    public static ItemMagazine HKMP7Mag;
    public static ItemMagazine M1CarbineMag;
    public static ItemMagazine M2CarbineMag;
    public static ItemMagazine M240Mag;
    public static ItemMagazine L115Mag;
    public static ItemMagazine SV98Mag;
    public static ItemMagazine ColtM1911Mag;
    public static ItemMagazine M249Mag;
    public static ItemMagazine Mk48Mag;
    public static ItemMagazine MXMag;
    public static ItemMagazine M41AMag;
    public static ItemMagazine Mag10mm;
    public static ItemMagazine Magazine9mm;
    public static ItemMagazine ScarHMag;
    public static ItemMagazine VectorMag;
    public static ItemMagazine PP19Mag;
    public static ItemMagazine Glock32Mag;
    public static ItemMagazine HecateIIMag;
    public static ItemMagazine Deagle50Mag;
    public static ItemMagazine VSSVintorezMag;
    public static ItemMagazine ASValMag;
    public static ItemMagazine PythonClip;
    public static ItemMagazine M8A7Mag;
    public static ItemMagazine PPSH41DrumMag;
    public static ItemMagazine Type100Mag;
    public static ItemMagazine M1A1mag;
    public static ItemMagazine MP18mag;
    public static ItemMagazine HK417Mag;
    public static ItemMagazine M16A1Mag;
    public static ItemMagazine Mag308;
    public static ItemMagazine Saiga12mag;
    public static ItemMagazine VEPR12Mag;
    public static ItemMagazine SVT40mag;
    public static ItemMagazine LugerP08Mag;
    public static ItemMagazine M3A1Mag;
    public static ItemMagazine STG44Mag;
    public static ItemMagazine LeeEnfieldMag;
    public static ItemMagazine Saiga410Mag;
    public static ItemMagazine AK15Mag;
    public static ItemMagazine Tec9Mag;
    public static ItemMagazine MPXmag;

    public static void init() {
        PythonClip = (new ItemMagazine.Builder()).withAmmo(6)
            .withCompatibleBullet(Bullets.Bullet357)
            .withName("PythonClip")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/revolverclip"), "RevolverClip.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(0.4F, 3.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(2.2D, 2.2D, 2.2D);
            })
            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Magazine762x39 = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("Magazine762x39")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/magazine762x39"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        STG44Mag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("STG44Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/stg44mag"), "STG44.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        SVT40mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet762x54)
            .withName("SVT40Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/svt40mag"), "SVT40.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.5F, -0.2F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        LeeEnfieldMag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet762x54)
            .withName("LeeEnfieldMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/leeenfieldno4magazine"), "LeeEnfieldNo4.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 1.8F, -2.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        Saiga12mag = (new ItemMagazine.Builder()).withAmmo(5)
            .withCompatibleBullet(Bullets.ShotgunShell)
            .withName("Saiga12mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/saiga12mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        VEPR12Mag = (new ItemMagazine.Builder()).withAmmo(8)
            .withCompatibleBullet(Bullets.ShotgunShell)
            .withName("VEPR12Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/veprmag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        Saiga410Mag = (new ItemMagazine.Builder()).withAmmo(7)
            .withCompatibleBullet(Bullets.ShotgunShell410)
            .withName("Saiga410Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/saiga12mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        Type100Mag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet8mm)
            .withName("Type100Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/type100mag"), "Type100Mag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.7F, 2.2F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        MP18mag = (new ItemMagazine.Builder()).withAmmo(32)
            .withCompatibleBullet(Bullets.Bullet762x25)
            .withName("MP18mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mp18mag"), "MP18.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 1.0F, 1.8F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        M1A1mag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet45ACP)
            .withName("M1A1mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m1a1mag"), "M1A1Thompson.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.5F, -0.1F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        PPSH41DrumMag = (new ItemMagazine.Builder()).withAmmo(71)
            .withCompatibleBullet(Bullets.Bullet762x25)
            .withName("PPSH41Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/ppshdrummag"), "PPSH41.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, -1.0F, 0.2F);
                GL11.glRotatef(-200.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        Mag75rnd762x39 = (new ItemMagazine.Builder()).withAmmo(75)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("Mag75rnd762x39")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mag75rnd"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.2F, -0.6F);
                GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        M8A7Mag = (new ItemMagazine.Builder()).withAmmo(32)
            .withCompatibleBullet(Bullets.Bullet300Blackout)
            .withName("M8A7Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m8a7mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.8F, -1.0F, 0.9F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.5D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.4F, -0.8F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        PMAG762x39 = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("PMAG762x39")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/pmag762x39"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        AK15Mag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("AK15Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/pmag762x39"), "AK15.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.5F, -1.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        AK12Mag = (new ItemMagazine.Builder()).withAmmo(31)
            .withCompatibleBullet(Bullets.Bullet556x39)
            .withName("AK12Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/ak12magazine"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.3F, -0.6F, 1.0F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -1.3F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, 0.55F, -1.2F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        RPK74MMag = (new ItemMagazine.Builder()).withAmmo(40)
            .withCompatibleBullet(Bullets.Bullet762x39)
            .withName("RPK74MMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/rpk74mmag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.85F, -0.35F, 0.4F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        AK74MMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet545x39)
            .withName("AK74MMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/magazine545x39"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.3F, -1.0F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        AKS74UMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet545x39)
            .withName("AKS74UMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/magazine545x39"), "AKS74UMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.7F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.3F, -1.0F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATOMag1 = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATOMag1")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/natomag1"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        MPXmag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("MPXmag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mpxmag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.7F, -1.5F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Mag308 = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet308)
            .withName("Mag308")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mag308"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-1.0F, 0.6F, -1.2F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATO20rnd = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATO20rnd")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/nato20rnd"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M16A1Mag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("M16A1Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m110mag"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATO40rnd = (new ItemMagazine.Builder()).withAmmo(40)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATO40rnd")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/nato40rnd"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, -0.1F, 0.2F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATOMag2 = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATOMag2")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/natomag2"), "NATOMag2.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATOFamasMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATOFamasMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/natofamasmag"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.1F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATOG36Mag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATOG36Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/natog36mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        DragunovMag = (new ItemMagazine.Builder()).withAmmo(11)
            .withCompatibleBullet(Bullets.Bullet762x54)
            .withName("DragunovMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/dragunovmag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.3F, 2.1F, -2.8F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        FALMag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet762x51)
            .withName("FALMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/fnfalmag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M110Mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet762x51)
            .withName("M110Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m110mag"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.25F, 0.1F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        HK417Mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet762x51)
            .withName("HK417Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/hk417mag"), "HK417Mag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-1.0F, 0.6F, -1.2F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M14DMRMag = (new ItemMagazine.Builder()).withAmmo(21)
            .withCompatibleBullet(Bullets.Bullet762x51)
            .withName("M14DMRMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m14mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Glock21Mag = (new ItemMagazine.Builder()).withAmmo(13)
            .withCompatibleBullet(Bullets.Bullet45ACP)
            .withName("Glock21Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/glockmagazine"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        LugerP08Mag = (new ItemMagazine.Builder()).withAmmo(8)
            .withCompatibleBullet(Bullets.Bullet762x21)
            .withName("LugerP08Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/lugermag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.5F, 0.6F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Tec9Mag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("Tec9Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/tec9mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-1.0F, -1.2F, 0.0F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        G18Mag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("G18Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/g18mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.65F, 0.45F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Glock32Mag = (new ItemMagazine.Builder()).withAmmo(14)
            .withCompatibleBullet(Bullets.Bullet357)
            .withName("Glock32Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/glockmagazine"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M9BerettaMag = (new ItemMagazine.Builder()).withAmmo(15)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("M9BerettaMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m9mag"), "M9Mag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.5F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Mag10mm = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet10mm)
            .withName("Mag10mm")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m9mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.5F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Magazine9mm = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("Magazine9mm")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/makarovmag"), "MakarovMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.5F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NATODrum100 = (new ItemMagazine.Builder()).withAmmo(100)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("NATODrum100")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/natodrum100"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.5F, 0.4F, 0.4F);
                GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        MP40Mag = (new ItemMagazine.Builder()).withAmmo(32)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("MP40Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mp40mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -1.3F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.0F, -1.3F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M3A1Mag = (new ItemMagazine.Builder()).withAmmo(31)
            .withCompatibleBullet(Bullets.Bullet45ACP)
            .withName("M3A1Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m3a1greasegunmag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -1.3F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, -0.2F, -1.8F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        VectorMag = (new ItemMagazine.Builder()).withAmmo(26)
            .withCompatibleBullet(Bullets.Bullet45ACP)
            .withName("VectorMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mp40mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -1.3F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.0F, -1.3F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        MP5KMag = (new ItemMagazine.Builder()).withAmmo(25)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("MP5KMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/hkmp5mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 1.0F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.9F, -1.6F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.95F, -1.6F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Deagle50Mag = (new ItemMagazine.Builder()).withAmmo(7)
            .withCompatibleBullet(Bullets.Bullet50)
            .withName("Deagle50Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/deaglemag"), "Deagle44.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.7F, -0.5F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        VSSVintorezMag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet9x39mm)
            .withName("VSSVintorezMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/vssvintorezmag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        ASValMag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet9x39mm)
            .withName("ASValMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/asvalmag"), "ASValMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        AS50Mag = (new ItemMagazine.Builder()).withAmmo(5)
            .withCompatibleBullet(Bullets.BMG50)
            .withName("AS50Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/as50mag"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        HecateIIMag = (new ItemMagazine.Builder()).withAmmo(7)
            .withCompatibleBullet(Bullets.BMG50)
            .withName("HecateIIMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/as50mag"), "NATOMag1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.6D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.6D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 0.9D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        FNP90Mag = (new ItemMagazine.Builder()).withAmmo(50)
            .withCompatibleBullet(Bullets.Bullet57x28)
            .withName("FNP90Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/p90mag"), "P90Mag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.3F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.5F, 0.1F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(0.3F, 1.2F, 0.4F);
                GL11.glRotatef(-170.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M107BMag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.BMG50)
            .withName("M107BMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m107mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.1F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        HKMP7Mag = (new ItemMagazine.Builder()).withAmmo(20)
            .withCompatibleBullet(Bullets.Bullet46x30)
            .withName("HKMP7Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mp7mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(1.0F, -0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M1CarbineMag = (new ItemMagazine.Builder()).withAmmo(15)
            .withCompatibleBullet(Bullets.Carbine30)
            .withName("M1CarbineMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m14mag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M2CarbineMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Carbine30)
            .withName("M2CarbineMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m2carbinemag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        L115Mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet308)
            .withName("L115Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/l115mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.3F, -0.5F, 0.6F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.2F, -0.5F, 0.9F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-1.0F, 0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        SV98Mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet762x54)
            .withName("SV98Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/sv98mag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.3F, -0.5F, 0.6F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.2F, -0.5F, 0.9F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-1.0F, 0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.2D, 1.2D, 1.2D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        ColtM1911Mag = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.Bullet45ACP)
            .withName("ColtM1911Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/glockmagazine"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.6F, -0.3F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M249Mag = (new ItemMagazine.Builder()).withAmmo(200)
            .withCompatibleBullet(Bullets.Bullet556x45)
            .withName("M249Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m249mag"), "M249.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.4F, 0.4F);
                GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        Mk48Mag = (new ItemMagazine.Builder()).withAmmo(100)
            .withCompatibleBullet(Bullets.Bullet762x51)
            .withName("Mk48Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mk48mod1mag"), "Mk48MOD1.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.4F, 0.4F);
                GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        MXMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet65x39)
            .withName("MXMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mxmag"), "MXMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        M41AMag = (new ItemMagazine.Builder()).withAmmo(99)
            .withCompatibleBullet(Bullets.Bullet65x39)
            .withName("M41AMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m41amag"), "M41AMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.1F, -1.0F, 0.2F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.2F, 0.6F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, -0.6F, 0.8F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.3D, 1.3D, 1.3D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        ScarHMag = (new ItemMagazine.Builder()).withAmmo(30)
            .withCompatibleBullet(Bullets.Bullet300Blackout)
            .withName("ScarHMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/scarhmag"), "GunmetalTexture.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.1D, 1.1D, 1.1D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        PP19Mag = (new ItemMagazine.Builder()).withAmmo(65)
            .withCompatibleBullet(Bullets.Bullet9mm)
            .withName("PP19Mag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/ppbizonmag"), "AK12.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.3F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.5F, 0.1F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(0.3F, 0.7F, -1.2F);
                GL11.glRotatef(-170.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);

        // ---- High-tier magazines and cells (LuV-UHV) ----
        CoilgunMag = (new ItemMagazine.Builder()).withAmmo(50)
            .withCompatibleBullet(Bullets.FerroSlug)
            .withName("CoilgunMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/p90mag"), "CoilgunMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.3F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.3F, 0.5F, 0.1F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(0.3F, 1.2F, 0.4F);
                GL11.glRotatef(-170.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        GaussRifleMag = (new ItemMagazine.Builder()).withAmmo(8)
            .withCompatibleBullet(Bullets.FerroSlug)
            .withName("GaussRifleMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/as50mag"), "GaussRifleMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        RailgunMag = (new ItemMagazine.Builder()).withAmmo(6)
            .withCompatibleBullet(Bullets.FerroSlug)
            .withName("RailgunMag")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m107mag"), "RailgunMag.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, -0.1F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        LaserRifleCell = (new ItemMagazine.Builder()).withAmmo(60)
            .withCompatibleBullet(Bullets.LaserCharge)
            .withName("LaserRifleCell")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m8a7mag"), "LaserRifleCell.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.8F, -1.0F, 0.9F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.5D, 0.5D, 0.5D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.8F, 0.4F, -0.8F);
                GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withTextureName("Dummy.png")

            .build(ItemMagazine.class);
        PlasmaRifleCell = (new ItemMagazine.Builder()).withAmmo(80)
            .withCompatibleBullet(Bullets.LaserCharge)
            .withName("PlasmaRifleCell")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m41amag"), "PlasmaRifleCell.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.1F, -1.0F, 0.2F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-0.8F, -0.2F, 0.6F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, -0.6F, 0.8F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.3D, 1.3D, 1.3D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        ParticleBeamCell = (new ItemMagazine.Builder()).withAmmo(10)
            .withCompatibleBullet(Bullets.ParticleCharge)
            .withName("ParticleBeamCell")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/as50mag"), "ParticleBeamCell.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.6D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.6D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.9F, 0.2F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 0.9D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        NeutronRifleCell = (new ItemMagazine.Builder()).withAmmo(150)
            .withCompatibleBullet(Bullets.ParticleCharge)
            .withName("NeutronRifleCell")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/m249mag"), "NeutronRifleCell.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.2F, 0.4F, 0.4F);
                GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
        ExoticMatterCell = (new ItemMagazine.Builder()).withAmmo(40)
            .withCompatibleBullet(Bullets.ExoticCharge)
            .withName("ExoticMatterCell")
            .withCreativeTab(NewGunrizonsMod.AmmoTab)
            .withModel(new JsonModel("magazine/mxmag"), "ExoticMatterCell.png")
            .withFirstPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(0.1F, -0.7F, 0.4F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withThirdPersonPositioning((player, itemStack) -> {
                GL11.glTranslatef(-1.0F, -0.5F, 0.8F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withInventoryPositioning((itemStack) -> {
                GL11.glTranslatef(-0.4F, 0.0F, 0.4F);
                GL11.glRotatef(-130.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })

            .withTextureName("Dummy.png")
            .build(ItemMagazine.class);
    }
}
