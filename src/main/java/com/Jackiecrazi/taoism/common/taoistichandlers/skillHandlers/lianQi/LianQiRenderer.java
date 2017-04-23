package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.SkillRender;

public class LianQiRenderer extends SkillRender {

	public LianQiRenderer(int xOffset, int yOffset) {
		super(xOffset, yOffset, Taoism.GRAY);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Integer getLvl(EntityPlayer p){
		LianQiHandler test=LianQiHandler.getThis(p);
		return test.getLevel();
	}
	@Override
	public Float getExp(EntityPlayer p){
		LianQiHandler test=LianQiHandler.getThis(p);
		return test.getXP();
	}
	@Override
	public Float getExpNeeded(EntityPlayer p){
		LianQiHandler test=LianQiHandler.getThis(p);
		return test.calculateXPNeeded();
	}
}
