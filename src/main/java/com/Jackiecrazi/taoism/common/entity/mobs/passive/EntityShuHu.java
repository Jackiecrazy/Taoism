package com.Jackiecrazi.taoism.common.entity.mobs.passive;

import net.minecraft.world.World;

import com.Jackiecrazi.taoism.client.models.entity.mobs.animation.shuhu.AnimationHandlerShuhu;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.entity.ai.aerialai.EntityAIFlyingWander;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityShuHu extends EntityTaoisticCreature implements IMCAnimatedEntity {
	protected AnimationHandler animHandler = new AnimationHandlerShuhu(this);
	//rideable
	public EntityShuHu(World p_i1604_1_) {
		super(p_i1604_1_);
		this.tasks.addTask(5, new EntityAIFlyingWander(this,10));
		this.setSize(2, 2);
	}
	
	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}
	
	public void onUpdate()
	{
		super.onUpdate();
		//this.rotationYaw=0;
		this.motionY*=0.1;
		this.fallDistance=0;
		if(!this.onGround){
			if(!getAnimationHandler().isAnimationActive("feixing")){
				getAnimationHandler().activateAnimation("feixing", 0);
			}
		}
		else getAnimationHandler().stopAnimation("feixing");
		
		//if(onGround){
			if(this.motionX!=0||this.motionZ!=0){
				if(this.moveForward>0.1){
					if(!getAnimationHandler().isAnimationActive("benpao"))
					getAnimationHandler().activateAnimation("benpao", 0);
				}
				else {
					if(!getAnimationHandler().isAnimationActive("xingzou"))
					getAnimationHandler().activateAnimation("xingzou", 0);
				}
			}
			else{
				getAnimationHandler().stopAnimation("xingzou");
				getAnimationHandler().stopAnimation("benpao");
			}
		//}
	}

	@Override
	public AnimationHandler getAnimationHandler() {
		return animHandler;
	}
}
