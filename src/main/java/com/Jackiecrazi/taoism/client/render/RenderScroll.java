package com.Jackiecrazi.taoism.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.ModelScroll;

public class RenderScroll implements IItemRenderer {
public static ModelScroll model;
	public RenderScroll() {
		model=new ModelScroll();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		//boolean ret=false;
		if(!(type==ItemRenderType.INVENTORY))return true;
		else
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if(helper==ItemRendererHelper.ENTITY_BOBBING||helper==ItemRendererHelper.ENTITY_ROTATION)
		return false;
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		AbstractClientPlayer equippingPlayer;
		Entity e;
		double scale;
		RenderPlayer rend;
		Render render;
		GL11.glPushMatrix();
		String mat="wood";
		if(item.getItem().getRarity(item)==EnumRarity.rare)mat="jade";
		if(item.getItem().getRarity(item)==EnumRarity.uncommon)mat="iron";
			String texture = "taoism:textures/items/weapons/"+mat+".png";
		RenderHalper.bindTexture(texture);
		scale=2d;
		GL11.glScaled(scale, scale, scale);
		if (type == IItemRenderer.ItemRenderType.ENTITY) {
			e=(Entity) data[1];
			//GL11.glTranslatef(0F, -0.2F, 0F);
			//GL11.glScalef(0.4F, 0.4F, 0.5F);
			GL11.glRotatef(90F, 1, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			scale=1;
			e=(Entity)data[1];
			equippingPlayer = (AbstractClientPlayer) data[1];
			model.setRotationAngles(0, 0, 0, 0, 0, 0, e);
		}
		else {
			e=(Entity)data[1];
			GL11.glTranslatef(0.3F, 0F, 0.7F);
			//GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(90F, 0, 0F, 0);
		}
		
	model.render(item);
		GL11.glPopMatrix();
	}

}
