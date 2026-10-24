package com.gtnewhorizon.newgunrizons.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.newgunrizons.entities.EntityVehicle;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Controls of a vehicle rider, sent every tick while riding: movement (driver only) and both triggers. */
public class VehicleControlMessage implements IMessage {

    private float forward, strafe;
    private byte flags;

    public VehicleControlMessage() {}

    public VehicleControlMessage(float forward, float strafe, boolean up, boolean down, boolean primary,
        boolean secondary) {
        this.forward = forward;
        this.strafe = strafe;
        this.flags = (byte) ((up ? 1 : 0) | (down ? 2 : 0) | (primary ? 4 : 0) | (secondary ? 8 : 0));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        forward = buf.readFloat();
        strafe = buf.readFloat();
        flags = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(forward);
        buf.writeFloat(strafe);
        buf.writeByte(flags);
    }

    public static class Handler implements IMessageHandler<VehicleControlMessage, IMessage> {

        @Override
        public IMessage onMessage(VehicleControlMessage m, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            EntityVehicle v = EntityVehicle.of(player);
            if (v == null) return null;
            int seat = v.seatOf(player);
            if (seat >= 0) {
                v.setInput(
                    seat,
                    m.forward,
                    m.strafe,
                    (m.flags & 1) != 0,
                    (m.flags & 2) != 0,
                    (m.flags & 4) != 0,
                    (m.flags & 8) != 0);
            }
            return null;
        }
    }
}
