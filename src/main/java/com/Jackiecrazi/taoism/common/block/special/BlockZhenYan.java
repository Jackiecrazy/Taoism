package com.Jackiecrazi.taoism.common.block.special;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.common.block.tile.TileZhenYan;

public class BlockZhenYan extends BlockContainer {
	/*TODO let's try this again. Zhenyan is the router. Zhenqi use the zhenyan to distribute or gain power. 
	Zhenjiao either use or generate power based on surrounding blocks. You can define which to use. EG lingshi blocks gen power
	use zhenqi to link zhenjiao and zhenyan
	zhenyan: battery and router of power
	zhenqi: wires for power
	zhenjiao: actually does something, either produces or uses ling based on surroundings
	logic: check blocks around on update and compile into a hashmap stored in the te, then on update read the hashmap vvvvv
	compile block effects into one package, constructor Block as key. Add it to hashmap with effect as value.
	determine if the block exists in the hashmap each tick, and if so perform effect 
	(determine form first block, then get list of targets in form second block, third block determines applied effect varies from block to entity, 
	fourth block is modifier which is determined in third block effect class for special modifiers*/
	
	//let's use block meta to determine ling proficiency!
	public BlockZhenYan(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int meta) {
		return new TileZhenYan(meta);
	}

}
