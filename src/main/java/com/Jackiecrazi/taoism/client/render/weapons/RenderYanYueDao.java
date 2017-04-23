package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelYanYueDao;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;

public class RenderYanYueDao implements IItemRenderer {
	public static ModelYanYueDao model;

	public RenderYanYueDao() {
		model = new ModelYanYueDao();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (helper == IItemRenderer.ItemRendererHelper.BLOCK_3D
				|| helper == ItemRendererHelper.ENTITY_BOBBING
				|| helper == ItemRendererHelper.ENTITY_ROTATION)
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

		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			GL11.glScaled(0.65, 0.65, 0.65);
			GL11.glTranslated(0, -1, 0);
			GL11.glRotatef(150F, 0, 0F, 0);
			GL11.glTranslated(-0.3, -1.9, -0.2);
		} else if (type == IItemRenderer.ItemRenderType.ENTITY) {
			GL11.glTranslatef(0F, -0.2F, 0F);
			GL11.glScalef(2F, 2F, 2F);
			GL11.glRotatef(90F, 0, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {

			equippingPlayer = (AbstractClientPlayer) data[1];
			// render right arm
			scale = 8.0 / 4.0;
			render = RenderManager.instance
					.getEntityRenderObject(equippingPlayer);
			rend = (RenderPlayer) render;
			scale = 8.0 / 4.0;
			boolean anim = AnimationStalker.getThis(equippingPlayer).isActive();
			int animType = AnimationStalker.getThis(equippingPlayer).getType();
			equippingPlayer = (AbstractClientPlayer) data[1];
			// render right arm
			scale = 8.0 / 4.0;
			render = RenderManager.instance
					.getEntityRenderObject(equippingPlayer);
			rend = (RenderPlayer) render;
			scale = 8.0 / 4.0;
			if (equippingPlayer.isUsingItem() && !anim) {

				GL11.glRotated(Math.sin(equippingPlayer.ticksExisted), 0, 0, 1);
			}
			if (animType == 1 && anim) {
				// TODO slam back
				GL11.glRotated(280, 0, 0, 1);
				GL11.glRotated(-60, 1, 0, 0);
				GL11.glRotated(-75, 0, 1, 0);
				GL11.glScaled(scale, scale, -scale);
				GL11.glTranslated(-0.0, -0.85, -0.51);

				// GL11.glPopMatrix();

				GL11.glScaled(-1, 1, 1);

				GL11.glTranslatef(0F, 0F, 0F);
				GL11.glScalef(0.3F, 0.3F, 0.3F);
				GL11.glRotatef(90F, 0, 0F, 0);// Not good for stick, but not bad
												// for polearms
				GL11.glRotated(equippingPlayer.attackTime * 9, 1, 0, 0);
				GL11.glTranslated(0, -equippingPlayer.attackTime / 4, 0);
			} else {

				// scale rotate translate
				// GL11.glScalef(0.3F, 0.3F, 0.3F);
				GL11.glRotatef(90F, 0, 0F, 0);
				
				GL11.glTranslated(0, -2, -0.7);
				double x=(-1.5)*Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime * 12)) + 1;
				double y=(99)*Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime*19))-5;
				//GL11.glTranslated(-x/2, x, 0);
				GL11.glRotated(y, 0, 0.3, 1);
				// Not good for stick, but not bad
				// for polearms

			}
		} else if (type == ItemRenderType.EQUIPPED) {
			// GL11.glScalef(0.3F, 0.3F, 0.3F);
			e = (Entity) data[1];
			GL11.glRotatef(524, 0.3f, 0F, 1f);
			GL11.glRotatef(897, 0f, 1F, 0f);
			GL11.glRotatef(1376, 0f, 0F, 1f);
			//System.out.println(e.ticksExisted);
			// GL11.glRotated(20, 0, 0, 1);
			GL11.glTranslatef(1F, -3F, -0.7F);

		} else {
			// GL11.glScalef(0.3F, 0.3F, 0.3F);

			GL11.glRotatef(200F, 0, 0F, 0);
			GL11.glRotated(20, 0, 0, 1);
			GL11.glTranslatef(0.3F, 1.3F, 0.7F);

		}

		RenderHalper.bindTexture("taoism:textures/items/weapons/xuantie/yanyudao.png");
		model.render(item);
		// System.out.println(model.toString());
		// model.renderAll();
		GL11.glPopMatrix();
	}

}
