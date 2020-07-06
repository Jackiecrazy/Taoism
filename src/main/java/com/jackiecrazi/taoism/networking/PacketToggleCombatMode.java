package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketToggleCombatMode implements IMessage {
    public PacketToggleCombatMode() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class CombatModeHandler implements IMessageHandler<PacketToggleCombatMode, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleCombatMode message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer p = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                TaoCasterData.getTaoCap(p).toggleCombatMode(TaoCasterData.getTaoCap(p).isInCombatMode());
            });
            return null;
        }
    }
}
