package com.jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;

public class PacketExtendThyReach implements IMessage {

	private int entityId;
	private boolean isMainHand;

	public PacketExtendThyReach() {
	}

	public PacketExtendThyReach(int parEntityId, boolean right) {
		entityId = parEntityId;
		isMainHand = right;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = ByteBufUtils.readVarInt(buf, 4);
		isMainHand = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityId, 4);
		buf.writeBoolean(isMainHand);
	}

	public static class ExtendReachHandler implements
			IMessageHandler<PacketExtendThyReach, IMessage> {
		@Override
		public IMessage onMessage(final PacketExtendThyReach message,
				MessageContext ctx) {
			//System.out.println("packet acquired!");
			final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
					.getPlayerEntityFromContext(ctx);

			Entity theEntity = thePlayer.world
					.getEntityByID(message.entityId);
			ItemStack heldItem = thePlayer.getHeldItem(message.isMainHand?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
			if (heldItem != null && theEntity != null) {
				//System.out.println("nonnull again!");
				if (heldItem.getItem() instanceof ICustomRange
						&& theEntity.isEntityAlive()) {
					ICustomRange theExtendedReachWeapon = (ICustomRange) heldItem.getItem();
					double distanceSq = thePlayer
							.getDistanceSq(theEntity);
					double reachSq = theExtendedReachWeapon.getReach(thePlayer,
							heldItem)
							* theExtendedReachWeapon.getReach(thePlayer,
									heldItem);
					if (reachSq >= distanceSq) {
							NeedyLittleThings.taoWeaponAttack(theEntity, thePlayer, message.isMainHand?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
					}

				}
			}
			return null;
		}
	}
}
