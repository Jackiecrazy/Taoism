package com.jackiecrazi.taoism.common.item.weapon.melee.hand;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

/**
 * hand weapons that lure, dodge and parry the enemy's weapons into a bind. High defense and combo, medium power and speed, low range
 * range 2, speed 2, damage 5, defense multiplier 0.6, can be equipped without cooldown
 * parry knockback is inverted so you actually draw in closer after a parry.
 * on parry, for 1 second: either automatically parry the next attack at no cost, or attack the opponent for crit damage and transform the remaining duration into bind
 * attacking a bound target counts as a crit
 */
public class YuanYangYue extends TaoWeapon {
    public YuanYangYue() {
        super(1, 2, 5, 2.1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("yuanyangyue.parry"));
        tooltip.add(I18n.format("yuanyangyue.riposte"));
        tooltip.add(I18n.format("yuanyangyue.crit"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 2;
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (getBuff(item, "lastParryTime") == 0) {
            defender.motionX = -defender.motionX;
            defender.motionZ = -defender.motionZ;
            gettagfast(item).setInteger("lastParryTime", defender.ticksExisted);
        } else gettagfast(item).setInteger("lastParryTime", 0);
        super.onParry(attacker, defender, item, amount);
    }

    @Override
    public void onOtherHandParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (getBuff(item, "lastParryTime") == 0) {
            defender.motionX = -defender.motionX;
            defender.motionZ = -defender.motionZ;
            gettagfast(item).setInteger("lastParryTime", defender.ticksExisted);
        } else gettagfast(item).setInteger("lastParryTime", 0);
        super.onOtherHandParry(attacker, defender, item, amount);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(@Nullable Entity attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        if (defender != null && getBuff(item, "lastParryTime") + 30 > defender.ticksExisted) {
            return 0;
        }
        return 0.7f;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        if (getBuff(item, "lastParryTime") + 30 > attacker.ticksExisted || TaoCasterData.getTaoCap(target).getBindTime() > 0)
            return Event.Result.ALLOW;
        return super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EnumHand hand = elb.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand, 1, true);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        int bindTime = 30 - attacker.ticksExisted + getBuff(stack, "lastParryTime");
        if (bindTime > 0) TaoCasterData.getTaoCap(target).setBindTime(bindTime);
        gettagfast(stack).setInteger("lastParryTime", 0);
    }
}
