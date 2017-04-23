package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffect;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectFilter;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectModifier;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectOperation;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectShape;
//TODO different classes of zhenjiao have different stats, namely the range maxes
//how to balance the generating zhen effect?
public class TileZhenJiao extends AbstractZhenComponent {
	private TaoistPosition shapePos, filterPos, opPos, modPos;
	private TaoistPosition effectStart, effectEnd;// this is used to determine
													// the area of the selection
													// and shape
	private ZhenEffect spell = new ZhenEffect(
			ZhenEffectShape.getEffectFromBlock(worldObj.getBlock(
					shapePos.getX(), shapePos.getY(), shapePos.getZ())),
			ZhenEffectFilter.getEffectByBlock(worldObj.getBlock(
					filterPos.getX(), filterPos.getY(), filterPos.getZ())),
			ZhenEffectOperation.getEffectByBlock(worldObj.getBlock(
					opPos.getX(), opPos.getY(), opPos.getZ())),
			ZhenEffectModifier.getModifierByBlock(worldObj.getBlock(
					modPos.getX(), modPos.getY(), modPos.getZ())));

	public TileZhenJiao(int meta) {
		super(meta);
	}

	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		shapePos = new TaoistPosition(c.getIntArray("shape"), worldObj);
		filterPos = new TaoistPosition(c.getIntArray("filter"), worldObj);
		opPos = new TaoistPosition(c.getIntArray("operation"), worldObj);
		modPos = new TaoistPosition(c.getIntArray("modifier"), worldObj);
		effectStart = new TaoistPosition(c.getIntArray("start"), worldObj);
		effectEnd = new TaoistPosition(c.getIntArray("end"), worldObj);
	}

	public void writeToNBT(NBTTagCompound c) {
		super.writeToNBT(c);
		c.setIntArray("shape", shapePos.toArray());
		c.setIntArray("filter", filterPos.toArray());
		c.setIntArray("operation", opPos.toArray());
		c.setIntArray("modifier", modPos.toArray());
		c.setIntArray("start", effectStart.toArray());
		c.setIntArray("end", effectEnd.toArray());
	}

	@Override
	protected boolean receiveLingRequest(int x, int y, int z, int amnt) {
		return false;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (spell.passesNullCheck()) {
			if (this.lingAmount >= spell.getCost() && effectStart != null
					&& effectEnd != null) {
				spell.performEffect(worldObj, this);
				lingAmount -= spell.getCost();
			}
		}
		else{
			if(worldObj.getTotalWorldTime()%100==0)updateSpell(shapePos,filterPos,opPos,modPos);
		}

	}

	/**
	 * make sure to call this when you make the zhenpan
	 * @param shape
	 * @param filter
	 * @param op
	 * @param mod
	 */
	public void updateSpell(TaoistPosition shape, TaoistPosition filter,
			TaoistPosition op, TaoistPosition mod) {
		shapePos = shape;
		filterPos = filter;
		opPos = op;
		modPos = mod;
		spell = new ZhenEffect(ZhenEffectShape.getEffectFromBlock(worldObj
				.getBlock(shapePos.getX(), shapePos.getY(), shapePos.getZ())),
				ZhenEffectFilter.getEffectByBlock(worldObj.getBlock(
						filterPos.getX(), filterPos.getY(), filterPos.getZ())),
				ZhenEffectOperation.getEffectByBlock(worldObj.getBlock(
						opPos.getX(), opPos.getY(), opPos.getZ())),
				ZhenEffectModifier.getModifierByBlock(worldObj.getBlock(
						modPos.getX(), modPos.getY(), modPos.getZ())));
	}
	public void updateShape(TaoistPosition shape){
		updateSpell(shape,filterPos,opPos,modPos);
	}
	public void updateFilter(TaoistPosition filter){
		updateSpell(shapePos,filter,opPos,modPos);
	}
	public void updateOperation(TaoistPosition op){
		updateSpell(shapePos,filterPos,op,modPos);
	}
	public void updateMod(TaoistPosition mod){
		updateSpell(shapePos,filterPos,opPos,mod);
	}

	public TaoistPosition getEffectStart() {
		return effectStart;
	}

	public void setEffectStart(TaoistPosition effectStart) {
		this.effectStart = effectStart;
	}

	public TaoistPosition getEffectEnd() {
		return effectEnd;
	}

	public void setEffectEnd(TaoistPosition effectEnd) {
		this.effectEnd = effectEnd;
	}

	// target range, filter, action, modifier. Targets can be blocks or
	// entities, so check
	// range, shape, action, modifier for blocks

	// same range identifier, returning two coords that represent the start and
	// end of the check

	// the code then will iterate through the area and look for either blocks or
	// entities depending
	// on target identifier, then sort through those and return either
	// TaoistCoords[] or Entity[]
	// the filter and range are determined at once to save calculations
	// depending on target, which then gets sent to action block that calls a
	// hashmap to determine function.

	// Effects have different tags, dictating what "type" of effect it is.
	// Modifiers take that and react accordingly.
	// Non-exhaustive list of tags: damage, pot effects, defense, give ling to
	// other places, boost train speed, teleport

	// some things have no target identifier if they just shoot something or
	// have a non-target effect like wall or summoning
	// instead having two shapes. One defining the shape they spawn in, and one
	// defining what else it does
	// OR two effects for unconditional attack!

}
