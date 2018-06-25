package com.jackiecrazi.taoism.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.potion.Potion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class StaticRefs {

	public static HashMap<String,Boolean> goodPotEffects=new HashMap<String,Boolean>();
	public static HashMap<String,Boolean> badPotEffects=new HashMap<String,Boolean>();
	public static ArrayList<Fluid> fluidList=new ArrayList<Fluid>();
	public static ArrayList<Fluid> gasList=new ArrayList<Fluid>();
	public static Fluid HOTTESTFLUID=FluidRegistry.LAVA,COLDESTFLUID=FluidRegistry.WATER;
	public static ArrayList<String> oreList=new ArrayList<String>();
//	public static final String KNOTS="knots",POMMEL="pommel",HANDLE="handle";
//	public static final String PRONGS="prongs",SHAFT="shaft";
//	public static final String HEAD="head",GUARD="guard";
//	public static final String DAOBLADE="daoblade",SWORDBLADE="swordblade",LOOP="loop",EDGE="edge";
//	public static final String TIP="tip",CROWN="crown";
//	public static final String DEFOFF="blade",FANCY="additions";
	public static final String HEAD="head",HANDLE="handle",GUARD="guard",POMMEL="pommel";
	public static final String[] weaponparts={ HEAD,HANDLE,GUARD,POMMEL };
	public static final String STAVE="stave",CABLE="cable",STRING="string",SIYAH="siyah";
	public static final String[] bowparts={ STAVE,STRING,SIYAH };

	public static void populateLists(){
		Iterator<Potion> i=Potion.REGISTRY.iterator();
		while(i.hasNext()){
			Potion p=i.next();
			if(p!=null){
				if(p.isBadEffect())badPotEffects.put(p.getName(), p.isInstant());
				else goodPotEffects.put(p.getName(), p.isInstant());
				//LogManager.getLogger("taoism").info("registered "+pot.getName()+" as a "+(pot.isBadEffect()?"bad":"good")+" effect for alchemical purposes");
			}
		}

		for(String name:OreDictionary.getOreNames())
			if(name.startsWith("ingot"))oreList.add(name);
		for(Fluid f:FluidRegistry.getRegisteredFluids().values()) 
			if(!f.isGaseous()){
				fluidList.add(f);
				if(f.getTemperature()>HOTTESTFLUID.getTemperature())HOTTESTFLUID=f;
				else if(f.getTemperature()<COLDESTFLUID.getTemperature())COLDESTFLUID=f;
			}
			else gasList.add(f);
//		Taoism.logDebug(oreList.toString());
//		Taoism.logDebug(fluidList.toString());
	}
	public static int tempRange(){
		return HOTTESTFLUID.getTemperature()-COLDESTFLUID.getTemperature();
	}
}
