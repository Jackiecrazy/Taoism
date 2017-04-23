package com.Jackiecrazi.taoism.api.allTheInterfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface IUltimate {
	public abstract void onUltimate(EntityPlayer p);
	public abstract void onUltimateTick(EntityPlayer p);
	public abstract void onUltimateEnd(EntityPlayer p);
	public abstract int getUltimateTime();
	public abstract float getUltimateCost();
	public abstract int getCDTime();
}
