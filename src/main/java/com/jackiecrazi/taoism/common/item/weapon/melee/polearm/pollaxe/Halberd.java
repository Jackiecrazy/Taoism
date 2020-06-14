package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
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

public class Halberd extends TaoWeapon {
    /*
     * A long-handled axe with incredible burst. High range and power, medium defense and speed, low combo
     * 6 blocks of reach, 2 handed
     * Leap attacks deal double posture damage
     * Left click is a standard attack that stacks cleave 2/3
     * Right click is a stab that detonates cleave for piercing damage with 10 second cooldown
     *      Each detonated layer of cleave will reduce a second of cleave
     */

    private static final boolean[] harvestList = {false, false, true, false};

    public Halberd() {
        super(3, 1, 8, 1f);
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
    public float getReach(EntityLivingBase p, ItemStack is) {
        return getHand(is)==EnumHand.MAIN_HAND?5f:6f;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.3f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected double speed(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND ? 0.1 - 4d : super.speed(stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("halberd.leap"));
        tooltip.add(I18n.format("halberd.cleave"));
        tooltip.add(I18n.format("halberd.stab"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("halberd.stab.cooldown") + TextFormatting.RESET);
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 2 : 3;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item) == EnumHand.OFF_HAND ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public double attackDamage(ItemStack stack) {
        return getHand(stack)==EnumHand.OFF_HAND?1:super.attackDamage(stack);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return attacker.motionY < 0 ? 2f : 1f;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        return 1;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        float doot = super.hurtStart(ds, attacker, target, item, orig);
        if (target.getActivePotionEffect(TaoPotion.ARMORBREAK) != null && getHand(item) == EnumHand.OFF_HAND) {
            PotionEffect pe = target.getActivePotionEffect(TaoPotion.ARMORBREAK);
            ds.setDamageBypassesArmor();
            target.removeActivePotionEffect(TaoPotion.ARMORBREAK);
            TaoCombatUtils.rechargeHand(attacker, EnumHand.OFF_HAND, pe.getAmplifier()/10f);
            return doot + (pe.getAmplifier() * 2f);
        }
        return doot;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 80, 1), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION));
        }
    }
}
