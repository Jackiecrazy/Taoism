package com.Jackiecrazi.taoism.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIMelee extends EntityAIBase {
	private EntityTaoisticCreature etc;
	private EntityLivingBase uke;
	private double speed,maxRange;private int CD;
	public EntityAIMelee(EntityTaoisticCreature host,double spd) {
		this.setMutexBits(1);
		etc=host;
		speed=spd;
	}

	public EntityAIMelee setCD(int ca){
		CD=ca;
		return this;
	}
	
	public EntityAIMelee setMax(double ca){
		maxRange=ca;
		return this;
	}
	
	@Override
	public boolean shouldExecute() {
		
		EntityLivingBase possibleAttackTarget = this.etc.getAttackTarget();
		if(possibleAttackTarget == null)
			return false;
		if(!possibleAttackTarget.isEntityAlive())
			return false;
		uke = possibleAttackTarget;
		//System.out.println("executing");
		return true;
	}
	
	public boolean continueExecuting()
    {
        return this.shouldExecute()&&etc.getDistanceToEntity(uke)<maxRange;
    }

	@Override
	public void updateTask(){
		
		if(uke==null)return;
		double d0 = this.etc.getDistanceSq(uke.posX, uke.boundingBox.minY, uke.posZ);
        double d1 = (double)(this.etc.width * 2.0F * this.etc.width * 2.0F + uke.width);
			if(!etc.useFlightNavigator()){
				//System.out.println("set fire land");
				this.etc.getNavigator().tryMoveToEntityLiving(this.uke, this.speed);
			}
			else{
				//System.out.println("set fire");
				this.etc.dpf.setTargetPosition(new ChunkCoordinates((int)uke.posX,(int)uke.posY,(int)uke.posZ), 1.0d);//this be null, wtf
				
			}

		this.etc.getLookHelper().setLookPositionWithEntity(this.uke, 30.0F, 30.0F);
		this.etc.dpf.adjustRotationToTarget(new ChunkCoordinates((int)uke.posX,(int)uke.posY,(int)uke.posZ));
		
		if(d0<=d1&&etc.attackTime==0){
			etc.melee(etc, (float) etc.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
			etc.attackTime=CD;
		}
	}
}
