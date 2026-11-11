package com.gtnewhorizon.newgunrizons.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.items.ItemVehicleComponent;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior.FieldKind;
import com.gtnewhorizon.newgunrizons.vehicle.FuelType;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleMode;
import com.gtnewhorizon.newgunrizons.vehicle.VehicleWeapon;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * One vehicle per tier, from a diesel jeep to a reality-bending ark. LV-EV run on combustion fuel and fight with real
 * rounds; IV and up are electric, ZPM burns plasma, and from UV on a tier core refills the battery in one go.
 * Seats beyond the driver's man the turrets; an empty gunner seat leaves its gun to the driver. Built in the
 * Vehicle Assembly Hangar.
 */
public final class Vehicles {

    public static ItemVehicleComponent Component;

    public static ItemVehicle FieldJeep;
    public static ItemVehicle OffroadTruck;
    public static ItemVehicle ArmoredAPC;
    public static ItemVehicle BattleTank;
    public static ItemVehicle HoverJeep;
    public static ItemVehicle TiltrotorVTOL;
    public static ItemVehicle PlasmaFighter;
    public static ItemVehicle ParticleCruiser;
    public static ItemVehicle AntimatterFrigate;
    public static ItemVehicle QuantumRingship;
    public static ItemVehicle GravityTank;
    public static ItemVehicle SpacetimeSkimmer;
    public static ItemVehicle DimensionalLeviathan;
    public static ItemVehicle GenesisArk;

    private static final List<ItemVehicle> ALL = new ArrayList<>();

    private Vehicles() {}

