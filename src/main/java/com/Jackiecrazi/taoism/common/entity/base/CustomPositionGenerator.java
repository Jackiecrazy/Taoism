package com.Jackiecrazi.taoism.common.entity.base;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class CustomPositionGenerator
{
	private static Vec3 staticVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
	public static Vec3 findRandomTarget(EntityTaoisticCreature entity, int range, int height) {
        return findRandomTarget(entity, range, height, 0);
    }
    public static Vec3 findRandomTarget(EntityTaoisticCreature entity, int range, int height, int heightLevel) {
        return getTargetBlock(entity, range, height, (Vec3)null, heightLevel);
    }

    // ========== Find Random Waypoint to Target ==========
    public static Vec3 findRandomTargetTowards(EntityTaoisticCreature entity, int range, int height, Vec3 par3Vec3) {
    	staticVector.xCoord = par3Vec3.xCoord - entity.posX;
        staticVector.yCoord = par3Vec3.yCoord - entity.posY;
        staticVector.zCoord = par3Vec3.zCoord - entity.posZ;
        return findRandomTargetTowards(entity, range, height, staticVector, 0);
    }
    public static Vec3 findRandomTargetTowards(EntityTaoisticCreature entity, int range, int height, Vec3 par3Vec3, int heightLevel) {
        staticVector.xCoord = par3Vec3.xCoord - entity.posX;
        staticVector.yCoord = par3Vec3.yCoord - entity.posY;
        staticVector.zCoord = par3Vec3.zCoord - entity.posZ;
        return getTargetBlock(entity, range, height, staticVector, heightLevel);
    }

    // ========== Find Random Waypoint from Target ==========
    public static Vec3 findRandomTargetAwayFrom(EntityTaoisticCreature entity, int range, int height, Vec3 par3Vec3) {
    	staticVector.xCoord = entity.posX - par3Vec3.xCoord;
        staticVector.yCoord = entity.posY - par3Vec3.yCoord;
        staticVector.zCoord = entity.posZ - par3Vec3.zCoord;
        return findRandomTargetAwayFrom(entity, range, height, staticVector, 0);
    }
    public static Vec3 findRandomTargetAwayFrom(EntityTaoisticCreature entity, int range, int height, Vec3 par3Vec3, int heightLevel) {
        staticVector.xCoord = entity.posX - par3Vec3.xCoord;
        staticVector.yCoord = entity.posY - par3Vec3.yCoord;
        staticVector.zCoord = entity.posZ - par3Vec3.zCoord;
        return getTargetBlock(entity, range, height, staticVector, heightLevel);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vec3 getTargetBlock(EntityTaoisticCreature entity, int range, int height, Vec3 par3Vec3, int heightLevel) {
        Random random = entity.getRNG();
        boolean validTarget = false;
        int targetX = 0;
        int targetY = 0;
        int targetZ = 0;
        float pathMin = -99999.0F;
        boolean pastHome;

        if(entity.hasHome()) {
            double homeDist = (double)(entity.getHomePosition().getDistanceSquared(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ)) + 4.0F);
            double homeDistMax = (double)(entity.getHomeDistanceMax() + (float)range);
            pastHome = homeDist < homeDistMax * homeDistMax;
        }
        else
        	pastHome = false;

        for(int j1 = 0; j1 < 10; ++j1) {
            int possibleX = random.nextInt(2 * range) - range;
            int possibleY = random.nextInt(2 * height) - height;
            if(entity.useFlightNavigator() || (entity.canSwim() && (entity.isInWater() || entity.handleLavaMovement()))) {
	            if(entity.posY > entity.worldObj.getPrecipitationHeight((int)entity.posX, (int)entity.posZ) + heightLevel * 1.25)
	        		possibleY = random.nextInt(2 * height) - height * 3 / 2;
	            else if(entity.posY < entity.worldObj.getPrecipitationHeight( (int)entity.posX, (int)entity.posZ) + heightLevel)
	            	possibleY = random.nextInt(2 * height) - height / 2;
            }
            int possibleZ = random.nextInt(2 * range) - range;

            if(par3Vec3 == null|| (double)possibleX * par3Vec3.xCoord + (double)possibleZ * par3Vec3.zCoord >= 0.0D) {
            	possibleX += MathHelper.floor_double(entity.posX);
            	possibleY += MathHelper.floor_double(entity.posY);
            	possibleZ += MathHelper.floor_double(entity.posZ);

                if(!pastHome || entity.positionNearHome(possibleX, possibleY, possibleZ)) {
                    float pathWeight = entity.getBlockPathWeight(possibleX, possibleY, possibleZ);
                    
                    if(pathWeight > pathMin) {
                    	pathMin = pathWeight;
                    	targetX = possibleX;
                    	targetY = possibleY;
                    	targetZ = possibleZ;
                        validTarget = true;
                    }
                }
            }
        }

        if(validTarget)
            return Vec3.createVectorHelper((double)targetX, (double)targetY, (double)targetZ);
        else
        	return null;
    }
}
