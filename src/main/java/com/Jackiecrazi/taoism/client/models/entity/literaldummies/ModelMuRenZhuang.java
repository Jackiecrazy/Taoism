package com.Jackiecrazi.taoism.client.models.entity.literaldummies;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;

public class ModelMuRenZhuang extends ModelBase {

ModelRenderer body;
ModelRenderer arm1;
ModelRenderer arm2;
ModelRenderer arm3;
ModelRenderer legUpper;
ModelRenderer base;
ModelRenderer legLower;

public ModelMuRenZhuang()
{

textureWidth = 64;
textureHeight = 64;

body = new ModelRenderer(this, 0, 0);
body.mirror = false;
body.addBox(0.0F, 0.0F, 0.0F, 8, 32, 8);
body.setRotationPoint(0.0F, 0.0F, 0.0F);
body.offsetX=-0.25F;
body.offsetZ=-0.25F;
//body.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body.setTextureSize(64, 64);

arm1 = new ModelRenderer(this, 0, 40);
arm1.mirror = false;
arm1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 11);
arm1.setRotationPoint(4.0F, 22.0F, 6.0F);
//arm1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.09584575F, 0.0F, 0.9953962F)).transpose());
arm1.setTextureSize(64, 64);
//arm1.setTextureOffset(30, 50);
body.addChild(arm1);

arm2 = new ModelRenderer(this, 0, 40);
arm2.mirror = false;
arm2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 11);
arm2.setRotationPoint(2.0F, 24.0F, 5.0F);
//arm2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.09584575F, 0.0F, 0.9953962F)).transpose());
arm2.setTextureSize(64, 64);
//arm2.setTextureOffset(10, 40);
body.addChild(arm2);

arm3 = new ModelRenderer(this, 0, 40);
arm3.mirror = false;
arm3.addBox(0.0F, 0.0F, 0.0F, 2, 2, 10);
arm3.setRotationPoint(3.0F, 16.0F, 3.0F);
//arm3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
arm3.setTextureSize(64, 64);
body.addChild(arm3);

legUpper = new ModelRenderer(this, 0, 0);
legUpper.mirror = false;
legUpper.addBox(-1.0F, -2.0F, -4.0F, 2, 2, 4);
legUpper.setRotationPoint(4.0F, 8.0F, 12.0F);
//legUpper.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
legUpper.setTextureSize(64, 64);
body.addChild(legUpper);

base = new ModelRenderer(this, 0, 0);
base.mirror = false;
base.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
base.setRotationPoint(-4.0F, 0.0F, -4.0F);
//noName.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
base.setTextureSize(64, 64);
body.addChild(base);

legLower = new ModelRenderer(this, 0, 0);
legLower.mirror = false;
legLower.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 7);
legLower.setRotationPoint(0.0F, 0.0F, 0.0F);
//noName.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.43051112F, 0.0F, 0.0F, 0.90258527F)).transpose());
legLower.setTextureSize(64, 64);
legUpper.addChild(legLower);

}

@Override
public void render(Entity par1Entity, float parTime,
		float parSwingSuppress, float par4, float parHeadAngleY,
		float parHeadAngleX, float par7) 
{
EntityMuRenZhuang entity = (EntityMuRenZhuang)par1Entity;

setRotationAngles(parTime, parSwingSuppress, par4, parHeadAngleY,
		parHeadAngleX, par7, entity);

//Render every non-child part
body.render(par7);
}
@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
	legLower.rotateAngleX=0.90258527F;
	arm1.rotateAngleX=0.09584575F;
	arm1.rotateAngleY=0.5F;
	arm2.rotateAngleY=-0.5F;
}

}