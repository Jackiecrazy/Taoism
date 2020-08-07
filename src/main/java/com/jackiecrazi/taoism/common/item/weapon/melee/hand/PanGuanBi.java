package com.jackiecrazi.taoism.common.item.weapon.melee.hand;

import com.jackiecrazi.taoism.api.BinaryMachiavelli;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class PanGuanBi extends TaoWeapon {
    /*
    a pen that pokes pressure points to drain posture and hp
    effect compilation: burst, chip, tank, cc
    three states: standing/dodging, airborne, sneaking.
    bursts every four hits, effect on burst depends on the detonating hit:
    standing (-1): bind and root for 2 seconds
    airborne (-2): 3 bleed damage and bleed 1/3
    sneaking (-3): 5 posture and fatigue 1/3
    by varying the previous three hits it's possible to extend these effects up to twice their original effectiveness
     */

    public PanGuanBi() {
        super(2, 1, 5f, 0.1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.4f;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("panguanbi.switch"));
        tooltip.add(I18n.format("panguanbi.lunge"));
        tooltip.add(I18n.format("panguanbi.detonation"));
        tooltip.add(I18n.format("panguanbi.sneak"));
        tooltip.add(I18n.format("panguanbi.normal"));
        tooltip.add(I18n.format("panguanbi.jump"));
        tooltip.add(I18n.format("panguanbi.variation"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 3;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EnumHand hand = elb.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand, 1, true);
        }
        setBuff(elb, stack, "hits", 0);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getBuff(item, "hits") == 3 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (getLastAttackedEntity(attacker.world, stack) != target) {
            setBuff(attacker, stack, "pressure", 0);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        super.applyEffects(stack, target, attacker, chi);
        setLastAttackedRangeSq(attacker, stack, (float) NeedyLittleThings.getDistSqCompensated(attacker, target));
        int hits = getBuff(stack, "hits");
        int setTo = getBuff(stack, "pressure");
        if (hits == 3) {//detonate
            int potency = 0;
            for (int a = 0; a < 16; a++)
                if (BinaryMachiavelli.getBoolean(setTo, a)) potency++;
            if (attacker.isSneaking()) {//sneaking, 5 posture and exhaust 1/3
                TaoCasterData.getTaoCap(target).consumePosture(4 * potency, true);
                TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.EXHAUSTION, 60 * potency, 0), false);
            } else if (!attacker.onGround) {//airborne, 5 bleed damage and bleed 1/3
                target.hurtResistantTime = 0;
                target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), 4 * potency);
                TaoPotionUtils.forceBleed(target, attacker, 60 * potency, 0, TaoPotionUtils.POTSTACKINGMETHOD.ONLYADD);
            } else {//standing, bind and root for 1.5 seconds
                TaoCasterData.getTaoCap(target).setRootTime(30 * potency);
                TaoCasterData.getTaoCap(target).setBindTime(30 * potency);
            }
            setTo = 0;
        } else if (attacker.isSneaking()) {
            setTo |= 2;
        } else if (!attacker.onGround) {
            setTo |= 4;
        } else setTo |= 1;
        setBuff(attacker, stack, "pressure", setTo);
        setBuff(attacker, stack, "hits", (hits + 1) % 4);
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        if (getLastAttackedRangeSq(is) > 4) {
            setLastAttackedRangeSq(elb, is, 0);
            return 0;
        }
        return 0.5f;
    }

    @Override
    public long lastAttackTime(EntityLivingBase elb, ItemStack is) {
        return elb.world.getTotalWorldTime();
    }
}
