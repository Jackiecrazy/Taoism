package com.Jackiecrazi.taoism.common.entity.ai.targetai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAISelectMob implements IEntitySelector {

	// Properties:
    final EntityAIBase targetAI;
    final IEntitySelector selector;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    EntityAISelectMob(EntityAIBase setTargetAI, IEntitySelector setSelector) {
        this.targetAI = setTargetAI;
        this.selector = setSelector;
    }
    
    
    // ==================================================
  	//                  Is Applicable
  	// ==================================================
    public boolean isEntityApplicable(Entity target) {
        if(this.selector != null && !this.selector.isEntityApplicable(target))
        	return false;
        if(this.targetAI instanceof EntityAITargetNew) {
            if(!(target instanceof EntityLivingBase))
            	return false;
	    	if(!((EntityAITargetNew)this.targetAI).isSuitableTarget((EntityLivingBase)target, false))
	    		return false;
        }
    	return true;
    }

}
