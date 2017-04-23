package com.Jackiecrazi.taoism.client.render.weapons;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.weapons.fu.ModelFuOne;
import com.Jackiecrazi.taoism.common.items.ItemWeaponPart;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

public class RenderFuOne implements IItemRenderer {
	public static ModelFuOne model;
	public RenderFuOne() {
		model=new ModelFuOne();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		boolean ret=false;
		if(type!=ItemRenderType.FIRST_PERSON_MAP)ret=true;
		return ret;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		AbstractClientPlayer equippingPlayer;
		Entity e=null;
		double scale;
		RenderPlayer rend;
		Render render;
		GL11.glPushMatrix();
		String mat="wood";
		if(item.getItem().getRarity(item)==EnumRarity.rare)mat="jade";
		if(item.getItem().getRarity(item)==EnumRarity.uncommon)mat="iron";
			String texture = "taoism:textures/items/weapons/"+mat+".png";
		scale=2d;
		RenderHalper.bindMatTex("xuantie/fu1");
		//GL11.glScaled(scale, scale, scale);
		if (type == IItemRenderer.ItemRenderType.ENTITY) {
			e=(Entity) data[1];
			//GL11.glTranslatef(0F, -0.2F, 0F);
			GL11.glScalef(1.5F, 1.5F, 1.5F);
			GL11.glRotatef(90F, 1, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			scale=0.7;
			e=(Entity)data[1];
			equippingPlayer = (AbstractClientPlayer) data[1];
			GL11.glScaled(scale, scale, scale);
			GL11.glRotated(180, 0, 0, 1);
			GL11.glTranslated(-0.3, -2.5, 0);
			model.setRotationAngles(0, 0, 0, 0, 0, 0, e);
		}
		else if(type==ItemRenderType.EQUIPPED){
			e=(Entity)data[1];
			scale=0.8;
			GL11.glScaled(scale, scale, scale);
			GL11.glRotated(180, 0, 0, 1);
			GL11.glTranslated(-0.3, -2.5, 0);
			//GL11.glTranslatef(0.3F, 0F, 0.7F);
			//GL11.glScalef(0.3F, 0.3F, 0.3F);
			//GL11.glRotatef(90F, 0, 0F, 0);
		}
		else{
			//e=(Entity)data[1];
			GL11.glTranslatef(7.5F, 0F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(7F, 7F, 7F);
			
		}
		
		HashMap<String, HashMap<String, ItemStack>> spear=((GenericTaoistWeapon)item.getItem()).getPartsOfParts(item);
		try{
			ItemStack guardstack=spear
					.get(StaticRefs.GUARD)
					.get("base");
			ItemWeaponPart guard=((ItemWeaponPart)spear.get(StaticRefs.GUARD).get("base").getItem());
			ItemStack bladestack=spear.get(StaticRefs.DAOBLADE).get("base");
			ItemWeaponPart blade=((ItemWeaponPart)spear.get(StaticRefs.DAOBLADE).get("base").getItem());
			ItemStack pommelstack=spear.get(StaticRefs.POMMEL).get("base");
			ItemWeaponPart pommel=((ItemWeaponPart)spear.get(StaticRefs.POMMEL).get("base").getItem());
			ItemStack handlestack=spear.get(StaticRefs.HANDLE).get("base");
			ItemWeaponPart handle=((ItemWeaponPart)spear.get(StaticRefs.HANDLE).get("base").getItem());
			ItemStack loopstack=spear.get(StaticRefs.LOOP).get("base");
			ItemWeaponPart loop=((ItemWeaponPart)spear.get(StaticRefs.LOOP).get("base").getItem());
			ItemStack edgestack=spear.get(StaticRefs.LOOP).get("base");
			ItemWeaponPart edge=((ItemWeaponPart)spear.get(StaticRefs.LOOP).get("base").getItem());
			
		model.render(item/*,
				edge.getParts(edgestack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				blade.getParts(bladestack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				
				loop.getParts(loopstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				handle.getParts(handlestack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				pommel.getParts(pommelstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				guard.getParts(guardstack)[0].getUnlocalizedName().substring(14).toLowerCase()*/);
		}
		catch(Exception npe){
			//npe.printStackTrace();
			String derp="xuantie/fu1";
			model.render(item/*, derp, derp, derp, derp, derp, derp*/);
		}
		GL11.glPopMatrix();
	}

}
