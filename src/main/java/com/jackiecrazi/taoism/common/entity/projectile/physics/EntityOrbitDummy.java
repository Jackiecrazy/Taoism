package com.jackiecrazi.taoism.common.entity.projectile.physics;

import com.jackiecrazi.taoism.capability.TaoCasterData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityOrbitDummy extends EntityPhysicsDummy {
    private double dist;

    public EntityOrbitDummy(World worldIn) {
        super(worldIn);
    }

    public EntityOrbitDummy(World worldIn, EntityLivingBase dude, Entity attachTo) {
        super(worldIn, dude, attachTo);
        setPosition(attachTo.posX, attachTo.posY, attachTo.posZ);
        dist = dude.getDistanceSq(attachTo);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05f;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setDouble("dist", dist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        dist = compound.getDouble("dist");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (target != null && !target.isDead && getThrower() != null && (!(target instanceof EntityLivingBase) || TaoCasterData.getTaoCap((EntityLivingBase) target).getRootTime() == 0)) {
//            if (getThrower() != null && getPosition().getY() == getThrower().getPosition().getY()) {
//                flip = true;
//            }
            //if (!flip) motionY = 0.1;
            if (getThrower().getDistanceSq(target) > dist && !(prevPosY > getThrower().posY && posY < getThrower().posY)) {
                motionX += (getThrower().posX - target.posX) * 0.05;
                motionY += (getThrower().posY - target.posY) * 0.05;
                motionZ += (getThrower().posZ - target.posZ) * 0.05;
            }
        } else setDead();
    }

    @Override
    protected void onHitBlock(RayTraceResult raytraceResultIn) {
        if (ticksExisted < 2 || raytraceResultIn.sideHit != EnumFacing.UP) return;
        if (target instanceof EntityLivingBase && getThrower() != null) {
            TaoCasterData.getTaoCap((EntityLivingBase) target).stopRecordingDamage(getThrower());
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).setRootTime(60);
        }
        setDead();
    }
}
