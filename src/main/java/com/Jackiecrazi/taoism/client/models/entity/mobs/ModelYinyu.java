package com.Jackiecrazi.taoism.client.models.entity.mobs;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.client.MCAClientLibrary.MCAModelRenderer;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.MCAVersionChecker;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Matrix4f;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLuoYu;

public class ModelYinyu extends ModelBase {
public final int MCA_MIN_REQUESTED_VERSION = 5;
public HashMap<String, MCAModelRenderer> parts = new HashMap<String, MCAModelRenderer>();

MCAModelRenderer body11;
MCAModelRenderer head1;
MCAModelRenderer body12;
MCAModelRenderer body22;
MCAModelRenderer yigu11;
MCAModelRenderer yugu21;
MCAModelRenderer yuqi1;
MCAModelRenderer yuqi2;
MCAModelRenderer yuqi3;
MCAModelRenderer body33;
MCAModelRenderer yigu12;
MCAModelRenderer yumao11;
MCAModelRenderer yumao12;
MCAModelRenderer yumso13;
MCAModelRenderer yigu22;
MCAModelRenderer yumao22;
MCAModelRenderer yumso23;
MCAModelRenderer yumao21;
MCAModelRenderer wei111;
MCAModelRenderer wei112;
MCAModelRenderer wei131;

public ModelYinyu()
{
MCAVersionChecker.checkForLibraryVersion(getClass(), MCA_MIN_REQUESTED_VERSION);

textureWidth = 64;
textureHeight = 128;

body11 = new MCAModelRenderer(this, "body11", 0, 96);
body11.mirror = false;
body11.addBox(-3.0F, -4.0F, -8.0F, 6, 8, 8);
body11.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
body11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body11.setTextureSize(64, 128);
parts.put(body11.boxName, body11);

head1 = new MCAModelRenderer(this, "head1", 44, 0);
head1.mirror = false;
head1.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 4);
head1.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
head1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
head1.setTextureSize(64, 128);
parts.put(head1.boxName, head1);
body11.addChild(head1);

body12 = new MCAModelRenderer(this, "body12", 0, 112);
body12.mirror = false;
body12.addBox(-4.0F, -3.0F, -10.0F, 8, 6, 10);
body12.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
body12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body12.setTextureSize(64, 128);
parts.put(body12.boxName, body12);
body11.addChild(body12);

body22 = new MCAModelRenderer(this, "body22", 0, 84);
body22.mirror = false;
body22.addBox(-2.5F, -2.5F, -7.0F, 5, 5, 7);
body22.setInitialRotationPoint(0.0F, 0.0F, -9.0F);
body22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body22.setTextureSize(64, 128);
parts.put(body22.boxName, body22);
body12.addChild(body22);

yigu11 = new MCAModelRenderer(this, "yigu11", 0, 46);
yigu11.mirror = false;
yigu11.addBox(0.0F, -1.0F, -2.0F, 7, 2, 2);
yigu11.setInitialRotationPoint(3.0F, 0.0F, -1.0F);
yigu11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(1.3510566E-18F, 0.08715574F, 1.544265E-17F, 0.9961947F)).transpose());
yigu11.setTextureSize(64, 128);
parts.put(yigu11.boxName, yigu11);
body12.addChild(yigu11);

yugu21 = new MCAModelRenderer(this, "yugu21", 0, 46);
yugu21.mirror = false;
yugu21.addBox(-7.0F, -1.0F, -2.0F, 7, 2, 2);
yugu21.setInitialRotationPoint(-3.0F, 0.0F, -1.0F);
yugu21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F)).transpose());
yugu21.setTextureSize(64, 128);
parts.put(yugu21.boxName, yugu21);
body12.addChild(yugu21);

yuqi1 = new MCAModelRenderer(this, "yuqi1", 0, 0);
yuqi1.mirror = false;
yuqi1.addBox(-0.5F, -2.0F, -3.0F, 1, 2, 3);
yuqi1.setInitialRotationPoint(0.0F, 4.0F, 0.0F);
yuqi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.5F, 0.0F, 0.0F, 0.8660254F)).transpose());
yuqi1.setTextureSize(64, 128);
parts.put(yuqi1.boxName, yuqi1);
body12.addChild(yuqi1);

yuqi2 = new MCAModelRenderer(this, "yuqi2", 10, 0);
yuqi2.mirror = false;
yuqi2.addBox(-0.5F, -2.0F, -5.0F, 1, 2, 5);
yuqi2.setInitialRotationPoint(0.0F, 4.0F, -3.0F);
yuqi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.34202012F, 0.0F, 0.0F, 0.9396926F)).transpose());
yuqi2.setTextureSize(64, 128);
parts.put(yuqi2.boxName, yuqi2);
body12.addChild(yuqi2);

yuqi3 = new MCAModelRenderer(this, "yuqi3", 25, 0);
yuqi3.mirror = false;
yuqi3.addBox(-0.5F, -2.0F, -7.0F, 1, 2, 7);
yuqi3.setInitialRotationPoint(0.0F, 4.0F, -7.0F);
yuqi3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
yuqi3.setTextureSize(64, 128);
parts.put(yuqi3.boxName, yuqi3);
body12.addChild(yuqi3);

body33 = new MCAModelRenderer(this, "body33", 0, 73);
body33.mirror = false;
body33.addBox(-1.5F, -3.0F, -5.0F, 3, 6, 5);
body33.setInitialRotationPoint(0.0F, 0.0F, -6.0F);
body33.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body33.setTextureSize(64, 128);
parts.put(body33.boxName, body33);
body22.addChild(body33);

