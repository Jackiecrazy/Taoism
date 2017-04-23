package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelJian;
import com.Jackiecrazi.taoism.client.render.CustomHandModel;

public class RenderJian implements IItemRenderer {
	public static ModelJian model;
	static CustomHandModel chm;
	
	public RenderJian() {
		model=new ModelJian();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
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
		RenderHalper.bindTexture("taoism:textures/items/weapons/xuantie/jian.png");
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
		else if(type==ItemRenderType.INVENTORY){
			GL11.glTranslatef(9.5F, -2F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(8F, 8F, 8F);
			
		}
		else{
			//e=(Entity)data[1];
			GL11.glTranslatef(9.5F, -2F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(8F, 8F, 10F);
			
		}
		
	model.render(item);
		GL11.glPopMatrix();
	}
}
