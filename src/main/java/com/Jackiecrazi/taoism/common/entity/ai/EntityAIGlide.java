package com.Jackiecrazi.taoism.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import com.Jackiecrazi.taoism.common.entity.base.CustomPositionGenerator;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIGlide extends EntityAIBase {

	EntityTaoisticCreature etc;
	private double descentspeed=3;
	private double aerodynamicness=1;
	private double xPosition;
    private double yPosition;
    private double zPosition;
    private boolean clear;
	public EntityAIGlide(EntityTaoisticCreature e) {
		etc=e;
		this.setMutexBits(8);
	}
	public EntityAIGlide setFallSpeed(double ne){
		descentspeed=ne;
		return this;
	}
	public EntityAIGlide setRunSpeed(double ne){
		aerodynamicness=ne;
		return this;
	}
	public EntityAIGlide setSpeed(boolean clears){
		clear=clears;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (etc.canFly()||
				etc.motionY>=0||
				etc.isInWater())
        {
			//System.out.println("glide fail");
            return false;
        }
        else
        {
        	//System.out.println("wings deployed");
        	//sets a position in the direction it's lookin' at
            Vec3 vec3 = CustomPositionGenerator.findRandomTargetAwayFrom(etc, 10, 7, etc.getLookVec());
            		//.findRandomTarget(this.etc, 10, 7);
            
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
                
                return true;
            }
        }
		
	}

	public void startExecuting() {
		//orient it in the correct direction
		this.etc.dpf.adjustRotationToTarget(new ChunkCoordinates((int)this.xPosition,(int)this.yPosition,(int)this.zPosition));
		this.etc.dpf.setTargetPosition(new ChunkCoordinates((int)this.xPosition,(int)this.yPosition,(int)this.zPosition), 1.0f);
	}
	public boolean continueExecuting()
    {
		//increment motion X/Z and decrease motionY
		double horizontalSpeed = aerodynamicness;
		double verticalSpeed = descentspeed;
		if (!etc.onGround && etc.motionY < 0) {
			//etc.dpf.adjustRotationToTarget(etc.dpf.targetPosition);
			etc.motionY *= verticalSpeed;
			double x = Math.cos(Math.toRadians(etc.rotationYawHead + 90)) * horizontalSpeed;
			double z = Math.sin(Math.toRadians(etc.rotationYawHead + 90)) * horizontalSpeed;
			etc.motionX += x;
			etc.motionZ += z;
			etc.fallDistance = 0f;
		}
        return !etc.isInWater()&&!etc.onGround;//!etc.dpf.atTargetPosition();
    }
	public void resetTask() {
		//set the new waypoint to be whereever it lands
		if(clear)
		etc.clearMovement();
	}
}
