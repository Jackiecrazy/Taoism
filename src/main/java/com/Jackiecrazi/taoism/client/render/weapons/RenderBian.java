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
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelWhipNew;
import com.Jackiecrazi.taoism.common.items.ItemWeaponPart;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

public class RenderBian implements IItemRenderer {
	public static ModelWhipNew model;
	public RenderBian() {
		model=new ModelWhipNew();
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
		RenderHalper.bindTexture("taoism:textures/items/weapons/xuantie/whip.png");
		scale=2d;
		//GL11.glScaled(scale, scale, scale);
		if (type == IItemRenderer.ItemRenderType.ENTITY) {
			e=(Entity) data[1];
			//GL11.glTranslatef(0F, -0.2F, 0F);
			GL11.glScalef(1.2F, 1.2F, 1.2F);
			GL11.glRotatef(90F, 1, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			scale=1;
			e=(Entity)data[1];
			equippingPlayer = (AbstractClientPlayer) data[1];
			GL11.glScaled(0.6, 0.6, 0.6);
			GL11.glTranslated(1, 0, 0);
			model.setRotationAngles(0, 0, 0, 0, 0, 0, e);
		}
		else if(type==ItemRenderType.EQUIPPED){
			e=(Entity)data[1];
			GL11.glScalef(0.7F, 0.7F, 0.7F);
			//GL11.glRotatef(90F, 0, 0F, 0);
			GL11.glTranslatef(0.3F, 0F, 0F);
		}
		else{
			//e=(Entity)data[1];
			GL11.glTranslatef(9.5F, -2F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(8F, 8F, 10F);
			
		}
		
		
		HashMap<String, HashMap<String, ItemStack>> spear=((GenericTaoistWeapon)item.getItem()).getPartsOfParts(item);
		try{
			ItemStack knotstck=spear.get(StaticRefs.KNOTS).get("base");
			ItemWeaponPart knot=((ItemWeaponPart)spear.get(StaticRefs.KNOTS).get("base").getItem());
			ItemStack guardstack=spear.get(StaticRefs.GUARD).get("base");
			ItemWeaponPart guard=((ItemWeaponPart)spear.get(StaticRefs.GUARD).get("base").getItem());
			ItemStack handlestack=spear.get(StaticRefs.HANDLE).get("base");
			ItemWeaponPart handle=((ItemWeaponPart)spear.get(StaticRefs.HANDLE).get("base").getItem());
			ItemStack pommelstack=spear.get(StaticRefs.POMMEL).get("base");
			ItemWeaponPart pommel=((ItemWeaponPart)spear.get(StaticRefs.POMMEL).get("base").getItem());
			ItemStack bodystack=spear.get(StaticRefs.SHAFT).get("base");
			ItemWeaponPart body=((ItemWeaponPart)spear.get(StaticRefs.SHAFT).get("base").getItem());
			
		model.render(item,
				body.getParts(bodystack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				knot.getParts(knotstck)[0].getUnlocalizedName().substring(14).toLowerCase(),
				pommel.getParts(pommelstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				handle.getParts(handlestack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				guard.getParts(guardstack)[0].getUnlocalizedName().substring(14).toLowerCase());
		}
		catch(Exception npe){
			//npe.printStackTrace();
			String derp="xuantie/whip";
			model.render(item, derp, derp, derp, derp, derp);
		}
		
		
	//model.render(item);
		GL11.glPopMatrix();
	}

}
