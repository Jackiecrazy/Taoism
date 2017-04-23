package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.Taoism;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class SkillRender {
	Minecraft mc = Minecraft.getMinecraft();
	protected String texture;
	Integer level=45;
	Float xp;
	Float xpNeeded;
	int xOff,yOff;
	protected String color;
	public SkillRender(int xOffset,int yOffset,String color){
		xOff=xOffset;
		yOff=yOffset;
		this.color=color;
	}
	  @SubscribeEvent
	  public void onRender(TickEvent.RenderTickEvent event)
	  {
	    onTickRender();
	  }
	  public Integer getLvl(EntityPlayer p){
		  return level;
	  }
	  public Float getExp(EntityPlayer p){
		  return xp;
	  }
	  public Float getExpNeeded(EntityPlayer p){
		  return xpNeeded;
	  }
	  private void onTickRender() {
	    if (mc.currentScreen == null) {
	      GuiIngame gig = mc.ingameGUI;
	      ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	      int i = scaledresolution.getScaledWidth();
	      int k = scaledresolution.getScaledHeight();
	      //mc.getTextureManager().bindTexture(new ResourceLocation(texture));
	      int y = yOff;
	      int x = i - xOff;
	      int off=0;
	      if(getLvl(mc.thePlayer)>=10){
	    	  off+=3;
	      }
	      if(getLvl(mc.thePlayer)>=100){
	    	  off+=3;
	      }
GL11.glPushMatrix();
Minecraft.getMinecraft().fontRenderer.drawString(Taoism.WHITE+getLvl(mc.thePlayer).toString(), x-387-off, (int) (1.33*y+68), 0);
Minecraft.getMinecraft().fontRenderer.drawString(color, x+9, y+14, 0);
//Minecraft.getMinecraft().fontRenderer.drawString(color+getExp(mc.thePlayer).toString(), x, y+21, 0);
	      GL11.glPushMatrix();
//	      GL11.glBlendFunc(770, Integer.valueOf(color));
	      GL11.glScaled(0.3, 0.15, 0.2);
	      mc.getTextureManager().bindTexture(new ResourceLocation("taoism:hud/GenericSkillBar.png"));
	      gig.drawTexturedModalRect(x-400, 9*y+400, 0, 0, 270, 150);
	      //mc.getTextureManager().bindTexture(new ResourceLocation("taoism:items/Zhen.png"));
	      gig.drawTexturedModalRect(x-330, 9*y+400, 90, 128, (int)((this.getExp(mc.thePlayer)/this.getExpNeeded(mc.thePlayer)*160)), 100);//this does not render right
	     // System.out.println(this.getLvl(mc.thePlayer)+" "+(this.getExp(mc.thePlayer)/this.getExpNeeded(mc.thePlayer))*250);
	      //System.out.println(this.getExp(mc.thePlayer));
	      //System.out.println(this.getExpNeeded(mc.thePlayer));
	      //System.out.println();
	      GL11.glPopMatrix();
	      //Minecraft.getMinecraft().fontRenderer.drawString(color+getLvl(mc.thePlayer).toString(), x+9, y+14, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString(color+getExp(mc.thePlayer).toString(), x, y+21, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 15, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 13, 0);
	      //Minecraft.getMinecraft().fontRenderer.drawString("" + soulPowerHelper.getProperties(mc.thePlayer).getBarInt(), x + 10, y + 14, 16777215);
	      GL11.glPopMatrix();
	    }
	  }
}
