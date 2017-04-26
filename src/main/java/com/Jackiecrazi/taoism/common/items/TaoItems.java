package com.Jackiecrazi.taoism.common.items;

import java.awt.Color;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemReed;
import net.minecraftforge.common.util.EnumHelper;

import com.Jackiecrazi.taoism.CommonProxy;
import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.items.armor.ClothingWushu;
import com.Jackiecrazi.taoism.common.items.dummies.ItemMuRenZhuang;
import com.Jackiecrazi.taoism.common.items.dummies.ItemSandbag;
import com.Jackiecrazi.taoism.common.items.equipment.ItemGongFa;
import com.Jackiecrazi.taoism.common.items.equipment.headdress.HeaddressLianPu;
import com.Jackiecrazi.taoism.common.items.resource.ItemResource;
import com.Jackiecrazi.taoism.common.items.tools.Hammer;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.items.weapons.SilverNeedle;

import cpw.mods.fml.common.registry.GameRegistry;

public class TaoItems {
	public static Item.ToolMaterial tawood = EnumHelper.addToolMaterial(
			"TAWOOD", 0, 89, 2.0F, -0.5F, 30);
	public static Item.ToolMaterial tairon = EnumHelper.addToolMaterial(
			"TAIRON", 2, 375, 6.0F, 1.5F, 20);
	public static Item.ToolMaterial tagem = EnumHelper.addToolMaterial("TAGEM",
			3, 48, 2.0F, 2.5F, 40);
	public static Item.ToolMaterial tamagic = EnumHelper.addToolMaterial("TALEGEND",
			3, 999, 2.0F, 9F, 45);
	public static ItemArmor.ArmorMaterial tacloth = EnumHelper.addArmorMaterial("TACLOTH", 40, new int[] { 0, 1, 0, 0 }, 30);
	  public static ItemArmor.ArmorMaterial tametal = EnumHelper.addArmorMaterial("TAMETAL", 100, new int[] { 3, 9, 4, 4 }, 15);
	  public static ItemArmor.ArmorMaterial talegend = EnumHelper.addArmorMaterial("TALEGEND", 110, new int[] { 1, 10, 6, 3 }, 40);

	  private static String[] metals={
			//lianqi materials
				"HuangTongIngot",//0
				"ChiJinIngot",//1
				"JiuTianHanTieIngot",//2
				"XuanTieIngot",//3
				"WuJinIngot",//4
				"ZiTongIngot",//5
				"QingTongIngot",//6
				"YingIngot",//7
				//two dummies for rendering
				"DummyNormalIngot",//8
				"DummyHighIngot",//9
	  };
	  private static Color[] metalcolors={
			//metals
				new Color(205,79,3),
				new Color(167,69,35),
				new Color(119,188,184),
				new Color(140,140,140),
				new Color(67,67,67),
				new Color(114,89,126),
				new Color(77,166,124),
				new Color(249,249,249),
				//dummies
				new Color(165,189,192),
				new Color(133,195,200),
	  };
	  
	  
	  
	  private static String[] wood={
			//wooden lianqi materials
				"ZaoMu",
				"TaoMu",
				"LiuMu",
				"BambooMu",
	  };
	  private static Color[] woodcolors={
			//wood
				new Color(164,46,3),
				new Color(209,103,69),
				new Color(226,93,27),
	  };
	  
	  
	  
	  private static String[] quench={
			//quench materials
				"ShanQuanShuiQuench",//6
				"YanJiangQuench",//7
				"BingQuench",//8
	  };
	  private static Color[] quenchcolors={
//quench
				new Color(64, 164, 223),
				new Color(81,6,13),
				new Color(212,240,255),
	  };
	  
	  
	  
	  private static String[] gem={
			//misc.
				"JadeGem"  
	  };
	  private static Color[] gemcolors={
			//misc
				new Color(42,241,96)
	  };
	  
	  
	  
	  private static String[] mob={
		  //mob drops
		  	"skin",
		  	"fat",
		  	"flesh",
		  	"bone",
		  	//"scales",
		  	"teeth",
		  	"claw",
		  	"eye",
		  	"stomach",
		  	"intestines",
		  	"lungs",
		  	"heart"
	  };
	  
	  
	  
