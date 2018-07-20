package com.jackiecrazi.taoism.common.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.client.gui.SlotLianQi;
import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.ItemDummy;
import com.jackiecrazi.taoism.common.item.weapon.TaoBow;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;

public class TaoItems {
	public static ItemDummy dummy = new ItemDummy();
	public static TaoWeapon weap = new TaoWeapon();
	public static TaoBow bow = new TaoBow();
	public static ItemBlueprint blueprint = new ItemBlueprint() {

		@Override
		public SlotLianQi[] getSlots(IInventory in) {
			return null;
		}

	};

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(weap);
		event.getRegistry().register(dummy);
		event.getRegistry().register(blueprint);
		event.getRegistry().register(bow);
	}

}