    /** Every vehicle, LV first. */
    public static List<ItemVehicle> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static void init() {
        Component = new ItemVehicleComponent();
        GameRegistry.registerItem(Component, "VehicleComponent");

        FieldJeep = register(
            new ItemVehicle.Builder("FieldJeep", 1).role("Light jeep with a pintle machine gun")
                .drive(VehicleMode.GROUND, 0.5F, 4.5F)
                .fuel(FuelType.DIESEL, null)
                .hull(60.0F, 0.15F)
                .weapon(
                    VehicleWeapon.turret("Pintle MG", 0)
                        .shot(7.0F, 3, 2.0F, 8.0F)
                        .rounds(150, Bullets.Bullet762x39)
                        .sound(soundOf(Guns.AKM))
                        .build())
                .build());
        OffroadTruck = register(
            new ItemVehicle.Builder("OffroadTruck", 2).role("Six-wheel truck, roof gunner, room for four")
                .drive(VehicleMode.GROUND, 0.55F, 3.5F)
                .fuel(FuelType.DIESEL, null)
                .hull(100.0F, 0.2F)
                .weapon(
                    VehicleWeapon.turret("Roof machine gun", 0)
                        .shot(9.0F, 3, 2.0F, 8.0F)
                        .rounds(200, Bullets.Bullet762x51)
                        .sound(soundOf(Guns.Mk48MOD1))
                        .build())
                .build());
        ArmoredAPC = register(
            new ItemVehicle.Builder("ArmoredAPC", 3).role("Armoured personnel carrier, remote .50 turret")
                .drive(VehicleMode.GROUND, 0.6F, 3.2F)
                .fuel(FuelType.HYBRID, null)
                .hull(220.0F, 0.4F)
                .weapon(
                    VehicleWeapon.turret(".50 BMG turret", 0)
                        .shot(16.0F, 4, 1.5F, 10.0F)
                        .rounds(200, Bullets.BMG50)
                        .sound(soundOf(Guns.M107))
                        .behavior(new WeaponBehavior().pierce(1, 0.6F))
                        .build())
                .build());
        BattleTank = register(
            new ItemVehicle.Builder("BattleTank", 4).role("Main battle tank, 120 mm cannon and coaxial MG")
                .drive(VehicleMode.GROUND, 0.4F, 2.5F)
                .fuel(FuelType.HYBRID, null)
                .hull(420.0F, 0.6F)
                .weapon(
                    VehicleWeapon.turret("120 mm cannon", 0)
                        .shot(60.0F, 50, 0.3F, 5.0F)
                        .explosive(3.0F)
                        .energy(6.0F)
                        .sound(soundOf(Guns.SMAW))
                        .look(ProjectileStyle.TRACER, 0xFFB03C, 1.4F)
                        .build())
                .weapon(
                    VehicleWeapon.turret("Coaxial MG", 0)
                        .secondary()
                        .shot(9.0F, 3, 2.0F, 8.0F)
                        .rounds(250, Bullets.Bullet762x51)
                        .sound(soundOf(Guns.Mk48MOD1))
                        .build())
                .build());
        HoverJeep = register(
            new ItemVehicle.Builder("HoverJeep", 5).role("Hover car with a railgun, crosses water")
                .drive(VehicleMode.HOVER, 0.8F, 4.0F)
                .fuel(FuelType.EU, null)
                .hull(300.0F, 0.45F)
                .weapon(
                    VehicleWeapon.turret("Railgun", 0)
                        .shot(45.0F, 25, 0.5F, 12.0F)
                        .rounds(30, Bullets.FerroSlug)
                        .sound(soundOf(Guns.Railgun))
                        .behavior(new WeaponBehavior().pierce(3, 0.75F))
                        .look(ProjectileStyle.RAIL, 0x28C8FF, 0.9F)
                        .build())
                .build());
        TiltrotorVTOL = register(
            new ItemVehicle.Builder("TiltrotorVTOL", 6).role("Tiltrotor VTOL, chin laser and rocket pods")
                .drive(VehicleMode.AIR, 1.0F, 4.0F)
                .fuel(FuelType.EU, null)
                .hull(500.0F, 0.45F)
                .weapon(laserTurret("Chin laser", 0, 30.0F, 32.0F, 0xFF46BE, soundOf(Guns.LaserRifle)))
                .weapon(rocket("Rocket pod L", 0, 50.0F, 2.5F, 0xFF46BE))
                .weapon(rocket("Rocket pod R", 1, 50.0F, 2.5F, 0xFF46BE))
                .build());
        PlasmaFighter = register(
            new ItemVehicle.Builder("PlasmaFighter", 7).role("Plasma space fighter")
                .drive(VehicleMode.SPACE, 1.6F, 6.0F)
                .fuel(FuelType.PLASMA, null)
                .hull(600.0F, 0.45F)
                .weapon(plasmaGun("Plasma cannon L", 0))
                .weapon(plasmaGun("Plasma cannon R", 1))
                .weapon(rocket("Plasma missile", 0, 90.0F, 3.0F, 0xFF781E))
                .build());
        ParticleCruiser = register(
            new ItemVehicle.Builder("ParticleCruiser", 8).role("Particle-drive cruiser, two gunner turrets")
                .drive(VehicleMode.SPACE, 1.6F, 3.0F)
                .fuel(FuelType.EU, Parts.ParticleAcceleratorSegment)
                .hull(1100.0F, 0.6F)
                .weapon(
                    VehicleWeapon.fixed("Particle lance", 0)
                        .shot(140.0F, 20, 0.5F, 10.0F)
                        .energy(6.0F)
                        .sound(soundOf(Guns.ParticleBeamRifle))
                        .behavior(
                            new WeaponBehavior().pierce(3, 0.85F)
                                .armorPiercing(0.75F))
                        .look(ProjectileStyle.HELIX, 0x6EA0FF, 1.2F)
                        .build())
                .weapon(laserTurret("Port defence laser", 0, 40.0F, 40.0F, 0x6EA0FF, soundOf(Guns.LaserRifle)))
                .weapon(laserTurret("Starboard defence laser", 1, 40.0F, 40.0F, 0x6EA0FF, soundOf(Guns.LaserRifle)))
                .build());
        AntimatterFrigate = register(
            new ItemVehicle.Builder("AntimatterFrigate", 9).role("Antimatter frigate, two laser batteries")
                .drive(VehicleMode.SPACE, 1.8F, 2.5F)
                .fuel(FuelType.EU, Parts.AntimatterTrap)
                .hull(1800.0F, 0.65F)
                .weapon(
                    VehicleWeapon.fixed("Antimatter cannon", 0)
                        .shot(260.0F, 40, 0.5F, 5.0F)
                        .explosive(3.0F)
                        .energy(10.0F)
                        .sound(soundOf(Guns.ExoticMatterRifle))
                        .behavior(
                            new WeaponBehavior().radiation(2)
                                .armorPiercing(0.5F))
                        .look(ProjectileStyle.ORB_RING, 0xB446FF, 1.4F)
                        .build())
                .weapon(laserTurret("Port laser battery", 0, 60.0F, 40.0F, 0xB446FF, soundOf(Guns.LaserRifle)))
                .weapon(laserTurret("Starboard laser battery", 1, 60.0F, 40.0F, 0xB446FF, soundOf(Guns.LaserRifle)))
                .build());
        QuantumRingship = register(
            new ItemVehicle.Builder("QuantumRingship", 10).role("Quantum ringship, shots phase through walls")
                .drive(VehicleMode.SPACE, 2.0F, 4.0F)
                .fuel(FuelType.EU, Parts.QuantumEntanglementCore)
                .hull(2400.0F, 0.7F)
                .weapon(
                    VehicleWeapon.fixed("Phase cannon", 0)
                        .shot(300.0F, 30, 0.3F, 8.0F)
                        .energy(8.0F)
                        .sound(soundOf(Guns.QuantumPhaseRifle))
                        .behavior(
                            new WeaponBehavior().phase(8)
                                .armorPiercing(0.6F))
                        .look(ProjectileStyle.PHASE, 0x505AFF, 1.2F)
                        .build())
                .weapon(
                    VehicleWeapon.turret("Ventral phase turret", 0)
                        .shot(120.0F, 10, 0.5F, 8.0F)
                        .energy(3.0F)
                        .sound(soundOf(Guns.QuantumPhaseRifle))
                        .behavior(new WeaponBehavior().phase(4))
                        .look(ProjectileStyle.PHASE, 0x505AFF, 0.8F)
                        .build())
                .build());
        GravityTank = register(
            new ItemVehicle.Builder("GravityTank", 11).role("Gravity hover tank, graviton cannon")
                .drive(VehicleMode.HOVER, 1.0F, 3.0F)
                .fuel(FuelType.EU, Parts.GravitonFieldEmitter)
                .hull(3600.0F, 0.8F)
                .weapon(
                    VehicleWeapon.turret("Graviton cannon", 0)
                        .shot(320.0F, 60, 0.3F, 6.0F)
                        .energy(10.0F)
                        .sound(soundOf(Guns.GravitonCannon))
                        .behavior(new WeaponBehavior().field(FieldKind.GRAVITY_WELL, 5.0F, 40, 6.0F))
                        .look(ProjectileStyle.GRAVITON, 0x28E6C8, 1.4F)
                        .build())
                .weapon(
                    VehicleWeapon.turret("Repulsor", 0)
                        .secondary()
                        .shot(80.0F, 20, 0.5F, 6.0F)
                        .energy(4.0F)
                        .sound(soundOf(Guns.GravitonCannon))
                        .behavior(new WeaponBehavior().field(FieldKind.REPULSE, 4.0F, 10, 0.0F))
                        .look(ProjectileStyle.PULSE, 0x28E6C8, 1.0F)
                        .build())
                .build());
        SpacetimeSkimmer = register(
            new ItemVehicle.Builder("SpacetimeSkimmer", 12).role("Spacetime skimmer, freezes its targets")
                .drive(VehicleMode.SPACE, 2.4F, 4.0F)
                .fuel(FuelType.EU, Parts.ChronoStabilizer)
                .hull(4200.0F, 0.8F)
                .weapon(
                    VehicleWeapon.fixed("Chrono lance", 0)
                        .shot(380.0F, 40, 0.3F, 8.0F)
                        .energy(8.0F)
                        .sound(soundOf(Guns.ChronoRifle))
                        .behavior(new WeaponBehavior().chrono(80))
                        .look(ProjectileStyle.CHRONO, 0x5AFF6E, 1.2F)
                        .build())
                .weapon(
                    VehicleWeapon.turret("Chrono turret", 0)
                        .shot(150.0F, 10, 0.5F, 8.0F)
                        .energy(3.0F)
                        .sound(soundOf(Guns.ChronoRifle))
                        .behavior(new WeaponBehavior().chrono(30))
                        .look(ProjectileStyle.CHRONO, 0x5AFF6E, 0.8F)
                        .build())
                .build());
        DimensionalLeviathan = register(
            new ItemVehicle.Builder("DimensionalLeviathan", 13).role("Dimensional capital ship, three void turrets")
                .drive(VehicleMode.SPACE, 2.0F, 2.0F)
                .fuel(FuelType.EU, Parts.DimensionalRiftEmitter)
                .hull(7000.0F, 0.85F)
                .weapon(
                    VehicleWeapon.fixed("Rift beam", 0)
                        .shot(500.0F, 40, 0.3F, 0.0F)
                        .energy(12.0F)
                        .sound(soundOf(Guns.DimensionalCutter))
                        .behavior(
                            new WeaponBehavior().hitscan(48.0F)
                                .throughBlocks()
                                .pierce(16, 1.0F)
                                .armorPiercing(1.0F))
                        .look(ProjectileStyle.RIFT, 0xDC50FF, 1.0F)
                        .build())
                .weapon(laserTurret("Void laser 1", 0, 180.0F, 40.0F, 0xDC50FF, soundOf(Guns.LaserRifle)))
                .weapon(laserTurret("Void laser 2", 1, 180.0F, 40.0F, 0xDC50FF, soundOf(Guns.LaserRifle)))
                .weapon(laserTurret("Void laser 3", 2, 180.0F, 40.0F, 0xDC50FF, soundOf(Guns.LaserRifle)))
                .build());
        GenesisArk = register(
            new ItemVehicle.Builder("GenesisArk", 14).role("The Genesis Ark, the last ship")
                .drive(VehicleMode.SPACE, 3.0F, 3.0F)
                .fuel(FuelType.EU, Parts.RealityEngine)
                .hull(10000.0F, 0.9F)
                .weapon(
                    VehicleWeapon.fixed("Reality eraser", 0)
                        .shot(900.0F, 30, 0.2F, 0.0F)
                        .energy(16.0F)
                        .sound(soundOf(Guns.RealityEraser))
                        .behavior(
                            new WeaponBehavior().hitscan(56.0F)
                                .pierce(8, 1.0F)
                                .armorPiercing(1.0F))
                        .look(ProjectileStyle.ERASE, 0xFFCD46, 0.8F)
                        .build())
                .weapon(laserTurret("Genesis lance 1", 0, 300.0F, 40.0F, 0xFFCD46, soundOf(Guns.LaserRifle)))
                .weapon(laserTurret("Genesis lance 2", 1, 300.0F, 40.0F, 0xFFCD46, soundOf(Guns.LaserRifle)))
                .build());
    }

