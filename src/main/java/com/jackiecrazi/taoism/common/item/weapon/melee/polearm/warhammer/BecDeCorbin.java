package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.warhammer;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

/**
 * horseback hammer that punches through armor with speed. High power, middling range and speed, low combo and defense
 * range 4, speed 1.4, two-handed, crit deals double
 * normal attack has higher knockback on dismounted targets, and converts knockback into posture for mounted targets
 * the less posture they have, the more posture is deducted
 * right click is a pierce that ignores armor based on your qi, and inflicts negative knockback
 * both actions will stab a staggered target for crit damage
 */
public class BecDeCorbin extends TaoWeapon {
    public BecDeCorbin() {
        super(2, 1.4, 6, 1);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

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
        return 1.4f;
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (target.isRiding()) {
            gettagfast(stack).setFloat("kbBuff", orig * 1.2f);
            return 0;
        }
        return super.onKnockingBack(attacker, target, stack, orig) * (getHand(stack) == EnumHand.OFF_HAND ? -1.2f : 1.2f);
    }

    @Override
    public float postureDealtBase(@Nullable EntityLivingBase attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        if (defender != null && attacker != null)
            return gettagfast(item).getFloat("kbBuff") + amount * (1.5f - TaoCasterData.getTaoCap(defender).getPosture() / (2 * TaoCasterData.getTaoCap(defender).getMaxPosture()));
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
        return TaoCasterData.getTaoCap(attacker).getQiFloored();
    }
}
