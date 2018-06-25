package com.jackiecrazi.taoism;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class TaoPotions extends Potion {

	public static Potion Hide;
	public static Potion Bleed;
	
	public TaoPotions( boolean isBad, int colour) {
		super(isBad, colour);
	}

	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		super.renderInventoryEffect(x, y, effect, mc);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase l, int amplifier) {
		if (this==Bleed) {
			l.attackEntityFrom(DamageSource.GENERIC, amplifier);
			if(amplifier>=5){
				l.attackEntityFrom(DamageSource.GENERIC, l.getMaxHealth()/10);
			}
		}
		if (this==Hide);
		
	}

	public static void brew() {
		//TODO register
	}
}
