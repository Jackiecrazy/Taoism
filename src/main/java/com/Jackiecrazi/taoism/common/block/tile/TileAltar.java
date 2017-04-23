package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileAltar extends TaoisticInvTE {
	//TODO operable either by hand or by machine arm, draw like yys
	public static final int sizeInv = 1;
	private int paperAmount;
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return true;
		}
	@Override
	public int getSizeInventory(){
		return 1;
	}
	@Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack)
    {
        return false;
    }
	@Override
	public void writeToNBT(NBTTagCompound nbts){
    	
    	nbts.setInteger("paperAmount", paperAmount);
    	super.writeToNBT(nbts);
    }
	@Override
	public void readFromNBT(NBTTagCompound j){
		super.readFromNBT(j);
        paperAmount = j.getInteger("paperAmount");
	}
	public static void runThatThing()
	{
		System.out.println("WIP");
	}
	
	public void setPaper(int paper){
		paperAmount=paper;
	}
	public int getPaper(){
		return paperAmount;
	}
	@Override
	public String getInventoryName() {
		return null;
	}
	@Override
	public void openInventory() {
		
	}
	@Override
	public void closeInventory() {
		
	}
	@Override
	public void tick() {
		
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public void eatItem(ItemStack eaten) {
		// TODO Auto-generated method stub
		
	}
}
