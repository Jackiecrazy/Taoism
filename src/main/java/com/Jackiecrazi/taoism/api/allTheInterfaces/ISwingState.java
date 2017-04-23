package com.Jackiecrazi.taoism.api.allTheInterfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface ISwingState {
public abstract void saveState(NBTTagCompound tag);
public abstract void readState(NBTTagCompound tag);
}
