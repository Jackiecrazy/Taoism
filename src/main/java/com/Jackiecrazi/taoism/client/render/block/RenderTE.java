package com.Jackiecrazi.taoism.client.render.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderTE implements IItemRenderer {
	private TileEntitySpecialRenderer tesr;
	private TileEntity te;
	public RenderTE(TileEntitySpecialRenderer sr, TileEntity e) {
		tesr=sr;
		te=e;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
		if (type == IItemRenderer.ItemRenderType.ENTITY)
		{
		GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
		}
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		 
		tesr.renderTileEntityAt(this.te, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glPopMatrix();
	}

}
