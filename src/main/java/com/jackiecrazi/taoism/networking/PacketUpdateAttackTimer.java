package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateAttackTimer implements IMessage {
    private int entityID;
    private int atkTime;

    public PacketUpdateAttackTimer() {
    }

    public PacketUpdateAttackTimer(EntityLivingBase elb) {
        entityID = elb.getEntityId();
        atkTime=Taoism.getAtk(elb);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        atkTime=buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(atkTime);
    }

    public static class UpdateAttackTimerHandler implements
            IMessageHandler<PacketUpdateAttackTimer, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateAttackTimer m,
                                  MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer thePlayer = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                if (thePlayer == null || thePlayer.world == null) return;
                Entity e = thePlayer.world
                        .getEntityByID(m.entityID);
                if (e !=null) {
                    Taoism.setAtk(e, m.atkTime);
                }
            });
            return null;
        }
    }
}
