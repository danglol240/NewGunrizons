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

public class HX9Factory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("HX9")
            .withFireRate(0.75F)
            .withRecoil(3.5F)

            .withShootSound("M240")
            .withSilencedShootSound("ar15silenced")
            .withReloadSound("M249Reload")
            .withUnloadSound("M249Unload")
            .withReloadingTime(110L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> 1.0F)
            .withFlashOffsetX(() -> 0.3F)
            .withFlashOffsetY(() -> -0.4F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: IV",
                    "Type: Heavy support rifle",
                    "Damage: 20",
                    "Ammo: Mk48 belts",
                    "Fire Rate: Auto"))
            .withCompatibleAttachment(Magazines.Mk48Mag, ((model) -> {}))
            .withCompatibleAttachment(
                AuxiliaryAttachments.M249Action,
                true,
                (model) -> { GL11.glScaled(1.0D, 1.0D, 1.0D); })
            .withCompatibleAttachment(AuxiliaryAttachments.M249Cover, true, (model) -> {
                if (JsonModel.is(model, "action/m249cover")) {
                    GL11.glScaled(1.0D, 1.0D, 1.0D);
                } else if (JsonModel.is(model, "sight/m249rearsight")) {
                    GL11.glTranslatef(0.195F, -1.645F, 1.55F);
                    GL11.glScaled(0.2D, 0.2D, 0.2D);
                }

            })
            .withCompatibleAttachment(AuxiliaryAttachments.Extra, true, (model) -> {
                if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(0.16F, -1.75F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m4iron2")) {
                    GL11.glTranslatef(0.26F, -1.53F, -2.05F);
                    GL11.glScaled(0.6D, 0.6D, 0.6D);
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
                    GL11.glTranslatef(0.168F, -1.55F, 1.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/g36ciron2")) {
                    GL11.glTranslatef(0.163F, -1.67F, -2.05F);
                    GL11.glScaled(0.3D, 0.3D, 0.5D);
                } else if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.165F, -1.65F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.55F, -2.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/faliron")) {
                    GL11.glTranslatef(0.165F, -1.65F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m14iron")) {
                    GL11.glTranslatef(0.15F, -1.7F, 1.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/mp5iron")) {
                    GL11.glTranslatef(0.215F, -1.54F, 1.2F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                }

            })
            .withCompatibleAttachment(Attachments.ACOG, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.7F, 1.1F);
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
            .withCompatibleAttachment(Attachments.Specter, (player, stack) -> {
                GL11.glTranslatef(0.15F, -1.47F, 0.8F);
                GL11.glScaled(0.35D, 0.35D, 0.35D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/acog2")) {
                    GL11.glTranslatef(0.15F, -1.035F, 1.513F);
                    GL11.glScaled(0.1D, 0.1D, 0.1D);
                }

            })
            .withCompatibleAttachment(Attachments.Reflex, (model) -> {
                if (JsonModel.is(model, "sight/reflex")) {
                    GL11.glTranslatef(0.235F, -1.57F, 0.7F);
                    GL11.glScaled(0.3D, 0.3D, 0.3D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.195F, -1.76F, 0.7F);
                    GL11.glScaled(0.07D, 0.07D, 0.07D);
                }

            })
            .withCompatibleAttachment(Attachments.Kobra, (model) -> {
                if (JsonModel.is(model, "sight/kobra")) {
                    GL11.glTranslatef(0.24F, -1.6F, 0.8F);
                    GL11.glScaled(0.4D, 0.4D, 0.4D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.195F, -1.76F, 0.4F);
                    GL11.glScaled(0.07D, 0.07D, 0.07D);
                }

            })
            .withCompatibleAttachment(Attachments.Holo2, (model) -> {
                if (JsonModel.is(model, "sight/holographic")) {
                    GL11.glTranslatef(0.25F, -1.6F, 0.8F);
                    GL11.glScaled(0.4D, 0.4D, 0.4D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(0.195F, -1.78F, 0.8F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.Holographic2, (model) -> {
                if (JsonModel.is(model, "sight/holographic2")) {
                    GL11.glTranslatef(0.25F, -1.6F, 0.8F);
                    GL11.glScaled(0.4D, 0.4D, 0.4D);
                } else if (JsonModel.is(model, "sight/holo2")) {
                    GL11.glTranslatef(0.195F, -1.78F, 0.8F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.MicroT1, (model) -> {
                if (JsonModel.is(model, "sight/microt1")) {
                    GL11.glTranslatef(0.15F, -1.7F, 0.8F);
                    GL11.glScaled(0.3D, 0.3D, 0.3D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.195F, -1.82F, 0.8F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.Grip2, (model) -> {
                GL11.glTranslatef(0.135F, -0.54F, -0.7F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.StubbyGrip, (model) -> {
                GL11.glTranslatef(0.135F, -0.54F, -0.7F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Grip, (model) -> {
                GL11.glTranslatef(0.135F, -0.57F, -0.9F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withCompatibleAttachment(Attachments.VGrip, (model) -> {
                GL11.glTranslatef(0.135F, -0.52F, -0.7F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Laser2, (p, s) -> {
                GL11.glTranslatef(0.32F, -1.14F, -1.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withCompatibleAttachment(Attachments.Laser, (p, s) -> {
                GL11.glTranslatef(0.32F, -1.14F, -1.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withCompatibleAttachment(Attachments.Silencer556x45, (model) -> {
                GL11.glTranslatef(0.13F, -1.48F, -4.0F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withTextureName("HX9")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/hx9"))
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
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.0F);
                        GL11.glRotatef(-0.6F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.065F, 1.4F, -2.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.012F, 0.16F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Specter)) {
                            GL11.glTranslatef(-0.012F, 0.135F, 0.65F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Kobra)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon
                            .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holographic2)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.MicroT1)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.6F);
                        }

                    })
                    .withFirstPersonCustomPositioning(Magazines.Mk48Mag, (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.M249Action.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.M249Cover.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.ACOG.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.Specter.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.Reflex.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.Kobra.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.Holo2.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonCustomPositioning(
                        Attachments.Holographic2.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonCustomPositioning(Attachments.MicroT1.getRenderablePart(), (renderContext) -> {})
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(5.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 400L, 20L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(8.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 350L, 150L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 1.5F, -2.1F);
                    }, 450L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 1.5F, -2.1F);
                    }, 500L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(4.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 1.5F, -2.1F);
                    }, 200L, 100L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 350L, 20L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 320L, 30L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 250L, 20L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 400L, 100L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 350L, 80L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 400L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(-6.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(12.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.6F, 1.7F, -2.1F);
                    }, 430L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(12.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.6F, 1.7F, -2.0F);
                    }, 330L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(-6.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(12.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.6F, 1.7F, -2.1F);
                    }, 350L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 300L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(5.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 400L, 20L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(8.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(43.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 350L, 150L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(2.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 1.5F, -2.1F);
                    }, 500L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 1.5F, -2.1F);
                    }, 450L, 0L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 400L, 100L), new Transition((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(1.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 1.5F, -2.1F);
                    }, 350L, 80L))
                    .withFirstPersonCustomPositioningUnloading(
                        Magazines.Mk48Mag,
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        AuxiliaryAttachments.M249Cover.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        AuxiliaryAttachments.M249Action.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Magazines.Mk48Mag,
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.2F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.M249Cover.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glScaled(1.0D, 1.0D, 1.0D);
                            GL11.glTranslatef(0.0F, -1.4F, 1.4F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.M249Action.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.ACOG.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.ACOG.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.7F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.Specter.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.Specter.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -2.5F, -1.95F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.Reflex.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.Reflex.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.Kobra.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.Kobra.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.Holo2.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.Holo2.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.Holographic2.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.Holographic2.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Attachments.MicroT1.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningUnloading(
                        Attachments.MicroT1.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -1.6F, 1.3F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.065F, 1.4F, -2.1F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(-0.012F, 0.16F, 0.65F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Specter)) {
                            GL11.glTranslatef(-0.012F, 0.135F, 0.7F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Kobra)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holo2)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon
                            .isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Holographic2)) {
                            GL11.glTranslatef(0.0F, 0.1F, 0.6F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.MicroT1)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.6F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(5.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(30.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.7F, 2.025F, -1.6F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(5.5D, 5.5D, 5.5D);
                        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-1.175F, 1.425F, -1.1F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.825F, -0.1F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.45F, -0.675F, -0.275F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-140.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.325F, -0.3F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -1.225F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.8F, -0.1F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.8F, -0.1F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-115.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.2F, -0.775F, -0.1F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-130.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.175F, -0.65F, -0.125F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.275F, -0.7F, -0.125F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-140.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.325F, -0.3F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.45F, -0.675F, -0.275F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.45F, 0.0F);
                    }, 250L, 0L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-65.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -0.3F, -0.05F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-65.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.475F, -0.475F, -0.125F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-65.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -0.3F, -0.05F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 0L))
                    .withFirstPersonLeftHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.45F, -0.675F, -0.275F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-140.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.325F, -0.3F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.125F, -0.8F, -0.1F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -1.225F, 0.0F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-140.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.325F, -0.3F);
                    }, 250L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.45F, -0.675F, -0.275F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.6F, -0.85F, 0.0F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(20.0F)
            .withSpawnEntityGravityVelocity(0.0118F)
            .build();
    }
}
