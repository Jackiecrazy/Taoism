package com.jackiecrazi.taoism.common;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoStatCapabilityDefault;
import com.jackiecrazi.taoism.capability.TaoStorage;
import com.jackiecrazi.taoism.networking.*;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach.ExtendReachHandler;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
    protected ModelBase[] models={
            null
    };

	public void preinit(FMLPreInitializationEvent event)
    {
		int dis=0;
    	Taoism.net.registerMessage(ExtendReachHandler.class, PacketExtendThyReach.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketMakeMove.MakeMoveHandler.class, PacketMakeMove.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketJump.ParryHandler.class, PacketJump.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketDodge.DodgeHandler.class, PacketDodge.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketUpdateClientPainful.UpdateClientHandler.class, PacketUpdateClientPainful.class, dis++, Side.CLIENT);
        Taoism.net.registerMessage(PacketUpdateSize.UpdateSizeHandler.class, PacketUpdateSize.class, dis++, Side.CLIENT);

        CapabilityManager.INSTANCE.register(ITaoStatCapability.class, new TaoStorage(), TaoStatCapabilityDefault::new);
    }

    public void init(FMLInitializationEvent event)
    {
        
    }

    public void postinit(FMLPostInitializationEvent event)
    {
        
    }

	public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}
    public ModelBase getModel(int index){
	    return models[index%models.length];
    }
}
