package com.Jackiecrazi.taoism.client.render.entity.mobs;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.entity.mobs.ModelLili;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLiLi;

public class RenderLili extends RenderLiving {

	public static final ResourceLocation Lili_texture = new ResourceLocation("taoism", "textures/mob/Lili.png");
	public static ModelLili modelLili = new ModelLili();	
	public static float modelHeight = 3.5F;
	
	public RenderLili()
    {
        super(modelLili, 1F);
    }
	
	@Override
	public void doRender(Entity _entity, double posX, double posY, double posZ, float var8, float var9) {
		EntityLiLi entity = (EntityLiLi) _entity;
				
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		super.doRender(_entity, posX, posY, posZ, var8, var9);
		//System.out.println("called");
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f)
	{
		GL11.glRotatef(180F, 0, 1F, 0F);
		GL11.glRotatef(180F, 0, 0, 1F);
		//System.out.println("translating");
		GL11.glTranslatef(0, modelHeight, 0.3f);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return Lili_texture;
	}
}