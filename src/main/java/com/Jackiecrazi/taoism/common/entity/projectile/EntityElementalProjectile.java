package com.Jackiecrazi.taoism.common.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageElemental;

public class EntityElementalProjectile extends EntityThrowable {
	//metal, wood, water, fire, earth, wind, thunder, yin, yang, sha
	//they will heal their respective elements and whatever they feed, and hurt more of what they don't.
	private int element;
	private double damage;
	public EntityElementalProjectile(World p_i1777_1_) {
		super(p_i1777_1_);
	}
	public EntityElementalProjectile(World w,
			EntityLivingBase elb,int e,double d) {
		super(w, elb);
		element=e;
		damage=d;
	}

	@Override
	protected void onImpact(MovingObjectPosition result) {
		if(result.typeOfHit==MovingObjectType.BLOCK)applyElementBlock();
		if(result.typeOfHit==MovingObjectType.ENTITY)applyElementEntity(result.entityHit);
		this.setDead();
	}
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		super.writeEntityToNBT(tag);
	}

	public void readEntityFromNBT(NBTTagCompound tag)
	{
		super.readEntityFromNBT(tag);
	}
	private void applyElementEntity(Entity e){
		if(e instanceof EntityLivingBase){
			EntityLivingBase elb=(EntityLivingBase)e;
			elb.attackEntityFrom(DamageElemental.causeElementDamageIndirectly(getThrower(), this, DamageElemental.TaoistElement.values()[element]), (float)damage);
			if(this.getThrower()!=null)
			this.getThrower().attackEntityAsMob(elb);
		}
	}
	private void applyElementBlock(){

	}
	protected float getGravityVelocity()
	{
		return 0F;
	}
	/**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
    	super.onUpdate();
    	if(this.ticksExisted>200){
    		this.setDead();
    		return;
    	}
    }
}
