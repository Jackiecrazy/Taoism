package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSlide implements IMessage {
    public PacketSlide() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class SlideHandler implements IMessageHandler<PacketSlide, IMessage> {

        @Override
        public IMessage onMessage(PacketSlide message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer p = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                TaoMovementUtils.attemptSlide(p);
            });
            return null;
        }
    }
}
