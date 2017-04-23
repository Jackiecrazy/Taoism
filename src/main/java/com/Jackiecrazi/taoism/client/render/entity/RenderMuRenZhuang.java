package com.Jackiecrazi.taoism.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.entity.literaldummies.ModelMuRenZhuang;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;

public class RenderMuRenZhuang extends Render {

	public static final ResourceLocation Sandbag_texture = new ResourceLocation("taoism", "entity/dummies/MRZ.png");
	public static ModelMuRenZhuang modelMuRenZhuang;	
	
	public RenderMuRenZhuang()
    {
        this.modelMuRenZhuang = new ModelMuRenZhuang();
    }
	
	@Override
	public void doRender(Entity _entity, double posX, double posY, double posZ, float var8, float var9) {
		EntityMuRenZhuang entity = (EntityMuRenZhuang) _entity;
				
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		GL11.glRotated(-entity.rotationYaw, 0, 1, 0);
		GL11.glRotated(180, 0, 1, 0);
		this.bindEntityTexture(entity);
		this.modelMuRenZhuang.render(entity, 0.0F, 0.0F, 1F, 0.0F, 0.0F, 0.0625F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return Sandbag_texture;
	}

}
