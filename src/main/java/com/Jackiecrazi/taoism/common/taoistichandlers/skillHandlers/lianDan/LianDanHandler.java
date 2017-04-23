package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;

public class LianDanHandler extends Skill {
	public LianDanHandler(EntityPlayer p) {
		super(p);
		this.name = "LianDanSkill";
		this.max=WayofConfig.LianDanMaxLvl;
	}

	public static LianDanHandler getThis(EntityPlayer p) {
		return (LianDanHandler) p.getExtendedProperties("LianDanSkill");
	}
}
