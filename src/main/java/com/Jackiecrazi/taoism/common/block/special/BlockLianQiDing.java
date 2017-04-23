package com.Jackiecrazi.taoism.common.block.special;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.client.gui.TaoisticGuiHandler;
import com.Jackiecrazi.taoism.common.block.ModBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.block.tile.TileDummy;
import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLianQiDing extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon2;
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;
	//TODO get this some tile data to store coordinate
	public BlockLianQiDing() {
		super(Material.rock);
		this.setHardness(2.0f);
		this.setBlockName("blockLianQiDing");
		this.setResistance(10.0f);
		//this.setBlockBounds(-1f, 0f, -1f, 2f, 4f, 2f);
		//this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setBlockTextureName("taoism:transparent");
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
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileDing();
	}

	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block block, int metadata) {
		super.breakBlock(world, x, y, z,
				block, metadata);
		world.removeTileEntity(x, y, z);
		for (int xc = x - 1; xc < x + 2; xc++)
			for (int yc = y; yc < y + 5; yc++)
				for (int zc = z - 1; zc < z + 2; zc++) {
					TileEntity tile = world.getTileEntity(x, y, z);
					// Make sure tile isn't null, is an instance of the same Tile, and isn't already a part of a multiblock
					if (tile != null && (tile instanceof TileDummy)) {
						TileDummy d=(TileDummy) world.getTileEntity(xc, yc, zc);
						if(d.isSlaveYet()&&d.getX()==x&&d.getY()==y&&d.getZ()==z){
							world.setBlockToAir(xc, yc, zc);
							world.removeTileEntity(xc, yc, zc);
						}
					}
				}
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.topIcon = iconRegister.registerIcon("taoism:dingTop");
		this.sideIcon2 = iconRegister.registerIcon("taoism:dingSide");
		this.bottomIcon = iconRegister.registerIcon("taoism:dingBottom");
	}*/

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int p_149727_6_, float p_149727_7_,
			float p_149727_8_, float p_149727_9_) {
		player.openGui(Taoism.inst, TaoisticGuiHandler.DING, world,x,y,z);
		return true;
	}
	public void onBlockAdded(World w, int x, int y, int z)
	{
		super.onBlockAdded(w, x, y, z);
		/*for(int xx=-1;xx<=1;xx++){
			for(int yy=0;yy<=3;yy++){
				for(int zz=-1;zz<=1;zz++){
					if(w.getBlock(x+xx, y+yy, z+zz).isReplaceable(w, x+xx, y+yy, z+zz)){
						w.setBlock(x+xx, y+yy, z+zz, ModBlocks.Dummy);
						w.setTileEntity(xx+x, yy+y, zz+z, new TileDummy());
					}
				}
			}
		}*/
	}
	public boolean canPlaceBlockAt(World w, int x, int y, int z)
	{
		/*boolean ret=true;
		for(int xx=-1;xx<=1;xx++){
			for(int yy=0;yy<=3;yy++){
				for(int zz=-1;zz<=1;zz++){
					if(!w.getBlock(x+xx, y+yy, z+zz).isReplaceable(w, x+xx, y+yy, z+zz))ret=false;
				}
			}
		}
		return ret;*/
		return true;
	}
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
	{
		return ModItems.Ding;
	}
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		return new ItemStack(ModItems.Ding);
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		boolean create=false;
		if(entity instanceof EntityPlayer)create=((EntityPlayer)entity).capabilities.isCreativeMode;
		int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
		System.out.println("set rot to "+l);
		int xx=x,yy=y,zz=z;//other side of square
		switch(l){
		case 0:zz++;xx++;break;
		case 1:xx--;zz++;break;
		case 2:zz--;xx--;break;
		case 3:xx++;zz--;break;
		}
		if(world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz)&&world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz)){
			world.setBlock(xx, yy, zz, ModBlocks.Dummy);
			world.setTileEntity(xx, yy, zz, new TileDummy().setX(x).setY(y).setZ(z));
			((TileDummy)world.getTileEntity(xx, yy, zz)).setIsSlave(true);
			if(!create)is.stackSize--;
		}
		else{
			world.setBlock(x, y, z, Blocks.air);
			if (entity instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)entity;
				if(!create)
					player.inventory.addItemStackToInventory(new ItemStack(ModBlocks.LianQiDing));
			}
			return;
		}
	}
}
