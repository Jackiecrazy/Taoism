package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class EntityWhiplash extends EntityThrownWeapon {
    double dist;

    public EntityWhiplash(World worldIn) {
        super(worldIn);
    }

    public EntityWhiplash(World worldIn, EntityLivingBase dude, EnumHand main, double maxDist) {
        super(worldIn, dude, main);
        dist = maxDist;
        if (main != null)
            hand = main;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setDouble("maxRangeSq", dist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        dist = compound.getDouble("maxRangeSq");
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != stack.getItem() || this.getDistanceSq(getThrower()) > 72) {
                this.setDead();
                return;
            }
            if (this.getDistanceSq(getThrower()) > dist * dist) {
                setDead();
            } else if (this.getDistanceSq(getThrower()) > (dist - 1) * (dist - 1)) {
                //sonic boom!
                stack.setTagInfo("boomer", new NBTTagByte((byte)1));

                stack.setTagInfo("boomer", new NBTTagByte((byte)0));
                onRecall();
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return hitStatus > 0 ? 0F : 0.01f;
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        super.onHitBlock(rtr);
        charge = 0;
        inGround = false;
        sync();
    }

    @Override
    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0 || world.isRemote || getThrower() == null) return;
        if (hit == getThrower() && !getThrower().isSneaking()) return;
        super.onHitEntity(hit);
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        assert is.getTagCompound() != null;
        TaoCombatUtils.attackIndirectly(getThrower(), this, hit, hand);
    }

    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
    }
}
