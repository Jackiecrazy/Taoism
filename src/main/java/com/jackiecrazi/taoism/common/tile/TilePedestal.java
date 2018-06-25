package com.jackiecrazi.taoism.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;


public abstract class TilePedestal extends TaoisticInvTE {

	public TilePedestal() {
		super(1);
	}
	

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		//refresh();
	}
	
	/**
	 * for organization's sake
	 * @param worldIn
	 * @param pos
	 * @param state
	 * @param playerIn
	 * @param hand
	 * @param facing
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @return
	 */
	public abstract boolean onRightClick(EntityPlayer p, EnumHand hand, EnumFacing facing, float x, float y, float z);
	public abstract void refresh();
}
