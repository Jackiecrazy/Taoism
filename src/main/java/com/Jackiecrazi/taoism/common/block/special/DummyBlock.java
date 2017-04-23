package com.Jackiecrazi.taoism.common.block.special;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.Jackiecrazi.taoism.common.block.tile.TileDummy;

public class DummyBlock extends BlockContainer {
	public DummyBlock(Material p_i45394_1_) {
		super(p_i45394_1_);
		this.setHardness(2.0f);
		this.setResistance(10.0f);
		this.setBlockName("taoisticdummyblock");
		this.setLightOpacity(0);
		this.setBlockTextureName("taoism:transparent");//transparent
	}
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what, float these, float are)
    {
    	TileDummy td=(TileDummy) world.getTileEntity(x, y, z);
    	if (td.isSlaveYet()) {
    		//System.out.println(world.isRemote);//after reload the client wouldn't know that the dummy is a slave
            return world.getBlock(td.getX(), td.getY(), td.getZ()).onBlockActivated(world, td.getX(), td.getY(), td.getZ(), player, idk, what, these, are);
        }
        return false;
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
    	((TileDummy)world.getTileEntity(x, y, z)).revolt();
    	super.breakBlock(world, x, y, z, par5, par6);
    	
    	world.removeTileEntity(x, y, z);
    }

	@Override
	public TileEntity createNewTileEntity(World w, int meta) {
		
		return new TileDummy();
	}
	public void onBlockAdded(World w, int x, int y, int z)
    {
		((TileDummy)w.getTileEntity(x, y, z)).setX(x).setY(y).setZ(z);
    }
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
		return false;
    }
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
		return Item.getItemById(0);//nothing
    }
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
		ItemStack ret=null;
    	if(world.getTileEntity(x, y, z)!=null){
    		TileDummy d=(TileDummy)world.getTileEntity(x, y, z);
    		ret=world.getBlock(d.redirectToSource().xCoord , d.redirectToSource().yCoord , d.redirectToSource().zCoord ).getPickBlock(target, world, x, y, z, player);
    	}
    	return ret;
    }
}
