package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoArrow;
import com.jackiecrazi.taoism.potions.TaoPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class WeaponPerk {
	public static final HashMap<String, WeaponPerk> REGISTERED = new HashMap<String, WeaponPerk>();
	public static final ArrayList<HandlePerk> REGISTEREDHANDLES = new ArrayList<HandlePerk>();

	@Nullable
	public static WeaponPerk get(String s) {
		return REGISTERED.get(s);
	}

	public static final WeaponPerk BLEED = new WeaponPerk("bleed") {

		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			if (uke.getActivePotionEffect(TaoPotions.Bleed) == null) uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, 80, 0));
			else uke.addPotionEffect(new PotionEffect(TaoPotions.Bleed, uke.getActivePotionEffect(TaoPotions.Bleed).getDuration(), uke.getActivePotionEffect(TaoPotions.Bleed).getAmplifier() + 1));
			return 1;
		}

	};
	public static final WeaponPerk DAZE = new WeaponPerk("daze") {

		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			uke.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 0));
			return 1;
		}

	};
	public static final WeaponPerk HOOK = new WeaponPerk("hook") {

		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f)
			uke.knockBack(seme, 1F, (double) -MathHelper.sin(seme.rotationYaw * 0.017453292F), (double) (MathHelper.cos(seme.rotationYaw * 0.017453292F)));
			return 1;
		}

	};
	public static final WeaponPerk PARRY = new WeaponPerk("parry") {

		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			//what do?
            return 1;
		}

	};
	public static final WeaponPerk AXE = new WeaponPerk("axe");
	public static final WeaponPerk SHOVEL = new WeaponPerk("spade");
	public static final WeaponPerk PICK = new WeaponPerk("pick");
	public static final WeaponPerk HOE = new WeaponPerk("hoe");
	public static final WeaponPerk SCYTHE = new WeaponPerk("scythe");
	public static final WeaponPerk CONTROL = new WeaponPerk("control");
	public static final WeaponPerk FEAR = new WeaponPerk("fear") {
		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f) {
                uke.setRevengeTarget(null);
            }
			return 1;
		}
	};
	public static final WeaponPerk CLEAVE = new WeaponPerk("cleave") {
		@Override
		public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
			if(!(seme instanceof EntityPlayer) ||((EntityPlayer)seme).getCooledAttackStrength(0)>0.9f){
				//damage armor
				for(ItemStack is:uke.getArmorInventoryList()){
					if(!is.isEmpty()){
						is.damageItem(1, uke);
					}
				}
			}
			return 1;
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

	public int hitEntity(EntityLivingBase seme, EntityLivingBase uke, ItemStack i, int level) {
		return 1;
	}

	public void arrowConstruct(EntityTaoArrow arrow, EntityLivingBase seme, int level){

	}

    public void arrowTickFly(EntityTaoArrow arrow, EntityLivingBase seme, int level){

    }

    public void arrowTickLand(EntityTaoArrow arrow, EntityLivingBase seme, int level, int ticksInGround){

    }

	public void onHitBlock(EntityTaoArrow arrow, EntityLivingBase seme, int level, IBlockState blockState, BlockPos pos){

	}

	public static class HandlePerk extends WeaponPerk {

		public HandlePerk(String name) {
			super(name);
			REGISTEREDHANDLES.add(this);
		}

	}
}
