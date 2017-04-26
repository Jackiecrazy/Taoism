package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;

public class PacketUpdateAttackTimer implements IMessage {

	private int atkTimer ;

    public PacketUpdateAttackTimer() 
    {}

    public PacketUpdateAttackTimer(int timeInTicks) 
    {
     atkTimer = timeInTicks;
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
     atkTimer = ByteBufUtils.readVarInt(buf, 4);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
     ByteBufUtils.writeVarInt(buf, atkTimer, 4);
    }

    public static class UpdateAttackTimeHandler implements IMessageHandler<PacketUpdateAttackTimer, 
          IMessage> 
    {
        @Override
        public IMessage onMessage(final PacketUpdateAttackTimer message, 
              MessageContext ctx) 
        {
            final AbstractClientPlayer thePlayer = (AbstractClientPlayer) Taoism.proxy.
                  getPlayerEntityFromContext(ctx);
            //thePlayer.set=message.atkTimer;
            //System.out.println("successfully interpreted attack time as "+thePlayer.attackTime+" on side "+ctx.side);
            return null;
        }
    }

}
