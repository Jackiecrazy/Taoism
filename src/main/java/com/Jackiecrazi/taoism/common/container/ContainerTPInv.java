package com.Jackiecrazi.taoism.common.container;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ITaoistEquipment;
import com.Jackiecrazi.taoism.common.inventory.InventoryTPInv;
import com.Jackiecrazi.taoism.common.inventory.InventoryWujiGongfa;
import com.Jackiecrazi.taoism.common.inventory.MoveableSlot;
import com.Jackiecrazi.taoism.common.inventory.SlotArmor;
import com.Jackiecrazi.taoism.common.inventory.SlotByInterface;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;

public class ContainerTPInv extends Container {
	private EntityPlayer player;
	private IInventory inventoryPlayer;
	private InventoryTPInv itpi;
	private InventoryWujiGongfa iwg;
	private int screen=0;

	public static final int HEADDRESS = 0, MIRROR = HEADDRESS + 1,
			BELT = MIRROR + 1, FAQISTART = MIRROR + 1, FAQIEND = FAQISTART + 1,
			AMULET = FAQIEND + 1, RINGSTART = AMULET + 1,
			RINGEND = RINGSTART + 1, GLOVE = RINGEND + 1, BACK = GLOVE + 1,
			CLOAK = BACK + 1,GONGFA=CLOAK+1,WUJISTART=GONGFA+1,WUJIEND=WUJISTART+3, ARMORSTART = WUJIEND + 1,
			ARMOREND = ARMORSTART + 3, INVSTART = ARMOREND + 1,
			INVEND = INVSTART + 26, HOTBARSTART = INVEND + 1,
			HOTBAREND = HOTBARSTART + 8;

