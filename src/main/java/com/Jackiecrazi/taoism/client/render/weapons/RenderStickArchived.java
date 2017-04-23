package com.Jackiecrazi.taoism.client.render.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
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
import com.Jackiecrazi.taoism.client.render.CustomHandModel;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderStickArchived implements IItemRenderer {
	ResourceLocation texture;
	ResourceLocation objModelLocation;
	IModelCustom model;
	IModelCustom aura;
	static CustomHandModel chm;

	public RenderStickArchived() {

	}

	public RenderStickArchived(String texLoc) {
		texture = new ResourceLocation(texLoc);
		objModelLocation = new ResourceLocation("taoism",
				"textures/items/weapons/Gun.obj");
		model = AdvancedModelLoader.loadModel(objModelLocation);
		//aura=RenderHalper.bindModel("taoism", "hud/sphere.obj");
		// chm=new CustomHandModel();
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
		// TODO My god.
		Minecraft mc=Minecraft.getMinecraft();
		AbstractClientPlayer equippingPlayer;
		RenderPlayer rend;
		double scale;
		GL11.glPushMatrix();
		Render render;
		
		
		//GL11.glTranslated(0, 4, 0);
		//RenderHalper.renderColoredSphere(1, 1, 1, 1);
		//GL11.glTranslatef(0, 0, -3);
		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			GL11.glScalef(0.7F, 0.1F, 0.1F);
			GL11.glTranslated(3, -1, 5);
			GL11.glTranslated(-2.5, 7, 4);
			GL11.glRotatef(30F, 0, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.ENTITY) {
			GL11.glTranslatef(0F, -1.8F, -1F);
			GL11.glScalef(0.3F, 0.3F, 0.5F);
			GL11.glRotatef(0F, 0, 0F, 0);
		} else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
			equippingPlayer = (AbstractClientPlayer) data[1];
			ScaledResolution res;
			// render right arm
			scale = 8.0 / 4.0;
			render = RenderManager.instance
					.getEntityRenderObject(equippingPlayer);
			rend = (RenderPlayer) render;
			
			/* if (equippingPlayer.isUsingItem()) {
				 RenderHalper.renderColoredSphere(136, 175, 234, (equippingPlayer.getItemInUseDuration()/50f), 0, 0, 0, (equippingPlayer.getItemInUseDuration()/50f));
				 //System.out.println(equippingPlayer.getItemInUseDuration());
				 	}*/
			 
			// TODO the above should reflect charging for an ultimate
			if (equippingPlayer.attackTime!=0||NeedyLittleThings.isUltimating(equippingPlayer)) {
				executeAnimations(item, equippingPlayer);
			} else {
				GL11.glPushMatrix();

				// this is not bad for some two-handed weapons like yue and gou
				// and bian and jian and chui and guai
				// actual stick
				if (!equippingPlayer.isCollidedVertically) {
					//render right arm
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glRotated(-equippingPlayer.attackTime*3, 0, 0, 0);
					GL11.glScaled(2, 2, -2);

					GL11.glTranslated(-0.7, -0.5, -0.7);

					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					
					GL11.glPopMatrix();

					// render left arm
					GL11.glPushMatrix();
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					
					GL11.glRotated(-equippingPlayer.attackTime*3, 0, 0, 0);
					GL11.glScaled(2, 2, -2);

					//GL11.glTranslated(-0.9, -0.85, -0.51);
					GL11.glTranslated(-1.1, -0.85, -0.51);
					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					GL11.glPopMatrix();
					
					// stick
					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(scale, scale, -scale);
					GL11.glTranslated(-0.0, -0.85, -0.51);
					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);

					GL11.glTranslatef(0F, 2.3F, -0.889F);
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					GL11.glRotatef(90F, 0F, 0F, 0);
					GL11.glRotated(-68, 1, 0, 0);
					GL11.glTranslated(-1, -6, 0);
					
				} else if (equippingPlayer.isSneaking()) {
					//right arm
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(2, 2, -2);

					GL11.glTranslated(-0.5, -0.85, -0.51);

					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					GL11.glPopMatrix();

					// render left arm
					GL11.glPushMatrix();
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(2, 2, -2);

					GL11.glTranslated(-1.1, -0.85, -0.51);

					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					GL11.glPopMatrix();
					//stick
					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(scale, scale, -scale);
					GL11.glTranslated(-0.0, -0.85, -0.51);
					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);

					GL11.glTranslatef(-0F,2.7F, -0.889F);
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					GL11.glRotated(5, 0, 0, 0.2);
					GL11.glRotatef(90F, 0, 0F, 0);
					GL11.glRotated(20, 1.4, 0, 0);
					
					GL11.glTranslated(-1, 0, 0);
					GL11.glRotated(5, 0, 0, -0.0001);
					
					/*GL11.glTranslatef(0F, 2.3F, -0.889F);
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					GL11.glRotated(5, 0, 0, 0.2);
					GL11.glRotated(20-20-equippingPlayer.attackTime*4, 1.4, 0, 0);
					GL11.glRotatef(90F, 0, 0F, 0);*/
				} else {
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(2, 2, -2);

					GL11.glTranslated(-0.5, -0.85, -0.51);

					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					GL11.glPopMatrix();

					// render left arm
					GL11.glPushMatrix();
					RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
							.getResourceDomain()
							+ ":"
							+ equippingPlayer.getLocationSkin().getResourcePath());

					GL11.glRotated(280, 0, 0, 1);
					GL11.glRotated(-60, 1, 0, 0);
					GL11.glRotated(-75, 0, 1, 0);
					GL11.glScaled(2, 2, -2);

					GL11.glTranslated(-1.1, -0.85, -0.51);

					RenderHelper.enableStandardItemLighting();

					GL11.glScaled(-1, 1, 1);
					renderHand(equippingPlayer, rend);
					GL11.glPopMatrix();
					// hold upright at the sides
					// GL11.glTranslatef(0F, 2.3F, -0.889F);
					// GL11.glScalef(0.3F, 0.3F, 0.3F);
					// GL11.glRotatef(90F, 0F, 0F, 0);
					// Hold with both hands

					GL11.glTranslatef(0F, -0.8F, 0.9F);
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					
					GL11.glRotatef(42F, 0F, 1F, 0F);
					//GL11.glRotated(equippingPlayer.attackTime * (-30), 1, 0, 0);
				}
			}

		} else {
			if (data[1] instanceof AbstractClientPlayer) {
				equippingPlayer = (AbstractClientPlayer) data[1];
				GL11.glPushMatrix();
				// hand transforms go here
				GL11.glPopMatrix();
				{
					GL11.glTranslatef(0F, 2.3F, -0.889F);
					GL11.glScalef(0.3F, 0.3F, 0.3F);
					GL11.glRotatef(90F, 0F, 0F, 0);
					if(NeedyLittleThings.isUltimating(equippingPlayer)){
					GL11.glRotated(equippingPlayer.ticksExisted*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 1, 0);
					GL11.glRotated(Math.sin(equippingPlayer.worldObj.getTotalWorldTime())*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 0, 0);
					GL11.glRotated(equippingPlayer.cameraYaw*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 0, 1);
					}
				}
			} else {

				GL11.glTranslatef(0F, 2.3F, -0.889F);
				GL11.glScalef(0.3F, 0.3F, 0.3F);
				GL11.glRotatef(90F, 0, 0F, 0);
			}
		}

		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(texture);
		model.renderAll();
		GL11.glPopMatrix();
	}

	private void renderHand(AbstractClientPlayer equippingPlayer,
			RenderPlayer rend) {
		if(!equippingPlayer.isInvisible())
		rend.renderFirstPersonArm(equippingPlayer);
	}

	private void executeAnimations(ItemStack item, AbstractClientPlayer equippingPlayer) {
		RenderPlayer rend = (RenderPlayer) RenderManager.instance
				.getEntityRenderObject(equippingPlayer);
		Minecraft mc=Minecraft.getMinecraft();
		GL11.glPushMatrix();
		// actual stick
		int type=AnimationStalker.getThis(equippingPlayer).getType();
		int length=6400;
		//RenderHalper.bindTexture("taoism:hud/QiHandOverlay.png");
		//mc.ingameGUI.drawTexturedModalRect(100, 100, 0, 0, length, length);
		//System.out.println("executing attack animation of type "+type);
		if(NeedyLittleThings.isUltimating(equippingPlayer)){
			//render right arm
			
			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-0.5, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			renderHand(equippingPlayer, rend);
			
			
			
			GL11.glPopMatrix();

			// render left arm
			GL11.glPushMatrix();
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-1.1, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			RenderHalper.renderColoredSphere(255, 255, 180, 0.4f, 0, 0, 0, 2f);
			GL11.glPopMatrix();
			
			// hold upright at the sides
			// GL11.glTranslatef(0F, 2.3F, -0.889F);
			// GL11.glScalef(0.3F, 0.3F, 0.3F);
			// GL11.glRotatef(90F, 0F, 0F, 0);
			
			// Hold with both hands
			//creates multiple sticks
			/*FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
			GL11.glPushMatrix();
			GL11.glRotated(equippingPlayer.ticksExisted*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 0, 1);
			GL11.glRotated(Math.sin(equippingPlayer.worldObj.getTotalWorldTime())*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 1, 0);
			GL11.glRotated(equippingPlayer.cameraYaw*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 0, 1);
			model.renderAll();
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glRotated(equippingPlayer.ticksExisted*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 0, 1);
			GL11.glRotated(Math.sin(equippingPlayer.worldObj.getTotalWorldTime())*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 1, 0);
			GL11.glRotated(equippingPlayer.cameraYaw*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 0, 1);
			model.renderAll();
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glRotated(equippingPlayer.ticksExisted*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 0, 0);
			GL11.glRotated(Math.sin(equippingPlayer.worldObj.getTotalWorldTime())*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 1, 0);
			GL11.glRotated(equippingPlayer.cameraYaw*WuGongHandler.getThis(equippingPlayer).getLevel(), 1.2, 0, 1);
			model.renderAll();
			GL11.glPopMatrix();*/
			
			GL11.glTranslatef(0F, -0.8F, 0.9F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(42F, 0F, 1F, 0F);
			
			GL11.glRotated(equippingPlayer.ticksExisted*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 1, 0);
			GL11.glRotated(Math.sin(equippingPlayer.worldObj.getTotalWorldTime())*WuGongHandler.getThis(equippingPlayer).getLevel(), 1, 0, 0);
			//GL11.glRotated(equippingPlayer.cameraYaw*WuGongHandler.getThis(equippingPlayer).getLevel(), 0, 0, 1);
		}
		else if (type==2) {
			//TODO jump attack!
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glRotated(-equippingPlayer.attackTime*3, 0, 0, 0);
			GL11.glScaled(2, 2, -2);

			//GL11.glTranslated(-0.7, -0.6, -0.51);
			GL11.glTranslated(-0.7, -0.5, -0.7);
			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();

			// render left arm
			GL11.glPushMatrix();
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glRotated(-equippingPlayer.attackTime*3, 0, 0, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-1.1, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();
			
			// hold upright at the center
			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);
			GL11.glTranslated(-0.0, -0.85, -0.51);
			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);

			GL11.glTranslatef(0F, 2.3F, -0.889F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(90F, 0F, 0F, 0);
			GL11.glRotated(-68, 1, 0, 0);
			GL11.glTranslated(-1, -6, 0);
			// get the swing progress somewhere round here, and the
			// distance walked with prevDistanceWalked
			GL11.glRotated(equippingPlayer.attackTime * 6, 1, 0.26, 0);
		} else if (type==1) {
			//TODO shift attack!
			//right arm
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-0.5, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();

			// render left arm
			GL11.glPushMatrix();
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-1.1, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();
			//stick
			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);
			GL11.glTranslated(-0.0, -0.85, -0.51);
			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);

			GL11.glTranslatef(-0F,2.7F, -0.889F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotated(5, 0, 0, 0.2);
			GL11.glRotatef(90F, 0, 0F, 0);
			GL11.glRotated(-30*Math.sin((equippingPlayer.attackTime)/3)+20, 1.4, 0, 0);
			
			GL11.glTranslated(-1, 0, 0);
			GL11.glRotated(5, 0, 0, -0.0001);
		} else {
			//TODO normal attack!
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-0.5, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();

			// render left arm
			GL11.glPushMatrix();
			RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
					.getResourceDomain()
					+ ":"
					+ equippingPlayer.getLocationSkin().getResourcePath());

			GL11.glRotated(280, 0, 0, 1);
			GL11.glRotated(-60, 1, 0, 0);
			GL11.glRotated(-75, 0, 1, 0);
			GL11.glScaled(2, 2, -2);

			GL11.glTranslated(-1.1, -0.85, -0.51);

			RenderHelper.enableStandardItemLighting();

			GL11.glScaled(-1, 1, 1);
			renderHand(equippingPlayer, rend);
			GL11.glPopMatrix();
			
			// hold upright at the sides
			// GL11.glTranslatef(0F, 2.3F, -0.889F);
			// GL11.glScalef(0.3F, 0.3F, 0.3F);
			// GL11.glRotatef(90F, 0F, 0F, 0);
			// Hold with both hands

			GL11.glTranslatef(0F, -0.8F, 0.9F);
			GL11.glScalef(0.3F, 0.3F, 0.3F);
			GL11.glRotatef(42F, 0F, 1F, 0F);
			
			GL11.glTranslated(-equippingPlayer.attackTime+(3*Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime*9))), 0, (-3*Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime*9))));
			GL11.glRotated(equippingPlayer.attackTime * (15), 0, 1, 0);
		}
	}
	
}
