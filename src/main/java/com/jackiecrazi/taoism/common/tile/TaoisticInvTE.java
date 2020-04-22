package com.jackiecrazi.taoism.common.tile;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import com.jackiecrazi.taoism.api.PlayerHelper;

public abstract class TaoisticInvTE extends TileEntity implements IInventory, ITickable {

	protected ItemStack[] inv;
	private Container potentialContainer;
	private WeakReference<EntityPlayer> ownerEntity = new WeakReference<EntityPlayer>(null);
	private UUID ownerID;

	public TaoisticInvTE(int size) {
		this.inv = new ItemStack[size];
	}

	private boolean shouldUpdate = false;

	public void setShouldUpdate(boolean a) {
		shouldUpdate = a;
		sync();
	}

	/**
	 * This should only ever be used on the client side. It checks whether to
	 * update the GUI if it is open
	 * 
	 * @return
	 */
	public boolean shouldUpdate() {
		return shouldUpdate;
	}

	/**
	 * Tries to set the content
	 * 
	 * @param it
	 * @return whether it can be set, AKA whether it was set
	 */
	public boolean setContent(ItemStack it) {
		for (int x = 0; x < inv.length; x++) {
			if (inv[x] == null) {
				inv[x] = it;
				world.scheduleBlockUpdate(this.getPos(), world.getBlockState(getPos()).getBlock(), 0, 1);
				this.refreshContainer();
				return true;
			}
		}
		return false;
	}

	public void setOwner(EntityPlayer owner) {
		this.ownerID = owner.getUniqueID();
		this.ownerEntity = new WeakReference<EntityPlayer>(owner);
	}

	public EntityPlayer getOwnerEntity() {
		EntityPlayer owner = ownerEntity.get();
		if (owner == null || owner.isDead) {
			owner = lookupOwner();
		}
		return owner;
	}

	public UUID getOwnerID() {
		return ownerID;
	}

	private EntityPlayer lookupOwner() {
		if (ownerID == null) { return null; }
		return PlayerHelper.getPlayerFromUUID(ownerID);
	}

	public abstract boolean isItemValidForSlot(int slot, ItemStack stack);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < this.inv.length; i++) {
			if (this.inv[i] != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				this.inv[i].writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}

		tag.setTag("Inventory", invList);
		if (ownerID != null) tag.setString("owner", ownerID.toString());
		tag.setBoolean("su", shouldUpdate);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList invList = tag.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");

			if (slot >= 0 && slot < inv.length) inv[slot] = new ItemStack(stackTag);
			//System.out.println("read inventory");
		}
		if (tag.hasKey("owner")) ownerID = UUID.fromString(tag.getString("owner"));
		ownerEntity = new WeakReference<EntityPlayer>(getOwnerEntity());
		shouldUpdate = tag.getBoolean("su");
	}

	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	public ItemStack decrStackSize(int slot, int dec) {
		if (inv[slot] != null) {
			ItemStack var3 = inv[slot].splitStack(dec);

			if (inv[slot].getCount() <= 0) {
				inv[slot] = null;
			}

			markDirty();
			return var3;
		}

		return null;
	}

	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	public void setInventorySlotContents(int slot, ItemStack content, boolean refreshcont) {
		if (slot < inv.length) {
			inv[slot] = content;
			this.sync();
			if (refreshcont) this.refreshContainer();
		}

	}

	public void setInventorySlotContents(int slot, ItemStack content) {
		setInventorySlotContents(slot, content, true);
	}

	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		//System.out.println("hi");
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new SPacketUpdateTileEntity(getPos(), 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		//System.out.println("reading packet");

		readFromNBT(packet.getNbtCompound());
	}

	public void refreshContainer() {
		if (potentialContainer != null) potentialContainer.onCraftMatrixChanged(this);
	}

	public final void sync() {
		//world.notifyBlo.markBlockForUpdate(getPos());
		//worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord,worldObj.getBlock(xCoord, yCoord, zCoord),0);
		this.markDirty();
	}

	public Container getContainer() {
		return potentialContainer;
	}

	public void setContainer(Container potentialContainer) {
		this.potentialContainer = potentialContainer;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public boolean isEmpty() {
		for (int a = 0; a < getSizeInventory(); a++)
			if (getStackInSlot(a) != null) return false;
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack ret=getStackInSlot(index).copy();
		inv[index]=null;
		return ret;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;//getOwnerEntity()==player;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
	}
	
	@Override
	public void clear() {
		for(int a=0;a<inv.length;a++){
			inv[a]=null;
		}
	}

	@Override
	public String getName() {
		return "derp";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void tick() {
		
	}

}