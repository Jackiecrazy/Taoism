package com.Jackiecrazi.taoism.common.block.tile;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class TaoisticInvTE extends TileEntity implements IInventory, ITickable {
	
    protected ItemStack[] inv;
    private WeakReference<EntityPlayer> ownerEntity = new WeakReference<EntityPlayer>(null);
    private UUID ownerID;

    public void setOwner(EntityPlayer owner) {
        this.ownerID = owner.getUniqueID();
        this.ownerEntity=new WeakReference<EntityPlayer>(owner);
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

    @SuppressWarnings("unchecked")
	private EntityPlayer lookupOwner() {
        if (ownerID == null) {
            return null;
        }
        List<EntityPlayerMP> allPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayerMP player : allPlayers) {
            if (player.getUniqueID().equals(ownerID)) {
                ownerEntity=new WeakReference<EntityPlayer>(player);
                return player;
            }
        }
        return null;
    }
    
    public abstract boolean isItemValidForSlot(int slot, ItemStack stack);
    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        NBTTagList invList = new NBTTagList();
        for (int i = 0; i < this.inv.length; i++)
        {
            if (this.inv[i] != null)
            {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot", (byte) i);
                this.inv[i].writeToNBT(stackTag);
                invList.appendTag(stackTag);
            }
        }

        tag.setTag("Inventory", invList);
        if(ownerID!=null)
        tag.setString("owner", ownerID.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        NBTTagList invList = tag.getTagList("Inventory",
                Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < invList.tagCount(); i++)
        {
            NBTTagCompound stackTag = invList.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot");

            if (slot >= 0 && slot < inv.length)
                inv[slot] = ItemStack.loadItemStackFromNBT(stackTag);
        }
        if(tag.hasKey("owner"))
        ownerID=UUID.fromString(tag.getString("owner"));
        ownerEntity=new WeakReference<EntityPlayer>(getOwnerEntity());
    }
    
    
    public ItemStack getStackInSlot(int slot)
    {
        return inv[slot];
    }
	public void update() {
		
	}
	public ItemStack decrStackSize(int slot, int dec) {
		ItemStack ret=new ItemStack(Blocks.dirt,dec);
		try{
			inv[slot].stackSize-=dec;
			ret=new ItemStack(inv[slot].getItem(),dec,inv[slot].getItemDamage());
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}
	public void setInventorySlotContents(int slot, ItemStack content) {
		try{
			inv[slot]=content;
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}
	public boolean hasCustomInventoryName() {
		return true;
	}
	public abstract void eatItem(ItemStack eaten);
	
	@Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }
}