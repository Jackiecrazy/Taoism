package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.utils.TaoCombatUtils;
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
     * this is called on LivingAttackEvent, before parries. It returns void because LAE doesn't support modifying amounts
     */
    void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingKnockBackEvent
     * @return a new knockback if necessary
     */
    float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingKnockBackEvent
     * @return a new knockback if necessary
     */
    float onBeingKnockedBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingHurtEvent, before armor reductions
     * @return a new damage if necessary
     */
    float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingDamageEvent, after armor, absorption, and all other reductions
     * @return a new damage if necessary
     */
    float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingHurtEvent to apply armor down for the particular attack only
     * @return how much armor to ignore
     */
    int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);

    /**
     * this is called on LivingHurtEvent, after hurtStart, but before downed damage multipliers have been applied
     * @return a new damage if necessary
     */
    float onBeingHurt(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount);

    /**
     * this is called on LivingDamageEvent, after damageStart
     * @return a new damage if necessary
     */
    float onBeingDamaged(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount);

    /**
     * This is called by TaoStatCapability when an entity is no longer "immune" to damage after a cinematic execution sequence
     * @param ds a DamageSource as dictated by {@link TaoCombatUtils#causeLivingDamage(EntityLivingBase)}
     * @param orig the recorded damage
     * @return a new damage if necessary
     */
    float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig);
}
