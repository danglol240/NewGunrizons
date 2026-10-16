package com.gtnewhorizon.newgunrizons.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.attachment.CompatibleAttachment;
import com.gtnewhorizon.newgunrizons.client.render.RenderableState;
import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.entities.EntityShellCasing;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceFactory;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceRegistry;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.registry.Sounds;
import com.gtnewhorizon.newgunrizons.weapon.WeaponState;

import lombok.Getter;

public class ItemWeapon extends Item implements ItemInstanceFactory<ItemWeaponInstance>, Updatable {

    @Getter
    private String shootSound;
    @Getter
    private String silencedShootSound;
    @Getter
    private String reloadSound;
    @Getter
    private String reloadIterationSound;
    @Getter
    private String allReloadIterationsCompletedSound;
    @Getter
    private String unloadSound;
    @Getter
    private final String name;
    @Getter
    private final String textureName;
    @Getter
    private final int ammoCapacity;
    @Getter
    private final float recoil;
    @Getter
    private final int cameraRecoilDurationMs;
    @Getter
    private final ItemAmmo ammo;
    @Getter
    private final float fireRate;
    @Getter
    private final List<Integer> maxShots;

    private final String crosshair;
    private final String crosshairRunning;
    private final String crosshairZoomed;

    private final float projectileSpeed;
    private final float projectileDamage;
    private final float projectileExplosionRadius;
    private final float projectileGravityVelocity;
    private final float inaccuracy;

    @Getter
    private final int pellets;
    @Getter
    private final boolean smokeEnabled;

    private final Function<ItemStack, List<String>> informationProvider;
    @Getter
    private final Map<ItemAttachment, CompatibleAttachment> compatibleAttachments;
    @Getter
    private final List<ItemBullet> compatibleBullets;
    @Getter
    private final WeaponRenderer renderer;
    @Getter
    private final long pumpTimeoutMilliseconds;
    @Getter
    private final float shellCasingForwardOffset;
    @Getter
    private final float shellCasingVerticalOffset;
    @Getter
    private final boolean shellCasingEjectEnabled;
    private final boolean hasIteratedLoad;
    @Getter
    private final float silencedShootSoundVolume;
    @Getter
    private final float shootSoundVolume;

    private ItemWeapon(ItemWeapon.Builder builder) {
        this.name = builder.name;
        this.textureName = builder.textureName;
        this.ammoCapacity = builder.ammoCapacity;
        this.recoil = builder.recoil;
        this.cameraRecoilDurationMs = builder.cameraRecoilDurationMs;
        this.ammo = builder.ammo;
        this.fireRate = builder.fireRate;
        this.maxShots = new ArrayList<>(builder.maxShots);
        this.crosshair = builder.crosshair;
        this.crosshairRunning = builder.crosshairRunning;
        this.crosshairZoomed = builder.crosshairZoomed;
        this.projectileSpeed = builder.spawnEntitySpeed;
        this.projectileDamage = builder.spawnEntityDamage;
        this.projectileExplosionRadius = builder.spawnEntityExplosionRadius;
        this.projectileGravityVelocity = builder.spawnEntityGravityVelocity;
        this.inaccuracy = builder.inaccuracy;
        this.pellets = builder.pellets;
        this.smokeEnabled = builder.smokeEnabled;
        this.informationProvider = builder.informationProvider;
        this.compatibleAttachments = builder.compatibleAttachments;
        this.compatibleBullets = new ArrayList<>(builder.compatibleBullets);
        this.renderer = builder.renderer;
        this.pumpTimeoutMilliseconds = builder.pumpTimeoutMilliseconds;
        this.shellCasingForwardOffset = builder.shellCasingForwardOffset;
        this.shellCasingVerticalOffset = builder.shellCasingVerticalOffset;
        this.shellCasingEjectEnabled = builder.shellCasingEjectEnabled;
        this.hasIteratedLoad = builder.hasIteratedLoad;
        this.silencedShootSoundVolume = builder.silencedShootSoundVolume;
        this.shootSoundVolume = builder.shootSoundVolume;
        this.setMaxStackSize(1);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        stack.setTagCompound(new NBTTagCompound());
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (this.ammoCapacity <= 0) {
            return 0.0;
        }
        return 1.0 - (double) ItemWeaponInstance.getAmmo(stack) / (double) this.ammoCapacity;
    }

