package com.gtnewhorizon.newgunrizons.items.factories.guns.hightier;

import java.util.Arrays;

import net.minecraft.item.Item;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.animation.Transition;
import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.registry.Attachments;
import com.gtnewhorizon.newgunrizons.registry.AuxiliaryAttachments;
import com.gtnewhorizon.newgunrizons.registry.Magazines;

public class QuantumPhaseRifleFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("QuantumPhaseRifle")
            .withProjectile(ProjectileStyle.PHASE, 0x505AFF, 1.0F)
            .withBehavior(
                new WeaponBehavior().phase(12)
                    .armorPiercing(0.6F))
            .withShellCasingEjectEnabled(false)
            .withFireRate(0.075F)
            .withRecoil(3.5F)

            .withMaxShots(1)
            .withShootSound("aug")
            .withSilencedShootSound("AR15Silenced")
            .withReloadSound("StandardReload")
            .withUnloadSound("Unload")
            .withReloadingTime(50L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> 1.2F)
            .withFlashOffsetX(() -> 0F)
            .withFlashOffsetY(() -> 0F)
            .withShellCasingForwardOffset(-0.1F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: UEV",
                    "Type: Quantum phase rifle",
                    "Damage: 350",
                    "Phases through up to 12 blocks",
                    "Armor penetration 60%",
                    "Ammo: Quantum Cell",
                    "Cells recharge in a UEV Battery Buffer"))
            .withCompatibleAttachment(AuxiliaryAttachments.AUGAction, true, (model) -> {})
            .withCompatibleAttachment(Magazines.QuantumCell, ((model) -> {
                GL11.glTranslatef(-0.335F, 0.9F, 1.2F);
                GL11.glScaled(1.0D, 1.5D, 1.5D);
                GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
            }))
            .withCompatibleAttachment(Attachments.AKMIron, true, (model) -> {
                if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(-0.16F, -1.5F, -0.3F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m4iron2")) {
                    GL11.glTranslatef(0.255F, -1.55F, -2.25F);
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
                    GL11.glTranslatef(-0.248F, -1.58F, 0.3F);
                    GL11.glScaled(0.8D, 0.7D, 0.3D);
                } else if (JsonModel.is(model, "sight/g36ciron1")) {
                    GL11.glTranslatef(-0.22F, -1.94F, 0.13F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron2")) {
                    GL11.glTranslatef(-0.18F, -1.26F, -2.9F);
                    GL11.glScaled(0.35D, 0.35D, 0.35D);
                } else if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.165F, -1.65F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.55F, -2.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/faliron")) {
                    GL11.glTranslatef(0.127F, -1.77F, -2.22F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
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
            .withCompatibleAttachment(AuxiliaryAttachments.AUGRail, true, (model) -> {
                if (JsonModel.is(model, "misc/akrail")) {
                    GL11.glTranslatef(0.16F, -0.88F, -3.5F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                } else if (JsonModel.is(model, "misc/akrail2")) {
                    GL11.glTranslatef(-0.39F, -0.715F, -3.5F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                } else if (JsonModel.is(model, "misc/akrail3")) {
                    GL11.glTranslatef(-0.03F, -0.52F, -3.5F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                } else if (JsonModel.is(model, "misc/akrail4")) {
                    GL11.glTranslatef(-0.23F, -1.26F, -2.9F);
                    GL11.glScaled(0.8D, 1.0D, 1.4D);
                }

            })
            .withCompatibleAttachment(Attachments.ACOG, (player, stack) -> {
                GL11.glTranslatef(-0.28F, -1.37F, -0.3F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/acogscope2")) {
                    GL11.glTranslatef(-0.018F, -0.25F, 0.13F);
                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                } else if (JsonModel.is(model, "sight/acogreticle")) {
                    GL11.glTranslatef(0.243F, -0.23F, 0.68F);
                    GL11.glScaled(0.03D, 0.03D, 0.03D);
                }

            })
            .withCompatibleAttachment(Attachments.MicroT1, (model) -> {
                if (JsonModel.is(model, "sight/microt1")) {
                    GL11.glTranslatef(-0.17F, -1.35F, -0.1F);
                    GL11.glScaled(0.3D, 0.3D, 0.3D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(-0.125F, -1.46F, -0.1F);
                    GL11.glScaled(0.07D, 0.07D, 0.07D);
                }

            })
            .withCompatibleAttachment(Attachments.Specter, (player, stack) -> {
                GL11.glTranslatef(-0.17F, -1.08F, -0.2F);
                GL11.glScaled(0.35D, 0.35D, 0.35D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/acog2")) {
                    GL11.glTranslatef(0.15F, -1.035F, 1.513F);
                    GL11.glScaled(0.1D, 0.1D, 0.1D);
                }

            })
            .withCompatibleAttachment(Attachments.Scope, (player, stack) -> {
                GL11.glTranslatef(-0.31F, -1.33F, -0.3F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.272F, 0.67F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.Reflex, (model) -> {
                if (JsonModel.is(model, "sight/reflex")) {
                    GL11.glTranslatef(-0.072F, -1.17F, -0.1F);
                    GL11.glScaled(0.4D, 0.4D, 0.4D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(-0.125F, -1.44F, -0.2F);
                    GL11.glScaled(0.07D, 0.07D, 0.07D);
                }

            })
            .withCompatibleAttachment(Attachments.Holo2, (model) -> {
                if (JsonModel.is(model, "sight/holographic")) {
                    GL11.glTranslatef(-0.053F, -1.22F, -0.0F);
                    GL11.glScaled(0.55D, 0.55D, 0.55D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(-0.12F, -1.49F, -0.0F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.Holographic2, (model) -> {
                if (JsonModel.is(model, "sight/holographic2")) {
                    GL11.glTranslatef(-0.053F, -1.22F, -0.0F);
                    GL11.glScaled(0.55D, 0.55D, 0.55D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(-0.12F, -1.49F, -0.0F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.Kobra, (model) -> {
                if (JsonModel.is(model, "sight/kobra")) {
                    GL11.glTranslatef(-0.053F, -1.2F, -0.0F);
                    GL11.glScaled(0.55D, 0.55D, 0.55D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(-0.12F, -1.45F, -0.5F);
                    GL11.glScaled(0.07D, 0.07D, 0.07D);
                }

            })
            .withCompatibleAttachment(Attachments.Grip2, (model) -> {
                GL11.glTranslatef(-0.17F, -0.1F, -2.8F);
                GL11.glScaled(0.8D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.StubbyGrip, (model) -> {
                GL11.glTranslatef(-0.17F, -0.2F, -2.8F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.VGrip, (model) -> {
                GL11.glTranslatef(-0.17F, -0.1F, -2.8F);
                GL11.glScaled(0.8D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.Bipod, (model) -> {
                GL11.glTranslatef(-0.17F, -0.1F, -3.2F);
                GL11.glScaled(0.8D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.Laser2, (p, s) -> {
                GL11.glTranslatef(-0.17F, -0.2F, -2.8F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Laser, (p, s) -> {
                GL11.glTranslatef(0.05F, -0.9F, -2.8F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withCompatibleAttachment(Attachments.Silencer556x45, (model) -> {
                GL11.glTranslatef(-0.2F, -0.97F, -5.12F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("QuantumPhaseRifle")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/quantumphaserifle"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.35D, 0.35D, 0.35D);
                        GL11.glTranslatef(0.0F, 0.8F, 0.0F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-1.5F, -0.9F, 1.7F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glTranslatef(-0.4F, 0.65F, -1.5F);
                        GL11.glRotatef(5.0F, 0.0F, 0.0F, 5.0F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glTranslatef(-0.4F, 0.65F, -1.4F);
                        GL11.glRotatef(5.0F, 0.0F, 0.0F, 5.0F);
                        GL11.glRotatef(-1.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glTranslatef(0.465F, 0.44F, -0.9F);
                        GL11.glRotatef(-0.3F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.005F, 0.245F, 1.3F);
                        } else
                            if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Specter)) {
                                GL11.glTranslatef(-0.005F, 0.13F, 1.1F);
                            } else if (ItemWeapon
                                .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                                    GL11.glTranslatef(-0.005F, 0.25F, 1.1F);
                                } else if (ItemWeapon
                                    .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                                        GL11.glTranslatef(0.0F, 0.17F, 0.3F);
                                    } else if (ItemWeapon
                                        .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                                            GL11.glTranslatef(0.0F, 0.19F, 0.3F);
                                        } else if (ItemWeapon.isActiveAttachment(
                                            renderContext.getWeaponInstance(),
                                            Attachments.Holographic2)) {
                                                GL11.glTranslatef(0.0F, 0.19F, 0.3F);
                                            } else if (ItemWeapon.isActiveAttachment(
                                                renderContext.getWeaponInstance(),
                                                Attachments.Kobra)) {
                                                    GL11.glTranslatef(0.0F, 0.17F, 0.3F);
                                                } else if (ItemWeapon.isActiveAttachment(
                                                    renderContext.getWeaponInstance(),
                                                    Attachments.MicroT1)) {
                                                        GL11.glTranslatef(1.373F, -1.14F, 2.8F);
                                                    }

                    })
                    .withFirstPersonCustomPositioning(Magazines.QuantumCell, (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.AUGAction.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.AUGRail.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomRecoiled(
                        AuxiliaryAttachments.AUGAction.getRenderablePart(),
                        (renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 1.0F); })
                    .withFirstPersonPositioningCustomZoomingRecoiled(
                        AuxiliaryAttachments.AUGAction.getRenderablePart(),
                        (renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 1.0F); })
                    .withFirstPersonPositioningCustomRecoiled(
                        Magazines.QuantumCell.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomZoomingRecoiled(
                        Magazines.QuantumCell.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomZoomingRecoiled(
                        AuxiliaryAttachments.AUGRail.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningCustomRecoiled(
                        AuxiliaryAttachments.AUGRail.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-65.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.15F, 0.825F, -0.875F);
                    }, 350L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-68.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.15F, 0.825F, -0.875F);
                    }, 300L, 100L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-58.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.15F, 0.825F, -0.875F);
                    }, 100L, 130L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.4F, 0.7F, -0.7F);
                    }, 400L, 100L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.4F, 0.7F, -0.6F);
                    }, 200L, 70L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.4F, 0.7F, -0.7F);
                    }, 180L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-58.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.15F, 0.825F, -0.875F);
                    }, 150L, 50L), new Transition((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-65.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.15F, 0.825F, -0.875F);
                    }, 150L, 50L))
                    .withFirstPersonCustomPositioningUnloading(
                        Magazines.QuantumCell,
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.2F, 0.5F, -0.2F);
                            GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.2F, 0.9F, -0.2F);
                            GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Magazines.QuantumCell,
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.3F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        AuxiliaryAttachments.AUGRail.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.AUGRail.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        AuxiliaryAttachments.AUGAction.getRenderablePart(),
                        new Transition((renderContext) -> {}, 500L, 1000L),
                        new Transition((renderContext) -> {}, 500L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.AUGAction.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 1.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glTranslatef(0.465F, 0.44F, -1.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.005F, 0.245F, 1.35F);
                        } else
                            if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Specter)) {
                                GL11.glTranslatef(-0.005F, 0.13F, 1.1F);
                            } else if (ItemWeapon
                                .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                                    GL11.glTranslatef(-0.005F, 0.25F, 1.1F);
                                } else if (ItemWeapon
                                    .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                                        GL11.glTranslatef(0.0F, 0.17F, 0.3F);
                                    } else if (ItemWeapon
                                        .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                                            GL11.glTranslatef(0.0F, 0.19F, 0.3F);
                                        } else if (ItemWeapon.isActiveAttachment(
                                            renderContext.getWeaponInstance(),
                                            Attachments.Holographic2)) {
                                                GL11.glTranslatef(0.0F, 0.19F, 0.3F);
                                            } else if (ItemWeapon.isActiveAttachment(
                                                renderContext.getWeaponInstance(),
                                                Attachments.Kobra)) {
                                                    GL11.glTranslatef(0.0F, 0.17F, 0.3F);
                                                } else if (ItemWeapon.isActiveAttachment(
                                                    renderContext.getWeaponInstance(),
                                                    Attachments.MicroT1)) {
                                                        GL11.glTranslatef(1.373F, -1.14F, 2.8F);
                                                    }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.5F, 0.9F, -0.4F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(2.0D, 2.0D, 2.0D);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.9F, 0.35F, -0.45F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.075F, -0.525F, 0.45F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.35F, -0.5F, 0.12F);
                    })
                    .withFirstPersonHandPositioningRunning((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.075F, -0.525F, 0.45F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.4F, -0.5F, 0.12F);
                    })
                    .withFirstPersonHandPositioningZooming((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.075F, -0.525F, 0.45F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.35F, -0.5F, 0.12F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.075F, -0.525F, 0.45F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.35F, -0.5F, 0.12F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.825F, -0.9F, 0.7F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.85F, -0.925F, 0.375F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.85F, -0.925F, 0.375F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-110.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.1F, -0.575F, 0.3F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-115.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.725F, 0.15F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 5.0F);
                        GL11.glRotatef(-110.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.1F, -0.575F, 0.3F);
                    }, 250L, 0L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.45F, -0.475F, 0.1F);
                    }, 250L, 0L))
                    .withFirstPersonLeftHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.85F, -0.925F, 0.375F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.825F, -0.9F, 0.7F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.4F, -0.5F, 0.12F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.5F, 3.5F, 4.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.4F, -0.5F, 0.12F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(350.0F)
            .withSpawnEntityGravityVelocity(0.0F)
            .build();
    }
}
