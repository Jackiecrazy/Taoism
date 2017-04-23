package com.Jackiecrazi.taoism.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderElementalProjectile extends RenderEntity {

	public RenderElementalProjectile() {
		// TODO Auto-generated constructor stub
	}

	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();
        renderOffsetAABB(p_76986_1_.boundingBox, p_76986_2_ - p_76986_1_.lastTickPosX, p_76986_4_ - p_76986_1_.lastTickPosY, p_76986_6_ - p_76986_1_.lastTickPosZ);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return null;
    }
}
