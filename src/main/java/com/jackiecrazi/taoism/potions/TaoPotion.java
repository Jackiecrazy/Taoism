package com.jackiecrazi.taoism.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class TaoPotion extends Potion {

	public TaoPotion(boolean isBad, int colour) {
		super(isBad, colour);
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldRender(PotionEffect effect) { return false; }

	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInvText(PotionEffect effect)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldRenderHUD(PotionEffect effect)
	{
		return false;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 1;
	}

	@Override
	public void performEffect(EntityLivingBase l, int amplifier) {
		if (this == TaoPotion.BLEED) {
			l.attackEntityFrom(DamageSource.WITHER, amplifier);
//			if (amplifier >= 5) {
//				l.attackEntityFrom(DamageSource.GENERIC, l.getMaxHealth() / 10);
//				l.removePotionEffect(TaoPotions.Bleed);
//			}//essentially useless so archived for now
		}
	}

	public static Potion HIDE = new TaoPotion(true, 0).setRegistryName("hide").setPotionName("hiding");
	public static Potion BLEED = new TaoPotion(true, new Color(187, 10, 30).getRGB()).setRegistryName("bleed").setPotionName("bleed");
	public static Potion ARMORBREAK =new TaoPotion(true, new Color(255, 233, 54).getRGB()).setRegistryName("armorbreak").setPotionName("armorbreak")
			.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR,"CC5AF142-2BD2-4215-B636-2605AED11727",-2,0);

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Potion> event) {
		event.getRegistry().register(BLEED);
		event.getRegistry().register(HIDE);
		event.getRegistry().register(ARMORBREAK);
	}
}
