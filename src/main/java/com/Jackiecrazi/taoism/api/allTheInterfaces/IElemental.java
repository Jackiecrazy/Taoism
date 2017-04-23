/**
 * 
 */
package com.Jackiecrazi.taoism.api.allTheInterfaces;

import net.minecraft.item.ItemStack;

public interface IElemental {
	
	/**
	 * @return a list of elemental affinities, in the order metal, wood, water, fire, earth
	 */
	int[] getAffinities(ItemStack is);
	void setAffinities(ItemStack is,int metal,int wood, int water, int fire,int earth);
	/**
	 * @param element 0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
	 * @param affinity don't set it larger than 2^32-1 mmkay?
	 */
	void setAffinity(ItemStack is,int element,int affinity);
	/**
	 * @param element 0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
	 * @return an int representing the affinity of an element
	 */
	int getAffinity(ItemStack is,int element);
	/**
	 * same as setAffinity except it adds instead
	 * @param is
	 * @param metal
	 * @param wood
	 * @param water
	 * @param fire
	 * @param earth
	 */
public void addAffinity(ItemStack is, int metal, int wood, int water, int fire, int earth);
}
