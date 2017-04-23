package com.Jackiecrazi.taoism.common.entity.mobs.hostile;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.client.models.entity.mobs.animation.luoyu.AnimationHandlerLuoYu;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.entity.ModEntities;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAIAvoid;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAIGlide;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAIRangedAttack;
import com.Jackiecrazi.taoism.common.entity.ai.aerialai.EntityAIFlyingWander;
import com.Jackiecrazi.taoism.common.entity.ai.aquaai.EntityAIFlopToWater;
import com.Jackiecrazi.taoism.common.entity.ai.aquaai.EntityAILeapFromWater;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAISayo;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAISetAvoid;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAITargetAMob;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;
import com.Jackiecrazi.taoism.common.entity.projectile.EntityElementalProjectile;

public class EntityLuoYu extends EntityTaoisticCreature implements IMCAnimatedEntity {
	//long range attack when high health, charge to kill when 
	//target low health. Self low health fly and terrorize passives to regen
	protected AnimationHandler animHandler = new AnimationHandlerLuoYu(this);
	public int groundHeight;
	public EntityLuoYu(World p_i1738_1_) {
		super(p_i1738_1_);
		this.ems.swim=true;
		this.getNavigator().setAvoidsWater(false);
		this.getNavigator().setCanSwim(true);
		//this.preferredLiquid=(MaterialLiquid) Material.water;
		//this.ems.fly=true;
		this.dpf.maxFlyHeight=7;
		this.tasks.addTask(0, new EntityAIAvoid(this).setFarDistance(32).setFarSpeed(1.3).setNearDistance(4).setNearSpeed(0.6).setTargetClass(EntityPlayer.class));
		this.tasks.addTask(0, new EntityAIAvoid(this).setFarDistance(32).setFarSpeed(1.3).setNearDistance(4).setNearSpeed(0.6).setTargetClass(EntityArrow.class));
		this.tasks.addTask(0, new EntityAIAvoid(this).setFarDistance(32).setFarSpeed(1.3).setNearDistance(4).setNearSpeed(0.6).setTargetClass(EntityThrowable.class));
		this.tasks.addTask(4, new EntityAIFlyingWander(this,1).setRate(40));
		this.tasks.addTask(1, new EntityAILeapFromWater(this).setFreq(9).setHeight(0.4).setMaxHP(0.3));
		this.tasks.addTask(3, new EntityAIGlide(this).setFallSpeed(0.4).setRunSpeed(0.03));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.tasks.addTask(0, new EntityAIFlopToWater(this));
        this.targetTasks.addTask(1, new EntityAISayo(this, false));
        this.targetTasks.addTask(1, new EntityAITargetAMob(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAITargetAMob(this, EntitySquid.class, 0, true));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityOcelot.class).setChance(10));
        
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityThrowable.class));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityArrow.class));
        //this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)); TODO revamp this
        this.tasks.addTask(2, new EntityAIRangedAttack(this,10,16,16d,4d,16d, 1.0D).setEnabled(true).setFireRate(10).setFireRange(16));// What do the values mean?
        this.setSize(2, 1);
        
	}
	public void onUpdate()
    {
		super.onUpdate();
		//this.rotationYaw=0;
		
		/*if(this.getNavigator().getPath()!=null&&this.getNavigator().getPath().getFinalPathPoint().yCoord>this.posY)
	    	motionY+=worldObj.rand.nextGaussian();
	    	else motionY-=worldObj.rand.nextGaussian();*/
		/*if(!this.inWater&&this.motionY<0)
		this.motionY*=0.1;*/
		//System.out.println((int)this.posX+"   "+(int)this.posY+"   "+(int)this.posZ+"   "+this.worldObj.isRemote);
		if(!this.onGround&&!this.inWater){
			//.out.println("fly fish!");
			animHandler.stopAnimation("youdong");
			if(!animHandler.isAnimationActive("feixing"))
		getAnimationHandler().activateAnimation("feixing", 0);
		}
		else {
			animHandler.stopAnimation("feixing");
			if(!animHandler.isAnimationActive("youdong"))
				getAnimationHandler().activateAnimation("youdong", 0);
		}
    }
	public void populate(){
		this.preferredLiquid.add(Material.water);
		this.preferredLiquid.add(Material.air);
	}
	@Override
	public AnimationHandler getAnimationHandler() {
		return animHandler;
	}
	@Override
	protected boolean isAIEnabled()
	{
	   return true;
	}
	public boolean canBreatheUnderwater()
    {
        return true;
    }
	@Override
	public void range(EntityLivingBase elb,
			float dam) {
		EntityElementalProjectile entitysnowball = new EntityElementalProjectile(this.worldObj, this,2,this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
        double d0 = elb.posX - this.posX;
        double d1 = elb.posY + (double)elb.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        double d2 = elb.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.worldObj.spawnEntityInWorld(entitysnowball);
	}
	public boolean attackEntityAsMob(Entity e)
    {
        this.heal((float) (this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()/2));
        return super.attackEntityAsMob(e);
    }
	
	@Override
	protected void applyEntityAttributes()
	{
	    super.applyEntityAttributes(); 

	    // standard stuff
	   getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	   getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20D);
	   getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.4D);
	   getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);

	    // elemental resistance
	   getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
	   getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
	   getEntityAttribute(ModEntities.RESISTANCE_WATER).setBaseValue(40);
	}
	public float getBlockPathWeight(int par1, int par2, int par3) {
		float ret=0f;
        if(worldObj.getBlock(par1, par2, par3).getMaterial().equals(Material.water))ret=0.9f;
        if(worldObj.getBlock(par1, par2, par3).getMaterial().equals(Material.air))ret=0.3f;
        
    	return ret;
    }
}
