package com.jackiecrazi.taoism.common.entity.ai;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class AIDualWielding extends EntityAIAttackMelee {

    public AIDualWielding(EntityCreature creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_)
    {
        double d0 = this.getAttackReachSqr(p_190102_1_);

        if (p_190102_2_ <= d0 && this.attackTick <= 0)
        {
            this.attackTick = 20;
            EnumHand hand=TaoCombatUtils.getHandCoolDown(attacker, EnumHand.MAIN_HAND)>0.9?EnumHand.MAIN_HAND:EnumHand.OFF_HAND;
            this.attacker.swingArm(hand);
            TaoCombatUtils.taoWeaponAttack(p_190102_1_, attacker, TaoCombatUtils.getAttackingItemStackSensitive(attacker),hand==EnumHand.MAIN_HAND, true);
        }
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        IAttributeInstance reach = attacker.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if(reach ==null)return(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
        if(TaoCombatUtils.getHandCoolDown(attacker, EnumHand.MAIN_HAND)>0.9) return reach.getAttributeValue()*reach.getAttributeValue();
        else if (TaoCombatUtils.getHandCoolDown(attacker, EnumHand.OFF_HAND)>0.9) {
            double real = reach.getAttributeValue() - NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, attacker, EnumHand.MAIN_HAND) - NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, attacker, EnumHand.OFF_HAND);
            return real*real;
        }
        else return 0;

    }
}