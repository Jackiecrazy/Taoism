package com.Jackiecrazi.taoism.common.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityDummyProjectileExtendReach extends EntityThrowable {
	private float atkDam;
	private DamageSource ds;
	public EntityDummyProjectileExtendReach(World p_i1777_1_,
			EntityLivingBase p_i1777_2_) {
		super(p_i1777_1_, p_i1777_2_);
		// TODO Auto-generated constructor stub
	}
	public EntityDummyProjectileExtendReach(World p_i1777_1_,
			EntityLivingBase p_i1777_2_,float damage,float velocity,DamageSource s) {
		super(p_i1777_1_, p_i1777_2_);
		this.atkDam=damage;
		this.ds=s;
		float f = velocity;
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.func_70183_g()) / 180.0F * (float)Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.func_70182_d(), 1.0F);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) {
		if(mop!=null){
			if(mop.typeOfHit==MovingObjectType.ENTITY){
				Entity uke=mop.entityHit;
				if(uke.isEntityAlive()&&uke instanceof EntityLivingBase){
					((EntityLivingBase)uke).attackEntityFrom(ds, atkDam);
					uke.hurtResistantTime=0;
					this.setDead();
				}
			}
		}
	}
	public void onUpdate()
    {
		super.onUpdate();
		if(this.ticksExisted>10){
			this.setDead();
		}
    }

}
