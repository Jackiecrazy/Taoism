package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IStaminaPostureManipulable {
    /*
    melee attacks deal posture damage when hitting an entity, more if it's blocking.
    ranged attacks must be blocked with a shield unless a perk or word of god says otherwise.
    this makes shields not absolutely necessary for assassins, while still keeping them viable for warriors and tanks.

    Weapons deal a base amount of posture, plus its actual damage on a multiplier if attack is blocked:
    0.2 for stab, 0.3 for slash, 0.4 for axes, 0.5 for blunt.
    This is then multiplied by block multipliers on the defender's side, since swords are better at blocking than knives
    the better weapon will be used to block, e.g. hammer-misericorde combo will have the hammer parry.
    posture regenerates at a rate of 2 per second after 1 second of not taking damage, or all the time if SSP is off.
    40% of base dealt posture damage (before def) will reflect on the attacker when a block is done. Will not stagger.
    if the attack would stagger the target, the remaining damage goes through to armor and knocks the target away,
    the target will be downed for however many ticks the damage is, times 10 and clamped between 10-60, and loses SSP
    e.g. 4 points of damage will down you for 40 ticks (2 seconds), during which time you cannot move except via rolling
    damage dealt to the target when downed is doubled and cannot be blocked
    this allows attackers to execute cinematic executions, but leaves them open if the target rolls away.

    The target can block by sneaking, parry for 7 ticks by briefly sneaking, or dodge by double tapping any movement key
    when a parry is executed, posture damage is reduced to 30% and the defender will execute the weapon's parry skill.
    parry skill numbers, if any, are multiplied by 75% when dual wielding, and posture use is the average of both.
    posture damage caused by a parry will always leave you with at least 1 posture.
    if the parry isn't successful, however, you will be disallowed from parrying for 13 ticks, adding up to 1 second
    dodging will reduce the height of the defender to its width or vice versa, whichever is smaller.
    dodging presents a smaller hitbox against attacks, but also reduces max posture, so caution is advised.
    this allows defenders to gain an edge by parrying or outright evading attacks.

    sudden stagger prevention: if you take a staggering move when posture is over 25%, posture will drop to 0.01.
    SSP recharges once your posture is full again.
    this prevents a player from being killed outright from misjudging whether they should block.
    it also works as a "downed" indicator to show whether the player should constantly regenerate posture for a comeback
    high vanilla damage sources to keep in mind: (charged) creeper 73(145), ghast 25, golem 31, ravager 18, dragon 15
     */
    void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item);

    void onBlock(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item);

    float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount);

    float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount);
}