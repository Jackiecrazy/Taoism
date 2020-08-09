package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherItem;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Qiang extends TaoWeapon implements ITetherItem {
    /*
    First two handed weapon!
    using 游场枪 tactics.
    A spear used for dueling, short enough to use staff moves. High reach and speed, medium power and combo, low defense
    left click for a narrow, but long normal stab. Damage scales based on distance, up to double at front 1/3
    right click for a swipe on the ground, or a top-down bonk when airborne, at 30% damage.
    highly dependent on footwork and facing your opponent.
    Primary method of avoidance is side dodge/jump/slide, then close in distance
    TODO holding some type of spear upward allows you to throw it like a javelin. Prepare sidearm.

    Execution:
    Normal attack cuts, alt kicks, thrice in 2-tick intervals
    normal knocks target upwards, alt vaults to target
    upon posture break, less than 20% health or state ending, throw target far away,
    then follow up with a heart stab that deals remaining hp damage
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE,
            new PartDefinition("tassel", StaticRefs.STRING)
    };

    public Qiang() {
        super(2, 1.4, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.4f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected double speed(ItemStack stack) {
        return (1d + (getHand(stack) == EnumHand.MAIN_HAND ? Math.sqrt(getLastAttackedRangeSq(stack)) / 6f : 0)) - 4d;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.stab"));
        tooltip.add(I18n.format("qiang.recharge"));
        tooltip.add(I18n.format("qiang.bash"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (!isCharged(attacker, stack) && getHand(stack) == EnumHand.OFF_HAND && getLastAttackedRangeSq(stack) != 0f) {
            splash(attacker, stack, 120);
            setLastAttackedRangeSq(attacker, attacker.getHeldItemMainhand(), 0);
        }
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 1 : 2;
    }

    @Override
    protected void performScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, long l, int interval) {
        if (getBuff(stack, "chargeHitHand") > 0) {//main
            TaoCombatUtils.attack(elb, victim, EnumHand.MAIN_HAND);
            if (l == 4) {
                NeedyLittleThings.knockBack(victim, elb, 1, true);
                victim.motionY += 0.1;
            }
        } else if (victim instanceof EntityLivingBase) {
            TaoMovementUtils.kick(elb, (EntityLivingBase) victim);
            if (l == 4)
                NeedyLittleThings.knockBack(elb, victim, -1, true);
        }
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.OFF_HAND ? 0.5f : 1 + (float) NeedyLittleThings.getDistSqCompensated(attacker, target) / 54f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if(isCharged(attacker, stack))return 0;
        if (getHand(stack) == EnumHand.OFF_HAND) return orig * 2;
        return super.knockback(attacker, target, stack, orig);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        }
        if (isCharged(attacker, stack)) {
            if (getHand(stack) == EnumHand.OFF_HAND) {
                TaoMovementUtils.kick(attacker, target);
                NeedyLittleThings.knockBack(target, attacker, 1, true);
            }
            if(TaoCasterData.getTaoCap(attacker).consumeQi(1, 5)) {
                setBuff(attacker, stack, "chargeHitHand", getHand(stack) == EnumHand.MAIN_HAND ? 1 : -1);
                scheduleExtraAction(attacker, stack, target, 4, 2);
            }else{
                //end with a massive slam
                setBuff(attacker, stack, "trackEntity", target.getEntityId());
            }
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f, true);
    }

    @Override
    public Entity getTetheringEntity(ItemStack stack, @Nullable EntityLivingBase wielder) {
        return isCharged(wielder, stack) ? wielder : null;
    }

    @Nullable
    @Override
    public Vec3d getTetheredOffset(ItemStack stack, @Nullable EntityLivingBase wielder) {
        return Vec3d.ZERO;
    }

    @Nullable
    @Override
    public Entity getTetheredEntity(ItemStack stack, @Nullable EntityLivingBase wielder) {
        return wielder.world.getEntityByID(getBuff(stack, "trackEntity"));
    }

    @Override
    public double getTetherLength(ItemStack stack) {
        return 3;
    }
}
