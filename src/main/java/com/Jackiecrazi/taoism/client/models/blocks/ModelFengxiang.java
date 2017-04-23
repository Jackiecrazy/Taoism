package com.Jackiecrazi.taoism.client.models.blocks;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.client.MCAClientLibrary.MCAModelRenderer;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.MCAVersionChecker;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Matrix4f;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.block.tile.TileBellows;

public class ModelFengxiang extends ModelBase {
	public final int MCA_MIN_REQUESTED_VERSION = 5;
	public HashMap<String, MCAModelRenderer> parts = new HashMap<String, MCAModelRenderer>();

	MCAModelRenderer xiangjiao1;
	MCAModelRenderer xiangjiao2;
	MCAModelRenderer xiangjiao3;
	MCAModelRenderer xiangjiao4;
	MCAModelRenderer xiangti1;
	MCAModelRenderer xiangti2;
	MCAModelRenderer xiangba1;
	MCAModelRenderer xiaxiangba11;
	MCAModelRenderer xiaxiangba12;

	public ModelFengxiang()
	{
		MCAVersionChecker.checkForLibraryVersion(getClass(), MCA_MIN_REQUESTED_VERSION);

		textureWidth = 128;
		textureHeight = 128;

		xiangjiao1 = new MCAModelRenderer(this, "xiangjiao1", 40, 0);
		xiangjiao1.mirror = false;
		xiangjiao1.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6);
		xiangjiao1.setInitialRotationPoint(5.0F, -19.0F, -2.0F);
		xiangjiao1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangjiao1.setTextureSize(128, 128);
		parts.put(xiangjiao1.boxName, xiangjiao1);

		xiangjiao2 = new MCAModelRenderer(this, "xiangjiao2", 40, 0);
		xiangjiao2.mirror = false;
		xiangjiao2.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6);
		xiangjiao2.setInitialRotationPoint(-4.0F, -19.0F, -2.0F);
		xiangjiao2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangjiao2.setTextureSize(128, 128);
		parts.put(xiangjiao2.boxName, xiangjiao2);

		xiangjiao3 = new MCAModelRenderer(this, "xiangjiao3", 40, 0);
		xiangjiao3.mirror = false;
		xiangjiao3.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6);
		xiangjiao3.setInitialRotationPoint(-4.0F, -19.0F, -18.0F);
		xiangjiao3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangjiao3.setTextureSize(128, 128);
		parts.put(xiangjiao3.boxName, xiangjiao3);

		xiangjiao4 = new MCAModelRenderer(this, "xiangjiao4", 40, 0);
		xiangjiao4.mirror = false;
		xiangjiao4.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6);
		xiangjiao4.setInitialRotationPoint(5.0F, -19.0F, -18.0F);
		xiangjiao4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangjiao4.setTextureSize(128, 128);
		parts.put(xiangjiao4.boxName, xiangjiao4);

		xiangti1 = new MCAModelRenderer(this, "xiangti1", 40, 77);
		xiangti1.mirror = false;
		xiangti1.addBox(-8.0F, -12.0F, -26.0F, 15, 24, 26);
		xiangti1.setInitialRotationPoint(1.0F, -7.0F, 4.0F);
		xiangti1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangti1.setTextureSize(128, 128);
		parts.put(xiangti1.boxName, xiangti1);

		xiangti2 = new MCAModelRenderer(this, "xiangti2", 0, 0);
		xiangti2.mirror = false;
		xiangti2.addBox(0.0F, -3.0F, -3.0F, 1, 6, 6);
		xiangti2.setInitialRotationPoint(-8.0F, -8.0F, -6.0F);
		xiangti2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangti2.setTextureSize(128, 128);
		parts.put(xiangti2.boxName, xiangti2);

		xiangba1 = new MCAModelRenderer(this, "xiangba1", 100, 0);
		xiangba1.mirror = false;
		xiangba1.addBox(-2.0F, -20.0F, -2.0F, 4, 20, 4);
		xiangba1.setInitialRotationPoint(0.0F, 3.0F, 9.0F);
		xiangba1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiangba1.setTextureSize(128, 128);
		parts.put(xiangba1.boxName, xiangba1);

		xiaxiangba11 = new MCAModelRenderer(this, "xiaxiangba11", 0, 0);
		xiaxiangba11.mirror = false;
		xiaxiangba11.addBox(-1.0F, -1.0F, -20.0F, 2, 2, 20);
		xiaxiangba11.setInitialRotationPoint(0.0F, -5.0F, 0.0F);
		xiaxiangba11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiaxiangba11.setTextureSize(128, 128);
		parts.put(xiaxiangba11.boxName, xiaxiangba11);
		xiangba1.addChild(xiaxiangba11);

		xiaxiangba12 = new MCAModelRenderer(this, "xiaxiangba12", 0, 0);
		xiaxiangba12.mirror = false;
		xiaxiangba12.addBox(-1.0F, -1.0F, -20.0F, 2, 2, 20);
		xiaxiangba12.setInitialRotationPoint(0.0F, -16.0F, 0.0F);
		xiaxiangba12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
		xiaxiangba12.setTextureSize(128, 128);
		parts.put(xiaxiangba12.boxName, xiaxiangba12);
		xiangba1.addChild(xiaxiangba12);

	}

	public void render(TileBellows te, float par7,float partialTick) 
	{

		//Render every non-child part
		xiangjiao1.render(par7);
		xiangjiao2.render(par7);
		xiangjiao3.render(par7);
		xiangjiao4.render(par7);
		xiangti1.render(par7);
		xiangti2.render(par7);
		if(te.getWind()!=0)
		xiangba1.offsetZ=0.5F*(float) Math.sin(-12*Math.toRadians(te.getWind()-partialTick))+0.4f-0.1f;
		xiangba1.render(par7);
	}
	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {}

	public MCAModelRenderer getModelRendererFromName(String name)
	{
		return parts.get(name);
	}
}