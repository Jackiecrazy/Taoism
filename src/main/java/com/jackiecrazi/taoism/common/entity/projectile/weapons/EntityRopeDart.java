package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.melee.rope.RopeDart;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityRopeDart extends EntityThrownWeapon {

    public EntityRopeDart(World w) {
        super(w);
    }

    public EntityRopeDart(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude, main);
        if (main != null)
            hand = main;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != TaoItems.ropedart || this.getDistanceSq(getThrower()) > RopeDart.MAXRANGESQ * 2 || ticksExisted > 60) {
                this.setDead();
                return;
            }
            //System.out.println(posX + " " + posY + " " + posZ);
            if (this.getDistanceSq(getThrower()) > RopeDart.MAXRANGESQ) {
                updateHitStatus(3);
            }
            else if ((getThrower().getDistanceSq(this) > 4 && hitStatus == -1)) {
                updateHitStatus(0);
            }
            if (hitStatus > 0) {
                onRecall();
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    private void resetCoolDown() {
        if (getThrower() == null || getThrower().getHeldItem(hand).getItem() != TaoItems.ropedart) {
            setDead();
            return;
        }
        ItemStack main = getThrower().getHeldItem(hand);
        RopeDart rd = ((RopeDart) main.getItem());
        main.removeSubCompound("connected");
        rd.chargeWeapon(getThrower(), main, rd.getMaxChargeTime());

    }

    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0||world.isRemote) return;
        super.onHitEntity(hit);
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        assert is.getTagCompound() != null;
        is.getTagCompound().setBoolean("connected", true);
        TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
        if (shootingEntity instanceof EntityPlayer)
            TaoCombatUtils.taoWeaponAttack(hit, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()));
    }

    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
        switch (hitStatus) {
            case 0://hit nothing, retract weapon, refund all
                stack.setItemDamage(0);
                break;
            case 1://hit block, refund nothing
                stack.setItemDamage(20);
                break;
            case 2://hit entity, refund half
                stack.setItemDamage(10);
        }
    }
}