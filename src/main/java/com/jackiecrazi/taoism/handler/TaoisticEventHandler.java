package com.jackiecrazi.taoism.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MaterialWrapper;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.TaoBow;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;

public class TaoisticEventHandler {
	@SubscribeEvent
	public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e) {
		//System.out.println("hi");
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
		if (!i.isEmpty()) {
			//System.out.println("nonnull");
			if (i.getItem() instanceof ICustomRange) {
				//System.out.println("range!");
				ICustomRange icr = (ICustomRange) i.getItem();

				EntityLivingBase elb = NeedyLittleThings.raytraceEntities(p.world, p, icr.getReach(p, i));
				if (elb != null) {
					//System.out.println("sending packet!");
					Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
				}
			}
		}
	}

	@SubscribeEvent
	public static void pleasekillmeoff(PlayerInteractEvent.RightClickItem e) {
		//System.out.println("hi");
		EntityPlayer p = e.getEntityPlayer();
		if (e.getItemStack().equals(e.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND))) return;
		ItemStack i = p.getHeldItem(EnumHand.OFF_HAND);
		if (!i.isEmpty()) {
			//System.out.println("nonnull");
			if (i.getItem() instanceof ICustomRange) {
				//System.out.println("range!");
				ICustomRange icr = (ICustomRange) i.getItem();

				EntityLivingBase elb = NeedyLittleThings.raytraceEntities(p.world, p, icr.getReach(p, i));
				if (elb != null) {
					//System.out.println("sending packet!");
					Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
				}
			}
		}
	}

	@SubscribeEvent
	public static void pleasedontkillme(AttackEntityEvent e) {
		//System.out.println("hi");
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
		if (!i.isEmpty()) {
			//System.out.println("nonnull");
			if (i.getItem() == TaoItems.weap) {
				if (TaoItems.weap.isBroken(i)) e.setCanceled(true);
				return;
			}
			if (i.getItem() instanceof ICustomRange) {
				//System.out.println("range!");
				ICustomRange icr = (ICustomRange) i.getItem();
				if (p.getDistanceSq(e.getTarget()) > icr.getReach(p, i) * icr.getReach(p, i)) e.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void holdItem(EntityJoinWorldEvent ev) {
		Entity e = ev.getEntity();

		if (e instanceof EntityZombie) {
			ItemStack is = ((EntityLivingBase) e).getHeldItem(EnumHand.MAIN_HAND);
			if (is.isEmpty() && ev.getWorld().rand.nextFloat() < 0.1) {
				ItemStack weap = TaoWeapon.createRandomWeapon(null, ev.getWorld().rand);
				((EntityLivingBase) e).setHeldItem(EnumHand.MAIN_HAND, weap);
				((EntityLiving) e).setDropChance(EntityEquipmentSlot.MAINHAND, 0.1f);
			}
		} else if (e instanceof AbstractSkeleton) {

			ItemStack is = ((EntityLivingBase) e).getHeldItem(EnumHand.MAIN_HAND);
			if (is.isEmpty() && ev.getWorld().rand.nextFloat() < 0.1) {
				ItemStack weap = TaoWeapon.createRandomWeapon(null, ev.getWorld().rand);
				if (ev.getWorld().rand.nextBoolean()) weap = TaoBow.createRandomBow(null, ev.getWorld().rand);
				((EntityLivingBase) e).setHeldItem(EnumHand.MAIN_HAND, weap);

				((EntityLiving) e).setDropChance(EntityEquipmentSlot.MAINHAND, 0.1f);
			}
		}
	}

	@SubscribeEvent
	public static void craftPart(AnvilUpdateEvent ev) {
		System.out.println("ay");
		if (!ev.getLeft().isEmpty() && !ev.getRight().isEmpty()) {
			System.out.println("ayy");
			ItemStack blue = ev.getLeft(), mat = ev.getRight();
			//item checks
			if (blue.getItem() != TaoItems.blueprint || MaterialsConfig.findMat(mat) == null) {
				System.out.println("invalid");
				return;
			}
			//material is metal check
			MaterialWrapper m = MaterialsConfig.findMat(mat);
			if (!m.isAnvilWorked()) return;
			WeaponStatWrapper wsw = ((ItemBlueprint) blue.getItem()).toWSW(blue);
			//cost check
			if (wsw.getCost() > m.amount * mat.getCount()) return;
			PartData ret = new PartData(wsw.getClassification(), m.msw.name, wsw.getOrdinal());
			if (ret.isValid()) {
				ev.setMaterialCost(wsw.getCost());
				ev.setCost(wsw.getCost());
				ItemStack output = ret.toStack();
				ev.setOutput(output);
			}
		}
	}
}
