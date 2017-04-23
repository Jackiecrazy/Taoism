package com.Jackiecrazi.taoism.common.block.tile;
import net.minecraft.block.Block;
import cpw.mods.fml.common.registry.GameRegistry;

public final class ModTEs {
	public static Block Altar;
    public static void init() {
        GameRegistry.registerTileEntity(TileAltar.class, "altar");
        GameRegistry.registerTileEntity(TileShrine.class, "shrine");
        GameRegistry.registerTileEntity(TileDing.class, "ding");
        GameRegistry.registerTileEntity(TileDummy.class, "dummy");
        GameRegistry.registerTileEntity(TileAnvil.class, "TaoisticAnvil");
        GameRegistry.registerTileEntity(TileBellows.class, "TaoisticBellows");
    }

}