	public ContainerTPInv(EntityPlayer p, IInventory playerInv,
			InventoryTPInv ji) {
		player = p;
		inventoryPlayer = p.inventory;
		itpi = ji;
		iwg=PlayerResourceStalker.get(player).iwg;
		int i;
		int j;

		// equipment!
		this.addSlotToContainer(new SlotByInterface(itpi, 5, 96, 18,
				EnumEquipmentType.AMULET));// amulet
		this.addSlotToContainer(new SlotByInterface(itpi, 6, 96, 36,
				EnumEquipmentType.RING));// ring 1
		this.addSlotToContainer(new SlotByInterface(itpi, 4, 96, 54,
				EnumEquipmentType.FAQI));// faqi2
		// accessories
		for (i = 0; i < 4; i++) {
			this.addSlotToContainer(new SlotByInterface(itpi, i, 132,
					9 + i * 18, EnumEquipmentType.values()[i]));
		}

		this.addSlotToContainer(new SlotByInterface(itpi, 9, 151, 18,
				EnumEquipmentType.BACK));// back
		this.addSlotToContainer(new SlotByInterface(itpi, 7, 151, 36,
				EnumEquipmentType.RING));// ring 2
		this.addSlotToContainer(new SlotByInterface(itpi, 3, 151, 54,
				EnumEquipmentType.GLOVE));// glove TODO isolate the icons to the
											// slot

		for (i = 0; i < 4; ++i) {
			addSlotToContainer(new SlotArmor(player, inventoryPlayer,
					inventoryPlayer.getSizeInventory() - 1 - i, 114,
					9 + i * 18, i));
		}

		// wuji+gongfa
		this.addSlotToContainer(new SlotByInterface(iwg, 0, -888, -777,
				EnumEquipmentType.GONGFA).setOrig(64, 9));//gongfa
		this.addSlotToContainer(new SlotByInterface(iwg, 1, -2333, -10086,
				EnumEquipmentType.WUJI).setOrig(64, 27));//wuji
		this.addSlotToContainer(new SlotByInterface(iwg, 2, -12315, -101,
				EnumEquipmentType.WUJI).setOrig(64, 45));//wuji
		this.addSlotToContainer(new SlotByInterface(iwg, 3, -12315, -88,
				EnumEquipmentType.WUJI).setOrig(64, 64));//wuji
		this.addSlotToContainer(new SlotByInterface(iwg, 4, -12315, -365,
				EnumEquipmentType.WUJI).setOrig(64, 83));//wuji
		
		// vanilla
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1)
						* 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18,
					142));
		}
		itpi.readFromNBT(player.getEntityData());
		iwg.readFromNBT(player.getEntityData());
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
		/*
		 * ItemStack previous = null; Slot slot = (Slot)
		 * this.inventorySlots.get(fromSlot);
		 * 
		 * if (slot != null && slot.getHasStack()) { ItemStack current =
		 * slot.getStack(); previous = current.copy();
		 * 
		 * if (fromSlot < ARMORSTART) { // From Inventory to Player Inventory if
		 * (!this.mergeItemStack(current, ARMORSTART, HOTBAREND, true)) return
		 * null; } else { // From Player Inventory to Inventory if
		 * (!this.mergeItemStack(current, 0, ARMORSTART, false)) return null; }
		 * 
		 * if (current.stackSize == 0) slot.putStack((ItemStack) null); else
		 * slot.onSlotChanged(); if (current.stackSize == previous.stackSize)
		 * return null; slot.onPickupFromSlot(playerIn, current); } return
		 * previous;
		 */
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// Either armor slot or custom item slot was clicked
			if (fromSlot < INVSTART) {
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INVSTART, HOTBAREND + 1,
						true)) {
					// System.out.println("merge failed");
					return null;
				}
				// System.out.println("merged");
				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in inventory / hotbar, try to place either in custom or
			// armor slots
			else {
				// System.out.println("not inv");
				// if item is our custom item
				if (itemstack1.getItem() instanceof ITaoistEquipment) {
					if (!this.mergeItemStack(itemstack1, 0,
							InventoryTPInv.size, false)) {
						return null;
					}
				}
				// if item is armor
				else if (itemstack1.getItem() instanceof ItemArmor) {
					int type = ((ItemArmor) itemstack1.getItem()).armorType;
					if (!this.mergeItemStack(itemstack1, ARMORSTART + type,
							ARMORSTART + type + 1, false)) {
						return null;
					}
				}
				// item in player's inventory, but not in action bar
				else if (fromSlot >= INVSTART && fromSlot < HOTBARSTART) {
					// place in action bar
					if (!this.mergeItemStack(itemstack1, HOTBARSTART,
							HOTBARSTART + 1, false)) {
						return null;
					}
				}
				// item in action bar - place in player inventory
				else if (fromSlot >= HOTBARSTART && fromSlot < HOTBAREND + 1) {
					if (!this.mergeItemStack(itemstack1, INVSTART, INVEND + 1,
							false)) {
						return null;
					}
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer p) {
		itpi.writeToNBT(p.getEntityData());
	}

	protected boolean mergeItemStack(ItemStack is, int rangemin, int rangemax,
			boolean reverse) {
		boolean ret = false;
		int k = rangemin;

		if (reverse) {
			k = rangemax - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (is.isStackable()) {
			while (is.stackSize > 0
					&& (!reverse && k < rangemax || reverse && k >= rangemin)) {
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 != null
						&& itemstack1.getItem() == is.getItem()
						&& (!is.getHasSubtypes() || is.getItemDamage() == itemstack1
								.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(is, itemstack1)) {
					int l = itemstack1.stackSize + is.stackSize;

					if (l <= is.getMaxStackSize()) {
						is.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						ret = true;
					} else if (itemstack1.stackSize < is.getMaxStackSize()) {
						is.stackSize -= is.getMaxStackSize()
								- itemstack1.stackSize;
						itemstack1.stackSize = is.getMaxStackSize();
						slot.onSlotChanged();
						ret = true;
					}
				}

				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}

		if (is.stackSize > 0) {
			if (reverse) {
				k = rangemax - 1;
			} else {
				k = rangemin;
			}

			while (!reverse && k < rangemax || reverse && k >= rangemin) {
				// System.out.println("attempt");
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 == null && slot.isItemValid(is)&&slot.xDisplayPosition>0&&slot.yDisplayPosition>0) {
					slot.putStack(is.copy());
					slot.onSlotChanged();
					is.stackSize = 0;
					ret = true;
					break;
				}

				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public void change(int to) {
		screen=to;
		// 0 is equip, 1 is gongfa+wuji, 2 is resistance. TODO moar!
		IInventory filter;
		switch (to) {
		case 0:
			//System.out.println("to itpi");
			filter = this.itpi;
			break;
		case 1:
			//System.out.println("to iwg");
			filter = this.iwg;
			break;
		default:
			//System.out.println("to default");
			filter = this.itpi;
		}
		Iterator<Slot> ite = this.inventorySlots.iterator();
		while (ite.hasNext()) {
			Slot ne = ite.next();
			if(ne instanceof MoveableSlot){
				MoveableSlot ms=(MoveableSlot)ne;
				if (ms.inventory != filter){
					//System.out.println("trumping");
					ms.xDisplayPosition = -999999;
					ms.yDisplayPosition=-999999;
				}
				else{
					ms.xDisplayPosition=ms.getOrigX();
					ms.yDisplayPosition=ms.getOrigY();
				}
			}
		}
	}
	public int getScreen(){
		return screen;
	}
}
