package com.jackiecrazi.taoism.common.item.weapon;

import com.jackiecrazi.taoism.Taoism;
import net.minecraft.item.Item;

public class ItemBlueprint extends Item {
	//TODO add instances that correspond to weapon, bow, arrow, armor, etc. and define them to open slots that only accept materials of a type that is called by the weaponstatwrapper of the one
	//organization: method that returns a list of custom slots. these slots inherit the type of wsw (e.g. head, siyah) and have an index that they use to cycle through the wsw list
	//clicking on the slot when it's empty brings up the selection screen
	//XXX real world interaction? if so, how?
	public ItemBlueprint() {
		this.setUnlocalizedName("burupurinto");
		this.setRegistryName("taoblueprint");
		this.setCreativeTab(Taoism.tabBlu);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
}
