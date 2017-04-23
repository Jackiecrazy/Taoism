package com.Jackiecrazi.taoism.client.render.weapons;

import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.client.models.items.weapons.ModelLance;
import com.Jackiecrazi.taoism.common.items.resource.ItemResource;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderSpearNew implements IItemRenderer {
	ModelLance lance;

	public RenderSpearNew() {
		lance=new ModelLance();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return false;
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
		Entity e;
		GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDepthMask(false);
	    GL11.glEnable(GL11.GL_ALPHA_TEST);
	    GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	
		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			/*GL11.glScalef(0.5F, 0.5F, 0.5F);
			GL11.glTranslated(0, -1, 0);
			GL11.glRotatef(30F, 0, 0F, 0);*/
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
				double y=Math.sin(NeedyLittleThings.rad(equippingPlayer.attackTime*19)-5);
				GL11.glTranslated(-y/2, y, 0);
				// Not good for stick, but not bad
				// for polearms

			}
		} else if (type == ItemRenderType.EQUIPPED) {
			// GL11.glScalef(0.3F, 0.3F, 0.3F);
			e = (Entity) data[1];
			GL11.glRotatef(9155, 0.3f, 0F, 1f);
			// System.out.println(e.ticksExisted*5);
			// GL11.glRotated(20, 0, 0, 1);
			GL11.glTranslatef(0.7F, -3F, 0.7F);

		} else {
			// GL11.glScalef(0.3F, 0.3F, 0.3F);

			GL11.glRotatef(200F, 0, 0F, 0);
			GL11.glRotated(20, 0, 0, 1);
			GL11.glTranslatef(0.3F, 1.3F, 0.7F);

		}

		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(new ResourceLocation("taoism:textures/items/weapons/xuantie/lance.png"));
		try{
			HashMap<String, HashMap<String, ItemStack>> spear=((GenericTaoistWeapon)item.getItem()).getPartsOfParts(item);
			ItemStack guardstack=spear
					.get(StaticRefs.GUARD)
					.get("base");
			ItemResource guard=((ItemResource)spear.get(StaticRefs.GUARD).get("base").getItem());
			ItemStack shaftstack=spear.get(StaticRefs.SHAFT).get("base");
			ItemResource shaft=((ItemResource)spear.get(StaticRefs.SHAFT).get("base").getItem());
			ItemStack pommelstack=spear.get(StaticRefs.POMMEL).get("base");
			ItemResource pommel=((ItemResource)spear.get(StaticRefs.POMMEL).get("base").getItem());
			ItemStack tipstack=spear.get(StaticRefs.TIP).get("base");
			ItemResource tip=((ItemResource)spear.get(StaticRefs.TIP).get("base").getItem());
			//System.out.println(guard.getUnlocalizedName().toLowerCase());
		lance.render(item,
				guard.getUnlocalizedName(guardstack).substring(14).toLowerCase(),//.getParts(guardstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				shaft.getUnlocalizedName(shaftstack).substring(14).toLowerCase(),//.getParts(shaftstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				pommel.getUnlocalizedName(pommelstack).substring(14).toLowerCase(),//.getParts(pommelstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				tip.getUnlocalizedName(tipstack).substring(14).toLowerCase());//.getParts(tipstack)[0].getUnlocalizedName().substring(14).toLowerCase());
		
		}
		catch(Exception npe){
			String derp="xuantie/lance";
			//npe.printStackTrace();
			lance.render(item, derp, derp, derp, derp);
		}
		
		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDepthMask(true);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}
}
