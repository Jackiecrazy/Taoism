package com.Jackiecrazi.taoism.common.taoistichandlers.tickhandlers;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IUltimate;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;
import com.Jackiecrazi.taoism.networking.PacketAnimUpdate;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class IApologizeForThisHandler {
	public static final UUID qilispdbuff = UUID
			.fromString("85ead999-5d2a-4c5e-8200-cca1da43ee9d");
	public static final UUID meditatespddebuff = UUID
			.fromString("85ead999-5d2a-4c5e-8200-cca1da43ed9d");

	private static final AttributeModifier QiLiSpeedBuff = (new AttributeModifier(
			qilispdbuff, "QiLi Speed Bonus", 0.3D, 2)).setSaved(true);
	private static final AttributeModifier MeditateSpdDebuff = (new AttributeModifier(
			meditatespddebuff, "Meditating Debuff", -1D, 2)).setSaved(false);
	public static HashMap<UUID, HashMap<Boolean, Float>> meditatingPlayersTemp;

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START)
			onTickStart(event.player);
		else
			onTickEnd(event.player);
	}

	private void onTickStart(EntityPlayer player) {
		if (player != null) {
			float WGlvl = (WuGongHandler.getThis(player).getLevel());
			float QLlvl = (XiuWeiHandler.getThis(player).getLevel());
			ItemStack held = player.getHeldItem();
			IAttributeInstance spd = player
					.getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.movementSpeed);

			if (PlayerResourceStalker.get(player).getIsMeditating()) { 
				if (WGlvl > 0) {
					//player.heal(WGlvl/40f);
					Vec3 playerPos = Vec3.createVectorHelper(player.posX,
							player.posY, player.posZ);
					Vec3 bottom = Vec3.createVectorHelper(player.posX, 0,
							player.posZ);
					MovingObjectPosition mop = player.worldObj.rayTraceBlocks(
							playerPos, bottom, true);
					if (mop != null) {
						if (mop.typeOfHit != null
								&& mop.typeOfHit == MovingObjectType.BLOCK
								&& !player.worldObj.isRemote) {// &&player.worldObj.isRemote
							// player.motionY=0;
							int shortestBlockYBelowPlayer = mop.blockY;
							// System.out.println(shortestBlockYBelowPlayer);
							// player.motionY+=1;

							// System.out.println(shortestBlockYBelowPlayer);
							if (player.posY - shortestBlockYBelowPlayer < (QLlvl / 33)) {
								player.motionY += 0.01d;
								player.isAirBorne = true;
								// System.out.println(player.motionY);

							} else {
								player.motionY -= 0.01d;
								player.fallDistance = -0.5f;

								// System.out.println(player.worldObj.isRemote+" "+(player.posY));
								// on the client side the pos has the height
								// added to it
							}
							player.motionX = 0;
							player.motionZ = 0;
							player.posX = player.prevPosX;
							player.posZ = player.prevPosZ;
							// System.out.println(player.motionY);
							if (player instanceof EntityPlayerMP)
								((EntityPlayerMP) player).playerNetServerHandler
										.sendPacket(new S12PacketEntityVelocity(
												player));

						}
					}
					// else System.out.println("mop is null");
				}
				if (spd.getModifier(meditatespddebuff) == null) {
					spd.applyModifier(MeditateSpdDebuff);
					// System.out.println("applied debuff");
				}

			} else
				spd.removeModifier(MeditateSpdDebuff);
			if (NeedyLittleThings.isUltimating(player)
					&& player.inventory.currentItem != player.getEntityData()
							.getInteger("TaoistUltimateSlot")) {
				player.inventory.currentItem = player.getEntityData()
						.getInteger("TaoistUltimateSlot");
			}
			if (held != null) {
				if (held.getItem() instanceof IUltimate) {
					IUltimate ult = (IUltimate) held.getItem();
					if (player.worldObj.getTotalWorldTime()
							- player.getEntityData().getLong(
									"HissatsuTimeStart") < ult
								.getUltimateTime()
							&& player.inventory.currentItem == player
									.getEntityData().getInteger(
											"TaoistUltimateSlot")
							&& XiuWeiHandler
									.getThis(player)
									.getSkillAwesomeness(
											"Ultimate" + GenericTaoistWeapon.getRealName(held.getUnlocalizedName())) >0) {
						ult.onUltimateTick(player);
						//System.out.println("hi");
					}
					//debug
				}
			}
			
			if (player.attackTime == 1 && player instanceof EntityPlayerMP) {
				Taoism.net.sendTo(new PacketAnimUpdate(false),
						(EntityPlayerMP) player);
			}
			// System.out.println(UUID.randomUUID());
		}
	}

	private void onTickEnd(EntityPlayer player) {
		((PlayerResourceStalker) (player
				.getExtendedProperties("taoistplayerstalk"))).regenValues();
		//player.addPotionEffect(new PotionEffect(TaoisticPotions.Hide.id,100,3));
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {

	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		PlayerResourceStalker.get(event.player).setValues(
				WayofConfig.LingLiDWID, 0.0F);
		PlayerResourceStalker.get(event.player).setValues(WayofConfig.QiDWID,
				0.0F);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(
			PlayerEvent.PlayerChangedDimensionEvent event) {

	}
}
