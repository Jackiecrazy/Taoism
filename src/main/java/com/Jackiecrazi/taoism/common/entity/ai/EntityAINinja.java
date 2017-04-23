package com.Jackiecrazi.taoism.common.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAINinja extends EntityAIBase {
	private int invisTimeMax;
	private int hidingFrame;
	private EntityTaoisticCreature etc;
	public EntityAINinja(EntityTaoisticCreature host,int itm) {
		etc=host;
		invisTimeMax=itm;
		this.setMutexBits(0);
	}
	
	public EntityAINinja setFrames(int f){
		hidingFrame=f;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		boolean ret=false;
		ret=etc.func_94060_bK()!=null&&etc.getAttackTarget()!=null;
		if(!ret){
			boolean gauss=etc.worldObj.rand.nextGaussian()<0.01f;
			if(!gauss)ret=gauss;
			else
			for(Material mat:etc.preferredLiquid){
				if(etc.isInsideOfMaterial(mat)){
					ret=true;
					break;
				}
				else if(etc.isInsideOfMaterial(Material.air)){
					ret=true;
					break;
				}
			}
		}
		
		return ret&&!etc.isInWater();
	}

	public void startExecuting() {
		etc.setHideCounter(invisTimeMax);
		etc.setHidingCounter(hidingFrame);
		etc.noClip=true;
		//etc.ems.fallBuff/=5;
		System.out.println("begin!");
	}
	
	public boolean continueExecuting()
    {
		boolean clip=false;
		for(Material mat:etc.preferredLiquid){
			if(etc.isInsideOfMaterial(mat))clip=true;
			
		}
		if(etc.isInsideOfMaterial(Material.air))clip=true;
		//if(etc.getHideCounter()>0&&clip)etc.motionY*=0.5;
		return clip;
    }
	
	public void resetTask() {
		etc.addVelocity(0.5*Math.sin(etc.rotationYaw), 0.5, 0.3*Math.cos(etc.rotationYaw));
		etc.noClip=false;
		//etc.ems.fallBuff*=5;
		System.out.println("stop");
	}
}
