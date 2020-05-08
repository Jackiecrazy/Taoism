package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Balisong extends TaoWeapon {
    //A stabbing dagger that is fast and relentless, but short in reach. Can be flicked open and closed.
    //has no switch in cooldown, and can be switched from hand to hand without cooldown as well.
    //backstabs deal double damage.
    //has 2 stances: hammer and reverse.
    //combos up to 6 times, increasing every other chi level, if in hammer grip
    //pierces 1 point of armor every chi level in reverse grip


    public Balisong() {
        super(2, 2, 5f, 0);
        setQiAccumulationRate(0.75f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return getHand(is) == EnumHand.MAIN_HAND ? (getQiFromStack(is) / 2) + 1 : 1;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float light=1+(15-attacker.world.getLight(attacker.getPosition()))/15;//light bonus
        float backstab=NeedyLittleThings.isBehindEntity(attacker, target) ? isCharged(attacker, item) ? 3f : 2f : 1f;
        return light*backstab;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        setCombo(defender, item, 0);
        defender.hurtResistantTime=getMaxChargeTime();
        Vec3d look = attacker.getLookVec();
        defender.addVelocity(-look.x, -look.y, -look.z);
        defender.velocityChanged = true;
        super.parrySkill(attacker, defender, item);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack)) {
            dischargeWeapon(attacker, stack);
        }
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 2f;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.isEmpty() || super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            Taoism.setAtk(elb, 5);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("balisong.switch"));
        tooltip.add(I18n.format("balisong.backstab"));
        tooltip.add(I18n.format("balisong.initiative"));
        tooltip.add(I18n.format("balisong.darkness"));
        tooltip.add(I18n.format("balisong.stance"));
        tooltip.add(I18n.format("balisong.hammer"));
        tooltip.add(I18n.format("balisong.reverse"));
        tooltip.add(I18n.format("balisong.riposte"));
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //ignore 1 point of armor every chi level
            return getQiFromStack(stack);
        }
        if (getLastAttackedEntity(attacker.world, stack)!=target) {
            return target.getTotalArmorValue();
        }
        return super.armorIgnoreAmount(ds,attacker,target,stack,orig);
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {

    }
}