	public static Item SilverNeedle;
	public static Item Talisman;
	public static CommonProxy proxy;
	public static Item Scroll,Ding,Anvil,Bellows,QiPu,Parts,GongFa,ResourceMetal,ResourceWood,ResourceGem,ResourceQuench,ResourceMob;
	public static Item QiangWood, SwordCherry, StickWood;
	public static Item QiangIron,QiangFancy, YueYaChan, StickIron,Cha,ChangChui,YanYueDao,DaDao,LongDoubleJi,ShortDoubleJi,LongSingleJi,Ji4,Ge;
	public static Item Jian, SwordIron, LargeSword, Macuahuitl, Gou, Chu, LongSword, EMeiCi,Bian,Chui,HuanShouDao,ChangDao,ZhiDao,MoDao,PuDao,Fu1,Fu2,Fu3,Yue,YuanYangYue;
	public static Item incense;
	public static Item sandbag, muRenZhuang;
	public static Item wushuRibbon,wushuShirt,wushuPants,wushuShoes;//Levelable armor comes later, make note
	public static Item LianPu;
	
	
	public static Item unown,hammer;

	public static void init() {
		unown=new DummySlotItem();
		ri(unown,"taoslots");
		hammer=new Hammer(tairon);
		ri(hammer, "taohamertime");
		
		GongFa=new ItemGongFa();
		ri(GongFa,"gongfa");
		LianPu=new HeaddressLianPu();
		ri(LianPu,"LianPu");
		
		Talisman = new Fuuuu();
		//GameRegistry.registerItem(Fu, "WrittenFu"); TODO reenable when it happens
		Ding=new ItemReed(TaoBlocks.LianQiDing).setTextureName("taoism:qilu").setCreativeTab(Taoism.TabTaoistMaterials).setUnlocalizedName("lianqilu").setMaxStackSize(1);
		ri(Ding,"TaoisticDingItem");
		QiPu=new ItemQiPu();
		ri(QiPu,"qipu");
		Parts=new ItemWeaponPart();
		ri(Parts,"parts");
		ResourceMetal=new ItemResource("metal",metals,metalcolors);
		ResourceWood=new ItemResource("wood",wood,woodcolors);
		ResourceQuench=new ItemResource("quench",quench,quenchcolors);
		ResourceGem=new ItemResource("gem",gem,gemcolors);
		
		ri(ResourceMetal,"TaoisticResourcesMetal");
		ri(ResourceWood,"TaoisticResourcesWood");
		ri(ResourceQuench,"TaoisticResourcesQuench");
		ri(ResourceGem,"TaoisticResourcesGem");
		
		if (WayofConfig.WeaponsEnabled) {
			//TODO balance the weapons!
			/*SwordCherry = new SwordGenericWood(tawood);
			GameRegistry.registerItem(SwordCherry, "SwordCherry");*/
			SwordIron = new GenericTaoistWeapon(tairon, "fword", 4, 0.1f, 7, StaticRefs.SWORDBLADE, StaticRefs.POMMEL,StaticRefs.HANDLE,StaticRefs.GUARD,StaticRefs.SWORDBLADE,StaticRefs.EDGE);
			Chu = new GenericTaoistWeapon(tairon, "chu", 6, 0.4f, 15, StaticRefs.SWORDBLADE, StaticRefs.POMMEL,StaticRefs.SHAFT,StaticRefs.GUARD,StaticRefs.SWORDBLADE,StaticRefs.EDGE);
			LongSword=new GenericTaoistWeapon(tairon, "changjian", 6, 0, 7, StaticRefs.SWORDBLADE, StaticRefs.POMMEL,StaticRefs.HANDLE,StaticRefs.GUARD,StaticRefs.SWORDBLADE,StaticRefs.EDGE);
			LargeSword = new GenericTaoistWeapon(tairon, "jujian", 5, 0.2f, 10, StaticRefs.SWORDBLADE, StaticRefs.POMMEL,StaticRefs.HANDLE,StaticRefs.GUARD,StaticRefs.SWORDBLADE,StaticRefs.EDGE);
			Macuahuitl=new GenericTaoistWeapon(tairon, "macuahuitl", 6, 0.2f, 20, StaticRefs.SWORDBLADE, StaticRefs.HANDLE,StaticRefs.GUARD, StaticRefs.POMMEL,StaticRefs.SWORDBLADE);
			Gou=new GenericTaoistWeapon(tairon, "gou", 5, 0.4f, 5, StaticRefs.SWORDBLADE,StaticRefs.HANDLE,StaticRefs.GUARD,StaticRefs.SWORDBLADE);
			GameRegistry.registerItem(SwordIron, "SwordIron");
			GameRegistry.registerItem(LongSword, "longsword");
			GameRegistry.registerItem(LargeSword, "bigsword");
			GameRegistry.registerItem(Chu, "reallylongsword");
			ri(Macuahuitl,"macuahuitl");
			ri(Gou,"gou");
			/*StickWood = new Gun(tawood);
			GameRegistry.registerItem(StickWood, "StickieWood");
			QiangWood = new Qiang(tawood);
			GameRegistry.registerItem(QiangWood, "QiangWood");*/
			
			QiangIron = new GenericTaoistWeapon(tairon, "yari", 7, 0.2f, 15, StaticRefs.TIP, StaticRefs.POMMEL,StaticRefs.SHAFT,StaticRefs.GUARD,StaticRefs.TIP);
			QiangFancy = new GenericTaoistWeapon(tairon, "fancyyari", 7, 0.2f, 15, StaticRefs.TIP, StaticRefs.POMMEL,StaticRefs.SHAFT,StaticRefs.GUARD,StaticRefs.TIP);
			ri(QiangFancy,"fancyyari");
			GameRegistry.registerItem(QiangIron, "QiangIron");
			
			StickIron = new GenericTaoistWeapon(tairon, "astick", 6.2f, 0.01f, 10, StaticRefs.SHAFT, StaticRefs.SHAFT,StaticRefs.CROWN);
			GameRegistry.registerItem(StickIron, "StickieIron");
			
			Jian = new GenericTaoistWeapon(tairon, "jian", 4, 0.03f, 10, StaticRefs.SHAFT, StaticRefs.SHAFT,StaticRefs.HANDLE,StaticRefs.GUARD);
			GameRegistry.registerItem(Jian, "Jian");
			
			SilverNeedle = new SilverNeedle(tairon);
			GameRegistry.registerItem(SilverNeedle, "SilverNeedle");
			
			EMeiCi=new GenericTaoistWeapon(true,tairon, "emeici", 2, 0, 5, StaticRefs.TIP, StaticRefs.HANDLE,StaticRefs.TIP);
			YuanYangYue=new GenericTaoistWeapon(true,tairon, "yyy", 2, 0, 5, StaticRefs.TIP, StaticRefs.HANDLE,StaticRefs.TIP);
			GameRegistry.registerItem(EMeiCi, "EMeiCi");
			ri(YuanYangYue,"yyy");
			
			Bian=new GenericTaoistWeapon(tairon, "bian", 5, 0.2f, 10, StaticRefs.KNOTS, StaticRefs.SHAFT,StaticRefs.HANDLE,StaticRefs.POMMEL,StaticRefs.GUARD,StaticRefs.KNOTS);
			ri(Bian,"Bian");
			
			Cha=new GenericTaoistWeapon(tairon, "cha", 7, 0.6f, 15, StaticRefs.PRONGS, StaticRefs.SHAFT,StaticRefs.PRONGS,StaticRefs.POMMEL,StaticRefs.HEAD);
			ri(Cha,"Cha");
			
			Chui=new GenericTaoistWeapon(tairon, "chui", 4, 1f, 20, StaticRefs.HEAD, StaticRefs.HANDLE,StaticRefs.HEAD,StaticRefs.POMMEL,StaticRefs.GUARD);
			ChangChui=new GenericTaoistWeapon(tairon, "changchui", 7, 1.3f, 25, StaticRefs.HEAD, StaticRefs.SHAFT,StaticRefs.HEAD,StaticRefs.POMMEL,StaticRefs.GUARD);
			ri(Chui,"Chui");
			ri(ChangChui,"longChui");
			
			YanYueDao=new GenericTaoistWeapon(tairon, "yanyuedao", 7, 0.6f, 15, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE);
			DaDao=new GenericTaoistWeapon(tairon, "dadao", 7, 0.6f, 15, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			YueYaChan= new GenericTaoistWeapon(tairon, "yueyachan", 7, 0.2f, 10, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.HEAD,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE);
			ri(YanYueDao, "YanYueDao");
			ri(YueYaChan, "YueYaChan");
			ri(DaDao,"dadao");
			
			HuanShouDao=new GenericTaoistWeapon(tairon, "huanshoudao", 4, 0.6f, 10, StaticRefs.EDGE, StaticRefs.LOOP,StaticRefs.HANDLE,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			ZhiDao=new GenericTaoistWeapon(tairon, "zhidao", 4, 0.4f, 10, StaticRefs.EDGE,StaticRefs.HANDLE,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			MoDao=new GenericTaoistWeapon(tairon, "modao", 4, 0.6f, 10, StaticRefs.EDGE,StaticRefs.HANDLE,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			ChangDao=new GenericTaoistWeapon(tairon, "changdao", 5, 0.7f, 11, StaticRefs.EDGE, StaticRefs.HANDLE,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			PuDao=new GenericTaoistWeapon(tairon, "pudao", 4, 0.7f, 10, StaticRefs.EDGE, StaticRefs.HANDLE,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.EDGE,StaticRefs.POMMEL);
			ri(HuanShouDao,"Dao");
			ri(ZhiDao,"zhidao");
			ri(MoDao,"modao");
			ri(ChangDao,"changdao");
			ri(PuDao,"pudao");
			
			LongDoubleJi=new GenericTaoistWeapon(tairon, "ji1", 8, 0.4f, 15, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.TIP,StaticRefs.EDGE,StaticRefs.POMMEL);
			ShortDoubleJi=new GenericTaoistWeapon(tairon, "ji2", 4, 0.1f, 13, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.TIP,StaticRefs.EDGE,StaticRefs.POMMEL);
			LongSingleJi=new GenericTaoistWeapon(tairon, "ji3", 8, 0.4f, 15, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.TIP,StaticRefs.EDGE,StaticRefs.POMMEL);
			Ji4=new GenericTaoistWeapon(tairon, "ji4", 4, 0.1f, 12, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.TIP,StaticRefs.EDGE,StaticRefs.POMMEL);
			ri(LongDoubleJi,"Ji1");
			ri(ShortDoubleJi,"Ji2");
			ri(LongSingleJi,"Ji3");
			//ri(Ji4,"Ji4");
			
			Ge=new GenericTaoistWeapon(tairon, "ge", 8, 0.2f, 18, StaticRefs.DAOBLADE, StaticRefs.SHAFT,StaticRefs.DAOBLADE,StaticRefs.GUARD,StaticRefs.POMMEL);
			ri(Ge,"ge");
			
			Fu1=new GenericTaoistWeapon(tairon, "fu1", 5, 0.8f, 20, StaticRefs.EDGE, StaticRefs.HANDLE,StaticRefs.HEAD,StaticRefs.EDGE,StaticRefs.POMMEL);
			Fu2=new GenericTaoistWeapon(tairon, "fu2", 5, 0.8f, 20, StaticRefs.EDGE, StaticRefs.HANDLE,StaticRefs.HEAD,StaticRefs.EDGE,StaticRefs.POMMEL);
			Fu3=new GenericTaoistWeapon(tairon, "fu3", 7, 0.8f, 20, StaticRefs.EDGE, StaticRefs.SHAFT,StaticRefs.HEAD,StaticRefs.EDGE,StaticRefs.POMMEL);
			Yue=new GenericTaoistWeapon(tairon, "yue", 8, 1f, 10, StaticRefs.HEAD, StaticRefs.SHAFT,StaticRefs.HEAD,StaticRefs.GUARD);
			ri(Fu1,"Fu1");
			ri(Fu2,"Fu2");
			ri(Fu3,"Fu3");
			ri(Yue,"yue");
			
		}
		incense = new Incense();
			GameRegistry.registerItem(incense, "incense");
		if(WayofConfig.ArmorEnabled){
			wushuRibbon=new ClothingWushu(tacloth, 0, 0);
			wushuShirt=new ClothingWushu(tacloth,0,1);
			wushuPants=new ClothingWushu(tacloth,0,2);
			wushuShoes=new ClothingWushu(tacloth,0,3);
			ri(wushuRibbon,"WuShuRibbon");
			GameRegistry.registerItem(wushuShirt,"WuShuShirt");
			GameRegistry.registerItem(wushuPants,"WuShuPants");
			GameRegistry.registerItem(wushuShoes,"WuShuShoes");
		}
		sandbag = new ItemSandbag();
		GameRegistry.registerItem(sandbag, "dummySandbag");
		muRenZhuang = new ItemMuRenZhuang();
		GameRegistry.registerItem(muRenZhuang, "dummyMuRenZhuang");
		
	}
	public static void postInit(){
		Scroll=new ItemSkillScroll();
		ri(Scroll,"taoisticSkillScroll");
	}
	private static void ri(Item hi,String bye){
		GameRegistry.registerItem(hi, bye);
	}
}
