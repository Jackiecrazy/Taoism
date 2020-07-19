package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
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
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Kusarigama extends TaoWeapon {
    /*
     * A sickle attached to a weighted chain. High power and speed, medium defense and combo, low range
     * Two handed, you hold the sickle in the offhand by default. Chain cannot block, but will store up charge by being held.
     * By default:
     * Left clicking once will throw the ball, bind charge/2; another click will drag them in
     * Right click will throw sickle if target's far, expending charge, and if less than 2
     *
     * throw a skillshot to gain a stacking buff
     */
    public Kusarigama() {
        super(1, 1.3, 5, 1);
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

    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            splash(attacker, stack, 90);
        }
    }

    protected double speed(ItemStack stack) {
        if(getHand(stack)==EnumHand.OFF_HAND)return -3.5;
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

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (attacker.world.getTotalWorldTime() - lastAttackTime(attacker, stack) > 100) setBuff(attacker.getHeldItemMainhand(), 0);
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig * (1 + getBuff(stack) * 0.1f);
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
        return getHand(is) == EnumHand.OFF_HAND ? 5 : 3;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if(getHand(stack)==EnumHand.OFF_HAND){
            setBuff(attacker.getHeldItemMainhand(), getBuff(stack)+1);
        }
    }
}
