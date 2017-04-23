package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi;

import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.tickhandlers.IApologizeForThisHandler;

public class XiuWeiHandler extends Skill {
	
	public XiuWeiHandler(EntityPlayer p) {
		super(p);
		this.name = "QiLiSkill";
		this.max=WayofConfig.QiLiMaxLvl;
	}

	public static XiuWeiHandler getThis(EntityPlayer p) {
		return (XiuWeiHandler) p.getExtendedProperties("QiLiSkill");
	}
	
	public void setLevel(int lev){
		super.setLevel(lev);
		UUID hi=IApologizeForThisHandler.qilispdbuff;
		IAttributeInstance spd=player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
		if(spd.getModifier(hi)!=null){
			
			spd.removeModifier(spd.getModifier(hi));
			//System.out.println("removed modifier");
		}
		final AttributeModifier QiLiSpeedBuf = (new AttributeModifier(hi, "QiLi Speed Bonus", this.level/200, 2)).setSaved(true);

		spd.applyModifier(QiLiSpeedBuf);
	}
	public static Set<String> getAllSkillNames(){
		return XiuWeiHandler.skillNamesAndMax.keySet();
	}
}
