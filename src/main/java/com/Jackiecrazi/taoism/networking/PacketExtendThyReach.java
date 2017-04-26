package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;

public class PacketExtendThyReach implements IMessage {

	private int entityId;
	private boolean atkAsPlayer;

	public PacketExtendThyReach() {
	}

	public PacketExtendThyReach(int parEntityId, boolean real) {
		entityId = parEntityId;
		atkAsPlayer = real;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = ByteBufUtils.readVarInt(buf, 4);
		atkAsPlayer = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityId, 4);
		buf.writeBoolean(atkAsPlayer);
	}

	public static class ExtendReachHandler implements
			IMessageHandler<PacketExtendThyReach, IMessage> {
		@Override
		public IMessage onMessage(final PacketExtendThyReach message,
				MessageContext ctx) {
			final EntityPlayerMP thePlayer = (EntityPlayerMP) Taoism.proxy
					.getPlayerEntityFromContext(ctx);

			Entity theEntity = thePlayer.world
					.getEntityByID(message.entityId);
			if (thePlayer.getHeldItemMainhand() != null && theEntity != null) {
				if (thePlayer.getHeldItemMainhand().getItem() instanceof ICustomRange
						&& theEntity.isEntityAlive()) {
					ICustomRange theExtendedReachWeapon = (ICustomRange) thePlayer
							.getHeldItemMainhand().getItem();
					double distanceSq = thePlayer
							.getDistanceSqToEntity(theEntity);// can throw a
																// null? Either
																// player does
																// not exist or
																// entity does
																// not exist
					double reachSq = theExtendedReachWeapon.getRange(thePlayer,
							thePlayer.getHeldItemMainhand())
							* theExtendedReachWeapon.getRange(thePlayer,
									thePlayer.getHeldItemMainhand());
					if (reachSq >= distanceSq) {
						if (message.atkAsPlayer) {
							NeedyLittleThings.attackWithDebuff(theEntity, thePlayer, 1/(theExtendedReachWeapon.getRange(thePlayer, thePlayer.getHeldItemMainhand())));
							/*thePlayer
									.attackTargetEntityWithCurrentItem(theEntity);*/
						}
					}

				}
			}
			return null;
		}
	}
}
