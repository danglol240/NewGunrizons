package com.gtnewhorizon.newgunrizons.vehicle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Where things sit on a vehicle: seats, turrets, fixed weapon mounts, rotors, hover emitters and engine nozzles.
 * Generated with the models by tools/vehicle_designs.py into assets/newgunrizons/vehicles/vehicle_&lt;name&gt;.json
 * and read on both sides. Positions are in model units (Java model space: Y down, nose towards -Z, y = 0 on the
 * ground); {@link #toVehicle} turns them into blocks in the vehicle's own frame.
 */
public final class VehicleLayout {

    public static final float UNIT = 1.0F / 16.0F;

    public static final class Point {

        public final float x, y, z, r;
        public final boolean hidden;

        Point(JsonObject o) {
            x = f(o, "x");
            y = f(o, "y");
            z = f(o, "z");
            r = f(o, "r");
            hidden = o.has("hidden") && o.get("hidden")
                .getAsBoolean();
        }
    }

    public static final class Turret {

        public final float x, y, z;
        /** Gun pivot relative to the turret pivot. */
        public final float gx, gy, gz;
        /** Barrel length from the gun pivot to the muzzle. */
        public final float length;
        public final float minPitch, maxPitch;
        public final int seat;
        public final boolean hasTurretModel, hasGunModel;

        Turret(JsonObject o) {
            x = f(o, "x");
            y = f(o, "y");
            z = f(o, "z");
            gx = f(o, "gx");
            gy = f(o, "gy");
            gz = f(o, "gz");
            length = f(o, "length");
            minPitch = f(o, "minPitch");
            maxPitch = f(o, "maxPitch");
            seat = o.get("seat")
                .getAsInt();
            hasTurretModel = o.get("turret")
                .getAsBoolean();
            hasGunModel = o.get("gun")
                .getAsBoolean();
        }
    }

    public final float scale;
    public final float width;
    public final float height;
    public final List<Point> seats;
    public final List<Turret> turrets;
    public final List<Point> mounts;
    public final List<Point> rotors;
    public final List<Point> pads;
    public final List<Point> exhausts;

    private VehicleLayout(JsonObject root) {
        scale = f(root, "scale");
        width = f(root, "width");
        height = f(root, "height");
        seats = points(root, "seats");
        mounts = points(root, "mounts");
        rotors = points(root, "rotors");
        pads = points(root, "pads");
        exhausts = points(root, "exhausts");
        List<Turret> t = new ArrayList<>();
        for (JsonElement e : root.getAsJsonArray("turrets")) t.add(new Turret(e.getAsJsonObject()));
        turrets = Collections.unmodifiableList(t);
    }

    public static VehicleLayout load(String name) {
        String path = "/assets/newgunrizons/vehicles/vehicle_" + name.toLowerCase() + ".json";
        InputStream is = VehicleLayout.class.getResourceAsStream(path);
        if (is == null) throw new IllegalStateException("Vehicle layout not found: " + path);
        try (InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new VehicleLayout(
                new JsonParser().parse(r)
                    .getAsJsonObject());
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read vehicle layout " + path, e);
        }
    }

    /**
     * Model units to blocks in the vehicle frame: x to the vehicle's right, y up, z forward (towards the nose).
     * Matches the renderer's transform (rotate 180 - yaw, then scale -s, -s, s).
     */
    public double[] toVehicle(float mx, float my, float mz) {
        double k = scale * UNIT;
        // model +x is the vehicle's left side (the renderer's -s x scale mirrors it)
        return new double[] { -mx * k, -my * k, -mz * k };
    }

    /** A point in the vehicle frame to a world offset for a vehicle facing yaw (degrees, Minecraft convention). */
    public static double[] toWorld(double right, double up, double forward, float yaw) {
        double rad = Math.toRadians(yaw);
        double fx = -Math.sin(rad), fz = Math.cos(rad); // forward
        double rx = -Math.cos(rad), rz = -Math.sin(rad); // right
        return new double[] { fx * forward + rx * right, up, fz * forward + rz * right };
    }

    private static List<Point> points(JsonObject root, String key) {
        List<Point> out = new ArrayList<>();
        JsonArray a = root.getAsJsonArray(key);
        if (a != null) for (JsonElement e : a) out.add(new Point(e.getAsJsonObject()));
        return Collections.unmodifiableList(out);
    }

    private static float f(JsonObject o, String key) {
        return o.has(key) ? o.get(key)
            .getAsFloat() : 0.0F;
    }
}
