package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelSword;

public class RenderSword implements IItemRenderer {
	public static ModelSword model;
	public RenderSword() {
		model=new ModelSword();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		boolean ret=false;
		if(type!=ItemRenderType.FIRST_PERSON_MAP&&type!=ItemRenderType.INVENTORY)ret=true;
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
		RenderHalper.bindTexture("taoism:textures/items/weapons/xuantie/sword.png");
		scale=2d;
		//GL11.glScaled(scale, scale, scale);
		if (type == IItemRenderer.ItemRenderType.ENTITY) {
			e=(Entity) data[1];
			//GL11.glTranslatef(0F, -0.2F, 0F);
			GL11.glScalef(2F, 2F, 2F);
			GL11.glRotatef(90F, 1, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			scale=1;
			e=(Entity)data[1];
			equippingPlayer = (AbstractClientPlayer) data[1];
			GL11.glScaled(0.8, 0.8, 0.8);
			GL11.glTranslated(0.2, 0.2, 0);
			model.setRotationAngles(0, 0, 0, 0, 0, 0, e);
		}
		else if(type==ItemRenderType.EQUIPPED){
			e=(Entity)data[1];
			//GL11.glTranslatef(0.3F, 0F, 0.7F);
			//GL11.glScalef(0.3F, 0.3F, 0.3F);
			//GL11.glRotatef(90F, 0, 0F, 0);
		}
		else{
			//e=(Entity)data[1];
			GL11.glTranslatef(6.5F, -1F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(10F, 10F, 20F);
			
		}
		
	model.render(item);
		GL11.glPopMatrix();
	}

}
