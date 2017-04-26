package com.Jackiecrazi.taoism.common.entity.mobs.hostile;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.client.models.entity.mobs.animation.lili.AnimationHandlerLili;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.entity.TaoEntities;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAINinja;
import com.Jackiecrazi.taoism.common.entity.ai.EntityAIRangedAttack;
import com.Jackiecrazi.taoism.common.entity.ai.aerialai.EntityAIFlyingWander;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAISayo;
import com.Jackiecrazi.taoism.common.entity.ai.targetai.EntityAITargetAMob;
import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;
import com.Jackiecrazi.taoism.common.entity.projectile.EntityElementalProjectile;

public class EntityLiLi extends EntityTaoisticCreature implements IMCAnimatedEntity {

	private AnimationHandlerLili ah;
	public EntityLiLi(World w) {
		super(w);
		this.tasks.addTask(2, new EntityAIRangedAttack(this,60,0,30D,20D,10D,3).setAutoaim(true));
		this.tasks.addTask(4, new EntityAIFlyingWander(this,0.2));
		this.tasks.addTask(1, new EntityAINinja(this,60));
		this.targetTasks.addTask(1, new EntityAITargetAMob(this,EntityPlayer.class,5,false,true));
		this.targetTasks.addTask(0, new EntityAISayo(this,true));
		ah=new AnimationHandlerLili(this);
		this.setSize(2, 2);
	}

	public EntityLiLi(World w, Material... liquid) {
		super(w, Material.air,Material.ground);
	}

	public void range(EntityLivingBase elb,float dam){
		EntityElementalProjectile entitysnowball = new EntityElementalProjectile(this.worldObj, this,4,this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
        double d0 = elb.posX - this.posX;
        double d1 = elb.posY + (double)elb.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        double d2 = elb.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.worldObj.spawnEntityInWorld(entitysnowball);
    }

	@Override
	public AnimationHandler getAnimationHandler() {
		return ah;
	}
	
	public boolean attackEntityFrom(DamageSource ds, float da)
    {
		if(ds==DamageSource.inWall||ds==DamageSource.fall)return false;
		return super.attackEntityFrom(ds, da);
    }
	
	protected void populate(){
		//this.preferredLiquid.add(Material.air);
		this.preferredLiquid.add(Material.ground);
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
	   getEntityAttribute(TaoEntities.RESISTANCE_EARTH).setBaseValue(30);
	}
}
