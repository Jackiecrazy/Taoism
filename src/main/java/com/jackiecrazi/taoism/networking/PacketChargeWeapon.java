package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketChargeWeapon implements IMessage {

    private EnumHand h;

    public PacketChargeWeapon() {
    }

    public PacketChargeWeapon(EnumHand hand) {
        h = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        h = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(h == EnumHand.MAIN_HAND);
    }

    public static class ChargeHandler implements
            IMessageHandler<PacketChargeWeapon, IMessage> {
        @Override
        public IMessage onMessage(final PacketChargeWeapon message,
                                  MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if (!CombatConfig.superSecretSetting) return;
                final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                ItemStack heldItem = thePlayer.getHeldItem(message.h);
                if (heldItem.getItem() instanceof IChargeableWeapon) {
                    IChargeableWeapon icw = (IChargeableWeapon) heldItem.getItem();
                    if (icw.canCharge(thePlayer, heldItem))
                        icw.chargeWeapon(thePlayer, heldItem);
                }
            });
            return null;
        }
    }
}
