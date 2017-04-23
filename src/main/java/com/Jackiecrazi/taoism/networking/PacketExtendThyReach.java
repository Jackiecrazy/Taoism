package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageArmorPierce;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageConcussion;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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

			Entity theEntity = thePlayer.worldObj
					.getEntityByID(message.entityId);
			if (thePlayer.getHeldItem() != null && theEntity != null) {
				if (thePlayer.getHeldItem().getItem() instanceof ICustomRange
						&& theEntity.isEntityAlive()) {
					ICustomRange theExtendedReachWeapon = (ICustomRange) thePlayer
							.getHeldItem().getItem();
					double distanceSq = thePlayer
							.getDistanceSqToEntity(theEntity);// can throw a
																// null? Either
																// player does
																// not exist or
																// entity does
																// not exist
					double reachSq = theExtendedReachWeapon.getRange(thePlayer,
							thePlayer.getHeldItem())
							* theExtendedReachWeapon.getRange(thePlayer,
									thePlayer.getHeldItem());
					if (reachSq >= distanceSq) {
						if (message.atkAsPlayer) {
							NeedyLittleThings.attackWithDebuff(theEntity, thePlayer, 1/(theExtendedReachWeapon.getRange(thePlayer, thePlayer.getHeldItem())));
							/*thePlayer
									.attackTargetEntityWithCurrentItem(theEntity);*/
						} else {
							if (thePlayer.getHeldItem().getItem() == ModItems.QiangIron
									|| thePlayer.getHeldItem().getItem() == ModItems.QiangWood) {
								theEntity
										.attackEntityFrom(
												DamageArmorPierce
														.punctureDirectly(thePlayer),
												(float) (thePlayer
														.getEntityAttribute(SharedMonsterAttributes.attackDamage)
														.getAttributeValue()) / 10f);
								if (theEntity instanceof EntityLiving)
									((EntityLiving) theEntity).hurtResistantTime = 0;
								// System.out.println("message received on side "+ctx.side+" and hurt time set to "+theEntity.hurtResistantTime);
							} else if (thePlayer.getHeldItem().getItem() == ModItems.Jian) {
								theEntity
										.attackEntityFrom(
												DamageConcussion
														.causeBrainDamageDirectly(thePlayer),
												(float) thePlayer
														.getEntityAttribute(
																SharedMonsterAttributes.attackDamage)
														.getAttributeValue() * 2);
								if (theEntity.worldObj.rand.nextInt(4) == 0
										&& theEntity instanceof EntityCreature
										&& !(theEntity instanceof IBossDisplayData)) {
									EntityCreature c = (EntityCreature) theEntity;
									c.getNavigator().setEnterDoors(false);
									c.getNavigator().setAvoidsWater(true);
									if (theEntity.worldObj.rand.nextInt(39) == 7) {
										c.tasks.taskEntries.clear();
										c.targetTasks.taskEntries.clear();
									}
								}

							} else if (thePlayer.getHeldItem().getItem() == ModItems.EMeiCi) {
								theEntity
										.attackEntityFrom(DamageSource.causePlayerDamage(thePlayer),(float) thePlayer
														.getEntityAttribute(
																SharedMonsterAttributes.attackDamage)
														.getAttributeValue() * 2);
							}
						}
					}

				}
			}
			return null;
		}
	}
}
