package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class EntityThrownWeapon extends EntityTaoProjectile {

    /**
     * -1 for fresh release, 0 for flying, 1 for block, 2 for entity
     */
    protected byte hitStatus = -1;
    EnumHand hand = EnumHand.MAIN_HAND;
    ItemStack stack;

    public EntityThrownWeapon(World worldIn) {
        super(worldIn);
    }

    public EntityThrownWeapon(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude);
        if (main != null)
            hand = main;
        stack = dude.getHeldItem(hand);
        stack.getTagCompound().setBoolean("thrown", true);
        this.rotationPitch = dude.rotationPitch;
        this.rotationYaw = dude.rotationYaw;
        setPosition(dude.posX, dude.posY + (double)dude.getEyeHeight() - height/2, dude.posZ);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
        hitStatus = id;
        if (hitStatus == 1) {
            inGround = true;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("status", hitStatus);
        compound.setBoolean("off", hand == EnumHand.OFF_HAND);
        if (stack != null)
            compound.setString("weapName", stack.getItem().getRegistryName().toString());

        //Stack is not saved or read. This is intentional.
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        hitStatus = compound.getByte("status");
        hand = compound.getBoolean("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        if (shootingEntity instanceof EntityLivingBase && ((EntityLivingBase) shootingEntity).getHeldItem(hand).getItem().getRegistryName().toString().equals(compound.getString("weapName"))) {
            stack = ((EntityLivingBase) shootingEntity).getHeldItem(hand);
        }
        //Stack is not saved or read. This is intentional.
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (firstUpdate) {
                sync();
            }
            if (!(this.shootingEntity instanceof EntityLivingBase) || ((EntityLivingBase) shootingEntity).getHeldItem(hand).getItem() != stack.getItem()) {
                onRetrieveWeapon();
                return;
            }
            if (shootingEntity == null || (shootingEntity.getDistanceSq(this) > 4 && hitStatus == -1)) {
                updateHitStatus(0);
            }
        }
        super.onUpdate();
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        super.onCollideWithPlayer(player);
        if (shootingEntity == player && shouldRetrieve()) {
            onRetrieveWeapon();
        }
    }

    @Override
    protected float velocityMultiplier() {
        return 1f;
    }

    @Override
    protected float getGravityVelocity() {
        return hitStatus > 0 ? 0F : super.getGravityVelocity();
    }

    protected void onHitBlock(RayTraceResult rtr) {
        super.onHitBlock(rtr);
        if (hitStatus <= 0) {
            updateHitStatus(1);
        }
    }

    protected void onHitEntity(Entity hit) {
        super.onHitEntity(hit);
        if (hitStatus <= 0)
            updateHitStatus(2);
    }

    protected void onRetrieveWeapon() {
        if (stack != null)
            stack.getTagCompound().setBoolean("thrown", false);
        this.setDead();
        this.velocityChanged = true;
        sync();
    }

    protected void updateHitStatus(int status) {
        hitStatus = (byte) status;
        world.setEntityState(this, (byte) status);
    }

    protected void onRecall() {
        Vec3d vec = getHomeLocation();
        if (!(this.shootingEntity instanceof EntityLivingBase) || ((EntityLivingBase) shootingEntity).getHeldItem(hand).getItem() != stack.getItem() || vec == null) {
            onRetrieveWeapon();
            return;
        }
        inGround = false;
        shoot(vec.x - posX, vec.y - posY, vec.z - posZ, getReturnVelocity(), 0);
        velocityChanged = true;
        sync();
    }

    @Nullable
    protected Vec3d getHomeLocation() {
        if (getThrower() == null) return null;
        return getThrower().getPositionVector().addVector(0, shootingEntity.getEyeHeight() -height/2, 0);
    }

    protected float getReturnVelocity() {
        return 0.8f;
    }

    protected boolean shouldRetrieve() {
        return hitStatus > 0;
    }

    public float xSpin() {
        return 0;
    }

    public float ySpin() {
        return 0;
    }

    public float zSpin() {
        return 0;
    }

}
