package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;

public class LianQiHandler extends Skill {

	public LianQiHandler(EntityPlayer p) {
		super(p);
		this.name = "LianQiSkill";
	}
	public static LianQiHandler getThis(EntityPlayer p){
		return (LianQiHandler)p.getExtendedProperties("LianQiSkill");
	}
}
