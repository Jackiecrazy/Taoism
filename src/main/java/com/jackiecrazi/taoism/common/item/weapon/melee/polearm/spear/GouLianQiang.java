package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GouLianQiang extends TaoWeapon {
    /*
    A spear weapon that specializes in hooking and grabbing. High range and speed, medium combo and defense, low power
    Reference: a guisarme can be used to catch incoming blades
    A guisarme can hook down an overhead parry for a stab
    A goulian qiang has a blunt hook used for pulling. It's mainly used to pull people off horses, trip horses or pull horses off people... wait.
    I imagine the cavity would lead to great catching of blades.
    Anyway this trips people and counters parries.

    left click for a normal stab. Like the misericorde, this ignores half armor when hitting a downed target
    right click to hook for no damage. Doing this twice in a row will trip, inflicting 50% max posture damage
    if the target is not facing the attacker, instantly trip
    if the target is unaware of attacker, instantly down

    TODO this loses the spear's ability to pierce. Instead, winning a blade lock will disable the opponent's weapon for 1 second.
    riposte:
    //the next bash in 4 seconds AoEs, knocks back and briefly slows the opponents
    //the next stab in 4 seconds instantly hooks and trips
     */

    public GouLianQiang() {
        super(2, 1.2, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float aerial = !attacker.onGround ? 1.5f : 1f;
        float hook = getHand(item) == EnumHand.OFF_HAND ? 0.1f : 1f;
        return aerial * hook;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 1 : 2;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 6f;
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

    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && isCharged(attacker, stack))
            splash(attacker, target, 4);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //main function. Check if previous move is also left click on the same target and trip if so
            if ((!getLastMove(stack).isLeftClick() && getLastAttackedEntity(attacker.world, stack) == target) || isCharged(attacker, stack) || !NeedyLittleThings.isFacingEntity(target, attacker)) {
                //we're going on a trip on our favourite hooked... ship?
                TaoCasterData.getTaoCap(target).consumePosture(TaoCasterData.getTaoCap(target).getMaxPosture() / 2f, true, attacker, null);
            }
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("goulianqiang.trip"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("goulianqiang.trip.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("goulianqiang.bash"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("goulianqiang.bash.riposte") + TextFormatting.RESET);
        tooltip.add(TextFormatting.BOLD + I18n.format("qiang.riposte") + TextFormatting.RESET);
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        if (getHand(is) == EnumHand.OFF_HAND&&TaoCombatUtils.getHandCoolDown(elb, EnumHand.MAIN_HAND)<0.5f)//
            TaoCombatUtils.rechargeHand(elb, EnumHand.MAIN_HAND, 0.5f);
    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getHand(stack) == EnumHand.MAIN_HAND && (TaoCasterData.getTaoCap(target).getDownTimer() > 0 || isCharged(attacker, stack))) {
            //ignore half armor when downed
            return TaoCombatUtils.recalculateIgnoreArmor(target, ds, orig, target.getTotalArmorValue() / 2f);
        }
        return super.damageStart(ds, attacker, target, stack, orig);
    }
}
