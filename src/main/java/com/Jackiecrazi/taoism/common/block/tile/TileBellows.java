package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileBellows extends TaoisticInvTE {
	private int windpow;
	public TileBellows() {
		this.inv=new ItemStack[0];
	}

	@Override
	public int getSizeInventory() {
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
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		
	}

	@Override
	public void tick() {
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public void eatItem(ItemStack eaten) {
		
	}

	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		windpow=tag.getInteger("wind");
    }
	
	 @Override
	    public void writeToNBT(NBTTagCompound tag)
	    {
		 super.writeToNBT(tag);
		 tag.setInteger("wind", windpow);
	    }
	 
	 public void setWind(int a){
		 windpow=a;
	 }
	 
	 public int getWind(){
		 return windpow;
	 }
	 
	 @Override
		public void updateEntity() {
		 super.updateEntity();
		 if(windpow>0)windpow--;
		 //System.out.println(windpow+" "+worldObj.isRemote);
	 }
	 
}
