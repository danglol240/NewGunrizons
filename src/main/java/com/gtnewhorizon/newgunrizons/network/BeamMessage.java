package com.gtnewhorizon.newgunrizons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/** Server to client: draw a hitscan beam fired by a shooter with a weapon from start to end. */
@Getter
public class BeamMessage implements IMessage {

    private int shooterId;
    private int weaponId;
    private double startX, startY, startZ;
    private double endX, endY, endZ;

    public BeamMessage() {}

    public BeamMessage(int shooterId, int weaponId, double startX, double startY, double startZ, double endX,
        double endY, double endZ) {
        this.shooterId = shooterId;
        this.weaponId = weaponId;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        shooterId = buf.readInt();
        weaponId = buf.readInt();
        startX = buf.readDouble();
        startY = buf.readDouble();
        startZ = buf.readDouble();
        endX = buf.readDouble();
        endY = buf.readDouble();
        endZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shooterId);
        buf.writeInt(weaponId);
        buf.writeDouble(startX);
        buf.writeDouble(startY);
        buf.writeDouble(startZ);
        buf.writeDouble(endX);
        buf.writeDouble(endY);
        buf.writeDouble(endZ);
    }
}
