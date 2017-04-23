package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi;

import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.SkillRender;

public class SpiritRenderer extends SkillRender{

	public SpiritRenderer(int xOffset, int yOffset) {
		super(xOffset, yOffset,Taoism.BLUE);
		this.texture="minecraft:stick.png";//TODO find a texture artist
	}
	@Override
	public Integer getLvl(EntityPlayer p){
		XiuWeiHandler test=(XiuWeiHandler) XiuWeiHandler.getThis(p);
		return test.getLevel();
	}
	@Override
	public Float getExp(EntityPlayer p){
		XiuWeiHandler test=XiuWeiHandler.getThis(p);//TODO this gets the client side. Server updates but clients don't
		return test.getXP();
	}
	@Override
	public Float getExpNeeded(EntityPlayer p){
		XiuWeiHandler test=XiuWeiHandler.getThis(p);//TODO this gets the client side. Server updates but clients don't
		return test.calculateXPNeeded();
	}
}
