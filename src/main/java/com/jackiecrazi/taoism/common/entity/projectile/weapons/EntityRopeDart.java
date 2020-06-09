package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.melee.rope.RopeDart;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityRopeDart extends EntityThrowable {
    private EnumHand hand = EnumHand.MAIN_HAND;
    private byte hitStatus = -1;//-1 for ignore, 0 for flying, 1 for ground, 2 for entity, 3 for empty

    public EntityRopeDart(World w) {
        super(w);
    }

    public EntityRopeDart(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude);
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
                hitStatus = 3;
            }
            if (hitStatus > 0) returnToSender();
        }
        if (getThrower() == null || (getThrower().getDistanceSq(this) > 4 && hitStatus == -1)) {
            //TODO hits the player on their head in client, spawn further away?
            hitStatus = 0;
        }
        super.onUpdate();
    }

    private void returnToSender() {
        if (getThrower() == null || getThrower().getHeldItem(hand).getItem() != TaoItems.ropedart) {
            setDead();
            return;
        }
        shoot(getThrower().posX - posX, getThrower().posY + getThrower().getEyeHeight() / 2 - posY, getThrower().posZ - posZ, 0.8f, 0);
        velocityChanged = true;
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (hitStatus == -1 || world.isRemote) return;
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) hitStatus = 1;
        if (getThrower() != null) {
            if (result.entityHit != null&&result.entityHit.isEntityAlive()) {
                ItemStack is = getThrower().getHeldItem(hand);
                if (result.entityHit != getThrower()) {
                    if (hitStatus > 0) return;
                    Entity e = result.entityHit;
                    if (is.getItem() != TaoItems.ropedart || !is.hasTagCompound()) this.setDead();
                    is.getTagCompound().setBoolean("connected", true);
                    TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
                    if (getThrower() instanceof EntityPlayer)
                        NeedyLittleThings.taoWeaponAttack(e, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true);
                    hitStatus = 2;
                    //it hits alright, but I&F redirects it which causes it to lose projectile affix, so no hit
                    //I&F will also remove offhand buffs, if any, with multipart redirection
                    //ick.

                } else {
                    this.setDead();
                    this.velocityChanged = true;
                    switch (hitStatus) {
                        case 0://hit nothing, retract weapon, refund all
                            is.setItemDamage(0);
                            break;
                        case 1://hit block, refund nothing
                            is.setItemDamage(20);
                            break;
                        case 2://hit entity, refund half
                            is.setItemDamage(10);
                    }
                }
            }
            //hit something, fly backwards to getThrower()
        } else {
            //hmm
            setDead();
        }
    }

    private void resetCoolDown() {
        if (getThrower() == null || getThrower().getHeldItem(hand).getItem() != TaoItems.ropedart) {
            setDead();
            return;
        }
        ItemStack main = getThrower().getHeldItem(hand);
        RopeDart rd = ((RopeDart) main.getItem());
        main.removeSubCompound("connected");
        rd.chargeWeapon(getThrower(), null, main, rd.getMaxChargeTime());

    }
}