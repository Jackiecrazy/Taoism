package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDodge implements IMessage {
    int side;//0 left, 1 back, 2 right

    public PacketDodge() {

    }

    public PacketDodge(int towards) {
        side = towards;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        side = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(side);
    }

    public static class DodgeHandler implements IMessageHandler<PacketDodge, IMessage> {

        @Override
        public IMessage onMessage(PacketDodge message, MessageContext ctx) {
            //System.out.println("packet dodge received");
            final EntityPlayerMP p = Taoism.proxy
                    .getPlayerEntityFromContext(ctx);
            TaoCasterData.attemptDodge(p, message.side);
            return null;
        }
    }
}
