package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public interface ITetherAnchor {
    /**
     * updates the tether wielder's velocity
     */
    default void updateTetheringVelocity() {
        if (getTetheringEntity() != null) {
            Vec3d offset = getTetheredOffset();
            Entity toBeMoved = getTetheringEntity();
            Entity moveTowards = getTetheredEntity();
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
                }
                //update the entity's relative position to the point
                //if the distance is below tether length, do nothing
                //if the distance is above tether length, apply centripetal force to the point
                if (getTetherLength() * getTetherLength() < distsq && point != null) {
                    toBeMoved.motionX += (point.x - toBeMoved.posX) * 0.05;
                    toBeMoved.motionY += (point.y - toBeMoved.posY) * 0.05;
                    toBeMoved.motionZ += (point.z - toBeMoved.posZ) * 0.05;
                }
                if (shouldRepel() && getTetherLength() * getTetherLength() < distsq && point != null) {
                    toBeMoved.motionX -= (point.x - toBeMoved.posX) * 0.05;
                    toBeMoved.motionY -= (point.y - toBeMoved.posY) * 0.05;
                    toBeMoved.motionZ -= (point.z - toBeMoved.posZ) * 0.05;
                }
                if (getTetherLength() == 0 && moveTowards != null) {//special case to help with catching up to entities
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

    Entity getTetheringEntity();

    @Nullable
    Vec3d getTetheredOffset();

    @Nullable
    Entity getTetheredEntity();

    double getTetherLength();

    default boolean shouldRepel() {
        return false;
    }
}
