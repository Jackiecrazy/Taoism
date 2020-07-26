package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityKusarigamaShot extends EntityThrownWeapon {
    public EntityKusarigamaShot(World worldIn) {
        super(worldIn);
    }

    public EntityKusarigamaShot(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude, main);
        if (main != null)
            hand = main;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != TaoItems.kusarigama || this.getDistanceSq(getThrower()) > 72) {
                this.setDead();
                return;
            }
            //upon returning to the origin point, if player is not there, turn hurtbox on and go at them again
            //System.out.println(posX + " " + posY + " " + posZ);
            if (this.getDistanceSq(getThrower()) > 36) {
                updateHitStatus(3);
            }
            if (hitStatus > 0 || inGround) {
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
