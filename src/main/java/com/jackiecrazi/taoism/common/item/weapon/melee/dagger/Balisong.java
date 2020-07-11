package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

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
        setQiAccumulationRate(0.35f);
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
        return NeedyLittleThings.isBehindEntity(attacker, target) ? 3f : 1f;
    }

//    @Override
//    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
//        return 1 + (15 - attacker.world.getLight(attacker.getPosition())) / 15;
//    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 2f;
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//        return oldStack.isEmpty() || super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
//    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("balisong.switch"));
        tooltip.add(I18n.format("balisong.backstab"));
        tooltip.add(I18n.format("balisong.initiative"));
        tooltip.add(I18n.format("balisong.stance"));
        tooltip.add(I18n.format("balisong.hammer"));
        tooltip.add(I18n.format("balisong.reverse"));
        //tooltip.add(I18n.format("balisong.riposte"));
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //TaoCasterData.getTaoCap(defender).setRollCounter(0);
//        defender.rotationYaw = attacker.rotationYaw;
//        defender.rotationPitch = attacker.rotationPitch;
//        setCombo(defender, item, 0);
//        Vec3d look = attacker.getLookVec();
//        defender.motionX=-look.x;
//        defender.motionY=-look.y;
//        defender.motionZ=-look.z;
//        defender.velocityChanged = true;
        super.onParry(attacker, defender, item);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EnumHand hand=elb.getHeldItemOffhand()==stack?EnumHand.OFF_HAND:EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand,1);
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {

    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return NeedyLittleThings.isBehindEntity(attacker, target) ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //ignore 1 point of armor every chi level
            return getQiFromStack(stack);
        }
        if ((target.getCombatTracker().getBestAttacker() != attacker)) {
            return target.getTotalArmorValue();
        }
        return super.armorIgnoreAmount(ds, attacker, target, stack, orig);
    }
}
