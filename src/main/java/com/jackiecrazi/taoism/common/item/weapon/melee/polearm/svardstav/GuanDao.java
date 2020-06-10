package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.svardstav;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GuanDao extends TaoWeapon {
    /*
     * Retains versatility and relentlessness from its smaller cousin. High range and combo, medium speed and power, low defense
     * Right click switches between guard and strike forms, with a 1s(-chi/20) cooldown.
     * Switching from guard to strike deals a frontal attack and vice versa, and immediately resets normal attack
     * In guard form, range 4, splash 2, deals (1.3+chi/20)x damage. Riposte: gain 1 chi
     * In strike form, range 7, splash 4, no buff, no riposte.
     */
    public GuanDao() {
        super(1, 1.5, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float chimult = TaoCasterData.getTaoCap(attacker).getQiFloored() / 20f;
        boolean off = getHand(item) == EnumHand.OFF_HAND;
        boolean charge = isCharged(attacker, item);
        return off || charge ? 1f : 1.3f * (1 + chimult);
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND && isCharged(null, is) ? 2 : 1;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return !isCharged(wielder, is) && getHand(is) != EnumHand.OFF_HAND ? 2 : 1;
    }

    protected double speed(ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND)
            return (1 + (getQiFromStack(stack) / 10d)) - 4d;
        return super.speed(stack);
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : super.getQiAccumulationRate(is);
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return (getHand(is) == EnumHand.MAIN_HAND) == isCharged(p, is) ? 5 : 2;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        if (!isCharged(attacker, item))
            TaoCasterData.getTaoCap(attacker).addQi(1f);
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && !isCharged(attacker, stack)) {
            splash(attacker, null, stack, 360, NeedyLittleThings.raytraceEntities(attacker.world, attacker, -2));
        } else {
            splash(attacker, stack, isCharged(attacker, stack) ? 180 : 90);
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            if (isCharged(attacker, stack)) {
                //strike form
                splash(attacker, null, stack, 360, NeedyLittleThings.raytraceEntities(attacker.world, attacker, -4));
                dischargeWeapon(attacker, stack);
                TaoCombatUtils.rechargeHand(attacker, EnumHand.MAIN_HAND, 0.8f);
                //strike behind you
            } else {
                //guard form
                chargeWeapon(attacker, null, stack, 100);
                TaoCombatUtils.rechargeHand(attacker, EnumHand.MAIN_HAND, 0.8f);
            }
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("guandao.alt"));
        tooltip.add(I18n.format("guandao.alt.attack"));
        tooltip.add(I18n.format("guandao.guard"));
        tooltip.add(I18n.format("guandao.strike"));
    }
}
