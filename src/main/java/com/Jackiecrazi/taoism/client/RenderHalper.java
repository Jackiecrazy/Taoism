package com.Jackiecrazi.taoism.client;


import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.ClientProxy;
import com.Jackiecrazi.taoism.Taoism;

public class RenderHalper {
	static HashMap<String,IModelCustom> modelMap = new HashMap<String,IModelCustom>();
	static HashMap<String,ResourceLocation> textureMap = new HashMap<String,ResourceLocation>();
	private static Minecraft mc()
	{
		return Minecraft.getMinecraft();
	}
	public static void bindTexture(String path)
	{
		mc()
		.getTextureManager()
		.bindTexture(getResource(path));
	}
	public static ResourceLocation getResource(String path)
	{
		ResourceLocation rl = textureMap.containsKey(path) ? textureMap.get(path) : new ResourceLocation(path);
		if(!textureMap.containsKey(path))
			textureMap.put(path, rl);
		return rl;
	}

	public static IModelCustom bindModel(String domain, String path)
	{
		String mapPath = domain+":"+path;
		if(modelMap.containsKey(mapPath))
			return modelMap.get(mapPath);

		try
		{
			IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(domain, path));
			modelMap.put(mapPath, model);
			return model;
		}
		catch(Exception e)
		{
			Taoism.logger.log(Level.ERROR, "Error on attempt to load model: "+domain+","+path);
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Draws a sphere. That's it.
	 * @param r red, 0-255
	 * @param g green, 0-255
	 * @param b blue, 0-255
	 * @param a alpha, 0.0-1.0
	 * @param x position x from center
	 * @param y position y from center
	 * @param z position z from center
	 * @param size relative to a block
	 */
	public static void renderColoredSphere(int r, int g, int b, float a,double x, double y, double z, float size){
		GL11.glPushMatrix();
	    GL11.glTranslated(x, y, z);
	    GL11.glScalef(size, size, size);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDepthMask(false);

	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	float red=r/255f;
	float green=g/255f;
	float blue=b/255f;
	    GL11.glColor4f(red, green, blue, a);
	    GL11.glEnable(GL11.GL_ALPHA_TEST);
	    GL11.glCallList(ClientProxy.sphereIDOutside);

	    GL11.glCallList(ClientProxy.sphereIDInside);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDepthMask(true);
	    GL11.glPopMatrix();
	}
	public static void bindMatTex(String path)
	{
		mc()
		.getTextureManager()
		.bindTexture(getResource("taoism:textures/items/weapons/"+path+".png"));
	}
}
