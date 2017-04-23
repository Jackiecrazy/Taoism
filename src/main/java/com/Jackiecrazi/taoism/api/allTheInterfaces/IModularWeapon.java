package com.Jackiecrazi.taoism.api.allTheInterfaces;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public interface IModularWeapon {
	/**
	 * NOTICE! The string is the key, and the itemstack is the module. NOT THE INDIVIDUAL PARTS!
	 * @param is itemstack
	 * @return a hashmap with a string as key and itemstack as part
	 */
	public HashMap<String,ItemStack> getParts(ItemStack is);
	/**
	 * same as {@link #getParts(ItemStack)}, but gets the parts of the parts.
	 * @param is
	 * @return a hashmap with a string as key and another hashmap as part, this one holding "base" as the base mat and "module+x" as the additions
	 */
	public HashMap<String,HashMap<String,ItemStack>> getPartsOfParts(ItemStack is);
	/**
	 * stores a part as a name under an itemstack. This will override {@link #setPartOfPart(String, ItemStack, ItemStack)}, I think.
	 * @param partName make sure you check under the individual weapons
	 * @param base original itemstack
	 * @param addition the itemstack that will be added. Should implement {@link IModular} or {@link ILianQiMaterial}, but I ain't checkin'
	 * 
	 */
	public boolean setPart(String partName,ItemStack base,ItemStack addition);
	/**
	 * same as {@link #getParts(ItemStack)}, but stores the addition as a part of the partName's list.
	 * @param partName make sure you check under the individual weapons, addition goes under this
	 * @param base original itemstack
	 * @param addition the itemstack that will be added. Should implement {@link ILianQiMaterial}, but I ain't checkin'
	 * 
	 */
	public void setPartOfPart(String partName,ItemStack base,ItemStack addition);
	
}
