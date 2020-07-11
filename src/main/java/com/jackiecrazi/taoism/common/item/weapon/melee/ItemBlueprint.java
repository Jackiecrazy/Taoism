package com.jackiecrazi.taoism.common.item.weapon.melee;

import net.minecraft.item.Item;

public class ItemBlueprint extends Item {
	//organization: method that returns a list of custom slots. these slots inherit the type of wsw (e.g. head, siyah) and have an index that they use to cycle through the wsw list
	//clicking on the slot when it's empty brings up the selection screen
	//XXX real world interaction? if so, how?
	public ItemBlueprint() {
		this.setUnlocalizedName("burupurinto");
		this.setRegistryName("taoblueprint");
		//this.setCreativeTab(Taoism.tabBlu);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
}