    public void toggleAiming() {
        ItemWeaponInstance mainHandHeldWeaponInstance = ItemInstanceRegistry.getMainHeldWeapon();
        if (mainHandHeldWeaponInstance != null && mainHandHeldWeaponInstance.getState() == WeaponState.IDLE) {
            mainHandHeldWeaponInstance.setAimed(!mainHandHeldWeaponInstance.isAimed());
        }

    }

    public String getCrosshair(ItemWeaponInstance weaponInstance) {
        if (weaponInstance.isAimed()) {
            String crosshair = null;
            ItemAttachment scopeAttachment = ItemWeaponInstance
                .getActiveAttachment(AttachmentCategory.SCOPE, weaponInstance);
            if (scopeAttachment != null) {
                crosshair = scopeAttachment.getCrosshair();
            }

            if (crosshair == null) {
                crosshair = this.crosshairZoomed;
            }

            return crosshair;
        } else {
            return weaponInstance.getPlayer()
                .isSprinting() ? this.crosshairRunning : this.crosshair;
        }
    }

    public static boolean isActiveAttachment(ItemWeaponInstance weaponInstance, ItemAttachment attachment) {
        return weaponInstance != null && ItemWeaponInstance.isActiveAttachment(attachment, weaponInstance);
    }

    public int getCurrentAmmo() {
        ItemWeaponInstance state = ItemInstanceRegistry.getMainHeldWeapon();
        return state.getAmmo();
    }

    public List<CompatibleAttachment> getActiveAttachments(EntityLivingBase player, ItemStack itemStack) {
        return ItemWeaponInstance.getActiveAttachments(player, itemStack);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip, boolean flag) {
        if (tooltip != null && this.informationProvider != null) {
            tooltip.addAll(this.informationProvider.apply(itemStack));
        }
    }

    public void reloadHeldItem(EntityPlayer player) {
        ItemWeaponInstance instance = ItemInstanceRegistry.getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (instance != null) {
            instance.tryReload();
        }
    }

    public void update(EntityPlayer player) {
        ItemWeaponInstance instance = ItemInstanceRegistry.getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (instance != null) {
            instance.update();
        }
    }

    public void tryFire(EntityPlayer player) {
        ItemWeaponInstance instance = ItemInstanceRegistry.getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (instance != null) {
            instance.tryFire();
        }
    }

    public void tryStopFire(EntityPlayer player) {
        ItemWeaponInstance instance = ItemInstanceRegistry.getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (instance != null) {
            instance.tryStopFire();
        }
    }

    public ItemWeaponInstance createItemInstance(EntityPlayer player, ItemStack itemStack, int slot) {
        ItemWeaponInstance instance = new ItemWeaponInstance(slot, player, itemStack);
        instance.setRecoil(this.recoil);
        instance.setMaxShots(this.maxShots.get(0));

        for (CompatibleAttachment compatibleAttachment : this.getCompatibleAttachments()
            .values()) {
            ItemAttachment attachment = compatibleAttachment.getAttachment();
            if (compatibleAttachment.isDefault() && attachment.getApplyHandler() != null) {
                attachment.getApplyHandler()
                    .apply(attachment, instance);
            }
        }

        return instance;
    }

