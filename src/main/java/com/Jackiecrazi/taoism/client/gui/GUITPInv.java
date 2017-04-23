package com.Jackiecrazi.taoism.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.container.ContainerTPInv;
import com.Jackiecrazi.taoism.common.inventory.InventoryTPInv;
import com.Jackiecrazi.taoism.networking.PacketRearrangeInventorySlots;

public class GUITPInv extends InventoryEffectRenderer {
	/** x size of the inventory window in pixels. Defined as  float, passed as int */
    private float xSizeFloat;
    /** y size of the inventory window in pixels. Defined as  float, passed as int. */
    private float ySizeFloat;
	private EntityPlayer pl;
	public GUITPInv(EntityPlayer p,InventoryTPInv itpi) {
		super(new ContainerTPInv(p, p.inventory, itpi));
		pl=p;
		
	}

	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @SuppressWarnings("unchecked")
	public void initGui()
    {
    	super.initGui();//+ 71    + 15
    	this.buttonList.add(new ButtonTexture(0, this.guiLeft-20 , this.guiTop, 16, 16, RenderHalper.getResource("taoism:textures/items/armor/wushushirt.png"), 256, 256));
    	this.buttonList.add(new ButtonTexture(1, this.guiLeft-20 , this.guiTop+16 , 16, 16, RenderHalper.getResource("taoism:textures/items/ScrollJade.png"), 256, 256));
    	
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderHalper.bindTexture("taoism:gui/custominv"+((ContainerTPInv)this.inventorySlots).getScreen()+".png");
        int k = this.guiLeft;
        int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

	    drawPlayerModel(k + 34, l + 75, 30, (float)(k + 51) - this.xSizeFloat, (float)(l + 75 - 50) - this.ySizeFloat, this.mc.thePlayer);
	    //element resist code goes here
	}

	public static void drawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase playerdrawn)
	  {
	    GL11.glEnable(2903);
	    GL11.glPushMatrix();
	    GL11.glTranslatef(x, y, 50.0F);
	    GL11.glScalef(-scale, scale, scale);
	    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
	    float f2 = playerdrawn.renderYawOffset;
	    float f3 = playerdrawn.rotationYaw;
	    float f4 = playerdrawn.rotationPitch;
	    float f5 = playerdrawn.prevRotationYawHead;
	    float f6 = playerdrawn.rotationYawHead;
	    GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
	    RenderHelper.enableStandardItemLighting();
	    GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
	    GL11.glRotatef(-(float)Math.atan(pitch / 40.0F) * 20.0F, 1.0F, 0.0F, 0.0F);
	    playerdrawn.renderYawOffset = ((float)Math.atan(yaw / 40.0F) * 20.0F);
	    playerdrawn.rotationYaw = ((float)Math.atan(yaw / 40.0F) * 40.0F);
	    playerdrawn.rotationPitch = (-(float)Math.atan(pitch / 40.0F) * 20.0F);
	    playerdrawn.rotationYawHead = playerdrawn.rotationYaw;
	    playerdrawn.prevRotationYawHead = playerdrawn.rotationYaw;
	    GL11.glTranslatef(0.0F, playerdrawn.yOffset, 0.0F);
	    RenderManager.instance.playerViewY = 180.0F;
	    RenderManager.instance.renderEntityWithPosYaw(playerdrawn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
	    playerdrawn.renderYawOffset = f2;
	    playerdrawn.rotationYaw = f3;
	    playerdrawn.rotationPitch = f4;
	    playerdrawn.prevRotationYawHead = f5;
	    playerdrawn.rotationYawHead = f6;
	    GL11.glPopMatrix();
	    RenderHelper.disableStandardItemLighting();
	    GL11.glDisable(32826);
	    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	    GL11.glDisable(3553);
	    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	  }
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        this.xSizeFloat = (float)p_73863_1_;
        this.ySizeFloat = (float)p_73863_2_;
    }
	
	@Override
    protected void actionPerformed(GuiButton g)
    {
		//System.out.println("trigger");
		Taoism.net.sendToServer(new PacketRearrangeInventorySlots(g.id));
		((ContainerTPInv)pl.openContainer).change(g.id);
    }
}
