package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderSpear implements IItemRenderer {
	ResourceLocation textureHandle;
	ResourceLocation textureTip;
	ResourceLocation objModelLocation;
	IModelCustom model;

	public RenderSpear(String texLocHandle, String texLocTip) {
		textureHandle = new ResourceLocation(texLocHandle);
		textureTip = new ResourceLocation(texLocTip);
		objModelLocation = new ResourceLocation("taoism",
				"textures/items/weapons/qiangtest.obj");
		model = AdvancedModelLoader.loadModel(objModelLocation);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (helper == IItemRenderer.ItemRendererHelper.BLOCK_3D||helper==ItemRendererHelper.ENTITY_BOBBING||helper==ItemRendererHelper.ENTITY_ROTATION)
			return false;
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		AbstractClientPlayer equippingPlayer;
		double scale;
		RenderPlayer rend;
		Render render;
		GL11.glPushMatrix();

		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			GL11.glScalef(2F, 0.2F, 0.15F);
			GL11.glTranslated(0, -1, 0);
			GL11.glRotatef(30F, 0, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.ENTITY) {
			GL11.glTranslatef(0F, -0.2F, 0F);
			GL11.glScalef(0.4F, 0.4F, 0.5F);
			GL11.glRotatef(0F, 0, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {

			equippingPlayer = (AbstractClientPlayer) data[1];
			// render right arm
			scale = 8.0 / 4.0;
			render = RenderManager.instance
					.getEntityRenderObject(equippingPlayer);
			rend = (RenderPlayer) render;
			scale = 8.0 / 4.0;
			if(NeedyLittleThings.isUltimating(equippingPlayer)){
				GL11.glPushMatrix();
				RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
						.getResourceDomain()
						+ ":"
						+ equippingPlayer.getLocationSkin().getResourcePath());
				RenderHelper.enableStandardItemLighting();
				GL11.glTranslated(-1, 0.75, -0);
				GL11.glRotated(45, 0, 0.5, 0);
				GL11.glRotated(45, 1, 0, 0);
				GL11.glRotated(10, 0, 0, -1);
				if(!equippingPlayer.isInvisible())
				rend.renderFirstPersonArm(equippingPlayer);
				GL11.glPopMatrix();
				
				GL11.glRotated(280, 0, 0, 1);
				GL11.glRotated(-60, 1, 0, 0);
				GL11.glRotated(-75, 0, 1, 0);
				GL11.glScaled(scale, scale, -scale);
				GL11.glTranslated(-0.0, -0.85, -0.51);

				GL11.glScaled(-1, 1, 1);

				GL11.glTranslatef(0F, 0F, 0F);
				GL11.glRotated(Math.sin(NeedyLittleThings.rad(equippingPlayer.ticksExisted*160)),0, 0, 0);
				GL11.glScalef(0.3F, 0.3F, 0.3F);
				GL11.glRotatef(90F, 0, 0F, 0);// Not good for stick, but not bad
												// for polearms
				
			}
			else if (equippingPlayer.attackTime==0) {
				if (equippingPlayer.isUsingItem()) {

					GL11.glRotated(3*Math.sin(equippingPlayer.ticksExisted), 0,
							0, 1);
				}
				GL11.glPushMatrix();
				RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
						.getResourceDomain()
						+ ":"
						+ equippingPlayer.getLocationSkin().getResourcePath());
				RenderHelper.enableStandardItemLighting();
				GL11.glTranslated(-1, 0.75, -0);
				GL11.glRotated(45, 0, 0.5, 0);
				GL11.glRotated(45, 1, 0, 0);
				GL11.glRotated(10, 0, 0, -1);
				if(!equippingPlayer.isInvisible())
				rend.renderFirstPersonArm(equippingPlayer);
				GL11.glPopMatrix();
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
			} else {
				//System.out.println(equippingPlayer.attackTime);
				int animType=AnimationStalker.getThis(equippingPlayer).getType();
				equippingPlayer = (AbstractClientPlayer) data[1];
				// render right arm
				scale = 8.0 / 4.0;
				render = RenderManager.instance
						.getEntityRenderObject(equippingPlayer);
				rend = (RenderPlayer) render;
				scale = 8.0 / 4.0;
				if (equippingPlayer.isUsingItem()) {

					GL11.glRotated(Math.sin(equippingPlayer.ticksExisted), 0,
							0, 1);
				}
				if (animType==1){
					//TODO slam back
					GL11.glPushMatrix();
				RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
						.getResourceDomain()
						+ ":"
						+ equippingPlayer.getLocationSkin().getResourcePath());
				RenderHelper.enableStandardItemLighting();
				GL11.glTranslated(-1, 0.75, -0);
				GL11.glRotated(45, 0, 0.5, 0);
				GL11.glRotated(45, 1, 0, 0);
				GL11.glRotated(10, 0, 0, -1);
				GL11.glRotated(-equippingPlayer.attackTime*9, 0, 0, 1);
				GL11.glTranslated(0, -equippingPlayer.attackTime/4, 0);
				if(!equippingPlayer.isInvisible())
				rend.renderFirstPersonArm(equippingPlayer);
				GL11.glPopMatrix();
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
				GL11.glRotated(equippingPlayer.attackTime*9, 1, 0, 0);
				GL11.glTranslated(0, -equippingPlayer.attackTime/4, 0);
				}
				else{
					GL11.glPushMatrix();
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());
					RenderHelper.enableStandardItemLighting();
					GL11.glTranslated(-1, 0.75, -0);
					GL11.glRotated(45, 0, 0.5, 0);
					GL11.glRotated(45, 1, 0, 0);
					GL11.glRotated(10, 0, 0, -1);
					if(!equippingPlayer.isInvisible())
					rend.renderFirstPersonArm(equippingPlayer);
					GL11.glPopMatrix();
					
					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(scale, scale, -scale);
					GL11.glTranslated(-0.0, -0.85, -0.51);

					GL11.glScaled(-1, 1, 1);

					GL11.glTranslatef(0F, 0F, 0F);
					GL11.glTranslated(0, -Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime*19)-5)+1, 0);//TODO get a better trig graph will ya?
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					GL11.glRotatef(90F, 0, 0F, 0);// Not good for stick, but not bad
													// for polearms
					
				}
			}
		} else {
			GL11.glTranslatef(0.3F, 1.3F, 0.7F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(90F, 0, 0F, 0);
		}

		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(textureHandle);
		model.renderPart("Shaft");
		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(textureTip);
		model.renderPart("Head_Cone.001");
		// System.out.println(model.toString());
		// model.renderAll();
		GL11.glPopMatrix();
	}

}
