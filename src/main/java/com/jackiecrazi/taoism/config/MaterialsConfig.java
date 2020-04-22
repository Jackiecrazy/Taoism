package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MaterialStatWrapper;
import com.jackiecrazi.taoism.api.MaterialType;
import com.jackiecrazi.taoism.api.MaterialWrapper;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.util.*;

public class MaterialsConfig {
    public static final String MATHEADER = "All enabled material parts";
    public static final MaterialWrapper FALLBACK=new MaterialWrapper(MaterialStatWrapper.FALLBACK,1);
    public static MaterialStatWrapper lightest, heaviest, sharpest, dullest;
    //TODO material traits
    private static final String[] defaultmatshard = {
            "metal",//human
            "stone",//hell
            "wood",//beast
            "gem",//heaven
            "bone",//preta
            "horn"};//asura
    private static final String[] defaultmatssoft = {
            "skin",//preta
            "sinew",//asura
            "silk",//heaven
            "fiber",//human
            "wool",//beast
            "intestine"};//hell
    private static final String[] defaultmatsfletch = {"feather"};

    public static HashMap<String, String[]> enabledTypes = new HashMap<String, String[]>();
    public static final MaterialStatWrapper[][] DEFAULT = {
            /*{//ingots, high durability at cost of weight and little affinity, and does not support spirits well. It's also magnetic, for what that's worth
            //name,spd,dam,level,dur,aff m, aff w, aff wa, aff f, aff e, r,g,b, dom, draw, aspd,traits

            new MaterialStatWrapper("ingotCopper", 992, 5.5, 2, 180, 1.1, 1, 1, 1.4, 1, 184, 115, 51, 2, 1, 40, 4.9),
            new MaterialStatWrapper("ingotTin", 809, 5, 2, 47, 1.1, 1, 1.4, 1, 1, 211, 212, 213, 2, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotBronze", 906, 6, 2, 250, 1.1, 1, 1.1, 1.4, 1, 205, 127, 50, 2, 2, 45, 5.1),
            new MaterialStatWrapper("ingotIron", 811, 6, 2, 251, 1.5, 1, 1, 1, 1, 67, 75, 77, 3, 1, 54, 5.2),
            new MaterialStatWrapper("ingotCobalt", 972, 6, 3, 800, 1.5, 1, 1, 1, 1, 0, 71, 171, 3, 2, 45, 5.3),
            new MaterialStatWrapper("ingotNickel", 978, 6, 2, 170, 1.5, 1, 1, 1, 1, 184, 184, 182, 3, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotSilver", 1166, 5.5, 2, 25, 1.5, 1, 1, 1, 1, 196, 199, 206, 4, 3, 40, 4.9),//fix
            new MaterialStatWrapper("ingotGold", 2147, 4, 1, 33, 1.5, 1, 1, 1, 1, 255, 191, 0, 4, 3, 40, 4.9),//fix
            new MaterialStatWrapper("ingotLead", 1260, 4, 1, 14, 1.5, 1, 1, 1, 1, 109, 106, 101, 2, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotTungsten", 2178, 7, 4, 1510, 0.3, 0.3, 0.3, 0.3, 0.3, 5, 1, 67, 70, 75, 40, 4.9),//fix
            new MaterialStatWrapper("ingotChromium", 793, 6.5, 3, 248, 1.5, 1, 1, 1, 1, 198, 200, 201, 4, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotManganese", 826, 4, 2, 705, 1.5, 1, 1, 1, 1, 36, 45, 54, 3, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotTitanium", 500, 7, 3, 900, 1.5, 1, 1, 1, 1, 244, 237, 237, 4, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotAluminium", 289, 5, 1, 50, 1.5, 1, 1, 1, 1, 132, 135, 137, 2, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotZinc", 793, 5, 2, 83, 1.5, 1, 1, 1, 1, 186, 196, 200, 2, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotBrass", 853, 6, 2, 15, 1.5, 1, 1, 1, 1, 181, 166, 66, 2, 1, 40, 4.9),//fix
            new MaterialStatWrapper("ingotSteel", 872, 6.75, 4, 750, 1.8, 0.9, 0.9, 0.9, 0.9, 67, 70, 75, 4, 1, 60, 5.5),},
            {
                    //stone, TODO a perk for stone

                    new MaterialStatWrapper("stone", 277, 5, 1, 132, 1, 1, 1, 1, 1.5, 128, 128, 128, 1, 2, 90, 1.0),
                    new MaterialStatWrapper("netherrack", 277, 5, 1, 132, 1, 1, 1, 1, 1.5, 178, 34, 34, 1, 3, 90, 1.0),
                    new MaterialStatWrapper("obsidian", 289, 6, 4, 71, 1, 1, 1.2, 1.2, 1.5, 91, 73, 101, 1, 3, 109, 1.0),
                    new MaterialStatWrapper("flint", 154, 4, 1, 171, 1, 1, 1, 1, 1.5, 105, 105, 105, 1, 1, 109, 1.0),},
            {
                    //wood, very flexible at the cost of low durability. I don't care that modern day steel is more resilient. Metals are too dominant in this day and age
                    //has a very high flexibility multiplier. Also houses spirits very well and can develop them even when processed, but still not as fast as bone

                    new MaterialStatWrapper("logWood", 100, 4, 0, 60, 2, 3, 2, 0.5, 2, 152, 124, 90, 1, 3, 18, 3.0),
                    new MaterialStatWrapper("logOak", 100, 4, 0, 60, 2, 3, 2, 0.5, 2, 152, 124, 90, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logDarkOak", 37, 4, 0, 60, 2, 3, 2, 0.5, 2, 113, 92, 67, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logBirch", 74, 4, 0, 60, 2, 3, 2, 0.5, 2, 252, 251, 227, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logPine", 59, 4, 0, 60, 2, 3, 2, 0.5, 2, 160, 130, 100, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logJungle", 59, 4, 0, 60, 2, 3, 2, 0.5, 2, 160, 124, 90, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logAcacia", 75, 4, 0, 60, 2, 3, 2, 0.5, 2, 255, 140, 0, 2, 3, 18, 3.0),
                    new MaterialStatWrapper("logWillow", 56, 4, 0, 60, 2, 3, 2, 0.5, 2, 252, 251, 227, 2, 5, 18, 3.0),
                    new MaterialStatWrapper("logJujube", 64, 4, 0, 60, 2, 3, 2, 0.5, 2, 152, 124, 90, 2, 5, 18, 3.0),
                    new MaterialStatWrapper("logPeach", 65, 4, 0, 60, 2, 3, 2, 0.5, 2, 255, 140, 0, 2, 5, 18, 3.0),
                    new MaterialStatWrapper("logPersimmon", 100, 4, 0, 60, 2, 3, 2, 0.5, 2, 152, 124, 90, 2, 4, 18, 3.0),
                    new MaterialStatWrapper("logPlum", 88, 4, 0, 60, 2, 3, 2, 0.5, 2, 190, 124, 90, 2, 5, 18, 3.0),
                    new MaterialStatWrapper("logPear", 72, 4, 0, 60, 2, 3, 2, 0.5, 2, 160, 124, 90, 2, 4, 18, 3.0),
                    new MaterialStatWrapper("bamboo", 44, 4, 0, 60, 2, 3, 2, 0.5, 2, 239, 235, 214, 2, 4, 18, 3.0),},
            {
                    //da gemz, fragile but is ridiculously sharp. Also can be used to capture spirits, but only in its raw form, processing it makes it unable to develop a soul
                    new MaterialStatWrapper("gemDiamond", 390, 7, 3, 33, 2, 2, 2.5, 2, 2.5, 185, 242, 255, 4, 4, 1000, 0.1),//fix all of these
                    new MaterialStatWrapper("gemEmerald", 300, 6.5, 2, 101, 2, 2.5, 2, 2, 3, 80, 200, 120, 4, 4, 1000, 0.1),
                    new MaterialStatWrapper("gemQuartz", 289, 5, 3, 101, 2, 2, 2, 2.5, 2.5, 247, 202, 201, 2, 4, 1000, 0.1),
                    new MaterialStatWrapper("gemLapis", 306, 4, 2, 90, 2, 2, 3, 2, 2.5, 38, 97, 156, 1, 4, 1000, 0.1),
                    new MaterialStatWrapper("gemAmber", 122, 5, 2, 90, 2, 3.5, 2, 2, 2.5, 248, 221, 92, 3, 4, 1000, 0.1),
                    new MaterialStatWrapper("gemJade", 376, 5.5, 1, 120, 2, 2.5, 2, 2, 3, 20, 169, 137, 3, 4, 1000, 0.1),},
            {//bone, is very attuned to living souls and supports growth of tsukumogami. A weapon made fully of one type of bone comes with a (slightly unfriendly) soul
                    new MaterialStatWrapper("bone", 222, 5, 1, 200, 1, 1, 1, 1, 1, 238, 234, 217, 1, 7, 38, 3.0)},
            {new MaterialStatWrapper("horn", 213, 5, 1, 200, 1, 1, 1, 1, 1, 238, 234, 217, 2, 7, 38, 3.0)},


            {
                    new MaterialStatWrapper("leather", 96, 7, 81, 1, 1, 1, 1, 1, 229, 136, 0, 1, 7),},
            {new MaterialStatWrapper("sinew", 211, 7, 151, 1, 1, 1, 1, 1, 222, 124, 0, 1, 4),},
            {
                    new MaterialStatWrapper("string", 149, 4, 57, 2, 2, 2, 2, 2, 255, 255, 255, 1, 2),
                    new MaterialStatWrapper("silk", 149, 4, 50, 3, 3, 3, 3, 3, 255, 255, 255, 1, 3),},
            {new MaterialStatWrapper("fiber", 96, 8, 192, 1, 2.5, 1, 1, 1, 228, 217, 111, 1, 4),},
            {new MaterialStatWrapper("wool", 49, 4, 101, 1, 1, 1, 1.5, 1, 255, 255, 255, 1, 3),},
            {new MaterialStatWrapper("intestine", 144, 6, 131, 1, 1, 1, 1, 1, 228, 217, 111, 1, 7)},


            {new MaterialStatWrapper("feather", 144, 6, 131, 1, 1, 1, 1, 1, 228, 217, 111, 1, 7)},//feather, TODO fill in values
            */
            //Open this to item registration in case someone wants to add something one-off, with subdivision stick and ingot/plate, which are worth different quantities (1 and 2 materials to not use doubles). A certain material can have both or only one.
            //this allows you to do more crazy things, like blaze rod shafts (arrow shafts are worth 1). When rendering armor if you made it out of something that only has sticks then it'll portray itself as bundled sticks for plates ,>
            //orrrrrr make armor discriminating so some do not accept sticks and the other type is splintmail so as to save on processing power
            //also split each entry into a json file
            //materials also have traits, see TConstruct

            //run flex through something like a modulus of resilience function and give favours to wood
            //Monsters are frightened by weapons made of their type of bone, and tsukumo of that type do not assault their own kind unless they're competitive anyways

            //name,spd,dam,dur,aff m, aff w, aff wa, aff f, aff e, r,g,b, dom, draw, aspd,traits
    };

