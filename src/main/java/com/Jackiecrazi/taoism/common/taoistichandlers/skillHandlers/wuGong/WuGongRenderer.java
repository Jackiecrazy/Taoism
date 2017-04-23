package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.SkillRender;

public class WuGongRenderer extends SkillRender {
	public WuGongRenderer(int xOffset, int yOffset) {
		super(xOffset, yOffset,Taoism.DARK_RED);
		this.texture="minecraft:iron_sword";//TODO find a texture artist
	}
	@Override
	public Integer getLvl(EntityPlayer p){
		WuGongHandler test=(WuGongHandler) WuGongHandler.getThis(p);
		return test.getLevel();
	}
	@Override
	public Float getExp(EntityPlayer p){
		WuGongHandler test=WuGongHandler.getThis(p);//TODO this gets the client side. Server updates but clients don't
		return test.getXP();
	}
	@Override
	public Float getExpNeeded(EntityPlayer p){
		WuGongHandler test=WuGongHandler.getThis(p);//TODO this gets the client side. Server updates but clients don't
		return test.calculateXPNeeded();
	}
	
}
