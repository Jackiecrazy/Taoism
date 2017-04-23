package com.Jackiecrazi.taoism;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import com.Jackiecrazi.taoism.api.PerlinNoise;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.client.gui.TaoisticGuiHandler;
import com.Jackiecrazi.taoism.common.block.ModBlocks;
import com.Jackiecrazi.taoism.common.block.tile.ModTEs;
import com.Jackiecrazi.taoism.common.crafting.ModCrafting;
import com.Jackiecrazi.taoism.common.entity.ModEntities;
import com.Jackiecrazi.taoism.common.items.ItemSkillScroll;
import com.Jackiecrazi.taoism.common.items.ModItems;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
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

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(Taoism.inst, new TaoisticGuiHandler());
    	ModItems.init();
    	ModBlocks.init();
    	ModTEs.init();
    	ModEntities.init();
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
    	ModItems.postInit();
    }

    public void init(FMLInitializationEvent e) {
    	ModCrafting.initCrafting();
    	
    }

    public void postInit(FMLPostInitializationEvent e) {
    	GameRegistry.registerWorldGenerator(new WorldGeneratorLingMai(), 5);
    	StaticRefs.populateLists();
    	initRenders();
    	gottaLootThemAll();
    }
    
    public void initRenders(){
    	
    }
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
    {
        return ctx.getServerHandler().playerEntity;
    }
    private void gottaLootThemAll(){
    	ItemStack phatloot=new ItemStack(ModItems.Scroll);
    	for(int x=0;x<ItemSkillScroll.types.size();x++){
    		phatloot.setTagCompound(new NBTTagCompound());
    		phatloot.getTagCompound().setString("name", ItemSkillScroll.types.get(x));
    		boolean rare=phatloot.getRarity()==EnumRarity.common;
    		int rarity=ItemSkillScroll.rarity(phatloot);
    		addLoot(new WeightedRandomChestContent(phatloot, 1, 1, (int)(rarity==0?50:rarity==1?30:rarity==2?7:1)),rare);//item, min, max, weight
    	}
    	for(int x=0;x<GenericTaoistWeapon.ListOfLoot.size();x++){
    		phatloot=new ItemStack(GenericTaoistWeapon.ListOfLoot.get(x));
    		addLoot(new WeightedRandomChestContent(phatloot, 1, 1, 50),true);//item, min, max, weight
    	}
    }
    private void addLoot(WeightedRandomChestContent loot, boolean bonus) {
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(loot);
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(loot);
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(loot);
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(loot);
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(loot);
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(loot);
		if(bonus)ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(loot);;
	}
}
