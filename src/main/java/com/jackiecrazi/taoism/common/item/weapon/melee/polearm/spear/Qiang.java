package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Qiang extends TaoWeapon {
    /*
    First two handed weapon! High reach and combo, medium power and speed, low defense
    left click for a normal stab, piercing enemies up to the max range.
    right click to do a bash that knocks the target a fair distance away and inflicts blunt damage, at cost of lower damage
    These two attacks have independent cooldowns, and doing one will instantly halve the other's, so you can continuously chain them.
    riposte:
    //the next bash in 4 seconds AoEs, knocks back and briefly slows the opponents
    //the next stab in 4 seconds deals cutting damage 3 times with an interval of 1 tick
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE,
            new PartDefinition("tassel", StaticRefs.STRING)
    };

    public Qiang() {
        super(2, 1.2, 6.5d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float aerial = !attacker.onGround ? 1.5f : 1f;
        float bash = getHand(item) == EnumHand.OFF_HAND ? 0.5f : 1f;
        return aerial * bash;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : isCharged(null, is) ? 1 : 2;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }

    @Override
    public int getMaxChargeTime() {
        return 80;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.4f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && isCharged(attacker, stack))
            splash(attacker, target, 4);
        else if (getHand(stack) == EnumHand.MAIN_HAND)
            splash(attacker, NeedyLittleThings.raytraceEntities(target.world, attacker, target, getReach(attacker, stack)));
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 1f);
            if (isCharged(attacker, stack)) {
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 0));
            }
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, TaoCombatUtils.getHandCoolDown(elb, other) * 0.5f);
    }

    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack) && getHand(stack) == EnumHand.MAIN_HAND) {
            multiHit(attacker, target, 4, 8);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.stab"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("qiang.stab.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.bash"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("qiang.bash.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.oscillate"));
        tooltip.add(TextFormatting.BOLD + I18n.format("qiang.riposte") + TextFormatting.RESET);
    }
}
