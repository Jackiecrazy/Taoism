package com.jackiecrazi.taoism.common.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.ItemDummy;
import com.jackiecrazi.taoism.common.item.weapon.TaoArrow;
import com.jackiecrazi.taoism.common.item.weapon.TaoBow;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;

public class TaoItems {
	public static ItemDummy part = new ItemDummy();
	public static TaoWeapon weap = new TaoWeapon();
	public static TaoBow bow = new TaoBow();
	public static ItemBlueprint blueprint = new ItemBlueprint();
	public static TaoArrow arrow=new TaoArrow();

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(weap);
		event.getRegistry().register(part);
		event.getRegistry().register(blueprint);
		event.getRegistry().register(bow);
		event.getRegistry().register(arrow);
	}

}
