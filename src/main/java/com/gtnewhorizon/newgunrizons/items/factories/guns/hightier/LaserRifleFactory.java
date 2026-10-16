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
import com.gtnewhorizon.newgunrizons.registry.AuxiliaryAttachments;
import com.gtnewhorizon.newgunrizons.registry.Magazines;

public class LaserRifleFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("LaserRifle")
            .withFireRate(0.9F)
            .withRecoil(2.4F)

            .withMaxShots(4)
            .withShootSound("M8A7")
            .withSilencedShootSound("MP5Silenced")
            .withReloadSound("StandardReload")
            .withUnloadSound("Unload")
            .withReloadingTime(43L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> 0.0F)
            .withFlashOffsetX(() -> 0.0F)
            .withFlashOffsetY(() -> 0.0F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: ZPM",
                    "Type: Directed-energy rifle",
                    "Damage: 32",
                    "Ammo: 60-shot Laser Rifle Cell",
                    "Fire Rate: Auto"))
            .withCompatibleAttachment(Magazines.LaserRifleCell, ((model) -> { GL11.glTranslatef(0.0F, -0.05F, 0.0F); }))
            .withCompatibleAttachment(AuxiliaryAttachments.Extra, true, (model) -> {
                if (JsonModel.is(model, "sight/g36ciron1")) {
                    GL11.glTranslatef(-0.17F, -1.155F, -0.1F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron2")) {
                    GL11.glTranslatef(-0.16F, -1.15F, -3.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron1")) {
                    GL11.glTranslatef(-0.157F, -1.425F, -1.57F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron2")) {
                    GL11.glTranslatef(0.13F, -1.55F, -3.05F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/ak47iron")) {
                    GL11.glTranslatef(0.092F, -1.91F, -0.9F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(-0.144F, -1.61F, 0.0F);
                    GL11.glScaled(0.2D, 0.2D, 0.2D);
                } else if (JsonModel.is(model, "sight/m4iron2")) {
                    GL11.glTranslatef(0.26F, -1.55F, -2.35F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/p90iron")) {
                    GL11.glTranslatef(0.26F, -1.55F, -2.35F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.165F, -1.65F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.55F, -2.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/faliron")) {
                    GL11.glTranslatef(-0.164F, -1.6F, -3.85F);
                    GL11.glScaled(0.4D, 0.4D, 0.4D);
                } else if (JsonModel.is(model, "sight/m14iron")) {
                    GL11.glTranslatef(0.129F, -1.63F, -2.08F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/mp5iron")) {
                    GL11.glTranslatef(-0.1F, -1.235F, -0.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                }

            })
            .withCompatibleAttachment(Attachments.Grip2, (model) -> {
                GL11.glTranslatef(-0.2F, -0.1F, -2.9F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.VGrip, (model) -> {
                GL11.glTranslatef(-0.2F, -0.1F, -2.9F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.Bipod, (model) -> {
                GL11.glTranslatef(-0.2F, -0.1F, -2.9F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.StubbyGrip, (model) -> {
                GL11.glTranslatef(-0.2F, -0.1F, -2.9F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.Silencer300AACBlackout, (model) -> {
                GL11.glTranslatef(-0.2F, -1.11F, -6.8F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("LaserRifle")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/m8a7"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.45D, 0.45D, 0.45D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.25D, 0.25D, 0.25D);
                        GL11.glTranslatef(1.0F, 2.0F, -1.4F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-1.9F, -1.0F, 2.1F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 0.9F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 1.2F);
                        GL11.glRotatef(-2.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.07F, -0.39F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-0.2F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(0.315F, -1.17F, 2.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.PSO1)) {
                            GL11.glTranslatef(1.333F, -1.25F, 2.15F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonCustomPositioning(Magazines.LaserRifleCell, (renderContext) -> {})
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.7F, -0.5F, -0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 250L, 500L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.7F, -0.5F, -0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 250L, 20L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 0.9F);
                    }, 350L, 60L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 0.9F);
                    }, 100L, 0L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 0.9F);
                    }, 100L, 0L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.32F, 0.15F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glTranslatef(-0.4F, -0.7F, 0.9F);
                    }, 250L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.7F, -0.5F, -0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.7F, -0.5F, -0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L))
                    .withFirstPersonCustomPositioningUnloading(Magazines.LaserRifleCell, new Transition((renderContext) -> {
                        GL11.glTranslatef(0.0F, 0.5F, -0.2F);
                        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glTranslatef(1.3F, 0.5F, -0.8F);
                        GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                    }, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Magazines.LaserRifleCell,
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glTranslatef(0.07F, -0.39F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(0.315F, -1.17F, 1.9F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.PSO1)) {
                            GL11.glTranslatef(1.333F, -1.25F, 2.15F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(0.9D, 0.9D, 0.9D);
                        GL11.glRotatef(-20.0F, -4.0F, 1.0F, -2.0F);
                        GL11.glTranslatef(0.3F, -0.25F, -0.1F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glRotatef(-35.0F, 2.0F, 1.0F, 1.0F);
                        GL11.glTranslatef(1.3F, -1.2F, -0.2F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(3.2F, 3.2F, 4.6F);
                        GL11.glTranslatef(0.5F, 0.08F, -0.1F);
                        GL11.glRotatef(115.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(30.0F, 1.0F, 1.0F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glTranslatef(0.9F, 0.5F, -0.4F);
                        GL11.glRotatef(99.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-60.0F, 20.0F, 20.0F, -20.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(2.3F, 2.3F, 2.5F);
                        GL11.glTranslatef(0.3F, 0.8F, 0.3F);
                        GL11.glRotatef(30.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(30.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(2.3F, 2.3F, 2.5F);
                        GL11.glTranslatef(0.3F, 0.6F, 0.3F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(2.4F, 2.4F, 3.7F);
                        GL11.glTranslatef(0.7F, 0.25F, 0.2F);
                        GL11.glRotatef(160.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(50.0F, 1.0F, 1.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.4F, 2.4F, 3.7F);
                        GL11.glTranslatef(0.73F, 0.25F, 0.2F);
                        GL11.glRotatef(160.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(50.0F, 1.0F, 1.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.4F, 2.4F, 3.7F);
                        GL11.glTranslatef(0.73F, 0.25F, 0.2F);
                        GL11.glRotatef(160.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(50.0F, 1.0F, 1.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.4F, 2.4F, 3.7F);
                        GL11.glTranslatef(0.7F, 0.25F, 0.2F);
                        GL11.glRotatef(160.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(50.0F, 1.0F, 1.0F, 0.0F);
                    }, 250L, 0L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L))
                    .withFirstPersonLeftHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.2F, 0.37F);
                        GL11.glRotatef(70.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.7F, 0.0F, 0.37F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(2.8F, 2.8F, 3.3F);
                        GL11.glTranslatef(-0.15F, 0.3F, 0.45F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(32.0F)
            .withSpawnEntityGravityVelocity(0.028F)
            .build();
    }
}
