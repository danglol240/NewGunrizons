package com.gtnewhorizon.newgunrizons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;

import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.entities.EntityGrenade;
import com.gtnewhorizon.newgunrizons.entities.EntityShellCasing;
import com.gtnewhorizon.newgunrizons.grenade.GrenadeAttackAspect;
import com.gtnewhorizon.newgunrizons.items.components.ItemBulletCasingLarge;
import com.gtnewhorizon.newgunrizons.items.components.ItemBulletCasingMedium;
import com.gtnewhorizon.newgunrizons.items.components.ItemBulletCasingSmall;
import com.gtnewhorizon.newgunrizons.items.components.ItemFiringMechanism;
import com.gtnewhorizon.newgunrizons.items.components.ItemFiringMechanismAdvanced;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunBarrelStainless;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunBarrelSteel;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunBarrelTitanium;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunBarrelTungstenSteel;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunReceiverStainless;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunReceiverSteel;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunReceiverTitanium;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunReceiverTungstenSteel;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunStockCarbon;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunStockPlastic;
import com.gtnewhorizon.newgunrizons.items.components.ItemGunStockWood;
import com.gtnewhorizon.newgunrizons.items.components.ItemPrecisionLens;
import com.gtnewhorizon.newgunrizons.items.components.ItemWeaponPartKit;
import com.gtnewhorizon.newgunrizons.items.instances.ItemGrenadeInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemMagazineInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.ExplosionMessage;
import com.gtnewhorizon.newgunrizons.network.GrenadeMessage;
import com.gtnewhorizon.newgunrizons.network.GrenadeMessageHandler;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;
import com.gtnewhorizon.newgunrizons.network.TypeRegistry;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessageHandler;
import com.gtnewhorizon.newgunrizons.registry.Attachments;
import com.gtnewhorizon.newgunrizons.registry.AuxiliaryAttachments;
import com.gtnewhorizon.newgunrizons.registry.Bullets;
import com.gtnewhorizon.newgunrizons.registry.Grenades;
import com.gtnewhorizon.newgunrizons.registry.Guns;
import com.gtnewhorizon.newgunrizons.registry.Magazines;
import com.gtnewhorizon.newgunrizons.state.StateManager;
import com.gtnewhorizon.newgunrizons.weapon.MagazineReloadAspect;
import com.gtnewhorizon.newgunrizons.weapon.MagazineState;
import com.gtnewhorizon.newgunrizons.weapon.WeaponAttachmentAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponFireAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponReloadAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponState;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    // Gun component items
    public static Item GunBarrelSteel;
    public static Item GunBarrelStainless;
    public static Item GunBarrelTitanium;
    public static Item GunBarrelTungstenSteel;
    public static Item GunReceiverSteel;
    public static Item GunReceiverStainless;
    public static Item GunReceiverTitanium;
    public static Item GunReceiverTungstenSteel;
    public static Item FiringMechanism;
    public static Item FiringMechanismAdvanced;
    public static Item GunStockWood;
    public static Item GunStockPlastic;
    public static Item GunStockCarbon;
    public static Item BulletCasingSmall;
    public static Item BulletCasingMedium;
    public static Item BulletCasingLarge;
    public static Item PrecisionLens;
    public static Item WeaponPartKit;

    private int modEntityID = 256;

    public void init(Object mod, FMLPreInitializationEvent event) {
        SimpleNetworkWrapper channel = NewGunrizonsMod.CHANNEL;

        GrenadeAttackAspect.INSTANCE.setStateManager(new StateManager<>((s1, s2) -> s1 == s2));
        MagazineReloadAspect.INSTANCE.setStateManager(new StateManager<>((s1, s2) -> s1 == s2));

        StateManager<WeaponState, ItemWeaponInstance> weaponStateManager = new StateManager<>((s1, s2) -> s1 == s2);
        WeaponReloadAspect.INSTANCE.setStateManager(weaponStateManager);
        WeaponFireAspect.INSTANCE.setStateManager(weaponStateManager);
        WeaponAttachmentAspect.INSTANCE.setStateManager(weaponStateManager);

        channel.registerMessage(
            new WeaponActionMessageHandler(WeaponFireAspect.INSTANCE),
            WeaponActionMessage.class,
            15,
            Side.SERVER);
        channel.registerMessage(
            new GrenadeMessageHandler(GrenadeAttackAspect.INSTANCE),
            GrenadeMessage.class,
            20,
            Side.SERVER);
        registerClientMessageHandlers(channel);

        EntityRegistry
            .registerModEntity(EntityBullet.class, "Ammo" + this.modEntityID, this.modEntityID++, mod, 64, 3, true);
        EntityRegistry.registerModEntity(
            EntityShellCasing.class,
            "ShellCasing" + this.modEntityID,
            this.modEntityID++,
            mod,
            64,
            500,
            true);
        EntityRegistry.registerModEntity(
            EntityGrenade.class,
            "Grenade" + this.modEntityID,
            this.modEntityID++,
            mod,
            64,
            10000,
            false);

        // Register gun component items
        GunBarrelSteel = new ItemGunBarrelSteel();
        GunBarrelStainless = new ItemGunBarrelStainless();
        GunBarrelTitanium = new ItemGunBarrelTitanium();
        GunBarrelTungstenSteel = new ItemGunBarrelTungstenSteel();
        GunReceiverSteel = new ItemGunReceiverSteel();
        GunReceiverStainless = new ItemGunReceiverStainless();
        GunReceiverTitanium = new ItemGunReceiverTitanium();
        GunReceiverTungstenSteel = new ItemGunReceiverTungstenSteel();
        FiringMechanism = new ItemFiringMechanism();
        FiringMechanismAdvanced = new ItemFiringMechanismAdvanced();
        GunStockWood = new ItemGunStockWood();
        GunStockPlastic = new ItemGunStockPlastic();
        GunStockCarbon = new ItemGunStockCarbon();
        BulletCasingSmall = new ItemBulletCasingSmall();
        BulletCasingMedium = new ItemBulletCasingMedium();
        BulletCasingLarge = new ItemBulletCasingLarge();
        PrecisionLens = new ItemPrecisionLens();
        WeaponPartKit = new ItemWeaponPartKit();

        GameRegistry.registerItem(GunBarrelSteel, "GunBarrelSteel");
        GameRegistry.registerItem(GunBarrelStainless, "GunBarrelStainless");
        GameRegistry.registerItem(GunBarrelTitanium, "GunBarrelTitanium");
        GameRegistry.registerItem(GunBarrelTungstenSteel, "GunBarrelTungstenSteel");
        GameRegistry.registerItem(GunReceiverSteel, "GunReceiverSteel");
        GameRegistry.registerItem(GunReceiverStainless, "GunReceiverStainless");
        GameRegistry.registerItem(GunReceiverTitanium, "GunReceiverTitanium");
        GameRegistry.registerItem(GunReceiverTungstenSteel, "GunReceiverTungstenSteel");
        GameRegistry.registerItem(FiringMechanism, "FiringMechanism");
        GameRegistry.registerItem(FiringMechanismAdvanced, "FiringMechanismAdvanced");
        GameRegistry.registerItem(GunStockWood, "GunStockWood");
        GameRegistry.registerItem(GunStockPlastic, "GunStockPlastic");
        GameRegistry.registerItem(GunStockCarbon, "GunStockCarbon");
        GameRegistry.registerItem(BulletCasingSmall, "BulletCasingSmall");
        GameRegistry.registerItem(BulletCasingMedium, "BulletCasingMedium");
        GameRegistry.registerItem(BulletCasingLarge, "BulletCasingLarge");
        GameRegistry.registerItem(PrecisionLens, "PrecisionLens");
        GameRegistry.registerItem(WeaponPartKit, "WeaponPartKit");

        Attachments.init();
        AuxiliaryAttachments.init();
        Bullets.init();
        Magazines.init();
        Guns.init();
        Grenades.init();
    }

    protected void registerClientMessageHandlers(SimpleNetworkWrapper channel) {
        channel.registerMessage((msg, ctx) -> null, SpawnParticleMessage.class, 18, Side.CLIENT);
        channel.registerMessage((msg, ctx) -> null, BlockHitMessage.class, 19, Side.CLIENT);
        channel.registerMessage((msg, ctx) -> null, ExplosionMessage.class, 21, Side.CLIENT);
    }

    public void onWeaponFireEffects(EntityLivingBase player, float smokeOffsetX, float smokeOffsetY,
        boolean silencerOn) {
        // No-op on server; overridden in ClientProxy
    }

    public boolean isLocalPlayer(EntityLivingBase player) {
        return false;
    }

    public void registerItem(String name, Item item, IItemRenderer renderer) {
        GameRegistry.registerItem(item, name);
    }

    static {
        TypeRegistry.getInstance()
            .register(MagazineState.class);
        TypeRegistry.getInstance()
            .register(ItemInstance.class);
        TypeRegistry.getInstance()
            .register(ItemWeaponInstance.class);
        TypeRegistry.getInstance()
            .register(ItemMagazineInstance.class);
        TypeRegistry.getInstance()
            .register(WeaponState.class);
        TypeRegistry.getInstance()
            .register(ItemGrenadeInstance.class);
    }
}
