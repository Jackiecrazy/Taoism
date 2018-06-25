package com.jackiecrazi.taoism.api.alltheinterfaces;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import com.jackiecrazi.taoism.api.PartData;

public interface IAmKindaModular {
	/**
	 * @param is
	 * @return an array of the parts that this thing has
	 */
	public ArrayList<String> getPartNames(ItemStack is);
	/**
	 * The itemstack will always be an instance of {@link IModular}
	 * @param is itemstack
	 * @return an array made of arrays of part data
	 */
	public HashMap<String, PartData> getParts(ItemStack is);
	/**
	 * stores a part as a name under an itemstack. This will override {@link #setPartOfPart(String, ItemStack, ItemStack)}, I think.
	 * @param partName make sure you check under the individual weapons
	 * @param base original itemstack
	 * @param addition the itemstack that will be added. Should implement {@link IModular} or {@link ILianQiMaterial}, but I ain't checkin'
	 * @return whether it was successfully set
	 */
	public boolean setPart(String partName, ItemStack is, PartData addition);
	
}
