package com.Jackiecrazi.taoism.client.render.entity.mobs;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.entity.mobs.ModelShuhu;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityShuHu;

public class RenderShuhu extends RenderLiving {

	public static final ResourceLocation Shuhu_texture = new ResourceLocation("taoism", "textures/mob/shuhu.png");
	public static ModelShuhu modelShuhu = new ModelShuhu();	
	public static float modelHeight = 3F;
	
	public RenderShuhu()
    {
        super(modelShuhu, 1F);
    }
	
	@Override
	public void doRender(Entity _entity, double posX, double posY, double posZ, float var8, float var9) {
		EntityShuHu entity = (EntityShuHu) _entity;
				
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		super.doRender(_entity, posX, posY, posZ, var8, var9);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f)
	{
		GL11.glRotatef(180F, 0, 1F, 0F);
		GL11.glRotatef(180F, 0, 0, 1F);
		GL11.glTranslatef(0, modelHeight, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return Shuhu_texture;
	}
}