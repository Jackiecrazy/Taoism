package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Event;

public interface ICombatManipulator {
    /**
     * called on LivingAttackEvent to determine whether the hit is valid
     */
    boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * called on CriticalHitEvent to determine whether the hit is critical
     */
    Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit);

    /**
     * this is called on CriticalHitEvent to determine crit multiplier
     */
    float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item);

    /**
     * this is called on LivingHurt to determine damage multiplier
     */
    float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item);

    /**
     * this is called on LivingAttackEvent, before parries
     */
    void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingKnockBackEvent
     * @return a new knockback if necessary
     */
    float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingHurtEvent, before armor reductions
     * @return a new damage if necessary
     */
    float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingDamageEvent, after armor, absorption, and all other reductions, and after armorIgnoreAmount
     * @return a new damage if necessary
     */
    float finalDamageMods(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingDamageEvent, after armor, absorption, and all other reductions, but before damageStart
     * @return a new damage if necessary
     */
    int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);
}
