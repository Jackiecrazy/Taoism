package com.Jackiecrazi.taoism.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.entity.literaldummies.ModelSandbag;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;

public class RenderSandbag extends Render {

	public static final ResourceLocation Sandbag_texture = new ResourceLocation("taoism", "entity/dummies/Sandbag.png");
	public static ModelSandbag modelSandbag;	
	
	public RenderSandbag()
    {
        this.modelSandbag = new ModelSandbag();
    }
	
	@Override
	public void doRender(Entity _entity, double posX, double posY, double posZ, float var8, float var9) {
		EntitySandbag entity = (EntitySandbag) _entity;
				
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		this.bindEntityTexture(entity);
		this.modelSandbag.render(entity, 0.0F, 0.0F, 1F, 0.0F, 0.0F, 0.0625F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return Sandbag_texture;
	}
}