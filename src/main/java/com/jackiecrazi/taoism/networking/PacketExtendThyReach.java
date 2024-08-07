package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExtendThyReach implements IMessage {

    private int entityId;
    private boolean off;

    public PacketExtendThyReach() {
    }

    public PacketExtendThyReach(boolean mainHand, Entity entity) {
        entityId = entity.getEntityId();
        off = mainHand;
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
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                //System.out.println("packet acquired!");
                final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                //thePlayer.getServerWorld().addScheduledTask(() -> {
                Entity theEntity = thePlayer.world
                        .getEntityByID(message.entityId);
                ItemStack heldItem = thePlayer.getHeldItem(message.off ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                float range=3;
                if (heldItem.getItem() instanceof IRange) {
                    //&& theEntity.isEntityAlive()) {
                    IRange ir = (IRange) heldItem.getItem();
                    range = ir.getReach(thePlayer, heldItem);
                }else if (!message.off){//main hand non IRange
                    return;
                }
                if (theEntity == null) {
                    //null entity... run again
                    theEntity = NeedyLittleThings.raytraceEntity(thePlayer.world, thePlayer, range);
                    if (theEntity == null) return;
                    //still null? I guess it's null then...
                }
                double distanceSq = NeedyLittleThings.getDistSqCompensated(thePlayer, theEntity);
                double reachSq = range * range;
                if (reachSq >= distanceSq) {
                    TaoCombatUtils.taoWeaponAttack(theEntity, thePlayer, heldItem, message.off, true);
                }
                TaoCombatUtils.rechargeHand(thePlayer, message.off ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, 0f, true);
            });
            return null;
        }
    }
}
