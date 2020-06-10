package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySwordBeam extends EntityThrowable {
    private EnumHand h;
    private String s;

    public EntitySwordBeam(World w) {
        super(w);
    }

    public EntitySwordBeam(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn);
        s=is.getUnlocalizedName();
        h = hand;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (thrower == null || this.getDistanceSq(thrower) > 100 || ticksExisted > 60 || h == null || s == null || !thrower.getHeldItem(h).getUnlocalizedName().equals(s)) {
                this.setDead();
                return;
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (thrower != null) {
                if (result.entityHit != null) {
                    if (result.entityHit != thrower) {
                        if(this.getDistanceSq(thrower) > 100 || ticksExisted > 60 || h == null || s == null || !thrower.getHeldItem(h).getUnlocalizedName().equals(s)) {
                                this.setDead();
                                return;
                            }
                        Entity e = result.entityHit;
                        float temp=TaoCombatUtils.getHandCoolDown(thrower, h);
                        TaoCombatUtils.rechargeHand(thrower, h, 1f);
                        TaoCombatUtils.taoWeaponAttack(e, (EntityPlayer) thrower, thrower.getHeldItem(h), true, false);
                        TaoCombatUtils.rechargeHand(thrower, h, temp);
                    }
                }
            }
        }
        setDead();
    }
}