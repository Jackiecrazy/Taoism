package com.Jackiecrazi.taoism.client.render;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderLing {
	Minecraft mc = Minecraft.getMinecraft();
	int offsetX,offsetY;
	public RenderLing(int offsetX,int offsetY){
		this.offsetX=offsetX;
		this.offsetY=offsetY;
	}
	  @SubscribeEvent
	  public void onRender(TickEvent.RenderTickEvent event)
	  {
	    onTickRender(event);
	  }
	  private void onTickRender(TickEvent.RenderTickEvent e) {
	    if (mc.currentScreen == null) {
	      GuiIngame gig = mc.ingameGUI;
	      ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	      int i = scaledresolution.getScaledWidth();
	      int k = scaledresolution.getScaledHeight();
	      mc.getTextureManager().bindTexture(new ResourceLocation("taoism:hud/LingMeter.png"));
	      int y = WayofConfig.LingY*10;
	      int x = WayofConfig.LingX*2;

	      EntityClientPlayerMP thePlayer = mc.thePlayer;
		Float amnt=PlayerResourceStalker.get(thePlayer).getValues(WayofConfig.LingLiDWID);
		
		float lingMax = (XiuWeiHandler.getThis(thePlayer).getLevel() * XiuWeiHandler
				.getThis(thePlayer).getLevel()) * 4;
	      float perc=amnt/lingMax;
	      GL11.glPushMatrix();
	      GL11.glEnable(GL11.GL_BLEND);
	      
	      GL11.glDisable(GL11.GL_LIGHTING);
	      GL11.glEnable(GL11.GL_TEXTURE_2D);
	      GL11.glEnable(GL11.GL_ALPHA_TEST);
	      
	      GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
	      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
	      GL11.glScaled(0.2, 0.2, 0.2);
	      GL11.glTranslated(x, y, 0);//this translates the entire thing, icon, rotation and all.
	      GL11.glRotatef(mc.thePlayer.ticksExisted*10*perc, 0, 0, 1);//this rotates the icon around the defined point below in the z axis
	      GL11.glTranslated(100, 100, 0);//this translates the centre of rotation to where it needs to be
	      
	      /*GL11.glEnable(GL11.GL_LIGHTING);
	      GL11.glDisable(GL11.GL_TEXTURE_2D);
	      GL11.glDisable(GL11.GL_ALPHA_TEST);*/
	      GL11.glEnable(GL11.GL_CULL_FACE);
	      //if(level>0)
		gig.drawTexturedModalRect(-229, -229, 0, 0, 270, 270);
		//System.out.println(amnt/(level*level*4));
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	    }
	  }
}
