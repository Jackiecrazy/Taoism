package com.jackiecrazi.taoism.common.block.special;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.tile.TilePedestal;
import com.jackiecrazi.taoism.common.tile.TileWorkstation;

public class BlockWorkstation extends BlockContainer {
	
	public BlockWorkstation() {
		super(Material.ROCK);
		this.setCreativeTab(Taoism.tabBlu);
		this.setRegistryName("workstation");
		this.setUnlocalizedName("workstation");
	}

	public BlockWorkstation(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
		this.setCreativeTab(Taoism.tabBlu);
		this.setRegistryName("workstation");
		this.setUnlocalizedName("workstation");
	}

	public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (w.getTileEntity(pos) instanceof TilePedestal) return ((TilePedestal) w.getTileEntity(pos)).onRightClick(p, hand, facing, hitX, hitY, hitZ);
		return false;
	}

	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
		if (world.getTileEntity(pos) instanceof TilePedestal) ((TilePedestal) world.getTileEntity(pos)).refresh();

	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}
	
	public TileEntity createNewTileEntity(World worldIn, int meta)
    {
		return new TileWorkstation();
    }
}
