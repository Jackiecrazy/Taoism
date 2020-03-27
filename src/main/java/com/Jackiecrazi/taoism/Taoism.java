package com.jackiecrazi.taoism;

import com.jackiecrazi.taoism.common.CommonProxy;
import com.jackiecrazi.taoism.common.block.TaoBlocks;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.TaoConfigs;
import com.jackiecrazi.taoism.crafting.TaoCrafting;
import com.jackiecrazi.taoism.handler.TaoCapabilityHandler;
import com.jackiecrazi.taoism.handler.TaoisticEventHandler;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Random;

@Mod(modid = Taoism.MODID, version = Taoism.VERSION)
public class Taoism {
    public static final Field atk = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_184617_aD");
    public static final String MODID = "taoism";
    public static final String VERSION = "1.0";
    public static final Random unirand = new Random();
    public static final CreativeTabs tabBlu = new CreativeTabs("taoBlu") {

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(TaoItems.blueprint);
        }

    };
    public static final CreativeTabs tabWea = new CreativeTabs("taoWea") {
        ItemStack icon = new ItemStack(Items.IRON_SWORD);

        @Override
        public ItemStack getTabIconItem() {
            return icon;
        }

    };
    @Mod.Instance(Taoism.MODID)
    public static Taoism INST = new Taoism();
    @SidedProxy(serverSide = "com.jackiecrazi.taoism.common.CommonProxy", clientSide = "com.jackiecrazi.taoism.client.ClientProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper net;
    public static Logger logger = LogManager.getLogger(MODID);
	/*
	public static final CreativeTabs tabBow = new CreativeTabs("taoBow") {

		@Override
		public ItemStack getTabIconItem() {
			return TaoBow.createRandomBow(null, unirand);
		}

	};
	
	public static final CreativeTabs tabArr = new CreativeTabs("taoArr") {

		@Override
		public ItemStack getTabIconItem() {
			return TaoBow.createRandomBow(null, unirand);
		}

	};

	 */

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(TaoItems.class);
        MinecraftForge.EVENT_BUS.register(TaoBlocks.class);
        MinecraftForge.EVENT_BUS.register(TaoCrafting.class);
        MinecraftForge.EVENT_BUS.register(TaoPotion.class);
        MinecraftForge.EVENT_BUS.register(TaoisticEventHandler.class);
        MinecraftForge.EVENT_BUS.register(TaoCapabilityHandler.class);
        MinecraftForge.EVENT_BUS.register(TaoEntities.class);

        //MinecraftForge.EVENT_BUS.register(ClientProxy.class);
        TaoConfigs.init(event.getModConfigurationDirectory() + "/taoism/");
        net = NetworkRegistry.INSTANCE.newSimpleChannel("TaoistChannel");
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
