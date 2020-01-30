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
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("qi", instance.getQi());
        nbt.setFloat("ling", instance.getLing());
        nbt.setInteger("swing", instance.getSwing());
        nbt.setFloat("posture", instance.getPosture());
        nbt.setInteger("parry", instance.getParryCounter());
        nbt.setInteger("combo", instance.getComboSequence());
        nbt.setFloat("maxling", instance.getMaxLing());
        nbt.setFloat("maxposture", instance.getMaxPosture());
        nbt.setInteger("lcd", instance.getLingRechargeCD());
        nbt.setInteger("pcd", instance.getPostureRechargeCD());
        nbt.setInteger("scd", instance.getStaminaRechargeCD());
        nbt.setLong("lastupdate", instance.getLastUpdatedTime());
        nbt.setBoolean("switch", instance.isSwitchIn());
        nbt.setInteger("ohcool", instance.getOffhandCool());
        nbt.setBoolean("protecc", instance.isProtected());
        nbt.setInteger("down", instance.getDownTimer());
        nbt.setInteger("dodge",instance.getRollCounter());
        nbt.setFloat("prevWidth",instance.getPrevSizes().getFirst());
        nbt.setFloat("prevHeight",instance.getPrevSizes().getSecond());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ITaoStatCapability> capability, ITaoStatCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound n = (NBTTagCompound) nbt;
        instance.setQi(n.getFloat("qi"));
        instance.setLing(n.getFloat("ling"));
        instance.setSwing(n.getInteger("swing"));
        instance.setPosture(n.getFloat("posture"));
        instance.setParryCounter(n.getInteger("parry"));
        instance.setComboSequence((n.getInteger("combo")));
        instance.setMaxLing(n.getFloat("maxling"));
        instance.setMaxPosture(n.getFloat("maxposture"));
        instance.setLingRechargeCD(n.getInteger("lcd"));
        instance.setPostureRechargeCD(n.getInteger("pcd"));
        instance.setStaminaRechargeCD(n.getInteger("scd"));
        instance.setLastUpdatedTime(n.getLong("lastupdate"));
        instance.setSwitchIn(n.getBoolean("switch"));
        instance.setOffhandCool(n.getInteger("ohcool"));
        instance.setProtected(n.getBoolean("protecc"));
        instance.setDownTimer(n.getInteger("down"));
        instance.setRollCounter(n.getInteger("dodge"));
        instance.setPrevSizes(n.getFloat("prevWidth"),n.getFloat("prevHeight"));
    }
}
