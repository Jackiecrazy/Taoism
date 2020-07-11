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
            Entity e = getTetheringEntity();
            Entity target = getTetheredEntity();
            if (e != null) {
                double distsq = 0;
                Vec3d point = null;
                if (offset != null) {
                    distsq = NeedyLittleThings.getDistSqCompensated(e, offset);
                    point = offset;
                    if (target != null) {
                        distsq = e.getDistanceSq(target);
                        point = target.getPositionVector().add(offset);
                    }
                }
                //update the entity's relative position to the point
                //if the distance is below tether length, do nothing
                //if the distance is above tether length, apply centripetal force to the point
                if (getTetherLength() * getTetherLength() < distsq && point != null) {
                    e.motionX += (point.x - e.posX) * 0.02;
                    e.motionY += (point.y - e.posY) * 0.02;
                    e.motionZ += (point.z - e.posZ) * 0.02;
                }
                else if (getTetherLength() == 0 && target != null) {//special case to help with catching up to entities
                    e.motionX = target.motionX;
                    e.motionZ = target.motionZ;
                    if(!target.onGround) e.motionY = target.motionY;
                    else e.motionY=0;
                }//else e.motionZ=e.motionX=e.motionY=0;
                e.velocityChanged = true;
            }
        }
    }

    Entity getTetheringEntity();

    @Nullable
    Vec3d getTetheredOffset();

    @Nullable
    Entity getTetheredEntity();

    double getTetherLength();
}
