package com.Jackiecrazi.taoism.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ITaoistEquipment;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerEquipmentStalker;

public class InventoryTPInv implements IInventory {
	//headdress, mirror, belt, faqi1, faqi2, amulet, ring1, ring2, glove, back, cloak
	public ItemStack[] inv;
	private Container cont;
	public EntityPlayer player;
	boolean allowEvents = true;
	public static final int size=13;
	public InventoryTPInv( EntityPlayer p) {
		inv=new ItemStack[size];
		//setCont(c);
		player=p;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return i>=inv.length?null:inv[i];

	}

	@Override
	public ItemStack decrStackSize(int slot, int nmbr) {
		//System.out.println("called");
		/*this.markDirty();
		if(getStackInSlot(slot)!=null&&getStackInSlot(slot).getItem() instanceof ITaoistEquipment){
			ITaoistEquipment item=(ITaoistEquipment)getStackInSlot(slot).getItem();
			ItemStack is=getStackInSlot(slot).copy();
			if(getStackInSlot(slot).stackSize<=0){
				System.out.println("illegal");
				this.setInventorySlotContents(slot, null);
				this.markDirty();
				return null;
			}
			if(getStackInSlot(slot).stackSize<=nmbr){
				System.out.println("out");
				item.onUnequipped(is, player);
				this.setInventorySlotContents(slot, null);
				this.markDirty();
				return is;
			}
			else{
				System.out.println("success");
				inv[slot].stackSize-=nmbr;
				is.stackSize=nmbr;
				this.markDirty();
				return is;
			}
		}*/

		ItemStack ret=new ItemStack(Blocks.dirt,nmbr);
		try{
			inv[slot].stackSize-=nmbr;
			ret=new ItemStack(inv[slot].getItem(),nmbr,inv[slot].getItemDamage());
			if(ret.getItem()instanceof ITaoistEquipment)((ITaoistEquipment)ret.getItem()).onUnequipped(ret, player);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack[] aitemstack = this.inv;

		if (aitemstack[slot] != null)
		{
			ItemStack itemstack = aitemstack[slot];
			aitemstack[slot] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack content) {

		/*try{
			inv[slot]=content;
			if(content!=null&&content.getItem() instanceof ITaoistEquipment){

				((ITaoistEquipment)content.getItem()).onEquipped(content, player);
			}
			System.out.println(inv[slot]);
			if(content!=null)System.out.println(inv[slot].stackSize);
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		if(player instanceof EntityPlayerMP)((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
		//if(player instanceof EntityPlayerMP)((EntityPlayerMP)player).sendContainerToPlayer(PlayerResourceStalker.get(player).itpi);
		 */
		try{
			inv[slot]=content;
			if(content!=null&&content.getItem() instanceof ITaoistEquipment){

				((ITaoistEquipment)content.getItem()).onEquipped(content, player);
			}
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}

	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("taoism.invTao");
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		for(int x=0;x<inv.length;x++){
			if(inv[x]!=null&&inv[x].stackSize<=0)inv[x]=null;
		}
		try
		{
			((EntityPlayer)player).inventory.markDirty();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p) {
		return true;
	}

	@Override
	public void openInventory() {
		System.out.println("adding stuff");
		readFromNBT(this.player.getEntityData());
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	public Container getCont() {
		return cont;
	}

	public void setCont(Container cont) {
		this.cont = cont;
	}
	private String getNbtKey(){
		return "TaoInv";
	}

	public void writeToNBT(NBTTagCompound compound) {
		String key = getNbtKey();
		if (key == null || key.equals("")) {
			return;
		}
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		compound.setTag(key, items);

	}

	public void readFromNBT(NBTTagCompound compound) {
		String key = getNbtKey();
		if (key == null || key.equals("")) {
			return;
		}
		NBTTagList items = compound.getTagList(key, compound.getId());
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inv[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void saveToEquip(){
		for(int a=0;a<EnumEquipmentType.values().length;a++)
			PlayerEquipmentStalker.getEquipmentList(player).setEquipment(EnumEquipmentType.values()[a], inv[a]);
	}

	public void readFromEquip() {
		for(int a=0;a<EnumEquipmentType.values().length;a++)
			PlayerEquipmentStalker.getEquipmentList(player).getEquipment(EnumEquipmentType.values()[a]);
	}
}
