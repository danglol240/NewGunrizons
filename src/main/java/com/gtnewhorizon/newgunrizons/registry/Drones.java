package com.gtnewhorizon.newgunrizons.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.items.DroneLayout;
import com.gtnewhorizon.newgunrizons.items.ItemDrone;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.ProjectileStyle;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior;
import com.gtnewhorizon.newgunrizons.items.WeaponBehavior.FieldKind;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Combat drones from a hobby-grade LV quadcopter to the MAX Sentinel. LV-IV drones fire real rounds that have to be
 * loaded; from LuV on they fire from the battery. Built in the Drone Assembly Complex.
 */
public final class Drones {

    public static ItemDrone ScoutDrone;
    public static ItemDrone AssaultDrone;
    public static ItemDrone GunshipDrone;
    public static ItemDrone HeavyDrone;
    public static ItemDrone RailDrone;
    public static ItemDrone LaserDrone;
    public static ItemDrone PlasmaDrone;
    public static ItemDrone ParticleDrone;
    public static ItemDrone AntimatterDrone;
    public static ItemDrone QuantumDrone;
    public static ItemDrone GravitonDrone;
    public static ItemDrone ChronoDrone;
    public static ItemDrone RiftDrone;
    public static ItemDrone SentinelDrone;

    private static final List<ItemDrone> ALL = new ArrayList<>();

    private Drones() {}

