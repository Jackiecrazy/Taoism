package com.jackiecrazi.taoism.common.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.client.gui.SlotLianQi;
import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.TaoBow;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;

public class TaoItems {
	//public static ItemWeaponPart parts=new ItemWeaponPart("part");
	public static TaoWeapon weap=new TaoWeapon();
	public static ItemBlueprint blueprint=new ItemBlueprint(){

		@Override
		public SlotLianQi[] getSlots(IInventory in) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	public static TaoBow bow=new TaoBow();
	public static Item renderdummy=new Item().setRegistryName("dumdum").setHasSubtypes(true).setUnlocalizedName("ignorethis");
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event){
		event.getRegistry().register(weap);
		event.getRegistry().register(blueprint);
		event.getRegistry().register(bow);
	}
}
