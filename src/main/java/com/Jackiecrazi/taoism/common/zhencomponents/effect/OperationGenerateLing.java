package com.Jackiecrazi.taoism.common.zhencomponents.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectModifier;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectOperation;

public class OperationGenerateLing extends ZhenEffectOperation {

	/**
	 * Operations that generate ling and do nothing else...
	 * @param source the activating block
	 * @param lingcost the constructor auto-converts to negative values for the operation.
	 */
	public OperationGenerateLing(Block source, int lingcost) {
		super(source, -lingcost);
	}

	@Override
	public void performEffect(TileEntity te, Entity[] e, ZhenEffectModifier zem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performEffect(TileEntity te, TaoistPosition[] e,
			ZhenEffectModifier zem) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addModifierEffect(ZhenEffectModifier zem) {
		
	}

}
