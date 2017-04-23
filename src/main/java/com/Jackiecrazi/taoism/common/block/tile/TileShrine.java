package com.Jackiecrazi.taoism.common.block.tile;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileShrine extends TaoisticInvTE {
	public String worship = "NvWa";
	public int incense;
	public ArrayList<UUID> listoplayers = new ArrayList<UUID>(20);

	// Amount of worshipers should change dynamically with shrine size
	
	@Override
	public void update() {
		if (incense > 0) {
			/*double trellis = Math.random();
			if (trellis >= 0.1) {
				try {
					HashMap<UUID, Integer> god = Deity.investitureofthegods
							.get(worship);
					if (Deity.investitureofthegods.containsKey(worship)) {
						for (Map.Entry<UUID, Integer> play : god.entrySet()) {
							Skills.relationAdd(play.getKey(), worship, 1);
						}
					} else{
						System.out.println("god does not exist");
						Deity.godRegister(worship);
					}
				} catch (IndexOutOfBoundsException e) {
					Deity.godRegister(worship);
					System.out.println("god added");
				}*/
			}
			incense--;
	//	}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbts) {
		
		nbts.setString("worship", worship);
		nbts.setInteger("incense", incense);
		super.writeToNBT(nbts);
	}

	@Override
	public void readFromNBT(NBTTagCompound j) {
		super.readFromNBT(j);
		worship = j.getString("worship");
		incense = j.getInteger("incense");
	}
	/*public void addFollower(EntityPlayer player){
		Deity.addtoworshipers(player, worship);
		}
	public boolean hasFollower(EntityPlayer player){
		return Deity.getDeity(worship).containsKey(player);
	}*/

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void eatItem(ItemStack eaten) {
		// TODO Auto-generated method stub
		
	}
}
