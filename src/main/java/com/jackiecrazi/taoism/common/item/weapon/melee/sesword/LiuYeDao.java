package com.jackiecrazi.taoism.common.item.weapon.melee.sesword;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * a fast weapon based on dragging slashes and overwhelming the enemy with speed, dependent on footwork and synergy. High combo, normal speed and power, low defense and range
 * range 3, cleave 120, speed 1.6, damage 5
 * damage is amplified up to 1.5x if you attack right as cooldown ends
 * knockback on both sides converted into posture damage; the victim, upon taking damage, also has knockback converted into armor pierce
 */
public class LiuYeDao extends TaoWeapon {
    public LiuYeDao() {
        super(1, 1.6, 5, 0.7f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 3;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.3f;
    }

    @Override
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        splash(attacker, stack, 120);
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (TaoCombatUtils.getHandCoolDown(attacker, getHand(item)) > 0.9)
            return MathHelper.clamp(2.5f - getCoolDownUncapped(attacker, getHand(item), 0.5f), 1, 1.5f);
        return 1;
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        TaoCasterData.getTaoCap(target).consumePosture(orig, true);
        gettagfast(stack).setInteger("kb", (int) orig*3);
        return 0;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return gettagfast(item).getInteger("kb");
    }

    @Override
    public float onBeingKnockedBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        TaoCasterData.getTaoCap(target).consumePosture(orig, true);
        return 0;
    }

    private static float getCoolDownUncapped(EntityLivingBase elb, EnumHand hand, float adjustTicks) {
        if (hand == EnumHand.OFF_HAND)
            return ((float) TaoCasterData.getTaoCap(elb).getOffhandCool() + adjustTicks) / TaoCombatUtils.getCooldownPeriodOff(elb);
        return ((float) Taoism.getAtk(elb) + adjustTicks) / TaoCombatUtils.getCooldownPeriod(elb);
    }
}
