package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.Taoism;
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
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        if (isCharged(p, is)) return 16;
        return 2f;
    }

    @Override
    public int getMaxChargeTime() {
        return 400;
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
        if (isCharged(elb, stack) && getHand(stack) == EnumHand.MAIN_HAND) {
            RayTraceResult r = NeedyLittleThings.raytraceAnything(elb.world, elb, 16);
            Vec3d tpTo = NeedyLittleThings.getClosestAirSpot(elb.getPositionVector(), r.hitVec, elb);
            gettagfast(stack).setInteger("flashesWithoutHit", gettagfast(stack).getInteger("flashesWithoutHit") + 1);
            if (r.entityHit != null) {
                tpTo = NeedyLittleThings.getPointInFrontOf(r.entityHit, elb, -5);
                TaoCombatUtils.attack(elb, r.entityHit, EnumHand.MAIN_HAND);
                gettagfast(stack).setInteger("flashesWithoutHit", 0);
                gettagfast(stack).setInteger("flashes", gettagfast(stack).getInteger("flashes") + 1);
            }
            for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                TaoCasterData.getTaoCap(e).setRootTime(0);
                TaoCasterData.getTaoCap(e).setBindTime(0);
            }
            TaoCasterData.getTaoCap(elb).setRootTime(0);
            elb.setPositionAndUpdate(tpTo.x, tpTo.y, tpTo.z);
            elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.PLAYERS, 1f, 0.5f + Taoism.unirand.nextFloat() / 2);
            TaoCasterData.getTaoCap(elb).setRootTime(400);
            if (gettagfast(stack).getInteger("flashesWithoutHit") > 3 || !TaoCasterData.getTaoCap(elb).consumeQi(1, 5)) {
                dischargeWeapon(elb, stack);
            } else {
                for (EntityLivingBase e : elb.world.getEntitiesWithinAABB(EntityLivingBase.class, elb.getEntityBoundingBox().grow(16))) {
                    TaoCasterData.getTaoCap(e).setRootTime(200);
                    TaoCasterData.getTaoCap(e).setBindTime(200);
                }
            }
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("karambit.switch"));
        tooltip.add(I18n.format("karambit.backstab"));
        tooltip.add(I18n.format("karambit.initiative"));
        //tooltip.add(I18n.format("karambit.darkness"));
        tooltip.add(I18n.format("karambit.bleed"));
        //tooltip.add(I18n.format("karambit.riposte"));
        tooltip.add(I18n.format("karambit.harvest"));
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        for (EntityLivingBase e : attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(32))) {
            TaoCasterData.getTaoCap(e).setRootTime(400);
            TaoCasterData.getTaoCap(e).setBindTime(400);
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
        return NeedyLittleThings.isBehindEntity(attacker, target, 90, 90) || isCharged(attacker, item) ? 2f : 1f;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return super.hurtStart(ds, attacker, target, stack, orig) * (1 + gettagfast(stack).getInteger("flashes") * 0.5f);
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
        if (target.getTotalArmorValue() - chi * 6d <= 0 || isCharged(attacker, stack) || (target.getLastDamageSource() == null || target.getLastDamageSource().getTrueSource() != attacker))
            TaoPotionUtils.forceBleed(target, attacker, 40, 1, TaoPotionUtils.POTSTACKINGMETHOD.NONE);
    }
}
