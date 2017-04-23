package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.model.AdvancedModelLoader;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelChui;

public class RenderHammer implements IItemRenderer {
	static ModelChui model;

	public RenderHammer() {
		model = new ModelChui();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (helper == IItemRenderer.ItemRendererHelper.BLOCK_3D)
			return false;
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		AbstractClientPlayer equippingPlayer;
		Entity e = null;
		double scale;
		RenderPlayer rend;
		Render render;
		GL11.glPushMatrix();
		String mat = "wood";
		if (item.getItem().getRarity(item) == EnumRarity.rare)
			mat = "jade";
		if (item.getItem().getRarity(item) == EnumRarity.uncommon)
			mat = "iron";
		String texture = "taoism:textures/items/weapons/" + mat + ".png";
		RenderHalper
				.bindTexture("taoism:textures/items/weapons/xuantie/chui.png");
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
			//GL11.glScaled(9, 9, 9);
			//GL11.glRotated(1978, 0, 0, 0);
			//System.out.println(equippingPlayer.ticksExisted);
			//GL11.glTranslated(3, 0, 0);
			GL11.glScaled(scale, scale, scale);
			GL11.glRotated(160, 0, 0, 1);
			GL11.glTranslated(-0.3, -2.5, 0);
			model.setRotationAngles(0, 0, 0, 0, 0, 0, e);
		}
		else if(type==ItemRenderType.EQUIPPED){
			e=(Entity)data[1];
			//GL11.glScalef(0.7F, 0.7F, 0.7F);
			//GL11.glRotated(14230, 1, 0.27, 0);
			//System.out.println(e.ticksExisted*5);
			//GL11.glTranslatef(3F, -5F, 0F);
			GL11.glScaled(scale, scale, scale);
			GL11.glRotated(160, 0, 0, 1);
			GL11.glTranslated(-0.3, -2.5, 0);
		}
		else if(type==ItemRenderType.INVENTORY){
			//GL11.glTranslatef(9.5F, -2F, 0F);
			GL11.glRotated(45, 0, 0, 1);
			GL11.glRotatef(30F, 0F, 0F, -1f);
			GL11.glScalef(1F, 1F, 1F);
			
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
