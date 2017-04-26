package com.Jackiecrazi.taoism.common.entity.mobs.hostile;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.common.entity.ai.EntityAIAvoid;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAIRangedAttack;
import com.Jackiecrazi.taoism.common.entity.ai.aerialai.EntityAIFlyingWander;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAISayo;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAISetAvoid;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAITargetAMob;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;
import com.Jackiecrazi.taoism.common.entity.projectile.EntityElementalProjectile;

public class EntityLingJing extends EntityTaoisticCreature {

	public EntityLingJing(World w) {
		/*super(w);
		this.tasks.addTask(2, new EntityAIRangedAttack(this,20,0,30D,20D,10D,3).setAutoaim(true).setEnabled(true));
		this.tasks.addTask(3, new EntityAIMelee(this,0.2));
		this.tasks.addTask(4, new EntityAIFlyingWander(this,0.2));
		//this.tasks.addTask(1, new EntityAINinja(this,60));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAITargetAMob(this,EntitySquid.class,5,false,true));
		this.targetTasks.addTask(0, new EntityAISayo(this,true));
		this.setSize(0.5f, 1);
	}

	protected void populate(){
		this.preferredLiquid.add(Material.rock);
		this.preferredLiquid.add(Material.air);
		this.ems.fly=true;
		this.noClip=true;
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
	   getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
	}
	
	
	
	*/
		super(w);
		this.ems.swim=true;
		this.ems.fly=true;
		this.getNavigator().setAvoidsWater(false);
		this.getNavigator().setCanSwim(true);
		//this.preferredLiquid=(MaterialLiquid) Material.water;
		//this.ems.fly=true;
		this.dpf.maxFlyHeight=20;
		this.tasks.addTask(0, new EntityAIAvoid(this).setFarDistance(32).setFarSpeed(1.3).setNearDistance(4).setNearSpeed(0.6).setTargetClass(Entity.class));
		this.tasks.addTask(4, new EntityAIFlyingWander(this,1).setRate(40));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAISayo(this, false));
        this.targetTasks.addTask(0, new EntityAITargetAMob(this, EntityLiving.class, 0, true));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityThrowable.class));
        this.targetTasks.addTask(0, new EntityAISetAvoid(this).setTargetClass(EntityArrow.class));
        //this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)); TODO revamp this
        this.tasks.addTask(2, new EntityAIRangedAttack(this,10,16,16d,16,0, 1.0D).setEnabled(true).setFireRate(10).setFireRange(16));// What do the values mean?
        this.setSize(0.5f, 1);
        this.setHomeDistanceMax(64f);
        
	}
	
	public EntityLingJing(World w, double x, double y, double z) {
		this(w);
		this.setPositionAndRotation(x, y, z, 0, 0);
		this.setHome((int)x, (int)y, (int)z, 64f);
	}
	
	public void onUpdate()
    {
		super.onUpdate();
		
    }
	public void populate(){
		this.preferredLiquid.add(Material.rock);
		this.preferredLiquid.add(Material.water);
		this.preferredLiquid.add(Material.air);
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
	public void range(EntityLivingBase elb,float dam){
		EntityElementalProjectile entitysnowball = new EntityElementalProjectile(this.worldObj, this,5,this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()/5);
        double d0 = elb.posX - this.posX;
        double d1 = elb.posY + (double)elb.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        double d2 = elb.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.worldObj.spawnEntityInWorld(entitysnowball);
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
	}
	public float getBlockPathWeight(int par1, int par2, int par3) {
		float ret=0f;
        if(worldObj.getBlock(par1, par2, par3).getMaterial().equals(Material.air))ret=0.9f;
        if(worldObj.getBlock(par1, par2, par3).getMaterial().equals(Material.rock))ret=0.3f;
        
    	return ret;
    }
	public boolean attackEntityFrom(DamageSource ds, float da)
    {
		if(ds==DamageSource.inWall||ds==DamageSource.fall)return false;
		return super.attackEntityFrom(ds, da);
    }
	
}
