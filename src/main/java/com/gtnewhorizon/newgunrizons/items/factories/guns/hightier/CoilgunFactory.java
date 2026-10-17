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

public class CoilgunFactory {

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("Coilgun")
            .withFireRate(0.75F)
            .withRecoil(2.8F)

            .withShootSound("P90")
            .withSilencedShootSound("m9silenced")
            .withReloadSound("P90Reload")
            .withUnloadSound("Unload")
            .withReloadingTime(70L)
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.4F)
            .withFlashScale(() -> 0.6F)
            .withFlashOffsetZ(() -> 3.4F)
            .withFlashOffsetX(() -> 0.3F)
            .withFlashOffsetY(() -> -0.2F)
            .withCreativeTab(NewGunrizonsMod.HighTierTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Tier: LuV",
                    "Type: Electromagnetic PDW",
                    "Damage: 24",
                    "Ammo: 50rnd Coilgun Slug Magazine",
                    "Fire Rate: Auto"))
            .withCompatibleAttachment(Magazines.CoilgunMag, ((model) -> {}))
            .withCompatibleAttachment(AuxiliaryAttachments.FNP90Sight, true, (model) -> {
                if (JsonModel.is(model, "sight/fnp90sight")) {
                    GL11.glTranslatef(0.045F, -1.7F, -0.3F);
                    GL11.glScaled(0.75D, 0.9D, 0.9D);
                } else if (JsonModel.is(model, "sight/reflex2")) {
                    GL11.glTranslatef(0.19F, -1.76F, 0.0F);
                    GL11.glScaled(0.2D, 0.2D, 0.2D);
                }

            })
            .withCompatibleAttachment(Attachments.Laser, (p, s) -> {
                GL11.glTranslatef(0.34F, -1.7F, -0.1F);
                GL11.glScaled(0.65D, 0.65D, 0.65D);
            })
            .withCompatibleAttachment(Attachments.Silencer57x38, (model) -> {
                GL11.glTranslatef(0.107F, -1.2F, -2.14F);
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            })
            .withTextureName("Coilgun")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/coilgun"))
                    .withEntityPositioning((itemStack) -> {
                        GL11.glScaled(0.35D, 0.35D, 0.35D);
                        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 4.0F);
                    })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.35D, 0.35D, 0.35D);
                        GL11.glTranslatef(2.0F, 0.5F, 0.0F);
                        GL11.glRotatef(-120.0F, -0.5F, 7.0F, 3.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.5D, 0.5D, 0.5D);
                        GL11.glTranslatef(-1.8F, 0.7F, 1.5F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    })
                    .withFirstPersonPositioningRecoiled((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.5F);
                        GL11.glRotatef(-1.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioningZoomingRecoiled((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.03F, 1.48F, -2.7F);
                        GL11.glRotatef(-1.0F, 1.0F, 0.0F, 0.0F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {}

                    })
                    .withFirstPersonCustomPositioning(Magazines.CoilgunMag, (renderContext) -> {})
                    .withFirstPersonPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(37.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(6.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(6.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    }, 350L, 150L), new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(4.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    }, 320L, 50L), new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    }, 330L, 200L), new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-1.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.4F, -3.725F);
                    }, 100L, 0L))
                    .withFirstPersonPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    }, 150L, 50L), new Transition((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(4.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glTranslatef(-0.85F, 1.375F, -3.725F);
                    }, 150L, 50L))
                    .withFirstPersonCustomPositioningUnloading(
                        Magazines.CoilgunMag,
                        new Transition((renderContext) -> { GL11.glRotatef(2.0F, 1.0F, 0.0F, 1.0F); }, 250L, 1000L),
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.0F, 0.5F); }, 250L, 1000L))
                    .withFirstPersonCustomPositioningReloading(
                        Magazines.CoilgunMag,
                        new Transition((renderContext) -> { GL11.glTranslatef(1.0F, 1.0F, 0.5F); }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, -0.17F, 0.0F);
                            GL11.glRotatef(2.0F, 1.0F, 0.0F, 1.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {
                            GL11.glTranslatef(0.0F, 0.0F, 0.1F);
                            GL11.glRotatef(4.0F, 1.0F, 0.0F, 0.0F);
                        }, 250L, 1000L),
                        new Transition((renderContext) -> {}, 250L, 1000L))
                    .withFirstPersonPositioningZooming((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(-0.03F, 1.48F, -2.8F);
                        if (ItemWeapon.isActiveAttachment(renderContext.getWeaponInstance(), Attachments.ACOG)) {}

                    })
                    .withFirstPersonPositioningRunning((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.3F, 2.2F, -1.6F);
                    })
                    .withFirstPersonPositioningModifying((renderContext) -> {
                        GL11.glScaled(4.0D, 4.0D, 4.0D);
                        GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.575F, 1.1F, -2.275F);
                    })
                    .withFirstPersonHandPositioning((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-35.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(50.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.35F, -0.8F, -0.075F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    })
                    .withFirstPersonHandPositioningModifying((renderContext) -> {
                        GL11.glScalef(2.2F, 2.2F, 2.2F);
                        GL11.glTranslatef(1.0F, 0.2F, 0.2F);
                        GL11.glRotatef(99.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-60.0F, 20.0F, 20.0F, -20.0F);
                    }, (renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    })
                    .withFirstPersonLeftHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.7F, -1.125F, -0.125F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-95.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(70.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.7F, -0.7F, -0.35F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-120.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.55F, -0.725F, -0.5F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.675F, -0.775F, -0.325F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningReloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 50L))
                    .withFirstPersonLeftHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-120.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.55F, -0.725F, -0.5F);
                    }, 50L, 200L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.5F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.7F, -1.125F, -0.125F);
                    }, 50L, 200L))
                    .withFirstPersonRightHandPositioningUnloading(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 1000L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 4.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.7F, -0.725F, -0.0F);
                    }, 250L, 50L))
                    .build())
            .withSpawnEntityDamage(24.0F)
            .withSpawnEntityGravityVelocity(0.028F)
            .build();
    }
}
