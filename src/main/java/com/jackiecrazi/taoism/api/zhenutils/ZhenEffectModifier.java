package com.jackiecrazi.taoism.api.zhenutils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
/**
 * To be very honest with you I'm not completely sure how to go around this yet.
 * @author Jackiecrazy
 *
 */
public abstract class ZhenEffectModifier {
	public static HashMap<Block,ZhenEffectModifier> blockToModifier=new HashMap<Block,ZhenEffectModifier>();
	private int price;
	public ZhenEffectModifier(Block b) {
		blockToModifier.put(b, this);
	}
	public static ZhenEffectModifier getModifierByBlock(Block b){
		return blockToModifier.get(b);
	}
	/**
	 * Modifies behaviour, scales damage, status power or status duration
	 * @param te the tile, access world and stuff here
	 * @param original number, can be used differently to increase different things. Experiment!
	 */
	public abstract int increaseModifierBuff(TileEntity te,int orig);
	public int getPrice(){return price;}
}
