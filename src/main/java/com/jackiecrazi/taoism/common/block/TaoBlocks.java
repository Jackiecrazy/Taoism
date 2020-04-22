package com.jackiecrazi.taoism.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TaoBlocks {
	//public static BlockWorkstation workstation=new BlockWorkstation();
	//public static ItemBlock workstationI=(ItemBlock) new ItemBlock(workstation).setRegistryName(workstation.getRegistryName());
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Block> event){
		//event.getRegistry().register(workstation);
		//GameRegistry.registerTileEntity(TileWorkstation.class, "taoworkstation");
	}
	@SubscribeEvent
	public static void initI(RegistryEvent.Register<Item> event){
		//event.getRegistry().register(workstationI);
		
	}
	
}
