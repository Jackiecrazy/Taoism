package com.Jackiecrazi.taoism.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.Jackiecrazi.taoism.common.block.special.BlockAltar;
import com.Jackiecrazi.taoism.common.block.special.BlockAnvil;
import com.Jackiecrazi.taoism.common.block.special.BlockBellows;
import com.Jackiecrazi.taoism.common.block.special.BlockLianQiDing;
import com.Jackiecrazi.taoism.common.block.special.BlockShrine;
import com.Jackiecrazi.taoism.common.block.special.DummyBlock;

import cpw.mods.fml.common.registry.GameRegistry;

public class TaoBlocks {
    public static Block Altar, Shrine,LianQiDing,Anvil,Bellows;
    public static Block Arboreal,PoisonSand,Dummy,LingOre;
    public static Block Sapling;
    static{
		Altar = new BlockAltar();
		Sapling = new TaoisticSaplingGeneric();
		Arboreal = new TaoisticWoodGeneric();
		Shrine = new BlockShrine();
		PoisonSand = new BlockPoisonSand();
		LianQiDing=new BlockLianQiDing();
		Dummy=new DummyBlock(Material.rock);
		Anvil=new BlockAnvil(Material.anvil);
		Bellows=new BlockBellows();
		LingOre=new TaoisticOreBlock("lingore","taoism:orelingshi");
    }
	public static void init()
    {
	
		//GameRegistry.registerBlock(Altar, "altar");
	//GameRegistry.registerBlock(Sapling, "TaoisticSapling");
	//GameRegistry.registerTileEntity(TileAltar.class, "TEAltar");
	//GameRegistry.registerBlock(Arboreal, "TaoisticWood");
	//GameRegistry.registerBlock(Shrine, "shrine");
	//GameRegistry.registerBlock(PoisonSand, "PoisonSand");
		GameRegistry.registerBlock(LianQiDing, "TaoisticDing");
		GameRegistry.registerBlock(Dummy, "TaoisticDummy");
		GameRegistry.registerBlock(Anvil, "TaoisticAnvil");
		GameRegistry.registerBlock(Bellows, "TaoisticBellows");
		GameRegistry.registerBlock(LingOre, "taolingore");
    }
	
}
