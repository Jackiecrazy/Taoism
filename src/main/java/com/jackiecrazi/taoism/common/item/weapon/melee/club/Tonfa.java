package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Tonfa extends TaoWeapon {
    /*
    Defensive punching weapon for quick counters. High defense and combo, decent power and speed, low range
    Normal attack is an auto 2-hit combo in the mainhand, and a heavy punch that inflicts brief weakness and slowness in the offhand
    Chi increases by 1 per parry. Parry charges apply to both hands.
    At 3 chi and above, both blocks and parries reset attack cooldown;
    at 7 chi, defense break is inflicted on block and fatigue is added on a parry
    Execution: 20 seconds of batman
    attacks temporarily knock down, parries become omnidirectional, free, and projectile-stopping, and you attack anyone you collide into.
    Net effect should be you using parries to bounce between multiple enemies, slowly wearing them down to KO, like arkham asylum
     */

    private final PartDefinition[] parts = {
            StaticRefs.HANDLE
    };

    public Tonfa() {
        super(0, 1.8, 5f, 1f);
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && onHand && isCharged((EntityLivingBase) e, stack) && getHand(stack) == EnumHand.MAIN_HAND) {
            EntityLivingBase elb = (EntityLivingBase) e;
            Entity target = TaoMovementUtils.collidingEntity(elb);
            if (target != null && getLastAttackedEntity(w, stack) != target) {
                TaoCombatUtils.attack(elb, target, EnumHand.MAIN_HAND);
                TaoCombatUtils.attack(elb, target, EnumHand.OFF_HAND);
            }
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tonfa.main"));
        tooltip.add(I18n.format("tonfa.off"));
        tooltip.add(I18n.format("tonfa.parry"));
        tooltip.add(I18n.format("tonfa.reset"));
        tooltip.add(I18n.format("tonfa.defbreak"));
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item, int ticks) {
        super.chargeWeapon(attacker, item, ticks);
        attacker.addPotionEffect(new PotionEffect(TaoPotion.RESOLUTION, 400));
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item) {
        if (isCharged(defender, item)) return true;
        return super.canBlock(defender, attacker, item);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        TaoCasterData.getTaoCap(defender).addQi(0.3f);
        int qi = TaoCasterData.getTaoCap(attacker).getQiFloored();
        if (qi >= 3) {
            Taoism.setAtk(attacker, 0);
        }
        if (qi >= 7) {
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(TaoPotion.ARMORBREAK, 100, qi - 7), false);
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.MINING_FATIGUE, 100, qi - 7), false);
        }
    }

    @Override
    public void onOtherHandParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        int qi = TaoCasterData.getTaoCap(defender).getQiFloored();
        if (qi >= 3) {//reset cooldown
            Taoism.setAtk(attacker, 0);
        }
        if (qi >= 7) {
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(TaoPotion.ARMORBREAK, 100, qi - 7), false);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, 20), false);
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.WEAKNESS, 20), false);
        }
        if (isCharged(attacker, stack)) {
            TaoCasterData.getTaoCap(target).setDownTimer(10);
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.ENFEEBLE, 20, 4), false);
        }
    }

    @Override
    protected void queueExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND)
            multiHit(attacker, stack, target, 3, 3);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (isCharged(defender, item)) return 2f;
        return 0.7f;
    }
}
