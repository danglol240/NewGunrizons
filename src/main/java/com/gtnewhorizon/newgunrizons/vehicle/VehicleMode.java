package com.gtnewhorizon.newgunrizons.vehicle;

/** How a vehicle moves. */
public enum VehicleMode {

    /** Wheels or tracks: gravity, climbs one block, A/D steer the hull. */
    GROUND(false, 1.1F),
    /** Floats about a block and a half over ground or water; A/D steer. */
    HOVER(false, 1.6F),
    /** Flies level: the nose follows the driver's view, W/S/A/D move, Space and the descend key climb and sink. */
    AIR(true, 0.0F),
    /** Flies where the driver looks, pitch included; Space and the descend key still climb and sink. */
    SPACE(true, 0.0F);

    public final boolean flies;
    public final float stepHeight;

    VehicleMode(boolean flies, float stepHeight) {
        this.flies = flies;
        this.stepHeight = stepHeight;
    }
}
