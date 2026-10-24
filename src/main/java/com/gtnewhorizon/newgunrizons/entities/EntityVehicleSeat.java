package com.gtnewhorizon.newgunrizons.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

/**
 * An extra seat of a vehicle (anything but the driver's). Invisible and not solid; it follows its vehicle every tick
 * and disappears when its passenger gets out. Never saved: a passenger who logs out stands up.
 */
public class EntityVehicleSeat extends Entity implements IEntityAdditionalSpawnData {

    private int vehicleId = -1;
    private int seat;
    private EntityVehicle vehicle;
    private int missing;
    private Entity lastRider;

    public EntityVehicleSeat(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.noClip = true;
    }

    public EntityVehicleSeat(World world, EntityVehicle vehicle, int seat) {
        this(world);
        this.vehicle = vehicle;
        this.vehicleId = vehicle.getEntityId();
        this.seat = seat;
    }

    public EntityVehicle getVehicle() {
        if (vehicle == null || vehicle.isDead) {
            Entity e = vehicleId >= 0 ? worldObj.getEntityByID(vehicleId) : null;
            vehicle = e instanceof EntityVehicle ? (EntityVehicle) e : null;
        }
        return vehicle;
    }

    public int getSeat() {
        return seat;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        EntityVehicle v = getVehicle();
        if (v == null || v.getVehicle() == null) {
            if (!worldObj.isRemote && ++missing > 40) leave();
            return;
        }
        missing = 0;
        Vec3 p = v.seatPosition(seat);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        setPosition(p.xCoord, p.yCoord, p.zCoord);
        this.rotationYaw = v.rotationYaw;
        if (!worldObj.isRemote) {
            if (riddenByEntity != null) lastRider = riddenByEntity;
            else {
                // the passenger got out: put them on top of the vehicle instead of inside its hull
                if (lastRider instanceof EntityLivingBase && !lastRider.isDead) {
                    ((EntityLivingBase) lastRider).setPositionAndUpdate(v.posX, v.boundingBox.maxY + 0.1D, v.posZ);
                } else if (lastRider != null && !lastRider.isDead) {
                    lastRider.setPosition(v.posX, v.boundingBox.maxY + 0.1D, v.posZ);
                }
                setDead();
            }
        }
    }

    private void leave() {
        if (riddenByEntity != null) riddenByEntity.mountEntity(null);
        setDead();
    }

    @Override
    public void updateRiderPosition() {
        if (riddenByEntity != null) {
            riddenByEntity.setPosition(posX, posY - 0.45D + riddenByEntity.getYOffset(), posZ);
        }
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(vehicleId);
        buffer.writeByte(seat);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        vehicleId = buffer.readInt();
        seat = buffer.readByte();
    }
}
