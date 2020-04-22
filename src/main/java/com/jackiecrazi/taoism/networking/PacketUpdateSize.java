package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSize implements IMessage {
    private int entityID;
    private float w, h;

    public PacketUpdateSize() {
    }

    public PacketUpdateSize(int ID, float width, float height) {
        entityID = ID;
        w = width;
        h = height;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        w = buf.readFloat();
        h = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeFloat(w);
        buf.writeFloat(h);
    }

    public static class UpdateSizeHandler implements
            IMessageHandler<PacketUpdateSize, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateSize m,
                                  MessageContext ctx) {
            //System.out.println("packet acquired!");
            final EntityPlayer thePlayer = Taoism.proxy
                    .getPlayerEntityFromContext(ctx);
            Entity e = thePlayer.world
                    .getEntityByID(m.entityID);
            if(e==null)return null;
            if (e instanceof EntityLivingBase) {
                TaoCasterData.getTaoCap((EntityLivingBase) e).setPrevSizes(e.width, e.height);
            }
            NeedyLittleThings.setSize(e, m.w, m.h);
            return null;
        }
    }
}
