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
import com.Jackiecrazi.taoism.client.render.entity.mobs.RenderLingjin;
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
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TileAnvil;
import com.Jackiecrazi.taoism.common.block.tile.TileBellows;
import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.entity.EntityLevitatingItem;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityDroppedWeapon;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLiLi;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLingJing;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLuoYu;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityDiJiang;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityShuHu;
import com.Jackiecrazi.taoism.common.items.TaoItems;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TaoRenders {
	public static void drawEntity(){
		RenderingRegistry.registerEntityRenderingHandler(EntitySandbag.class, new RenderSandbag());
		RenderingRegistry.registerEntityRenderingHandler(EntityMuRenZhuang.class, new RenderMuRenZhuang());
		RenderingRegistry.registerEntityRenderingHandler(EntityDroppedWeapon.class, new RenderDroppedWeapon());
		RenderingRegistry.registerEntityRenderingHandler(EntityLevitatingItem.class, new RenderLevitatingItems());
		RenderingRegistry.registerEntityRenderingHandler(EntityLuoYu.class, new RenderYinyu());
		RenderingRegistry.registerEntityRenderingHandler(EntityShuHu.class, new RenderShuhu());
		RenderingRegistry.registerEntityRenderingHandler(EntityDiJiang.class, new RenderDijiang());
		RenderingRegistry.registerEntityRenderingHandler(EntityLiLi.class, new RenderLili());
		RenderingRegistry.registerEntityRenderingHandler(EntityLingJing.class, new RenderLingjin());
		Taoism.logDebug("Successfully drew everything");
	}
	public static void drawItem(){
			MinecraftForgeClient.registerItemRenderer(TaoItems.StickWood, new RenderBigWeapons(1f,0.5f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.StickIron, new RenderBigWeapons(1f,0.3f,90));
			MinecraftForgeClient.registerItemRenderer(TaoItems.SilverNeedle, new RenderNeedle());
			MinecraftForgeClient.registerItemRenderer(TaoItems.QiangWood, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.QiangIron, new RenderBigWeapons(1f,0.3f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.QiangFancy, new RenderBigWeapons(1f));
			
			MinecraftForgeClient.registerItemRenderer(TaoItems.Jian, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Scroll, new RenderScroll());
			MinecraftForgeClient.registerItemRenderer(TaoItems.SwordIron, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.LargeSword, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.LongSword, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Macuahuitl, new RenderBigWeapons(0.75f));

			MinecraftForgeClient.registerItemRenderer(TaoItems.Chu, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Bian, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Cha, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.YanYueDao, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.YueYaChan, new RenderBigWeapons(1f,0.5f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Ge, new RenderBigWeapons(1f,0.3f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Gou, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Yue, new RenderBigWeapons(1,0.5f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Chui, new RenderBigWeapons(0.75f,0.2f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.ChangChui, new RenderBigWeapons(1f));

			MinecraftForgeClient.registerItemRenderer(TaoItems.HuanShouDao, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.ChangDao, new RenderBigWeapons(0.75f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.MoDao, new RenderBigWeapons(0.85f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.ZhiDao, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.DaDao, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.PuDao, new RenderBigWeapons(0.65f));
			
			MinecraftForgeClient.registerItemRenderer(TaoItems.EMeiCi, new RenderDoubleBigWeapons(0.5f,0.5f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.YuanYangYue, new RenderDoubleBigWeapons(0.5f,0.5f));
			
			MinecraftForgeClient.registerItemRenderer(TaoItems.LongDoubleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.ShortDoubleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.LongSingleJi, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Ji4, new RenderBigWeapons(1f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Fu1, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Fu2, new RenderBigWeapons(0.65f));
			MinecraftForgeClient.registerItemRenderer(TaoItems.Fu3, new RenderBigWeapons(0.65f));
			
		
			if(WayofConfig.FancyEnabled){
				MinecraftForgeClient.registerItemRenderer(TaoItems.StickWood, new RenderStick());
				MinecraftForgeClient.registerItemRenderer(TaoItems.StickIron, new RenderStick());
				MinecraftForgeClient.registerItemRenderer(TaoItems.SilverNeedle, new RenderNeedle());
				MinecraftForgeClient.registerItemRenderer(TaoItems.QiangWood, new RenderSpearNew());
				MinecraftForgeClient.registerItemRenderer(TaoItems.QiangIron, new RenderSpearNew());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Jian, new RenderJian());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Scroll, new RenderScroll());
				MinecraftForgeClient.registerItemRenderer(TaoItems.SwordIron, new RenderSword());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Bian, new RenderBian());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Cha, new RenderTrident());
				MinecraftForgeClient.registerItemRenderer(TaoItems.YanYueDao, new RenderYanYueDao());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Chui, new RenderHammer());
				MinecraftForgeClient.registerItemRenderer(TaoItems.HuanShouDao, new RenderDao());
				MinecraftForgeClient.registerItemRenderer(TaoItems.EMeiCi, new RenderEMeiCi());
				MinecraftForgeClient.registerItemRenderer(TaoItems.LongDoubleJi, new RenderJiOne());
				MinecraftForgeClient.registerItemRenderer(TaoItems.ShortDoubleJi, new RenderJiTwo());
				MinecraftForgeClient.registerItemRenderer(TaoItems.LongSingleJi, new RenderJiThree());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Ji4, new RenderJiFour());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Fu1, new RenderFuOne());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Fu2, new RenderFuTwo());
				MinecraftForgeClient.registerItemRenderer(TaoItems.Fu3, new RenderFuThree());
			}
			TileEntitySpecialRenderer ding=new RenderDing(),anvil=new RenderTieZhen(),bellows=new RenderFengxiang();
		MinecraftForgeClient.registerItemRenderer(TaoItems.QiPu, new RenderQiPu());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileDing.class, ding);
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnvil.class, anvil);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBellows.class, bellows);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaoBlocks.Anvil), new RenderTE(anvil,new TileAnvil()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaoBlocks.LianQiDing), new RenderTE(ding,new TileDing()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaoBlocks.Bellows), new RenderTE(bellows,new TileBellows()));
		
	}

}
