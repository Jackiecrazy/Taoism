package com.Jackiecrazi.taoism.common.entity.ai.targetai;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAISetAvoid extends EntityAITargetNew {

	// Targets:
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private int targetChance = 0;
    private Sorter targetSorter;
    protected boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAISetAvoid(EntityTaoisticCreature settaskOwner) {
        super(settaskOwner);
        this.setMutexBits(8);
        this.targetSelector = new EntityAISelectMob(this, (IEntitySelector)null);
        this.targetSorter = new Sorter(settaskOwner);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAISetAvoid setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAISetAvoid setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAISetAvoid setSightCheck(boolean setSightCheck) {
    	this.shouldCheckSight = setSightCheck;
    	return this;
    }
    public EntityAISetAvoid setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    /*public EntityAISetAvoid setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }*/
    public EntityAISetAvoid setSelector(IEntitySelector selector) {
    	this.targetSelector = new EntityAISelectMob(this, selector);
    	return this;
    }
    public EntityAISetAvoid setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                    taskOwner Target
 	// ==================================================
    @Override
    protected Entity getTarget() { 
    	return this.taskOwner.getAvoidTarget(); }
    @Override
    protected void setTarget(Entity newTarget) {
    	//System.out.println(newTarget);
    	this.taskOwner.setAvoidTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isSuitableTarget(EntityLivingBase target) {
    	// Own Class Check:
    	if(this.targetClass != this.taskOwner.getClass() && target.getClass() == this.taskOwner.getClass())
            return false;
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	
    	this.avoidTarget = null;
    	
        if(this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.taskOwner.useFlightNavigator()) heightDistance = distance;
        List possibleTargets = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            return false;
        else{
            this.avoidTarget = (EntityLivingBase)possibleTargets.get(0);
            this.attackTarget=(EntityLivingBase)possibleTargets.get(0);
        }
        //System.out.println("avoiding "+possibleTargets.get(0));
        return this.avoidTarget != null&&this.attackTarget!=null;
    }

}
