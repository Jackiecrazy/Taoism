package com.jackiecrazi.taoism.common.block;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.block.tile.TileTempExplosion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TaoBlocks {
    //public static BlockWorkstation workstation=new BlockWorkstation();
    //public static ItemBlock workstationI=(ItemBlock) new ItemBlock(workstation).setRegistryName(workstation.getRegistryName());
    public static BlockTempExplosion temp = null;

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Block> event) {
        temp = new BlockTempExplosion();
        event.getRegistry().register(temp);
        GameRegistry.registerTileEntity(TileTempExplosion.class, new ResourceLocation(Taoism.MODID, "explosion"));
        //event.getRegistry().register(workstation);
        //GameRegistry.registerTileEntity(TileWorkstation.class, "taoworkstation");
    }

    @SubscribeEvent
    public static void initI(RegistryEvent.Register<Item> event) {
        //event.getRegistry().register(workstationI);

    }

}
