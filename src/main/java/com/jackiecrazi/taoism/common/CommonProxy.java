package com.jackiecrazi.taoism.common;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoStatCapabilityDefault;
import com.jackiecrazi.taoism.capability.TaoStorage;
import com.jackiecrazi.taoism.networking.*;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach.ExtendReachHandler;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;

public class CommonProxy {
    protected ModelBase[] models={
            null
    };

	public void preinit(FMLPreInitializationEvent event)
    {
		int dis=0;
    	Taoism.net.registerMessage(ExtendReachHandler.class, PacketExtendThyReach.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketMakeMove.MakeMoveHandler.class, PacketMakeMove.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketJump.JumpHandler.class, PacketJump.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketDodge.DodgeHandler.class, PacketDodge.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketUpdateClientPainful.UpdateClientHandler.class, PacketUpdateClientPainful.class, dis++, Side.CLIENT);
        Taoism.net.registerMessage(PacketUpdateSize.UpdateSizeHandler.class, PacketUpdateSize.class, dis++, Side.CLIENT);
        Taoism.net.registerMessage(PacketSlide.SlideHandler.class, PacketSlide.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketUpdateAttackTimer.UpdateAttackTimerHandler.class, PacketUpdateAttackTimer.class, dis++, Side.CLIENT);
        Taoism.net.registerMessage(PacketChargeWeapon.ChargeHandler.class, PacketChargeWeapon.class, dis++, Side.SERVER);
        Taoism.net.registerMessage(PacketUpdateProjectile.UpdateProjectileHandler.class, PacketUpdateProjectile.class, dis++, Side.CLIENT);

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

    Field hasBlock= ObfuscationReflectionHelper.findField(PlayerInteractionManager.class,"field_73088_d");
	Field destroyPos=ObfuscationReflectionHelper.findField(PlayerInteractionManager.class, "field_180240_f");

    public BlockPos getPlayerBreakingBlockCoords(EntityPlayer entityplayer)
    {
        if(entityplayer instanceof EntityPlayerMP)
        {
            try {
                PlayerInteractionManager manager = ((EntityPlayerMP) (entityplayer)).interactionManager;
                boolean hb = hasBlock.getBoolean(manager);
                if (hb) {
                    return (BlockPos) destroyPos.get(manager);
                }
            }catch(Exception ignored){}
        }
        return null;
    }

    public boolean isBreakingBlock(EntityPlayer entityplayer)
    {
        if(entityplayer instanceof EntityPlayerMP)
        {
            try {
                PlayerInteractionManager manager = ((EntityPlayerMP) (entityplayer)).interactionManager;
                return hasBlock.getBoolean(manager);
            }catch(Exception ignored){}
        }
        return false;
    }
}
