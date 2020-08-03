package com.jackiecrazi.taoism.common.item.weapon.melee.sectional;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Nunchaku extends TaoWeapon {
    /*
     * A flexible blunt weapon that still hurts. High speed and defense, medium power and combo, low range
     * I'm gonna be using this to test the move system.
     * Can be either one or two handed, with different movesets for both.
     * Nunchaku will become two-handed if in main hand and offhand is empty
     * after a given attack, your nunchaku will end up on a certain side:
     * high (h) or low (l), same (s) or different (d) side
     * high is fast, low is powerful
     * right side does extra posture damage, left side applies short effects
     * From a given stance it's possible to launch a high attack by default or a low attack by pressing back
     *
     * Available moves: hl smash, hh flick, ll sweep, lh 8-spin, ?? jab
     * flick is very fast
     * smash inflicts slowness and deals 1.3x damage
     * sweep hits in radius 1 and knocks back slightly
     * 8-spin hits chip damage 3 times with no knockback
     *
     * Extra moves when two-handed: dhl backflip (default guard), dlh updraft
     * When two-handed, you can chain with rapidly switching hands, resetting one hand if the other is used.
     * backflip interrupts enemy attacks and knocks them back very slightly, by back-alt
     * updraft AoEs, hits through blocks and knocks back, done by normal alt
     *
     * Execution: if you are behind your opponent and in jab range (1), inflict slow 2/infinite, gain rooted 1/infinite (no knockback),
     * and causes the opponent to lose 5 bars of oxygen per second in a choke hold.
     * Lasts until distance > 3, at which point the opponent is tripped for 50% max posture damage
     * Alternatively, by dealing enough damage to you (threshold scales with higher chi) will end this state as well
     *
     */
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            StaticRefs.CHAIN
    };

    public Nunchaku() {
        super(0, 1.6, 6f, 0.6f);
        this.addPropertyOverride(new ResourceLocation("nuns"), (stack, w, elb) -> {
            int low = getCurrentMove(stack).isSneakPressed() ? 1 : 0;
            int dual = isTwoHanded(stack) ? 2 : 0;
            return low + dual;
        });
    }

    public boolean isTwoHanded(ItemStack is) {
        return false;//isOffhandEmpty(is) || isDummy(is);
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && e.ticksExisted % 10 == 0) {
            EntityLivingBase elb = (EntityLivingBase) e;
            Entity target = getLastAttackedEntity(w, stack);
            if (isCharged(elb, stack) && target != null) {
                if (target instanceof EntityLivingBase && NeedyLittleThings.getDistSqCompensated(target, elb) < getReach(elb, stack) * getReach(elb, stack)) {
                    EntityLivingBase targ = (EntityLivingBase) target;
                    target.setAir(target.getAir() - 1);
                    target.attackEntityFrom(DamageSource.DROWN, (float) getDamageAgainst(elb, targ, stack) / 5);
                    if (elb.onGround)
                        TaoCasterData.getTaoCap(elb).setRootTime(200);
                    if (targ.onGround)
                        TaoCasterData.getTaoCap(targ).setRootTime(200);
                    TaoCasterData.getTaoCap(targ).setBindTime(200);
                    targ.rotationYaw = elb.rotationYaw;
                    targ.rotationYawHead = elb.rotationYawHead;
                    return;
                }
                dischargeWeapon(elb, stack);
            }
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (isCharged(elb, is)) {
            dischargeWeapon(elb, is);
            return true;
        }
        if (elb instanceof EntityPlayer && !Taoism.proxy.isBreakingBlock((EntityPlayer) elb))
            updateStanceSide(is);
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("nunchaku.stance"));
        tooltip.add(I18n.format("nunchaku.moves"));
        tooltip.add(I18n.format("nunchaku.flick"));
        tooltip.add(I18n.format("nunchaku.smash"));
        tooltip.add(I18n.format("nunchaku.sweep"));
        tooltip.add(I18n.format("nunchaku.spin"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    protected void aoe(ItemStack is, EntityLivingBase attacker, int chi) {
        if (getCurrentMove(is).isSneakPressed() && getLastMove(is).isSneakPressed()) {//low low
            //sweep!
            splash(attacker, is, 90);
        }
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        setLastAttackedEntity(item, null);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        Entity target = getLastAttackedEntity(elb.world, item);
        if (target instanceof EntityLivingBase) {
            TaoCasterData.getTaoCap(elb).stopRecordingDamage(elb);
            TaoCasterData.getTaoCap(elb).setForcedLookAt(null);
            TaoCasterData.getTaoCap((EntityLivingBase) target).consumePosture((float) (getDamageAgainst(elb, (EntityLivingBase) target, item) * 2), true, true, elb);
        }
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (isCharged(defender, item)) {
            setLastAttackedEntity(item, attacker);
            Vec3d pos = NeedyLittleThings.getPointInFrontOf(attacker, defender, -2);
            defender.attemptTeleport(pos.x, pos.y, pos.z);
            TaoCasterData.getTaoCap(defender).startRecordingDamage();
            TaoCasterData.getTaoCap(defender).setForcedLookAt(attacker);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getCurrentMove(item).isSneakPressed() && !getLastMove(item).isSneakPressed() ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (getCurrentMove(item).isSneakPressed() && !getLastMove(item).isSneakPressed()) {//high low
            //smash!
            return 1.5f;
        }
        return 1;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (!getCurrentMove(item).isSneakPressed() && getLastMove(item).isSneakPressed()) {//low high
            //8-spin! reduce damage
            return 0.4f;
        }
        return 1;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (isCharged(attacker, stack) && !TaoCasterData.getTaoCap(attacker).isRecordingDamage()) {
            setLastAttackedEntity(stack, target);
            Vec3d pos = NeedyLittleThings.getPointInFrontOf(target, attacker, -2);
            attacker.attemptTeleport(pos.x, pos.y, pos.z);
            TaoCasterData.getTaoCap(attacker).startRecordingDamage();
            TaoCasterData.getTaoCap(attacker).setForcedLookAt(target);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getCurrentMove(stack).isSneakPressed() && !getLastMove(stack).isSneakPressed()) {//high low
            //smash!
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, 10, 0), false);
        }
    }

    @Override
    protected void queueExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (!getCurrentMove(stack).isSneakPressed() && getLastMove(stack).isSneakPressed()) {//low high
            //8-spin!
            multiHit(attacker, stack, target, 4, 2);
        }
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        if (!getCurrentMove(is).isSneakPressed() && !getLastMove(is).isSneakPressed()) {//high high
            //flick!
            return 0.7f;
        }
        return 0f;
    }

    private void updateStanceSide(ItemStack is) {
        MoveCode mc = getCurrentMove(is);
        gettagfast(is).setBoolean("onOtherSide", mc.isValid() && !gettagfast(is).getBoolean("onOtherSide"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }
}
