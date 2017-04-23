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

import com.Jackiecrazi.taoism.client.RenderHalper;

public class RenderNeedle implements IItemRenderer {
	ResourceLocation texture;
	ResourceLocation objModelLocation;
	IModelCustom model;
	public RenderNeedle(){
        texture = new ResourceLocation("taoism", "textures/items/Zhen.png");
        objModelLocation = new ResourceLocation("taoism", "textures/items/Zhen.obj");
        model=AdvancedModelLoader.loadModel(objModelLocation);
}
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (helper == IItemRenderer.ItemRendererHelper.BLOCK_3D||helper==ItemRendererHelper.ENTITY_BOBBING||helper==ItemRendererHelper.ENTITY_ROTATION) return false;
	    return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderPlayer rend;
		Render render;
		GL11.glPushMatrix();
		AbstractClientPlayer equippingPlayer;
		
		if(type==IItemRenderer.ItemRenderType.INVENTORY){
			GL11.glScalef(0.1F, 0.1F, 0.1F);
			GL11.glTranslated(0.5, -1, -1.0);
			GL11.glRotatef(30F, 0, 0F, 0);
		}
		else if(type==IItemRenderer.ItemRenderType.ENTITY){
			GL11.glTranslatef(0F, 0F, 0F);
			GL11.glScalef(0.05F, 0.05F, 0.05F);
			GL11.glRotatef(0F, 0, 0F, 0);
		}
		else if(type==IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON){
			
			equippingPlayer = (AbstractClientPlayer) data[1];
			render = RenderManager.instance
					.getEntityRenderObject(equippingPlayer);
			rend = (RenderPlayer) render;
			GL11.glPushMatrix();
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			//GL11.glRotated(-equippingPlayer.attackTime*3, 0, 0, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-0.5, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			if(!equippingPlayer.isInvisible())
			rend.renderFirstPersonArm(equippingPlayer);
			GL11.glPopMatrix();
			
		GL11.glTranslatef(2F, 0.8F, -0.4F);
		GL11.glScalef(0.1F, 0.1F, 0.1F);
		GL11.glRotatef(90F, 0, 0F, 0);
		//GL11.glRotated(equippingPlayer.attackTime * 6, 1, 0.26, 0);
		}
		else{
			GL11.glTranslatef(0F, 0.8F, 0.9F);
			GL11.glScalef(0.1F, 0.1F, 0.1F);
			GL11.glRotatef(90F, 0, 0F, 0);
			
			}
		RenderHalper.bindTexture("taoism:textures/items/Zhen.png");
		model.renderAll();
		
		GL11.glPopMatrix();
	}

}