yigu12 = new MCAModelRenderer(this, "yigu12", 0, 42);
yigu12.mirror = false;
yigu12.addBox(0.0F, -1.0F, -2.0F, 5, 2, 2);
yigu12.setInitialRotationPoint(7.0F, 0.0F, 0.0F);
yigu12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.25881904F, 0.0F, 0.9659258F)).transpose());
yigu12.setTextureSize(64, 128);
parts.put(yigu12.boxName, yigu12);
yigu11.addChild(yigu12);

yumao11 = new MCAModelRenderer(this, "yumao11", 0, 33);
yumao11.mirror = false;
yumao11.addBox(0.0F, 0.0F, -8.0F, 2, 1, 8);
yumao11.setInitialRotationPoint(2.0F, -0.5F, -1.0F);
yumao11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.1305262F, 0.0F, 0.9914449F)).transpose());
yumao11.setTextureSize(64, 128);
parts.put(yumao11.boxName, yumao11);
yigu11.addChild(yumao11);

yumao12 = new MCAModelRenderer(this, "yumao12", 0, 33);
yumao12.mirror = false;
yumao12.addBox(0.0F, 0.0F, -8.0F, 2, 1, 8);
yumao12.setInitialRotationPoint(4.0F, -0.5F, -1.0F);
yumao12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.25881904F, 0.0F, 0.9659258F)).transpose());
yumao12.setTextureSize(64, 128);
parts.put(yumao12.boxName, yumao12);
yigu11.addChild(yumao12);

yumso13 = new MCAModelRenderer(this, "yumso13", 20, 33);
yumso13.mirror = false;
yumso13.addBox(0.0F, 0.0F, -8.0F, 2, 1, 8);
yumso13.setInitialRotationPoint(8.0F, -0.5F, -3.0F);
yumso13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.42261827F, 0.0F, 0.90630776F)).transpose());
yumso13.setTextureSize(64, 128);
parts.put(yumso13.boxName, yumso13);
yigu11.addChild(yumso13);

yigu22 = new MCAModelRenderer(this, "yigu22", 0, 42);
yigu22.mirror = false;
yigu22.addBox(-5.0F, -1.0F, -2.0F, 5, 2, 2);
yigu22.setInitialRotationPoint(-7.0F, 0.0F, 0.0F);
yigu22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.25881904F, 0.0F, 0.9659258F)).transpose());
yigu22.setTextureSize(64, 128);
parts.put(yigu22.boxName, yigu22);
yugu21.addChild(yigu22);

yumao22 = new MCAModelRenderer(this, "yumao22", 0, 24);
yumao22.mirror = false;
yumao22.addBox(-2.0F, 0.0F, -8.0F, 2, 1, 8);
yumao22.setInitialRotationPoint(-4.0F, -0.5F, -1.0F);
yumao22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.25881904F, 0.0F, 0.9659258F)).transpose());
yumao22.setTextureSize(64, 128);
parts.put(yumao22.boxName, yumao22);
yugu21.addChild(yumao22);

yumso23 = new MCAModelRenderer(this, "yumso23", 20, 23);
yumso23.mirror = false;
yumso23.addBox(-2.0F, 0.0F, -8.0F, 2, 1, 8);
yumso23.setInitialRotationPoint(-8.0F, -0.5F, -3.0F);
yumso23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.42261827F, 0.0F, 0.90630776F)).transpose());
yumso23.setTextureSize(64, 128);
parts.put(yumso23.boxName, yumso23);
yugu21.addChild(yumso23);

yumao21 = new MCAModelRenderer(this, "yumao21", 0, 24);
yumao21.mirror = false;
yumao21.addBox(-2.0F, 0.0F, -8.0F, 2, 1, 8);
yumao21.setInitialRotationPoint(-2.0F, -0.5F, -1.0F);
yumao21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.1305262F, 0.0F, 0.9914449F)).transpose());
yumao21.setTextureSize(64, 128);
parts.put(yumao21.boxName, yumao21);
yugu21.addChild(yumao21);

wei111 = new MCAModelRenderer(this, "wei111", 0, 61);
wei111.mirror = false;
wei111.addBox(-0.5F, -3.0F, -9.0F, 1, 3, 9);
wei111.setInitialRotationPoint(0.0F, 3.0F, -4.0F);
wei111.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
wei111.setTextureSize(64, 128);
parts.put(wei111.boxName, wei111);
body33.addChild(wei111);

wei112 = new MCAModelRenderer(this, "wei112", 0, 50);
wei112.mirror = false;
wei112.addBox(-0.5F, 0.0F, -8.0F, 1, 3, 8);
wei112.setInitialRotationPoint(0.0F, -3.0F, -2.0F);
wei112.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.104528464F, 0.0F, 0.0F, 0.9945219F)).transpose());
wei112.setTextureSize(64, 128);
parts.put(wei112.boxName, wei112);
body33.addChild(wei112);

wei131 = new MCAModelRenderer(this, "wei131", 20, 64);
wei131.mirror = false;
wei131.addBox(-0.5F, -2.0F, -5.0F, 1, 4, 5);
wei131.setInitialRotationPoint(0.0F, 0.0F, -4.0F);
wei131.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
wei131.setTextureSize(64, 128);
parts.put(wei131.boxName, wei131);
body33.addChild(wei131);

}

@Override
public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) 
{
EntityLuoYu entity = (EntityLuoYu)par1Entity;

AnimationHandler.performAnimationInModel(parts, entity);

//Render every non-child part
body11.render(par7);
}
@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {}

public MCAModelRenderer getModelRendererFromName(String name)
{
return parts.get(name);
}
}