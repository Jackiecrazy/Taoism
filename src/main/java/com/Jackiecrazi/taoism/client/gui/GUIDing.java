package com.Jackiecrazi.taoism.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.container.ContainerDing;

public class GUIDing extends GuiContainer {
private TileDing td;
	public GUIDing(InventoryPlayer p, TileDing d) {
		super(new ContainerDing(d,p));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderHalper.bindTexture("taoism:gui/GUIDing.png");
	    int k = (width - xSize) / 2;
	    int l = (height - ySize) / 2;

	    GL11.glEnable(3042);
	    drawTexturedModalRect(k, l, 0, 0, xSize, ySize);


	}

}
