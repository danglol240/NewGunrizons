package com.gtnewhorizon.newgunrizons.weapon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceRegistry;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.registry.Sounds;
import com.gtnewhorizon.newgunrizons.state.Aspect;
import com.gtnewhorizon.newgunrizons.state.StateManager;

public class WeaponFireAspect implements Aspect<WeaponState, ItemWeaponInstance> {

    private static final long ALERT_TIMEOUT = 500L;

    private static final Predicate<ItemWeaponInstance> readyToShootAccordingToFireRate = (
        instance) -> (float) (System.currentTimeMillis() - instance.getLastFireTimestamp())
            >= 50.0F / instance.getWeapon()
                .getFireRate();
    private static final Predicate<ItemWeaponInstance> readyToShootAccordingToFireMode = (
        instance) -> instance.getSeriesShotCount() < instance.getMaxShots();
    private static final Predicate<ItemWeaponInstance> hasAmmo = (instance) -> instance.getAmmo() > 0;
    private static final Predicate<ItemWeaponInstance> ejectSpentRoundRequired = (instance) -> instance.getWeapon()
        .ejectSpentRoundRequired();
    private static final Predicate<ItemWeaponInstance> ejectSpentRoundTimeoutExpired = (
        instance) -> System.currentTimeMillis() >= instance.getWeapon()
            .getPumpTimeoutMilliseconds() + instance.getStateUpdateTimestamp();
    private static final Predicate<ItemWeaponInstance> alertTimeoutExpired = (instance) -> System.currentTimeMillis()
        >= ALERT_TIMEOUT + instance.getStateUpdateTimestamp();
    private static final Predicate<ItemWeaponInstance> sprinting = (instance) -> instance.getPlayer()
        .isSprinting();
    private static final Predicate<ItemWeaponInstance> overheated = (instance) -> {
        EntityLivingBase player = instance.getPlayer();
        return player != null && WeaponHeat.isLocked(player.getHeldItem(), player.worldObj.getTotalWorldTime());
    };
    private static final Set<WeaponState> allowedFireOrEjectFromStates;
    private static final Set<WeaponState> allowedUpdateFromStates;
    public static final WeaponFireAspect INSTANCE = new WeaponFireAspect();

    private StateManager<WeaponState, ? super ItemWeaponInstance> stateManager;

    public WeaponFireAspect() {}

    public void setStateManager(StateManager<WeaponState, ? super ItemWeaponInstance> stateManager) {
        this.stateManager = stateManager;
        stateManager.in(this)
            .change(WeaponState.READY)
            .to(WeaponState.ALERT)
            .when(hasAmmo.negate())
            .withAction(this::cannotFire)
            .manual()
            .in(this)
            .change(WeaponState.ALERT)
            .to(WeaponState.READY)
            .when(alertTimeoutExpired)
            .automatic()
            .in(this)
            .change(WeaponState.READY)
            .to(WeaponState.FIRING)
            .when(
                hasAmmo.and(sprinting.negate())
                    .and(overheated.negate())
                    .and(readyToShootAccordingToFireRate))
            .withAction(this::fire)
            .manual()
            .in(this)
            .change(WeaponState.FIRING)
            .to(WeaponState.RECOILED)
            .automatic()
            .in(this)
            .change(WeaponState.RECOILED)
            .to(WeaponState.PAUSED)
            .automatic()
            .in(this)
            .change(WeaponState.PAUSED)
            .to(WeaponState.EJECT_REQUIRED)
            .when(ejectSpentRoundRequired)
            .manual()
            .in(this)
            .change(WeaponState.EJECT_REQUIRED)
            .to(WeaponState.EJECTING)
            .withAction(this::ejectSpentRound)
            .manual()
            .in(this)
            .change(WeaponState.EJECTING)
            .to(WeaponState.READY)
            .when(ejectSpentRoundTimeoutExpired)
            .automatic()
            .in(this)
            .change(WeaponState.PAUSED)
            .to(WeaponState.FIRING)
            .when(
                hasAmmo.and(sprinting.negate())
                    .and(overheated.negate())
                    .and(readyToShootAccordingToFireMode)
                    .and(readyToShootAccordingToFireRate))
            .withAction(this::fire)
            .manual()
            .in(this)
            .change(WeaponState.PAUSED)
            .to(WeaponState.READY)
            .when(ejectSpentRoundRequired.negate())
            .withAction(ItemWeaponInstance::resetCurrentSeries)
            .manual();
    }

