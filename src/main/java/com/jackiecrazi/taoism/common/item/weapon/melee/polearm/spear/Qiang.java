package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
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

public class Qiang extends TaoWeapon {
    /*
    First two handed weapon!
    using 游场枪 tactics.
    A spear used for dueling, short enough to use staff moves. High reach and speed, medium power and combo, low defense
    left click for a narrow, but long normal stab. Damage scales based on distance, up to double at front 1/3
    right click for a swipe on the ground, or a top-down bonk when airborne, at 30% damage.
    highly dependent on footwork and facing your opponent.
    Primary method of avoidance is side dodge/jump/slide, then close in distance
    TODO holding some type of spear upward allows you to throw it like a javelin. Prepare sidearm.
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE,
            new PartDefinition("tassel", StaticRefs.STRING)
    };

    public Qiang() {
        super(2, 1.4, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public int getMaxChargeTime() {
        return 80;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.4f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected double speed(ItemStack stack) {
        return (1d + (getHand(stack) == EnumHand.MAIN_HAND ? Math.sqrt(getLastAttackedRangeSq(stack)) / 6f : 0)) - 4d;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && getLastAttackedRangeSq(stack) != 0f) {
            splash(attacker, stack, 120);
            setLastAttackedRangeSq(attacker, attacker.getHeldItemMainhand(), 0);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.stab"));
        tooltip.add(I18n.format("qiang.recharge"));
        tooltip.add(I18n.format("qiang.bash"));
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 1 : 2;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.OFF_HAND ? 0.5f : 1 + (float) NeedyLittleThings.getDistSqCompensated(attacker, target) / 54f;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 1f);
        } else {
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }
}
