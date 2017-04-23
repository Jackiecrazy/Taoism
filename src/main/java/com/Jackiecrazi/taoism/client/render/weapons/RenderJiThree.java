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
import com.Jackiecrazi.taoism.client.models.items.weapons.ji.ModelJiThree;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderJiThree implements IItemRenderer {
	ModelJiThree lance;

	public RenderJiThree() {
		lance=new ModelJiThree();
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
		Entity e;

		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			GL11.glScalef(2F, 0.2F, 0.15F);
			GL11.glTranslated(0, -1, 0);
			GL11.glRotatef(30F, 0, 0F, 0);
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
				.bindTexture(new ResourceLocation("taoism:textures/items/weapons/xuantie/ji3.png"));
		HashMap<String, HashMap<String, ItemStack>> spear=((GenericTaoistWeapon)item.getItem()).getPartsOfParts(item);
		/*try{
			ItemStack guardstack=spear.get(StaticRefs.GUARD).get("base");
			ItemWeaponPart guard=((ItemWeaponPart)spear.get(StaticRefs.GUARD).get("base").getItem());
			ItemStack shaftstack=spear.get(StaticRefs.SHAFT).get("base");
			ItemWeaponPart shaft=((ItemWeaponPart)spear.get(StaticRefs.SHAFT).get("base").getItem());
			ItemStack pommelstack=spear.get(StaticRefs.POMMEL).get("base");
			ItemWeaponPart pommel=((ItemWeaponPart)spear.get(StaticRefs.POMMEL).get("base").getItem());
			ItemStack tipstack=spear.get(StaticRefs.TIP).get("base");
			ItemWeaponPart tip=((ItemWeaponPart)spear.get(StaticRefs.TIP).get("base").getItem());
		lance.render(item,
				guard.getParts(guardstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				shaft.getParts(shaftstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				pommel.getParts(pommelstack)[0].getUnlocalizedName().substring(14).toLowerCase(),
				tip.getParts(tipstack)[0].getUnlocalizedName().substring(14).toLowerCase());
		}
		catch(Exception npe){
			String derp="xuantie/lance";
			lance.render(item, derp, derp, derp, derp);
		}*/
		lance.render(item);
		GL11.glPopMatrix();
	}
}
