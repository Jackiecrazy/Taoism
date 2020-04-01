package com.jackiecrazi.taoism.common.item.weapon.melee.stick;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
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
        super(0, 1.8, 3f, 1.3f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tonfa.main"));
        tooltip.add(I18n.format("tonfa.off"));
        tooltip.add(I18n.format("tonfa.parry"));
        tooltip.add(I18n.format("tonfa.reset"));
        tooltip.add(I18n.format("tonfa.defbreak"));
        tooltip.add(I18n.format("tonfa.riposte"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return 0f;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        TaoCasterData.getTaoCap(attacker).addQi(1f);
        if (attacker.getHeldItemMainhand().getItem() instanceof IChargeableWeapon) {
            IChargeableWeapon icw = (IChargeableWeapon) attacker.getHeldItemMainhand().getItem();
            icw.chargeWeapon(attacker, defender, attacker.getHeldItemMainhand(), icw.getMaxChargeTime());
        }
        if (attacker.getHeldItemOffhand().getItem() instanceof IChargeableWeapon) {
            IChargeableWeapon icw = ((IChargeableWeapon) attacker.getHeldItemOffhand().getItem());
            icw.chargeWeapon(attacker, defender, attacker.getHeldItemOffhand(), icw.getMaxChargeTime());
        }
        int qi = TaoCasterData.getTaoCap(defender).getQiFloored();
        if (qi >= 3) {
            Taoism.setAtk(defender, 0);
        }
        if (qi >= 7) {
            defender.addPotionEffect(new PotionEffect(TaoPotion.ARMORBREAK, 100, qi - 7));
            defender.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, qi - 7));
        }
    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    @Override
    public void onBlock(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        int qi = TaoCasterData.getTaoCap(defender).getQiFloored();
        if (qi >= 3) {//reset cooldown
            Taoism.setAtk(defender, 0);
        }
        if (qi >= 7) {
            defender.addPotionEffect(new PotionEffect(TaoPotion.ARMORBREAK, 100, qi - 7));
        }
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (off) {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20));
        }
        if (isCharged(attacker, stack)) {
            attacker.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, getChargeTimeLeft(attacker, stack)));
            attacker.addPotionEffect(new PotionEffect(TaoPotion.RESOLUTION, 40));

        }
    }

    @Override
    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (!off)
            multiHit(attacker, target, 1, 1);
    }
}
