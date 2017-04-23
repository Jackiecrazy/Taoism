package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.SkillRender;

public class LianDanRenderer extends SkillRender {
	public LianDanRenderer(int xOffset, int yOffset) {
		super(xOffset, yOffset,Taoism.GOLD);
		this.texture="minecraft:items/iron_sword.png";//TODO find a texture artist
	}
	@Override
	public Integer getLvl(EntityPlayer p){
		LianDanHandler test=(LianDanHandler) LianDanHandler.getThis(p);
		return test.getLevel();
	}
	@Override
	public Float getExp(EntityPlayer p){
		LianDanHandler test=LianDanHandler.getThis(p);
		return test.getXP();
	}
	@Override
	public Float getExpNeeded(EntityPlayer p){
		LianDanHandler test=LianDanHandler.getThis(p);
		return test.calculateXPNeeded();
	}
}
