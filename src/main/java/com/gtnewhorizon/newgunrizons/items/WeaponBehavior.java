package com.gtnewhorizon.newgunrizons.items;

/**
 * How a weapon's shots behave beyond plain damage: piercing, hitscan beams, overheating, armor penetration, fire,
 * knockback, radiation, phasing through blocks, time and gravity fields, erasing. Configured fluently once when the
 * weapon is built, e.g. {@code new WeaponBehavior().pierce(6, 0.8F)}; never changed afterwards.
 */
public final class WeaponBehavior {

    public static final WeaponBehavior NONE = new WeaponBehavior();

    /** Kind of field a shot leaves where it lands. */
    public enum FieldKind {
        NONE,
        /** Pulls living things towards its centre and crushes them at the end. */
        GRAVITY_WELL,
        /** One violent push away from the centre. */
        REPULSE,
        /** Pulls living things and dropped items in, slows them and damages them over time. */
        SINGULARITY,
        /** Slows everything inside to a crawl: mobs, players and projectiles. */
        CHRONO
    }

    /** Extra targets a projectile or beam passes through after the first. */
    private int pierce;
    /** Damage multiplier applied after each pierced target. */
    private float pierceFalloff = 1.0F;
    /** Fraction of the damage that ignores armor (0..1). */
    private float armorPiercing;
    private float knockback;
    private int burnSeconds;
    /** GregTech radioactivity level applied on hit. */
    private int radiation;
    /** Beam range in blocks; above zero the weapon fires instant hitscan beams instead of projectiles. */
    private float hitscanRange;
    private boolean throughBlocks;
    /** Solid blocks a projectile may pass through before it stops. */
    private int phaseBlocks;
    /** Heat added per shot, heat at which the weapon locks up, and heat lost per tick. */
    private float heatPerShot;
    private float maxHeat;
    private float coolingPerTick;
    /** Ticks of near-total slowness put on a directly hit target. */
    private int chronoTicks;
    private boolean eraser;

    private FieldKind field = FieldKind.NONE;
    private float fieldRadius;
    private int fieldTicks;
    private float fieldDamage;
    private FieldKind altField = FieldKind.NONE;
    private float altFieldRadius;
    private int altFieldTicks;
    private float altFieldDamage;

    public WeaponBehavior pierce(int targets, float falloff) {
        this.pierce = targets;
        this.pierceFalloff = falloff;
        return this;
    }

    public WeaponBehavior armorPiercing(float fraction) {
        this.armorPiercing = Math.max(0.0F, Math.min(1.0F, fraction));
        return this;
    }

    public WeaponBehavior knockback(float strength) {
        this.knockback = strength;
        return this;
    }

    public WeaponBehavior burn(int seconds) {
        this.burnSeconds = seconds;
        return this;
    }

    public WeaponBehavior radiation(int level) {
        this.radiation = level;
        return this;
    }

    public WeaponBehavior hitscan(float range) {
        this.hitscanRange = range;
        return this;
    }

    public WeaponBehavior throughBlocks() {
        this.throughBlocks = true;
        return this;
    }

    public WeaponBehavior phase(int blocks) {
        this.phaseBlocks = blocks;
        return this;
    }

    public WeaponBehavior heat(float perShot, float max, float coolingPerTick) {
        this.heatPerShot = perShot;
        this.maxHeat = max;
        this.coolingPerTick = coolingPerTick;
        return this;
    }

    public WeaponBehavior chrono(int ticks) {
        this.chronoTicks = ticks;
        return this;
    }

    public WeaponBehavior eraser() {
        this.eraser = true;
        return this;
    }

    public WeaponBehavior field(FieldKind kind, float radius, int ticks, float damagePerSecond) {
        this.field = kind;
        this.fieldRadius = radius;
        this.fieldTicks = ticks;
        this.fieldDamage = damagePerSecond;
        return this;
    }

    /** Field left instead of the main one when the shooter is sneaking. */
    public WeaponBehavior altField(FieldKind kind, float radius, int ticks, float damagePerSecond) {
        this.altField = kind;
        this.altFieldRadius = radius;
        this.altFieldTicks = ticks;
        this.altFieldDamage = damagePerSecond;
        return this;
    }

    public int getPierce() {
        return pierce;
    }

    public float getPierceFalloff() {
        return pierceFalloff;
    }

    public float getArmorPiercing() {
        return armorPiercing;
    }

    public float getKnockback() {
        return knockback;
    }

    public int getBurnSeconds() {
        return burnSeconds;
    }

    public int getRadiation() {
        return radiation;
    }

    public boolean isHitscan() {
        return hitscanRange > 0.0F;
    }

    public float getHitscanRange() {
        return hitscanRange;
    }

    public boolean isThroughBlocks() {
        return throughBlocks;
    }

    public int getPhaseBlocks() {
        return phaseBlocks;
    }

    public boolean hasHeat() {
        return heatPerShot > 0.0F && maxHeat > 0.0F;
    }

    public float getHeatPerShot() {
        return heatPerShot;
    }

    public float getMaxHeat() {
        return maxHeat;
    }

    public float getCoolingPerTick() {
        return coolingPerTick;
    }

    public int getChronoTicks() {
        return chronoTicks;
    }

    public boolean isEraser() {
        return eraser;
    }

    public FieldKind getField(boolean sneaking) {
        return sneaking && altField != FieldKind.NONE ? altField : field;
    }

    public float getFieldRadius(boolean sneaking) {
        return sneaking && altField != FieldKind.NONE ? altFieldRadius : fieldRadius;
    }

    public int getFieldTicks(boolean sneaking) {
        return sneaking && altField != FieldKind.NONE ? altFieldTicks : fieldTicks;
    }

    public float getFieldDamage(boolean sneaking) {
        return sneaking && altField != FieldKind.NONE ? altFieldDamage : fieldDamage;
    }
}
