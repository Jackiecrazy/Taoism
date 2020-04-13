package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.networking.PacketUpdateClientPainful;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TaoCasterData implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(ITaoStatCapability.class)
    public static final Capability<ITaoStatCapability> CAP = null;
    private ITaoStatCapability inst = CAP.getDefaultInstance();
    private EntityLivingBase elb;

    public TaoCasterData(){

    }

    @Nonnull
    public static ITaoStatCapability getTaoCap(EntityLivingBase entity) {
        ITaoStatCapability cap = entity.getCapability(CAP, null);
        assert cap != null;
        return cap;
    }

    public static void forceUpdateTrackingClients(EntityLivingBase entity) {
        if (!entity.world.isRemote) {
            Taoism.net.sendToAllTracking(new PacketUpdateClientPainful(entity), entity);
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAP ? CAP.cast(this.inst) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        CAP.getStorage().readNBT(CAP, inst, null, nbt);
    }
}
