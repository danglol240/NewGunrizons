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
import com.gtnewhorizon.newgunrizons.registry.AuxiliaryAttachments;
import com.gtnewhorizon.newgunrizons.registry.Magazines;

public class ParticleBeamRifleFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("ParticleBeamRifle")
            .withProjectile(ProjectileStyle.HELIX, 0x5AFF78, 1.0F)
            .withFireRate(0.16F)
            .withEjectRoundRequired()
            .withEjectSpentRoundSound("L96BoltAction")
            .withRecoil(4.0F)

            .withMaxShots(1)
            .withShootSound("HecateII")
            .withPumpTimeout(2000L)
            .withSilencedShootSound("RifleSilenced")
            .withReloadSound("BoltActionReload")
            .withUnloadSound("Unload")
            .withReloadingTime(30L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.8F)
            .withFlashOffsetZ(() -> 0.0F)
            .withFlashOffsetX(() -> 0.1F)
            .withFlashOffsetY(() -> 0.1F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withShellCasingEjectEnabled(false)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: UV",
                    "Type: Particle beam rifle",
                    "Damage: 220",
                    "Ammo: 10-shot Particle Beam Cell",
                    "Fire Rate: Semi"))
            .withCompatibleAttachment(Magazines.ParticleBeamCell, ((model) -> {
                GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                GL11.glScaled(1.2D, 0.7D, 1.0D);
            }))
            .withCompatibleAttachment(AuxiliaryAttachments.HecateIIBoltAction, true, (model) -> {
                GL11.glTranslatef(-0.01F, 0.02F, 0.03F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withCompatibleAttachment(Attachments.HP, true, true, (player, stack) -> {
                GL11.glTranslatef(0.06F, -1.37F, 0.3F);
                GL11.glScaled(0.6D, 0.6D, 0.6D);
            }, (model) -> {
                if (JsonModel.is(model, "sight/lpscope")) {
                    GL11.glTranslatef(0.237F, -0.235F, 1.16F);
                    GL11.glScaled(0.1D, 0.1D, 0.1D);
                }

            })
            .withCompatibleAttachment(Attachments.Bipod, true, (model) -> {
                GL11.glTranslatef(0.135F, -0.55F, -1.2F);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
            })
            .withCompatibleAttachment(Attachments.Silencer50BMG, (model) -> {
                GL11.glTranslatef(0.107F, -1.45F, -6.95F);
                GL11.glScaled(1.1D, 1.1D, 1.3D);
            })
            .withTextureName("ParticleBeamRifle")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/particlebeamrifle"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.35D, 0.35D, 0.35D);
                        GL11.glTranslatef(1.0F, 1.5F, -0.7F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.7D, 0.7D, 0.7D);
                        GL11.glTranslatef(-1.6F, 0.4F, 1.3F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glTranslatef(0.33F, -0.23F, -0.27F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.65D, 0.65D, 0.65D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.8F);
                    })
                    .withFirstPersonCustomPositioning(Magazines.ParticleBeamCell, (renderContext) -> {})
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.33F, -0.23F, -0.27F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.65D, 0.65D, 0.65D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.0F);
                        GL11.glRotatef(-3.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.3F, 0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(0.17F, -0.99F, 0.45F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-0.5F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.16F, 0.15F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.16F, 0.15F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(1.38F, -1.19F, 2.9F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonCustomPositioning(
                        AuxiliaryAttachments.HecateIIBoltAction.getRenderablePart(),
                        (renderContext) -> {})
                    .withFirstPersonPositioningEjectSpentRound(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 250L, 300L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 150L, 0L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 150L, 0L))
                    .withFirstPersonCustomPositioningEjectSpentRound(
                        AuxiliaryAttachments.HecateIIBoltAction.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 300L),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> {}, 250L, 50L))
                    .withFirstPersonCustomPositioningEjectSpentRound(
                        Magazines.ParticleBeamCell.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> {}, 250L, 50L))
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 250L, 300L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.5F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 350L, 100L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.5F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 250L, 100L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 350L, 250L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 150L, 0L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.28F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(7.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(-0.4F, -0.8F, 1.2F);
                    }, 150L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.5F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L), new Transition((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.5F, -0.3F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glTranslatef(-0.4F, -0.8F, 0.9F);
                    }, 150L, 50L))
                    .withFirstPersonCustomPositioningUnloading(
                        Magazines.ParticleBeamCell,
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
                        Magazines.ParticleBeamCell,
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.05F, 1.0F, 0.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        AuxiliaryAttachments.HecateIIBoltAction.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 50L),
                        new Transition((renderContext) -> { GL11.glTranslatef(0.0F, 0.0F, 0.3F); }, 250L, 50L),
                        new Transition((renderContext) -> {}, 250L, 50L))
                    .withFirstPersonCustomPositioningUnloading(
                        AuxiliaryAttachments.HecateIIBoltAction.getRenderablePart(),
                        new Transition((renderContext) -> {}, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glTranslatef(0.2F, -0.3F, 0.1F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glScaled(0.6D, 0.6D, 0.6D);
                        GL11.glTranslatef(0.17F, -0.99F, 0.4F);
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.HP)) {
                            GL11.glTranslatef(0.0F, 0.16F, 0.15F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.Scope)) {
                            GL11.glTranslatef(0.0F, 0.16F, 0.15F);
                        }

                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {
                            GL11.glTranslatef(1.38F, -1.19F, 2.9F);
                        } else {
                            GL11.glTranslatef(1.373F, -1.34F, 2.4F);
                        }

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(0.9D, 0.9D, 0.9D);
                        GL11.glRotatef(-20.0F, -4.0F, 1.0F, -2.0F);
                        GL11.glTranslatef(0.5F, -0.3F, -1.0F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(0.55D, 0.55D, 0.55D);
                        GL11.glRotatef(-35.0F, 2.0F, 1.0F, 1.0F);
                        GL11.glTranslatef(1.0F, -0.8F, -1.5F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
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
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.5F);
                        GL11.glTranslatef(0.4F, 0.5F, 0.7F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(2.0F, 2.0F, 2.5F);
                        GL11.glTranslatef(0.4F, 0.5F, 0.7F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.55F, -0.25F, 0.5F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.67F, -0.15F, 0.6F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.67F, -0.15F, 0.6F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.55F, -0.25F, 0.6F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.29F, 0.032F, 0.5F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L))
                    .withFirstPersonLeftHandPositioningEjectSpentRound(new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(1.7F, 1.7F, 3.0F);
                        GL11.glTranslatef(0.65F, -0.04F, 0.3F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                    }, 250L, 50L))
                    .withFirstPersonRightHandPositioningEjectSpentRound(new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.55F, -0.25F, 0.5F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.67F, -0.15F, 0.6F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.55F, -0.25F, 0.6F);
                        GL11.glRotatef(110.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.29F, 0.032F, 0.5F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    }, 350L, 1050L))
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
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(1.8F, 1.8F, 2.5F);
                        GL11.glTranslatef(-0.15F, 0.06F, 0.95F);
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(220.0F)
            .withSpawnEntityGravityVelocity(0.0F)
            .build();
    }
}