    /** Every drone, LV first. */
    public static List<ItemDrone> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static void init() {
        ScoutDrone = register(
            new ItemDrone.Builder("ScoutDrone", 1).role("Scout quadcopter, 9mm pistol")
                .frame(DroneLayout.QUAD, 0.45F)
                .flight(10.0F, 0.45F, 16.0F)
                .gun(4.0F, 16, 1, 3.0F, 5.0F)
                .rounds(24, Bullets.Bullet9mm)
                .sound(soundOf(Guns.M1911))
                .look(ProjectileStyle.TRACER, 0xFFD070, 0.4F)
                .build());
        AssaultDrone = register(
            new ItemDrone.Builder("AssaultDrone", 2).role("Assault quadcopter, .45 SMG")
                .frame(DroneLayout.QUAD, 0.5F)
                .flight(16.0F, 0.5F, 18.0F)
                .gun(5.0F, 10, 3, 3.0F, 6.0F)
                .rounds(60, Bullets.Bullet45ACP)
                .sound(soundOf(Guns.UMP45))
                .look(ProjectileStyle.TRACER, 0xFFC060, 0.45F)
                .build());
        GunshipDrone = register(
            new ItemDrone.Builder("GunshipDrone", 3).role("Gunship hexacopter, 5.56 rifle")
                .frame(DroneLayout.HEX, 0.55F)
                .flight(24.0F, 0.55F, 22.0F)
                .gun(8.0F, 14, 3, 2.0F, 8.0F)
                .rounds(90, Bullets.Bullet556x45)
                .sound(soundOf(Guns.M4A1))
                .look(ProjectileStyle.TRACER, 0xFFE070, 0.5F)
                .build());
        HeavyDrone = register(
            new ItemDrone.Builder("HeavyDrone", 4).role("Heavy octocopter, twin 7.62 machine guns")
                .frame(DroneLayout.OCTO, 0.65F)
                .flight(36.0F, 0.5F, 24.0F)
                .gun(11.0F, 3, 1, 3.0F, 8.0F)
                .rounds(200, Bullets.Bullet762x51)
                .sound(soundOf(Guns.Mk48MOD1))
                .behavior(new WeaponBehavior().pierce(1, 0.5F))
                .look(ProjectileStyle.TRACER, 0xFFB03C, 0.6F)
                .build());
        RailDrone = register(
            new ItemDrone.Builder("RailDrone", 5).role("Railgun drone, pierces up to 3 extra targets")
                .frame(DroneLayout.QUAD, 0.6F)
                .flight(50.0F, 0.6F, 28.0F)
                .gun(40.0F, 30, 1, 0.5F, 12.0F)
                .rounds(20, Bullets.FerroSlug)
                .sound(soundOf(Guns.Railgun))
                .behavior(new WeaponBehavior().pierce(3, 0.75F))
                .look(ProjectileStyle.RAIL, 0x28C8FF, 0.8F)
                .build());
        LaserDrone = register(
            new ItemDrone.Builder("LaserDrone", 6).role("Laser drone, hitscan")
                .frame(DroneLayout.QUAD, 0.6F)
                .flight(70.0F, 0.65F, 30.0F)
                .gun(30.0F, 8, 1, 0.5F, 0.0F)
                .sound(soundOf(Guns.LaserRifle))
                .behavior(new WeaponBehavior().hitscan(30.0F))
                .look(ProjectileStyle.LASER, 0xFF46BE, 0.8F)
                .build());
        PlasmaDrone = register(
            new ItemDrone.Builder("PlasmaDrone", 7).role("Plasma drone, burns and knocks back")
                .frame(DroneLayout.HEX, 0.65F)
                .flight(90.0F, 0.6F, 28.0F)
                .gun(55.0F, 20, 1, 1.0F, 4.0F)
                .sound(soundOf(Guns.PlasmaRifle))
                .behavior(
                    new WeaponBehavior().burn(4)
                        .knockback(0.5F)
                        .armorPiercing(0.3F))
                .look(ProjectileStyle.PLASMA, 0xFF781E, 1.2F)
                .build());
        ParticleDrone = register(
            new ItemDrone.Builder("ParticleDrone", 8).role("Particle beam drone, pierces armor")
                .frame(DroneLayout.HEX, 0.65F)
                .flight(120.0F, 0.65F, 32.0F)
                .gun(110.0F, 25, 1, 0.5F, 10.0F)
                .sound(soundOf(Guns.ParticleBeamRifle))
                .behavior(
                    new WeaponBehavior().pierce(3, 0.85F)
                        .armorPiercing(0.75F))
                .look(ProjectileStyle.HELIX, 0x6EA0FF, 1.0F)
                .build());
        AntimatterDrone = register(
            new ItemDrone.Builder("AntimatterDrone", 9).role("Antimatter drone, irradiates targets")
                .frame(DroneLayout.OCTO, 0.7F)
                .flight(160.0F, 0.6F, 32.0F)
                .gun(200.0F, 40, 1, 0.5F, 5.0F)
                .sound(soundOf(Guns.ExoticMatterRifle))
                .behavior(
                    new WeaponBehavior().radiation(2)
                        .armorPiercing(0.5F))
                .look(ProjectileStyle.ORB_RING, 0xB446FF, 1.2F)
                .build());
        QuantumDrone = register(
            new ItemDrone.Builder("QuantumDrone", 10).role("Quantum drone, shots phase through 8 blocks")
                .frame(DroneLayout.QUAD, 0.65F)
                .flight(200.0F, 0.7F, 34.0F)
                .gun(260.0F, 30, 1, 0.3F, 8.0F)
                .sound(soundOf(Guns.QuantumPhaseRifle))
                .behavior(
                    new WeaponBehavior().phase(8)
                        .armorPiercing(0.6F))
                .look(ProjectileStyle.PHASE, 0x505AFF, 1.0F)
                .build());
        GravitonDrone = register(
            new ItemDrone.Builder("GravitonDrone", 11).role("Graviton drone, gravity wells")
                .frame(DroneLayout.HEX, 0.7F)
                .flight(250.0F, 0.65F, 34.0F)
                .gun(280.0F, 60, 1, 0.5F, 6.0F)
                .sound(soundOf(Guns.GravitonCannon))
                .behavior(new WeaponBehavior().field(FieldKind.GRAVITY_WELL, 5.0F, 40, 6.0F))
                .look(ProjectileStyle.GRAVITON, 0x28E6C8, 1.0F)
                .build());
        ChronoDrone = register(
            new ItemDrone.Builder("ChronoDrone", 12).role("Chrono drone, freezes its targets")
                .frame(DroneLayout.HEX, 0.7F)
                .flight(300.0F, 0.7F, 36.0F)
                .gun(320.0F, 50, 1, 0.3F, 8.0F)
                .sound(soundOf(Guns.ChronoRifle))
                .behavior(new WeaponBehavior().chrono(80))
                .look(ProjectileStyle.CHRONO, 0x5AFF6E, 1.0F)
                .build());
        RiftDrone = register(
            new ItemDrone.Builder("RiftDrone", 13).role("Rift drone, cuts everything on a line")
                .frame(DroneLayout.OCTO, 0.75F)
                .flight(360.0F, 0.7F, 36.0F)
                .gun(420.0F, 50, 1, 0.3F, 0.0F)
                .sound(soundOf(Guns.DimensionalCutter))
                .behavior(
                    new WeaponBehavior().hitscan(36.0F)
                        .throughBlocks()
                        .pierce(16, 1.0F)
                        .armorPiercing(1.0F))
                .look(ProjectileStyle.RIFT, 0xDC50FF, 0.8F)
                .build());
        SentinelDrone = register(
            new ItemDrone.Builder("SentinelDrone", 14).role("Sentinel, the ultimate escort")
                .frame(DroneLayout.OCTO, 0.8F)
                .flight(500.0F, 0.75F, 40.0F)
                .gun(600.0F, 40, 1, 0.2F, 0.0F)
                .sound(soundOf(Guns.RealityEraser))
                .behavior(
                    new WeaponBehavior().hitscan(40.0F)
                        .pierce(8, 1.0F)
                        .armorPiercing(1.0F))
                .look(ProjectileStyle.ERASE, 0xFFCD46, 0.6F)
                .build());
    }

    private static ItemDrone register(ItemDrone drone) {
        GameRegistry.registerItem(drone, drone.getName());
        ALL.add(drone);
        return drone;
    }

    private static String soundOf(Item gun) {
        return gun instanceof ItemWeapon ? ((ItemWeapon) gun).getShootSound() : null;
    }
}
