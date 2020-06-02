package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.config.CombatConfig;
import javafx.util.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class TaoMovementUtils {
    private static Method jump = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "func_70664_aZ", Void.TYPE);

    public static boolean isTechnicallyGrounded(EntityLivingBase elb) {
        return elb.onGround || (TaoCasterData.getTaoCap(elb).getQi() > 3 && elb.collidedHorizontally && elb.isSprinting());
    }

    public static boolean isWallClinging(EntityLivingBase elb) {
        return elb.isSprinting() && elb.collidedHorizontally && !elb.collidedVertically;
    }

    /**
     * Checks the bottom, then north, south, east, west in that order
     * @param elb
     * @return
     */
    public static Tuple<EnumFacing, NeedyLittleThings.HitResult> collisionStatus(EntityLivingBase elb){
        elb.world.checkBlockCollision()
    }

    public static boolean attemptJump(EntityLivingBase elb) {
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        //if you're exhausted or just jumped, you can't jump again
        if (itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.EXHAUSTED || itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.JUMPING)
            return false;
        if (elb instanceof EntityPlayer)
            ((EntityPlayer) elb).jump();
        else try {
            jump.invoke(elb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isWallClinging(elb)){
            //redistribute x, y and z according to look
            Vec3d vec=elb.getLookVec();
            double speed=Math.sqrt(elb.motionX*elb.motionX+elb.motionY*elb.motionY+elb.motionZ*elb.motionZ);
            elb.motionX=vec.x*speed;
            elb.motionY=vec.y*speed;
            elb.motionZ=vec.z*speed;
        }
        return true;
    }

    public static boolean attemptDodge(EntityLivingBase elb, int side) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() > CombatConfig.rollCooldown && (elb.onGround || TaoCasterData.getTaoCap(elb).getQi() > 2) && !elb.isSneaking() && (!(elb instanceof EntityPlayer) || !((EntityPlayer) elb).capabilities.isFlying)) {
            //System.out.println("execute roll to side " + side);
            TaoCasterData.getTaoCap(elb).setRollCounter(0);
            TaoCasterData.getTaoCap(elb).setJumpState(ITaoStatCapability.JUMPSTATE.DODGING);
            TaoCasterData.getTaoCap(elb).setPrevSizes(elb.width, elb.height);
            float min = Math.min(elb.width, elb.height);
            double x = 0, y = 0.3, z = 0;
            switch (side) {
                case 0://left
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw));
                    break;
                case 1://back
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    break;
                case 2://right
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    break;
            }
            x /= 1.5;
            z /= 1.5;

            //NeedyLittleThings.setSize(elb, min, min);

            elb.addVelocity(x, y, z);
            elb.velocityChanged = true;
            return true;
        }
        return false;
    }
}
