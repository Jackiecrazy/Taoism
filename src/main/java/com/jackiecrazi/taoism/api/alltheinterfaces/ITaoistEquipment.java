package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ITaoistEquipment {
	public void onEquipped(ItemStack is, EntityPlayer ep);
	public void equippedTick(ItemStack is, EntityPlayer ep);
	public void onUnequipped(ItemStack is, EntityPlayer ep);
	public boolean canUnequip(ItemStack is, EntityPlayer ep);
	public boolean canEquip(ItemStack is, EntityPlayer ep);
	public EnumEquipmentType getType(ItemStack is);
	}
