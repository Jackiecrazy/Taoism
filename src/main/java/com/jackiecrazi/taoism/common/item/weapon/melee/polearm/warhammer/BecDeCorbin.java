package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.warhammer;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

/**
 * horseback hammer that punches through armor. High power, middling range and speed, low combo and defense
 * range 4, speed 1.4, two-handed, crit deals double
 * normal attack has higher knockback on dismounted targets, and converts knockback into posture for mounted targets
 * the less posture they have, the more posture is deducted
 * right click is a pierce that ignores armor based on missing health, and inflicts negative knockback
 * both actions will stab a staggered target for crit damage
 */
public class BecDeCorbin extends TaoWeapon {
    public BecDeCorbin() {
        super(2, 1.4, 6, 7);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED+I18n.format("weapon.hands")+TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN+I18n.format("weapon.disshield")+TextFormatting.RESET);
        tooltip.add(I18n.format("becdecorbin.stagger"));
        tooltip.add(I18n.format("becdecorbin.knockback"));
        tooltip.add(I18n.format("becdecorbin.scaling"));
        tooltip.add(I18n.format("becdecorbin.armpen"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 4;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return super.onKnockingBack(attacker, target, stack, orig) * (getHand(stack) == EnumHand.OFF_HAND ? -1.2f : 1.2f);
    }

    @Override
    public float postureDealtBase(@Nullable EntityLivingBase attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        if (defender != null && attacker != null && getHand(item) == EnumHand.MAIN_HAND)
            return itemPostureMultiplier * (1.5f - TaoCasterData.getTaoCap(defender).getPosture() / (TaoCasterData.getTaoCap(defender).getMaxPosture()));
        return super.postureDealtBase(attacker, defender, item, amount);
    }

    @Override
    public int getDamageType(ItemStack is) {
        if (getHand(is) == EnumHand.OFF_HAND) return 2;
        return 0;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? Event.Result.ALLOW : Event.Result.DEFAULT;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return getHand(item) == EnumHand.OFF_HAND ? (int) ((1 - target.getHealth() / target.getMaxHealth()) * 10) : 0;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }
}