    private static VehicleWeapon laserTurret(String name, int turret, float damage, float range, int color,
        String sound) {
        return VehicleWeapon.turret(name, turret)
            .shot(damage, 6, 0.5F, 0.0F)
            .energy(2.0F)
            .sound(sound)
            .behavior(new WeaponBehavior().hitscan(range))
            .look(ProjectileStyle.LASER, color, 0.8F)
            .build();
    }

    private static VehicleWeapon rocket(String name, int mount, float damage, float radius, int color) {
        return VehicleWeapon.fixed(name, mount)
            .secondary()
            .shot(damage, 30, 0.8F, 3.5F)
            .explosive(radius)
            .energy(6.0F)
            .sound(soundOf(Guns.SMAW))
            .look(ProjectileStyle.PLASMA, color, 1.0F)
            .build();
    }

    private static VehicleWeapon plasmaGun(String name, int mount) {
        return VehicleWeapon.fixed(name, mount)
            .shot(55.0F, 8, 0.6F, 5.0F)
            .energy(3.0F)
            .sound(soundOf(Guns.PlasmaRifle))
            .behavior(
                new WeaponBehavior().burn(4)
                    .knockback(0.5F))
            .look(ProjectileStyle.PLASMA, 0xFF781E, 1.0F)
            .build();
    }

    private static ItemVehicle register(ItemVehicle v) {
        GameRegistry.registerItem(v, v.getName());
        ALL.add(v);
        return v;
    }

    private static String soundOf(Item gun) {
        return gun instanceof ItemWeapon ? ((ItemWeapon) gun).getShootSound() : null;
    }
}
