package com.Jackiecrazi.taoism.client.models.items;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModelScroll extends ModelBase {
private static final int LENGTH = 2;

private static final int HEIGHT = 16;

private static final int THICKNESS = 1;

public HashMap<String, ModelRenderer> parts = new HashMap<String, ModelRenderer>();

ModelRenderer scroll1;
ModelRenderer scroll2;
ModelRenderer scroll3;
ModelRenderer scroll4;
ModelRenderer scroll5;
ModelRenderer scroll6;
ModelRenderer scroll7;
ModelRenderer scroll8;
ModelRenderer scroll9;
ModelRenderer scroll10;
ModelRenderer scroll11;
ModelRenderer scroll12;
ModelRenderer scroll13;
ModelRenderer scroll14;
ModelRenderer scroll15;
ModelRenderer scroll16;
ModelRenderer scroll17;
ModelRenderer scroll18;
ModelRenderer scroll19;
ModelRenderer scroll20;
ModelRenderer scroll21;
ModelRenderer scroll22;
ModelRenderer scroll23;
ModelRenderer scroll24;

public ModelScroll()
{

textureWidth = HEIGHT;
textureHeight = HEIGHT;

scroll1 = new ModelRenderer(this, 0, 0);
scroll1.mirror = false;
scroll1.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll1.setRotationPoint(-11.0F, -8.0F, 0.0F);
scroll1.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll1.boxName, scroll1);

scroll2 = new ModelRenderer(this, 0, 0);
scroll2.mirror = false;
scroll2.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll2.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll2.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll2.boxName, scroll2);
scroll1.addChild(scroll2);

scroll3 = new ModelRenderer(this, 0, 0);
scroll3.mirror = false;
scroll3.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll3.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll3.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll3.boxName, scroll3);
scroll2.addChild(scroll3);

scroll4 = new ModelRenderer(this, 0, 0);
scroll4.mirror = false;
scroll4.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll4.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll4.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll4.boxName, scroll4);
scroll3.addChild(scroll4);

scroll5 = new ModelRenderer(this, 0, 0);
scroll5.mirror = false;
scroll5.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll5.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll5.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll5.boxName, scroll5);
scroll4.addChild(scroll5);

scroll6 = new ModelRenderer(this, 0, 0);
scroll6.mirror = false;
scroll6.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll6.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll6.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll6.boxName, scroll6);
scroll5.addChild(scroll6);

scroll7 = new ModelRenderer(this, 0, 0);
scroll7.mirror = false;
scroll7.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll7.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll7.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll7.boxName, scroll7);
scroll6.addChild(scroll7);

scroll8 = new ModelRenderer(this, 0, 0);
scroll8.mirror = false;
scroll8.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll8.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll8.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll8.boxName, scroll8);
scroll7.addChild(scroll8);

scroll9 = new ModelRenderer(this, 0, 0);
scroll9.mirror = false;
scroll9.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll9.setRotationPoint(1.0F, 0.0F, 0.0F);
scroll9.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll9.boxName, scroll9);
scroll8.addChild(scroll9);

scroll10 = new ModelRenderer(this, 0, 0);
scroll10.mirror = false;
scroll10.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll10.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll10. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll10.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll10.boxName, scroll10);
scroll9.addChild(scroll10);

scroll11 = new ModelRenderer(this, 0, 0);
scroll11.mirror = false;
scroll11.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll11.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll11. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll11.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll11.boxName, scroll11);
scroll10.addChild(scroll11);

scroll12 = new ModelRenderer(this, 0, 0);
scroll12.mirror = false;
scroll12.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll12.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll12. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll12.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll12.boxName, scroll12);
scroll11.addChild(scroll12);

scroll13 = new ModelRenderer(this, 0, 0);
scroll13.mirror = false;
scroll13.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll13.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll13. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll13.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll13.boxName, scroll13);
scroll12.addChild(scroll13);

scroll14 = new ModelRenderer(this, 0, 0);
scroll14.mirror = false;
scroll14.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll14.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll14. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll14.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll14.boxName, scroll14);
scroll13.addChild(scroll14);

scroll15 = new ModelRenderer(this, 0, 0);
scroll15.mirror = false;
scroll15.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll15.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll15. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll15.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll15.boxName, scroll15);
scroll14.addChild(scroll15);

scroll16 = new ModelRenderer(this, 0, 0);
scroll16.mirror = false;
scroll16.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll16.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll16. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll16.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll16.boxName, scroll16);
scroll15.addChild(scroll16);

scroll17 = new ModelRenderer(this, 0, 0);
scroll17.mirror = false;
scroll17.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll17.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll17. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll17.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll17.boxName, scroll17);
scroll16.addChild(scroll17);

scroll18 = new ModelRenderer(this, 0, 0);
scroll18.mirror = false;
scroll18.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll18.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll18. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll18.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll18.boxName, scroll18);
scroll17.addChild(scroll18);

scroll19 = new ModelRenderer(this, 0, 0);
scroll19.mirror = false;
scroll19.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll19.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll19. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll19.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll19.boxName, scroll19);
scroll18.addChild(scroll19);

scroll20 = new ModelRenderer(this, 0, 0);
scroll20.mirror = false;
scroll20.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll20.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll20. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll20.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll20.boxName, scroll20);
scroll19.addChild(scroll20);

scroll21 = new ModelRenderer(this, 0, 0);
scroll21.mirror = false;
scroll21.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll21.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll21. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll21.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll21.boxName, scroll21);
scroll20.addChild(scroll21);

scroll22 = new ModelRenderer(this, 0, 0);
scroll22.mirror = false;
scroll22.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll22.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll22. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll22.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll22.boxName, scroll22);
scroll21.addChild(scroll22);

scroll23 = new ModelRenderer(this, 0, 0);
scroll23.mirror = false;
scroll23.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll23.setRotationPoint(1.0F, 0.0F, 0.0F);
//setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll23.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll23.boxName, scroll23);
scroll22.addChild(scroll23);

scroll24 = new ModelRenderer(this, 0, 0);
scroll24.mirror = false;
scroll24.addBox(0.0F, 0.0F, 0.0F, LENGTH, HEIGHT, THICKNESS);
scroll24.setRotationPoint(1.0F, 0.0F, 0.0F);
//scroll24. setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
scroll24.setTextureSize(HEIGHT, HEIGHT);
parts.put(scroll24.boxName, scroll24);
scroll23.addChild(scroll24);

}

public void render(ItemStack is)
{
	if (RenderManager.instance.renderEngine == null) return;
    Item wand = is.getItem();
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    GL11.glScaled(1, 1, 1);
    GL11.glRotated(0, 0, 0, 0);
    scroll2.rotateAngleY=NeedyLittleThings.rad(10);
    scroll2.rotateAngleX=0;
    scroll2.rotateAngleZ=0;
    scroll1.render(0.0625f);
}
/***
 * I should really make a fancier method based on taking the derivative of an archimedean spiral in cartesian coords.
 * Or maybe not.
 */
@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
	
}

public ModelRenderer getModelRendererFromName(String name)
{
return parts.get(name);
}
}