    public void onFireButtonClick(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry.INSTANCE
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            if (overheated.test(weaponInstance)) {
                float left = WeaponHeat.secondsLeft(player.getHeldItem(), player.worldObj.getTotalWorldTime());
                StatusMessageManager.INSTANCE.addAlertMessage(
                    StatCollector.translateToLocalFormatted("gui.newgunrizons.overheated", String.format("%.1f", left)),
                    1,
                    250L,
                    200L);
                return;
            }
            this.stateManager.changeStateFromAnyOf(
                this,
                weaponInstance,
                allowedFireOrEjectFromStates,
                WeaponState.FIRING,
                WeaponState.EJECTING,
                WeaponState.ALERT);
        }

    }

    public void onFireButtonRelease(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry.INSTANCE
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            this.stateManager.changeState(this, weaponInstance, WeaponState.EJECT_REQUIRED, WeaponState.READY);
        }

    }

    public void onUpdate(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry.INSTANCE
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            this.stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
        }

    }

    private void cannotFire(ItemWeaponInstance weaponInstance) {
        if (weaponInstance.getAmmo() == 0) {
            String message;
            if (weaponInstance.getWeapon()
                .getAmmoCapacity() == 0
                && WeaponAttachmentAspect.INSTANCE.getActiveAttachment(weaponInstance, AttachmentCategory.MAGAZINE)
                    == null) {
                message = StatCollector.translateToLocalFormatted("gui.noMagazine");
            } else {
                message = StatCollector.translateToLocalFormatted("gui.noAmmo");
            }

            StatusMessageManager.INSTANCE.addAlertMessage(message, 3, 250L, 200L);
            if (weaponInstance.getPlayer() instanceof EntityPlayer) {
                if (Sounds.DRY_FIRE != null) {
                    weaponInstance.getPlayer()
                        .playSound(Sounds.DRY_FIRE, 1.0F, 1.0F);
                }
            }
        }

    }

    private void fire(ItemWeaponInstance weaponInstance) {
        EntityLivingBase player = weaponInstance.getPlayer();
        ItemWeapon weapon = (ItemWeapon) weaponInstance.getItem();
        Random random = player.getRNG();
        NewGunrizonsMod.CHANNEL.sendToServer(
            new WeaponActionMessage(WeaponActionMessage.FIRE, ((EntityPlayer) player).inventory.currentItem));
        boolean silencerOn = WeaponAttachmentAspect.INSTANCE.isSilencerOn(weaponInstance);
        {
            String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
            if (snd != null) {
                player.playSound(
                    snd,
                    silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume(),
                    1.0F);
            }
        }
        if (weaponInstance.getAmmo() == 1 && weapon.getEndOfShootSound() != null) {
            player.playSound(weapon.getEndOfShootSound(), 1.0F, 1.0F);
        }

        player.rotationPitch -= weaponInstance.getRecoil();
        float rotationYawFactor = -1.0F + random.nextFloat() * 2.0F;
        player.rotationYaw += weaponInstance.getRecoil() * rotationYawFactor;

        NewGunrizonsMod.proxy.onWeaponFireEffects(
            player,
            weapon.getSmokeOffsetX()
                .get(),
            weapon.getSmokeOffsetY()
                .get(),
            silencerOn);

        WeaponHeat.addShot(player.getHeldItem(), player.worldObj.getTotalWorldTime());
        weaponInstance.setSeriesShotCount(weaponInstance.getSeriesShotCount() + 1);
        weaponInstance.setLastFireTimestamp(System.currentTimeMillis());
        weaponInstance.setAmmo(weaponInstance.getAmmo() - 1);
    }

    private void ejectSpentRound(ItemWeaponInstance weaponInstance) {
        EntityLivingBase player = weaponInstance.getPlayer();
        if (weaponInstance.getWeapon()
            .getEjectSpentRoundSound() != null) {
            player.playSound(
                weaponInstance.getWeapon()
                    .getEjectSpentRoundSound(),
                1.0F,
                1.0F);
        }
    }

    public void serverFire(EntityLivingBase player, ItemStack itemStack, int slotIndex) {
        if (!(itemStack.getItem() instanceof ItemWeapon)) {
            return;
        }
        ItemWeapon weapon = (ItemWeapon) itemStack.getItem();

        ItemWeaponInstance instance = ItemInstance.fromStack(itemStack, ItemWeaponInstance.class);
        if (instance == null) {
            instance = weapon.createItemInstance(player, itemStack, slotIndex);
            ItemInstance.toStack(itemStack, instance);
        }
        instance.setPlayer(player);

        // Server-authoritative ammo and heat checks
        int ammo = instance.getAmmo();
        if (ammo <= 0) {
            return;
        }
        long now = player.worldObj.getTotalWorldTime();
        if (WeaponHeat.isLocked(itemStack, now)) {
            return;
        }
        WeaponHeat.addShot(itemStack, now);
        instance.setAmmo(ammo - 1);
        ItemInstance.setAmmo(itemStack, ammo - 1);
        ItemInstance.toStack(itemStack, instance);

        // Spawn projectiles
        for (int i = 0; i < weapon.getPellets(); ++i) {
            weapon.spawnBullet(player);
        }

        if (weapon.isShellCasingEjectEnabled()) {
            weapon.spawnShell(instance, player);
        }

        // Play sound for other players
        boolean silencerOn = WeaponAttachmentAspect.INSTANCE.isSilencerOn(instance);
        String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
        float vol = silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume();
        if (player instanceof EntityPlayer) {
            player.worldObj.playSoundToNearExcept((EntityPlayer) player, snd, vol, 1.0F);
        } else {
            player.worldObj.playSoundAtEntity(player, snd, vol, 1.0F);
        }
    }

    static {
        allowedFireOrEjectFromStates = new HashSet<>(
            Arrays.asList(WeaponState.READY, WeaponState.PAUSED, WeaponState.EJECT_REQUIRED));
        allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(
                WeaponState.EJECTING,
                WeaponState.PAUSED,
                WeaponState.FIRING,
                WeaponState.RECOILED,
                WeaponState.PAUSED,
                WeaponState.ALERT));
    }
}
