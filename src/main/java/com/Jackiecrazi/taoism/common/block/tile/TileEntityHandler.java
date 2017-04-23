package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityHandler extends TileEntity implements IInventory {
	protected ItemStack[] inv;
	public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }
	public ItemStack getStackInSlot(int slot)
    {
        return inv[slot];
    }
	
}
