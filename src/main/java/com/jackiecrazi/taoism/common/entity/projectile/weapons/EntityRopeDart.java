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
    private byte hitStatus = -1;//-1 for ignore, 0 for flying, 1 for ground, 2 for entity, 3 for empty

    public EntityRopeDart(World w) {
        super(w);
    }

    public EntityRopeDart(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.thrower == null || this.thrower.getHeldItemMainhand().getItem() != TaoItems.ropedart || this.getDistanceSq(thrower) > RopeDart.MAXRANGESQ * 2 || ticksExisted > 60) {
                this.setDead();
                return;
            }
            //System.out.println(posX + " " + posY + " " + posZ);
            if (this.getDistanceSq(thrower) > RopeDart.MAXRANGESQ) {
                hitStatus = 3;
            }
            if (hitStatus > 0) returnToSender();
        }
        if (thrower == null || (NeedyLittleThings.getDistSqCompensated(thrower, this) > 1 && hitStatus == -1)) {
            hitStatus = 0;
        }
        super.onUpdate();
    }

    private void returnToSender() {
        if (thrower == null || thrower.getHeldItemMainhand().getItem() != TaoItems.ropedart) {
            setDead();
            return;
        }
        shoot(thrower.posX - posX, thrower.posY + thrower.getEyeHeight() / 2 - posY, thrower.posZ - posZ, 0.8f, 0);
        velocityChanged = true;
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (hitStatus == -1) return;
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) hitStatus = 1;
        if (!world.isRemote) {
            if (thrower != null) {
                if (result.entityHit != null) {
                    ItemStack is = thrower.getHeldItemMainhand();
                    if (result.entityHit != thrower) {
                        if (hitStatus > 0) return;
                        Entity e = result.entityHit;
                        if (is.getItem() != TaoItems.ropedart || !is.hasTagCompound()) this.setDead();
                        is.getTagCompound().setBoolean("connected", true);
                        TaoCombatUtils.rechargeHand(thrower, EnumHand.MAIN_HAND, 1f);
                        NeedyLittleThings.taoWeaponAttack(e, (EntityPlayer) thrower, is, true, false);
                        hitStatus = 2;
                        //it hits alright, but I&F redirects it which causes it to lose projectile affix, so no hit
                        //I&F will also remove offhand buffs, if any, with multipart redirection
                        //ick.

                    } else {
                        this.setDead();
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
                //hit something, fly backwards to thrower
            } else {
                //hmm
                setDead();
            }
        }
    }

    private void resetCoolDown() {
        if (thrower == null || thrower.getHeldItemMainhand().getItem() != TaoItems.ropedart) {
            setDead();
            return;
        }
        ItemStack main = thrower.getHeldItemMainhand();
        RopeDart rd = ((RopeDart) main.getItem());
        main.removeSubCompound("connected");
        rd.chargeWeapon(thrower, null, main, rd.getMaxChargeTime());

    }
}