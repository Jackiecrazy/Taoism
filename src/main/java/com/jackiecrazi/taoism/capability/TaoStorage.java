package com.jackiecrazi.taoism.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TaoStorage implements Capability.IStorage<ITaoStatCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ITaoStatCapability> capability, ITaoStatCapability instance, EnumFacing side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<ITaoStatCapability> capability, ITaoStatCapability instance, EnumFacing side, NBTBase nbt) {
        instance.deserializeNBT(nbt instanceof NBTTagCompound ? (NBTTagCompound) nbt : new NBTTagCompound());
    }
}
