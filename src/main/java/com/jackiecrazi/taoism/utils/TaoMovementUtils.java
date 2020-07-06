package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.List;

public class TaoMovementUtils {
    private static Method jump = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "func_70664_aZ", Void.TYPE);
    private static EnumFacing[] intToFacing = {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH};

    public static boolean shouldStick(EntityLivingBase elb) {
        return (TaoCasterData.getTaoCap(elb).getQi() > 3
                && willHitWall(elb))
                && elb.isSprinting();
    }

    /**
     * Checks the +x, -x, +y, -y, +z, -z, in that order
     *
     * @param elb
     * @return
     */
    public static boolean willHitWall(Entity elb) {
        double allowance = 1;
        AxisAlignedBB aabb = elb.getEntityBoundingBox();
        List<AxisAlignedBB> boxes = elb.world.getCollisionBoxes(elb, aabb.expand(elb.motionX, elb.motionY, elb.motionZ));
        for (AxisAlignedBB a : boxes) {
            if (aabb.calculateXOffset(a, allowance) != allowance) return true;
            if (aabb.calculateXOffset(a, -allowance) != -allowance) return true;
            if (aabb.calculateZOffset(a, allowance) != allowance) return true;
            if (aabb.calculateZOffset(a, -allowance) != -allowance) return true;
        }
        return false;
    }

    public static boolean[] collisionStatusVelocitySensitive(EntityLivingBase elb) {
        double allowance = 0.1;
        boolean[] ret = {false, false, false, false, false, false};
        AxisAlignedBB aabb = elb.getEntityBoundingBox();
        List<AxisAlignedBB> boxes = elb.world.getCollisionBoxes(elb, aabb.expand(elb.motionX, elb.motionY, elb.motionZ));
        for (AxisAlignedBB a : boxes) {
            if (aabb.calculateXOffset(a, allowance) != allowance) ret[0] = true;
            if (aabb.calculateXOffset(a, -allowance) != -allowance) ret[1] = true;
            if (aabb.calculateYOffset(a, allowance) != allowance) ret[2] = true;
            if (aabb.calculateYOffset(a, -allowance) != -allowance) ret[3] = true;
            if (aabb.calculateZOffset(a, allowance) != allowance) ret[4] = true;
            if (aabb.calculateZOffset(a, -allowance) != -allowance) ret[5] = true;
        }
        return ret;
    }

    public static boolean attemptSlide(EntityLivingBase elb) {
        if (!elb.onGround) return false;
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        //qi has to be nonzero
        if (itsc.getQi() == 0) return false;
        itsc.setRollCounter(0);
        itsc.setJumpState(ITaoStatCapability.JUMPSTATE.DODGING);
        Vec3d v=elb.getLookVec().subtract(0, elb.getLookVec().y, 0).normalize();
        elb.motionX = (1+itsc.getQi()/4) * v.x;
        elb.motionZ = (1+itsc.getQi()/4) * v.z;
        elb.velocityChanged = true;
        return true;
    }

    public static boolean attemptJump(EntityLivingBase elb) {
        //if you're on the ground, I'll let vanilla handle you
        if (elb.onGround) return false;
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        //qi has to be nonzero
        if (itsc.getQi() == 0) return false;
        //mario mario, wherefore art thou mario? Ignores all other jump condition checks
        Entity ent = collidingEntity(elb);
        if (ent instanceof EntityLivingBase) {
            EntityLivingBase uke = (EntityLivingBase) ent;
            uke.attackEntityFrom(DamageSource.FALLING_BLOCK, 1);
            TaoCasterData.getTaoCap(uke).consumePosture(7, true, elb);
            for (int i = 0; i < 10; ++i) {
                double d0 = Taoism.unirand.nextGaussian() * 0.02D;
                double d1 = Taoism.unirand.nextGaussian() * 0.02D;
                double d2 = Taoism.unirand.nextGaussian() * 0.02D;
                elb.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, uke.posX + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, uke.posY + 1.0D + (double) (Taoism.unirand.nextFloat() * uke.height), uke.posZ + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, d0, d1, d2);
            }
            elb.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.PLAYERS, 0.5f + Taoism.unirand.nextFloat() * 0.5f, 0.85f + Taoism.unirand.nextFloat() * 0.3f);
        } else {
            //if you're exhausted or just jumped, you can't jump again
            if ((itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.EXHAUSTED || itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.JUMPING))
                return false;
            if (itsc.getQi() > 3)
                itsc.setJumpState(ITaoStatCapability.JUMPSTATE.JUMPING);
            else itsc.setJumpState(ITaoStatCapability.JUMPSTATE.EXHAUSTED);
        }
        itsc.setClingDirections(new ITaoStatCapability.ClingData(false, false, false, false));
        if (elb instanceof EntityPlayer)
            ((EntityPlayer) elb).jump();
        else try {
            jump.invoke(elb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double speed = Math.sqrt(NeedyLittleThings.getSpeedSq(elb));
        if (isTouchingWall(elb)) {
            boolean[] dir = collisionStatus(elb);
            EnumFacing face = elb.getHorizontalFacing();
            boolean facingWall = false;
            switch (face) {
                case WEST:
                    facingWall = dir[0];
                    break;
                case EAST:
                    facingWall = dir[1];
                    break;
                case NORTH:
                    facingWall = dir[4];
                    break;
                case SOUTH:
                    facingWall = dir[5];
                    break;
            }
            //Vec3d look=elb.getLookVec();
            if (dir[0] && !facingWall) {//east
                elb.motionX += speed / 2;
            }
            if (dir[1] && !facingWall) {//west
                elb.motionX -= speed / 2;
            }
            if (dir[4] && !facingWall) {//south
                elb.motionZ += speed / 2;
            }
            if (dir[5] && !facingWall) {//north
                elb.motionZ -= speed / 2;
            }
            if (!facingWall) elb.motionY /= 2;
        }
        elb.velocityChanged = true;
        TaoCasterData.forceUpdateTrackingClients(elb);
        return true;
    }

    /**
     * Checks the +x, -x, +y, -y, +z, -z, in that order
     *
     * @param elb
     * @return
     */
    public static Entity collidingEntity(Entity elb) {
        AxisAlignedBB aabb = elb.getEntityBoundingBox();
        List<Entity> entities = elb.world.getEntitiesInAABBexcluding(elb, aabb.expand(elb.motionX * 3, elb.motionY * 3, elb.motionZ * 3), EntitySelectors.NOT_SPECTATING);
        double dist = 0;
        Entity pick = null;
        for (Entity e : entities) {
            if (e.getDistanceSq(elb) < dist || dist == 0) {
                pick = e;
                dist = e.getDistanceSq(elb);
            }
        }
        return pick;
    }

    public static boolean isTouchingWall(Entity elb) {
        boolean[] b = collisionStatus(elb);
        return !elb.onGround && !b[2] && !b[3] && ((b[0] || b[1]) || (b[4] || b[5]));
    }

    public static boolean[] collisionStatus(Entity elb) {
        double allowance = 0.1;
        boolean[] ret = {false, false, false, false, false, false};
        AxisAlignedBB aabb = elb.getEntityBoundingBox();
        List<AxisAlignedBB> boxes = elb.world.getCollisionBoxes(elb, aabb.grow(allowance / 2));
        for (AxisAlignedBB a : boxes) {
            if (aabb.calculateXOffset(a, allowance) != allowance) ret[0] = true;
            if (aabb.calculateXOffset(a, -allowance) != -allowance) ret[1] = true;
            if (aabb.calculateYOffset(a, allowance) != allowance) ret[2] = true;
            if (aabb.calculateYOffset(a, -allowance) != -allowance) ret[3] = true;
            if (aabb.calculateZOffset(a, allowance) != allowance) ret[4] = true;
            if (aabb.calculateZOffset(a, -allowance) != -allowance) ret[5] = true;
        }
        return ret;
    }

    public static boolean attemptDodge(EntityLivingBase elb, int side) {
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        if (itsc.getRollCounter() > CombatConfig.rollCooldown && itsc.getJumpState() != ITaoStatCapability.JUMPSTATE.DODGING && (elb.onGround || itsc.getQi() > 2) && (side != 3 || !elb.onGround) && !elb.isSneaking() && (!(elb instanceof EntityPlayer) || !((EntityPlayer) elb).capabilities.isFlying)) {//
            //System.out.println("execute roll to side " + side);
            itsc.setRollCounter(0);
            if (itsc.getQi() > 3)
                itsc.setJumpState(ITaoStatCapability.JUMPSTATE.DODGING);
            else itsc.setJumpState(ITaoStatCapability.JUMPSTATE.EXHAUSTED);
            double x = 0, y = 0.3, z = 0;
            switch (side) {
                case 0://left
                    x = MathHelper.cos(NeedyLittleThings.rad(elb.rotationYaw));
                    z = MathHelper.sin(NeedyLittleThings.rad(elb.rotationYaw));
                    break;
                case 1://back
                    x = MathHelper.cos(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    z = MathHelper.sin(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    break;
                case 2://right
                    x = MathHelper.cos(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    z = MathHelper.sin(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    break;
                case 3://forward
                    x = MathHelper.cos(NeedyLittleThings.rad(elb.rotationYaw + 90));
                    z = MathHelper.sin(NeedyLittleThings.rad(elb.rotationYaw + 90));
                    break;
            }
            float divisor = side == 3 ? 10f : 20f;
            float multiplier = (1 + (itsc.getQi() / divisor));
            x *= 0.6 * multiplier;
            z *= 0.6 * multiplier;

            //NeedyLittleThings.setSize(elb, min, min);

            elb.addVelocity(x, y, z);
            itsc.setJumpState(ITaoStatCapability.JUMPSTATE.DODGING);
            elb.velocityChanged = true;
            TaoCasterData.forceUpdateTrackingClients(elb);
            return true;
        }
        return false;
    }
}
