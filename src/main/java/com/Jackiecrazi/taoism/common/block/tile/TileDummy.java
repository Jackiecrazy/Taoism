package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileDummy extends TaoisticInvTE {
	private static final String XL = "masterx",YL="mastery",ZL="masterz";
	private int x,y,z;
	private boolean isSlaveYet;
	public TileDummy() {
		this.inv=new ItemStack[0];
	}

	@Override
	public int getSizeInventory() {
		//there is no inventory
		return 0;
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p) {
		return true;//isSlaveYet?((TaoisticInvTE)worldObj.getTileEntity(x, y, z)).isUseableByPlayer(p):false;
	}

	@Override
	public void openInventory() {
		// TODO Open master inv instead

	}

	@Override
	public void closeInventory() {
		// TODO close master inv

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
		tag.setBoolean("slave", isSlaveYet);
		tag.setInteger(XL, x);
		tag.setInteger(YL, y);
		tag.setInteger(ZL, z);
    }
	 @Override
	    public void readFromNBT(NBTTagCompound tag)
	    {
		 super.readFromNBT(tag);
		 setIsSlave(tag.getBoolean("slave"));
		 setX(tag.getInteger(XL));
		 setY(tag.getInteger(YL));
		 setZ(tag.getInteger(ZL));
	    }

	public int getX() {
		return x;
	}

	public TileDummy setX(int x) {
		this.x = x;
		return this;
	}

	public int getY() {
		return y;
	}

	public TileDummy setY(int y) {
		this.y = y;
		return this;
	}

	public int getZ() {
		return z;
	}

	public TileDummy setZ(int z) {
		this.z = z;
		return this;
	}
	
	public TileDummy setSlave(boolean isSlaveYet) {
		this.isSlaveYet = isSlaveYet;
		return this;
	}
	
	public TaoisticInvTE redirectToSource(){
		return (TaoisticInvTE)this.worldObj.getTileEntity(x, y, z);
	}
	@Override
    public void updateEntity() {
        super.updateEntity();
        if(!isSlaveYet())System.out.println("tantrum!");
        if (!worldObj.isRemote&&redirectToSource()==null&&isSlaveYet()) {
        	//System.out.println("x: "+x+" y: "+y+" z: " +z);
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            worldObj.removeTileEntity(xCoord, yCoord, zCoord);
        }
    }

	public boolean isSlaveYet() {
		return isSlaveYet;
	}

	public void setIsSlave(boolean isSlaveYet) {
		this.isSlaveYet = isSlaveYet;
	}

	@Override
	public void eatItem(ItemStack eaten) {
		
	}
	
	public void revolt(){
		worldObj.getBlock(x, y, z).breakBlock(worldObj, x, y, z, worldObj.getBlock(x, y, z), 0);
		worldObj.setBlockToAir(x, y, z);
		worldObj.removeTileEntity(x, y, z);
	}
}
