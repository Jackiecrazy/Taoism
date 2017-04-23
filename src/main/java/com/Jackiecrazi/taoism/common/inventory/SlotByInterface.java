package com.Jackiecrazi.taoism.common.inventory;

import java.util.Arrays;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ITaoistEquipment;
import com.Jackiecrazi.taoism.common.items.DummySlotItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotByInterface extends MoveableSlot {
	private EnumEquipmentType filter;
	public SlotByInterface(IInventory inv, int slot,
			int x, int y,EnumEquipmentType eet) {
		super(inv, slot, x, y,x,y);
		filter=eet;
	}

	public int getSlotStackLimit()
    {
        return 1;
    }
	
	public boolean isItemValid(ItemStack is)
    {
		boolean ret=false;
		if(is!=null){
			if(is.getItem()!=null&&is.getItem() instanceof ITaoistEquipment&&((ITaoistEquipment)is.getItem()).getType(is)==filter)ret=true;
		}
        return ret;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex() {
		return DummySlotItem.mark[Arrays.asList(EnumEquipmentType.values()).indexOf(filter)];
	}
	
	/*public boolean canTakeStack(EntityPlayer p)
    {
        return (this.inventory.getStackInSlot(slotNumber)!=null&&this.inventory.getStackInSlot(slotNumber).getItem() instanceof ITaoistEquipment&&
        		((ITaoistEquipment)this.inventory.getStackInSlot(slotNumber).getItem()).canUnequip(is, ep));
    }*/
	
	
}
