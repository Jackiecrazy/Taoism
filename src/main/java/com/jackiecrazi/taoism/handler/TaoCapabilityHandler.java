package com.jackiecrazi.taoism.handler;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TaoCapabilityHandler {
    public static final ResourceLocation TAOCAP = new ResourceLocation(Taoism.MODID, "stuff");

    @SubscribeEvent
    public static void addCap(AttachCapabilitiesEvent<Entity> event) {
        //System.out.println("attaching cap");
        if(event.getObject() instanceof EntityLivingBase) {
            EntityLivingBase elb= (EntityLivingBase) event.getObject();
            event.addCapability(TAOCAP, new TaoCasterData(elb));
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())return;
        EntityPlayer player = event.getEntityPlayer();
        ITaoStatCapability neww = player.getCapability(TaoCasterData.CAP, null);
        ITaoStatCapability oldd = event.getOriginal().getCapability(TaoCasterData.CAP, null);
        if(neww!=null&&oldd!=null)
        neww.copy(oldd);
    }
}
