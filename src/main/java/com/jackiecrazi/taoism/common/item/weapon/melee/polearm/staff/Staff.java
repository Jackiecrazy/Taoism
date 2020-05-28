package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.staff;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Staff extends TaoWeapon {
    /*
     * A long weapon with great flexibility of moves. High range and combo, medium speed and defense, low power
     * Leap attacks deal 1.5x damage
     * Left click is a downward smash that knocks back airborne targets and a flick up that sends standing targets flying
     * Right click is a standard sweep, range 4, with small knockback
     * Block (not parry) is a continuous whirl, deflecting projectiles with speed less than qi*2 TODO integrate with idle parry
     * After ground-ground, gain small jump boost. Hitting grounded enemy while airborne will launch you further, while air-air will hit them up and away.
     * ^^^aerial duel test.
     * Riposte: combo extended to 2,
     *  block will deal 1/4 of attack damage all around and deflect any projectile, reflecting those it used to deflect for next 10s
     */

    public Staff() {
        super(0, 1.4f, 6f, 1f);
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return 0f;
    }

    @Override
    protected double speed(ItemStack stack) {
        double ret = super.speed(stack) + 4f;
        if (getHand(stack) == EnumHand.OFF_HAND) {
            ret /= 1.2d;
        }
        ret -= 4d;
        return ret;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return isCharged(wielder, is) && getHand(is) != EnumHand.OFF_HAND ? 2 : 1;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        float leap=attacker.onGround?1f:2f;
        float off=getHand(item)==EnumHand.OFF_HAND?0.4f:1f;
        return leap*off;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            splash(attacker, stack, 120);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            float groundKB = attacker.onGround ? 1f : 1.3f;
            NeedyLittleThings.knockBack(target, attacker, groundKB);
        } else {
            if (target.onGround) {
                target.addVelocity(0, 0.4, 0);
            } else {
                NeedyLittleThings.knockBack(target, attacker, 1f);
                target.addVelocity(0, -1, 0);
                target.fallDistance += 3f;
            }
            target.velocityChanged = true;
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 5f;
    }

    @Override
    public int getMaxChargeTime() {
        return 200;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("staff.leap"));
        tooltip.add(I18n.format("staff.flick"));
        tooltip.add(I18n.format("staff.smash"));
        tooltip.add(I18n.format("staff.swipe"));
        tooltip.add(I18n.format("staff.oscillate"));
        tooltip.add(I18n.format("staff.block"));
        tooltip.add(I18n.format("staff.riposte"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("staff.block.riposte") + TextFormatting.RESET);
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (onHand && e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (TaoCombatUtils.isEntityBlocking(elb)) {
                if (e.ticksExisted % 20 == 1)
                    splash(elb, stack, 120);
                for (Entity ent : w.getEntitiesInAABBexcluding(elb, elb.getEntityBoundingBox().grow(3, 3d, 3), null)) {
                    if (ent instanceof IProjectile && !NeedyLittleThings.isBehindEntity(ent, elb)) {
                        IProjectile ip = (IProjectile) ent;
                        Vec3d velocity = new Vec3d(ent.motionX, ent.motionY, ent.motionZ);
                        boolean isCharged = isCharged(elb, stack);
                        if (velocity.lengthSquared() < getQiFromStack(stack) * getQiFromStack(stack)) {
                            //reflect. I suppose just reversing its velocity will do...
                            if (isCharged) {
                                ip.shoot(-ent.motionX, -ent.motionY, -ent.motionZ, 1.6f, 0);
                            } else {
                                ent.motionX = 0;
                                ent.motionZ = 0;
                            }
                        } else if (isCharged) {
                            ent.motionX = 0;
                            ent.motionZ = 0;
                        }
                        ent.velocityChanged=true;
                    }
                }
            }
        }
    }
}