    public static final String[][] DICT = {{//ingots, high durability at cost of weight and little affinity, and does not support spirits well. It's also magnetic, for what that's worth

            "ingotCopper,2;blockCopper,18",
            "ingotTin,2;blockTin,18",
            "ingotBronze,2;blockBronze,18",
            "ingotIron,2;blockIron,18",
            "ingotCobalt,2;blockCobalt,18",
            "ingotNickel,2;blockNickel,18",
            "ingotSilver,2;blockSilver,18",
            "ingotGold,2;blockGold,18",
            "ingotLead,2;blockLead,18",
            "ingotTungsten,2;blockTungsten,18",
            "ingotChromium,2;blockChromium,18",
            "ingotManganese,2;blockManganese,18",
            "ingotTitanium,2;blockTitanium,18",
            "ingotAluminium,2;blockAluminium,18",
            "ingotZinc,2;blockZinc,18",
            "ingotBrass,2;blockBrass,18",
            "ingotSteel,2;blockSteel,18",},
            {
                    //stone
                    "stone,2",
                    "netherrack,2",
                    "obsidian,2",
                    "flint,2",},
            {
                    //wood, very flexible at the cost of low durability. I don't care that modern day steel is more resilient. Metals are too dominant in this day and age
                    //has a very high flexibility multiplier. Also houses spirits very well and can develop them even when processed, but still not as fast as bone

                    "logWood,2",
                    "logOak,2",
                    "logDarkOak,2",
                    "logBirch,2",
                    "logPine,2",
                    "logJungle,2",
                    "logAcacia,2",
                    "logWillow,2",
                    "logJujube,2",
                    "logPeach,2",
                    "logPersimmon,2",
                    "logPlum,2",
                    "logPear,2",
                    "bamboo,1;blockBamboo,2",},
            {
                    //da gemz, fragile but is ridiculously sharp. Also can be used to capture spirits, but only in its raw form, processing it makes it unable to develop a soul
                    "gemDiamond,2;blockDiamond,18",
                    "gemEmerald,2;blockEmerald,18",
                    "gemQuartz,2;blockQuartz,18",
                    "gemLapis,2;blockLapis,18",
                    "gemAmber,2;blockAmber,18",
                    "gemJade,2;blockJade,18",},
            {//bone, is very attuned to living souls and supports growth of tsukumogami. A weapon made fully of one type of bone comes with a (slightly unfriendly) soul
                    "bone,1"},
            {"horn,2"},
            {"leather,2",},
            {"sinew,2",},
            {"string,2", "silk,2",},
            {"fiber,2",},
            {"wool,2",},
            {"intestine,2"},
            {"feather,2"},//feather
    };

