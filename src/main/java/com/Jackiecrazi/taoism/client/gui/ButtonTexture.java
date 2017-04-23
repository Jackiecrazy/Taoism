package com.Jackiecrazi.taoism.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ButtonTexture extends GuiButton {

	private final ResourceLocation texture;
    private final int bWidth, bHeight;
	
    public ButtonTexture(int id, int x, int y, int wid, int hei, ResourceLocation texture, int width, int height)
    {
        super(id, x, y, wid, hei, "");
        this.texture = texture;
        this.bWidth = width;
        this.bHeight = height;
    }
	
	@Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            final FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            this.getHoverState(this.field_146123_n);
            double scale =0.05d;
            GL11.glScaled(scale, scale, scale);
            par1Minecraft.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect((int)(this.xPosition/scale), (int)(this.yPosition/scale), 0, 0, this.bWidth, this.bHeight);
            this.mouseDragged(par1Minecraft, (int)(par2/scale), (int)(par3/scale));
            int var6 = 14737632;

            if (!this.enabled)
            {
                var6 = -6250336;
            }
            else if (this.field_146123_n)
            {
                var6 = 16777120;
            }

            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
            GL11.glPopMatrix();
        }
    }
}
