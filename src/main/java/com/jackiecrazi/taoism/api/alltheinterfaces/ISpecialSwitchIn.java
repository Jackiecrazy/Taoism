package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * implement this if you want your item to do something special on a switch in from any other item
 */
public interface ISpecialSwitchIn {
    void onSwitchIn(ItemStack stack, EntityLivingBase elb);
}
