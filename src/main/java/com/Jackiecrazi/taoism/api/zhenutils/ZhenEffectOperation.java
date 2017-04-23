package com.Jackiecrazi.taoism.api.zhenutils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import com.Jackiecrazi.taoism.api.TaoistPosition;
/**
 * Does something to the list of entities passed by the filter
 * @author Jackiecrazy
 *
 */
public abstract class ZhenEffectOperation {
	private int cost;
	private static HashMap<Block,ZhenEffectOperation> blockToOp=new HashMap<Block,ZhenEffectOperation>();
	/**
	 * 
	 * @param source the block that triggers this
	 * @param lingcost
	 */
	public ZhenEffectOperation(Block source,int lingcost) {
		blockToOp.put(source, this);
		cost=lingcost;
	}
	/**
	 * Takes the array generated and applies effects to each entity.
	 * NOTICE IT CAN PASS YOU NULL!
	 * @param te the tile. Access needed stuff here.
	 * @param e an array of entities, preferably passed from shapes
	 * @param zem additional mods to apply
	 */
	public abstract void performEffect(TileEntity te, Entity[] e,ZhenEffectModifier zem);
	/**
	 * Takes the array generated and applies effects to each block.
	 * NOTICE IT CAN PASS YOU NULL!
	 * @param te the tile. Access needed stuff here.
	 * @param e an array of block positions, preferably passed from shapes
	 * @param zem additional mods ot apply
	 */
	public abstract void performEffect(TileEntity te, TaoistPosition[] e,ZhenEffectModifier zem);
	/**
	 * Modifies behaviour, replacing with new ones if needed
	 * @param zem
	 */
	protected abstract void addModifierEffect(ZhenEffectModifier zem);
	
	/**
	 * Be sure to call this before applying the effects
	 * @param b the block
	 * @return the operation assigned with the block, or null
	 */
	public static ZhenEffectOperation getEffectByBlock(Block b){
		return blockToOp.get(b);
	}
	public int getPrice(){
		return cost;
	}
}
