package com.Jackiecrazi.taoism.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class EntityAIRangedAttack extends EntityAIBase {
	//parameters: fired proj (call class from entity), fire rate, requires direct sight,
	//is rapid fire (fires right after CD), attack range, whether it's actually enabled,
	//variable CDs for long/short range? If so, then add check to how long is "long" and "short"
	//some mobs are rapid-fire for a while then run out of juice, an int for stamina,
	//stamina per attack, recovery rate.
	private int fireRate,fireCD,staminaMax,staminaPerAtk,staminaRecovery,currentStamina;
	private double range,longRange,shortRange,speed;
	private boolean autoAim, enabled=true, rapidFire,rememberTarget=true,mountedAttacks;
	private final EntityTaoisticCreature etc;
	private EntityLivingBase uke;
	public EntityAIRangedAttack(EntityTaoisticCreature c,int FR, int FCD,double range,double lr, double sr,double spd) {
		etc=c;
		speed=spd;
		fireRate=FR;
		fireCD=FCD;
		this.range=range;
		longRange=lr;
		shortRange=sr;
		this.setMutexBits(1);
	}
	//======================
	//set stuff
	//======================
	public EntityAIRangedAttack setFireRate(int ne){
		fireRate=ne;
		return this;
	}
	public EntityAIRangedAttack setFireCD(int ne){
		fireCD=ne;
		return this;
	}
	public EntityAIRangedAttack setStaminaMax(int ne){
		staminaMax=ne;
		return this;
	}
	public EntityAIRangedAttack setStaminaPerAtk(int ne){
		staminaPerAtk=ne;
		return this;
	}
	public EntityAIRangedAttack setCurrentStamina(int ne){
		currentStamina=Math.min(ne, staminaMax);
		return this;
	}
	public EntityAIRangedAttack setFireRange(double ne){
		range=ne*ne;
		return this;
	}
	public EntityAIRangedAttack setLongRange(double ne){
		longRange=ne;
		return this;
	}
	public EntityAIRangedAttack setShortRange(double ne){
		shortRange=Math.min(ne, longRange);
		return this;
	}
	public EntityAIRangedAttack setSpeed(double ne){
		speed=ne;
		return this;
	}
	public EntityAIRangedAttack setAutoaim(boolean ne){
		autoAim=ne;
		return this;
	}
	public EntityAIRangedAttack setEnabled(boolean ne){
		enabled=ne;
		return this;
	}
	public EntityAIRangedAttack setRapidFire(boolean ne){
		rapidFire=ne;
		return this;
	}
	public EntityAIRangedAttack setRememberTarget(boolean ne){
		rememberTarget=ne;
		return this;
	}
	public EntityAIRangedAttack setMountedAttacks(boolean ne){
		mountedAttacks=ne;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		// Attack Stamina/Cooldown Recovery:
		if(this.staminaMax > 0) {
			if(this.autoAim) {
				this.currentStamina += this.staminaRecovery;
				if(this.currentStamina >= this.staminaMax)
					this.autoAim = false;
			}
		}

		// Should Execute:
		if(!this.enabled)
			return false;
		/*if(!this.mountedAttacks && this.host instanceof EntityCreatureRideable) { //TODO riding attacks
            EntityCreatureRideable rideableHost = (EntityCreatureRideable)this.host;
            if(rideableHost.getRiderTarget() instanceof EntityPlayer)
                return false;
        }*/
		EntityLivingBase possibleAttackTarget = this.etc.getAttackTarget();
		if(possibleAttackTarget == null)
			return false;
		if(!possibleAttackTarget.isEntityAlive())
			return false;
		uke = possibleAttackTarget;
		//System.out.println("executing");
		return true;
	}

	@Override
	public void updateTask(){
		//attack when enough stamina and ticksexisted%firerate, deplete stamina
		// do shenanigans with short and long range attack rate. Make sure target in range.
		//yeah.
		double d0 = this.etc.getDistanceSq(this.uke.posX, this.uke.boundingBox.minY, this.uke.posZ);
		boolean flag = this.etc.getEntitySenses().canSee(this.uke);

		/*if (flag)
        {
            ++this.field_75318_f;
        }
        else
        {
            this.field_75318_f = 0;
        }*///TODO figure out what that field is

		if (d0 <= (double)this.range) //&& this.field_75318_f >= 20)//75318 is distance to targ?
		{
			//System.out.println("clearing move");
			this.etc.clearMovement();
		}
		else
		{
			//System.out.println("on target");
			if(!etc.useFlightNavigator()){
				//System.out.println("set fire land");
				this.etc.getNavigator().tryMoveToEntityLiving(this.uke, this.speed);
			}
			else{
				//System.out.println("set fire");
				this.etc.dpf.setTargetPosition(new ChunkCoordinates((int)uke.posX,(int)uke.posY,(int)uke.posZ), 1.0d);//this be null, wtf
				
			}
		}

		this.etc.getLookHelper().setLookPositionWithEntity(this.uke, 30.0F, 30.0F);
		this.etc.dpf.adjustRotationToTarget(new ChunkCoordinates((int)uke.posX,(int)uke.posY,(int)uke.posZ));
		float f;

		if (--this.fireCD == 0)
		{
			if (d0 > (double)this.range || !flag)
			{
				//System.out.println("flagged");
				return;
			}

			f = MathHelper.sqrt_double(d0); // this.field_96562_i;
			float f1 = f;

			if (f < 0.1F)
			{
				f1 = 0.1F;
			}

			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			}

			this.etc.range(uke, f1);
			//System.out.println("ranged");
			this.fireCD = MathHelper.floor_float(f * (float)(this.fireRate));
		}
		else if (this.fireCD < 0)
		{
			//System.out.println("removing cd");
			f = MathHelper.sqrt_double(d0);
			this.fireCD = MathHelper.floor_float(f * (float)(this.fireRate));
		}
	}

}
