package com.gtnewhorizon.newgunrizons.items;

/**
 * Rotor arrangement of a drone frame, in model units (1/16 block, Y down). Must match LAYOUTS in
 * tools/drone_designs.py, which places the motors the rotors spin on.
 */
public enum DroneLayout {

    QUAD(4, 9.9F, 45.0F, 5.5F),
    HEX(6, 10.0F, 30.0F, 4.6F),
    OCTO(8, 11.0F, 22.5F, 4.0F);

    /** Height of the rotor discs (just above the motor shafts). */
    public static final float ROTOR_Y = -4.5F;

    public final int rotors;
    public final float hubDistance;
    public final float firstAngle;
    public final float rotorRadius;

    DroneLayout(int rotors, float hubDistance, float firstAngle, float rotorRadius) {
        this.rotors = rotors;
        this.hubDistance = hubDistance;
        this.firstAngle = firstAngle;
        this.rotorRadius = rotorRadius;
    }

    public float hubX(int i) {
        return (float) (hubDistance * Math.cos(Math.toRadians(firstAngle + 360.0D * i / rotors)));
    }

    public float hubZ(int i) {
        return (float) (hubDistance * Math.sin(Math.toRadians(firstAngle + 360.0D * i / rotors)));
    }
}
