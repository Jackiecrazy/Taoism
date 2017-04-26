package com.Jackiecrazi.taoism.common.entity.ai.aerialai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import com.Jackiecrazi.taoism.common.entity.base.CustomPositionGenerator;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIFlyingWander extends EntityAIBase
{
    private EntityTaoisticCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed=1;
    private int rate=120;//lower is faster
    
    public EntityAIFlyingWander(EntityTaoisticCreature mob, double speed)
    {
        this.entity = mob;
        this.speed = speed;
        this.setMutexBits(1);
    }
    public EntityAIFlyingWander setRate(int ne){
    	rate=ne;
    	return this;
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.entity.getAge() >= 100)
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(rate) != 0)
        {
            return false;
        }
        else
        {
        	int flight = 0;
        	if(this.entity.useFlightNavigator()) flight = (int) this.entity.dpf.maxFlyHeight;
        	
            Vec3 vec3 = CustomPositionGenerator.findRandomTarget(this.entity, 10, flight==0?7:flight);
            
            if (vec3 == null)
            {
                return false;
            }
            else
            {
            	System.out.println("execute wander script");
                this.xPosition = vec3.xCoord;
                this.yPosition = vec3.yCoord;
                this.zPosition = vec3.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
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