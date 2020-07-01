package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
    Charge special: next attack in 1 second add resistance I to self for the amount of time elapsed since parry, immune to stagger for 2 sec
     */

    private final PartDefinition[] parts = {
            StaticRefs.HANDLE
    };

    public Tonfa() {
        super(0, 1.8, 5f, 1.3f);
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
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
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
    public void onBlock(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
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
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.RESISTANCE, getChargedTime(attacker, stack)), false);
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(TaoPotion.RESOLUTION, 40), false);

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
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }
}
