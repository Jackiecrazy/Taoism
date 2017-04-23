package com.Jackiecrazi.taoism.common.entity.literaldummies;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.client.models.entity.literaldummies.animations.Fengxiang.AnimationHandlerFengxiang;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;

public class EntityBellows extends Entity implements IMCAnimatedEntity {
	protected AnimationHandler animHandler = new AnimationHandlerFengxiang(this);

	public EntityBellows(World par1World) {
		super(par1World);
	}

	@Override
	protected void entityInit() {
		
	}
	
	@Override
	public AnimationHandler getAnimationHandler() {
		return animHandler;
	}

	@Override
	public void onUpdate()
	{
            super.onUpdate();
        }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}
}