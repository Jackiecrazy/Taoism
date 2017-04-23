package com.Jackiecrazi.taoism.common.block.special;

import com.Jackiecrazi.taoism.common.block.tile.TileAltar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBarrel extends BlockContainer {

	public BlockBarrel(Material p_i45386_1_) {
		super(p_i45386_1_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
        System.out.println("TE created");
        return new TileAltar();
    }

    @Override
    public void breakBlock(World world, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.breakBlock(world,p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_,p_149749_6_);
        world.removeTileEntity(p_149749_2_,p_149749_3_,p_149749_4_);
        System.out.println("Te removed");
    }

}