    public void toggleClientAttachmentSelectionMode(EntityPlayer player) {
        ItemWeaponInstance instance = ItemInstanceRegistry.getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (instance != null) {
            instance.toggleAttachmentMode();
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack itemStack, EntityPlayer player) {
        ItemWeaponInstance instance = ItemWeaponInstance.fromStack(itemStack);
        return instance == null || instance.getState() == WeaponState.IDLE;
    }

    public void changeFireMode(ItemWeaponInstance instance) {
        int currentIndex = this.maxShots.indexOf(instance.getMaxShots());
        int nextIndex = (currentIndex + 1) % this.maxShots.size();
        int result = this.maxShots.get(nextIndex);
        instance.setMaxShots(result);
        String message;
        if (result == 1) {
            message = StatCollector.translateToLocalFormatted("gui.firearmMode.semi");
        } else if (result == Integer.MAX_VALUE) {
            message = StatCollector.translateToLocalFormatted("gui.firearmMode.auto");
        } else {
            message = StatCollector.translateToLocalFormatted("gui.firearmMode.burst");
        }

        StatusMessageManager.INSTANCE
            .addMessage(StatCollector.translateToLocalFormatted("gui.firearmMode", message), 1000L);
        if (Sounds.FIRE_MODE_SWITCH != null) {
            instance.getPlayer()
                .playSound(Sounds.FIRE_MODE_SWITCH, 1.0F, 1.0F);
        }

        NewGunrizonsMod.CHANNEL.sendToServer(
            new WeaponActionMessage(WeaponActionMessage.CHANGE_FIRE_MODE, instance.getPlayer().inventory.currentItem));
    }

    public void spawnBullet(EntityPlayer player) {
        EntityBullet bullet = new EntityBullet(
            this,
            player.worldObj,
            player,
            this.projectileSpeed,
            this.projectileGravityVelocity,
            this.inaccuracy,
            this.projectileDamage,
            this.projectileExplosionRadius);
        bullet.setPositionAndDirection();
        bullet.captureSpawnDirection();
        player.worldObj.spawnEntityInWorld(bullet);
    }

    public void spawnShell(ItemWeaponInstance weaponInstance, EntityPlayer player) {
        EntityShellCasing shell = new EntityShellCasing(weaponInstance, player.worldObj, player, 0.1F, 0.05F, 20.0F);
        shell.setPositionAndDirection();
        player.worldObj.spawnEntityInWorld(shell);
    }

    /**
     * Returns the animation duration for a given state, derived from the Bedrock animation file.
     */
    public long getAnimationDurationMs(RenderableState state) {
        return this.renderer.getAnimationDurationMs(state);
    }

    public void incrementZoom(ItemWeaponInstance instance) {
        Item scopeItem = instance.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
        if (scopeItem instanceof ItemScope && ((ItemScope) scopeItem).isOptical()) {
            float minZoom = ((ItemScope) scopeItem).getMinZoom();
            float maxZoom = ((ItemScope) scopeItem).getMaxZoom();
            float increment = (minZoom - maxZoom) / 20.0F;
            float zoom = instance.getZoom();
            if (zoom > maxZoom) {
                zoom = Math.max(zoom - increment, maxZoom);
            }

            instance.setZoom(zoom);
            float ratio = (minZoom - zoom) / (minZoom - maxZoom);
            StatusMessageManager.INSTANCE.addMessage(
                StatCollector.translateToLocalFormatted("gui.currentZoom", Math.round(ratio * 100.0F)),
                800L);
            if (Sounds.ZOOM != null) {
                instance.getPlayer()
                    .playSound(Sounds.ZOOM, 1.0F, 1.0F);
            }

            NewGunrizonsMod.CHANNEL.sendToServer(
                new WeaponActionMessage(WeaponActionMessage.ZOOM_IN, instance.getPlayer().inventory.currentItem));
        }
    }

    public void decrementZoom(ItemWeaponInstance instance) {
        Item scopeItem = instance.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
        if (scopeItem instanceof ItemScope && ((ItemScope) scopeItem).isOptical()) {
            float minZoom = ((ItemScope) scopeItem).getMinZoom();
            float maxZoom = ((ItemScope) scopeItem).getMaxZoom();
            float increment = (minZoom - maxZoom) / 20.0F;
            float zoom = instance.getZoom();
            if (zoom < minZoom) {
                zoom = Math.min(zoom + increment, minZoom);
            }

            instance.setZoom(zoom);
            float ratio = (minZoom - zoom) / (minZoom - maxZoom);
            StatusMessageManager.INSTANCE.addMessage(
                StatCollector.translateToLocalFormatted("gui.currentZoom", Math.round(ratio * 100.0F)),
                800L);
            if (Sounds.ZOOM != null) {
                instance.getPlayer()
                    .playSound(Sounds.ZOOM, 1.0F, 1.0F);
            }

            NewGunrizonsMod.CHANNEL.sendToServer(
                new WeaponActionMessage(WeaponActionMessage.ZOOM_OUT, instance.getPlayer().inventory.currentItem));
        }
    }

    public boolean hasIteratedLoad() {
        return this.hasIteratedLoad;
    }

    public static class Builder {

        @Getter
        public String name;
        @Getter
        public String textureName;

        public int ammoCapacity = 0;
        float recoil = 1.0F;
        int cameraRecoilDurationMs = 50;
        private String shootSound;
        private String silencedShootSound;
        private String reloadSound;
        private String reloadIterationSound;
        private String allReloadIterationsCompletedSound;
        private String unloadSound;
        public ItemAmmo ammo;
        public float fireRate = 0.5F;
        private CreativeTabs creativeTab;
        private WeaponRenderer renderer;
        public List<Integer> maxShots = new ArrayList<>();
        String crosshair;
        String crosshairRunning;
        String crosshairZoomed;

        public float spawnEntitySpeed = 10.0F;
        public float spawnEntityDamage;
        public float spawnEntityExplosionRadius;
        public float spawnEntityGravityVelocity;

        Map<ItemAttachment, CompatibleAttachment> compatibleAttachments = new HashMap<>();
        List<ItemBullet> compatibleBullets = new ArrayList<>();

        private Class<? extends EntityBullet> spawnEntityClass;
        public long pumpTimeoutMilliseconds;
        private float inaccuracy = 1.0F;
        public int pellets = 1;

        public boolean smokeEnabled = true;
        private Function<ItemStack, List<String>> informationProvider;
        private float shellCasingForwardOffset = 0.1F;
        private float shellCasingVerticalOffset = 0.0F;

        public boolean shellCasingEjectEnabled = true;
        private boolean hasIteratedLoad;

        private final float silencedShootSoundVolume;
        private final float shootSoundVolume;

        public Builder() {
            this.silencedShootSoundVolume = 0.7F;
            this.shootSoundVolume = 10.0F;
        }

        public ItemWeapon.Builder withInformationProvider(Function<ItemStack, List<String>> informationProvider) {
            this.informationProvider = informationProvider;
            return this;
        }

        public ItemWeapon.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public ItemWeapon.Builder withAmmoCapacity(int ammoCapacity) {
            this.ammoCapacity = ammoCapacity;
            return this;
        }

        public ItemWeapon.Builder withIteratedLoad() {
            this.hasIteratedLoad = true;
            return this;
        }

        public ItemWeapon.Builder withRecoil(float recoil) {
            this.recoil = recoil;
            return this;
        }

        public ItemWeapon.Builder withCameraRecoilDuration(int durationMs) {
            this.cameraRecoilDurationMs = durationMs;
            return this;
        }

        public ItemWeapon.Builder withMaxShots(int... maxShots) {
            for (int m : maxShots) {
                this.maxShots.add(m);
            }
            return this;
        }

        public ItemWeapon.Builder withFireRate(float fireRate) {
            if (fireRate > 1.0F || fireRate <= 0.0F) {
                throw new IllegalArgumentException("Invalid fire rate " + fireRate);
            }
            this.fireRate = fireRate;
            return this;
        }

        public ItemWeapon.Builder withTextureName(String textureName) {
            this.textureName = textureName.toLowerCase() + ".png";
            return this;
        }

        public ItemWeapon.Builder withCrosshair(String crosshair) {
            this.crosshair = NewGunrizonsMod.MODID + ":" + "textures/crosshairs/" + crosshair.toLowerCase() + ".png";
            return this;
        }

        public ItemWeapon.Builder withCrosshairRunning(String crosshairRunning) {
            this.crosshairRunning = NewGunrizonsMod.MODID + ":"
                + "textures/crosshairs/"
                + crosshairRunning.toLowerCase()
                + ".png";
            return this;
        }

        public ItemWeapon.Builder withCrosshairZoomed(String crosshairZoomed) {
            this.crosshairZoomed = NewGunrizonsMod.MODID + ":"
                + "textures/crosshairs/"
                + crosshairZoomed.toLowerCase()
                + ".png";
            return this;
        }

        public ItemWeapon.Builder withShootSound(String shootSound) {
            this.shootSound = shootSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withSilencedShootSound(String silencedShootSound) {
            this.silencedShootSound = silencedShootSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withReloadSound(String reloadSound) {
            this.reloadSound = reloadSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withReloadIterationSound(String reloadIterationSound) {
            this.reloadIterationSound = reloadIterationSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withAllReloadIterationsCompletedSound(String allReloadIterationCompletedSound) {
            this.allReloadIterationsCompletedSound = allReloadIterationCompletedSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withUnloadSound(String unloadSound) {
            this.unloadSound = unloadSound.toLowerCase();
            return this;
        }

        public ItemWeapon.Builder withCreativeTab(CreativeTabs creativeTab) {
            this.creativeTab = creativeTab;
            return this;
        }

        public ItemWeapon.Builder withSpawnEntityDamage(float spawnEntityDamage) {
            this.spawnEntityDamage = spawnEntityDamage;
            return this;
        }

        public ItemWeapon.Builder withSpawnEntitySpeed(float spawnEntitySpeed) {
            this.spawnEntitySpeed = spawnEntitySpeed;
            return this;
        }

        public ItemWeapon.Builder withSpawnEntityExplosionRadius(float spawnEntityExplosionRadius) {
            this.spawnEntityExplosionRadius = spawnEntityExplosionRadius;
            return this;
        }

        public ItemWeapon.Builder withSpawnEntityGravityVelocity(float spawnEntityGravityVelocity) {
            this.spawnEntityGravityVelocity = spawnEntityGravityVelocity;
            return this;
        }

        public ItemWeapon.Builder withInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
            return this;
        }

        public ItemWeapon.Builder withRenderer(WeaponRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public ItemWeapon.Builder withCompatibleBullet(ItemBullet bullet) {
            this.compatibleBullets.add(bullet);
            return this;
        }

        /** Adds a compatible attachment that renders at the given bone. */
        public ItemWeapon.Builder withCompatibleAttachment(ItemAttachment attachment, String boneName) {
            this.compatibleAttachments.put(attachment, new CompatibleAttachment(attachment, boneName));
            return this;
        }

        /** Adds a compatible attachment with default flag. */
        public ItemWeapon.Builder withCompatibleAttachment(ItemAttachment attachment, String boneName,
            boolean isDefault) {
            this.compatibleAttachments.put(attachment, new CompatibleAttachment(attachment, boneName, isDefault));
            return this;
        }

        /** Adds a compatible attachment with equip/unequip handlers (no visual bone). */
        public ItemWeapon.Builder withCompatibleAttachment(ItemAttachment attachment,
            ItemAttachment.AttachmentHandler applyHandler, ItemAttachment.AttachmentHandler removeHandler) {
            this.compatibleAttachments
                .put(attachment, new CompatibleAttachment(attachment, applyHandler, removeHandler));
            return this;
        }

        public ItemWeapon.Builder withShellCasingEjectEnabled(boolean shellCasingEjectEnabled) {
            this.shellCasingEjectEnabled = shellCasingEjectEnabled;
            return this;
        }

        public ItemWeapon.Builder withShellCasingForwardOffset(float shellCasingForwardOffset) {
            this.shellCasingForwardOffset = shellCasingForwardOffset;
            return this;
        }

        public ItemWeapon.Builder withShellCasingVerticalOffset(float shellCasingVerticalOffset) {
            this.shellCasingVerticalOffset = shellCasingVerticalOffset;
            return this;
        }

        public ItemWeapon.Builder withPumpTimeout(long pumpTimeoutMilliseconds) {
            this.pumpTimeoutMilliseconds = pumpTimeoutMilliseconds;
            return this;
        }

        public ItemWeapon.Builder withPellets(int pellets) {
            if (pellets < 1) {
                throw new IllegalArgumentException("Pellet count must be >= 1");
            }
            this.pellets = pellets;
            return this;
        }

        public ItemWeapon.Builder withSmokeEnabled(boolean smokeEnabled) {
            this.smokeEnabled = smokeEnabled;
            return this;
        }

        public ItemWeapon build() {
            if (this.name == null) {
                throw new IllegalStateException("Weapon name not provided");
            }

            if (this.shootSound == null) {
                this.shootSound = this.name;
            }

            if (this.silencedShootSound == null) {
                this.silencedShootSound = this.shootSound;
            }

            if (this.reloadSound == null) {
                this.reloadSound = "reload";
            }

            if (this.unloadSound == null) {
                this.unloadSound = "unload";
            }

            if (this.spawnEntityClass == null) {
                this.spawnEntityClass = EntityBullet.class;
            }

            if (this.crosshairRunning == null) {
                this.crosshairRunning = this.crosshair;
            }

            if (this.crosshairZoomed == null) {
                this.crosshairZoomed = this.crosshair;
            }

            if (this.maxShots.isEmpty()) {
                this.maxShots.add(Integer.MAX_VALUE);
            }

            ItemWeapon weapon = new ItemWeapon(this);
            weapon.shootSound = Sounds.resolve(this.shootSound);
            weapon.reloadSound = Sounds.resolve(this.reloadSound);
            weapon.reloadIterationSound = Sounds.resolve(this.reloadIterationSound);
            weapon.allReloadIterationsCompletedSound = Sounds.resolve(this.allReloadIterationsCompletedSound);
            weapon.unloadSound = Sounds.resolve(this.unloadSound);
            weapon.silencedShootSound = Sounds.resolve(this.silencedShootSound);
            weapon.setCreativeTab(this.creativeTab);
            weapon.setUnlocalizedName(this.name);
            if (this.ammo != null) {
                this.ammo.addCompatibleWeapon(weapon);
            }

            NewGunrizonsMod.proxy.registerItem(this.name, weapon, this.renderer);

            return weapon;
        }
    }
}
