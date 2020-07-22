package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.svardstav;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityMeidoZangetsuha;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GuanDao extends TaoWeapon {
    /*
     * Retains versatility and relentlessness from its smaller cousin. High power and combo, medium speed and power, low defense
     * Right click switches between guard and strike forms, with a 1s(-chi/20) cooldown.
     * Switching from guard to strike deals a frontal attack and vice versa, and immediately resets normal attack
     * In guard form, range 4, splash 2, deals (1.3+chi/20)x damage. Riposte: gain 1 chi
     * In strike form, range 7, splash 4, no buff, no riposte.
     *
     * gain momentum to cut continuously
     * start at 1.3 attack speed and 5 damage in the "new moon" phase
     * Right click moves the blade backwards, sweeping enemies in 90 degrees *behind* you
     * Left click moves the backwards blade forward again, sweeping enemies in 90 degrees before you
     * Successfully alternating attacks stacks a layer of moonlight, up to 8, at which point it's a "full moon"
     * Each layer of moonlight adds 0.1 attack speed and 10% base damage
     * Moonlight layers last up to 5 seconds, each gain resets the timer
     *
     * TODO execution: only executable at full moon
     * gain the ninth layer, blood moon.
     * MEIDO ZANGETSUHA!
     * Your attack range is doubled
     * Each time you gain moonlight, a black sword beam is shot at every enemy in a 12 block radius
     * Crits send out three black sword beams angled in a claw formation forwards/backwards
     * Both actions cost 0.5 qi, at 5 qi you unleash a barrage of black sword beams with random orientation in the given direction
     * the number of beams increase with moonlight, your moonlight is reset.
     */
    public GuanDao() {
        super(1, 1, 5d, 1f);
        //this.addPropertyOverride(new ResourceLocation("long"), (stack, world, ent) -> isLong(stack) ? 1 : 0);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
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

    @Override
    protected void oncePerHit(EntityLivingBase attacker, EntityLivingBase target, ItemStack is) {
        if (getLastMove(is).isValid() && ((getLastMove(is).isLeftClick() && getHand(is) == EnumHand.OFF_HAND) || (!getLastMove(is).isLeftClick() && getHand(is) == EnumHand.MAIN_HAND))) {
            int max = 8;
            if (isCharged(attacker, is)) {
                max = 30;
                setBuff(attacker.getHeldItemMainhand(), Math.min(getBuff(is) + 4, max));
            }
            setBuff(attacker.getHeldItemMainhand(), Math.min(getBuff(is) + 1, max));
        }
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (attacker.world.isRemote) return;
        if (!isCharged(attacker, stack) || TaoCasterData.getTaoCap(attacker).consumeQi(0.5f, 5)) {
            if (getHand(stack) == EnumHand.OFF_HAND) {
                if (!attacker.onGround && (attacker.isSneaking() || !TaoCasterData.getTaoCap(attacker).isInCombatMode())) {
                    if (isCharged(attacker, stack)) {
                        for (int x = 0; x < 5; x++) {
                            EntityMeidoZangetsuha meido = new EntityMeidoZangetsuha(attacker, getHand(stack), stack);
                            meido.setRenderRotation(90).shoot(attacker, -attacker.rotationPitch, attacker.rotationYaw + 240 - x * 30, 0, 1, 0);
                            meido.motionY -= attacker.motionY;
                            attacker.world.spawnEntity(meido);
                        }
                    }
                    splash(attacker, stack, -20, 120);
                } else {
                    splash(attacker, stack, -120);
                }
            } else {
                if (!attacker.onGround && (attacker.isSneaking() || !TaoCasterData.getTaoCap(attacker).isInCombatMode())) {
                    if (isCharged(attacker, stack)) {
                        for (int x = 0; x < 3; x++) {
                            EntityMeidoZangetsuha meido = new EntityMeidoZangetsuha(attacker, getHand(stack), stack);
                            meido.setRenderRotation(90).shoot(attacker, attacker.rotationPitch, attacker.rotationYaw - 30 + x * 30, 0, 1f, 0);
                            meido.motionY -= attacker.motionY;
                            attacker.world.spawnEntity(meido);
                        }
                    }
                    splash(attacker, stack, 20, 120);
                } else {
                    splash(attacker, stack, 90);
                }
            }
        } else {
            dischargeWeapon(attacker, stack);
        }
    }

    protected double speed(ItemStack stack) {
        return Math.min(0.8 + (getBuff(stack) / 10f) - 4, -0.24);
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : super.getQiAccumulationRate(is);
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return (isCharged(p, is) ? 6 : 3);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("guandao.crit"));
        tooltip.add(I18n.format("guandao.attack"));
        tooltip.add(I18n.format("guandao.alt"));
        tooltip.add(I18n.format("guandao.stack"));
        tooltip.add(I18n.format("guandao.moonlight"));
    }

    @Override
    protected void additionalSplashAction(EntityLivingBase attacker, Entity target, ItemStack is) {
        if (isCharged(attacker, is) && !attacker.world.isRemote) {
            EntityMeidoZangetsuha meido = new EntityMeidoZangetsuha(attacker, getHand(is), is);
            meido.shootTo(target.getPositionVector().addVector(0, target.height / 2, 0), 1, 0);
            attacker.world.spawnEntity(meido);
        }
    }

    @Override
    public boolean canCharge(EntityLivingBase wielder, ItemStack item) {
        if (wielder instanceof EntityPlayer && ((EntityPlayer) wielder).isCreative()) {
            setBuff(item, 8);
        }
        return super.canCharge(wielder, item) && getBuff(item) > 7;
    }

//    @Override
//    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
//        return getBuff(item) > 3 ? Event.Result.ALLOW : Event.Result.DENY;
//    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        //spawn a barrage of mdzgh
        multiHit(elb, item, elb, 10 + getBuff(item), 1);
        setBuff(item, 0);
    }

    @Override
    protected void performScheduledAction(EntityLivingBase attacker, Entity victim, ItemStack is, long l, int interval) {
        if (!attacker.world.isRemote)
            for (int i = 0; i < 2; i++) {
                EntityMeidoZangetsuha meido = new EntityMeidoZangetsuha(attacker, getHand(is), is);
                meido.setPositionAndRotation(attacker.posX - 1 + Taoism.unirand.nextFloat() * 2, attacker.posY, attacker.posZ - 1 + Taoism.unirand.nextFloat() * 2, attacker.rotationYaw, attacker.rotationPitch);
                meido.shoot(attacker, -5 - Taoism.unirand.nextInt(25), attacker.rotationYaw - 30 + Taoism.unirand.nextInt(60), 0, 1.5f, 0);
                attacker.world.spawnEntity(meido);
            }
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1.5f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        if (attacker.world.getTotalWorldTime() - lastAttackTime(attacker, stack) > 100) setBuff(stack, 0);
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig * (1 + getBuff(stack) * 0.1f);
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        if (!elb.onGround && (elb.isSneaking() || !TaoCasterData.getTaoCap(elb).isInCombatMode()))
            return 0.6f;
        return super.newCooldown(elb, is);
    }

    @Override
    protected void afterSwing(EntityLivingBase attacker, ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            //offhand attack
            TaoCombatUtils.rechargeHand(attacker, EnumHand.MAIN_HAND, 0.5f, true);
        } else {
            //mainhand attack
            TaoCombatUtils.rechargeHand(attacker, EnumHand.OFF_HAND, 0.5f, true);
        }
    }

    private void setBuff(ItemStack is, int phase) {
        gettagfast(is).setInteger("phase", phase);
    }

    private int getBuff(ItemStack is) {
        return gettagfast(is).getInteger("phase");
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }
}
