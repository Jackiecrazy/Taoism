package com.Jackiecrazi.taoism.client.models.entity.literaldummies;
/*package com.Jackiecrazi.taoism.entity.models.literaldummies;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.Jackiecrazi.taoism.entity.literaldummies.EntitySandbag;

public class ModelSandbag extends ModelBase{
public ModelRenderer strap;
public ModelRenderer bag;
public int textureWidth=64;
public int textureHeight=64;
public ModelSandbag(){
	strap=new ModelRenderer(this,0,0).addBox(0, 15, 0, 1, 17, 1);
	
	bag=new ModelRenderer(this,0,0);
	
	strap.setTextureSize(textureWidth, textureHeight);
	strap.setRotationPoint(0, 0, 0);
	strap.addChild(bag);
	bag.addBox(0, -29, 0, 17, 32, 17);
}
@Override
public void render(Entity parEntity, float parTime, float parSwingSuppress, 
      float par4, float parHeadAngleY, float parHeadAngleX, float par7)
{
    // best to cast to actual expected entity, to allow access to custom fields 
    // related to animation
    renderBag((EntitySandbag) parEntity, parTime, parSwingSuppress, par4, 
          parHeadAngleY, parHeadAngleX, par7);
}
public void renderBag(EntitySandbag parEntity, float parTime, float parSwingSuppress, 
        float par4, float parHeadAngleY, float parHeadAngleX, float par7)
  {
      setRotationAngles(parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, 
            par7, parEntity);
      GL11.glPushMatrix();
strap.render(par7);

      // don't forget to pop the matrix for overall scaling
      GL11.glPopMatrix();
  }
}
*/