package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
            Minecraft.getMinecraft().addScheduledTask(() -> {
                //System.out.println("packet acquired!");
                final EntityPlayer thePlayer = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                Entity e = thePlayer.world
                        .getEntityByID(m.entityID);
                if (e == null) return;
                NeedyLittleThings.setSize(e, m.w, m.h);
            });
            return null;
        }
    }
}
