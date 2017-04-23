package com.Jackiecrazi.taoism.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractZhenComponent extends TileEntity {
	protected int yanx,yany,yanz;
	protected int lingAmount;
	protected int lingMax;
	protected boolean isActive;
	/**
	 * 
	 * @param meta gets multiplied by 500
	 */
	public AbstractZhenComponent(int meta) {
		lingMax=meta*500;
	}
	public void setYanLoc(int x, int y, int z){
		yanx=x;
		yany=y;
		yanz=z;
	}
	public int getYanX(){
		return yanx;
	}
	public int getYanY(){
		return yany;
	}
	public int getYanZ(){
		return yanz;
	}
	public int getLing() {
		return lingAmount;
	}
	public void setLing(int lingAmount) {
		this.lingAmount = lingAmount;
		if(this.lingAmount<0)this.lingAmount=0;
	}
	public void addLing(int add){
		lingAmount+=add;
		if(lingAmount>lingMax)lingAmount=lingMax;
	}
	/**
	 * @return false if subtraction leads to a ling level less than 0
	 */
	public boolean removeLing(int add){
		if(lingAmount-add<0)return false;
		lingAmount-=add;
		return lingAmount<0;
	}
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		yanx=c.getInteger("yanx");
		yany=c.getInteger("yany");
		yanz=c.getInteger("yanz");
		lingAmount=c.getInteger("ling");
	}
	public void writeToNBT(NBTTagCompound c) {
		super.writeToNBT(c);
		c.setInteger("yanx", yanx);
		c.setInteger("yany", yanx);
		c.setInteger("yanz", yanx);
		c.setInteger("ling", lingAmount);
	}
	public boolean sendLingToYan(int amnt){
		if(worldObj.getTileEntity(yanx, yany, yanz) instanceof AbstractZhenComponent&&amnt<lingAmount){
			((AbstractZhenComponent)worldObj.getTileEntity(yanx, yany, yanz)).addLing(amnt);
		}
		return removeLing(amnt);
	}
	public boolean sendLingTo(int x, int y, int z, int amnt){
		if(worldObj.getTileEntity(x, y, z) instanceof AbstractZhenComponent&&amnt<lingAmount){
			((AbstractZhenComponent)worldObj.getTileEntity(x, y, z)).addLing(amnt);
		}
		return removeLing(amnt);
	}
	public boolean requestLing(int amnt){
		if(worldObj.getTileEntity(yanx, yany, yanz) instanceof AbstractZhenComponent){
			return ((AbstractZhenComponent)worldObj.getTileEntity(yanx, yany, yanz)).receiveLingRequest(xCoord,yCoord,zCoord,amnt);
		}
		return false;
	}
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
	/**
	 * Tries to give ling over to the target
	 * @param amnt
	 * @return success of transaction
	 */
	protected abstract boolean receiveLingRequest(int x, int y, int z,int amnt);
}
