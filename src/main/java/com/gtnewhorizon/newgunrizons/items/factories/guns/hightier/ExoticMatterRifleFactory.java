package com.gtnewhorizon.newgunrizons.items.factories.guns.hightier;

import java.util.Arrays;

import net.minecraft.item.Item;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.animation.Transition;
import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.registry.Attachments;
import com.gtnewhorizon.newgunrizons.registry.Magazines;

public class ExoticMatterRifleFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("ExoticMatterRifle")
            .withProjectile(ProjectileStyle.SHARDS, 0xFF4034, 1.0F)
            .withFireRate(0.55F)
            .withRecoil(3.0F)

            .withMaxShots(Integer.MAX_VALUE, 3, 1)
            .withShootSound("AR15")
            .withSilencedShootSound("AR15Silenced")
            .withReloadSound("StandardReload")
            .withUnloadSound("Unload")
            .withReloadingTime(50L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> 0.7F)
            .withFlashOffsetX(() -> 0.3F)
            .withFlashOffsetY(() -> 0.0F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: UHV",
                    "Type: Exotic matter rifle",
                    "Damage: 90",
                    "Ammo: 40-shot Exotic Matter Cell",
                    "Fire Rate: Auto/Burst/Semi"))
            .withCompatibleAttachment(
                Magazines.ExoticMatterCell,
                ((model) -> { GL11.glTranslatef(0.0F, 0.0F, 0.07F); }))
            .withCompatibleAttachment(Attachments.AKMIron, true, (model) -> {
                if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.17F, -1.61F, 1.3F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.52F, -2.3F);
                    GL11.glScaled(0.52D, 0.52D, 0.52D);
                } else if (JsonModel.is(model, "sight/akmiron1")) {
                    GL11.glTranslatef(0.14F, -1.566F, 0.3F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron2")) {
                    GL11.glTranslatef(0.13F, -1.39F, -2.8F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/ak47iron")) {
                    GL11.glTranslatef(0.13F, -1.55F, -3.05F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(0.167F, -1.62F, 0.8F);
                    GL11.glScaled(0.26D, 0.26D, 0.26D);
                } else if (JsonModel.is(model, "sight/m4iron2")) {
                    GL11.glTranslatef(0.26F, -1.55F, -2.35F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/p90iron")) {
                    GL11.glTranslatef(0.26F, -1.55F, -2.35F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron1")) {
                    GL11.glTranslatef(-0.22F, -1.94F, 0.13F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron2")) {
                    GL11.glTranslatef(-0.205F, -1.9F, -3.15F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/faliron")) {
                    GL11.glTranslatef(0.129F, -1.63F, -2.08F);
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
            .withCompatibleAttachment(Attachments.ACOG, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.58F, 0.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/acogscope2")) {
                    GL11.glTranslatef(-0.018F, -0.25F, 0.13F);
                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                } else if (JsonModel.is(model, "sight/acogreticle")) {
                    GL11.glTranslatef(0.243F, -0.23F, 0.68F);
                    GL11.glScaled(0.03D, 0.03D, 0.03D);
                }

            })
            .withCompatibleAttachment(Attachments.Scope, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.56F, 0.4F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.272F, 0.67F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.HP, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.58F, 0.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.235F, 1.16F);
                    GL11.glScaled(0.1D, 0.1D, 0.1D);
                }

            })
            .withCompatibleAttachment(Attachments.Reflex, (model) -> {
                if (JsonModel.is(model, "sight/reflex")) {
                    GL11.glTranslatef(0.27F, -1.4F, 0.0F);
                    GL11.glScaled(0.5D, 0.5D, 0.5D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.202F, -1.65F, 0.0F);
                    GL11.glScaled(0.2D, 0.2D, 0.2D);
                }

            })
            .withCompatibleAttachment(Attachments.Kobra, (model) -> {
                if (JsonModel.is(model, "sight/kobra")) {
                    GL11.glTranslatef(0.29F, -1.45F, 0.5F);
                    GL11.glScaled(0.7D, 0.7D, 0.7D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.202F, -1.67F, -0.15F);
                    GL11.glScaled(0.2D, 0.2D, 0.2D);
                }

            })
            .withCompatibleAttachment(Attachments.Holo2, (model) -> {
                if (JsonModel.is(model, "sight/holographic")) {
                    GL11.glTranslatef(0.27F, -1.48F, 0.21F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(0.202F, -1.77F, 0.3F);
                    GL11.glScaled(0.06D, 0.06D, 0.06D);
                }

            })
            .withCompatibleAttachment(Attachments.Holographic2, (model) -> {
                if (JsonModel.is(model, "sight/holographic2")) {
                    GL11.glTranslatef(0.27F, -1.48F, 0.21F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(0.202F, -1.77F, 0.3F);
                    GL11.glScaled(0.06D, 0.06D, 0.06D);
                }

            })
            .withCompatibleAttachment(Attachments.Grip2, (model) -> {
                GL11.glTranslatef(0.135F, -0.6F, -1.25F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.StubbyGrip, (model) -> {
                GL11.glTranslatef(0.135F, -0.6F, -1.25F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Grip, (model) -> {
                GL11.glTranslatef(0.135F, -0.5F, -1.25F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.VGrip, (model) -> {
                GL11.glTranslatef(0.135F, -0.6F, -1.25F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Bipod, (model) -> {
                GL11.glTranslatef(0.135F, -0.5F, -1.25F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Laser2, (p, s) -> {
                GL11.glTranslatef(0.35F, -1.2F, -2.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withCompatibleAttachment(Attachments.Laser, (p, s) -> {
                GL11.glTranslatef(0.35F, -1.2F, -2.0F);
                GL11.glScaled(0.7D, 0.7D, 0.7D);
            })
            .withCompatibleAttachment(Attachments.Silencer65x39, (model) -> {
                GL11.glTranslatef(0.107F, -1.35F, -4.2F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("ExoticMatterRifle")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/exoticmatterrifle"))
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
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-1.8F, 0.3F, 1.5F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glTranslatef(0.3F, -0.3F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.3F, -0.3F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                        GL11.glRotatef(-3.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glTranslatef(-0.047F, -0.315F, -0.25F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.136F, -1.035F, 1.36F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-0.5F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(0.0F, 0.08F, 0.75F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.061F, 0.43F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.061F, 0.3F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(-0.01F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                            GL11.glTranslatef(0.0F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon
                            .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holographic2)) {
                            GL11.glTranslatef(0.0F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Kobra)) {
                            GL11.glTranslatef(1.369F, -1.21F, 2.8F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonCustomPositioning(Magazines.ExoticMatterCell, (renderContext) -> {})
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.4F, -0.4F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 250L, 500L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.4F, -0.4F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 250L, 20L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.3F, -0.28F, -0.15F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.3F, -0.8F, 0.8F);
                    }, 250L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.4F, -0.4F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.4F, -0.4F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L))
                    .withFirstPersonCustomPositioningUnloading(
                        Magazines.ExoticMatterCell,
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.2F, 0.5F, -0.2F);
                            GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(1.3F, 0.5F, -0.8F);
                            GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                        }, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Magazines.ExoticMatterCell,
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glTranslatef(-0.047F, -0.315F, -0.25F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.136F, -1.035F, 1.34F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(0.0F, 0.08F, 0.75F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.061F, 0.43F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.061F, 0.3F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(-0.01F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                            GL11.glTranslatef(0.0F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon
                            .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holographic2)) {
                            GL11.glTranslatef(0.0F, 0.13F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Kobra)) {
                            GL11.glTranslatef(1.369F, -1.21F, 2.8F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glRotatef(-20.0F, -4.0F, 1.0F, -2.0F);
                        GL11.glTranslatef(0.5F, -0.35F, -1.0F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-35.0F, 2.0F, 1.0F, 1.0F);
                        GL11.glTranslatef(1.0F, -0.8F, -1.5F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.35F, 0.37F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-55.0F, 1.0F, 0.0F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(2.2F, 2.2F, 2.2F);
                        GL11.glTranslatef(1.0F, 0.2F, 0.2F);
                        GL11.glRotatef(99.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-60.0F, 20.0F, 20.0F, -20.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.5F);
                        GL11.glTranslatef(0.4F, 0.5F, 0.8F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.5F);
                        GL11.glTranslatef(0.4F, 0.5F, 0.8F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.35F, 0.37F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-55.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 0L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.4F, -0.2F, 0.6F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
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
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.0F, 1.0F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(90.0F)
            .withSpawnEntityGravityVelocity(0.0118F)
            .build();
    }
}
