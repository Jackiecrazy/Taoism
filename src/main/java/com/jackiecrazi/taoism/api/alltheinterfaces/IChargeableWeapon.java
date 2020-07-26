package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IChargeableWeapon {

    boolean canCharge(EntityLivingBase wielder, ItemStack item);

    void chargeWeapon(EntityLivingBase attacker, ItemStack item);

    void dischargeWeapon(EntityLivingBase attacker, ItemStack item);

    boolean isCharged(EntityLivingBase elb, ItemStack item);

    int getChargedTime(EntityLivingBase elb, ItemStack item);

    int getMaxChargeTime();
}
