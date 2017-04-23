package com.Jackiecrazi.taoism.common.items.equipment.headdress;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ITaoistEquipment;

public abstract class BaseHeaddress extends Item implements IElemental, ITaoistEquipment {

	public BaseHeaddress(String d) {
		this.setUnlocalizedName(d);
		this.setMaxStackSize(1);
		this.setCreativeTab(Taoism.TabTaoistAccessories);
		
	}

	@Override
	public abstract void onEquipped(ItemStack is, EntityPlayer ep);

	@Override
	public abstract void equippedTick(ItemStack is, EntityPlayer ep);

	@Override
	public abstract void onUnequipped(ItemStack is, EntityPlayer ep);

	@Override
	public abstract boolean canUnequip(ItemStack is, EntityPlayer ep);

	@Override
	public abstract boolean canEquip(ItemStack is, EntityPlayer ep) ;

	@Override
	public EnumEquipmentType getType(ItemStack is) {
		return EnumEquipmentType.HEADDRESS;
	}

	@Override
	public int[] getAffinities(ItemStack is) {
		int[] ret = new int[10];
		for (int x = 0; x < ret.length; x++) {
			ret[x] = getAffinity(is, x);
		}
		return ret;
	}

	@Override
	public void setAffinities(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinity(is, 0, metal);
		setAffinity(is, 1, wood);
		setAffinity(is, 2, water);
		setAffinity(is, 3, fire);
		setAffinity(is, 4, earth);
	}

	@Override
	public void setAffinity(ItemStack is, int element, int affinity) {
		getAffinityTag(is).setInteger(element + "", affinity);
	}

	@Override
	public int getAffinity(ItemStack is, int element) {
		return getAffinityTag(is).getInteger(element + "");
	}

	@Override
	public void addAffinity(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinities(is, getAffinity(is, 0) + metal,
				getAffinity(is, 1) + wood, getAffinity(is, 2) + water,
				getAffinity(is, 3) + fire, getAffinity(is, 4) + earth);
	}
	protected NBTTagCompound getAffinityTag(ItemStack is) {
		if (!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getCompoundTag("TaoistAffinity");
	}

}
