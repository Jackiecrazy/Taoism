package com.Jackiecrazi.taoism.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CustomHandModel extends ModelBase {
	
	public ModelRenderer customRightArm;
    public ModelRenderer customLeftArm;
    public ModelRenderer bodyAxis;
    public CustomHandModel(){
    	this.bodyAxis=new ModelRenderer(this,0,0);
    	this.bodyAxis.addBox(-4.5F, -1.0F, 0.0F, 8, 2, 4);
    	this.bodyAxis.setRotationPoint(-4.0F, 0.0F, -2.0F);
    	this.customRightArm = new ModelRenderer(this, 40, 16);
        this.customRightArm.addBox(-4.0F, -11.0F, -2.0F, 4, 12, 4);
        this.customRightArm.setRotationPoint(-4.5F, 0.0F, 2.0F);
        this.customLeftArm = new ModelRenderer(this, 40, 16);
        this.customLeftArm.mirror = true;
        this.customLeftArm.addBox(0.0F, -11.0F, -2.0F, 4, 12, 4);
        this.customLeftArm.setRotationPoint(3.5F, 0.0F, 2.0F);
        this.bodyAxis.addChild(customLeftArm);
        this.bodyAxis.addChild(customRightArm);
    }
    public void render(Entity e, float time, float swingSuppress, float f4, float headingAngleY, float headingAngleX, float f7)
    {
    	EntityPlayer p=(EntityPlayer)e;
    	this.bodyAxis.rotateAngleY=p.rotationYaw;
    	this.bodyAxis.isHidden=false;
    	this.bodyAxis.render(time);
    }
}
