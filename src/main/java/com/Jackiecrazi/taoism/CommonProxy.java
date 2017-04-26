package com.Jackiecrazi.taoism;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.Jackiecrazi.taoism.api.PerlinNoise;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.client.gui.TaoisticGuiHandler;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TaoTEs;
import com.Jackiecrazi.taoism.common.crafting.TaoCrafting;
import com.Jackiecrazi.taoism.common.entity.TaoEntities;
import com.Jackiecrazi.taoism.common.items.TaoItems;
import com.Jackiecrazi.taoism.common.world.gen.WorldGeneratorLingMai;
import com.Jackiecrazi.taoism.networking.PacketAnimUpdate;
import com.Jackiecrazi.taoism.networking.PacketAnimUpdate.UpdateAnimationHandler;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach.ExtendReachHandler;
import com.Jackiecrazi.taoism.networking.PacketOpenTPInv;
import com.Jackiecrazi.taoism.networking.PacketOpenTPInv.InvPacketHandler;
import com.Jackiecrazi.taoism.networking.PacketRearrangeInventorySlots;
import com.Jackiecrazi.taoism.networking.PacketRearrangeInventorySlots.InvChangeHandler;
import com.Jackiecrazi.taoism.networking.PacketSetInvContent;
import com.Jackiecrazi.taoism.networking.PacketSetInvContent.InvContentHandler;
import com.Jackiecrazi.taoism.networking.PacketSetPlayerMeditating;
import com.Jackiecrazi.taoism.networking.PacketSetPlayerMeditating.SetMeditateHandler;
import com.Jackiecrazi.taoism.networking.PacketSetSkillStuff;
import com.Jackiecrazi.taoism.networking.PacketSetSkillStuff.SetSkillXPHandler;
import com.Jackiecrazi.taoism.networking.PacketSetUnlockSkill;
import com.Jackiecrazi.taoism.networking.PacketSetUnlockSkill.SetSkillHandler;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer.UpdateAttackTimeHandler;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(Taoism.inst, new TaoisticGuiHandler());
    	TaoItems.init();
    	TaoBlocks.init();
    	TaoTEs.init();
    	TaoEntities.init();
    	TaoisticPotions.brew();
    	PerlinNoise.init();
    	int dis=0;
    	Taoism.net.registerMessage(SetSkillXPHandler.class, PacketSetSkillStuff.class, dis++, Side.CLIENT);
    	Taoism.net.registerMessage(ExtendReachHandler.class, PacketExtendThyReach.class, dis++, Side.SERVER);
    	Taoism.net.registerMessage(UpdateAttackTimeHandler.class, PacketUpdateAttackTimer.class, dis++, Side.CLIENT);
    	Taoism.net.registerMessage(UpdateAnimationHandler.class, PacketAnimUpdate.class, dis++, Side.CLIENT);
    	Taoism.net.registerMessage(SetMeditateHandler.class, PacketSetPlayerMeditating.class, dis++, Side.SERVER);
    	Taoism.net.registerMessage(SetSkillHandler.class, PacketSetUnlockSkill.class, dis++, Side.CLIENT);
    	Taoism.net.registerMessage(InvPacketHandler.class, PacketOpenTPInv.class, dis++, Side.SERVER);
    	Taoism.net.registerMessage(InvContentHandler.class, PacketSetInvContent.class, dis++, Side.CLIENT);
    	Taoism.net.registerMessage(InvChangeHandler.class, PacketRearrangeInventorySlots.class, dis++, Side.SERVER);
    	
    }
    public void postpreInit(FMLPreInitializationEvent e){
    	TaoItems.postInit();
    }

    public void init(FMLInitializationEvent e) {
    	TaoCrafting.initCrafting();
    	
    }

    public void postInit(FMLPostInitializationEvent e) {
    	GameRegistry.registerWorldGenerator(new WorldGeneratorLingMai(), 5);
    	StaticRefs.populateLists();
    	initRenders();
    }
    
    public void initRenders(){
    	
    }
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
    {
        return ctx.getServerHandler().playerEntity;
    }
}
