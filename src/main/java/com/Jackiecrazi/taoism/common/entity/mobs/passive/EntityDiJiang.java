package com.Jackiecrazi.taoism.common.entity.mobs.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.client.models.entity.mobs.animation.dijiang.AnimationHandlerDijiang;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.entity.ai.aerialai.EntityAIFlyingWander;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityDiJiang extends EntityTaoisticCreature implements IMCAnimatedEntity {
	protected AnimationHandler animHandler = new AnimationHandlerDijiang(this);
	public EntityDiJiang(World par1World) {
		super(par1World);
		//this.tasks.addTask(0, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0f, 0.1, 0.4));
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(5, new EntityAIFlyingWander(this, 0.4D));
		//this.tasks.addTask(5, new EntityAIWander(this, 0.4D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        //this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        //this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        //this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		setSize(2F, 3F);
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public AnimationHandler getAnimationHandler() {
		return animHandler;
	}

	@Override
	public void onUpdate()
	{
		if(!animHandler.isAnimationActive("baiwei"))animHandler.activateAnimation("baiwei", 0);
		if(!animHandler.isAnimationActive("feixing")&&!this.onGround&&!this.inWater){
			animHandler.activateAnimation("feixing", 0);
			//System.out.println("fly");
		}
		else if(this.onGround||this.inWater){
			animHandler.stopAnimation("feixing");
			//System.out.println(this.isAirBorne);
		}
		if(!animHandler.isAnimationActive("xingzou")&&(this.motionX!=0||this.motionZ!=0)){//
			animHandler.activateAnimation("xingzou", 0);
			//System.out.println("walk");
			}
		else if(this.motionX==0&&this.motionZ==0)animHandler.stopAnimation("xingzou");
			if(!this.inWater&&this.motionY<0)
		this.motionY*=0.5f;
			this.fallDistance=0;
		super.onUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
	}
	@Override
	public AxisAlignedBB getCollisionBox(Entity p) {
		return p.getBoundingBox();
		// return AxisAlignedBB.getBoundingBox(-1, -0.5, -1, 1, 3, 1);
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	/*protected void fall(float p_70069_1_) {}

	protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {}*/
	//tame with music, is cute, 
}