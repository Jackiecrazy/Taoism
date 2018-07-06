package com.jackiecrazi.taoism.api;

import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

import com.jackiecrazi.taoism.TaoPotions;

public abstract class WeaponPerk {
	public static final HashMap<String, WeaponPerk> REGISTERED = new HashMap<String, WeaponPerk>();
	public static final WeaponPerk BLEED = new WeaponPerk("bleed") {

		@Override
		public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			if (uke.getActivePotionEffect(TaoPotions.Bleed) == null) uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, 80, 0));
			else uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, uke.getActivePotionEffect(TaoPotions.Bleed).getDuration(), uke.getActivePotionEffect(TaoPotions.Bleed).getAmplifier() + 1));
		}

	};
	public static final WeaponPerk DAZE = new WeaponPerk("daze") {

		@Override
		public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			uke.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 0));
		}

	};
	public static final WeaponPerk HOOK = new WeaponPerk("hook") {

		@Override
		public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			uke.knockBack(seme, 1F, (double) -MathHelper.sin(seme.rotationYaw * 0.017453292F), (double) (MathHelper.cos(seme.rotationYaw * 0.017453292F)));
		}

	};
	public static final WeaponPerk PARRY = new WeaponPerk("parry") {

		@Override
		public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			//what do?
		}

	};
	public static final WeaponPerk AXE = new WeaponPerk("axe") {
	};
	public static final WeaponPerk SHOVEL = new WeaponPerk("spade") {
	};
	public static final WeaponPerk PICK = new WeaponPerk("pick") {
	};
	public static final WeaponPerk HOE = new WeaponPerk("hoe") {
	};
	public static final WeaponPerk SCYTHE = new WeaponPerk("scythe") {
	};
	public static final WeaponPerk CONTROL = new WeaponPerk("control") {
	};
	public static final WeaponPerk CHAIN = new WeaponPerk("chain") {
		@Override
		public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			//invoke your own capability
		}
	};
	public static final WeaponPerk FEAR = new WeaponPerk("fear") {
		//????
	};
	public static final WeaponPerk CLEAVE = new WeaponPerk("cleave") {
		//damage armor?
	};
	public static final WeaponPerk HORIZONTAL = new WeaponPerk("horizontal") {
	};
	public static final WeaponPerk SHORT = new WeaponPerk("short") {
	};
	public static final WeaponPerk MEDIUM = new WeaponPerk("medium") {
	};
	public static final WeaponPerk LONG = new WeaponPerk("long") {
	};
	public final String name;
	public static WeaponPerk lookUp(String s){
		return REGISTERED.get(s);
	}

	public WeaponPerk(String name) {
		REGISTERED.put(name, this);
		this.name=name;
	}

	public void onSwing(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {

	}
}
