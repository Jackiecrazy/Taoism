package com.Jackiecrazi.taoism.api.allTheInterfaces;

import net.minecraft.item.ItemStack;

/**
 * @author Jackiecrazy
 * <BR><BR>Anything that is a weapon ingredient, or otherwise is a module should implement this.
 */
public interface IModular {
public ItemStack[] getParts(ItemStack is);
	
	public void setPart(ItemStack base,ItemStack addition);
}
