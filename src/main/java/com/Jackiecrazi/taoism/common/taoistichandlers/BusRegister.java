package com.Jackiecrazi.taoism.common.taoistichandlers;

import net.minecraftforge.common.MinecraftForge;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.tickhandlers.IApologizeForThisHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class BusRegister {
	public static final TaoisticEventHandler handler= new TaoisticEventHandler();;
	public static final BoringSetupStuff handler2=new BoringSetupStuff();
	public static final IApologizeForThisHandler handler3=new IApologizeForThisHandler();
	public static void setup(){
	    
	    FMLCommonHandler.instance().bus().register(handler);
    	MinecraftForge.EVENT_BUS.register(handler);
    	FMLCommonHandler.instance().bus().register(handler2);
    	MinecraftForge.EVENT_BUS.register(handler2);
    	Taoism.addSpecialEventBus(handler3);
    	//Taoism.proxy.initRenders();
	}
}
