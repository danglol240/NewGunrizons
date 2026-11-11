package com.gtnewhorizon.newgunrizons.vehicle;

import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.items.ShotProfile;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;

/**
 * A weapon mounted on a vehicle. It fires from a turret (aimed by whoever sits in the turret's seat) or from a fixed
 * mount along the vehicle's nose (aimed by the driver), with the primary or secondary trigger. Real guns take rounds
 * loaded into the vehicle; energy weapons draw from the vehicle's charge.
 */
public final class VehicleWeapon implements ShotProfile {

    public enum Trigger {
        PRIMARY,
        SECONDARY
    }

    public final String name;
    /** Index into the layout's turrets ({@code turret >= 0}) or fixed mounts ({@code mount >= 0}). */
    public final int turret;
    public final int mount;
    public final Trigger trigger;
    public final float damage;
    public final int interval;
    public final float spread;
    /** Blocks per tick; 0 for hitscan weapons (the behaviour gives the range). */
    public final float speed;
    public final float explosion;
    public final int magazine;
    public final Item ammo;
    /** Energy per shot as a multiple of the vehicle tier's voltage, for weapons without rounds. */
    public final float energyPerShot;
    public final String sound;
    private final WeaponBehavior behavior;
    private final ProjectileStyle style;
    private final int color;
    private final float scale;

    private VehicleWeapon(Builder b) {
        name = b.name;
        turret = b.turret;
        mount = b.mount;
        trigger = b.trigger;
        damage = b.damage;
        interval = b.interval;
        spread = b.spread;
        speed = b.speed;
        explosion = b.explosion;
        magazine = b.magazine;
        ammo = b.ammo;
        energyPerShot = b.energy;
        sound = b.sound;
        behavior = b.behavior;
        style = b.style;
        color = b.color;
        scale = b.scale;
    }

    public boolean usesRounds() {
        return magazine > 0 && ammo != null;
    }

    public boolean isHitscan() {
        return behavior.isHitscan();
    }

    @Override
    public WeaponBehavior getBehavior() {
        return behavior;
    }

    @Override
    public ProjectileStyle getProjectileStyle() {
        return style;
    }

    @Override
    public int getProjectileColor() {
        return color;
    }

    @Override
    public float getProjectileScale() {
        return scale;
    }

    public static Builder turret(String name, int turret) {
        return new Builder(name, turret, -1);
    }

    public static Builder fixed(String name, int mount) {
        return new Builder(name, -1, mount);
    }

    public static final class Builder {

        private final String name;
        private final int turret, mount;
        private Trigger trigger = Trigger.PRIMARY;
        private float damage = 6.0F, spread = 1.0F, speed = 8.0F, explosion, energy = 2.0F, scale = 0.6F;
        private int interval = 10, magazine, color = 0xFFD070;
        private Item ammo;
        private String sound = "random.bow";
        private WeaponBehavior behavior = WeaponBehavior.NONE;
        private ProjectileStyle style = ProjectileStyle.TRACER;

        private Builder(String name, int turret, int mount) {
            this.name = name;
            this.turret = turret;
            this.mount = mount;
        }

        public Builder secondary() {
            this.trigger = Trigger.SECONDARY;
            return this;
        }

        public Builder shot(float damage, int interval, float spread, float speed) {
            this.damage = damage;
            this.interval = interval;
            this.spread = spread;
            this.speed = speed;
            return this;
        }

        public Builder explosive(float radius) {
            this.explosion = radius;
            return this;
        }

        public Builder rounds(int magazine, Item ammo) {
            this.magazine = magazine;
            this.ammo = ammo;
            return this;
        }

        public Builder energy(float perShot) {
            this.energy = perShot;
            return this;
        }

        public Builder sound(String sound) {
            if (sound != null) this.sound = sound;
            return this;
        }

        public Builder behavior(WeaponBehavior behavior) {
            this.behavior = behavior;
            return this;
        }

        public Builder look(ProjectileStyle style, int color, float scale) {
            this.style = style;
            this.color = color;
            this.scale = scale;
            return this;
        }

        public VehicleWeapon build() {
            return new VehicleWeapon(this);
        }
    }
}
