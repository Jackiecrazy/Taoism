package com.Jackiecrazi.taoism.api.zhenutils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.api.TaoistPosition;

/**
 * Determines the shape of the zhen, with zhenqi as corners. Available shapes
 * include sphere, chain, pillar, line, cross, circle, wave and wall. The
 * included statement returns taoistposition[] for block statement and entity[]
 * for entity statement.
 * 
 * @author Jackiecrazy
 */
public abstract class ZhenEffectShape {
	private static HashMap<Block, ZhenEffectShape> blockToEffect = new HashMap<Block, ZhenEffectShape>();
	private int price;
	public ZhenEffectShape(Block b, int priceA) {
		blockToEffect.put(b, this);
		price=priceA;
	}

	public static ZhenEffectShape getEffectFromBlock(Block b) {
		if (!blockToEffect.containsKey(b))
			throw new IllegalArgumentException("No such Zhen for block");
		return blockToEffect.get(b);
	}
	public int getPrice(){ return price;}

	/**
	 * Does all the blocks on the edge.<BR>
	 * Does not air check for you, beware.
	 * 
	 * @return a list of positions on the edge of the scan. Wave would get the
	 *         ones on the edge of the projected wave, while circle does the
	 *         circumference, etc.
	 */
	public abstract TaoistPosition[] performEffectBlock(World w,TaoistPosition origin, int minx,int miny, int minz, int maxx,int maxy, int maxz);

	/**
	 * Does all within range.
	 * 
	 * @return an array of entities that were in its way, for effect application purposes.
	 */
	public abstract Entity[] performEffectEntity(World w,TaoistPosition origin, int minx,int miny, int minz, int maxx,int maxy, int maxz);
}
