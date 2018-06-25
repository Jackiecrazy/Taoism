package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ICustomRange {
public float getReach(EntityPlayer p, ItemStack is);
public float getHorizontalRange(EntityPlayer p, ItemStack is);
}
