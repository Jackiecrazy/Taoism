package com.jackiecrazi.taoism.common.item.weapon.melee.hand;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Emeici extends TaoWeapon {
    /*
    A spinning weapon used to attack pressure points. High speed, medium power and combo, low defense and range
    Keywords: spin, pressure points
    If the current target's relative facing to you is different from the last (4 cardinal directions):
        deal crit damage. Drain equivalent posture.
     */

    private static final boolean[] harvestList = {false, false, false, true};

    public Emeici() {
        super(1, 1, 5f, 1.3f);
    }


    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("emeici.switch"));
        tooltip.add(I18n.format("emeici.lunge"));
        tooltip.add(I18n.format("emeici.spin"));
        tooltip.add(I18n.format("emeici.crit"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 3;
    }

    @Override
    public float postureDealtBase(@Nullable EntityLivingBase attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        if (getBuff(item, "crit") == 1) {
            return 4;
        }
        return 1.3f;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        setBuff(elb, stack, "facing", -1);
        if (elb instanceof EntityPlayer) {
            EnumHand hand = elb.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand, 1, true);
        }
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (getLastAttackedEntity(attacker.world, stack) != target) {
            setBuff(attacker, stack, "facing", -1);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        if (getBuff(item, "facing") > 0 && getBuff(item, "facing") != attacker.getAdjustedHorizontalFacing().getIndex()) {
            setBuff(attacker, item, "crit", 1);
            return Event.Result.ALLOW;
        }
        return Event.Result.DENY;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (getBuff(item, "crit") == 1) {
            return 999;
        }
        return 0;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1.5f;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        setLastAttackedRangeSq(attacker, stack, (float) NeedyLittleThings.getDistSqCompensated(attacker, target));
        setBuff(attacker, stack, "crit", 0);
        setBuff(attacker, stack, "facing", attacker.getAdjustedHorizontalFacing().getIndex());
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        if (getLastAttackedRangeSq(is) > 4) {
            setLastAttackedRangeSq(elb, is, 0);
            return 0;
        }
        return 0.5f;
    }

}
