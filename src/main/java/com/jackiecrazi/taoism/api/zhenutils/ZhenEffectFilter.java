/**
 * 
 */
package com.jackiecrazi.taoism.api.zhenutils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import com.jackiecrazi.taoism.api.TaoistPosition;

/**
 * Filters the list of blocks or entities for specific ones. Essentially a
 * generic blacklist/whitelist.
 * 
 * @author wuguoxian
 * 
 */
public class ZhenEffectFilter {
	private static HashMap<Block, ZhenEffectFilter> blockToEffect = new HashMap<Block, ZhenEffectFilter>();
	private Block blockFilter;
	private Entity entityFilter;
	private EntityAIBase aiFilter;
	private EnumCreatureAttribute typeFilter;
	private boolean blacklist;
	private EnumZhenTypes zhenfilter;
	private int price;
	public enum EnumZhenTypes {
		BLOCK, ENTITY, AI, CREATURETYPE
	}
	/**
	 * 
	 * @param source the block that creates the filter
	 * @param bl is it a blacklist
	 * @param variation block, entity, ai or creaturetype
	 * @param priceA the price...
	 */
	private ZhenEffectFilter(Block source, boolean bl, int variation, int priceA) {
		blacklist = bl;
		zhenfilter = EnumZhenTypes.values()[variation];
		blockToEffect.put(source, this);
		price=priceA;
	}

	/**
	 * Generic types like EntityMob or EntityAnimal, please.
	 * 
	 * @param source
	 *            the block you want this effect for
	 * @param e
	 *            entity addition
	 * @param o
	 *            whether it's a blacklist
	 */
	public ZhenEffectFilter(Block source, Entity e, boolean o, int price) {
		this(source, o, 1,price);
		entityFilter = e;
	}

	/**
	 * @param source
	 *            effect block
	 * @param e
	 *            AI filter
	 * @param o
	 *            whether it's a blacklist
	 */
	public ZhenEffectFilter(Block source, EntityAIBase e, boolean o,int price) {
		this(source, o, 2,price);
		aiFilter = e;

	}

	/**
	 * @param source
	 *            effect block
	 * @param e
	 *            block addition
	 * @param o
	 *            whether it's a blacklist
	 * @param price additional price addition to activation cost
	 */
	public ZhenEffectFilter(Block source, Block e, boolean o,int price) {
		this(source, o, 0,price);
		blockFilter = e;
	}

	/**
	 * @param source
	 *            effect block
	 * @param e
	 *            creature type
	 * @param o
	 *            whether it's a blacklist
	 */
	public ZhenEffectFilter(Block source, EnumCreatureAttribute e, boolean o,int price) {
		this(source, o, 3,price);
		typeFilter = e;
	}

	public Block getBlockFilter() {
		return blockFilter;
	}

	public Entity getEntityFilter() {
		return entityFilter;
	}

	public EntityAIBase getAIFilter() {
		return aiFilter;
	}

	public EnumCreatureAttribute getEntityTypeFilter() {
		return typeFilter;
	}

	public boolean isBlacklist() {
		return blacklist;
	}

	public static ZhenEffectFilter getEffectByBlock(Block param) {
		return blockToEffect.get(param);
	}

	public int getPrice(){ return price;}
	
	/**
	 * you should call this before getting the variables and switch to make sure
	 * there are no null cases
	 * @return what the zhen is intended for, for which the the zhen is intended, and this better not be null.
	 */
	public EnumZhenTypes getType() {
		return this.zhenfilter;
	}
	/**
	 * Filters based on block. 
	 * @param w the world
	 * @param tp Block location
	 * @return whether this passes the filter test. An invalid filter will default to true.
	 */
	public boolean passesFilter(World w, TaoistPosition tp){
		boolean ret=true;
		if(blockFilter!=null){
			Block b=w.getBlockState(tp.toBlockPos()).getBlock();
			if(blacklist){
				if(b==blockFilter)ret=false;
			}
			else{
				if(b!=blockFilter)ret=false;
			}
		}
		return ret;
	}
	/**
	 * Filters based on entity. 
	 * @param e the entity to be tested
	 * @return whether the entity passes the filter test. Defaults to true
	 */
	public boolean passesFilter(Entity e){
		boolean ret=true;
		if(blacklist){
			if(e instanceof EntityLiving){
				EntityLiving elb=(EntityLiving)e;
				if(aiFilter!=null){
				ret=!elb.targetTasks.taskEntries.contains(aiFilter)&&!elb.tasks.taskEntries.contains(aiFilter);
				}
				else if(typeFilter!=null)ret=elb.getCreatureAttribute()!=typeFilter;
				
			}
			ret=entityFilter!=e;
		}
		else{
			if(e instanceof EntityLiving){
				EntityLiving elb=(EntityLiving)e;
				if(aiFilter!=null){
				ret=elb.targetTasks.taskEntries.contains(aiFilter)&&elb.tasks.taskEntries.contains(aiFilter);
				}
				else if(typeFilter!=null)ret=elb.getCreatureAttribute()==typeFilter;
				
			}
			ret=entityFilter==e;
		}
		return ret;
	}
}
