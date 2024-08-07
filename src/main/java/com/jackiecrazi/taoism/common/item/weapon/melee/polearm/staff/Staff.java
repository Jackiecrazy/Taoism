package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.staff;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.capability.TaoStatCapability;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Staff extends TaoWeapon {
    /*
     * A long weapon with great flexibility of moves. High range and combo, medium speed and defense, low power
     * Leap attacks deal 1.5x damage
     * Left click is a downward smash that knocks back airborne targets and a flick up that sends standing targets flying
     * Right click is a standard sweep, range 4, with small knockback
     * Block (not parry) is a continuous whirl, deflecting projectiles with speed less than qi*2
     * After ground-ground, gain small jump boost. Hitting grounded enemy while airborne will launch you further, while air-air will hit them up and away.
     * ^^^aerial duel test.
     * Riposte: combo extended to 2,
     *  block will deal 1/4 of attack damage all around and deflect any projectile, reflecting those it used to deflect for next 10s
     */

    public Staff() {
        super(0, 1.4f, 6f, 3f);
        setUnlocalizedName("taostaff");
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 2;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 5f;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
//        if (onHand && e instanceof EntityLivingBase) {
//            EntityLivingBase elb = (EntityLivingBase) e;
////            if (e.ticksExisted % 20 == 1)
////                splash(elb, stack, 120);
//            for (Entity ent : w.getEntitiesInAABBexcluding(elb, elb.getEntityBoundingBox().grow(3, 3d, 3), null)) {
//
//            }
//        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("staff.flick"));
        tooltip.add(I18n.format("staff.smash"));
        tooltip.add(I18n.format("staff.doink"));
        tooltip.add(I18n.format("staff.throw"));
        tooltip.add(I18n.format("staff.oscillate"));
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f, true);
    }

    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return Event.Result.DENY;
        //recharged, fallen more than 0 blocks, not on ground, not on ladder, not in water, not blind, not riding, target is ELB
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1f;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        attacker.motionY = target.motionY = 0;
        if (getHand(stack) == EnumHand.OFF_HAND) {
            if (!attacker.isSneaking()) {
                //ground-ground air flick
                target.addVelocity(0, chi / 15f, 0);
                TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.JUMP_BOOST, 20, chi / 4), false);
                TaoCasterData.getTaoCap(attacker).setJumpState(ITaoStatCapability.JUMPSTATE.DODGING);
            } else {
                //ground-air slam down
                NeedyLittleThings.knockBack(target, attacker, 1f, true, false);
                target.hurtResistantTime = 0;
                target.fallDistance += chi;
            }
        } else {
            if (!attacker.isSneaking()) {
                //air-ground boost up
                attacker.motionY = chi / 10f;
            } else {
                //air-air target knock
                NeedyLittleThings.knockBack(target, attacker, 1f, true, false);
                TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.JUMP_BOOST, 20, chi / 4), false);
            }
        }
        attacker.velocityChanged = true;
        target.velocityChanged = true;
    }
}
