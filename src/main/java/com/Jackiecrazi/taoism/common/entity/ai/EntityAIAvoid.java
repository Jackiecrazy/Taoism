package com.Jackiecrazi.taoism.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Vec3;

import com.Jackiecrazi.taoism.common.entity.base.CustomPositionGenerator;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIAvoid extends EntityAIBase {

	// Targets:
    private EntityTaoisticCreature host;
    private Entity avoidTarget;
    
    // Properties:
    private double farSpeed = 1.0D;
    private double nearSpeed = 1.2D;
    private double farDistance = 4096.0D;
    private double nearDistance = 49.0D;
    private Class targetClass;
    private float distanceFromEntity = 6.0F;
    private PathEntity pathEntity;
    private Vec3 vec3;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIAvoid(EntityTaoisticCreature setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIAvoid setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    public EntityAIAvoid setNearSpeed(double setSpeed) {
    	this.nearSpeed = setSpeed;
    	return this;
    }
    public EntityAIAvoid setFarDistance(double dist) {
    	this.farDistance = dist * dist;
    	return this;
    }
    public EntityAIAvoid setNearDistance(double dist) {
    	this.nearDistance = dist * dist;
    	return this;
    }
    public EntityAIAvoid setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	//System.out.println("a");
        this.avoidTarget = this.host.getAvoidTarget();//null because we never set it.
        if(this.avoidTarget == null) {
        	//System.out.println("target null");
        	return false;
        }
    	
        if(!this.avoidTarget.isEntityAlive()){
        	//System.out.println("dead");
        	//this.host.setAvoidTarget(null);
        	return false;
        }
            
    	
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(this.avoidTarget.getClass())){
        	//System.out.println("targ not same type");
        	this.host.setAvoidTarget(null);
        	return false;
        }

        if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance) {
        	/*System.out.println(this.host.getDistanceSqToEntity(this.avoidTarget));
        	System.out.println(this.farDistance);*/
        	return false;
        }
        
        vec3 = CustomPositionGenerator.findRandomTargetAwayFrom(this.host, 24, 10, Vec3.createVectorHelper(this.avoidTarget.posX, this.avoidTarget.posY, this.avoidTarget.posZ));
        if(vec3 == null){
        	//System.out.println("null vec");
            return false;
        }
        
        if(this.avoidTarget.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.avoidTarget.getDistanceSqToEntity(this.host)){
        	/*System.out.println(this.avoidTarget.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord));
        	System.out.println();
        	System.out.println(this.avoidTarget.getDistanceSqToEntity(this.host));
        	*/
            return false;
        }
        pathEntity=this.host.getNavigator().getPathToXYZ(vec3.xCoord,vec3.yCoord,vec3.zCoord);
        
        if(this.pathEntity == null&&!host.useFlightNavigator()){
        	//System.out.println("no path");
        	return false ;
        }
        else if(!host.dpf.setTargetPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, farSpeed))
        	return false;
        return
        		this.pathEntity!=null?
        this.pathEntity.isDestinationSame(vec3):true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
        if(!this.host.useFlightNavigator() && this.host.getNavigator().noPath())
        	return false;
		if(this.host.useFlightNavigator() && this.host.dpf.atTargetPosition())
			return false;
        if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance)
        	return false;
    	return true;
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useFlightNavigator())
    		this.host.getNavigator().setPath(this.pathEntity, this.farSpeed);
    	else if(this.pathEntity!=null)
    		this.host.dpf.setTargetPosition(pathEntity.getFinalPathPoint().xCoord,pathEntity.getFinalPathPoint().yCoord,pathEntity.getFinalPathPoint().zCoord, this.farSpeed);
    	else
    		this.host.dpf.setTargetPosition(vec3.xCoord,vec3.yCoord,vec3.zCoord, this.farSpeed);
    	//System.out.println("begin run away to "+host.dpf.targetPosition);
    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
        this.avoidTarget = null;
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {
        if(this.host.getDistanceSqToEntity(this.avoidTarget) < 49.0D)
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().setSpeed(this.nearSpeed);
        	else
        		this.host.dpf.spdMod = this.nearSpeed;
        else
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().setSpeed(this.farSpeed);
        	else
        		this.host.dpf.spdMod = this.farSpeed;
        //System.out.println("run forrest run");
    }

}
