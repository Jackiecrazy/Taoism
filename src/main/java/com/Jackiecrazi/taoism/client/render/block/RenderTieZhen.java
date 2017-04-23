package com.Jackiecrazi.taoism.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.blocks.ModelTieZhen;
import com.Jackiecrazi.taoism.common.block.tile.TileAnvil;

public class RenderTieZhen extends TileEntitySpecialRenderer {
	ResourceLocation ding = new ResourceLocation("taoism:textures/models/tiezhen.png");

	private ModelTieZhen model;
	EntityItem entItem = null;

	private RenderItem customRenderItem;
	public RenderTieZhen() {
		model=new ModelTieZhen();
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
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float idk) {
		//System.out.println("x at "+x+" and y at "+y+" and z at "+z);
		TileAnvil tileEntity=(TileAnvil)tile;
		Minecraft mc=Minecraft.getMinecraft();
		GL11.glPushMatrix();
		//GL11.glScaled(0.01, 0.01, 0.01);
		GL11.glTranslated((float)x + 0.5, (float)y+1.5, (float)z + 0.5);
		GL11.glRotatef(180, 0f, 0f, 1f);
		int met = mc.theWorld.getBlockMetadata((int)tile.xCoord, (int)tile.yCoord,(int) tile.zCoord);
		GL11.glRotatef(90*met, 0f, 1f, 0f);
		//System.out.println(mc.theWorld.getBlock((int)x, (int)y,(int) z));
		//GL11.glScaled(0.3, 0.3, 0.3);
		this.bindTexture(ding);
		model.render(0.0625f);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if (tileEntity.getStackInSlot(0) != null)
        {
            float scaleFactor = getGhostItemScaleFactor(tileEntity.getStackInSlot(0));
            float rotationAngle = Minecraft.getMinecraft().gameSettings.fancyGraphics ? (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) : 0;
            EntityItem ghostEntityItem = new EntityItem(tileEntity.getWorldObj());
            ghostEntityItem.hoverStart = 0.0F;
            ghostEntityItem.setEntityItemStack(tileEntity.getStackInSlot(0));
            float displacement = 0.2F;
            float offx=0;//(met%2==1?0.5f:1)*(met==1?-1:1);
        	float offz=0;//(met%2==0?-0.5f:0)*(met==0?1:-1);
        	
        	GL11.glTranslated(-1,0,-1);//there's more here
        	
            if (ghostEntityItem.getEntityItem().getItem() instanceof ItemBlock)
            {
            	
                GL11.glTranslatef((float) x + 0.5F+offx, (float) y + displacement + 0.95F, (float) z + 0.5F+offz);
            } else
            {
                GL11.glTranslatef((float) x + 1.3F+offx, (float) y + displacement + 0.85F, (float) z + 1.8F+offz);
            }
            GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
            GL11.glRotated(90*met, 0, 1, 0);
            GL11.glRotatef(rotationAngle, 0.0F, 1.0F, 0.0F);
            GL11.glRotated(90, 1, 0, 0);
            GL11.glRotated(90, 0, 1, 0);
            customRenderItem.doRender(ghostEntityItem, 0, 0, 0, 0, 0);
        }
		
		
		
		//item
		GL11.glPopMatrix();
		
		/*if(tileEntity.getTool()==null)return;
		if(((entItem == null) || entItem.getEntityItem().getItem() != tileEntity.getTool().getItem()))
			entItem = new EntityItem(tileEntity.getWorldObj(), x, y, z, tileEntity.getTool());
		GL11.glPushMatrix();
		this.entItem.hoverStart = 0.0F;
		RenderItem.renderInFrame = true;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 3F, (float)z + 0.3F);
		//System.out.println(x);
		GL11.glRotatef(180, 0, 1, 1);
		if(entItem!=null)
			RenderManager.instance.renderEntityWithPosYaw(entItem, tile.xCoord + 0.5, tile.yCoord + 1, tile.zCoord + 0.5, 0, 0);
		RenderItem.renderInFrame = false;
		GL11.glPopMatrix();*/
		//else System.out.println(entItem.getEntityItem().getItem() != tileEntity.getTool().getItem());
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
