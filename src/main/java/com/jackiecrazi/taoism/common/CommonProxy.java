package com.jackiecrazi.taoism.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach.ExtendReachHandler;

public class CommonProxy {

	public void preinit(FMLPreInitializationEvent event)
    {
		int dis=0;
    	Taoism.net.registerMessage(ExtendReachHandler.class, PacketExtendThyReach.class, dis++, Side.SERVER);
    	
    }

    public void init(FMLInitializationEvent event)
    {
        
    }

    public void postinit(FMLPostInitializationEvent event)
    {
        
    }

	public EntityPlayerMP getPlayerEntityFromContext(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}

}
