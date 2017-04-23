package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;

import cpw.mods.fml.client.FMLClientHandler;
/*
	 * This is more of a reference file than anything else.
	 */
public class RenderPolearms implements IItemRenderer {
	
	ResourceLocation texture;
	ResourceLocation objModelLocation;
	IModelCustom model;
	public RenderPolearms(){
		
	}
	public RenderPolearms(String texLoc){
        texture = new ResourceLocation(texLoc);
        objModelLocation = new ResourceLocation("taoism", "textures/items/weapons/Gun.obj");
        model=AdvancedModelLoader.loadModel(objModelLocation);
}
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (helper == IItemRenderer.ItemRendererHelper.BLOCK_3D) return false;
	    return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		// TODO Auto-generated method stub
		AbstractClientPlayer equippingPlayer;
		double scale;
		GL11.glPushMatrix();
		
		//GL11.glTranslatef(0, 0, -3);
		if(type==IItemRenderer.ItemRenderType.INVENTORY){
			GL11.glScalef(0.1F, 0.1F, 0.1F);
			GL11.glTranslated(3, -1, 0);
			GL11.glRotatef(30F, 0, 0F, 0);
		}
		else if(type==IItemRenderer.ItemRenderType.ENTITY){
			GL11.glTranslatef(0F, -1.9F, -1F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(0F, 0, 0F, 0);
		}
		else if(type==IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON){
			
			equippingPlayer = (AbstractClientPlayer) data[1];

			scale = 8.0/4.0;
			if(equippingPlayer.isUsingItem())
			{
				
				GL11.glRotated(20, 0, 0, 1);
				GL11.glTranslated(.5,-.5,0);
			}

			RenderHalper.bindTexture(equippingPlayer.getLocationSkin().getResourceDomain()+":"+equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(scale,scale,-scale);
			GL11.glTranslated(-0.0,-0.85,-0.51);
			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1,1,1);

			GL11.glTranslatef(0F, 2.3F, -0.889F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(90F, 0, 0F, 0);//Not good for stick, but not bad for polearms
		}
		else{
		GL11.glTranslatef(0F, 2.3F, -0.889F);
		GL11.glScalef(0.3F, 0.3F, 0.3F);
		GL11.glRotatef(90F, 0, 0F, 0);
		}
		
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
model.renderAll();
GL11.glPopMatrix();
	}

}
