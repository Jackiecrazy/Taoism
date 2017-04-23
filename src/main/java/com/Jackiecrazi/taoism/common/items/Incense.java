package com.Jackiecrazi.taoism.common.items;

import com.Jackiecrazi.taoism.Taoism;

import net.minecraft.item.Item;

public class Incense extends Item {
	public Incense(){
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.setUnlocalizedName("incense");
		this.setTextureName("Taoism." + getUnlocalizedName());
	}
}
