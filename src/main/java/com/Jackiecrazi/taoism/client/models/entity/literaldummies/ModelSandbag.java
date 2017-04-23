package com.Jackiecrazi.taoism.client.models.entity.literaldummies;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;

public class ModelSandbag extends ModelBase {
	ModelRenderer base;
	ModelRenderer strap;
	ModelRenderer bag;

	public ModelSandbag() {

		textureWidth = 64;
		textureHeight = 64;
		base = new ModelRenderer(this, 0, 0);
		base.mirror = false;
		base.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
		base.setRotationPoint(0.0F, 16.0F, 0.0F);
		base.offsetY=1.5F;
		//base.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		base.setTextureSize(64, 64);

		strap = new ModelRenderer(this, 0, 0);
		strap.mirror = false;
		strap.addBox(0.0F, 0.0F, 0.0F, 1, 15, 1);
		strap.setRotationPoint(0.0F, -15.0F, 0.0F);
		//strap.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		strap.setTextureSize(64, 64);
		base.addChild(strap);

		bag = new ModelRenderer(this, 0, 0);
		bag.mirror = false;
		bag.addBox(-8.0F, 0.0F, -8.0F, 17, 32, 17);
		bag.setRotationPoint(0.0F, -29.0F, 0.0F);
		//bag.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		bag.setTextureSize(64, 64);
		strap.addChild(bag);


	}

	@Override
	public void render(Entity par1Entity, float parTime,
			float parSwingSuppress, float par4, float parHeadAngleY,
			float parHeadAngleX, float par7) {
		EntitySandbag entity = (EntitySandbag) par1Entity;
		setRotationAngles(parTime, parSwingSuppress, par4, parHeadAngleY,
				parHeadAngleX, par7, entity);
		base.render(par7);
		/*base.rotateAngleX+=degToRad((float) (entity.getDir().zCoord));
		base.rotateAngleZ+=degToRad((float) (entity.getDir().xCoord));
		if(entity.getDir().xCoord==0&&base.rotateAngleZ!=0){
			base.rotateAngleZ-=base.rotateAngleZ<0?-0.1;
		}
if(entity.getDir().zCoord==0&&base.rotateAngleX!=0){
			
		}*/
		//TODO reimplement when you actually know what you're doing
	}

	@Override
public void setRotationAngles(float parTime, float parSwingSuppress, float par3, float parHeadAngleY, float parHeadAngleX, float par6, Entity parEntity)
{
    // animate if moving     
    //bag.rotateAngleX;
    //bag.rotateAngleY;
    //bag.rotateAngleZ;
}
	protected void spinX(ModelRenderer model)
    {
        model.rotateAngleX += degToRad(0.5F);
        //up and down circle
    }
    
    protected void spinY(ModelRenderer model)
    {
        model.rotateAngleY += degToRad(0.5F);
        //rotates around
    }
    
    protected void spinZ(ModelRenderer model)
    {
        model.rotateAngleZ += degToRad(0.5F);
    }
	protected float degToRad(float degrees) {
		return degrees * (float) Math.PI / 180;
	}
}