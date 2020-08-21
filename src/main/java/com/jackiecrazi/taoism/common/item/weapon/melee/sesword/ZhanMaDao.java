package com.jackiecrazi.taoism.common.item.weapon.melee.sesword;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class ZhanMaDao extends TaoWeapon {
    /**
     * large two-handed blade to counter cavalry. High power and defense, medium combo and range, low speed
     * Two handed!
     * Left and right click reset each other's attack bar to half
     * Left click is a high damage chop that additionally hits all entities riding or being ridden by the target
     * Right click is a 120 degree slash at half damage
     * Consecutive alternating attacks crit
     * <p>
     * execution: bakusaiga!
     * Heavy attack an opponent. The opponent's HP will now begin to decrease at an alarming rate.
     * Upon their death, this spreads all around EXCEPT to the mobs you've recently attacked
     */

    public ZhanMaDao() {
        super(3, 1.2, 6, 1);
    }

    @Override
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            splash(attacker, stack, 30, 120);
        } else splash(attacker, stack, 120, 30);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED+ I18n.format("weapon.hands")+TextFormatting.RESET);
        tooltip.add(I18n.format("zhanmadao.chop"));
        tooltip.add(I18n.format("zhanmadao.cleave"));
        tooltip.add(I18n.format("zhanmadao.crit"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public int getMaxChargeTime() {
        return 0;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 4;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getLastMove(item).isValid() && (getLastMove(item).isLeftClick() ^ getHand(item) == EnumHand.MAIN_HAND) ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return (target.isRiding() || target.isBeingRidden()) && getHand(item) == EnumHand.MAIN_HAND ? 1.3f : 1;
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        EnumHand other = getHand(stack) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(attacker, other, 0.5f, true);
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }
}
