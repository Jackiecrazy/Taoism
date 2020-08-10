package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public interface ITetherItem {
    /**
     * updates the tether wielder's velocity
     */
    default void updateTetheringVelocity(ItemStack stack, EntityLivingBase wielder) {
        if (getTetheringEntity(stack, wielder) != null) {
            Vec3d offset = getTetheredOffset(stack, wielder);
            Entity toBeMoved = getTetheringEntity(stack, wielder);
            Entity moveTowards = getTetheredEntity(stack, wielder);
            if (toBeMoved != null) {
                double distsq = 0;
                Vec3d point = null;
                if (offset != null) {
                    distsq = NeedyLittleThings.getDistSqCompensated(toBeMoved, offset);
                    point = offset;
                    if (moveTowards != null) {
                        distsq = toBeMoved.getDistanceSq(moveTowards);
                        point = moveTowards.getPositionVector().add(offset);
                    }
                }else if (moveTowards != null) {
                    distsq = toBeMoved.getDistanceSq(moveTowards);
                    point = moveTowards.getPositionVector();
                }
                //update the entity's relative position to the point
                //if the distance is below tether length, do nothing
                //if the distance is above tether length, apply centripetal force to the point
                if (getTetherLength(stack) * getTetherLength(stack) < distsq && point != null) {
                    toBeMoved.motionX += (point.x - toBeMoved.posX) * 0.01;
                    toBeMoved.motionY += (point.y - toBeMoved.posY) * 0.01;
                    toBeMoved.motionZ += (point.z - toBeMoved.posZ) * 0.01;
                }
                if (shouldRepel(stack) && getTetherLength(stack) * getTetherLength(stack) > distsq*2 && point != null) {
                    toBeMoved.motionX -= (point.x - toBeMoved.posX) * 0.01;
                    toBeMoved.motionY -= (point.y - toBeMoved.posY) * 0.01;
                    toBeMoved.motionZ -= (point.z - toBeMoved.posZ) * 0.01;
                }
                if (getTetherLength(stack) == 0 && moveTowards != null) {//special case to help with catching up to entities
                    //System.out.println(target.getDistanceSq(e));
                    //if(NeedyLittleThings.getDistSqCompensated(moveTowards, toBeMoved)>8){
                    toBeMoved.posX = moveTowards.posX;
                    toBeMoved.posY = moveTowards.posY;
                    toBeMoved.posZ = moveTowards.posZ;
                    //}
                    toBeMoved.motionX = moveTowards.motionX;
                    toBeMoved.motionZ = moveTowards.motionZ;
                    if (!moveTowards.onGround) toBeMoved.motionY = moveTowards.motionY;
                    else toBeMoved.motionY = 0;
                }//else e.motionZ=e.motionX=e.motionY=0;
                toBeMoved.velocityChanged = true;
            }
        }
    }

    Entity getTetheringEntity(ItemStack stack, EntityLivingBase wielder);

    @Nullable
    Vec3d getTetheredOffset(ItemStack stack, EntityLivingBase wielder);

    @Nullable
    Entity getTetheredEntity(ItemStack stack, EntityLivingBase wielder);

    double getTetherLength(ItemStack stack);

    default boolean shouldRepel(ItemStack stack) {
        return false;
    }

    default boolean renderTether(ItemStack stack) {
        return false;
    }
}
