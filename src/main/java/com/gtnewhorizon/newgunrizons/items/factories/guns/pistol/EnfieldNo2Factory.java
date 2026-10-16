package com.gtnewhorizon.newgunrizons.items.factories.guns.pistol;

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
import com.gtnewhorizon.newgunrizons.registry.Bullets;

public class EnfieldNo2Factory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("EnfieldNo2")
            .withAmmoCapacity(6)
            .withMaxBulletsPerReload(6)
            .withFireRate(0.3F)
            .withIteratedLoad()
            .withRecoil(9.0F)

            .withMaxShots(1)
            .withPumpTimeout(850L)
            .withShootSound("webley")
            .withAllReloadIterationsCompletedSound("webleyclose")
            .withReloadSound("webleyopen")
            .withReloadIterationSound("revolverload")
            .withReloadingTime(1000L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withShellCasingEjectEnabled(false)
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.5F)
            .withFlashOffsetZ(() -> 0.7F)
            .withFlashOffsetX(() -> 0.0F)
            .withFlashOffsetY(() -> -0.6F)
            .withCreativeTab(NewGunrizonsMod.PistolsTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Type: Service Revolver",
                    "Damage: 7",
                    "Cartridge: .380/200 Bullet",
                    "Fire Rate: Double Action Revolver"))
            .withCompatibleAttachment(
                AuxiliaryAttachments.WebleyCylinder,
                true,
                (model) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); })
            .withCompatibleAttachment(
                AuxiliaryAttachments.WebleyBullets,
                true,
                (model) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); })
            .withCompatibleBullet(Bullets.Bullet380200, (model) -> {})
            .withTextureName("EnfieldNo2")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/webley"))
                    .withPrepareFirstLoadIterationAnimationDuration(900)
                    .withAllLoadIterationAnimationsCompletedDuration(400)
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.35D, 0.35D, 0.35D);
                        GL11.glTranslatef(1.0F, 0.8F, 0.0F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.25D, 0.25D, 0.25D);
                        GL11.glTranslatef(-3.5F, -2.0F, 3.5F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningLoadIterationCompleted((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-0.75F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(8.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.15F, 1.5F, -5.0F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.15F, 1.8F, -4.5F);
                        GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(0.43F, 1.1F, -4.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Leupold)) {
                            GL11.glTranslatef(0.0F, 0.185F, 0.45F);
                        }

                    })
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomRecoiled(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomRecoiled(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomZoomingRecoiled(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomZoomingRecoiled(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioningLoadIterationCompleted(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        (renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        })
                    .withFirstPersonCustomPositioningLoadIterationCompleted(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        (renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        })
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 300L, 100L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 100L, 0L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 300L, 0L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 280L, 0L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 290L, 0L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 270L, 0L))
                    .withFirstPersonCustomPositioningAllLoadIterationsCompleted(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 150L, 50L),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 150L, 50L))
                    .withFirstPersonCustomPositioningLoadIteration(
                        AuxiliaryAttachments.WebleyCylinder.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 170L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L))
                    .withFirstPersonCustomPositioningAllLoadIterationsCompleted(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.6F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 150L, 50L),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 150L, 50L),
                        new Transition((renderContext) -> { GL11.glScalef(1.0F, 1.0F, 1.0F); }, 150L, 50L))
                    .withFirstPersonCustomPositioningLoadIteration(
                        AuxiliaryAttachments.WebleyBullets.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -15.0F, -0.45F);
                            GL11.glRotatef(65.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 50L))
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.2F, 1.8F, -3.0F);
                        GL11.glRotatef(-35.0F, 1.0F, 0.0F, 0.0F);
                    }, 300L, 100L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                    }, 100L, 150L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                    }, 270L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 260L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 240L, 0L))
                    .withFirstPersonPositioningAllLoadIterationsCompleted(new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-0.7F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 10L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 300L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.0F, 2.3F, -3.0F);
                        GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                    }, 280L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-1.2F, 1.8F, -3.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                    }, 100L, 150L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-2.0F, 1.8F, -4.0F);
                        GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                    }, 300L, 0L))
                    .withFirstPersonPositioningLoadIteration(new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-0.8F, 2.5F, -3.0F);
                        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                    }, 300L, 0L), new Transition((renderContext) -> {
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(23.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(-0.9F, 2.3F, -3.0F);
                        GL11.glRotatef(-23.0F, 1.0F, 0.0F, 0.0F);
                    }, 290L, 0L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glTranslatef(0.43F, 1.1F, -5.0F);
                        GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Leupold)) {
                            GL11.glTranslatef(0.0F, 0.185F, 0.45F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glRotatef(-65.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.425F, 3.6F, -1.125F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.0F);
                        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-3.3F, 0.1F, -0.5F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.275F, -0.7F, 0.225F);
                    }, (renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    })
                    .withFirstPersonHandPositioningLoadIterationCompleted((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.55F, -0.2F);
                    }, (renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    })
                    .withFirstPersonHandPositioningZooming((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.275F, -0.7F, 0.225F);
                    }, (renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-55.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(65.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.95F, 0.25F);
                    }, (renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.475F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.475F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.475F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-35.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.575F, 0.2F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.05F, -0.725F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.55F, -0.2F);
                    }, 250L, 50L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L))
                    .withFirstPersonLeftHandPositioningLoadIteration(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-65.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.55F, -0.175F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.55F, -0.2F);
                    }, 250L, 50L))
                    .withFirstPersonRightHandPositioningLoadIteration(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L))
                    .withFirstPersonLeftHandPositioningAllLoadIterationsCompleted(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.55F, -0.2F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-75.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.65F, -0.025F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.475F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.5F, -0.075F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-55.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.3F, -0.725F, -0.375F);
                    }, 250L, 50L))
                    .withFirstPersonRightHandPositioningAllLoadIterationsCompleted(new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(7.0F, 7.0F, 7.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.425F, -0.575F, 0.177F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(7.0F)
            .withSpawnEntityGravityVelocity(0.0F)
            .build();
    }
}
