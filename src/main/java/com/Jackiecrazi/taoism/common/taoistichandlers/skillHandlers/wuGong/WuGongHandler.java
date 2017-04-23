package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;

public class WuGongHandler extends Skill {
	
	public WuGongHandler(EntityPlayer p) {
		super(p);
		this.name = "WuGongSkill";
		this.max=WayofConfig.WuGongMaxLvl;
	}

	public static WuGongHandler getThis(EntityPlayer p) {
		return (WuGongHandler) p.getExtendedProperties("WuGongSkill");
	}
	public static Set<String> getAllSkillNames(){
		return WuGongHandler.skillNamesAndMax.keySet();
	}
}
