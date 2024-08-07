package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
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

public class Balisong extends TaoWeapon {
    //A stabbing dagger that is fast and relentless, but short in reach. Can be flicked open and closed.
    //has no switch in cooldown, and can be switched from hand to hand without cooldown as well.
    //backstabs deal double damage.
    //has 2 stances: hammer and reverse.
    //combos up to 6 times, increasing every other chi level, if in hammer grip
    //pierces 1 point of armor every chi level in reverse grip
    //execution:gain flash dodges that damage anyone on the path for full attack effects and backstab,
    // each enemy killed lengthens duration or makes the attack more potent


    public Balisong() {
        super(2, 2, 5f, 1.3f);
        setQiAccumulationRate(0.35f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return getHand(is) == EnumHand.MAIN_HAND ? (getQiFromStack(is) / 2) + 1 : 1;
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
        if (isCharged(elb, stack) && getHand(stack) == EnumHand.MAIN_HAND) {
            RayTraceResult r = NeedyLittleThings.raytraceAnything(elb.world, elb, 16);
            Vec3d tpTo = NeedyLittleThings.getClosestAirSpot(elb.getPositionVector(), r.hitVec, elb);
            gettagfast(stack).setInteger("flashesWithoutHit", gettagfast(stack).getInteger("flashesWithoutHit") + 1);
            if (r.entityHit != null) {
                tpTo = NeedyLittleThings.getPointInFrontOf(r.entityHit, elb, -5);
                TaoCombatUtils.attack(elb, r.entityHit, EnumHand.MAIN_HAND);
                TaoCasterData.getTaoCap(elb).addQi(0.5f);
                gettagfast(stack).setInteger("flashesWithoutHit", 0);
            }
            for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                TaoCasterData.getTaoCap(e).setRootTime(0);
                TaoCasterData.getTaoCap(e).setBindTime(0);
            }
            elb.setPositionAndUpdate(tpTo.x, tpTo.y, tpTo.z);
            elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.PLAYERS, 1f, 0.5f + Taoism.unirand.nextFloat() / 2);
            if (gettagfast(stack).getInteger("flashesWithoutHit") > 3 || !TaoCasterData.getTaoCap(elb).consumeQi(1, 5)) {
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

    //    @Override
//    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
//        return 1 + (15 - attacker.world.getLight(attacker.getPosition())) / 15;
//    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("balisong.switch"));
        tooltip.add(I18n.format("balisong.backstab"));
        tooltip.add(I18n.format("balisong.initiative"));
        tooltip.add(I18n.format("balisong.stance"));
        tooltip.add(I18n.format("balisong.hammer"));
        tooltip.add(I18n.format("balisong.reverse"));
        tooltip.add(I18n.format("balisong.dash"));
        //tooltip.add(I18n.format("balisong.riposte"));
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        for (EntityLivingBase e : attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(16))) {
            TaoCasterData.getTaoCap(e).setRootTime(200);
            if (e != attacker)
                TaoCasterData.getTaoCap(e).setBindTime(200);
        }
        TaoCasterData.getTaoCap(attacker).startRecordingDamage();
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        TaoCasterData.getTaoCap(elb).setRootTime(0);
        TaoCasterData.getTaoCap(elb).stopRecordingDamage(elb);
        gettagfast(item).setInteger("flashesWithoutHit", 0);
        gettagfast(item).setInteger("flashes", 0);
        elb.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 100));
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EnumHand hand = elb.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            TaoCombatUtils.rechargeHand(elb, hand, 1, true);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return NeedyLittleThings.isBehindEntity(attacker, target, 90, 90) || isCharged(attacker, item) ? Event.Result.ALLOW : Event.Result.DENY;
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//        return oldStack.isEmpty() || super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
//    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return NeedyLittleThings.isBehindEntity(attacker, target, 90, 90) || isCharged(attacker, item) ? 3f : 1f;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if ((target.getLastDamageSource() == null || target.getCombatTracker().getBestAttacker() != attacker) || isCharged(attacker, stack)) {
            return target.getTotalArmorValue();
        }
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //ignore 1 point of armor every chi level
            return getQiFromStack(stack);
        }
        return super.armorIgnoreAmount(ds, attacker, target, stack, orig);
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        if (isCharged(p, is)) return 16;
        return 2f;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() < CombatConfig.rollThreshold) {
            TaoCombatUtils.attack(elb, collidingEntity, getHand(stack));
            return true;
        }
        return false;
    }
}
