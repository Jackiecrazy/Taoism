package com.Jackiecrazi.taoism.common.block.special;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.block.tile.TileAltar;

public class BlockShrine extends Block implements ITileEntityProvider{
	private final String name = "shrine";
	public BlockShrine()
	{
		super(Material.rock);
		setCreativeTab(Taoism.TabTaoistWeapon);
		setBlockName("Shrine");
		setHarvestLevel("pickaxe", 2);
        isBlockContainer = false;
        setHardness(2);
        }
	public String getName()
	{
	return name;
	}
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