package com.Jackiecrazi.taoism.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.client.render.block.RenderDing;
import com.Jackiecrazi.taoism.client.render.block.RenderFengxiang;
import com.Jackiecrazi.taoism.client.render.block.RenderTE;
import com.Jackiecrazi.taoism.client.render.block.RenderTieZhen;
import com.Jackiecrazi.taoism.client.render.entity.RenderDroppedWeapon;
import com.Jackiecrazi.taoism.client.render.entity.RenderLevitatingItems;
import com.Jackiecrazi.taoism.client.render.entity.RenderMuRenZhuang;
import com.Jackiecrazi.taoism.client.render.entity.RenderSandbag;
import com.Jackiecrazi.taoism.client.render.entity.mobs.RenderDijiang;
import com.Jackiecrazi.taoism.client.render.entity.mobs.RenderLili;
import com.Jackiecrazi.taoism.client.render.entity.mobs.RenderShuhu;
import com.Jackiecrazi.taoism.client.render.entity.mobs.RenderYinyu;
import com.Jackiecrazi.taoism.client.render.weapons.RenderBian;
import com.Jackiecrazi.taoism.client.render.weapons.RenderBigWeapons;
import com.Jackiecrazi.taoism.client.render.weapons.RenderDao;
import com.Jackiecrazi.taoism.client.render.weapons.RenderDoubleBigWeapons;
import com.Jackiecrazi.taoism.client.render.weapons.RenderEMeiCi;
import com.Jackiecrazi.taoism.client.render.weapons.RenderFuOne;
import com.Jackiecrazi.taoism.client.render.weapons.RenderFuThree;
import com.Jackiecrazi.taoism.client.render.weapons.RenderFuTwo;
import com.Jackiecrazi.taoism.client.render.weapons.RenderHammer;
import com.Jackiecrazi.taoism.client.render.weapons.RenderJiFour;
import com.Jackiecrazi.taoism.client.render.weapons.RenderJiOne;
import com.Jackiecrazi.taoism.client.render.weapons.RenderJiThree;
import com.Jackiecrazi.taoism.client.render.weapons.RenderJiTwo;
import com.Jackiecrazi.taoism.client.render.weapons.RenderJian;
import com.Jackiecrazi.taoism.client.render.weapons.RenderNeedle;
import com.Jackiecrazi.taoism.client.render.weapons.RenderSpearNew;
import com.Jackiecrazi.taoism.client.render.weapons.RenderStick;
import com.Jackiecrazi.taoism.client.render.weapons.RenderSword;
import com.Jackiecrazi.taoism.client.render.weapons.RenderTrident;
import com.Jackiecrazi.taoism.client.render.weapons.RenderYanYueDao;
import com.Jackiecrazi.taoism.common.block.ModBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TileAnvil;
import com.Jackiecrazi.taoism.common.block.tile.TileBellows;
import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.entity.EntityLevitatingItem;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityDroppedWeapon;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLiLi;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLuoYu;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityDiJiang;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityShuHu;
import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ModRenders {
	public static void drawEntity(){
		RenderingRegistry.registerEntityRenderingHandler(EntitySandbag.class, new RenderSandbag());
		RenderingRegistry.registerEntityRenderingHandler(EntityMuRenZhuang.class, new RenderMuRenZhuang());
		RenderingRegistry.registerEntityRenderingHandler(EntityDroppedWeapon.class, new RenderDroppedWeapon());
		RenderingRegistry.registerEntityRenderingHandler(EntityLevitatingItem.class, new RenderLevitatingItems());
		RenderingRegistry.registerEntityRenderingHandler(EntityLuoYu.class, new RenderYinyu());
		RenderingRegistry.registerEntityRenderingHandler(EntityShuHu.class, new RenderShuhu());
		RenderingRegistry.registerEntityRenderingHandler(EntityDiJiang.class, new RenderDijiang());
		RenderingRegistry.registerEntityRenderingHandler(EntityLiLi.class, new RenderLili());
		Taoism.logDebug("Successfully drew everything");
	}
	public static void drawItem(){
			MinecraftForgeClient.registerItemRenderer(ModItems.StickWood, new RenderBigWeapons(1f,0.5f));
			MinecraftForgeClient.registerItemRenderer(ModItems.StickIron, new RenderBigWeapons(1f,0.3f,90));
			MinecraftForgeClient.registerItemRenderer(ModItems.SilverNeedle, new RenderNeedle());
			MinecraftForgeClient.registerItemRenderer(ModItems.QiangWood, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.QiangIron, new RenderBigWeapons(1f,0.3f));
			MinecraftForgeClient.registerItemRenderer(ModItems.QiangFancy, new RenderBigWeapons(1f));
			
			MinecraftForgeClient.registerItemRenderer(ModItems.Jian, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Scroll, new RenderScroll());
			MinecraftForgeClient.registerItemRenderer(ModItems.SwordIron, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.LargeSword, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.LongSword, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Macuahuitl, new RenderBigWeapons(0.75f));

			MinecraftForgeClient.registerItemRenderer(ModItems.Chu, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Bian, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Cha, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.YanYueDao, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.YueYaChan, new RenderBigWeapons(1f,0.5f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Ge, new RenderBigWeapons(1f,0.3f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Gou, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Yue, new RenderBigWeapons(1,0.5f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Chui, new RenderBigWeapons(0.75f,0.2f));
			MinecraftForgeClient.registerItemRenderer(ModItems.ChangChui, new RenderBigWeapons(1f));

			MinecraftForgeClient.registerItemRenderer(ModItems.HuanShouDao, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.ChangDao, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(ModItems.MoDao, new RenderBigWeapons(0.85f));
			MinecraftForgeClient.registerItemRenderer(ModItems.ZhiDao, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(ModItems.DaDao, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.PuDao, new RenderBigWeapons(0.65f));
			
			MinecraftForgeClient.registerItemRenderer(ModItems.EMeiCi, new RenderDoubleBigWeapons(0.5f,0.5f));
			MinecraftForgeClient.registerItemRenderer(ModItems.YuanYangYue, new RenderDoubleBigWeapons(0.5f,0.5f));
			
			MinecraftForgeClient.registerItemRenderer(ModItems.LongDoubleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.ShortDoubleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.LongSingleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Ji4, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Fu1, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Fu2, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(ModItems.Fu3, new RenderBigWeapons(0.65f));
			
		
			if(WayofConfig.FancyEnabled){
				MinecraftForgeClient.registerItemRenderer(ModItems.StickWood, new RenderStick());
				MinecraftForgeClient.registerItemRenderer(ModItems.StickIron, new RenderStick());
				MinecraftForgeClient.registerItemRenderer(ModItems.SilverNeedle, new RenderNeedle());
				MinecraftForgeClient.registerItemRenderer(ModItems.QiangWood, new RenderSpearNew());
				MinecraftForgeClient.registerItemRenderer(ModItems.QiangIron, new RenderSpearNew());
				MinecraftForgeClient.registerItemRenderer(ModItems.Jian, new RenderJian());
				MinecraftForgeClient.registerItemRenderer(ModItems.Scroll, new RenderScroll());
				MinecraftForgeClient.registerItemRenderer(ModItems.SwordIron, new RenderSword());
				MinecraftForgeClient.registerItemRenderer(ModItems.Bian, new RenderBian());
				MinecraftForgeClient.registerItemRenderer(ModItems.Cha, new RenderTrident());
				MinecraftForgeClient.registerItemRenderer(ModItems.YanYueDao, new RenderYanYueDao());
				MinecraftForgeClient.registerItemRenderer(ModItems.Chui, new RenderHammer());
				MinecraftForgeClient.registerItemRenderer(ModItems.HuanShouDao, new RenderDao());
				MinecraftForgeClient.registerItemRenderer(ModItems.EMeiCi, new RenderEMeiCi());
				MinecraftForgeClient.registerItemRenderer(ModItems.LongDoubleJi, new RenderJiOne());
				MinecraftForgeClient.registerItemRenderer(ModItems.ShortDoubleJi, new RenderJiTwo());
				MinecraftForgeClient.registerItemRenderer(ModItems.LongSingleJi, new RenderJiThree());
				MinecraftForgeClient.registerItemRenderer(ModItems.Ji4, new RenderJiFour());
				MinecraftForgeClient.registerItemRenderer(ModItems.Fu1, new RenderFuOne());
				MinecraftForgeClient.registerItemRenderer(ModItems.Fu2, new RenderFuTwo());
				MinecraftForgeClient.registerItemRenderer(ModItems.Fu3, new RenderFuThree());
			}
			TileEntitySpecialRenderer ding=new RenderDing(),anvil=new RenderTieZhen(),bellows=new RenderFengxiang();
		MinecraftForgeClient.registerItemRenderer(ModItems.QiPu, new RenderQiPu());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileDing.class, ding);
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnvil.class, anvil);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBellows.class, bellows);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.Anvil), new RenderTE(anvil,new TileAnvil()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.LianQiDing), new RenderTE(ding,new TileDing()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.Bellows), new RenderTE(bellows,new TileBellows()));
		
	}

}
