package com.jackiecrazi.taoism;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jackiecrazi.taoism.common.CommonProxy;
import com.jackiecrazi.taoism.common.block.TaoBlocks;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.TaoBow;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import com.jackiecrazi.taoism.config.TaoConfigs;

@Mod(modid = Taoism.MODID, version = Taoism.VERSION)
public class Taoism {
	public static String BLACK = "\u00A70";
	public static String DARK_BLUE = "\u00A71";
	public static String DARK_GREEN = "\u00A72";
	public static String DARK_AQUA = "\u00A73";
	public static String DARK_RED = "\u00A74";
	public static String DARK_PURPLE = "\u00A75";
	public static String GOLD = "\u00A76";
	public static String GRAY = "\u00A77";
	public static String DARK_GRAY = "\u00A78";
	public static String BLUE = "\u00A79";
	public static String GREEN = "\u00A7a";
	public static String AQUA = "\u00A7b";
	public static String RED = "\u00A7c";
	public static String LIGHT_PURPLE = "\u00A7d";
	public static String YELLOW = "\u00A7e";
	public static String WHITE = "\u00A7f";

	public static final String MODID = "taoism";
	public static final String VERSION = "1.0";
	@SidedProxy(serverSide = "com.jackiecrazi.taoism.common.CommonProxy", clientSide = "com.jackiecrazi.taoism.client.ClientProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper net;
	public static Logger logger = LogManager.getLogger(MODID);
	public static final Random unirand = new Random();
	
	public static final CreativeTabs tabBlu = new CreativeTabs("taoBlu") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(TaoItems.blueprint);
		}

	};
	
	public static final CreativeTabs tabWea = new CreativeTabs("taoWea") {

		@Override
		public ItemStack getTabIconItem() {
			return TaoWeapon.createRandomWeapon(null, unirand);
		}

	};
	
	public static final CreativeTabs tabBow = new CreativeTabs("taoBow") {

		@Override
		public ItemStack getTabIconItem() {
			return TaoBow.createRandomBow(null, unirand);
		}

	};

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(TaoItems.class);
		MinecraftForge.EVENT_BUS.register(TaoBlocks.class);
		
		TaoConfigs.init(event.getModConfigurationDirectory() + "/taoism/");
		proxy.preinit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		proxy.postinit(event);
	}

}
