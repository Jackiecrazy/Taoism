package com.jackiecrazi.taoism.api;

import net.minecraft.potion.Potion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StaticRefs {

    public static HashMap<String, Boolean> goodPotEffects = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> badPotEffects = new HashMap<String, Boolean>();
    public static ArrayList<Fluid> fluidList = new ArrayList<Fluid>();
    public static ArrayList<Fluid> gasList = new ArrayList<Fluid>();
    public static Fluid HOTTESTFLUID = FluidRegistry.LAVA, COLDESTFLUID = FluidRegistry.WATER;
    public static ArrayList<String> oreList = new ArrayList<String>();
    //	public static final String KNOTS="knots",POMMEL="pommel",HANDLE="handle";
//	public static final String PRONGS="prongs",SHAFT="shaft";
//	public static final String HEAD="head",GUARD="guard";
//	public static final String DAOBLADE="daoblade",SWORDBLADE="swordblade",LOOP="loop",EDGE="edge";
//	public static final String TIP="tip",CROWN="crown";
//	public static final String DEFOFF="blade",FANCY="additions";
//    public static final String HEAD = "head", HANDLE = "handle", FITTING = "fitting";
//    public static final String[] weaponParts = {HEAD, HANDLE, FITTING};

    //just some commonly used part definitions
    public static final MaterialType[] HARD={
            MaterialType.WOOD,
            MaterialType.BAMBOO,
            MaterialType.METAL,
            MaterialType.STONE,
            MaterialType.GEM,
            MaterialType.BONE,
            MaterialType.TOOTH,
            MaterialType.HORN,
            MaterialType.ICE
    }, FABRIC={
            MaterialType.WOOL,
            MaterialType.SKIN,
            MaterialType.FIBER,
            MaterialType.SILK,
            MaterialType.SINEW,
            MaterialType.INTESTINE
    }, STRING={
            MaterialType.FIBER,
            MaterialType.SILK,
            MaterialType.SINEW,
            MaterialType.INTESTINE
    }, FLETCH={
            MaterialType.FEATHER
    };
    public static final PartDefinition
    HEAD=new PartDefinition("head", HARD),
    HANDLE=new PartDefinition("handle", HARD),
    FITTING=new PartDefinition("fitting", HARD),
    HANDLEWRAP=new PartDefinition("handlewrap", false,FABRIC),
    SCABBARD=new PartDefinition("sheath",HARD),
    SCABBARDWRAP=new PartDefinition("sheathwrap", false,FABRIC);
    public static final PartDefinition[]
            SWORD={HEAD,HANDLE,FITTING,HANDLEWRAP,SCABBARD,SCABBARDWRAP};
    public static final PartDefinition[] SIMPLE={
            StaticRefs.HEAD,
            StaticRefs.HANDLE
    };


    public static final String STAVE = "stave", BOWSTRING = "string", SIYAH = "siyah";
    public static final String[] bowParts = {STAVE, BOWSTRING, SIYAH};
    public static final String HELM = "helmet", AVENTAIL = "aventail", CREST = "crest";
    public static final String CUIRASS = "cuirass", SPAULDER = "spaulder", MIRROR = "mirror", GAUNTLET = "gauntlet";
    public static final String FAULDS = "faulds", GREAVES = "greaves";
    public static final String BOOTS = "boots";
    public static final String[] headParts = {HELM, AVENTAIL, CREST}, chestParts = {CUIRASS, SPAULDER, MIRROR, GAUNTLET}, legParts = {FAULDS, GREAVES}, footParts = {BOOTS};
    public static final String[][] armorParts = {footParts, legParts, chestParts, headParts};

    public static void populateLists() {
        Iterator<Potion> i = Potion.REGISTRY.iterator();
        while (i.hasNext()) {
            Potion p = i.next();
            if (p != null) {
                if (p.isBadEffect()) badPotEffects.put(p.getName(), p.isInstant());
                else goodPotEffects.put(p.getName(), p.isInstant());
                //LogManager.getLogger("taoism").info("registered "+pot.getName()+" as a "+(pot.isBadEffect()?"bad":"good")+" effect for alchemical purposes");
            }
        }

        for (String name : OreDictionary.getOreNames())
            if (name.startsWith("ingot")) oreList.add(name);
        for (Fluid f : FluidRegistry.getRegisteredFluids().values())
            if (!f.isGaseous()) {
                fluidList.add(f);
                if (f.getTemperature() > HOTTESTFLUID.getTemperature()) HOTTESTFLUID = f;
                else if (f.getTemperature() < COLDESTFLUID.getTemperature()) COLDESTFLUID = f;
            } else gasList.add(f);
//		Taoism.logDebug(oreList.toString());
//		Taoism.logDebug(fluidList.toString());
    }

    public static int tempRange() {
        return HOTTESTFLUID.getTemperature() - COLDESTFLUID.getTemperature();
    }
}
