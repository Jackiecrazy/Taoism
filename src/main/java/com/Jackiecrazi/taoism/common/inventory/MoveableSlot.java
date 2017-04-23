package com.Jackiecrazi.taoism.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;

import com.Jackiecrazi.taoism.common.items.DummySlotItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MoveableSlot extends Slot {
	private int origx, origy;
	public MoveableSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_,
			int p_i1824_4_, int ox, int oy) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		origx=ox;
		origy=oy;
	}
	public MoveableSlot setOrig(int x, int y){
		origx=x;
		origy=y;
		return this;
	}

	public int getOrigX(){
		return origx;
	}
	
	public int getOrigY(){
		return origy;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex() {
		return DummySlotItem.emptySlot;
	}
}
