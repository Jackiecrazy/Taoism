package com.Jackiecrazi.taoism.client.models.entity.mobs;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLingjin extends ModelBase {

ModelRenderer body;
ModelRenderer bozi;
ModelRenderer leftarm;
ModelRenderer rightarm;
ModelRenderer leftleg;
ModelRenderer rightleg;
ModelRenderer noName;
ModelRenderer head;
ModelRenderer leftfoot;
ModelRenderer gightfoot;
ModelRenderer jiao1;
ModelRenderer jiao2;

public ModelLingjin()
{
	
textureWidth = 64;
textureHeight = 32;

body = new ModelRenderer(this, 0, 22);
body.mirror = false;
body.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4);
//body.setInitialRotationPoint(0.0F, -23.0F, 0.0F);
//body.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body.setTextureSize(64, 32);
//parts.put(body.boxName, body);

bozi = new ModelRenderer(this, 27, 0);
bozi.mirror = false;
bozi.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4);
//bozi.setInitialRotationPoint(0.0F, 3.0F, 0.0F);
//bozi.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
bozi.setTextureSize(64, 32);
//parts.put(bozi.boxName, bozi);
body.addChild(bozi);

leftarm = new ModelRenderer(this, 58, 0);
leftarm.mirror = false;
leftarm.addBox(0.0F, -5.0F, -1.0F, 1, 5, 2);
//leftarm.setInitialRotationPoint(3.0F, 2.5F, 0.0F);
//leftarm.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftarm.setTextureSize(64, 32);
//parts.put(leftarm.boxName, leftarm);
body.addChild(leftarm);

rightarm = new ModelRenderer(this, 58, 0);
rightarm.mirror = false;
rightarm.addBox(-1.0F, -5.0F, -1.0F, 1, 5, 2);
//rightarm.setInitialRotationPoint(-3.0F, 2.5F, 0.0F);
//rightarm.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightarm.setTextureSize(64, 32);
//parts.put(rightarm.boxName, rightarm);
body.addChild(rightarm);

leftleg = new ModelRenderer(this, 53, 0);
leftleg.mirror = false;
leftleg.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1);
//leftleg.setInitialRotationPoint(1.5F, -3.0F, 0.0F);
//leftleg.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftleg.setTextureSize(64, 32);
//parts.put(leftleg.boxName, leftleg);
body.addChild(leftleg);

rightleg = new ModelRenderer(this, 53, 0);
rightleg.mirror = false;
rightleg.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1);
//rightleg.setInitialRotationPoint(-1.5F, -3.0F, 0.0F);
//rightleg.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightleg.setTextureSize(64, 32);
//parts.put(rightleg.boxName, rightleg);
body.addChild(rightleg);

noName = new ModelRenderer(this, 0, 18);
noName.mirror = false;
noName.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 1);
//noName.setInitialRotationPoint(0.0F, 1.5F, -1.5F);
//noName.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
noName.setTextureSize(64, 32);
//parts.put(noName.boxName, noName);
body.addChild(noName);

head = new ModelRenderer(this, 0, 0);
head.mirror = false;
head.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
//head.setInitialRotationPoint(0.0F, 4.0F, 0.0F);
//head.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
head.setTextureSize(64, 32);
//parts.put(head.boxName, head);
bozi.addChild(head);

leftfoot = new ModelRenderer(this, 44, 0);
leftfoot.mirror = false;
leftfoot.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2);
//leftfoot.setInitialRotationPoint(0.0F, -4.0F, 0.0F);
//leftfoot.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftfoot.setTextureSize(64, 32);
//parts.put(leftfoot.boxName, leftfoot);
leftleg.addChild(leftfoot);

gightfoot = new ModelRenderer(this, 44, 0);
gightfoot.mirror = false;
gightfoot.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2);
//gightfoot.setInitialRotationPoint(0.0F, -4.0F, 0.0F);
//gightfoot.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
gightfoot.setTextureSize(64, 32);
//parts.put(gightfoot.boxName, gightfoot);
rightleg.addChild(gightfoot);

jiao1 = new ModelRenderer(this, 0, 0);
jiao1.mirror = false;
jiao1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
//jiao1.setInitialRotationPoint(1.0F, 3.0F, -2.0F);
//jiao1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25F, -0.0669873F, -0.25F, 0.93301266F)).transpose());
jiao1.setTextureSize(64, 32);
//parts.put(jiao1.boxName, jiao1);
head.addChild(jiao1);

jiao2 = new ModelRenderer(this, 0, 0);
jiao2.mirror = false;
jiao2.addBox(-1.0F, 0.0F, 0.0F, 1, 3, 1);
//jiao2.setInitialRotationPoint(-1.0F, 3.0F, -2.0F);
//jiao2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25F, 0.0669873F, 0.25F, 0.93301266F)).transpose());
jiao2.setTextureSize(64, 32);
//parts.put(jiao2.boxName, jiao2);
head.addChild(jiao2);

}

@Override
public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) 
{

//Render every non-child part
body.render(par7);
}
@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {}

}