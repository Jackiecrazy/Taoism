package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.networking.PacketUpdateClientPainful;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
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
        itsc.setMaxPosture(getMaxPosture(elb));//a horse has 20 posture right off the bat, just saying
        //brings it to a tidy sum of 10 for the player, 20 with full armor.
        itsc.setMaxLing(10f);
        tickCasterData(elb, (int) (elb.world.getTotalWorldTime() - itsc.getLastUpdatedTime()));
    }

    /**
     * @return the entity's width (minimum 1) squared multiplied by the ceiling of its height, multiplied by 5 and added armor%, and finally rounded
     */
    private static float getMaxPosture(EntityLivingBase elb) {
        float width = Math.max(elb.width, 1f);
        float height = (float) Math.ceil(elb.height);
        float armor = 1 + (elb.getTotalArmorValue() / 20f);
        float stateModifier = elb.onGround ? elb.isSneaking() ? 1.5f : 1f : 0.5f;
        return Math.round(width * width * height * 5 * armor * stateModifier);
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
        //downed ticking
        if (itsc.getDownTimer() > 0) {
            itsc.setDownTimer(itsc.getDownTimer() - ticks);
            //elb.addVelocity(0,-0.08,0);
            //elb.velocityChanged=true;
            if (itsc.getDownTimer() <= 0) {
                //yes honey
                Tuple<Float, Float> thing = itsc.getPrevSizes();
                if (!elb.world.isRemote) {
                    forceUpdateTrackingClients(elb);
                    //NeedyLittleThings.setSize(elb, thing.getFirst(), thing.getSecond());
                }

                int overflow = -itsc.getDownTimer();
                itsc.setDownTimer(0);
                itsc.addPosture(getPostureRegenAmount(elb, overflow));
            } else itsc.setPostureRechargeCD(itsc.getDownTimer());
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
        itsc.setPosInvulTime(itsc.getPosInvulTime() - diff);
        itsc.addParryCounter(diff);
        itsc.addRollCounter(diff);
        //roll ticking
//        if (itsc.getRollCounter() == CombatConfig.rollCooldown) {
//            Tuple<Float, Float> thing = itsc.getPrevSizes();
//            elb.width = thing.getFirst();
//            elb.height = thing.getSecond();
//        }
        itsc.setOffhandCool(itsc.getOffhandCool() + ticks);
        diff = ticks - itsc.getQiGracePeriod();
        //qi decay
        if (diff > 0) {
            if (!itsc.consumeQi(getQiDecayAmount(itsc.getQi(), diff)))
                itsc.setQi(0);
        } else itsc.setQiGracePeriod(-diff);

        if (!(elb instanceof EntityPlayer))
            itsc.setSwing(itsc.getSwing() + ticks);
        itsc.setLastUpdatedTime(elb.world.getTotalWorldTime());

    }

    public static void forceUpdateTrackingClients(EntityLivingBase entity) {
        if (!entity.world.isRemote) {
            PacketUpdateClientPainful pucp = new PacketUpdateClientPainful(entity);
            Taoism.net.sendToAllTracking(pucp, entity);
            if (entity instanceof EntityPlayerMP) {
                Taoism.net.sendTo(pucp, (EntityPlayerMP) entity);
            }
        }
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getPostureRegenAmount(EntityLivingBase elb, int ticks) {
        float posMult = (float) elb.getEntityAttribute(TaoEntities.POSREGEN).getAttributeValue();
        float armorMod = 1f - ((float) elb.getTotalArmorValue() / 30f);
        return ticks * armorMod * 0.05f * posMult * elb.getHealth() / elb.getMaxHealth();
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getQiDecayAmount(float currentQi, int ticks) {
        return 0.005f * ticks * currentQi/8;
    }

    @Nonnull
    public static ITaoStatCapability getTaoCap(EntityLivingBase entity) {
        ITaoStatCapability cap = entity.getCapability(CAP, null);
        assert cap != null;
        return cap;
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
            if (itsc.getDownTimer() == 0) {
                Tuple<Float, Float> thing = itsc.getPrevSizes();
                elb.width = thing.getFirst();
                elb.height = thing.getSecond();
            }
        }
        itsc.addRollCounter(1);
        if (itsc.getRollCounter() == CombatConfig.rollCooldown) {
            Tuple<Float, Float> thing = itsc.getPrevSizes();
            elb.width = thing.getFirst();
            elb.height = thing.getSecond();
        }
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
