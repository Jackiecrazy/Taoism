package com.Jackiecrazi.taoism.common.block.special;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TileBellows;
import com.Jackiecrazi.taoism.common.block.tile.TileDummy;

public class BlockBellows extends BlockContainer {

	public BlockBellows() {
		super(Material.wood);
		this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setBlockName("TaoisticBellows");
		this.setBlockTextureName("taoism:transparent");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileBellows();
	}

	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block block, int metadata) {
		super.breakBlock(world, x, y, z,
				block, metadata);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int meta, float hitx, float hity, float hitz)
	{
		TileBellows be = (TileBellows)w.getTileEntity(x, y, z);
		if(be.getWind()==0){
			be.setWind(30);
			/*be.markDirty();
			w.markBlockForUpdate(x, y, z);*/
			//System.out.println("wind");
			
		}
		return true;
	}
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }
    
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		boolean create=false;
		if(entity instanceof EntityPlayer)create=((EntityPlayer)entity).capabilities.isCreativeMode;
		int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            world.setBlockMetadataWithNotify(x, y, z, l, 2);
            System.out.println("set rot to "+l);
            int xx=x,yy=y,zz=z;
            switch(l){
            case 0:zz++;break;
            case 1:xx--;break;
            case 2:zz--;break;
            case 3:xx++;break;
            }
            if(world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz)&&world.getBlock(xx, yy+1, zz).isReplaceable(world, xx, yy+1, zz)&&world.getBlock(xx, y+1, zz).isReplaceable(world, xx, y+1, zz)){
				world.setBlock(xx, yy, zz, TaoBlocks.Dummy);
				world.setTileEntity(xx, yy, zz, new TileDummy().setX(x).setY(y).setZ(z));
				((TileDummy)world.getTileEntity(xx, yy, zz)).setIsSlave(true);
				yy++;
				world.setBlock(xx, yy, zz, TaoBlocks.Dummy);
				world.setTileEntity(xx, yy, zz, new TileDummy().setX(x).setY(y).setZ(z));
				((TileDummy)world.getTileEntity(xx, yy, zz)).setIsSlave(true);
				
				world.setBlock(x, y+1, z, TaoBlocks.Dummy);
				world.setTileEntity(x, y+1, z, new TileDummy().setX(x).setY(y).setZ(z));
				((TileDummy)world.getTileEntity(x, y+1, z)).setIsSlave(true);
				if(!create)is.stackSize--;
			}
            else{
            	world.setBlock(x, y, z, Blocks.air);
    			if (entity instanceof EntityPlayer){
    				EntityPlayer player = (EntityPlayer)entity;
    				if(!create)
    				player.inventory.addItemStackToInventory(new ItemStack(TaoBlocks.Bellows));
    			}
    			return;
            }
	}
}
