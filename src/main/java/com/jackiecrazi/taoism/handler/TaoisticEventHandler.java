package com.jackiecrazi.taoism.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;

public class TaoisticEventHandler {
	@SubscribeEvent
	public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e){
		//System.out.println("hi");
		EntityPlayer p=e.getEntityPlayer();
		ItemStack i=p.getHeldItem(EnumHand.MAIN_HAND);
		if(!i.isEmpty()){
			//System.out.println("nonnull");
			if(i.getItem() instanceof ICustomRange){
				//System.out.println("range!");
				ICustomRange icr=(ICustomRange) i.getItem();
				
				EntityLivingBase elb=NeedyLittleThings.raytraceEntities(p.world, p, icr.getReach(p, i));
				if(elb!=null){
					//System.out.println("sending packet!");
					Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(),true));
				}
			}
		}
	}
	
	public static void pleasedontkillme(AttackEntityEvent e){
		//System.out.println("hi");
		EntityPlayer p=e.getEntityPlayer();
		ItemStack i=p.getHeldItem(EnumHand.MAIN_HAND);
		if(!i.isEmpty()){
			//System.out.println("nonnull");
			if(i.getItem() instanceof ICustomRange){
				//System.out.println("range!");
				ICustomRange icr=(ICustomRange) i.getItem();
				
				if(p.getDistanceSq(e.getTarget())>icr.getReach(p, i)*icr.getReach(p, i))e.setCanceled(true);
			}
		}
	}
}
