package com.jackiecrazi.taoism.common.item.weapon.melee.pick;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
        super(2, 1.6, 4, 1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("chickensickle.normal"));
        tooltip.add(I18n.format("chickensickle.detonate"));
        tooltip.add(I18n.format("chickensickle.debuff"));
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        if (isCharged(attacker, item)) {
            float percent = target.getHealth() / target.getMaxHealth();
            PotionEffect amputate = TaoPotionUtils.stackPot(target, new PotionEffect(TaoPotion.AMPUTATION, 1000, 0), TaoPotionUtils.POTSTACKINGMETHOD.MAXDURATION);
            TaoPotionUtils.attemptAddPot(target, amputate, true);
            TaoCasterData.getTaoCap(target).setMustDropHead(true);
            target.setHealth(percent * target.getMaxHealth());
        }
        if (getCombo(attacker, item) == 4) {
            return Event.Result.ALLOW;
        }
        final PotionEffect hemorrhage = target.getActivePotionEffect(TaoPotion.HEMORRHAGE);
        return hemorrhage != null && hemorrhage.getAmplifier() * 4 >= target.getTotalArmorValue() ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (isCharged(attacker, item) && getCombo(attacker, item) == 4) {
            dischargeWeapon(attacker, item);
            return 3;
        }
        return 1;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        PotionEffect hemorrhage = TaoPotionUtils.stackPot(target, new PotionEffect(TaoPotion.HEMORRHAGE, 100, 0), TaoPotionUtils.POTSTACKINGMETHOD.ADD);
        if (hemorrhage.getAmplifier() * 4 >= target.getTotalArmorValue() || !TaoPotionUtils.attemptAddPot(target, hemorrhage, false)) {//isCharged(attacker,stack)
            target.hurtResistantTime = 0;
            target.removeActivePotionEffect(TaoPotion.HEMORRHAGE);
            target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), Math.min(target.getMaxHealth() / (20 - 2 * (hemorrhage.getAmplifier())), 2 * (float) getDamageAgainst(attacker, target, stack)));
            TaoPotionUtils.forceBleed(target, attacker, hemorrhage.getDuration(), hemorrhage.getAmplifier(), TaoPotionUtils.POTSTACKINGMETHOD.MAXDURATION);
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        //last guy didn't make it
        if (!elb.world.isRemote)
            if (getLastAttackedEntity(elb.world, is) == null || getLastAttackedEntity(elb.world, is).isDead || elb.getLastAttackedEntity() == null || !elb.getLastAttackedEntity().isEntityAlive()) {
                dischargeWeapon(elb, is);
                setCombo(elb, is, -1);//deception 100
            }
        super.afterSwing(elb, is);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        if (isCharged(wielder, is)) return 5;
        return 1;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 3;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }
}
