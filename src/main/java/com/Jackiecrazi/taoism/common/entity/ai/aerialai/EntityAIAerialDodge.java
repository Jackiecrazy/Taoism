package com.Jackiecrazi.taoism.common.entity.ai.aerialai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import com.Jackiecrazi.taoism.common.entity.base.CustomPositionGenerator;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIAerialDodge extends EntityAIBase {
	EntityTaoisticCreature entity;
	EntityThrowable proj;
	private double xPosition,yPosition,zPosition,speed;
	public EntityAIAerialDodge(EntityTaoisticCreature e,double spd) {
		this.entity=e;
		this.setMutexBits(7);
		speed=spd;
	}

	@Override
	public boolean shouldExecute() {

    	int flight = 0;
    	if(this.entity.useFlightNavigator()) flight = (int) this.entity.dpf.maxFlyHeight;
    	
        Vec3 vec3 = CustomPositionGenerator.findRandomTarget(this.entity, 10, flight==0?7:flight);
        
        if (vec3 == null)
        {
            return false;
        }
        else
        {
        	//System.out.println(vec3.toString());
            this.xPosition = vec3.xCoord;
            this.yPosition = vec3.yCoord;
            this.zPosition = vec3.zCoord;
        }
    
		return !entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, AxisAlignedBB.getBoundingBox(entity.posX-5, entity.posY-5, entity.posZ-5, entity.posX+5, entity.posY+5, entity.posZ+5)).isEmpty();
	}
	
	public boolean isInterruptible()
    {
        return false;
    }
	
	public boolean continueExecuting()
    {
    	if(entity.useFlightNavigator())return !this.entity.dpf.atTargetPosition()&&this.entity.dpf.isTargetPositionValid();
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	if(entity.useFlightNavigator()){
    		this.entity.dpf.adjustRotationToTarget(new ChunkCoordinates((int)this.xPosition,(int)this.yPosition,(int)this.zPosition));
    		this.entity.dpf.setTargetPosition(new ChunkCoordinates((int)this.xPosition,(int)this.yPosition,(int)this.zPosition), this.speed);
    	}
    	else this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}
