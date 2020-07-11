package com.jackiecrazi.taoism.common.tile;

import net.minecraft.item.ItemStack;

import com.jackiecrazi.taoism.api.TaoistPosition;

public class TileZhenJiao extends TaoisticInvTE {

	public TileZhenJiao() {
		super(0);
		}

	public TaoistPosition getEffectStart() {
		return null;
	}

	public TaoistPosition getEffectEnd() {
		return null;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

}
