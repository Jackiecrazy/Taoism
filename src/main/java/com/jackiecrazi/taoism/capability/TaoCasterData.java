package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.networking.PacketUpdateAttackTimer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public TaoCasterData(EntityLivingBase target) {
        inst = new TaoStatCapability(target);
    }

    public static void updateCasterData(EntityLivingBase elb) {
        ITaoStatCapability itsc = elb.getCapability(CAP, null);
        float percentage = itsc.getPosture() / itsc.getMaxPosture();
        itsc.setMaxPosture(getMaxPosture(elb));//a horse has 20 posture right off the bat, just saying
        if (!elb.world.isRemote)
            itsc.setPosture(itsc.getMaxPosture() * percentage);
        //brings it to a tidy sum of 10 for the player, 20 with full armor.
        itsc.setMaxLing(10f);
        if (elb.onGround && itsc.getJumpState() != ITaoStatCapability.JUMPSTATE.GROUNDED) {
            //elb.setSprinting(false);
            itsc.setJumpState(ITaoStatCapability.JUMPSTATE.GROUNDED);
        }
        tickCasterData(elb, (int) (elb.world.getTotalWorldTime() - itsc.getLastUpdatedTime()));
    }

    /**
     * @return the entity's width multiplied by its height, multiplied by 5 and added armor%, and finally rounded
     */
    private static float getMaxPosture(EntityLivingBase elb) {
        float width = (float) Math.ceil(elb.width);
        float height = (float) Math.ceil(elb.height);
        float armor = 1 + (elb.getTotalArmorValue() / 20f);
        return Math.round(width * height * 10 * armor);
    }

    /**
     * ticks the caster for however many ticks dictated by the second argument.
     */
    public static void tickCasterData(EntityLivingBase elb, final int ticks) {
        if (!elb.isEntityAlive()) return;
        ITaoStatCapability itsc = elb.getCapability(CAP, null);
        float lingMult = (float) elb.getEntityAttribute(TaoEntities.LINGREGEN).getAttributeValue();
        int diff = ticks;
        //spirit power recharge
        if (itsc.getLingRechargeCD() >= diff) itsc.setLingRechargeCD(itsc.getLingRechargeCD() - diff);
        else {
            diff -= itsc.getLingRechargeCD();
            itsc.addLing(diff * lingMult);
            itsc.setLingRechargeCD(0);
        }
        if (itsc.getDownTimer() > 0) {
            itsc.setDownTimer(itsc.getDownTimer() - ticks);
            if (itsc.getDownTimer() <= 0) {
                int overflow = -itsc.getDownTimer();
                itsc.setDownTimer(0);
                itsc.addPosture(getPostureRegenAmount(elb, overflow));
            }
        }
        diff = ticks;
        if (itsc.getPostureRechargeCD() <= diff || !itsc.isProtected()) {
            if (itsc.isProtected())
                diff -= itsc.getPostureRechargeCD();
            itsc.setPostureRechargeCD(0);
            itsc.addPosture(getPostureRegenAmount(elb, diff));
        } else itsc.setPostureRechargeCD(itsc.getPostureRechargeCD() - ticks);
        diff = ticks;
        //value updating
        if (ticks > 0) {//so apparently time can randomly run backwards. Hmm.
            itsc.setPosInvulTime(itsc.getPosInvulTime() - diff);
            itsc.addParryCounter(diff);
            itsc.addRollCounter(diff);
        }
        if (itsc.getBindTime() > 0)
            itsc.setBindTime(itsc.getBindTime() - 1);
        if (itsc.getRootTime() > 0)
            itsc.setRootTime(itsc.getRootTime() - 1);
        itsc.setOffhandCool(itsc.getOffhandCool() + ticks);
        diff = ticks - itsc.getQiGracePeriod();
        //qi decay
        if (diff > 0) {
            if (!itsc.consumeQi(getQiDecayAmount(itsc.getQi(), diff), 0))
                itsc.setQi(0);
        } else itsc.setQiGracePeriod(-diff);

        if (!(elb instanceof EntityPlayer))
            itsc.setSwing(itsc.getSwing() + ticks);
        itsc.setLastUpdatedTime(elb.world.getTotalWorldTime());
        forceUpdateTrackingClients(elb);
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getPostureRegenAmount(EntityLivingBase elb, int ticks) {
        float posMult = (float) elb.getEntityAttribute(TaoEntities.POSREGEN).getAttributeValue()*2;
        float armorMod = 1f - ((float) elb.getTotalArmorValue() / 40f);
        float healthMod = (float) ((elb.getHealth()/elb.getMaxHealth()));
        float downedBonus = TaoCasterData.getTaoCap(elb).getDownTimer() > 0 ? TaoCasterData.getTaoCap(elb).getMaxPosture() / 120 : 0;

        return (downedBonus + ticks * 0.05f) * armorMod * posMult * healthMod;
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getQiDecayAmount(float currentQi, int ticks) {
        return 0.005f * ticks * (currentQi + 1) / 8;
    }

    public static void forceUpdateTrackingClients(EntityLivingBase entity) {
//        if (!entity.world.isRemote) {
//            PacketUpdateClientPainful pucp = new PacketUpdateClientPainful(entity);
//            Taoism.net.sendToAllTracking(pucp, entity);
//            if (entity instanceof EntityPlayerMP) {
//                Taoism.net.sendTo(pucp, (EntityPlayerMP) entity);
//            }
//        }
        if (!entity.world.isRemote)
            getTaoCap(entity).sync();
    }

    @Nonnull
    public static ITaoStatCapability getTaoCap(EntityLivingBase entity) {
        ITaoStatCapability cap = entity.getCapability(CAP, null);
        assert cap != null;
        return cap;
    }

    public static void syncAttackTimer(EntityLivingBase entity) {
        if (!entity.world.isRemote) {
            PacketUpdateAttackTimer puat = new PacketUpdateAttackTimer(entity);
            Taoism.net.sendToAllTracking(puat, entity);
            if (entity instanceof EntityPlayerMP) {
                Taoism.net.sendTo(puat, (EntityPlayerMP) entity);
            }
        }
    }

    /**
     * ticks the caster once, to save processing. For players only.
     */
    public static void tickCasterData(EntityLivingBase elb) {
        ITaoStatCapability itsc = elb.getCapability(CAP, null);
        if (itsc.getLingRechargeCD() == 0) itsc.addLing(1f);
        else itsc.setLingRechargeCD(itsc.getLingRechargeCD() - 1);
        if (itsc.getPostureRechargeCD() == 0 || !itsc.isProtected()) itsc.addPosture(getPostureRegenAmount(elb, 1));
        else itsc.setPostureRechargeCD(itsc.getPostureRechargeCD() - 1);
        itsc.addParryCounter(1);
        itsc.setOffhandCool(itsc.getOffhandCool() + 1);
        if (itsc.getDownTimer() > 0) {
            itsc.setDownTimer(itsc.getDownTimer() - 1);
            itsc.setPostureRechargeCD(itsc.getDownTimer());
        }
        itsc.addRollCounter(1);
        itsc.setLastUpdatedTime(elb.world.getTotalWorldTime());
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
