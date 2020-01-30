package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMakeMove implements IMessage {

    private int entityId;

    public PacketMakeMove() {
    }

    public PacketMakeMove(EntityLivingBase instigator) {
        entityId = instigator.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = ByteBufUtils.readVarInt(buf, 4);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
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
                        EntityLivingBase theEntity = (EntityLivingBase) thePlayer.world
                                .getEntityByID(message.entityId);
                        if (thePlayer == theEntity && thePlayer.getCooledAttackStrength(0f) > 0.9f)
                            thePlayer.world.spawnEntity(TaoCasterData.getMoveFromEntity(theEntity));

                    }
            );
			return null;
        }
    }
}
