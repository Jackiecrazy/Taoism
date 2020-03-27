package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IChargeableWeapon {
    /**
     * charges the weapon.
     */
    void chargeWeapon(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, int ticks);

    void dischargeWeapon(EntityLivingBase attacker, ItemStack item);

    boolean isCharged(EntityLivingBase elb, ItemStack item);

    int getChargeTimeLeft(EntityLivingBase elb, ItemStack item);
}
