package com.jackiecrazi.taoism.common.item.weapon.melee.pick;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ChickenSickle extends TaoWeapon {
    /*
     * An unassuming weapon until you fight back. High power and speed, medium combo and defense, low range
     *
     * Redesign: stack hemorrhage, detonate automatically when hemorrhage>armor
     * while hemorrhage is active, receiving a negative buff will add hemorrhage's duration and potency to it, consuming hemorrhage in the process
     */
    public ChickenSickle() {
        super(2, 1.6, 7, 1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("chickensickle.normal"));
        tooltip.add(I18n.format("chickensickle.riposte1"));
        tooltip.add(I18n.format("chickensickle.riposte2"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 4;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        PotionEffect hemorrhage=NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.HEMORRHAGE,100,1), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION);
        if(hemorrhage.getAmplifier()*4>=target.getTotalArmorValue()){//isCharged(attacker,stack)
            target.attackEntityFrom(DamageSourceBleed.causeBleedingDamage(),(target.getMaxHealth()/100)*hemorrhage.getAmplifier()*hemorrhage.getAmplifier());
            target.addPotionEffect(NeedyLittleThings.stackPot(target,new PotionEffect(TaoPotion.BLEED, 100, hemorrhage.getAmplifier()), NeedyLittleThings.POTSTACKINGMETHOD.ADD));
        }else target.addPotionEffect(hemorrhage);
    }
}
