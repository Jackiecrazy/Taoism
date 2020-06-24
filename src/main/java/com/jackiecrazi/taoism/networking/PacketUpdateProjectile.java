package com.jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateProjectile implements IMessage {
    private int entityID;
    private NBTTagCompound nbt;

    public PacketUpdateProjectile() {
    }

    public PacketUpdateProjectile(Entity elb) {
        entityID = elb.getEntityId();
        nbt=elb.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class UpdateProjectileHandler implements
            IMessageHandler<PacketUpdateProjectile, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateProjectile m,
                                  MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity e = Minecraft.getMinecraft().world.getEntityByID(m.entityID);
                if (e != null)
                    e.readFromNBT(m.nbt);
            });
            return null;
        }
    }
}