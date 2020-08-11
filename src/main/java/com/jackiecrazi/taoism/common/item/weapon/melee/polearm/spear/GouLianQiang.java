package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherItem;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.physics.EntityOrbitDummy;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
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

public class GouLianQiang extends TaoWeapon implements ITetherItem {
    /*
    A spear weapon that specializes in hooking and grabbing. High range and speed, medium combo and defense, low power
    Reference: a guisarme can be used to catch incoming blades
    A guisarme can hook down an overhead parry for a stab
    A goulian qiang has a blunt hook used for pulling. It's mainly used to pull people off horses, trip horses or pull horses off people... wait.
    I imagine the cavity would lead to great catching of blades.
    Anyway this trips people and counters parries.

    left click for a normal stab. Like the misericorde, this ignores all armor when hitting a downed target
    right click to hook for no damage. Doing this twice in a row will trip, inflicting 50% max posture damage
    if the target is not facing the attacker, instantly trip
    if the target is unaware of attacker, instantly down

    Execution:
    grab enemy with hook and slam them to the other side, using the reaction force to leap
     then follow up with a heart stab that deals a *lot* of damage
     */

    public GouLianQiang() {
        super(2, 1.4, 6d, 1f);
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
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase) {
            updateTetheringVelocity(stack, (EntityLivingBase) e);
            if (isCharged((EntityLivingBase) e, stack) && getBuff(stack, "heartStab") > 0 && e.motionY < 0)
                e.motionY = -0.5;
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("goulianqiang.trip"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("goulianqiang.trip.sneaky") + TextFormatting.RESET);
        tooltip.add(I18n.format("goulianqiang.bash"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
//        if (isCharged(elb, stack) && getBuff(stack, "heartStab") > 0) {
//            TaoCombatUtils.attack(elb, collidingEntity, EnumHand.MAIN_HAND);
//            return true;
//        }
        return false;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && isCharged(attacker, stack))
            splash(attacker, stack, 120);
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 1 : 2;
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        setBuff(attacker, item, "flipOverID", 0);
        setBuff(attacker, item, "heartStab", 0);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        setBuff(elb, item, "flipOverID", 0);
        TaoCasterData.getTaoCap(elb).consumeQi(4, 5);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.OFF_HAND && !isCharged(attacker, item) ? 0.1f : 1f;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack) && getBuff(stack, "flipOverID") != 0) {
            ds.setDamageIsAbsolute().setDamageBypassesArmor();
            return orig * 10;
        }
        return super.hurtStart(ds, attacker, target, stack, orig);
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack)) return target.getTotalArmorValue();
        if (getHand(stack) == EnumHand.MAIN_HAND && (TaoCasterData.getTaoCap(target).getDownTimer() > 0)) {
            //ignore half armor when downed
            return target.getTotalArmorValue() / 2;
        }
        return super.armorIgnoreAmount(ds, attacker, target, stack, orig);
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        attacker.motionY += 2;
        //TaoCasterData.getTaoCap(attacker).toggleCombatMode(false);
        //attacker.motionZ += (target.posZ - attacker.posZ) * 0.1;
        //attacker.motionX += (target.posX - attacker.posX) * 0.1;
        //attacker.posZ=target.posZ;
        //attacker.posX=target.posX;
        attacker.velocityChanged = true;
        setBuff(attacker, item, "heartStab", 1);
        TaoCasterData.getTaoCap(target).consumePosture(TaoCasterData.getTaoCap(target).getMaxPosture(), true, true, attacker);
        return 0;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack) && !attacker.world.isRemote) {
            //begin the flip sequence
            if (getBuff(stack, "flipOverID") == 0) {
                setBuff(attacker, stack, "flipOverID", target.getEntityId());
                TaoCasterData.getTaoCap(attacker).startRecordingDamage();
                TaoCasterData.getTaoCap(target).startRecordingDamage();
                EntityOrbitDummy epd = new EntityOrbitDummy(attacker.world, attacker, target);
                //epd.shoot(attacker, 0, 0, 0.0F, 0.5f, 0.0F);
                //epd.setPositionAndUpdate(target.posX, target.posY, target.posZ);
                epd.motionY = 1;
                attacker.world.spawnEntity(epd);
                TaoCasterData.getTaoCap(attacker).setForcedLookAt(target);
            } else {
                setBuff(attacker, stack, "flipOverID", 0);
                TaoCasterData.getTaoCap(attacker).stopRecordingDamage(attacker);
                TaoCasterData.getTaoCap(attacker).setForcedLookAt(null);
//                for (Entity e : attacker.world.getEntitiesWithinAABBExcludingEntity(attacker, attacker.getEntityBoundingBox().grow(16))) {
//                    if (e != target) NeedyLittleThings.knockBack(e, attacker, 2, true);
//                }
                dischargeWeapon(attacker, stack);
            }
        }
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //main function. Check if previous move is also right click on the same target and trip if so
            if (!NeedyLittleThings.isFacingEntity(target, attacker, 90) || (!getLastMove(stack).isLeftClick() && getLastAttackedEntity(attacker.world, stack) == target) && getLastAttackedRangeSq(stack) != 0) {
                //we're going on a trip on our favourite hooked... ship?
                setLastAttackedRangeSq(attacker, stack, 0);
                TaoCasterData.getTaoCap(target).consumePosture((float) Math.min(TaoCasterData.getTaoCap(target).getMaxPosture() / 2d, getDamageAgainst(attacker, target, stack)) * 2, true, true, attacker);
            } else setLastAttackedRangeSq(attacker, stack, 1);
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        if (getHand(is) == EnumHand.OFF_HAND && TaoCombatUtils.getHandCoolDown(elb, EnumHand.MAIN_HAND) < 0.5f)//
            TaoCombatUtils.rechargeHand(elb, EnumHand.MAIN_HAND, 0.5f, true);
    }

    @Override
    public Entity getTetheringEntity(ItemStack stack, EntityLivingBase wielder) {
        return isCharged(wielder, stack) && getBuff(stack, "heartStab") != 0 ? wielder : null;
    }

    @Nullable
    @Override
    public Vec3d getTetheredOffset(ItemStack stack, EntityLivingBase wielder) {
        return null;//Vec3d.ZERO;
    }

    @Nullable
    @Override
    public Entity getTetheredEntity(ItemStack stack, EntityLivingBase wielder) {
        return wielder.world.getEntityByID(getBuff(stack, "flipOverID"));
    }

    @Override
    public double getTetherLength(ItemStack stack) {
        return 1;
    }
}
