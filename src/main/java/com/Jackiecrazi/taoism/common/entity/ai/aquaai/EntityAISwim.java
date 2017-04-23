package com.Jackiecrazi.taoism.common.entity.ai.aquaai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISwim extends EntityAIBase {
	private EntityLiving host;
	private boolean sink;
	public EntityAISwim(EntityLiving e,boolean s) {
		this.host=e;
		sink=s;
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setNina(boolean d){
		sink=d;
	}
	/**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
    	host.motionY=0.0000001f;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
    	host.motionY=0;
    }

    /**
     * Updates the task
     */
    public void updateTask() {
    	/*if(this.sink) {
	    	double targetY = this.host.posY;
	    	if(!this.host.useDirectNavigator()) {
	    		if(!this.host.getNavigator().noPath())
                    targetY = this.host.getNavigator().getPath().getPosition(this.host).yCoord;
	    	}
	    	else {
	    		if(!this.host.directNavigator.atTargetPosition()) {
                    targetY = this.host.directNavigator.targetPosition.getY();
                }
	    	}
	    	if(this.host.posY < targetY && !this.host.canSwim())
                this.host.getJumpHelper().setJumping();
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F && !this.host.canSwim())
            this.host.getJumpHelper().setJumping();*/
    }
}