	/*private Gson writeToJson(MatWrap mw){
		Gson ret=new Gson();
		ret.toJson(mw);
		FileWriter filewrite=new FileWriter("derp derp derp");
		filewrite.wri
	}*/
    //never mind...

    /**
     * This is used to record materials on a per-item or per-oredict-entry
     * basis.
     */
    public static final HashMap<String, MaterialWrapper> loggedItems = new HashMap<String, MaterialWrapper>();
    /**
     * This is used to record materials on a per-entry basis. DO NOT CONFUSE
     * WITH LOGGEDITEMS!
     */
    private static final HashMap<String, ArrayList<MaterialStatWrapper>> loggedMaterials = new HashMap<String, ArrayList<MaterialStatWrapper>>();

    private static final HashMap<String, ArrayList<MaterialStatWrapper>> dummy = new HashMap<String, ArrayList<MaterialStatWrapper>>();
    public static Configuration material;

    @Nullable
    public static MaterialWrapper findMat(ItemStack s) {
        if (loggedItems.get(s.getUnlocalizedName()) != null) return loggedItems.get(s.getUnlocalizedName());
        return loggedItems.get(findName(s));
    }

    @Nonnull
    public static MaterialWrapper findMat(String s) {
        return loggedItems.get(s)!=null?loggedItems.get(s):FALLBACK;
    }

