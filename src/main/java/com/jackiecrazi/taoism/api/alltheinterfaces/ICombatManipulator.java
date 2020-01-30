package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface ICombatManipulator {
    /**
     * this is called on CriticalHitEvent to determine crit multiplier
     */
    float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item);

    /**
     * this is called on LivingAttackEvent, before parries
     */
    void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingHurtEvent, before armor reductions
     * @return a new damage if necessary
     */
    float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingDamageEvent, after armor, absorption, and all other reductions
     * @return a new damage if necessary
     */
    float damageEnd(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingKnockBackEvent
     * @return a new knockback if necessary
     */
    float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);
}
