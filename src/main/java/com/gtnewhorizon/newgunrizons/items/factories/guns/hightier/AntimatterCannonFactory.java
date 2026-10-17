package com.gtnewhorizon.newgunrizons.items.factories.guns.hightier;

import java.util.Arrays;

import net.minecraft.item.Item;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.animation.Transition;
import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.registry.Attachments;
import com.gtnewhorizon.newgunrizons.registry.Bullets;

public class AntimatterCannonFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("AntimatterCannon")
            .withAmmoCapacity(1)
            .withFireRate(0.5F)
            .withRecoil(2.0F)

            .withMaxShots(1)
            .withShootSound("SMAW")
            .withReloadSound("RocketLauncherReload")
            .withReloadingTime(70L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withShellCasingEjectEnabled(false)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: UHV",
                    "Type: Antimatter cannon",
                    "Damage: 400",
                    "Blast radius: 5",
                    "Ammo: Antimatter Charge",
                    "Fire Rate: Semi"))
            .withCompatibleAttachment(Attachments.AKMIron, true, (model) -> {
                if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(0.41F, -2.13F, 1.1F);
                    GL11.glScaled(0.3D, 0.3D, 0.3D);
                } else if (JsonModel.is(model, "sight/m4iron2")) {
                    GL11.glTranslatef(0.26F, -2.0F, -2.22F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/p90iron")) {
                    GL11.glTranslatef(0.26F, -1.55F, -2.35F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron1")) {
                    GL11.glTranslatef(0.125F, -1.8F, -0.5F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron2")) {
                    GL11.glTranslatef(0.13F, -1.55F, -3.05F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/ak47iron")) {
                    GL11.glTranslatef(0.092F, -1.91F, -0.9F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron1")) {
                    GL11.glTranslatef(-0.22F, -1.94F, 0.13F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron2")) {
                    GL11.glTranslatef(-0.205F, -1.9F, -3.15F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.165F, -1.65F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.55F, -2.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/faliron")) {
                    GL11.glTranslatef(0.36F, -2.1F, -2.0F);
                    GL11.glScaled(0.5D, 0.5D, 1.0D);
                } else if (JsonModel.is(model, "sight/m14iron")) {
                    GL11.glTranslatef(0.129F, -1.63F, -2.08F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/mp5iron")) {
                    GL11.glTranslatef(0.215F, -1.54F, 1.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/mbusiron")) {
                    GL11.glTranslatef(0.215F, -1.54F, 1.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                }

            })
            .withCompatibleAttachment(Attachments.ACOG, (player, stack) -> {
                GL11.glTranslatef(0.4F, -2.0F, 1.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/acog2")) {
                    GL11.glTranslatef(0.237F, -0.26F, 0.46F);
                    GL11.glScaled(0.06D, 0.06D, 0.06D);
                }

            })
            .withCompatibleAttachment(Bullets.AntimatterCharge, ((model) -> { GL11.glTranslatef(0.0F, 0.0F, 0.07F); }))
            .withTextureName("AntimatterCannon")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/antimattercannon"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.3D, 0.3D, 0.3D);
                        GL11.glTranslatef(1.0F, 0.0F, 1.9F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-1.8F, 0.3F, 1.5F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glTranslatef(0.1F, -0.2F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -1.0F, 0.9F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.1F, -0.2F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -1.0F, 1.3F);
                        GL11.glRotatef(-4.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.1F, 0.2F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.1F, -0.1F, 2.0F);
                        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
                    }, 600L, 4000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.58F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.753F, -1.0F, 2.5F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.09F, -0.03F, -0.13F);
                        }

                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.58F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.753F, -1.0F, 2.53F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.09F, -0.03F, -0.13F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(0.7D, 0.7D, 0.7D);
                        GL11.glRotatef(-20.0F, -4.0F, 1.0F, -2.0F);
                        GL11.glTranslatef(1.0F, -0.5F, -0.8F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-35.0F, 2.0F, 1.0F, 1.0F);
                        GL11.glTranslatef(1.0F, -0.8F, -1.5F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 3.0F);
                        GL11.glTranslatef(0.85F, -0.1F, 0.35F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.1F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(0.85F, -0.0F, 0.18F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.1F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 3.0F);
                        GL11.glTranslatef(0.85F, -0.1F, 0.35F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.1F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L))
                    .build())
            .withSpawnEntitySpeed(5.0F)
            .withSpawnEntityDamage(400.0F)
            .withSpawnEntityExplosionRadius(5.0F)
            .build();
    }
}
