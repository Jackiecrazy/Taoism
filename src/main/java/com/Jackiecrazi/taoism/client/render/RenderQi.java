package com.Jackiecrazi.taoism.client.render;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderQi {
	Minecraft mc = Minecraft.getMinecraft();
	int offsetX,offsetY;
	public RenderQi(int offsetX,int offsetY){
		this.offsetX=offsetX;
		this.offsetY=offsetY;
	}
	  @SubscribeEvent
	  public void onRender(TickEvent.RenderTickEvent event)
	  {
	    onTickRender();
	  }
	  private void onTickRender() {
	    if (mc.currentScreen == null) {
	      GuiIngame gig = mc.ingameGUI;
	      ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	      int i = scaledresolution.getScaledWidth();
	      int k = scaledresolution.getScaledHeight();
	      mc.getTextureManager().bindTexture(new ResourceLocation("taoism:hud/QiMeter.png"));//TODO draw something!
	      int y = WayofConfig.QiY;
	      int x = WayofConfig.QiX;

	      Float amnt=PlayerResourceStalker.get(mc.thePlayer).getValues(WayofConfig.QiDWID);

	      int level=XiuWeiHandler.getThis(mc.thePlayer).getLevel();
	      GL11.glPushMatrix();
	      GL11.glScaled(0.2, 0.4, 0.2);
		gig.drawTexturedModalRect(x, y, 0, 0, 270, 150);
		GL11.glPushMatrix();
		float m=((120*amnt)/(level*level*4));
		gig.drawTexturedModalRect(x, y, 0, 128, 270, (int) (-Math.abs(m)+120));
		GL11.glPopMatrix();
GL11.glPopMatrix();
int offset =Math.floor(amnt)+1>=10000?0:Math.floor(amnt)+1>=1000?3:Math.floor(amnt)+1>=100?5:Math.floor(amnt)+1>=10?7:10;
	      Minecraft.getMinecraft().fontRenderer.drawString(Math.max((int)Math.floor(amnt), 0)+"", x+14+offset, y+16, 0);
	      
	      
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 15, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 13, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 14, 16777215);
	    }
	  }
}
