package com.Jackiecrazi.taoism.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.entity.EntityLevitatingItem;

public class RenderLevitatingItems extends Render {
	private RenderItem customRenderItem;
	public RenderLevitatingItems() {
		customRenderItem = new RenderItem()
        {
            @Override
            public boolean shouldBob()
            {
                return false;
            }
        };
        customRenderItem.setRenderManager(RenderManager.instance);
	}
	/*public void doRender(EntityItem p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
		System.out.println("this is being called");
		super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		RenderHalper.renderColoredSphere(135, 206, 250, 0.5f, 0, 0, 0, 0.8f);
    }*/

	@Override
	public void doRender(Entity e, double x,
			double y, double z, float s,
			float q) {
		EntityLevitatingItem eli=(EntityLevitatingItem)e;
		GL11.glPushMatrix();
		//System.out.println("i am being called");
		if (eli.getEntityItem() != null)
        {
			//System.out.println("i am being called");
			//RenderHalper.renderColoredSphere(135, 206, 250, 0.5f, x, y, z, 0.8f);
            float scaleFactor = getGhostItemScaleFactor(eli.getEntityItem());
            float rotationAngle = Minecraft.getMinecraft().gameSettings.fancyGraphics ? (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) : 0;
            EntityItem ghostEntityItem = new EntityItem(e.worldObj);
            ghostEntityItem.hoverStart = 0.0F;
            ghostEntityItem.setEntityItemStack(eli.getEntityItem());
            float displacement = 0.2F;

            if (ghostEntityItem.getEntityItem().getItem() instanceof ItemBlock)
            {
                GL11.glTranslatef((float) x + 0.5F, (float) y + displacement + 0.8F, (float) z + 0.5F);
            } else
            {
                GL11.glTranslatef((float) x + 0.5F, (float) y + displacement + 0.7F, (float) z + 0.5F);
            }
            GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
            GL11.glRotatef(rotationAngle, 0.0F, 1.0F, 0.0F);
            customRenderItem.doRender(ghostEntityItem, 0, 0, 0, 0, 0);
            RenderHalper.renderColoredSphere(135, 206, 250, 0.3f, 0, -0.001, 0, 0.8f);
        }
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}
	private float getGhostItemScaleFactor(ItemStack itemStack)
    {
        float scaleFactor = 1.0F;

        if (itemStack != null)
        {
            if (itemStack.getItem() instanceof ItemBlock)
            {
                switch (customRenderItem.getMiniBlockCount(itemStack, (byte) 1))
                {
                    case 1:
                        return 0.90F;

                    case 2:
                        return 0.90F;

                    case 3:
                        return 0.90F;

                    case 4:
                        return 0.90F;

                    case 5:
                        return 0.80F;

                    default:
                        return 0.90F;
                }
            } else
            {
                switch (customRenderItem.getMiniItemCount(itemStack, (byte) 1))
                {
                    case 1:
                        return 0.65F;

                    case 2:
                        return 0.65F;

                    case 3:
                        return 0.65F;

                    case 4:
                        return 0.65F;

                    default:
                        return 0.65F;
                }
            }
        }

        return scaleFactor;
    }
}
