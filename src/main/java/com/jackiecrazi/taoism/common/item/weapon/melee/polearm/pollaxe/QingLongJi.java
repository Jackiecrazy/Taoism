package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe;

import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class QingLongJi extends TaoWeapon {
    /*
     * A polearm capable of continuous attack. High range and combo, medium defense and power, low speed
     * Two-handed, range 6, attack speed 1.4(+chi/2)
     * Normal attack is a simple stab that inflicts (1.5+chi/10)x damage.
     * Alt will switch sides, dealing splash 3 (+chi/5) in the process,
     * and inflicting slow 1/(chi/10) if on blunt end,
     * and cutting damage with bleed 1/(chi/5) if on sharp end
     * If idle for over 5 seconds or switched out, it'll revert to main end.
     * Notably, only alternating attacks will accumulate chi, and chi is directly tied to attack speed.
     * Riposte: instantly gain two layers of chi.
     *
     * execution: soryuha! leap into the air and levitate
     * Offhand attacks have massive reach and collect mobs in front of you
     * Each collecting hit consumes 0.5 qi
     * below 5 qi, or on left click, perform a high damage pierce that drives them into the ground
     */
    public QingLongJi() {
        super(2, 1.2, 5.5d, 1f);
        this.addPropertyOverride(new ResourceLocation("invert"), (stack, world, ent) -> isReversed(stack) ? 1 : 0);
    }

    private boolean isReversed(ItemStack is) {
        return gettagfast(is).getBoolean("longForm");
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
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 4f;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) splash(attacker, stack, 60 + chi * 6);
    }

    protected double speed(ItemStack stack) {
        return (1.4d + (getQiFromStack(stack) / 20d)) - 4d;
    }

    protected float getQiAccumulationRate(ItemStack is) {
        MoveCode mc = getLastMove(is);
        if (!mc.isValid()) return super.getQiAccumulationRate(is);
        boolean lastIsNormalAtk = mc.isLeftClick();
        boolean onMainhand = getHand(is) == EnumHand.MAIN_HAND;
        if (lastIsNormalAtk ^ onMainhand) {
            return super.getQiAccumulationRate(is);
        } else return 0f;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("qinglongji.stab"));
        tooltip.add(I18n.format("qinglongji.alt"));
        tooltip.add(I18n.format("qinglongji.alt.bash"));
        tooltip.add(I18n.format("qinglongji.alt.cut"));
        tooltip.add(I18n.format("qinglongji.oscillate"));
        tooltip.add(I18n.format("qinglongji.atkspd"));
        tooltip.add(TextFormatting.YELLOW + I18n.format("qinglongji.qi") + TextFormatting.RESET);
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? isReversed(is) ? 1 : 0 : 2;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return isReversed(item) ^ getHand(item) == EnumHand.MAIN_HAND ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float chimult = TaoCasterData.getTaoCap(attacker).getQiFloored() / 10f;
        return getHand(item) == EnumHand.OFF_HAND ? 1f : 1.5f * (1 + chimult);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 0.3f);
            if (isReversed(stack)) {
                //crescent cut!
                TaoPotionUtils.forceBleed(target, attacker, chi * 4, 0, TaoPotionUtils.POTSTACKINGMETHOD.ADD);
            } else {
                //butt smash!
                TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, chi * 2, 0), false);
            }
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        if (!attacker.world.isRemote && getHand(stack) == EnumHand.OFF_HAND) {
            if (isReversed(stack)) {
                setReversed(stack, false);
            } else setReversed(stack, true);
        }
        EnumHand other = getHand(stack) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(attacker, other, TaoCombatUtils.getHandCoolDown(attacker, other) * 0.5f, true);
    }

    private void setReversed(ItemStack is, boolean to) {
        gettagfast(is).setBoolean("longForm", to);
    }
}
