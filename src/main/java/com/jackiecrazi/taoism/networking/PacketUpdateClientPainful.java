package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateClientPainful implements IMessage {
    private int entityID;
    private NBTTagCompound nbt;

    public PacketUpdateClientPainful() {
    }

    public PacketUpdateClientPainful(EntityLivingBase elb) {
        entityID = elb.getEntityId();
        nbt = TaoCasterData.getTaoCap(elb).serializeNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        nbt=ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class UpdateClientHandler implements
            IMessageHandler<PacketUpdateClientPainful, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateClientPainful m,
                                  MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer thePlayer = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                if (thePlayer == null || thePlayer.world == null) return;
                Entity e = thePlayer.world
                        .getEntityByID(m.entityID);
                if (e instanceof EntityLivingBase) {
                    ITaoStatCapability i = TaoCasterData.getTaoCap((EntityLivingBase) e);
                    i.deserializeNBT(m.nbt);
                }
            });
            return null;
        }
    }
}
