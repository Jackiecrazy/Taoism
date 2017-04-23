package com.Jackiecrazi.taoism.common.entity.ai.aquaai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAILeapFromWater extends EntityAIBase {
	// make it leap from water when at the end of wander, randomly, if touching.
	// to do that make it compatible with everything and don't set a target
	// and make it shouldexecute when it's at the destination and add velocity at start
	EntityTaoisticCreature etc;
	private double height=1;
			private int freq=120;
			private double triggerBloodPercentage;
	public EntityAILeapFromWater(EntityTaoisticCreature e) {
		etc=e;
		this.setMutexBits(2);
	}
	public EntityAILeapFromWater setHeight(double ne){
		height=ne;
		return this;
	}
	public EntityAILeapFromWater setFreq(int ne){
		freq=ne;
		return this;
	}
	public EntityAILeapFromWater setMaxHP(double ne){
		triggerBloodPercentage=ne;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		return etc.worldObj.getBlock((int)etc.posX, (int)etc.posY+1, (int)etc.posZ)==Blocks.air&&
				etc.getRNG().nextInt(freq)==0
				&&etc.isInWater()&&etc.dpf.targetPosition!=null&&
				etc.getHealth()/etc.getMaxHealth()<triggerBloodPercentage;
	}

	public void startExecuting() {
		if(etc.dpf.targetPosition==null)return;
		System.out.println("accelerate as x:"+(etc.dpf.targetPosition.posX-etc.posX)/100f+"   y:"+height+"   z:"+(etc.dpf.targetPosition.posZ-etc.posZ)/100f);
		etc.addVelocity((etc.dpf.targetPosition.posX-etc.posX)/100f, height, (etc.dpf.targetPosition.posZ-etc.posZ)/100f);
	}
	
}
