package com.Jackiecrazi.taoism.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.models.blocks.ModelLianQiLu;
import com.Jackiecrazi.taoism.common.block.tile.TileDing;

public class RenderDing extends TileEntitySpecialRenderer {
	ResourceLocation ding = new ResourceLocation("taoism:textures/models/lianqilu.png");
	 
	 private ModelLianQiLu model;
	public RenderDing() {
		model=new ModelLianQiLu();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float what) {
		TileDing te=(TileDing)tile;
		Minecraft mc=Minecraft.getMinecraft();
		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	    //GL11.glEnable(GL11.GL_BLEND);
	    
		//GL11.glScaled(0.7, 0.7, 0.7);
		GL11.glTranslatef((float)x + 0.5f, (float)y + 1.2f, (float)z + 0.5f);
		GL11.glRotatef(180, 0f, 0f, 1f);
		int meta = mc.theWorld.getBlockMetadata((int)tile.xCoord, (int)tile.yCoord,(int) tile.zCoord);
		GL11.glRotatef(90*meta, 0f, 1f, 0f);
		this.bindTexture(ding);
		model.render(0.0625f);
		/*
		if(Tessellator.instance!=null){
			Tessellator t=Tessellator.instance;
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("taoism:hud/LingMeter.png"));
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        //ying yang
		t.startDrawingQuads();
		t.setColorOpaque(255, 255, 255);
		GL11.glRotatef(180, 0, 0, 0);
		t.addVertexWithUV(-3, -1.1, -3, 0, 0);
		t.addVertexWithUV(-3, -1.1, +3, 0, 1);
		t.addVertexWithUV(+3, -1.1, +3, 1, 1);
		t.addVertexWithUV(+3, -1.1, -3, 1, 0);
		t.draw();
		//bagua
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("taoism:hud/benti.png"));
		t.startDrawingQuads();
		t.setColorOpaque(255, 255, 255);
		GL11.glRotatef(180, 0, 0, 0);
		t.addVertexWithUV(5, 1, 5, 0, 0);
		t.addVertexWithUV(5, 1, -5, 1, 0);
		t.addVertexWithUV(-5, 1, -5, 1, 1);
		t.addVertexWithUV(-5, 1, 5, 0, 1);
		t.draw();
		//rest of bagua
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("taoism:hud/fuhao.png"));
		t.startDrawingQuads();
		t.setColorOpaque(255, 255, 255);
		GL11.glRotatef(180, 0, 0, 0);
		t.addVertexWithUV(5, 0.7, 5, 0, 0);
		t.addVertexWithUV(5, 0.7, -5, 1, 0);
		t.addVertexWithUV(-5, 0.7, -5, 1, 1);
		t.addVertexWithUV(-5, 0.7, 5, 0, 1);
		t.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
	    GL11.glDisable(GL11.GL_BLEND);
		}*/
		GL11.glPopMatrix();
	}

}
