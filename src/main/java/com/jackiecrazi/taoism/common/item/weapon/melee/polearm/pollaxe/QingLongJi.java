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
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
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
     * execution: soryuha!
     * your qi gains are unaffected, but you lose 0.5 qi a second
     * attacking mobs will not cost additional qi and will start recording damage
     * range is doubled
     * Left click upgrades to 20 degree sweep
     * switching into reverse form bats mobs to just in front of you
     * switching into normal form grabs mobs from far away
     * so the overall effect is bat mobs to a point in front of you-hit them all-suck them back in-hit them again
     * When qi hits 5, unleash a dragon strike, size depending on how long the form has lasted
     * -two dragons spiral each other while traveling forward, driving enemies into the ground
     */
    public QingLongJi() {
        super(2, 1.2, 5.5d, 4f);
        this.addPropertyOverride(new ResourceLocation("invert"), (stack, world, ent) -> isReversed(stack) ? 1 : 0);
    }

    private boolean isReversed(ItemStack is) {
        return gettagfast(is).getBoolean("reversed");
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return .8f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (getHand(stack) == EnumHand.MAIN_HAND && e instanceof EntityLivingBase && e.ticksExisted % 20 == 0) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (isCharged(elb, stack) && !TaoCasterData.getTaoCap(elb).consumeQi(0.5f, 5)) {
                gettagfast(stack).setBoolean("release", true);
                elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.PLAYERS, 1f, 1f);
                splash(elb, stack, 120);
                dischargeWeapon(elb, stack);
                gettagfast(stack).setBoolean("release", false);
            }
        }
    }

    protected double speed(ItemStack stack) {
        return (1.4d + (getQiFromStack(stack) / 20d)) - 4d;
    }

    public float getQiAccumulationRate(ItemStack is) {
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
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return (gettagfast(is).getBoolean("release") ? 10 : 0) + (isCharged(p, is) ? 8 : 4f);
    }

    public void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND){
            if (isCharged(attacker, stack)) splash(attacker, stack, 120);
            else splash(attacker, stack, 60 + chi * 6);
        }
        else if (isCharged(attacker, stack)) splash(attacker, stack, 20);
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? isReversed(is) ? 1 : 0 : 2;
    }

    /**
     * override to keep qi gen on
     */
    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        if (isDummy(item) && attacker.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            chargeWeapon(attacker, attacker.getHeldItemMainhand());
            return;
        }
        gettagfast(item).setBoolean("charge", true);
        gettagfast(item).setLong("chargedAtTime", attacker.world.getTotalWorldTime());
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getLastMove(item).isLeftClick() ^ getHand(item) == EnumHand.MAIN_HAND ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float chimult = TaoCasterData.getTaoCap(attacker).getQiFloored() / 10f;
        return getHand(item) == EnumHand.OFF_HAND ? 1f : 1 + chimult;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return gettagfast(item).getBoolean("release") ? getChargedTime(attacker, item)/10f : isCharged(attacker, item) ? 2 : 1;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds,attacker,target,stack,orig);
        if (isCharged(attacker, stack)) {
            if (!TaoCasterData.getTaoCap(target).isRecordingDamage())
                TaoCasterData.getTaoCap(target).startRecordingDamage();
        } else TaoCasterData.getTaoCap(target).stopRecordingDamage(attacker);
        if (gettagfast(stack).getBoolean("release"))
            TaoCasterData.getTaoCap(target).stopRecordingDamage(attacker);
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (gettagfast(stack).getBoolean("release"))
            return orig + getChargedTime(attacker, stack) / 20f;
        return super.onKnockingBack(attacker, target, stack, orig);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (gettagfast(stack).getBoolean("release")) {
            target.addVelocity(MathHelper.cos(attacker.rotationYaw), 0.1, MathHelper.sin(attacker.rotationYaw));
            return;
        }
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 0.3f, true, true);
            if (isReversed(stack)) {
                //crescent cut!
                TaoPotionUtils.forceBleed(target, attacker, chi * 4, 0, TaoPotionUtils.POTSTACKINGMETHOD.ADD);
                if (isCharged(attacker, stack)) {
                    //NeedyLittleThings.knockBack(target, attacker, -5);
                    target.addVelocity((attacker.posX - target.posX) * 0.2, (attacker.posY - target.posY) * 0.2, (attacker.posZ - target.posZ) * 0.2);
                }
            } else {
                //butt smash!
                TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, chi * 2, 0), false);
                if (isCharged(attacker, stack)) {
                    //NeedyLittleThings.knockBack(target, attacker, -1);
                    //NeedyLittleThings.knockBack(target, attacker, 5);
                    target.addVelocity(-MathHelper.sin(-attacker.rotationYaw * 0.017453292F - (float) Math.PI) * 0.5, 0.1, -MathHelper.cos(-attacker.rotationYaw * 0.017453292F - (float) Math.PI) * 0.5);
                }
            }
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        if (!attacker.world.isRemote && getHand(stack) == EnumHand.OFF_HAND) {
            if (isReversed(stack)) {
                setReversed(attacker.getHeldItemMainhand(), false);
            } else setReversed(attacker.getHeldItemMainhand(), true);
        }
        EnumHand other = getHand(stack) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(attacker, other, 0.5f, true);
    }

    private void setReversed(ItemStack is, boolean to) {
        gettagfast(is).setBoolean("reversed", to);
    }
}
