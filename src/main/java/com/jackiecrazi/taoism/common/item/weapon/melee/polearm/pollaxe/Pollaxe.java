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
     * A two-handed axe that strikes on both ends. High combo and defense, medium range and speed, low power
     * Cue end can stab or be jammed into the opponent in a grapple, and won't be grabbed by the other guy
     * Business end can stab, chop, hook, or smash
     * Pruning this down:
     * cue end can stab into a grapple or parry into a shove
     * axe end can chop into a hook or smash into a stab
     * either side being parried means you can hit with other side
     * 4 blocks of reach, 1.5 handed
     * Leap attacks deal double posture damage
     * Right click is a cue stab, if after a parry, double posture damage
     * Left click is a heavy overhead swing, inflicting cleave (no stack), if after a parry, double posture damage
     *      if sprinting/charging, left click is with hammer head, knocking back. Further attacks will stab, causing extra piercing damage
     *
     * Oscillates
     * primary methods to counter it are to predict your opponent's next strike hand and engage from range
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
        return attacker.motionY<0 ? 2f : 1f;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        float off = getHand(item) == EnumHand.OFF_HAND ? 0.4f : 1f;
        return off;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 4f;
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

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is)==EnumHand.OFF_HAND ? 2 : 3;
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item)==EnumHand.OFF_HAND ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
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
        if (target.getActivePotionEffect(TaoPotion.ARMORBREAK) != null) {
            PotionEffect pe = target.getActivePotionEffect(TaoPotion.ARMORBREAK);
            if (getHand(item) == EnumHand.OFF_HAND){
                ds.setDamageBypassesArmor();
                target.removeActivePotionEffect(TaoPotion.ARMORBREAK);
                return doot + (pe.getAmplifier() / 2f);
            }
        }
        return doot;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 50, 1), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION));
        }
    }
}
