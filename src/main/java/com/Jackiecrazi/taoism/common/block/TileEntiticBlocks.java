package com.Jackiecrazi.taoism.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntiticBlocks extends BlockContainer {
	protected TileEntiticBlocks(Material materialIn) {
		super(materialIn);
	}
    
    //It's not a normal block, so you need this too.
    public boolean renderAsNormalBlock() {
            return false;
    }
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}
}
