package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketJump implements IMessage {
    public PacketJump() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class ParryHandler implements IMessageHandler<PacketJump, IMessage> {

        @Override
        public IMessage onMessage(PacketJump message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer p = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                TaoMovementUtils.attemptJump(p);
            });
            return null;
        }
    }
}
