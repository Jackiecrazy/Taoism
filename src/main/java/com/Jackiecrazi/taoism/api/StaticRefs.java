package com.Jackiecrazi.taoism.api;

import java.util.HashMap;

import net.minecraft.potion.Potion;

import org.apache.logging.log4j.LogManager;

public class StaticRefs {

	public static HashMap<Integer,Boolean> goodPotEffects=new HashMap<Integer,Boolean>();
	public static HashMap<Integer,Boolean> badPotEffects=new HashMap<Integer,Boolean>();
	public static final String KNOTS="knots",POMMEL="pommel",HANDLE="handle";
	public static final String PRONGS="prongs",SHAFT="shaft";
	public static final String HEAD="head",GUARD="guard";
	public static final String DAOBLADE="daoblade",SWORDBLADE="swordblade",LOOP="loop",EDGE="edge";
	public static final String TIP="tip",CROWN="crown";
	public static final String DEFOFF="blade",FANCY="additions";
	
	public static void populateLists(){
		for(int x=0;x<Potion.potionTypes.length;x++){
			if(Potion.potionTypes[x]!=null){
				Potion pot=Potion.potionTypes[x];
				if(pot.isBadEffect())badPotEffects.put(pot.id, pot.isInstant());
				else goodPotEffects.put(pot.id, pot.isInstant());
				LogManager.getLogger("taoism").info("registered "+pot.getName()+" as a "+(pot.isBadEffect()?"bad":"good")+" effect for alchemical purposes");
			}
		}
		
		
	}

}
