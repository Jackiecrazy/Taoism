package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Horseback spear! High power and range, medium defense, low combo and speed
 * two handed unless you're riding, in which case you couch it. You retain one-handedness for 3 seconds after dismounting to properly end downed targets
 * damage and posture multiplier based on the speed at which you're going, you are slightly slowed for each successful attack
 * above certain levels of speed attacks are automatic with the above restriction
 * right click has no range. Instead it cancels iframes from dodging so you can attack.
 */
public class Lance extends TaoWeapon {
    public Lance() {
        super(2, 1.3, 5, 1);
    }

    private static float chargeAndLookCos(Entity attacker) {
        return MathHelper.cos((float) MathHelper.atan2(attacker.rotationYaw - attacker.prevRotationYaw, attacker.rotationPitch - attacker.prevRotationPitch));
//        float ret;
//        if (attacker.isRiding()) {
//            Vec3d vel = NeedyLittleThings.getVelVec(attacker);
//            Vec3d look = attacker.getLook(1);
//            //System.out.println("vel "+vel);
//            //System.out.println("look "+look);
//            ret = (float) (vel.dotProduct(look) / (MathHelper.sqrt(vel.lengthSquared() * look.lengthSquared())));
//        } else
//            ret = (float) (NeedyLittleThings.getVelVec(attacker).dotProduct(attacker.getLookVec()) / (MathHelper.sqrt(NeedyLittleThings.getVelVec(attacker).lengthSquared() * attacker.getLook(1).lengthSquared())));
//        if (Float.isNaN(ret)) return 0;
//        return ret;
    }

    @Override
    public float postureDealtBase(@Nullable EntityLivingBase attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        if (attacker == null) return super.postureDealtBase(attacker, defender, item, amount);
        return super.postureDealtBase(attacker, defender, item, amount) * (1 + (getBuff(item, "charge") / 20f) * chargeAndLookCos(attacker));
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (!w.isRemote && e instanceof EntityLivingBase) {
            if (e.isRiding()) {
                setBuff((EntityLivingBase) e, stack, "couch", 1);
            } else if (getBuff(stack, "couch") == 1) {
                //dismount grace period
                final int dismountTime = getBuff(stack, "dismountTime");
                if (dismountTime == 0) {
                    gettagfast(stack).setLong("dismountTime", w.getTotalWorldTime());
                } else if (dismountTime + 60 < w.getTotalWorldTime()) {
                    setBuff((EntityLivingBase) e, stack, "dismountTime", 0);
                    setBuff((EntityLivingBase) e, stack, "couch", 0);
                }
            }
            //System.out.println(chargeAndLookCos(e));
            if ((e.isRiding()) && ((EntityLivingBase) e).moveForward != 0 && chargeAndLookCos(e) > 0.86) {
                setBuff((EntityLivingBase) e, stack, "charge", getBuff(stack, "charge") + 1);
                if (getBuff(stack, "charge") > 30) {
                    Vec3d vel = e.getLook(1).normalize().scale(getReach((EntityLivingBase) e, stack));
                    //System.out.println(vel);
                    splash((EntityLivingBase) e, e.getRidingEntity(), stack, 360, 360, ((EntityLivingBase) e).world.getEntitiesInAABBexcluding(null, (e.getRidingEntity() != null ? e.getRidingEntity() : e).getEntityBoundingBox().expand(vel.x, vel.y, vel.z), TaoCombatUtils.VALID_TARGETS::test));
                }
            } else if (NeedyLittleThings.getSpeedSq(e) > 1 && !TaoMovementUtils.isDodging((EntityLivingBase) e) && chargeAndLookCos(e) > 0.86) {
                splash((EntityLivingBase) e, stack, 20);
            }else setBuff((EntityLivingBase) e, stack, "charge", 0);
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if (isDummy(stack)) {
            if (TaoCombatUtils.getCooledAttackStrengthOff(elb, 1) > 0.9)
                TaoMovementUtils.attemptSlide(elb);
            TaoCasterData.getTaoCap(elb).setRollCounter(CombatConfig.rollThreshold);
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    protected double speed(ItemStack stack) {
        if (isDummy(stack)) return -3.5;
        return super.speed(stack);
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1 + MathHelper.clamp(getBuff(item, "charge") / 20f, 0, 1) * chargeAndLookCos(attacker);
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig * ((1 + getBuff(stack, "charge") / 20f) * chargeAndLookCos(attacker));
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return getBuff(is, "couch") == 0;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED+ I18n.format("lance.hands")+TextFormatting.RESET);
        tooltip.add(I18n.format("lance.mount"));
        tooltip.add(I18n.format("lance.joust"));
        tooltip.add(I18n.format("lance.alt"));
    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        setBuff(attacker, stack, "charge", Math.min(getBuff(stack, "charge"), 15));
        return super.damageStart(ds, attacker, target, stack, orig);
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        if (isDummy(is)) return 0;
        return 6;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1;
    }
}
