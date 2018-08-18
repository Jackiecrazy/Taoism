package com.jackiecrazi.taoism.api;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

import com.jackiecrazi.taoism.potions.TaoPotions;

public abstract class WeaponPerk {
	public static final HashMap<String, WeaponPerk> REGISTERED = new HashMap<String, WeaponPerk>();
	public static final ArrayList<HandlePerk> REGISTEREDHANDLES = new ArrayList<HandlePerk>();

	@Nullable
	public static WeaponPerk get(String s) {
		return REGISTERED.get(s);
	}

	public static final WeaponPerk BLEED = new WeaponPerk("bleed") {

		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			if (uke.getActivePotionEffect(TaoPotions.Bleed) == null) uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, 80, 0));
			else uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, uke.getActivePotionEffect(TaoPotions.Bleed).getDuration(), uke.getActivePotionEffect(TaoPotions.Bleed).getAmplifier() + 1));
		}

	};
	public static final WeaponPerk DAZE = new WeaponPerk("daze") {

		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			uke.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 0));
		}

	};
	public static final WeaponPerk HOOK = new WeaponPerk("hook") {

		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f)
			uke.knockBack(seme, 1F, (double) -MathHelper.sin(seme.rotationYaw * 0.017453292F), (double) (MathHelper.cos(seme.rotationYaw * 0.017453292F)));
		}

	};
	public static final WeaponPerk PARRY = new WeaponPerk("parry") {

		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
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
	public static final WeaponPerk FEAR = new WeaponPerk("fear") {
		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f){
				uke.setRevengeTarget(null);
			}
			
		}
	};
	public static final WeaponPerk CLEAVE = new WeaponPerk("cleave") {
		@Override
		public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f){
				//damage armor
				for(ItemStack is:uke.getArmorInventoryList()){
					if(!is.isEmpty()){
						is.damageItem(1, uke);
					}
				}
			}
			
		}
	};
	/**
	 * only used as a dummy.
	 */
	@SuppressWarnings("unused")
	private static final HandlePerk DEFAULT = new HandlePerk("default");
	public static final HandlePerk FLEXIBLE = new HandlePerk("flexible");
	public static final HandlePerk HORIZONTAL = new HandlePerk("horizontal");
	public static final HandlePerk SHORT = new HandlePerk("short");
	public static final HandlePerk MEDIUM = new HandlePerk("medium");
	public static final HandlePerk LONG = new HandlePerk("long");
	public final String name;

	public static WeaponPerk lookUp(String s) {
		return REGISTERED.get(s);
	}

	public WeaponPerk(String name) {
		REGISTERED.put(name, this);
		this.name = name;
	}

	public void hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i) {

	}

	public static class HandlePerk extends WeaponPerk {

		public HandlePerk(String name) {
			super(name);
			REGISTEREDHANDLES.add(this);
		}

	}
}
