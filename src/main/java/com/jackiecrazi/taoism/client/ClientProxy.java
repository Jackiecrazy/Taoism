package com.jackiecrazi.taoism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.common.CommonProxy;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

public class ClientProxy extends CommonProxy {
	public void preinit(FMLPreInitializationEvent event) {

		super.preinit(event);
		/*for (WeaponStatWrapper a : WeaponConfig.enabledParts.values()) {
			int damage = WeaponConfig.lookupDamage(a.getName());
			ModelLoader.setCustomModelResourceLocation(TaoItems.parts, damage, new ModelResourceLocation(TaoItems.parts.getRegistryName(), "test=" + damage));
			ModelLoader.setCustomModelResourceLocation(TaoItems.blueprint, damage, new ModelResourceLocation(TaoItems.blueprint.getRegistryName(), "inventory"));

		}*/
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);

	}

	@SubscribeEvent
	public void init(ColorHandlerEvent event) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				//System.out.println(tintIndex);
				TaoWeapon tw = (TaoWeapon) stack.getItem();
				PartData p=tw.getPart(stack, TaoConfigs.weapc.lookupType(tintIndex));
				if (p != null&&MaterialsConfig.findMat(p.getMat())!=null) return MaterialsConfig.findMat(p.getMat()).msw.color.getRGB();
				else return -1;
			}

		}, TaoItems.weap);
		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if(stack.hasTagCompound()){
				return MaterialsConfig.findMat(stack.getTagCompound().getString("dict")).msw.color.getRGB();
				}
				else return -1;
			}

		}, TaoItems.dummy);
		
	}

}
