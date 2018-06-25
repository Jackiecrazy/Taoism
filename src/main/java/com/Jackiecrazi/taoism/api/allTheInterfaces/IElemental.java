/**
 * 
 */
package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.item.ItemStack;

public interface IElemental {
	public static final int NUMOFELEM=5;
	/**
	 * @return a list of elemental affinities, in the order metal, wood, water, fire, earth
	 */
	float[] getAffinities(ItemStack is);
	void setAffinities(ItemStack is,float... affinities);
	/**
	 * @param element 0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
	 * @param affinity don't set it larger than 2^32-1 mmkay?
	 */
	void setAffinity(ItemStack is,int element,float affinity);
	/**
	 * @param element 0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
	 * @return an int representing the affinity of an element
	 */
	float getAffinity(ItemStack is,int element);
	
public void addAffinity(ItemStack is, float... aff);
}
