package com.Jackiecrazi.taoism.client.render.weapons;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.Jackiecrazi.taoism.client.RenderHalper;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

public class RenderDoubleBigWeapons implements IItemRenderer {
	private float size,interpolation;
	private int rotate;
	public RenderDoubleBigWeapons() {}
	public RenderDoubleBigWeapons(float scale,float hold) {
		this(scale,hold,0);
	}
	public RenderDoubleBigWeapons(float scale,float hold,int side) {
		size=scale;
		interpolation=hold;
		rotate=side;
		
	}
	public RenderDoubleBigWeapons(float scale) {
		this(scale,0);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type==ItemRenderType.EQUIPPED||type==ItemRenderType.EQUIPPED_FIRST_PERSON||type==ItemRenderType.ENTITY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		render((Entity)data[1],item,type);
	}

	private void render(Entity entity, ItemStack stack, ItemRenderType type){
		GL11.glPushMatrix();
		
		/*GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDepthMask(false);
	    GL11.glEnable(GL11.GL_ALPHA_TEST);
	    GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4d(1, 1, 1, 1);*/

		float f = size;
		if (type==ItemRenderType.EQUIPPED_FIRST_PERSON) {
			//renderManyArms((AbstractClientPlayer)entity);
			f=1;
			drawHand((AbstractClientPlayer)entity,0);
			f *= 1.75F;
			GL11.glTranslatef(-0.35F * size, -0.125F * size, 0.0F);
		} else if(type==ItemRenderType.EQUIPPED){
			
			f *= (entity instanceof EntityPlayer ? 2.0F : 1.75F);
			GL11.glRotated(rotate, 1, 0, 0);
			GL11.glTranslatef(1F - f+(interpolation*size), -0.125F * size-(interpolation*size), (0.05F * size)-(rotate/200f));
		}
		else{
			f*=1.75f;
			GL11.glScaled(3, 3, 3);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(-45, 0, 0, 1);
			GL11.glTranslated(-0.5, -1, 0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
		Tessellator tessellator = Tessellator.instance;
		
		
		GL11.glScalef(f, f, 0.4f);
		for(int x=0;x<stack.getItem().getRenderPasses(stack.getItemDamage());x++){
			IIcon icon = stack.getItem().getIcon(stack, x);
			
			//drawL(stack,icon,false);
			
			Color color=new Color(0);
			try{
				color=new Color(((GenericTaoistWeapon)stack.getItem()).getColorFromItemStack(stack, x));
				//System.out.println("peacefully seized color");
			}catch(Exception e){

			}
			draw(tessellator, stack, icon, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}
		/*GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDepthMask(true);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);*/
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, -1.5);
		f = size;
		if (type==ItemRenderType.EQUIPPED_FIRST_PERSON) {
			//renderManyArms((AbstractClientPlayer)entity);
			f=1;
			drawHand((AbstractClientPlayer)entity,0);
			f *= 1.75F;
			GL11.glTranslatef(-0.35F * size, -0.125F * size, 0.0F);
		} else if(type==ItemRenderType.EQUIPPED){
			
			f *= (entity instanceof EntityPlayer ? 2.0F : 1.75F);
			//GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(rotate, 1, 0, 0);
			GL11.glTranslatef(1F - f+(interpolation*size), -0.125F * size-(interpolation*size), (0.05F * size)-(rotate/200f));
			GL11.glTranslatef(0, 0, 2.4f);
		}
		else{
			f*=1.75f;
			GL11.glScaled(3, 3, 3);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(-45, 0, 0, 1);
			GL11.glTranslated(-0.5, -1, 0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
		tessellator = Tessellator.instance;
		
		
		GL11.glScalef(f, f, 0.4f);
		for(int x=0;x<stack.getItem().getRenderPasses(stack.getItemDamage());x++){
			IIcon icon = stack.getItem().getIcon(stack, x);
			
			//drawL(stack,icon,false);
			
			Color color=new Color(0);
			try{
				color=new Color(((GenericTaoistWeapon)stack.getItem()).getColorFromItemStack(stack, x));
				//System.out.println("peacefully seized color");
			}catch(Exception e){

			}
			draw(tessellator, stack, icon, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}
		GL11.glPopMatrix();
	}
	
	
	private static void draw(Tessellator tess, ItemStack stack, IIcon icon, int red, int green, int blue, int alpha){
		Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();
        
        if (icon == null || stack == null) {

            GL11.glPopMatrix();
            return;
        }

        mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(stack.getItemSpriteNumber()));
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (red >= 0 && green >= 0 && blue >= 0){
            GL11.glColor4d(red/255d, green/255d, blue/255d,alpha/255d);//either 0 or 1, the rest are not translucent
            //System.out.println(red/255d);
        }
        //else System.out.println("kek");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
	}
	
	private void drawHand(AbstractClientPlayer equippingPlayer, float ptt){
		
		Render render = RenderManager.instance
				.getEntityRenderObject(equippingPlayer);
		RenderPlayer rend = (RenderPlayer) render;
		GL11.glPushMatrix();
		RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
				.getResourceDomain()
				+ ":"
				+ equippingPlayer.getLocationSkin().getResourcePath());

		/*GL11.glRotated(280, 0, 0, 1);
		GL11.glRotated(30, 1, 0, 1);
		GL11.glRotated(-75, 0, 1, 0);*/
		GL11.glRotated(303, 0, 0, 1);
		GL11.glScaled(1.2, 1.2, 1.2);

		GL11.glTranslated(0.3, -0.3, 0);

		RenderHelper.enableStandardItemLighting();

		//GL11.glScaled(-1, 1, 1);
		if(!equippingPlayer.isInvisible())
		rend.renderFirstPersonArm(equippingPlayer);
		GL11.glPopMatrix();
	}
	
	private void renderManyArms(AbstractClientPlayer equippingPlayer){
		GL11.glPushMatrix();
		RenderPlayer rend = (RenderPlayer) RenderManager.instance
				.getEntityRenderObject(equippingPlayer);
		RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
				.getResourceDomain()
				+ ":"
				+ equippingPlayer.getLocationSkin().getResourcePath());

		GL11.glRotated(90, 0, 1, 0);
		GL11.glPushMatrix();
		//GL11.glRotated(280, 0, 0, 1);
		GL11.glRotated(60, 1, 0, 0);
		GL11.glRotated(-100, 0, 1, 0);
		GL11.glScaled(2, 2, -2);

		GL11.glTranslated(-0.5, -0.85, 0.51);

		RenderHelper.enableStandardItemLighting();

		GL11.glScaled(-1, 1, 1);
		if(!equippingPlayer.isInvisible())
			rend.renderFirstPersonArm(equippingPlayer);
		GL11.glPopMatrix();

		// render left arm
		GL11.glPushMatrix();
		RenderHalper.bindTexture(equippingPlayer.getLocationSkin()
				.getResourceDomain()
				+ ":"
				+ equippingPlayer.getLocationSkin().getResourcePath());

		GL11.glRotated(280, 0, 0, 1);//280
		GL11.glRotated(-60, 1, 0, 0);
		GL11.glRotated(-75, 0, 1, 0);
		GL11.glScaled(2, 2, -2);

		GL11.glTranslated(-1.1, -0.85, -0.51);

		RenderHelper.enableStandardItemLighting();

		GL11.glScaled(-1, 1, 1);
		//if(!equippingPlayer.isInvisible())
			//rend.renderFirstPersonArm(equippingPlayer);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
}
