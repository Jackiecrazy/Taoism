package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IStaminaPostureManipulable;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.entity.ai.AIDowned;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class TaoCombatUtils {
    public static void executeMove(EntityLivingBase entity, byte moveCode) {
        ItemStack is = getAttackingItemStackSensitive(entity);
        if (is.getMaxStackSize() == 1) {
            if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
            is.getTagCompound().setByte("lastMove", is.getTagCompound().getByte("currentMove"));
            is.setTagInfo("currentMove", new NBTTagByte(moveCode));
        }
    }

    public static ItemStack getAttackingItemStackSensitive(EntityLivingBase elb) {
        if (elb.getHeldItemMainhand().getItem() instanceof ITwoHanded && ((ITwoHanded) elb.getHeldItemMainhand().getItem()).isTwoHanded(elb.getHeldItemMainhand()))
            return elb.getHeldItemMainhand();
        return TaoWeapon.off ? elb.getHeldItemOffhand() : elb.getHeldItemMainhand();
    }

    private static boolean isHoldingEligibleItem(EntityLivingBase entity) {
        ItemStack main = entity.getHeldItemMainhand(), off = entity.getHeldItemOffhand();
        if (main.getItem() instanceof ITwoHanded && ((ITwoHanded) main.getItem()).isTwoHanded(main) && !(main.getItem() instanceof IStaminaPostureManipulable))
            return false;
        return
                main.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) main.getItem()).canBlock(entity, main)
                        || off.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) off.getItem()).canBlock(entity, off)
                        || CombatConfig.parryCapableItems.contains(main.getUnlocalizedName())
                        || CombatConfig.parryCapableItems.contains(off.getUnlocalizedName());
    }

    public static ItemStack getParryingItemStack(EntityLivingBase attacker, EntityLivingBase elb, float amount) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        float defMult = 42;//meaning of life, the universe and everything
        ItemStack ret = ItemStack.EMPTY;
        //shield and sword block
        if (main.getItem().isShield(main, elb) || CombatConfig.parryCapableItems.contains(main.getItem().getUnlocalizedName())) {
            ret = main;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        if (off.getItem().isShield(off, elb) || CombatConfig.parryCapableItems.contains(off.getItem().getUnlocalizedName())) {
            ret = off;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        //offhand
        if (off.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount) <= defMult) {
            defMult = ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount);
            ret = off;
        }
        //mainhand
        if (main.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, elb, main, amount) <= defMult) {
            ret = main;
        }
        return ret;
    }

    public static boolean isEntityBlocking(EntityLivingBase entity) {
        return ((entity.isSneaking() && isHoldingEligibleItem(entity)) || entity.isActiveItemStackBlocking()) && !isEntityParrying(entity);
    }

    public static boolean isEntityParrying(EntityLivingBase entity) {
        return TaoCasterData.getTaoCap(entity).getParryCounter() <= CombatConfig.parryThreshold && isHoldingEligibleItem(entity);
    }

    /**
     * I just split them because base posture damage is reflected onto attacker at a 40% rate.
     */
    public static float requiredPosture(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        return requiredPostureAtk(defender, attacker, attack, amount) * requiredPostureDef(defender, attacker, attack, amount);
    }

    public static float requiredPostureAtk(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        return attack.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) attack.getItem()).postureDealtBase(attacker, defender, attack, amount) : amount * 0.3f;
    }

    public static float requiredPostureDef(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        ItemStack main = defender.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack off = defender.getHeldItem(EnumHand.OFF_HAND);
        float defMult = CombatConfig.defaultMultiplierPostureDefend;
        if (isEntityParrying(defender) || isEntityBlocking(defender)) {
//            if (main.getItem() instanceof ITwoHanded && ((ITwoHanded) main.getItem()).isTwoHanded(main)) {
//                if (main.getItem() instanceof IStaminaPostureManipulable) {
//                    return ((IStaminaPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, defender, main, amount);
//                } else if (CombatConfig.parryCapableItems.contains(main.getItem().getUnlocalizedName())) {
//                    return CombatConfig.defaultMultiplierPostureDefend;
//                } else ;//sorry, no parry for you!
//            }
            //is shield, highest priority
            if (off.getItem().isShield(off, defender) || main.getItem().isShield(main, defender))
                defMult = CombatConfig.defaultMultiplierPostureDefend;
            //mainhand
            if (main.getItem() instanceof IStaminaPostureManipulable)
                defMult = Math.min(((IStaminaPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, defender, main, amount), defMult);
            //offhand
            if (off.getItem() instanceof IStaminaPostureManipulable)
                defMult = Math.min(((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, defender, off, amount), defMult);
            //default parry
            if (CombatConfig.parryCapableItems.contains(off.getItem().getUnlocalizedName()) || CombatConfig.parryCapableItems.contains(main.getItem().getUnlocalizedName()))
                defMult = Math.min(CombatConfig.defaultMultiplierPostureDefend, defMult);
        }
        return defMult;
    }

    public static void beatDown(EntityLivingBase target, EntityLivingBase attacker, float damage) {
        target.dismountRidingEntity();
        NeedyLittleThings.knockBack(target, attacker, damage * 0.2F);
        int downtimer = MathHelper.clamp((int) damage * 10, 10, 100);
        TaoCasterData.getTaoCap(target).setDownTimer(downtimer);
        //do this first to prevent hurtbox curiosities
        if (target instanceof EntityLiving) {
            EntityLiving el = (EntityLiving) target;
            el.tasks.addTask(0, new AIDowned(el));
            el.targetTasks.addTask(0, new AIDowned(el));
        }
        //babe! it's 4pm, time for your flattening!
        TaoCasterData.getTaoCap(target).setPrevSizes(target.width, target.height);//set this on the client as well
        TaoCasterData.forceUpdateTrackingClients(target);
        float min = Math.min(target.width, target.height), max = Math.max(target.width, target.height);
        NeedyLittleThings.setSize(target, max, min);
        if (target instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) target;
            p.sendStatusMessage(new TextComponentTranslation("you have been staggered for " + downtimer / 20f + " seconds!"), true);
        }
        if (attacker instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) attacker;
            p.sendStatusMessage(new TextComponentTranslation("the target has been staggered for " + downtimer / 20f + " seconds!"), true);
        }
        //System.out.println("target is downed for " + downtimer + " ticks!");
    }

    public static boolean attemptDodge(EntityLivingBase elb, int side) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() > CombatConfig.rollCooldown && elb.onGround && !elb.isSneaking()) {
            //System.out.println("execute roll to side " + side);
            TaoCasterData.getTaoCap(elb).setRollCounter(0);
            TaoCasterData.getTaoCap(elb).setPrevSizes(elb.width, elb.height);
            float min = Math.min(elb.width, elb.height);
            double x = 0, y = 0.3, z = 0;
            switch (side) {
                case 0://left
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw));
                    break;
                case 1://back
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw - 90));
                    break;
                case 2://right
                    x = Math.cos(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    z = Math.sin(NeedyLittleThings.rad(elb.rotationYaw - 180));
                    break;
            }
            x /= 1.5;
            z /= 1.5;

            NeedyLittleThings.setSize(elb, min, min);

            elb.addVelocity(x, y, z);
            elb.velocityChanged = true;
            return true;
        }
        return false;
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getPostureRegenAmount(EntityLivingBase elb, int ticks) {
        float posMult = (float) elb.getEntityAttribute(TaoEntities.POSREGEN).getAttributeValue();
        float armorMod = 1f - ((float) elb.getTotalArmorValue() / 40f);
        return ticks * armorMod * 0.25f * posMult * elb.getHealth() / elb.getMaxHealth();
    }


    public static void rechargeHand(EntityLivingBase elb, EnumHand hand, float percent) {
        double totalSec = 20 / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, hand);
        if (percent != 0f)
            switch (hand) {
                case OFF_HAND:
                    TaoCasterData.getTaoCap(elb).setOffhandCool((int) (percent * totalSec));
                    break;
                case MAIN_HAND:
                    Taoism.setAtk(elb, (int) (percent * totalSec));
                    break;
            }
    }

    public static float getHandCoolDown(EntityLivingBase elb, EnumHand hand) {
        switch (hand) {
            case OFF_HAND:
                return NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f);
            case MAIN_HAND:
                return NeedyLittleThings.getCooledAttackStrength(elb, 0.5f);
        }
        return 0;
    }

    public static void updateCasterData(EntityLivingBase elb) {
        ITaoStatCapability itsc = elb.getCapability(TaoCasterData.CAP, null);
        itsc.setMaxPosture((float) Math.ceil(elb.width) * (float) Math.ceil(elb.width) * (float) Math.ceil(elb.height) * 5 * (1 + (elb.getTotalArmorValue() / 20f)));
        //brings it to a tidy sum of 10 for the player, 20 with full armor. TODO toughness although I guess that's factored in already since damage and all?
        itsc.setMaxLing(10f);//TODO ???
        tickCasterData(elb, (int) (elb.world.getTotalWorldTime() - itsc.getLastUpdatedTime()));
    }

    /**
     * ticks the caster for however many ticks dictated by the second argument.
     */
    public static void tickCasterData(EntityLivingBase elb, final int ticks) {
        if (!elb.isEntityAlive()) return;
        ITaoStatCapability itsc = elb.getCapability(TaoCasterData.CAP, null);
        float lingMult = (float) elb.getEntityAttribute(TaoEntities.LINGREGEN).getAttributeValue();
        int diff = ticks;
        //spirit power recharge
        if (itsc.getLingRechargeCD() >= diff) itsc.setLingRechargeCD(itsc.getLingRechargeCD() - diff);
        else {
            diff -= itsc.getLingRechargeCD();
            itsc.addLing(diff * lingMult);
            itsc.setLingRechargeCD(0);
        }
        //downed ticking
        if (itsc.getDownTimer() > 0) {
            itsc.setDownTimer(itsc.getDownTimer() - ticks);
            if (itsc.getDownTimer() <= 0) {
                //yes honey
                Tuple<Float, Float> thing = itsc.getPrevSizes();
                if (!elb.world.isRemote) {
                    TaoCasterData.forceUpdateTrackingClients(elb);
                    NeedyLittleThings.setSize(elb, thing.getFirst(), thing.getSecond());
                }

                int overflow = -itsc.getDownTimer();
                itsc.setDownTimer(0);
                itsc.addPosture(getPostureRegenAmount(elb, overflow));
            } else itsc.setPostureRechargeCD(itsc.getDownTimer());
        }
        diff = ticks;
        if (itsc.getPostureRechargeCD() <= diff || !itsc.isProtected()) {
            if (itsc.isProtected())
                diff -= itsc.getPostureRechargeCD();
            itsc.setPostureRechargeCD(0);
            itsc.addPosture(getPostureRegenAmount(elb, diff));
        } else itsc.setPostureRechargeCD(itsc.getPostureRechargeCD() - ticks);
        diff = ticks;
        //value updating
        itsc.setPosInvulTime(itsc.getPosInvulTime() - diff);
        itsc.addParryCounter(diff);
        itsc.addRollCounter(diff);
        //roll ticking
        if (itsc.getRollCounter() == CombatConfig.rollCooldown) {
            Tuple<Float, Float> thing = itsc.getPrevSizes();
            elb.width = thing.getFirst();
            elb.height = thing.getSecond();//FIXME!
        }
        itsc.setOffhandCool(itsc.getOffhandCool() + ticks);
        itsc.setQi(Math.max(itsc.getQi() - 0.1f * ticks, 0));

        if (!(elb instanceof EntityPlayer))
            itsc.setSwing(itsc.getSwing() + ticks);
        itsc.setLastUpdatedTime(elb.world.getTotalWorldTime());

    }

    /**
     * ticks the caster once, to save processing. For players only.
     */
    public static void tickCasterData(EntityLivingBase elb) {
        ITaoStatCapability itsc = elb.getCapability(TaoCasterData.CAP, null);
        if (itsc.getLingRechargeCD() == 0) itsc.addLing(1f);
        else itsc.setLingRechargeCD(itsc.getLingRechargeCD() - 1);
        if (itsc.getPostureRechargeCD() == 0 || !itsc.isProtected()) itsc.addPosture(getPostureRegenAmount(elb, 1));
        else itsc.setPostureRechargeCD(itsc.getPostureRechargeCD() - 1);
        itsc.addParryCounter(1);
        itsc.setOffhandCool(itsc.getOffhandCool() + 1);
        if (itsc.getDownTimer() > 0) {
            itsc.setDownTimer(itsc.getDownTimer() - 1);
            itsc.setPostureRechargeCD(itsc.getDownTimer());
            if (itsc.getDownTimer() == 0) {
                Tuple<Float, Float> thing = itsc.getPrevSizes();
                elb.width = thing.getFirst();
                elb.height = thing.getSecond();
            }
        }
        itsc.addRollCounter(1);
        if (itsc.getRollCounter() == CombatConfig.rollCooldown) {
            Tuple<Float, Float> thing = itsc.getPrevSizes();
            elb.width = thing.getFirst();
            elb.height = thing.getSecond();
        }
        itsc.setLastUpdatedTime(elb.world.getTotalWorldTime());
    }

}
