package com.Jackiecrazi.taoism.client.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.common.items.ItemQiPu;

public class RenderQiPu implements IItemRenderer {

	public RenderQiPu() {
		
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		GL11.glPushMatrix();
		
		/*GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);*/

        //HERE: alpha and blend are disabled:
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        
        
		if(type==ItemRenderType.INVENTORY){
			GL11.glScaled(16, 16, 16);
			GL11.glScaled(0.7, 0.7, 0.7);
		}
		IIcon icon=stack.getItem().getIcon(stack, 0);
		Tessellator tessellator = Tessellator.instance;
		
		//RenderHelper.disableStandardItemLighting();
		if(type==ItemRenderType.ENTITY){
			GL11.glScaled(2, 2, 2);
			GL11.glRotated(90, 1, 0, 0);
		}
		
		GL11.glRotated(45, 0, 0, 1);
		GL11.glTranslated(0.5, -0.5, 0);
		
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		
		//above
		icon=stack.getItem().getIcon(stack, 1);
		if(type==ItemRenderType.INVENTORY){
			GL11.glTranslated(0, 0, 0.06);
		}
		else GL11.glTranslated(0, 0, -0.06);
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		
		GL11.glPushMatrix();
		//below
		if(type==ItemRenderType.INVENTORY){
			GL11.glTranslated(0, 0, -0.12);
		}
		else GL11.glTranslated(0, 0, 0.12);
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		GL11.glPopMatrix();
		
		
		icon = ItemQiPu.icon[stack.getItemDamage()];
		//GL11.glPushMatrix();
		GL11.glScaled(0.6, 0.6, 1.2);
		if(type==ItemRenderType.INVENTORY){
			GL11.glTranslated(0.3, 0.3, 1);
		}
		else GL11.glTranslated(0.3, 0.3, -0.01);
		
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		//GL11.glPopMatrix();
		
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
        
        //RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}

}
