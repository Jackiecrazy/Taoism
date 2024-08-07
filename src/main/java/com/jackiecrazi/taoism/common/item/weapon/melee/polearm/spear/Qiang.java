package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherItem;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.physics.EntityBaseball;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

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
        super(2, 1.4, 6d, 2.5f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase) e;
            updateTetheringVelocity(stack, elb);
            final Entity last = getLastAttackedEntity(w, stack);
            if (isCharged(elb, stack)) {
                if ((TaoCasterData.getTaoCap(elb).getQi() < 5) && last == null) dischargeWeapon(elb, stack);
                if (last != null && getBuff(stack, "kill") == 0) {
                    last.motionY = 0.05;
                    last.velocityChanged = true;
                }
            }
        }
    }

    protected double speed(ItemStack stack) {
        if (isCharged(null, stack)) return -2;
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
        if (isCharged(p, is)) return 16;
        return 6f;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        if (isCharged(elb, stack) && getBuff(stack, "heartStab") > 0) {
            TaoCombatUtils.attack(elb, collidingEntity, EnumHand.MAIN_HAND);
            return true;
        }
        return false;
    }

    public void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
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
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        setBuff(attacker, item, "chargeHitHand", 0);
        setBuff(attacker, item, "heartStab", -1);
        setBuff(attacker, item, "kill", 0);
        setLastAttackedEntity(item, null);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        setBuff(elb, item, "chargeHitHand", 0);
        setBuff(elb, item, "heartStab", -1);
        setBuff(elb, item, "kill", 0);
        TaoCasterData.getTaoCap(elb).setForcedLookAt(null);
    }

    @Override
    protected void performScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, long l, int interval) {
        if (interval % 2 == 0)
            if (getBuff(stack, "chargeHitHand") > 0) {//main
                TaoCombatUtils.attack(elb, victim, EnumHand.MAIN_HAND);
                if (interval == 4)
                    NeedyLittleThings.knockBack(victim, elb, 0.01f, false, true);
            } else if (victim instanceof EntityLivingBase) {
                TaoMovementUtils.kick(elb, (EntityLivingBase) victim);
                //NeedyLittleThings.knockBack(elb, victim, -0.2f, false, true);
            }
        elb.motionY = 0.2;
        elb.velocityChanged = true;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return isCharged(attacker, item) && getBuff(item, "heartStab") > 0 ? Event.Result.ALLOW : Event.Result.DEFAULT;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (isCharged(attacker, item) && getBuff(item, "heartStab") > 0) {
            dischargeWeapon(attacker, item);
            return 5;
        }
        return 1.5f;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (isCharged(attacker, item) && getBuff(item, "heartStab") <= 0) return 1f;
        return getHand(item) == EnumHand.OFF_HAND ? 0.5f : 1 + (float) NeedyLittleThings.getDistSqCompensated(attacker, target) / 54f;
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack)) return 0;
        if (getHand(stack) == EnumHand.OFF_HAND) return orig * 2;
        return super.onKnockingBack(attacker, target, stack, orig);
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        setBuff(attacker, item, "heartStab", target.getEntityId());
        return super.onStoppedRecording(ds, attacker, target, item, orig);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        }
        if (isCharged(attacker, stack)) {
            if (!TaoCasterData.getTaoCap(target).isRecordingDamage())
                TaoCasterData.getTaoCap(target).startRecordingDamage();
            TaoCasterData.getTaoCap(attacker).setForcedLookAt(target);
            if (getHand(stack) == EnumHand.OFF_HAND) {
                TaoMovementUtils.kick(attacker, target);
                NeedyLittleThings.knockBack(target, attacker, 0.02f, true, true);
            }
        }
    }

    @Override
    protected void followUp(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack))
            if (TaoCasterData.getTaoCap(target).getRecordedDamage() < target.getHealth() && TaoCasterData.getTaoCap(attacker).consumeQi(0.5f, 5)) {
                setBuff(attacker, stack, "chargeHitHand", getHand(stack) == EnumHand.MAIN_HAND ? 1 : -1);
                scheduleExtraAction(attacker, stack, target, 4, 2);
            } else {
                //end with a massive slam
                setBuff(attacker, stack, "kill", 1);
                EntityBaseball epd = new EntityBaseball(attacker.world, attacker, target);
                epd.setPosition(target.posX, target.posY, target.posZ);
                epd.shoot(attacker, 90, attacker.rotationYaw, 0.0F, 1.5f, 0.0F);
                attacker.world.spawnEntity(epd);
                attacker.motionY = 1;
            }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f, true);
    }

    @Override
    public Entity getTetheringEntity(ItemStack stack, EntityLivingBase wielder) {
        return isCharged(wielder, stack) && getLastAttackedEntity(wielder.world, stack) != null ? wielder : null;
    }

    @Nullable
    @Override
    public Vec3d getTetheredOffset(ItemStack stack, EntityLivingBase wielder) {
        return null;//Vec3d.ZERO;
    }

    @Nullable
    @Override
    public Entity getTetheredEntity(ItemStack stack, EntityLivingBase wielder) {
        return getLastAttackedEntity(wielder.world, stack);
    }

    @Override
    public double getTetherLength(ItemStack stack) {
        return getBuff(stack, "heartStab") > 0 ? 1 : 5;
    }

    @Override
    public boolean shouldRepel(ItemStack stack) {
        return true;
    }
}
