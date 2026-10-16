package com.gtnewhorizon.newgunrizons.items.factories.grenades;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.attachment.Part;
import com.gtnewhorizon.newgunrizons.client.animation.Transition;
import com.gtnewhorizon.newgunrizons.grenade.GrenadeRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemGrenade;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.registry.Grenades;

public class FuseGrenadeFactory {

    public ItemGrenade createGrenade() {
        return (new ItemGrenade.Builder()).withName("M67Frag")
            .withCreativeTab(NewGunrizonsMod.ShotgunsTab)
            .withTextureNames("M67Frag")
            .withMaxStackSize(64)
            .withExplosionStrength(1.0F)
            .withExplosionTimeout(5000)
            .withExplosionSound("grenadeexplosion")
            .withBounceSoftSound("grenade-soft-bounce")
            .withBounceHardSound("grenade-hard-bounce")
            .withThrowSound("grenadethrow")
            .withSafetyPinOffSound("safetypinoff")
            .withEffectiveRadius(15.0F)
            .withFragmentCount(1500)
            .withFragmentDamage(30.0F)
            .withCompatibleAttachment(Grenades.GrenadeSafetyPin, (p, s) -> {})
            .withVelocity(() -> { return 0.8F; })
            .withFarVelocity(() -> { return 1.3F; })
            .withGravityVelocity(() -> { return 0.06F; })
            .withRotationSlowdownFactor(() -> { return 0.99F; })
            .withRenderer(
                (new GrenadeRenderer.Builder()).withModel(new JsonModel("misc/m67frag"))
                    .withAnimationDuration(500)
                    .withThrownEntityPositioning(() -> {
                        GL11.glScalef(0.2F, 0.2F, 0.2F);
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 0.0F);
                    })
                    .withEntityRotationCenterOffsets(
                        () -> { return -0.025F; },
                        () -> { return 0.2F; },
                        () -> { return -0.025F; })
                    .withInventoryPositioning((itemStack) -> {
                        GL11.glScaled(0.7D, 0.7D, 0.7D);
                        GL11.glTranslatef(1.0F, 1.8F, -1.6F);
                        GL11.glRotatef(230.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withThirdPersonPositioning((renderContext) -> {
                        GL11.glScaled(0.3D, 0.3D, 0.3D);
                        GL11.glTranslatef(-3.0F, -1.0F, 3.0F);
                        GL11.glRotatef(-225.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                    })
                    .withFirstPersonPositioning((context) -> {
                        GL11.glScalef(0.4F, 0.4F, 0.4F);
                        GL11.glRotatef(25.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-3.0F, -0.6F, -2.2F);
                    })
                    .withFirstPersonCustomPositioning(
                        Grenades.GrenadeSafetyPin.getRenderablePart(),
                        (Part) null,
                        (context) -> {})
                    .withFirstPersonHandPositioning((context) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -0.525F, 0.425F);
                    }, (context) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.2F, -0.3F, 0.1F);
                    })
                    .withFirstPersonPositioningSafetyPinOff(new Transition((renderContext) -> {
                        GL11.glScalef(0.33F, 0.33F, 0.33F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.5F, -2.15F, -2.3F);
                    }, 200L, 50L), new Transition((renderContext) -> {
                        GL11.glScalef(0.33F, 0.33F, 0.33F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.5F, -2.15F, -2.3F);
                    }, 200L, 0L))
                    .withFirstPersonLeftHandPositioningSafetyPinOff(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(105.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.2F, -0.775F, 0.1F);
                    }, 70L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -1.025F, 0.225F);
                    }, 70L, 0L))
                    .withFirstPersonRightHandPositioningSafetyPinOff(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.325F, -0.375F, -0.125F);
                    }, 70L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.325F, -0.375F, -0.125F);
                    }, 70L, 0L))
                    .withFirstPersonCustomPositioningSafetyPinOff(
                        Grenades.GrenadeSafetyPin.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        }, 70L, 0L, Part.LEFT_HAND),
                        new Transition((renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        }, 70L, 0L, Part.LEFT_HAND))
                    .withFirstPersonPositioningThrowing(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glScalef(0.1F, 0.1F, 0.1F);
                        GL11.glRotatef(-40.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -6.85F, -2.4F);
                    }, 260L, 120L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.475F, -0.75F, -0.075F);
                        GL11.glScalef(0.0F, 0.0F, 0.0F);
                    }, 80L, 80L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(155.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-35.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.475F, -0.75F, 0.075F);
                    }, 80L, 80L))
                    .withFirstPersonLeftHandPositioningThrowing(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -1.025F, 0.225F);
                    }, 70L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -1.025F, 0.225F);
                    }, 70L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.225F, -0.975F, 0.55F);
                    }, 70L, 0L))
                    .withFirstPersonRightHandPositioningThrowing(new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-150.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glTranslatef(0.5F, -0.2F, -0.3F);
                    }, 70L, 0L), new Transition((renderContext) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-5.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.25F, -0.5F, -0.3F);
                    }, 70L, 0L), new Transition((renderContext) -> {}, 70L, 0L))
                    .withFirstPersonCustomPositioningThrowing(
                        Grenades.GrenadeSafetyPin.getRenderablePart(),
                        new Transition((renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        }, 70L, 0L, Part.LEFT_HAND),
                        new Transition((renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        }, 70L, 0L, Part.LEFT_HAND),
                        new Transition((renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        }, 70L, 0L, Part.LEFT_HAND))
                    .withFirstPersonPositioningStrikerLeverOff((renderContext) -> {
                        GL11.glScalef(0.33F, 0.33F, 0.33F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.5F, -2.15F, -2.3F);
                    })
                    .withFirstPersonCustomPositioningStrikerLeverOff(
                        Grenades.GrenadeSafetyPin.getRenderablePart(),
                        Part.LEFT_HAND,
                        (renderContext) -> {
                            GL11.glScalef(0.2F, 0.2F, 0.2F);
                            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(-3.9F, 1.4F, -2.4F);
                        })
                    .withFirstPersonCustomPositioningThrown(
                        Grenades.GrenadeSafetyPin.getRenderablePart(),
                        Part.MAIN_ITEM,
                        (renderContext) -> {
                            GL11.glScalef(0.5F, 0.5F, 0.5F);
                            GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.8F, 1.025F, -0.05F);
                        })
                    .withFirstPersonHandPositioningStrikerLevelOff((context) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.175F, -1.025F, 0.225F);
                    }, (context) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-15.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.325F, -0.375F, -0.125F);
                    })
                    .withFirstPersonHandPositioningThrown((context) -> {
                        GL11.glScalef(3.0F, 3.0F, 3.0F);
                        GL11.glRotatef(-85.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.1F, -0.725F, 0.7F);
                    }, (context) -> {})
                    .build())
            .build();
    }
}
