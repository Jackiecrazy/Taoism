package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.config.CombatConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBeginParry implements IMessage {
    public PacketBeginParry(){

    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class ParryHandler implements IMessageHandler<PacketBeginParry, IMessage> {

        @Override
        public IMessage onMessage(PacketBeginParry message, MessageContext ctx) {
            final EntityPlayerMP p = Taoism.proxy
                    .getPlayerEntityFromContext(ctx);
            if (TaoCasterData.getTaoCap(p).getParryCounter() > CombatConfig.parryCooldown)
                TaoCasterData.getTaoCap(p).setParryCounter(0);
            return null;
        }
    }
}
