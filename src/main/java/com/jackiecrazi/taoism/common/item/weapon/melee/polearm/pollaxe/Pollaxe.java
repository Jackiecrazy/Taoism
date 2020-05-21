package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Pollaxe extends TaoWeapon {
    /*
     * A long-handled axe with incredible burst. High range and power, medium defense and speed, low combo
     * 6 blocks of reach, 2 handed
     * Leap attacks deal double posture damage
     * Left click is a standard attack that stacks cleave 2/3
     * Right click for a sweep that AoEs and knocks enemies back slightly
     * Parry special:
     * the next attack in 5 seconds deals 0.35*damage posture regardless of block.
     * your next standard attack in the next 5 seconds becomes a stab, dealing 2 piercing damage per cleave layer
     * your next sweep in the next 5 seconds has its damage multiplied by 1+(cleave/10) and add cleave 3/5 on all enemies
     */

    private static final boolean[] harvestList = {false, false, true, false};

    public Pollaxe() {
        super(3, 0.8, 9, 2f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        float leap = attacker.onGround ? 1f : 2f;
        float off = getHand(item) == EnumHand.OFF_HAND ? 0.4f : 1f;
        return leap * off;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 5f : 6f;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return isCharged(null, is) ? 2 : 3;
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.leap"));
        tooltip.add(I18n.format("pollaxe.cleave"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("pollaxe.cleave.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.swipe"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("pollaxe.swipe.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.riposte"));
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return isCharged(attacker, item) ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(orig * 0.35f, true, attacker, ds);
        }
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        float doot = super.hurtStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item) && target.getActivePotionEffect(TaoPotion.ARMORBREAK) != null) {
            PotionEffect pe = target.getActivePotionEffect(TaoPotion.ARMORBREAK);
            if (getHand(item) == EnumHand.OFF_HAND)
                return doot * ((pe.getAmplifier() + 1) / 10f);
            else {
                target.removeActivePotionEffect(TaoPotion.ARMORBREAK);
                return doot + (pe.getAmplifier() / 2f);
            }
        }
        return doot;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            splash(attacker, target, 4);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, attacker.onGround ? 1f : 1.3f);
            if (isCharged(attacker, stack)) {
                target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 100, 2), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION));
            }
        } else {
            target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 60, 1), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION));
        }
    }
}
