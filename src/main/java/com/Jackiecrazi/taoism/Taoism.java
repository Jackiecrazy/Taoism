package com.Jackiecrazi.taoism;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.apache.logging.log4j.LogManager;

import com.Jackiecrazi.taoism.common.items.ModItems;
import com.Jackiecrazi.taoism.common.taoistichandlers.BusRegister;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;


@Mod(modid = Taoism.MODID, version = Taoism.MODVER, name = "taoism")
public class Taoism
{
	public static String BLACK = "\u00A70"; public static String DARK_BLUE = "\u00A71"; public static String DARK_GREEN = "\u00A72"; public static String DARK_AQUA = "\u00A73"; public static String DARK_RED = "\u00A74";
	  public static String DARK_PURPLE = "\u00A75"; public static String GOLD = "\u00A76"; public static String GRAY = "\u00A77"; public static String DARK_GRAY = "\u00A78"; public static String BLUE = "\u00A79";
	  public static String GREEN = "\u00A7a"; public static String AQUA = "\u00A7b"; public static String RED = "\u00A7c"; public static String LIGHT_PURPLE = "\u00A7d"; public static String YELLOW = "\u00A7e";
	  public static String WHITE = "\u00A7f";
	  public static org.apache.logging.log4j.Logger logger=LogManager.getLogger("taoism");
    public static final String MODID = "taoism";
    public static final String MODVER = "0.0";
    //private static Item fword = ModItems.SwordCherry;
    public static SimpleNetworkWrapper net;
    @SidedProxy(clientSide="com.Jackiecrazi.taoism.ClientProxy", serverSide="com.Jackiecrazi.taoism.ServerProxy")
    public static CommonProxy proxy;
    @Mod.Instance("taoism")
    public static Taoism inst;
    public static CreativeTabs TabTaoistWeapon = new CreativeTabs("TaoistWeapon"){
    @Override
    public Item getTabIconItem() {
    	return ModItems.SwordIron;
    	}
    };
    public static CreativeTabs TabTaoistMaterials = new CreativeTabs("TaoistMats"){
        @Override
        public Item getTabIconItem() {
        	return (ModItems.ResourceMetal);
        	}
        };
        public static CreativeTabs TabTaoistAccessories = new CreativeTabs("TaoistAccs"){
            @Override
            public Item getTabIconItem() {
            	return ModItems.GongFa;
            	}
            };
    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
    	WayofConfig.init(event.getSuggestedConfigurationFile());
    	net=NetworkRegistry.INSTANCE.newSimpleChannel("TaoistChannel");
    	
    	proxy.preInit(event);
    	Skill.populateSkillHashMap();
    	proxy.postpreInit(event);
    	//Stuff I need
    	BusRegister.setup();
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init(event);
    }
    @EventHandler
    public void init(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);
    }
	public static void addSpecialEventBus(Object o) {
	    FMLCommonHandler.instance().bus().register(o);
	  }
	//I'm too lazy to type. Yay!
	public static void logDebug(Object msg){
		LogManager.getLogger("taoism").debug(msg);
	}
}
