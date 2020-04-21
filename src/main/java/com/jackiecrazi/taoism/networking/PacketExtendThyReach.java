package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExtendThyReach implements IMessage {

    private int entityId;
    private boolean off;

    public PacketExtendThyReach() {
    }

    public PacketExtendThyReach(int parEntityId, boolean mainHand) {
        entityId = parEntityId;
        off = mainHand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = ByteBufUtils.readVarInt(buf, 4);
        off = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
        buf.writeBoolean(off);
    }

    public static class ExtendReachHandler implements
            IMessageHandler<PacketExtendThyReach, IMessage> {
        @Override
        public IMessage onMessage(final PacketExtendThyReach message,
                                  MessageContext ctx) {
            //System.out.println("packet acquired!");
            final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
                    .getPlayerEntityFromContext(ctx);
            //thePlayer.getServerWorld().addScheduledTask(() -> {
            Entity theEntity = thePlayer.world
                    .getEntityByID(message.entityId);
            ItemStack heldItem = thePlayer.getHeldItem(message.off ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            if (heldItem.getItem() instanceof IRange) {
                //&& theEntity.isEntityAlive()) {
                IRange ir = (IRange) heldItem.getItem();
                float range = ir.getReach(thePlayer, heldItem);
                if (theEntity == null) {
                    //null entity... run again
                    theEntity = NeedyLittleThings.raytraceEntity(thePlayer.world, thePlayer, range);
                    if(theEntity==null)return null;
                    //still null? I guess it's null then...
                }
                double distanceSq = NeedyLittleThings.getDistSqCompensated(thePlayer, theEntity);
                double reachSq = range * range;
                if (reachSq >= distanceSq) {
                    NeedyLittleThings.taoWeaponAttack(theEntity, thePlayer, heldItem, message.off, true);
                }

            }
            //});
            return null;
        }
    }
}
