package com.Jackiecrazi.taoism.common.items.equipment;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ITaoistEquipment;
import com.Jackiecrazi.taoism.common.entity.ModEntities;

public class ItemGongFa extends Item implements IElemental,ITaoistEquipment {
	public static final UUID moduuid=UUID.fromString("f94b0bc7-fc44-4d11-8bd8-7302a1547e41");
	//define types by json, with the 10 elements, speed of xiulian, body stat limit
	//TODO other advancements (BM/TC/B) increase your xiuwei slightly (万法归宗), fixes OPness slightly. JSON EVERYTHING!
	public ItemGongFa() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(Taoism.TabTaoistAccessories);
		this.setTextureName("taoism:ScrollJade");
		this.setUnlocalizedName("gongfa");
		int x=0;
		double inc=0.19*(Math.pow(Math.E, -(Math.pow((x%12000d)-600d,2))/53000d))+0.01;
	}
	
	
	
	@Override
	public int[] getAffinities(ItemStack is) {
		int[] ret=new int[10];
		for(int x=0;x<ret.length;x++){
			ret[x]=getAffinity(is,x);
		}
		return ret;
	}

	@Override
	public void setAffinities(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinity(is,0,metal);
		setAffinity(is,1,wood);
		setAffinity(is,2,water);
		setAffinity(is,3,fire);
		setAffinity(is,4,earth);
	}

	private NBTTagCompound getTag(ItemStack is){
		if(!is.hasTagCompound())is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound();
	}
	
	@Override
	public void setAffinity(ItemStack is, int element, int affinity) {
		getTag(is).setInteger(ModEntities.ALLRES[element].getAttributeUnlocalizedName(), affinity);
	}

	@Override
	public int getAffinity(ItemStack is, int element) {
		return getTag(is).getInteger(ModEntities.ALLRES[element].getAttributeUnlocalizedName());
	}

	@Override
	public void addAffinity(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinities(is,metal+getAffinity(is,0),wood+getAffinity(is,1),water+getAffinity(is,2),fire+getAffinity(is,3),earth+getAffinity(is,4));
	}

	public AttributeModifier modify(EntityPlayer p,ItemStack is,IAttribute ra){
		return new AttributeModifier(moduuid,"gongfa modification",getTag(is).getDouble(ra.getAttributeUnlocalizedName()),0);
	}

	@Override
	public void onEquipped(ItemStack is, EntityPlayer ep) {
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeAllModifiers();
		for(IAttribute a:ModEntities.ALLRES){
			if(ep.getEntityAttribute(a).getModifier(moduuid)==null)
			ep.getEntityAttribute(a).applyModifier(modify(ep,is,a));
		}
		if(ep.getEntityAttribute(ModEntities.LING_MAX).getModifier(moduuid)==null)
		ep.getEntityAttribute(ModEntities.LING_MAX).applyModifier(modify(ep,is,ModEntities.LING_MAX));
		if(ep.getEntityAttribute(ModEntities.LING_SPEED).getModifier(moduuid)==null)
		ep.getEntityAttribute(ModEntities.LING_SPEED).applyModifier(modify(ep,is,ModEntities.LING_SPEED));
		if(ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(moduuid)==null)
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(modify(ep,is,SharedMonsterAttributes.maxHealth));
	}

	public void levelUp(ItemStack is,EntityPlayer p){
		
	}

	@Override
	public void equippedTick(ItemStack is, EntityPlayer ep) {
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean par4) {
		list.add(EnumChatFormatting.GOLD
				+ StatCollector.translateToLocal("suffix.gongfa.1"));
		list.add(EnumChatFormatting.GOLD
				+ StatCollector.translateToLocal("suffix.gongfa.2"));
		for(int i:getAffinities(stack))
		list.add(i+"");
		list.add(modify(player,stack,SharedMonsterAttributes.maxHealth).getAmount()+"");
	}

	@Override
	public void onUnequipped(ItemStack is, EntityPlayer ep) {
		for(IAttribute a:ModEntities.ALLRES){
			ep.getEntityAttribute(a).removeModifier(ep.getEntityAttribute(a).getModifier(moduuid));
		}
		ep.getEntityAttribute(ModEntities.LING_MAX).removeModifier(ep.getEntityAttribute(ModEntities.LING_MAX).getModifier(moduuid));
		ep.getEntityAttribute(ModEntities.LING_SPEED).removeModifier(ep.getEntityAttribute(ModEntities.LING_SPEED).getModifier(moduuid));
		ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(ep.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(moduuid));
	}



	@Override
	public boolean canUnequip(ItemStack is, EntityPlayer ep) {
		return true;
	}



	@Override
	public boolean canEquip(ItemStack is, EntityPlayer ep) {
		return true;
	}



	@Override
	public EnumEquipmentType getType(ItemStack is) {
		return EnumEquipmentType.GONGFA;
	}
	
	public void onCreated(ItemStack is, World w, EntityPlayer p) {
		int max=100;
		for(IAttribute a:ModEntities.ALLRES)
		getTag(is).setDouble(a.getAttributeUnlocalizedName(), w.rand.nextInt(max));
		getTag(is).setDouble(ModEntities.LING_MAX.getAttributeUnlocalizedName(), w.rand.nextInt(max));
		getTag(is).setDouble(ModEntities.LING_SPEED.getAttributeUnlocalizedName(), w.rand.nextInt(max));
		getTag(is).setDouble(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), w.rand.nextInt(max));
		
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack,
			EntityPlayer player, World w, int i,int j, int k, int l, float m, float n, float o) {
			onCreated(stack,w,player);
			return true;
	}
}
