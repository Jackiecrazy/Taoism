package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMakeMove implements IMessage {

    private byte code;

    public PacketMakeMove() {
    }

    public PacketMakeMove(MoveCode code) {
        this.code = code.toByte();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        code = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(code);
    }

    public static class MakeMoveHandler implements
            IMessageHandler<PacketMakeMove, IMessage> {
        @Override
        public IMessage onMessage(final PacketMakeMove message,
                                  MessageContext ctx) {
            //System.out.println("packet acquired!");
            final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
                    .getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(() -> {
                        TaoCombatUtils.executeMove(thePlayer, message.code);
                    }
            );
            return null;
        }
    }
}
