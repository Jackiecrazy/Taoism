package com.Jackiecrazi.taoism.common.entity.base;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

/**
 * my attempts at being an awesome modder who can do path calculations. 
 * Lycanite and Pinkalulan, you have my respect.
 * @author Lycanite, basically
 * 
 */
public class DecentPathFinder {
	public ChunkCoordinates targetPosition;
	/**
	 * the entity in question
	 */
	private EntityTaoisticCreature etc;
	public double maxFlyHeight=10;
	public double maxFlyRange=10;
	public double flySpd=1.0;
	public boolean faceFly=true;
	public double spdMod=1.0;
	public DecentPathFinder(EntityTaoisticCreature e) {
		etc=e;
	}
	//====================================
	// set stuff
	//====================================
	public DecentPathFinder setSpeed(double setSpeed) {
		this.flySpd = setSpeed;
		return this;
	}
	public DecentPathFinder setMaxHeight(double maxH) {
		this.maxFlyHeight = maxH;
		return this;
	}
	public DecentPathFinder setFacing(boolean facing) {
		this.faceFly = facing;
		return this;
	}
	public boolean setTargetPosition(Entity target,double speed){
		return this.setTargetPosition(new ChunkCoordinates((int)target.posX,(int)target.posY,(int)target.posZ), speed);
	}
	/**
	 * sets target. Nuff said.
	 * @param targetPosition can be null
	 * @param setSpeedMod
	 * @return whether the set was successful
	 */
	public boolean setTargetPosition(ChunkCoordinates targetPosition, double setSpeedMod) {
		//System.out.println(targetPosition);
		if(isTargetPositionValid(targetPosition)) {
			this.targetPosition = targetPosition;
			this.spdMod = setSpeedMod;
			return true;
		}
		return false;
	}
	public boolean clearTargetPosition(double setSpeedMod) {
		return this.setTargetPosition((ChunkCoordinates)null, setSpeedMod);
	}
	/**
	 * Checks if the destination can actually accomodate the entity
	 * @param targetPosition
	 * @return
	 */
	public boolean isTargetPositionValid(ChunkCoordinates targetPosition) {
		//System.out.println(targetPosition);
		if(targetPosition == null){
			//System.out.println("targpos is null, assume true");
			return true;
		}
		if(this.etc.canSwim() && this.etc.isSwimmable(targetPosition.posX, targetPosition.posY, targetPosition.posZ)){
			//System.out.println("I can swim to "+ targetPosition.posX+"   "+ targetPosition.posY+"    "+ targetPosition.posZ);
			return true;
		}
		if(!this.etc.canFly()){
			System.out.println("I can't fly to "+ targetPosition.posX+"   "+ targetPosition.posY+"    "+ targetPosition.posZ);
			return false;
		}
		if(!this.etc.worldObj.isAirBlock(targetPosition.posX, targetPosition.posY, targetPosition.posZ) && !this.etc.noClip){
			System.out.println("I ain't a ghost");
			return false;
		}
		if(targetPosition.posY < 3){
			System.out.println("I won't kill myself");
			return false;
		}
		return true;
	}
	
	public double distanceToTargetPosition(){
        return this.etc.getDistance(targetPosition.posX, targetPosition.posY, targetPosition.posZ);
    }
	
