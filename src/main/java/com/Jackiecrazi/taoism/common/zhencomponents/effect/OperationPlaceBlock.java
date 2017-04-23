package com.Jackiecrazi.taoism.common.zhencomponents.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectModifier;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectOperation;

public class OperationPlaceBlock extends ZhenEffectOperation {
	private Block b;
	public OperationPlaceBlock(Block source, int lingcost,Block toplace) {
		super(source, lingcost);
		b=toplace;
	}

	@Override
	public void performEffect(TileEntity te, Entity[] e, ZhenEffectModifier zem) {
		//should we bury the mob?
	}

	@Override
	public void performEffect(TileEntity te, TaoistPosition[] e,
			ZhenEffectModifier zem) {
		for(TaoistPosition tp:e){
			te.getWorldObj().setBlock(tp.getX(), tp.getY(), tp.getZ(), b);
		}
	}

	@Override
	protected void addModifierEffect(ZhenEffectModifier zem) {
		
	}
	
}
