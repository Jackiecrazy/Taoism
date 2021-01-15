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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestUpdate implements IMessage {
    private int entityID;
    private NBTTagCompound nbt;

    public PacketRequestUpdate() {
    }

    public PacketRequestUpdate(EntityLivingBase elb) {
        entityID = elb.getEntityId();
        nbt = TaoCasterData.getTaoCap(elb).serializeNBT();
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

    public static class RequestHandler implements
            IMessageHandler<PacketRequestUpdate, IMessage> {
        @Override
        public IMessage onMessage(final PacketRequestUpdate m,
                                  MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer thePlayer = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                if (thePlayer == null || thePlayer.world == null) return;
                Entity e = thePlayer.world
                        .getEntityByID(m.entityID);
                if (e instanceof EntityLivingBase) {
                    TaoCasterData.updateCasterData((EntityLivingBase) e);
                }
            });
            return null;
        }
    }
}
