package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Karambit extends TaoWeapon {
    //A slashing dagger that is fast and relentless, but short in reach.
    //has no switch in cooldown, and can be switched from hand to hand without cooldown as well.
    //backstabs deal 1.5x damage, inflicts a layer of bleed if target is unarmored.
    //Each chi level allows you to ignore 6 points of armor when inflicting bleed.
    //Can be used to harvest plant blocks, for what that's worth.

    /*
    common rework feature: attack on collide when dodging

    //rework 1: as you progress in qi, more and more bleed is converted into damage
    so you start at like 1 damage and 6 seconds of bleed 1, and it stacks by max duration so you have a constantly decreasing bleed counter on them
    but every other qi level (13579) it converts 1 second of bleed to 1 damage
    that is, you can open up with a 6 second bleed, then at the end of it all you're dealing 6 damage and just a little bleed
    it's flipped on the offhand... maybe

    //rework 2: inverse qi scaling, bleed scales with damage
    you start at 6 damage, and at level 258 it decreases by 1
    duration of bleed inflicted is equal to damage dealt

    //rework 3: no rework, increase bleed duration to scale with qi up to 10 seconds, each stack resets timer
    //execution rework: all 5 hits are unloaded on a single enemy for a shower of blood, nearby enemies are afflicted with fear
     */

    private static final boolean[] harvestList = {false, false, false, true};

    public Karambit() {
        super(1, 2, 5f, 0);
        setQiAccumulationRate(0.35f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 2f;//not all that good at defense now is it...
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && isCharged((EntityLivingBase) e, stack)) {
            for (int ignored = 0; ignored < 3; ignored++)
                w.spawnParticle(EnumParticleTypes.END_ROD, e.posX - 4 + Taoism.unirand.nextFloat() * 8, e.posY - 4 + Taoism.unirand.nextFloat() * 8, e.posZ - 4 + Taoism.unirand.nextFloat() * 8, 0, -0.1f, 0);
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if (isCharged(elb, stack) && getHand(stack) == EnumHand.MAIN_HAND && !elb.world.isRemote) {
            RayTraceResult r = NeedyLittleThings.raytraceAnything(elb.world, elb, 16);
            Vec3d tpTo = NeedyLittleThings.getClosestAirSpot(elb.getPositionVector(), r.hitVec, elb);
            boolean shouldDischarge = gettagfast(stack).getInteger("flashesWithoutHit") > 3;
            gettagfast(stack).setInteger("flashesWithoutHit", gettagfast(stack).getInteger("flashesWithoutHit") + 1);
            if (getLastAttackedEntity(elb.world, stack) != null) {
                tpTo = NeedyLittleThings.getPointInFrontOf(r.entityHit, elb, -5);
                shouldDischarge = true;
            }
            for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                TaoCasterData.getTaoCap(e).setRootTime(0);
                TaoCasterData.getTaoCap(e).setBindTime(0);
            }
            TaoCasterData.getTaoCap(elb).setRootTime(0);
            elb.setPositionAndUpdate(tpTo.x, tpTo.y, tpTo.z);
            elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.PLAYERS, 1f, 0.5f + Taoism.unirand.nextFloat() / 2);
            TaoCasterData.getTaoCap(elb).setRootTime(400);
            if (shouldDischarge) {
                dischargeWeapon(elb, stack);
            } else {
                for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                    TaoCasterData.getTaoCap(e).setRootTime(200);
                    if (e != elb)
                        TaoCasterData.getTaoCap(e).setBindTime(200);
                }
            }
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    public double attackDamage(ItemStack stack) {
        return 3 + ((getQiFromStack(stack) - 1) / 2);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("karambit.switch"));
        tooltip.add(I18n.format("karambit.backstab"));
        tooltip.add(I18n.format("karambit.initiative"));
        tooltip.add(I18n.format("karambit.damage"));
        tooltip.add(I18n.format("karambit.bleed"));
        tooltip.add(I18n.format("karambit.duration"));
        tooltip.add(I18n.format("karambit.dash"));
        tooltip.add(I18n.format("karambit.harvest"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        if (isCharged(p, is)) return 16;
        return 2f;
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() < CombatConfig.rollThreshold) {
            TaoCombatUtils.attack(elb, collidingEntity, getHand(stack));
            return true;
        }
        return false;
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        for (EntityLivingBase e : attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(32))) {
            TaoCasterData.getTaoCap(e).setRootTime(400);
            if (e != attacker)
                TaoCasterData.getTaoCap(e).setBindTime(400);
        }
        TaoCasterData.getTaoCap(attacker).startRecordingDamage();
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        TaoCasterData.getTaoCap(elb).consumeQi(4, 5);
        TaoCasterData.getTaoCap(elb).setRootTime(0);
        TaoCasterData.getTaoCap(elb).stopRecordingDamage(elb);
        gettagfast(item).setInteger("flashesWithoutHit", 0);
        gettagfast(item).setInteger("flashes", 0);
    }

    @Override
    protected void performScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, long l, int interval) {
    }

    @Override
    protected void endScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, int interval) {
        if (victim instanceof EntityLivingBase) {
            //TODO aoe fear if enemy dies
            for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                if (e != elb)
                    TaoPotionUtils.fear(e, elb, (int) TaoCasterData.getTaoCap((EntityLivingBase) victim).getRecordedDamage() * 3);
            }
            TaoCasterData.getTaoCap((EntityLivingBase) victim).stopRecordingDamage(elb);
            for (int ignored = 0; ignored < 60; ignored++) {
                double x = victim.posX + (Taoism.unirand.nextFloat() - 0.5) * 10;
                double y = victim.posY + Taoism.unirand.nextFloat() * 5;
                double z = victim.posZ + (Taoism.unirand.nextFloat() - 0.5) * 10;
                victim.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 1, 0, 0);
            }
        }
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EnumHand hand = elb.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand, 1, true);
        }
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//        return oldStack.isEmpty() || super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
//    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return NeedyLittleThings.isBehindEntity(attacker, target, 90, 90) || isCharged(attacker, item) ? Event.Result.ALLOW : Event.Result.DENY;
    }

//    @Override
//    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
//        return 1 + (15f - attacker.world.getLight(attacker.getPosition())) / 15f;//light bonus
//    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (isCharged(attacker, stack)) {
            gettagfast(stack).setInteger("flashes", 1);
            TaoCasterData.getTaoCap(target).startRecordingDamage();
        }
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return super.hurtStart(ds, attacker, target, stack, orig) * (1 + (gettagfast(stack).getInteger("flashes") * 10f));
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if ((target.getLastDamageSource() == null || target.getLastDamageSource().getTrueSource() != attacker) || isCharged(attacker, stack)) {
            return target.getTotalArmorValue();
        }
        return super.armorIgnoreAmount(ds, attacker, target, stack, orig);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        TaoPotionUtils.forceBleed(target, attacker, (6 - (chi) / 2) * 20, (chi - 1) / 2, TaoPotionUtils.POTSTACKINGMETHOD.ONLYADD);
    }

    @Override
    protected void queueExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack)) {
            multiHit(attacker, stack, target, 20, 20);
        }
    }
}
