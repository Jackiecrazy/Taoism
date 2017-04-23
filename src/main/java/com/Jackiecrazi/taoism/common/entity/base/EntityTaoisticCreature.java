package com.Jackiecrazi.taoism.common.entity.base;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.common.taoistichandlers.ExtraMobStuff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTaoisticCreature extends EntityLiving {
	/** A location used for mobs that stick around a certain home spot. **/
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
    /** How far this mob can move from their home spot. **/
    private float homeDistanceMax = -1.0F;
    /**
     * tells the AI to give up after this time.
     */
    public final int AICD=100;
    /**
     * hide counter
     */
    private int hideCounter=0;
    
    public int getHideCounter() {
		return hideCounter;
	}
	public void setHideCounter(int hideCounter) {
		this.hideCounter = hideCounter;
	}
	public int getHidingCounter() {
		return hidingCounter;
	}
	public void setHidingCounter(int hidingCounter) {
		this.hidingCounter = hidingCounter;
	}
	
	/**
     * hiding frame counter
     */
    private int hidingCounter=0;
    private Entity avoid;
	public ExtraMobStuff ems;
	public ArrayList<Material> preferredLiquid=new ArrayList<Material>();
	public DecentPathFinder dpf;
	public EntityTaoisticCreature(World w) {
		super(w);
		ems=new ExtraMobStuff(this);
		dpf=new DecentPathFinder(this);
		populate();
	}
	public EntityTaoisticCreature(World w,Material... liquid) {
		super(w);
		ems=new ExtraMobStuff(this);
		dpf=new DecentPathFinder(this);
	}
	/**
	 * preferred liquids go here
	 */
	protected void populate(){
		
	}
	public void readEntityFromNBT(NBTTagCompound c)
    {
		super.readEntityFromNBT(c);
		if(c.hasKey("HomeX") && c.hasKey("HomeY") && c.hasKey("HomeZ") && c.hasKey("HomeDistanceMax")) {
        	this.setHome(c.getInteger("HomeX"), c.getInteger("HomeY"), c.getInteger("HomeZ"), c.getFloat("HomeDistanceMax"));
        }
    }
	public void writeEntityToNBT(NBTTagCompound c)
    {
		super.writeEntityToNBT(c);
		if(this.hasHome()) {
    		ChunkCoordinates homePos = this.getHomePosition();
    		c.setInteger("HomeX", homePos.posX);
    		c.setInteger("HomeY", homePos.posY);
    		c.setInteger("HomeZ", homePos.posZ);
    		c.setFloat("HomeDistanceMax", this.getHomeDistanceMax());
    	}
    }
	public boolean isSwimmable(int x, int y, int z) {
		Block block = this.worldObj.getBlock(x, y, z);
		if(block == null||preferredLiquid==null){
			System.out.println("preference is null, assume false");
			return false;
		}
		if(preferredLiquid.contains(block.getMaterial())){
			//System.out.println("is swimmable");
			return true;
		}
		return false;
	}
	public double[] getFacingPosition(double x, double y, double z, double distance, double angle) {
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
    	double[] coords = new double[3];
        coords[0] = x + (distance * xAmount);
        coords[1] = y;
        coords[2] = z + (distance * zAmount);
        return coords;
    }
	public double[] getFacingPosition(Entity entity, double distance, double angleOffset) {
        return this.getFacingPosition(entity.posX, entity.posY, entity.posZ, distance, Math.toRadians(entity.rotationYaw) + angleOffset);
    }
	public boolean canSwim(){
		return this.ems.swim;
	}
	public boolean canFly(){
		return this.ems.fly;
	}
	public boolean useFlightNavigator() {
    	boolean freeSwimming = this.canSwim() && this.isInWater();
    	boolean noClipping=false;
    	for(Material liq:preferredLiquid){
    		if(this.isInsideOfMaterial(liq)){
    			noClipping=true;
    			break;
    		}
    	}
    	if(this.canFly() || freeSwimming || (noClipping&&this.noClip)){
    		System.out.println("trigger");
    		return true;
    		
    	}
    	return false;
    }
	@Override
    protected void updateAITasks() {
		//System.out.println("updating ai");
		if(this.useFlightNavigator()){
			dpf.updateFlight();
			//System.out.println("can't talk flying");
		}
		//else System.out.println("can't talk unflying");
		super.updateAITasks();
	}
	@Override
	protected boolean isAIEnabled()
	{
	   return true;
	}
	@Override
    public void moveEntityWithHeading(float moveStrafe, float moveForward) {
    	if(!this.useFlightNavigator()) super.moveEntityWithHeading(moveStrafe, moveForward);
    	else this.dpf.flightMovement(moveStrafe, moveForward);
    }
	/**
	 * override for reactions when wandering
	 * @param wanderPosition
	 * @return
	 */
	public ChunkCoordinates getWanderPosition(ChunkCoordinates wanderPosition) {
        return wanderPosition;
    }
	public boolean hasHome() {
    	return this.getHomePosition() != null && this.getHomeDistanceMax() >= 0;
    }
	public void setHome(int x, int y, int z, float distance) {
    	this.setHomePosition(x, y, z);
    	this.setHomeDistanceMax(distance);
    }
    /** Sets the home position for this entity to stay around. **/
    public void setHomePosition(int x, int y, int z) {
    	this.homePosition = new ChunkCoordinates(x, y, z);
    }
	/** Sets the distance this mob is allowed to stray from it's home. -1 will turn off the home restriction. **/
    public void setHomeDistanceMax(float newDist) { this.homeDistanceMax = newDist; }
    /** Returns the home position in ChunkCoordinates. **/
    public ChunkCoordinates getHomePosition() { return this.homePosition; }
    /** Gets the distance this mob is allowed to stray from it's home. -1 is used to unlimited distance. **/
    public float getHomeDistanceMax() { return this.homeDistanceMax; }
    /** Clears the current home position. **/
    public void detachHome() {
    	this.setHomeDistanceMax(-1);
    }
    public boolean positionNearHome(int x, int y, int z) {
        if(!hasHome()) return true;
        return this.getDistanceFromHome(x, y, z) < this.getHomeDistanceMax() * this.getHomeDistanceMax();
    }
    public float getDistanceFromHome(int x, int y, int z) {
    	if(!hasHome()) return 0;
    	return this.homePosition.getDistanceSquared(x, y, z);
    }
    /** Returns the distance that the entity's position is from the home position. **/
    public float getDistanceFromHome() {
    	return this.homePosition.getDistanceSquared((int)this.posX, (int)this.posY, (int)this.posZ);
    }
    /**
     * anything to do with this?
     * @param par1 x
     * @param par2 y
     * @param par3 z
     * @return 0 for now
     */
    public float getBlockPathWeight(int par1, int par2, int par3) {
        /*if(this.mobInfo.spawnInfo.spawnsInDark && !this.mobInfo.spawnInfo.spawnsInLight)
        	return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
        if(this.mobInfo.spawnInfo.spawnsInLight && !this.mobInfo.spawnInfo.spawnsInDark)
        	return this.worldObj.getLightBrightness(par1, par2, par3) - 0.5F;*/
    	return 0.0F;
    }
    /** 
     * Called when the mob has hit the ground after falling, fallDistance is how far it fell and can be translated into fall damage.
     * getFallResistance() is used to reduce falling damage, if it is at or above 100 no falling damage is taken at all.
     * **/
    @Override
    protected void fall(float fallDistance) {
    	if(this.useFlightNavigator())
    		return;
    	fallDistance -= this.getFallResistance();
    	if(this.getFallResistance() >= 100)
    		fallDistance = 0;
    	super.fall(fallDistance);
    }
    
    protected float getFallResistance() {
		return 0;
	}
	/** Called when this mob is falling, fallDistance is how far the mob has fell so far and onGround is true when it has hit the ground. **/
    @Override
    protected void updateFallState(double fallDistance, boolean onGround) {
    	if(!this.useFlightNavigator()) super.updateFallState(fallDistance, onGround);
    }
    public void clearMovement(){
    	this.dpf.clearTargetPosition(1d);
    	this.getNavigator().clearPathEntity();
    }
    public boolean canBreatheAtLocation(int x, int y, int z) {
    	Block block = this.worldObj.getBlock(x, y, z);
    	if(block == null)
    		return true;
    	if(this.preferredLiquid.contains(block))return true;
    	return false;
    }
    /**
     * Override for range attack
     */
    public void range(EntityLivingBase elb,float dam){
    	
    }
    /**
     * Override for melee.
     */
    public void melee(EntityLivingBase elb, float dam){
    	elb.attackEntityFrom(DamageSource.causeMobDamage(this), dam);
    }
	public Entity getAvoidTarget() {
		return avoid;
	}
	public void setAvoidTarget(Entity avoid) {
		this.avoid = avoid;
	}
	public void onUpdate()
    {
		super.onUpdate();
		if (this.motionY < 0.0D)//!this.onGround && 
        {
            this.motionY *= ems.fallBuff;
        }
		if(hidingCounter>0)
		hidingCounter--;
		if(this.worldObj.difficultySetting==EnumDifficulty.PEACEFUL&&this.getEntityAttribute(SharedMonsterAttributes.attackDamage)!=null)this.setDead();
    }
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increment)
    {
		super.setPositionAndRotation2(x, y, z, yaw, pitch, increment);
    }
}
