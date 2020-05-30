package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IStaminaPostureManipulable;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

public class TaoCombatUtils {
    public static void executeMove(EntityLivingBase elb, byte moveCode) {
        ItemStack is = getAttackingItemStackSensitive(elb);
        if (is.getMaxStackSize() == 1 && is.getItem() instanceof TaoWeapon) {
            if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
            is.getTagCompound().setByte("lastMove", is.getTagCompound().getByte("currentMove"));
            is.setTagInfo("currentMove", new NBTTagByte(moveCode));
            if (elb.getHeldItemMainhand().getItem() instanceof ITwoHanded && ((ITwoHanded) elb.getHeldItemMainhand().getItem()).isTwoHanded(elb.getHeldItemMainhand())) {
                is = elb.getHeldItemOffhand();
                if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
                is.getTagCompound().setByte("lastMove", is.getTagCompound().getByte("currentMove"));
                is.setTagInfo("currentMove", new NBTTagByte(moveCode));
            }
        }
    }

    public static ItemStack getAttackingItemStackSensitive(EntityLivingBase elb) {
        if (elb.getHeldItemMainhand().getItem() instanceof ITwoHanded && ((ITwoHanded) elb.getHeldItemMainhand().getItem()).isTwoHanded(elb.getHeldItemMainhand()))
            return elb.getHeldItemMainhand();
        return TaoWeapon.off ? elb.getHeldItemOffhand() : elb.getHeldItemMainhand();
    }

    public static ItemStack getParryingItemStack(EntityLivingBase attacker, EntityLivingBase elb, float amount) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        float defMult = 42;//meaning of life, the universe and everything
        ItemStack ret = ItemStack.EMPTY;
        //shield and sword block
        if (main.getItem().isShield(main, elb) || contains(main.getItem())) {
            ret = main;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        if (off.getItem().isShield(off, elb) || contains(off.getItem())) {
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

    private static boolean contains(Item i) {
        if (i.getRegistryName() == null) return false;
        for (String s : CombatConfig.parryCapableItems) {
            if (s.equals(i.getRegistryName().toString())) return true;
        }
        return false;
    }

    public static float postureAtk(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        return attack.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) attack.getItem()).postureDealtBase(attacker, defender, attack, amount) : amount * 0.1f;
    }

    public static float postureDef(EntityLivingBase defender, EntityLivingBase attacker, ItemStack defend, float amount) {
        return defend.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) defend.getItem()).postureDealtBase(attacker, defender, defend, amount) : CombatConfig.defaultMultiplierPostureDefend;
    }

    public static boolean attemptDodge(EntityLivingBase elb, int side) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() > CombatConfig.rollCooldown && (elb.onGround || TaoCasterData.getTaoCap(elb).getQi() > 2) && !elb.isSneaking()) {
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

            //NeedyLittleThings.setSize(elb, min, min);

            elb.addVelocity(x, y, z);
            elb.velocityChanged = true;
            return true;
        }
        return false;
    }

    public static void rechargeHand(EntityLivingBase elb, EnumHand hand, float percent) {
        double totalSec = 20 / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, hand);
        if (percent != 0f)//this is because this is called in tickStuff on the first tick after cooldown starts, so constant resetting would just make the weapon dysfunctional
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
        if(elb instanceof EntityPlayer)
            switch (hand) {
            case OFF_HAND:
                return NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f);
            case MAIN_HAND:
                return NeedyLittleThings.getCooledAttackStrength(elb, 0.5f);
        }
        return 1f;
    }

    public static float recalculateDamageIgnoreArmor(EntityLivingBase target, DamageSource ds, float orig, float pointsToIgnore) {
        return armorCalc(target, Math.max(target.getTotalArmorValue() - pointsToIgnore, 0), target.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue(), ds, orig);
    }

    private static float armorCalc(EntityLivingBase target, float armor, double tough, DamageSource ds, float damage) {
        if (!ds.isUnblockable())
            damage = CombatRules.getDamageAfterAbsorb(damage, armor, (float) tough);
        if (!ds.isDamageAbsolute())
            damage = applyPotionDamageCalculations(target, ds, damage);
        return damage;
    }

    private static float applyPotionDamageCalculations(EntityLivingBase elb, DamageSource source, float damage) {
        if (source.isDamageAbsolute()) {
            return damage;
        } else {
            if (elb.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
                int i = (elb.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = damage * (float) j;
                damage = f / 25.0F;
            }

            if (damage <= 0.0F) {
                return 0.0F;
            } else {
                int k = EnchantmentHelper.getEnchantmentModifierDamage(elb.getArmorInventoryList(), source);

                if (k > 0) {
                    damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float) k);
                }

                return damage;
            }
        }
    }
}
