// Date: 2017/2/8 15:19:32
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX






package com.Jackiecrazi.taoism.client.models.items.weapons.ji;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class ModelJiTwo extends ModelBase
{
  //fields
    ModelRenderer jian15;
    ModelRenderer jian13;
    ModelRenderer jian11;
    ModelRenderer jige1;
    ModelRenderer jian12;
    ModelRenderer jian14;
    ModelRenderer jian21;
    ModelRenderer jian23;
    ModelRenderer jige2;
    ModelRenderer jige3;
    ModelRenderer jian22;
    ModelRenderer jian24;
    ModelRenderer jiduan;
    ModelRenderer jibing;
  
  public ModelJiTwo()
  {
    textureWidth = 64;
    textureHeight = 128;
    
      jian15 = new ModelRenderer(this, 18, 0);
      jian15.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
      jian15.setRotationPoint(0F, -78F, 0F);
      jian15.setTextureSize(64, 128);
      jian15.mirror = true;
      setRotation(jian15, 0F, 0F, 0.7853982F);
      jian13 = new ModelRenderer(this, 23, 0);
      jian13.addBox(0F, -2F, -0.5F, 2, 2, 1);
      jian13.setRotationPoint(-1.9F, -76.8F, 0F);
      jian13.setTextureSize(64, 128);
      jian13.mirror = true;
      setRotation(jian13, 0F, 0F, 0.7853982F);
      jian11 = new ModelRenderer(this, 30, 0);
      jian11.addBox(-2F, -8F, -0.5F, 2, 8, 1);
      jian11.setRotationPoint(0.5F, -69F, 0F);
      jian11.setTextureSize(64, 128);
      jian11.mirror = true;
      setRotation(jian11, 0F, 0F, -0.0523599F);
      jige1 = new ModelRenderer(this, 50, 0);
      jige1.addBox(-2F, 0F, -1.5F, 4, 2, 3);
      jige1.setRotationPoint(0F, -69F, 0F);
      jige1.setTextureSize(64, 128);
      jige1.mirror = true;
      setRotation(jige1, 0F, 0F, 0F);
      jian12 = new ModelRenderer(this, 30, 0);
      jian12.addBox(0F, -8F, -0.5F, 2, 8, 1);
      jian12.setRotationPoint(-0.5F, -69F, 0F);
      jian12.setTextureSize(64, 128);
      jian12.mirror = true;
      setRotation(jian12, 0F, 0F, 0.0523599F);
      jian14 = new ModelRenderer(this, 23, 0);
      jian14.addBox(-2F, -2F, -0.5F, 2, 2, 1);
      jian14.setRotationPoint(1.9F, -76.8F, 0F);
      jian14.setTextureSize(64, 128);
      jian14.mirror = true;
      setRotation(jian14, 0F, 0F, -0.7853982F);
      jian21 = new ModelRenderer(this, 38, 0);
      jian21.addBox(0F, -4.6F, -0.5F, 2, 8, 1);
      jian21.setRotationPoint(3F, -55F, 0F);
      jian21.setTextureSize(64, 128);
      jian21.mirror = true;
      setRotation(jian21, 0F, 0F, -0.1745329F);
      jian23 = new ModelRenderer(this, 45, 0);
      jian23.addBox(0F, 0F, -0.5F, 1, 3, 1);
      jian23.setRotationPoint(3.6F, -51.7F, 0F);
      jian23.setTextureSize(64, 128);
      jian23.mirror = true;
      setRotation(jian23, 0F, 0F, -1.22173F);
      jige2 = new ModelRenderer(this, 0, 29);
      jige2.addBox(-0.5F, 0F, -0.5F, 4, 2, 1);
      jige2.setRotationPoint(0F, -65F, 0F);
      jige2.setTextureSize(64, 128);
      jige2.mirror = true;
      setRotation(jige2, 0F, 0F, 0F);
      jige3 = new ModelRenderer(this, 0, 29);
      jige3.addBox(-0.5F, 0F, -0.5F, 4, 2, 1);
      jige3.setRotationPoint(0F, -56F, 0F);
      jige3.setTextureSize(64, 128);
      jige3.mirror = true;
      setRotation(jige3, 0F, 0F, 0F);
      jian22 = new ModelRenderer(this, 38, 0);
      jian22.addBox(0F, -3.4F, -0.5F, 2, 8, 1);
      jian22.setRotationPoint(3F, -64F, 0F);
      jian22.setTextureSize(64, 128);
      jian22.mirror = true;
      setRotation(jian22, 0F, 0F, 0.1745329F);
      jian24 = new ModelRenderer(this, 45, 0);
      jian24.addBox(-1F, 0F, -0.5F, 1, 3, 1);
      jian24.setRotationPoint(3.6F, -67.3F, 0F);
      jian24.setTextureSize(64, 128);
      jian24.mirror = true;
      setRotation(jian24, 0F, 0F, -1.919862F);
      jiduan = new ModelRenderer(this, 0, 122);
      jiduan.addBox(-1.5F, 0F, -1.5F, 3, 3, 3);
      jiduan.setRotationPoint(0F, -27F, 0F);
      jiduan.setTextureSize(64, 128);
      jiduan.mirror = true;
      setRotation(jiduan, 0F, 0F, 0F);
      jibing = new ModelRenderer(this, 0, 32);
      jibing.addBox(-1F, 0F, -1F, 2, 40, 2);
      jibing.setRotationPoint(0F, -67F, 0F);
      jibing.setTextureSize(64, 128);
      jibing.mirror = true;
      setRotation(jibing, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    //setRotationAngles(f, f1, f2, f3, f4, f5);
    jian15.render(f5);
    jian13.render(f5);
    jian11.render(f5);
    jige1.render(f5);
    jian12.render(f5);
    jian14.render(f5);
    jian21.render(f5);
    jian23.render(f5);
    jige2.render(f5);
    jige3.render(f5);
    jian22.render(f5);
    jian24.render(f5);
    jiduan.render(f5);
    jibing.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
 
  public void render(ItemStack is)
  {
  	if (RenderManager.instance.renderEngine == null) return;
      Item wand = is.getItem();
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      GL11.glScaled(0.6, 0.6, 0.6);
      GL11.glRotated(30, 0, 0, 1);
      GL11.glTranslated(1.2, 2.8d, 0);
      float f5=0.0625f;
      jian15.render(f5);
      jian13.render(f5);
      jian11.render(f5);
      jige1.render(f5);
      jian12.render(f5);
      jian14.render(f5);
      jian21.render(f5);
      jian23.render(f5);
      jige2.render(f5);
      jige3.render(f5);
      jian22.render(f5);
      jian24.render(f5);
      jiduan.render(f5);
      jibing.render(f5);
    }
}
