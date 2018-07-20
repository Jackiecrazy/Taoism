package com.jackiecrazi.taoism.potions;

import java.awt.Color;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TaoPotions {

	public static Potion Hide = new TaoPotion(true, 0).setRegistryName("hide").setPotionName("hiding");
	public static Potion Bleed = new TaoPotion(true, new Color(187, 10, 30).getRGB()).setRegistryName("bleed").setPotionName("bleed");

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Potion> event) {
		event.getRegistry().register(Bleed);
		event.getRegistry().register(Hide);
	}
}
