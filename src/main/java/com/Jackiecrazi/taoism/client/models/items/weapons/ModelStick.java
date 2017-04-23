package com.Jackiecrazi.taoism.client.models.items.weapons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModelStick extends ModelBase {
	ModelRenderer stick;
	public ModelStick() {

		textureWidth = 128;
	    textureHeight = 64;
	    stick=new ModelRenderer(this);
	    stick.addBox(0, 0, 0, 128, 9, 9);
	    stick.setTextureSize(128, 64);
	}
	public void render(ItemStack is)
	  {
	  	if (RenderManager.instance.renderEngine == null) return;
	      Item wand = is.getItem();
	      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	      GL11.glScaled(0.6, 0.6, 0.6);
	      GL11.glRotated(30, 0, 0, 1);
	      GL11.glTranslated(1.2, 2.8d, 0);
	      //GL11.glRotated(180, 0d, 0d, 1d);
	      float f5=0.0625f;
	      stick.render(f5);
	  }
}
