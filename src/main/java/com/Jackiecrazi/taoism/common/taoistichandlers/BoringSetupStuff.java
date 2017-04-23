package com.Jackiecrazi.taoism.common.taoistichandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.entity.ModEntities;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan.LianDanHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi.LianQiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;
import com.Jackiecrazi.taoism.networking.PacketAnimUpdate;
import com.Jackiecrazi.taoism.networking.PacketSetPlayerMeditating;
import com.Jackiecrazi.taoism.networking.PacketSetSkillStuff;
import com.Jackiecrazi.taoism.networking.PacketSetUnlockSkill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class BoringSetupStuff {

	public BoringSetupStuff() {
		// TODO Auto-generated constructor stub
	}
	@SubscribeEvent
	public void boringConstruction(EntityEvent.EntityConstructing e) {
		if(e.entity instanceof EntityLivingBase){
			if (e.entity instanceof EntityPlayer) {
				EntityPlayer p=(EntityPlayer) e.entity;
				if (e.entity.getExtendedProperties("taoistplayerstalk") == null) {
					//System.out.println("hi");
					e.entity.registerExtendedProperties("taoistplayerstalk", new PlayerResourceStalker((EntityPlayer)e.entity));
				}
				if (XiuWeiHandler.getThis(p)==null) {
					//System.out.println("qilihandler is null!");
					e.entity.registerExtendedProperties("QiLiSkill", new XiuWeiHandler(p));
				}
				if(WuGongHandler.getThis(p)==null){
					e.entity.registerExtendedProperties("WuGongSkill", new WuGongHandler(p));
				}
				if(LianDanHandler.getThis(p)==null){
					e.entity.registerExtendedProperties("LianDanSkill", new LianDanHandler(p));
				}
				if(LianQiHandler.getThis(p)==null){
	    	e.entity.registerExtendedProperties("LianQiSkill", new LianQiHandler(p));
	    }
				if(AnimationStalker.getThis(p)==null){
					e.entity.registerExtendedProperties("TaoisticAnimationStalker", new AnimationStalker(p));
				}
				if(PlayerEquipmentStalker.getEquipmentList(p)==null){
					e.entity.registerExtendedProperties("taoistgear", new PlayerEquipmentStalker(p));
				}
			}
			else if(e.entity.getExtendedProperties("taoisttrainingstalk")==null)e.entity.registerExtendedProperties("taoisttrainingstalk", new TrainingStalker());
			EntityLivingBase elb=(EntityLivingBase) e.entity;
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_EARTH);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_FIRE);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_METAL);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_SHA);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_THUNDER);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_WATER);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_WIND);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_WOOD);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_YANG);
			elb.getAttributeMap().registerAttribute(ModEntities.RESISTANCE_YIN);
			elb.getAttributeMap().registerAttribute(ModEntities.LING_SPEED);
			elb.getAttributeMap().registerAttribute(ModEntities.LING_MAX);
			
		}
	}
	@SubscribeEvent
	public void boringJoin(PlayerEvent.PlayerLoggedInEvent e){
		EntityPlayer player=e.player;
		if(player instanceof EntityPlayerMP){
			Taoism.net.sendTo(new PacketSetSkillStuff(XiuWeiHandler.getThis(player).getSkill(),XiuWeiHandler.getThis(player).getLevel(),XiuWeiHandler.getThis(player).getXP()), (EntityPlayerMP)player);
			Taoism.net.sendTo(new PacketSetSkillStuff(WuGongHandler.getThis(player).getSkill(),WuGongHandler.getThis(player).getLevel(),WuGongHandler.getThis(player).getXP()), (EntityPlayerMP)player);
			Taoism.net.sendTo(new PacketSetSkillStuff(LianDanHandler.getThis(player).getSkill(),LianDanHandler.getThis(player).getLevel(),LianDanHandler.getThis(player).getXP()), (EntityPlayerMP)player);
			Taoism.net.sendTo(new PacketAnimUpdate(AnimationStalker.getThis(player).isActive(), AnimationStalker.getThis(player).getItemStack(), AnimationStalker.getThis(player).getIsRightClick(),
					AnimationStalker.getThis(player).getType(),player.inventory.currentItem), (EntityPlayerMP) player);
			Taoism.net.sendTo(new PacketSetPlayerMeditating(PlayerResourceStalker.get(player).getIsMeditating(),player), (EntityPlayerMP)player);
			Object[] skill=Skill.getAllSkillNames().toArray();
			for(int x=0;x<Skill.getAllSkillNames().size();x++){
				Taoism.net.sendTo(new PacketSetUnlockSkill((String) skill[x],XiuWeiHandler.getThis(player).getSkillAwesomeness((String) skill[x])), (EntityPlayerMP) player);
			}
		}
	}
	@SubscribeEvent
	public void boringLeave(PlayerEvent.PlayerLoggedOutEvent e){
		//EntityPlayer player=e.player;
		/*if(PlayerResourceStalker.get(player).getIsMeditating()){

		}*/
	}
}
