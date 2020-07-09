package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CatNineTails extends TaoWeapon {
    //whipping implement used on prisoners, causing grievous lacerations. High power and speed, medium defense and range, low combo potential
    //whips around shields, cannot block
    //inflicts laceration
    //that's all. Better find something to cleave that armor off first...
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            new PartDefinition("handlewrap", false, StaticRefs.STRING)
    };

    public CatNineTails() {
        super(1, 1.4f, 5f, 0.6f);
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
        return 0;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }

    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", getReach(null, stack)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttMult", postureDealtBase(null, null, stack, 1)) + TextFormatting.RESET);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.parry") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.ignore") + TextFormatting.RESET);
        tooltip.add(I18n.format("catninetails.lacerate"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("catninetails.armord") + TextFormatting.RESET);
        tooltip.add(TextFormatting.ITALIC + I18n.format("catninetails.armorp") + TextFormatting.RESET);
    }

    public boolean canBlock(EntityLivingBase defender, ItemStack item) {
        return false;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        ds.setDamageBypassesArmor();
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        Taoism.setBypassArmor(ds, false);
        return super.hurtStart(ds, attacker, target, item, orig);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        int armor = target.getTotalArmorValue();
        int potency, duration;
        if (armor >= 10) return;//10 armor and above means no laceration, can't rip a guy in full iron...
        else {
            potency = (int) (2 - (Math.ceil(armor/5f)));
            duration = 100 - (armor * 10);
        }
        TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.LACERATION, duration, potency), false);
        TaoPotionUtils.forceBleed(target, attacker, duration, potency, TaoPotionUtils.POTSTACKINGMETHOD.ADD);
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3.5f;
    }
}
