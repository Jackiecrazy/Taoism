package com.Jackiecrazi.taoism.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.blocks.ModelFengxiang;
import com.Jackiecrazi.taoism.common.block.tile.TileBellows;

public class RenderFengxiang extends TileEntitySpecialRenderer {

	public static final ResourceLocation Fengxiang_texture = new ResourceLocation("taoism", "textures/models/Fengxiang.png");
	public static ModelFengxiang modelFengxiang;	
	
	public RenderFengxiang()
    {
        modelFengxiang = new ModelFengxiang();
    }
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float partialtick) {
		TileBellows entity = (TileBellows) tile;
		Minecraft mc=Minecraft.getMinecraft();
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glTranslated(x+0.5, y+1.075, z+0.39);
		int meta = mc.theWorld.getBlockMetadata((int)tile.xCoord, (int)tile.yCoord,(int) tile.zCoord);
		GL11.glRotatef(90*meta+(meta%2==0?180:0), 0f, 1f, 0f);
		//System.out.println(meta);
		//GL11.glScaled(0.7, 0.7, 0.7);
		GL11.glColor4d(1.0, 1.0,1.0,1.0);
		this.bindTexture(Fengxiang_texture);
		modelFengxiang.render(entity, 0.0625F, partialtick);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}