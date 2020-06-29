package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityAxeCleave extends EntitySwordBeam {
    //first pass: crack particles
    //second pass: explosion particles
    List<Entity> hitList = new ArrayList<>();

    public EntityAxeCleave(World w) {
        super(w);
        setSize(3, 1);
    }

    public EntityAxeCleave(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn, hand, is);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getThrower() != null && !world.isRemote)
            if (this.getDistanceSq(getThrower()) > 324 || ticksExisted > 60 || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                onRetrieveWeapon();
                return;
            }
    }

    @Override
    protected float getGravityVelocity() {
        return onGround ? 0 : 0.05f;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!world.isRemote) {
            if (getThrower() != null) {
                if (this.getDistanceSq(getThrower()) > 324 || ticksExisted > 60 || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                    onRetrieveWeapon();
                    return;
                }
                if (result.entityHit != null) {
                    if (result.entityHit != getThrower()) {
                        hitList.add(result.entityHit);
                    }
                }
            }
        }
    }

    /**
     * necessary because it's riding on the ground rather than sticking to it
     */
    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        motionY = 0;
    }

    @Override
    protected void onHitEntity(Entity hit) {
        super.onHitEntity(hit);
        if (getThrower() != null) {
            if (this.getDistanceSq(getThrower()) > 324 || ticksExisted > 60 || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                onRetrieveWeapon();
                return;
            }
            if (hit != getThrower())
                hitList.add(hit);
        }
    }

    @Override
    protected void onRetrieveWeapon() {
        ArrayList<Entity> hittedList = new ArrayList<>();
        for (Entity e : hitList) {
            if (!hittedList.contains(e)) {
                TaoCombatUtils.attack(getThrower(), e, hand);
                hittedList.add(e);
            }
        }
        super.onRetrieveWeapon();
    }
}
