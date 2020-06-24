package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class EntityThrownWeaponArchived extends EntityThrowable {
    /**
     * -1 for fresh release, 0 for flying, 1 for block, 2 for entity
     */
    protected byte hitStatus = -1;
    EnumHand hand = EnumHand.MAIN_HAND;
    ItemStack stack;

    public EntityThrownWeaponArchived(World worldIn) {
        super(worldIn);
    }

    public EntityThrownWeaponArchived(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude);
        if (main != null)
            hand = main;
        stack = dude.getHeldItem(hand);
        stack.getTagCompound().setBoolean("thrown", true);
        this.rotationPitch = dude.rotationPitch;
        this.rotationYaw = dude.rotationYaw;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != stack.getItem()) {
                onRetrieveWeapon();
                return;
            }
            if (getThrower() == null || (getThrower().getDistanceSq(this) > 4 && hitStatus == -1)) {
                updateHitStatus(0);
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return hitStatus > 0 ? 0F : super.getGravityVelocity();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (hitStatus == -1 || world.isRemote) return;
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            onHitBlock(result.getBlockPos(), result.sideHit);
        }
        if (getThrower() != null) {
            if (result.entityHit != null && result.entityHit.isEntityAlive()) {
                if (result.entityHit != getThrower()) {
                    Entity e = result.entityHit;
                    onHitEntity(e);
                } else {
                    this.velocityChanged = true;
                    onRetrieveWeapon();
                }
            }
        } else {
            //hmm
            setDead();
        }
    }

    protected void onHitBlock(BlockPos bp, EnumFacing es) {
        if (hitStatus <= 0) {
            updateHitStatus(1);
            inGround=true;
            motionX=motionY=motionZ=0;
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
        hitStatus=id;
        if(hitStatus==1){
            inGround=true;
            motionX=motionY=motionZ=0;
        }
    }

    protected void onHitEntity(Entity hit) {
        if (hitStatus <= 0)
            updateHitStatus(2);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("status", hitStatus);
        compound.setBoolean("off", hand == EnumHand.OFF_HAND);
        if(stack!=null)
        compound.setString("weapName", stack.getItem().getRegistryName().toString());
        //Stack is not saved or read. This is intentional.
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        hitStatus = compound.getByte("status");
        hand = compound.getBoolean("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        if (thrower != null && thrower.getHeldItem(hand).getItem().getRegistryName().toString().equals(compound.getString("weapName"))) {
            stack = thrower.getHeldItem(hand);
        }
        //Stack is not saved or read. This is intentional.
    }

    protected void onRetrieveWeapon() {
        if (stack != null)
            stack.getTagCompound().setBoolean("thrown", false);
        this.setDead();
        this.velocityChanged = true;
    }

    protected void onRecall() {
        if (getThrower() == null || getThrower().getHeldItem(hand).getItem() != stack.getItem()) {
            onRetrieveWeapon();
            return;
        }
        shoot(getThrower().posX - posX, getThrower().posY + getThrower().getEyeHeight() / 2 - posY, getThrower().posZ - posZ, 0.8f, 0);
        velocityChanged = true;
    }

    public float xSpin(){
        return 0;
    }
    public float ySpin(){
        return 0;
    }
    public float zSpin(){
        return 0;
    }

    protected void updateHitStatus(int status){
        hitStatus= (byte) status;
        world.setEntityState(this, (byte) status);
    }

}
