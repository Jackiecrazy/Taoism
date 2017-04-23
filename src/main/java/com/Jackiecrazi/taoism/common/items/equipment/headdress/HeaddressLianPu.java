package com.Jackiecrazi.taoism.common.items.equipment.headdress;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeaddressLianPu extends BaseHeaddress {

	public HeaddressLianPu() {
		super("lianpu");
		
	}

	@Override
	public void onEquipped(ItemStack is, EntityPlayer ep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void equippedTick(ItemStack is, EntityPlayer ep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnequipped(ItemStack is, EntityPlayer ep) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canUnequip(ItemStack is, EntityPlayer ep) {
		return false;
	}

	@Override
	public boolean canEquip(ItemStack is, EntityPlayer ep) {
		return true;
	}

}
