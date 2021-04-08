package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface ICombo extends IQiPostureManipulable {
    int getCombo(EntityLivingBase wielder, ItemStack is);

    void setCombo(EntityLivingBase wielder, ItemStack is, int to);

    int getComboLength(EntityLivingBase wielder, ItemStack is);

    float newCooldown(EntityLivingBase elb, ItemStack is);

    long lastAttackTime(EntityLivingBase elb, ItemStack is);

    void updateLastAttackTime(EntityLivingBase elb, ItemStack is);
}
