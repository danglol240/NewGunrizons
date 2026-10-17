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
    SHARDS,
    /** Flickering bolt trailed by out-of-phase ghost copies of itself. */
    PHASE,
    /** Dark core with rings collapsing inwards. */
    GRAVITON,
    /** Green bolt carrying a ticking clock dial. */
    CHRONO,
    /** Black core inside a spinning accretion disc. */
    SINGULARITY,
    /** Hitscan: jagged tear in space along the beam. */
    RIFT,
    /** Hitscan: wide white-gold beam that erases what it touches. */
    ERASE
}
