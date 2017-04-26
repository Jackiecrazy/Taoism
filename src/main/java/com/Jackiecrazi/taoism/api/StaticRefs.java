package com.Jackiecrazi.taoism.api;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.potion.Potion;

import org.apache.logging.log4j.LogManager;

public class StaticRefs {

	public static HashMap<Potion,Boolean> goodPotEffects=new HashMap<Potion,Boolean>();
	public static HashMap<Potion,Boolean> badPotEffects=new HashMap<Potion,Boolean>();
	public static final String KNOTS="knots",POMMEL="pommel",HANDLE="handle";
	public static final String PRONGS="prongs",SHAFT="shaft";
	public static final String HEAD="head",GUARD="guard";
	public static final String DAOBLADE="daoblade",SWORDBLADE="swordblade",LOOP="loop",EDGE="edge";
	public static final String TIP="tip",CROWN="crown";
	public static final String DEFOFF="blade",FANCY="additions";
	
	public static void populateLists(){
		Iterator<Potion> i=Potion.REGISTRY.iterator();
		while(i.hasNext()){
			Potion pot=i.next();
			if(pot!=null){
				if(pot.isBadEffect())badPotEffects.put(pot, pot.isInstant());
				else goodPotEffects.put(pot, pot.isInstant());
				LogManager.getLogger("taoism").info("registered "+pot.getName()+" as a "+(pot.isBadEffect()?"bad":"good")+" effect for alchemical purposes");
			}
		}
		
		
	}

}
