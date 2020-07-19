package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.svardstav;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GuanDao extends TaoWeapon {
    /*
     * Retains versatility and relentlessness from its smaller cousin. High power and combo, medium speed and power, low defense
     * Right click switches between guard and strike forms, with a 1s(-chi/20) cooldown.
     * Switching from guard to strike deals a frontal attack and vice versa, and immediately resets normal attack
     * In guard form, range 4, splash 2, deals (1.3+chi/20)x damage. Riposte: gain 1 chi
     * In strike form, range 7, splash 4, no buff, no riposte.
     *
     * TODO Redesign:
     * gain momentum to cut continuously
     * start at 1.3 attack speed and 5 damage in the "new moon" phase
     * Right click moves the blade backwards, sweeping enemies in 90 degrees *behind* you
     * Left click moves the backwards blade forward again, sweeping enemies in 90 degrees before you
     * Successfully alternating attacks stacks a layer of moonlight, up to 4, at which point it's a "full moon"
     * Each layer of moonlight adds 0.1 attack speed and 10% base damage
     * Moonlight layers last up to 5 seconds, each gain resets the timer
     *
     * execution: only executable at full moon
     * gain the fifth layer, blood moon.
     * main attack slashes legs, reducing mobility.
     * off attack hits twice against slowed enemies
     * when enemy is staggered, lop off their head, ending blood moon.
     */
    public GuanDao() {
        super(1, 1, 5d, 1f);
        //this.addPropertyOverride(new ResourceLocation("long"), (stack, world, ent) -> isLong(stack) ? 1 : 0);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }


    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    protected void oncePerHit(EntityLivingBase attacker, EntityLivingBase target, ItemStack is) {
        if (getLastMove(is).isValid() && ((getLastMove(is).isLeftClick() && getHand(is) == EnumHand.OFF_HAND)||(!getLastMove(is).isLeftClick() && getHand(is) == EnumHand.MAIN_HAND))) {
            setBuff(attacker.getHeldItemMainhand(), getBuff(is) + 1);
        }
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //if (getBuff(stack) < 1) return;
            //setBuff(attacker.getHeldItemMainhand(), getBuff(stack) - 1);
            splash(attacker, stack, -120);
        } else {
            splash(attacker, stack, 90);
        }
    }

    protected double speed(ItemStack stack) {
        return 0.8 + (getBuff(stack) / 10f) - 4;
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : super.getQiAccumulationRate(is);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("guandao.alt"));
        tooltip.add(I18n.format("guandao.alt.attack"));
        tooltip.add(I18n.format("guandao.guard"));
        tooltip.add(I18n.format("guandao.strike"));
    }

//    @Override
//    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
//        return getBuff(item) > 3 ? Event.Result.ALLOW : Event.Result.DENY;
//    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1.5f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (attacker.world.getTotalWorldTime() - lastAttackTime(attacker, stack) > 100) setBuff(stack, 0);
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig * (1 + getBuff(stack) * 0.1f);
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //offhand attack
            TaoCombatUtils.rechargeHand(attacker, EnumHand.MAIN_HAND, 0.5f);
            //strike behind you
        } else {
            //mainhand attack
            TaoCombatUtils.rechargeHand(attacker, EnumHand.OFF_HAND, 0.5f);
        }
    }

    private void setBuff(ItemStack is, int phase) {
        System.out.println("setting moonlight to "+phase);
        gettagfast(is).setInteger("phase", MathHelper.clamp(phase, 0, 4));
    }

    private int getBuff(ItemStack is) {
        return gettagfast(is).getInteger("phase");
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3;
    }
}
