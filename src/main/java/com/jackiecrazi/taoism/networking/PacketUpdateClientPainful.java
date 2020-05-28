package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateClientPainful implements IMessage {
    private int entityID;
    private float qi, ling, posture, swing;
    private int combo, ohcool;
    private float maxLing, maxPosture;
    private int lcd, pcd, scd, qcd, down;
    private long timey;
    private boolean swi, protecc;
    private int parry, dodge, protec;
    private float width, height;

    public PacketUpdateClientPainful() {
    }

    public PacketUpdateClientPainful(EntityLivingBase elb) {
        entityID = elb.getEntityId();
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        qi = itsc.getQi();
        ling = itsc.getLing();
        posture = itsc.getPosture();
        combo = itsc.getComboSequence();
        swing = itsc.getSwing();
        ohcool = itsc.getOffhandCool();
        maxLing = itsc.getMaxLing();
        maxPosture = itsc.getMaxPosture();
        lcd = itsc.getLingRechargeCD();
        pcd = itsc.getPostureRechargeCD();
        scd = itsc.getStaminaRechargeCD();
        qcd= itsc.getQiGracePeriod();
        down = itsc.getDownTimer();
        timey = itsc.getLastUpdatedTime();
        swi = itsc.isSwitchIn();
        protecc = itsc.isProtected();
        parry = itsc.getParryCounter();
        dodge = itsc.getRollCounter();
        protec = itsc.getPosInvulTime();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        qi = buf.readFloat();
        ling = buf.readFloat();
        posture = buf.readFloat();
        combo = buf.readInt();
        swing = buf.readFloat();
        ohcool = buf.readInt();
        maxLing = buf.readFloat();
        maxPosture = buf.readFloat();
        lcd = buf.readInt();
        pcd = buf.readInt();
        scd = buf.readInt();
        qcd= buf.readInt();
        down = buf.readInt();
        timey = buf.readLong();
        swi = buf.readBoolean();
        protecc = buf.readBoolean();
        parry = buf.readInt();
        dodge = buf.readInt();
        protec = buf.readInt();
        width = buf.readFloat();
        height = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeFloat(qi);
        buf.writeFloat(ling);
        buf.writeFloat(posture);
        buf.writeInt(combo);
        buf.writeFloat(swing);
        buf.writeInt(ohcool);
        buf.writeFloat(maxLing);
        buf.writeFloat(maxPosture);
        buf.writeInt(lcd);
        buf.writeInt(pcd);
        buf.writeInt(scd);
        buf.writeInt(qcd);
        buf.writeInt(down);
        buf.writeLong(timey);
        buf.writeBoolean(swi);
        buf.writeBoolean(protecc);
        buf.writeInt(parry);
        buf.writeInt(dodge);
        buf.writeInt(protec);
        buf.writeFloat(width);
        buf.writeFloat(height);
    }

    public static class UpdateClientHandler implements
            IMessageHandler<PacketUpdateClientPainful, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateClientPainful m,
                                  MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer thePlayer = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                Entity e = thePlayer.world
                        .getEntityByID(m.entityID);
                if (e instanceof EntityLivingBase) {
                    ITaoStatCapability i = TaoCasterData.getTaoCap((EntityLivingBase) e);
                    i.setQi(m.qi);
                    i.setLing(m.ling);
                    i.setPosture(m.posture);
                    i.setPosInvulTime(m.protec);
                    i.setOffhandCool(m.ohcool);
                    i.setSwing(m.swing);
                    i.setRollCounter(m.dodge);
                    i.setPostureRechargeCD(m.pcd);
                    i.setQiGracePeriod(m.qcd);
                    i.setDownTimer(m.down);
                    i.setProtected(m.protecc);
                    i.setParryCounter(m.parry);
                    i.setLingRechargeCD(m.lcd);
                    i.setSwitchIn(m.swi);
                    i.setLastUpdatedTime(m.timey);
                    i.setStaminaRechargeCD(m.scd);
                    i.setMaxLing(m.maxLing);
                    i.setMaxPosture(m.maxPosture);
                    i.setComboSequence(m.combo);
                    i.setPrevSizes(m.width, m.height);
                }
            });
            return null;
        }
    }
}
