package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import com.Jackiecrazi.taoism.api.allTheInterfaces.ILianQiMaterial;
import com.Jackiecrazi.taoism.common.entity.EntityLevitatingItem;
import com.Jackiecrazi.taoism.common.items.TaoItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileDing extends TaoisticInvTE {

	public TileDing() {
		this.inv=new ItemStack[7];
	}

	@Override
	public int getSizeInventory() {
		return 7;
	}

	@Override
	public String getInventoryName() {
		return "Ding";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p) {
		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() instanceof ILianQiMaterial;
	}
	private boolean isStillIntact(){
		//int i = 0;
		// Scan a 3x3x4 area, starting with the bottom left corner
		/*for (int x = xCoord - 1; x < xCoord + 2; x++)
			for (int y = yCoord; y < yCoord + 5; y++)
				for (int z = zCoord - 1; z < zCoord + 2; z++) {
					TileEntity tile = worldObj.getTileEntity(x, y, z);
					//System.out.println(tile != null);
					if (tile != null && (tile instanceof TileDummy)) {
						//System.out.println("is dummy");
						((TileDummy)tile).setX(xCoord).setY(yCoord).setZ(zCoord);
						((TileDummy)tile).setIsSlave(true);
						i++;
					}
				}
		return i>34;*/
		return true;
	}
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!isStillIntact()){
			//ArrayList<ItemStack> drop=worldObj.getBlock(xCoord, yCoord, zCoord).getDrops(worldObj, xCoord, yCoord, zCoord, blockMetadata, 0);
			//for(ItemStack stack:drop)
			if(!worldObj.isRemote)worldObj.spawnEntityInWorld(new EntityItem(worldObj,xCoord,yCoord,zCoord,new ItemStack(TaoItems.Ding)));
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			worldObj.removeTileEntity(xCoord, yCoord, zCoord);
		}
		for(int x=0;x<inv.length;x++){
			//teleport to nearest usable stuff?
			if(inv[x]!=null&&!worldObj.isRemote&&worldObj.rand.nextGaussian()>0.998){
				//System.out.println(worldObj.isRemote);
				/*System.out.println(xCoord);
				System.out.println(yCoord);
				System.out.println(zCoord);*/
				worldObj.spawnEntityInWorld(new EntityLevitatingItem(worldObj, xCoord, yCoord, zCoord, inv[x].copy(), xCoord, yCoord, zCoord));
				inv[x]=null;
			}
		}

	}
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord-5, yCoord, zCoord-5, xCoord+5, yCoord+5, zCoord+5);
	}

	@Override
	public void eatItem(ItemStack eaten) {
		// TODO Auto-generated method stub
		
	}
}
