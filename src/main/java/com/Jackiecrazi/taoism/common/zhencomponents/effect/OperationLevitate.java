package com.Jackiecrazi.taoism.common.zhencomponents.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectModifier;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectOperation;

public class OperationLevitate extends ZhenEffectOperation {

	public OperationLevitate(Block source, int lingcost) {
		super(source, lingcost);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void performEffect(TileEntity te, Entity[] e, ZhenEffectModifier zem) {
		
	}

	@Override
	public void performEffect(TileEntity te, TaoistPosition[] e,
			ZhenEffectModifier zem) {
		
	}

	@Override
	protected void addModifierEffect(ZhenEffectModifier zem) {
		// TODO Auto-generated method stub

	}

}
