package com.Jackiecrazi.taoism.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.inventory.SlotLianQiOnly;

public class ContainerDing extends Container {
	private TileDing ding;
	/*
	 * SLOTS:
	 * 
	 * Tile Entity 0-8 ........ 0  - 6
	 * Player Inventory 9-35 .. 7  - 33
	 * Player Inventory 0-8 ... 34 - 42
	 */
	public ContainerDing(TileDing tile,IInventory playerInv) {
		ding=tile;
		// Tile Entity, Slot 0-6, Slot IDs 0-6
	        this.addSlotToContainer(new SlotLianQiOnly(ding,0,7,20));//18
	        this.addSlotToContainer(new SlotLianQiOnly(ding,1,39,8));
	        this.addSlotToContainer(new SlotLianQiOnly(ding,2,69,23));
	        this.addSlotToContainer(new SlotLianQiOnly(ding,3,97,34));//50
	        this.addSlotToContainer(new SlotLianQiOnly(ding,4,105,63));//79
	        this.addSlotToContainer(new SlotLianQiOnly(ding,5,141,63));//79
	        this.addSlotToContainer(new SlotLianQiOnly(ding,6,150,34));//50
	    // Player Inventory, Slot 7-34, Slot IDs 7-33
	    for (int y = 0; y < 3; ++y) {
	        for (int x = 0; x < 9; ++x) {
	            this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
	        }
	    }

	    // Player Inventory, Slot 0-8, Slot IDs 34-42
	    for (int x = 0; x < 9; ++x) {
	        this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
	    }
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return this.ding.isUseableByPlayer(p);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
	    ItemStack previous = null;
	    Slot slot = (Slot) this.inventorySlots.get(fromSlot);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack current = slot.getStack();
	        previous = current.copy();

	        if (fromSlot < 7) {
	            // From TE Inventory to Player Inventory
	            if (!this.mergeItemStack(current, 7, 43, true))
	                return null;
	        } else {
	            // From Player Inventory to TE Inventory
	            if (!this.mergeItemStack(current, 0, 7, false))
	                return null;
	        }
	        //TODO more custom behaviour goes here

	        if (current.stackSize == 0)
	            slot.putStack((ItemStack) null);
	        else
	            slot.onSlotChanged();
	        if (current.stackSize == previous.stackSize)
	            return null;
	        slot.onPickupFromSlot(playerIn, current);
	    }
	    return previous;
	}
}
