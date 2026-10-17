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

public class GaussRifleFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("GaussRifle")
            .withFireRate(0.16F)
            .withRecoil(4.0F)

            .withMaxShots(1)
            .withShootSound("AS50")
            .withSilencedShootSound("RifleSilencer")
            .withReloadSound("AKReload")
            .withUnloadSound("Unload")
            .withReloadingTime(43L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> -0.6F)
            .withFlashOffsetX(() -> 0.3F)
            .withFlashOffsetY(() -> -0.1F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: LuV",
                    "Type: Electromagnetic marksman rifle",
                    "Damage: 75",
                    "Ammo: 8rnd Gauss Rifle Magazine",
                    "Fire Rate: Semi"))
            .withCompatibleAttachment(Magazines.GaussRifleMag, ((model) -> {}))
            .withCompatibleAttachment(AuxiliaryAttachments.Extra, true, (model) -> {
                if (JsonModel.is(model, "sight/akmiron1")) {
                    GL11.glTranslatef(0.125F, -1.8F, -0.5F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/akmiron2")) {
                    GL11.glTranslatef(0.13F, -1.55F, -4.8F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/ak47iron")) {
                    GL11.glTranslatef(0.092F, -1.89F, -1.3F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
                } else if (JsonModel.is(model, "sight/m4iron1")) {
                    GL11.glTranslatef(0.155F, -1.74F, 1.0F);
                    GL11.glScaled(0.0D, 0.0D, 0.0D);
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
                    GL11.glTranslatef(0.14F, -1.74F, -2.05F);
                    GL11.glScaled(0.36D, 0.36D, 0.36D);
                } else if (JsonModel.is(model, "sight/scariron1")) {
                    GL11.glTranslatef(0.176F, -1.75F, 1.0F);
                    GL11.glScaled(0.19D, 0.19D, 0.19D);
                } else if (JsonModel.is(model, "sight/scariron2")) {
                    GL11.glTranslatef(0.25F, -1.55F, -2.0F);
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
                }

            })
            .withCompatibleAttachment(Attachments.ACOG, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.8F, 0.0F);
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
                GL11.glTranslatef(0.055F, -1.76F, 0.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.272F, 0.67F);
                    GL11.glScaled(0.05D, 0.05D, 0.05D);
                }

            })
            .withCompatibleAttachment(Attachments.HP, (player, stack) -> {
                GL11.glTranslatef(0.055F, -1.76F, 0.2F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.235F, 1.16F);
                    GL11.glScaled(0.1D, 0.1D, 0.1D);
                }

            })
            .withCompatibleAttachment(Attachments.Bipod, (model) -> {
                GL11.glTranslatef(0.135F, -0.84F, -2.1F);
                GL11.glScaled(0.8D, 0.8D, 0.8D);
            })
            .withCompatibleAttachment(Attachments.Laser2, (p, s) -> {
                GL11.glTranslatef(0.25F, -1.4F, -2.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withCompatibleAttachment(Attachments.Laser, (p, s) -> {
                GL11.glTranslatef(0.25F, -1.4F, -2.0F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            })
            .withCompatibleAttachment(Attachments.Silencer50BMG, (model) -> {
                GL11.glTranslatef(0.107F, -1.52F, -5.8F);
                GL11.glScaled(1.1D, 1.1D, 1.3D);
            })
            .withTextureName("GaussRifle")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/gaussrifle"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.3D, 0.3D, 0.3D);
                        GL11.glTranslatef(0.7F, 1.1F, 0.0F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-1.8F, 0.3F, 1.5F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glTranslatef(0.25F, -0.23F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.25F, -0.23F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.0F);
                        GL11.glRotatef(-4.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.3F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.1377F, -1.0F, 1.3F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-1.0F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(0.0F, 0.185F, 0.75F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.1F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(0.0F, 0.2F, 0.7F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonCustomPositioning(Magazines.GaussRifleMag, (renderContext) -> {})
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
                        Magazines.GaussRifleMag,
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
                        Magazines.GaussRifleMag,
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glTranslatef(0.0F, -0.3F, -0.2F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(0.1377F, -1.0F, 1.35F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(0.0F, 0.185F, 0.75F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.4F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.15F, 0.1F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Reflex)) {
                            GL11.glTranslatef(0.0F, 0.2F, 0.7F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(0.8D, 0.8D, 0.8D);
                        GL11.glRotatef(-20.0F, -4.0F, 1.0F, -2.0F);
                        GL11.glTranslatef(0.5F, -0.25F, -1.0F);
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
            .withSpawnEntityDamage(75.0F)
            .build();
    }
}
