package com.Jackiecrazi.taoism.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.common.items.resource.ItemResource;

public class SlotLianQiOnly extends Slot {

	public SlotLianQiOnly(IInventory p_i1824_1_, int p_i1824_2_,
			int p_i1824_3_, int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		// TODO Auto-generated constructor stub
	}
	public boolean isItemValid(ItemStack s)
    {
        return s!=null&&s.getItem() instanceof ItemResource;
    }
}
