package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IStaminaPostureManipulable;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
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
        return TaoCasterData.getTaoCap(elb).isOffhandAttack() ? elb.getHeldItemOffhand() : elb.getHeldItemMainhand();
    }

    public static ItemStack getParryingItemStack(EntityLivingBase attacker, EntityLivingBase elb, float amount) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        boolean mainRec = NeedyLittleThings.getCooledAttackStrength(elb, 0.5f) > 0.8f, offRec = NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f) > 0.8f;
        float defMult = 42;//meaning of life, the universe and everything
        ItemStack ret = ItemStack.EMPTY;
        //shield and sword block
        if ((main.getItem().isShield(main, elb) || contains(main.getItem())) && mainRec) {
            ret = main;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        if ((off.getItem().isShield(off, elb) || contains(off.getItem())) && offRec) {
            ret = off;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        //offhand
        if (offRec && off.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount) <= defMult) {
            defMult = ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount);
            ret = off;
        }
        //mainhand
        if (mainRec && main.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, elb, main, amount) <= defMult) {
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
        return (defender.onGround ? defender.isSneaking() ? 0.5f : 1f : 2f) *
                (defend.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) defend.getItem()).postureMultiplierDefend(attacker, defender, defend, amount) : CombatConfig.defaultMultiplierPostureDefend);
    }

    public static void rechargeHand(EntityLivingBase elb, EnumHand hand, float percent) {
        if (!(elb instanceof EntityPlayer)) return;
        double totalSec = 20 / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, hand);
        //if (percent != 0f)//this is because this is called in tickStuff on the first tick after cooldown starts, so constant resetting would just make the weapon dysfunctional
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
        if (elb instanceof EntityPlayer)
            switch (hand) {
                case OFF_HAND:
                    return NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f);
                case MAIN_HAND:
                    return NeedyLittleThings.getCooledAttackStrength(elb, 0.5f);
            }
        return 1f;
    }
}
