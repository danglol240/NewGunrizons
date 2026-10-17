package com.gtnewhorizon.newgunrizons.items;

/** How a weapon's projectile is drawn in flight. */
public enum ProjectileStyle {
    /** Invisible (classic firearms). */
    NONE,
    /** Short glowing streak. */
    TRACER,
    /** Long electromagnetic streak that leaves fading coil rings behind it. */
    RAIL,
    /** Continuous flickering beam from the muzzle to the projectile. */
    LASER,
    /** Pulsing plasma ball with a short tail. */
    PLASMA,
    /** Bright core wrapped in a rotating double helix. */
    HELIX,
    /** Glowing pulse emitting expanding shock rings. */
    PULSE,
    /** Glowing orb inside two rotating containment rings. */
    ORB_RING,
    /** Jittering shards of energy around the flight path. */
    SHARDS
}
