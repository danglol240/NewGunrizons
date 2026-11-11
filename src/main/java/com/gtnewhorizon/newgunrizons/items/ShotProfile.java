package com.gtnewhorizon.newgunrizons.items;

/**
 * What a shot looks like and does: implemented by hand-held weapons and by drones, so projectiles, hitscan beams and
 * hit effects work the same for both.
 */
public interface ShotProfile {

    WeaponBehavior getBehavior();

    ProjectileStyle getProjectileStyle();

    /** RGB colour of the shot effect. */
    int getProjectileColor();

    /** Size multiplier of the shot effect. */
    float getProjectileScale();
}