	public void updateFlight() {
		if(this.targetPosition == null){
			//System.out.println("targpos is null, leaving");
			return;
		}
       //System.out.println("gonna flyswim");
        double[] coords = this.etc.getFacingPosition(this.targetPosition.posX + 0.5D, this.targetPosition.posY, this.targetPosition.posZ + 0.5D, 1.0D, 0);
        //double dirX = (double)this.targetPosition.posX + 0.5D - this.etc.posX;
        double dirX = coords[0] - this.etc.posX;
        double dirY = (double)this.targetPosition.posY + 0.1D - this.etc.posY;
        //double dirZ = (double)this.targetPosition.posZ + 0.5D - this.etc.posZ;
        double dirZ = coords[2] - this.etc.posZ;

        double speed = this.etc.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() * 2;
		this.etc.motionX += ((Math.signum(dirX) * speed - this.etc.motionX) * 0.10000000149011612D*0.3D) * spdMod;
		this.etc.motionY += ((Math.signum(dirY) * speed - this.etc.motionY) * 0.10000000149011612D*0.3D) * spdMod;
		this.etc.motionZ += ((Math.signum(dirZ) * speed - this.etc.motionZ) * 0.10000000149011612D*0.3D) * spdMod;
		float fullAngle = (float)(Math.atan2(this.etc.motionZ, this.etc.motionX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.etc.rotationYaw);
		this.etc.moveForward = 0.5F;
		if(this.faceFly /*&& (this.etc.getAttackTarget() != null)*/ && (this.etc.motionX > 0.025F || this.etc.motionZ > 0.025F))
			this.etc.rotationYaw += angle;
	}
	
	public void flightMovement(float moveStrafe, float moveForward) {
		if(this.etc.isInWater() && !etc.canSwim()) {
            this.etc.moveFlying(moveStrafe, moveForward, 0.02F);
            this.etc.moveEntity(this.etc.motionX, this.etc.motionY, this.etc.motionZ);
            this.etc.motionX *= 0.800000011920929D;
            this.etc.motionY *= 0.800000011920929D;
            this.etc.motionZ *= 0.800000011920929D;
        }
        else if(this.etc.handleLavaMovement() && !etc.canSwim()) {
            this.etc.moveFlying(moveStrafe, moveForward, 0.02F);
            this.etc.moveEntity(this.etc.motionX, this.etc.motionY, this.etc.motionZ);
            this.etc.motionX *= 0.5D;
            this.etc.motionY *= 0.5D;
            this.etc.motionZ *= 0.5D;
        }
        else {
        	float motion = 0.91F;
            if(this.etc.onGround) {
            	motion = 0.54600006F;
                Block block = this.etc.worldObj.getBlock(MathHelper.floor_double(this.etc.posX), MathHelper.floor_double(this.etc.boundingBox.minY) - 1, MathHelper.floor_double(this.etc.posZ));
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            float flyingMotion = 0.16277136F / (motion * motion * motion);
            this.etc.moveFlying(moveStrafe, moveForward, this.etc.onGround ? 0.1F * flyingMotion : (float)(0.02F * this.flySpd));
            
            motion = 0.91F;
            if(this.etc.onGround) {
            	motion = 0.54600006F;
                Block block = this.etc.worldObj.getBlock(MathHelper.floor_double(this.etc.posX), MathHelper.floor_double(this.etc.boundingBox.minY) - 1, MathHelper.floor_double(this.etc.posZ));
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            
            if(this.etc != null && this.etc.boundingBox != null)
            	this.etc.moveEntity(this.etc.motionX, this.etc.motionY, this.etc.motionZ);
            this.etc.motionX *= (double)motion;
            this.etc.motionY *= (double)motion;
            this.etc.motionZ *= (double)motion;
        }
		
        this.etc.prevLimbSwingAmount = this.etc.limbSwingAmount;
        double deltaX = this.etc.posX - this.etc.prevPosX;
        double deltaZ = this.etc.posZ - this.etc.prevPosZ;
        float var7 = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 4.0F;
        if(var7 > 1.0F) var7 = 1.0F;
        this.etc.limbSwingAmount += (var7 - this.etc.limbSwingAmount) * 0.4F;
        this.etc.limbSwing += this.etc.limbSwingAmount;
	}
	
	
	
	protected void adjustRotationToWaypoint() {
		double distX = targetPosition.posX - this.etc.posX;
		double distZ = targetPosition.posZ - this.etc.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI);// - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.etc.rotationYaw);
		if(angle > 30.0F) angle = 30.0F;
		if(angle < -30.0F) angle = -30.0F;
		this.etc.renderYawOffset = this.etc.rotationYaw += angle;
	}
	
	// ========== Rotate to Target ==========
    public void adjustRotationToTarget(ChunkCoordinates target) {
		double distX = target.posX - this.etc.posX;
		double distZ = target.posZ - this.etc.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.etc.rotationYaw);
		this.etc.rotationYaw += angle; 
    }
    public boolean atTargetPosition(){
		if(targetPosition != null)
			return distanceToTargetPosition() < (this.etc.width / 2);
		return true;
	}
    public boolean isTargetPositionValid() {
		return isTargetPositionValid(this.targetPosition);
	}
	public boolean setTargetPosition(int xCoord, int yCoord, int zCoord,
			double farSpeed) {
		return this.setTargetPosition(new ChunkCoordinates(xCoord,yCoord,zCoord), farSpeed);
	}
	public boolean setTargetPosition(double xCoord, double yCoord, double zCoord,
			double farSpeed) {
		return this.setTargetPosition(new ChunkCoordinates((int)xCoord,(int)yCoord,(int)zCoord), farSpeed);
	}
}