    public static MaterialStatWrapper getRandomMatofType(String type, Random r) {
        return loggedMaterials.get(type).get(r.nextInt(loggedMaterials.get(type).size()));
    }

    public static MaterialStatWrapper getRandomMat(Random r, MaterialType type) {
        //System.out.println(type);
        return dummy.get(type.toString()).get(r.nextInt(dummy.get(type.toString()).size()));
    }

    /**
     * finds the name of an item, similar to findMat() but for its key instead
     *
     * @param s
     * @return
     */
    @Nullable
    public static String findName(ItemStack s) {
        if (s == null || s.isEmpty()) return null;
        int[] a = OreDictionary.getOreIDs(s);
        String dom = NeedyLittleThings.firstOredictName(s);
        for (int o : a) {
            if (loggedItems.get(OreDictionary.getOreName(o)) != null) {
                if (dom == null || loggedItems.get(OreDictionary.getOreName(o)).msw.dominance >= loggedItems.get(dom).msw.dominance) {
                    dom = OreDictionary.getOreName(o);
                }
            }
        }
        return dom;
    }

    public static void init(File path) {/*
        new MaterialStatWrapper("ingotCopper", 992, 5.5f, 2, 180, 1.1f, 1, 1, 1.4f, 1, 184, 115, 51, 2, 1, 40, 4.9f, MaterialType.HARD);

        dummy.put("HARD", new ArrayList<MaterialStatWrapper>());
        dummy.put("SOFT", new ArrayList<MaterialStatWrapper>());
        dummy.put("FLETCH", new ArrayList<MaterialStatWrapper>());
        material = new Configuration(new File(path + "enabledparts.cfg"), Taoism.VERSION, true);
        try {
            material.load();
            enabledTypes.put("HARD", material.get("materials", "hard", defaultmatshard).getStringList());
            enabledTypes.put("SOFT", material.get("materials", "soft", defaultmatssoft).getStringList());
            enabledTypes.put("FLETCH", material.get("materials", "fletch", defaultmatsfletch).getStringList());
        } catch (Exception e) {
            Taoism.logger.fatal("Oh my, the master material config failed to load. We'll only have default material types, then.");
            e.printStackTrace();
        } finally {
            material.save();
        }

        int count = 0;
        for (String emt : enabledTypes.get("HARD")) {
            material = new Configuration(new File(path + "hard/" + emt.toString().toLowerCase() + ".cfg"), Taoism.VERSION, true);

            try {
                material.load();
                if ((material.getCategoryNames().size() == 0 || !material.getDefinedConfigVersion().equals(material.getLoadedConfigVersion())) && Arrays.asList(defaultmatshard).contains(emt)) //a fresh config or an update will add default stuff
                    for (int x = 0; x < DEFAULT[count].length; x++) {
                        material.addCustomCategoryComment(DEFAULT[count][x].name, null);
                    }

                Iterator<String> i = material.getCategoryNames().iterator();

                while (i.hasNext()) {
                    String s = i.next();
                    //the tradeoff, sharp weapons have less arm pen, but deal more damage faster; blunt weapons deal more arm pen from weight and are more durable but are slower
                    Taoism.logger.info("initializing " + s);
                    MaterialStatWrapper msw = lookup(s);
                    int ord = 0;
                    float mass = (float) material.get(s, "my swing time. Also gives some armor piercing damage", msw.swingSpeed).getDouble();
                    mass = 5.336f/(float)Math.pow(mass,0.25);
                    float hard = (float) material.get(s, "my damage. Blunt weapons do not use this", msw.damageOrSpringiness).getDouble();
                    int ml = material.get(s, "my mining level", (int) msw.getMiningLevel()).getInt();
                    //float ms = (float) material.get(s, "my mining speed", lookupDefault(s, ord++)).getDouble();
                    float dura = (float) material.get(s, "my durability", msw.durability).getDouble();
                    float meta = (float) material.get(s, "my affinity for metal", msw.affinity()[0]).getDouble(1.0D);
                    float wood = (float) material.get(s, "my affinity for wood", msw.affinity()[1]).getDouble(1.0D);
                    float wate = (float) material.get(s, "my affinity for water", msw.affinity()[2]).getDouble(1.0D);
                    float fire = (float) material.get(s, "my affinity for fire", msw.affinity()[3]).getDouble(1.0D);
                    float eart = (float) material.get(s, "my affinity for earth", msw.affinity()[4]).getDouble(1.0D);
                    int r = (int) material.get(s, "amount of red in my tool color", msw.color.getRed()).getDouble();
                    int g = (int) material.get(s, "amount of green in my tool color", msw.color.getGreen()).getDouble();
                    int b = (int) material.get(s, "amount of blue in my tool color", msw.color.getBlue()).getDouble();
                    int d = material.get(s, "my material dominance", (int) msw.dominance, "rank from 1 to 10. If an item is registered in two or more ranks the higher one will be used", 1, 10).getInt();
                    int sp = material.get(s, "my spirit receptiveness", (int) msw.lingAbility, "from 1 to 10, 1 being unreceptive and 10 being very receptive", 1, 10).getInt();
                    float draw = (float) material.get(s, "my draw time", msw.drawSpeed).getDouble();
                    float arro = (float) material.get(s, "my arrow speed", msw.arrowSpeed).getDouble();

                    MaterialStatWrapper msw1 = new MaterialStatWrapper(s, mass, hard, ml, dura, meta, wood, wate, fire, eart, r, g, b, d, sp, draw, arro);
                    String[] damn = material.get(s, "The list of items that I apply to, and how much each item counts for the purpose (double it, e.g. 1 ingot is actually 2 here, for arrow shafts)", getApplicableItems(s)).getStringList();
                    //System.out.println(damn);
                    for (String st : damn) {

                        MaterialWrapper mw = new MaterialWrapper(msw1, Integer.valueOf(st.split("\\,")[1]));
                        loggedItems.put(st.split("\\,")[0], mw);
                        //System.out.println("registered "+st.split("\\,")[0]+" as "+mw.amount+" of "+s);
                    }
                    if (!loggedMaterials.containsKey(emt))
                        loggedMaterials.put(emt, new ArrayList<>());
                    loggedMaterials.get(emt).add(msw);
                    dummy.get("HARD").add(msw);
                }
                //System.out.println(loggedMaterials.toString());
            } catch (Exception e) {
                LogManager.getLogger("Taoism").fatal("The hard material config at " + material.getConfigFile().getAbsolutePath() + " failed to load. I'll heartlessly throw an error and continue merrily on my way.");
                e.printStackTrace();
            } finally {
                material.save();

            }
            count++;
        }

        for (String emt : enabledTypes.get("SOFT")) {
            material = new Configuration(new File(path + "soft/" + emt.toString().toLowerCase() + ".cfg"), Taoism.VERSION, true);

            try {
                material.load();
                if ((material.getCategoryNames().size() == 0 || !material.getDefinedConfigVersion().equals(material.getLoadedConfigVersion())) && Arrays.asList(defaultmatssoft).contains(emt)) //a fresh config or an update will add default stuff
                    for (int x = 0; x < DEFAULT[count].length; x++) {
                        material.addCustomCategoryComment(DEFAULT[count][x].name, null);
                    }

                Iterator<String> i = material.getCategoryNames().iterator();

                while (i.hasNext()) {
                    String s = i.next();
                    MaterialStatWrapper mw = lookup(s);
                    //the tradeoff, sharp weapons have less arm pen, but deal more damage faster; blunt weapons deal more arm pen from weight and are more durable but are slower
                    Taoism.logger.info("initializing " + s);
                    int ord = 0;
                    float mass = (float) material.get(s, "my swingSpeed. Affects armor and draw weight", mw.swingSpeed).getDouble();
                    mass = 5.336f/(float)Math.pow(mass,0.25);
                    float hard = (float) material.get(s, "my springiness. Affects armor resistance and draw weight", mw.damageOrSpringiness).getDouble();
                    float flex = (float) material.get(s, "my durability", mw.durability).getDouble();
                    float meta = (float) material.get(s, "my affinity for metal", mw.affinity()[0]).getDouble(1.0D);
                    float wood = (float) material.get(s, "my affinity for wood", mw.affinity()[1]).getDouble(1.0D);
                    float wate = (float) material.get(s, "my affinity for water", mw.affinity()[2]).getDouble(1.0D);
                    float fire = (float) material.get(s, "my affinity for fire", mw.affinity()[3]).getDouble(1.0D);
                    float eart = (float) material.get(s, "my affinity for earth", mw.affinity()[4]).getDouble(1.0D);
                    int r = (int) material.get(s, "amount of red in my tool color", mw.color.getRed()).getDouble();
                    int g = (int) material.get(s, "amount of green in my tool color", mw.color.getGreen()).getDouble();
                    int b = (int) material.get(s, "amount of blue in my tool color", mw.color.getBlue()).getDouble();
                    int d = material.get(s, "my material dominance", (int) mw.dominance, "rank from 1 to 10. If an item is registered in two or more ranks the higher one will be used", 1, 10).getInt();
                    int sp = material.get(s, "my spirit receptiveness", (int) mw.lingAbility, "from 1 to 10, 1 being unreceptive and 10 being very receptive", 1, 10).getInt();
                    MaterialStatWrapper msw = new MaterialStatWrapper(s, mass, hard, flex, meta, wood, wate, fire, eart, r, g, b, d, sp);
                    for (String st : material.get(s, "The list of items that I apply to, and how much each item counts for the purpose (double it, e.g. 1 ingot is actually 2 here, for arrow shafts)", getApplicableItems(s)).getStringList()) {
                        //System.out.println(st);
                        MaterialWrapper ms = new MaterialWrapper(msw, Integer.valueOf(st.split("\\,")[1]));
                        loggedItems.put(st.split("\\,")[0], ms);

                    }
                    if (!loggedMaterials.containsKey(emt))
                        loggedMaterials.put(emt, new ArrayList<>());
                    loggedMaterials.get(emt).add(msw);
                    dummy.get("SOFT").add(msw);
                }
                //System.out.println(loggedMaterials.toString());
            } catch (Exception e) {
                LogManager.getLogger("Taoism").fatal("The soft material config at " + material.getConfigFile().getAbsolutePath() + " failed to load. I'll heartlessly throw an error and continue merrily on my way.");
                e.printStackTrace();
            } finally {
                material.save();

            }
            count++;
        }
        for (String emt : enabledTypes.get("FLETCH")) {
            material = new Configuration(new File(path + "fletch/" + emt.toString().toLowerCase() + ".cfg"), Taoism.VERSION, true);

            try {
                material.load();
                if ((material.getCategoryNames().size() == 0 || !material.getDefinedConfigVersion().equals(material.getLoadedConfigVersion())) && Arrays.asList(defaultmatsfletch).contains(emt)) //a fresh config or an update will add default stuff
                    for (int x = 0; x < DEFAULT[count].length; x++) {
                        material.addCustomCategoryComment(DEFAULT[count][x].name, null);
                    }

                Iterator<String> i = material.getCategoryNames().iterator();

                while (i.hasNext()) {
                    String s = i.next();
                    MaterialStatWrapper mw = lookup(s);
                    //the tradeoff, sharp weapons have less arm pen, but deal more damage faster; blunt weapons deal more arm pen from weight and are more durable but are slower
                    Taoism.logger.info("initializing " + s);
                    int ord = 0;
                    float mass = (float) material.get(s, "my swingSpeed. Affects arrow speed", mw.swingSpeed).getDouble();
                    mass = 5.336f/(float)Math.pow(mass,0.25);
                    float hard = (float) material.get(s, "my resistance. Affects arrow straightness", mw.damageOrSpringiness).getDouble();
                    float flex = (float) material.get(s, "my durability, which kinda translates to how many arrows in a stack", mw.durability).getDouble();
                    float meta = (float) material.get(s, "my affinity for metal", mw.affinity()[0]).getDouble(1.0D);
                    float wood = (float) material.get(s, "my affinity for wood", mw.affinity()[1]).getDouble(1.0D);
                    float wate = (float) material.get(s, "my affinity for water", mw.affinity()[2]).getDouble(1.0D);
                    float fire = (float) material.get(s, "my affinity for fire", mw.affinity()[3]).getDouble(1.0D);
                    float eart = (float) material.get(s, "my affinity for earth", mw.affinity()[4]).getDouble(1.0D);
                    int r = (int) material.get(s, "amount of red in my tool color", mw.color.getRed()).getDouble();
                    int g = (int) material.get(s, "amount of green in my tool color", mw.color.getGreen()).getDouble();
                    int b = (int) material.get(s, "amount of blue in my tool color", mw.color.getBlue()).getDouble();
                    int d = material.get(s, "my material dominance", (int) mw.dominance, "rank from 1 to 10. If an item is registered in two or more ranks the higher one will be used", 1, 10).getInt();
                    int sp = material.get(s, "my spirit receptiveness", (int) mw.lingAbility, "from 1 to 10, 1 being unreceptive and 10 being very receptive", 1, 10).getInt();
                    MaterialStatWrapper msw = new MaterialStatWrapper(s, MaterialType.FLETCH, mass, hard, flex, new float[]{meta, wood, wate, fire, eart}, new Color(r, g, b), d, sp, 0, 0);
                    for (String st : material.get(s, "The list of items that I apply to, and how much each item counts for the purpose (double it, e.g. 1 ingot is actually 2 here, for arrow shafts)", getApplicableItems(s)).getStringList()) {
                        //System.out.println(st);
                        MaterialWrapper ms = new MaterialWrapper(msw, Integer.valueOf(st.split("\\,")[1]));//FIXME this only gets the front part, the back number doesn't count for some infernal reason
                        loggedItems.put(st.split("\\,")[0], ms);

                    }
                    if (!loggedMaterials.containsKey(emt))
                        loggedMaterials.put(emt, new ArrayList<MaterialStatWrapper>());
                    loggedMaterials.get(emt).add(msw);
                    dummy.get("FLETCH").add(msw);
                }
                //System.out.println(loggedMaterials.toString());
            } catch (Exception e) {
                LogManager.getLogger("Taoism").fatal("The fletching material config at " + material.getConfigFile().getAbsolutePath() + " failed to load. I'll heartlessly throw an error and continue merrily on my way.");
                e.printStackTrace();
            } finally {
                material.save();

            }
            count++;
        }*/
    }

    private static MaterialStatWrapper lookup(String mat) {
        for (MaterialStatWrapper[] msww : DEFAULT) {
            for (MaterialStatWrapper msw : msww) {
                if (msw.name.equals(mat)) return msw;
            }
        }
        return DEFAULT[0][0];
    }

    private static String[] getApplicableItems(String mat) {
        for (int longnamesoidontuseit = 0; longnamesoidontuseit < 2; longnamesoidontuseit++) {
            for (String[] st : DICT) {
                for (String s : st)
                    if (s.startsWith(mat)) {
                        String[] more = s.split(";");
                        //System.out.println(more);
                        return more;

                    }
            }
            mat = "ingotIron";
            System.out.println("commencing second search");
        }
        return new String[]{};
    }
}
