package com.Jackiecrazi.taoism.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityDroppedWeapon;

public class RenderDroppedWeapon extends RenderEntity {

	private RenderItem customRenderItem;
	public RenderDroppedWeapon() {
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
	
	@Override
	public void doRender(Entity e, double x,
			double y, double z, float yaw,
			float pt) {
		//TODO "random" rotation is actually based on passing the xyz yaw pt through some fancy function that returns -180~180
		EntityDroppedWeapon eli=(EntityDroppedWeapon)e;
		GL11.glPushMatrix();
		if (eli.getEntityItem() != null)
        {
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
                GL11.glTranslatef((float) x, (float) y + displacement, (float) z);
            }
            GL11.glRotatef(rotationAngle, 0.0F, 1.0F, 0.0F);
            if(!eli.onGround){
            	GL11.glRotated(eli.ticksExisted*4d, 1,0,0);
            	GL11.glRotated(eli.ticksExisted*3d, 0,1,0);
            	GL11.glRotated(eli.ticksExisted*7d, 0,0,1);
            }
            else{
            	GL11.glRotated((eli.getEntityId()*4d)%180-90, 1,0,0);
                GL11.glRotated((eli.getEntityId()*3d)%180+rotationAngle, 0,1,0);
                GL11.glRotated((eli.getEntityId()*7d)%180-90, 0,0,1);
            }
            GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);

            customRenderItem.doRender(ghostEntityItem, 0, 0, 0, 0, 0);
        }
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
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
