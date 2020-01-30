package com.jackiecrazi.taoism.common;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoStorage;
import com.jackiecrazi.taoism.networking.PacketBeginParry;
import com.jackiecrazi.taoism.networking.PacketDodge;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach.ExtendReachHandler;
import com.jackiecrazi.taoism.networking.PacketMakeMove;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayerMP;
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
        Taoism.net.registerMessage(PacketBeginParry.ParryHandler.class, PacketBeginParry.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketDodge.DodgeHandler.class, PacketDodge.class, dis++, Side.SERVER);

        CapabilityManager.INSTANCE.register(ITaoStatCapability.class, new TaoStorage(), TaoStatCapability::new);
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
    public ModelBase getModel(int index){
	    return models[index%models.length];
    }
}
