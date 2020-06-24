package com.jackiecrazi.taoism.common.item.weapon.melee.pick;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class ChickenSickle extends TaoWeapon {
    /*
     * A weapon that amplifies effects to the point of debilitation. High power and speed, medium combo and defense, low range
     *
     * Redesign: stack hemorrhage, detonate automatically when hemorrhage>armor
     * while hemorrhage is active, receiving a negative buff will add hemorrhage's duration and potency to it, consuming hemorrhage in the process
     */
    public ChickenSickle() {
        super(2, 1.6, 6, 1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("chickensickle.normal"));
        tooltip.add(I18n.format("chickensickle.detonate"));
        tooltip.add(I18n.format("chickensickle.debuff"));
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        final PotionEffect hemorrhage = target.getActivePotionEffect(TaoPotion.HEMORRHAGE);
        return hemorrhage != null && hemorrhage.getAmplifier() * 4 >= target.getTotalArmorValue() ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        PotionEffect hemorrhage = TaoPotionUtils.stackPot(target, new PotionEffect(TaoPotion.HEMORRHAGE, 100, 0), TaoPotionUtils.POTSTACKINGMETHOD.ADD);
        if (!TaoPotionUtils.attemptAddPot(target, hemorrhage, false) || hemorrhage.getAmplifier() * 4 >= target.getTotalArmorValue()) {//isCharged(attacker,stack)
            target.hurtResistantTime = 0;
            target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), Math.min(target.getMaxHealth() / (20 - 2 * (hemorrhage.getAmplifier())), 2 * (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            TaoPotionUtils.forceBleed(target, attacker, hemorrhage.getDuration(), hemorrhage.getAmplifier(), TaoPotionUtils.POTSTACKINGMETHOD.ADD);
        }
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
        return 3;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }
}
