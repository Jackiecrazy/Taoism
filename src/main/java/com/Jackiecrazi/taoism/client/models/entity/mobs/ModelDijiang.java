package com.Jackiecrazi.taoism.client.models.entity.mobs;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.client.MCAClientLibrary.MCAModelRenderer;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.MCAVersionChecker;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Matrix4f;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityDiJiang;

public class ModelDijiang extends ModelBase {
public final int MCA_MIN_REQUESTED_VERSION = 5;
public HashMap<String, MCAModelRenderer> parts = new HashMap<String, MCAModelRenderer>();

MCAModelRenderer body1;
MCAModelRenderer body2;
MCAModelRenderer body3;
MCAModelRenderer body4;
MCAModelRenderer zuoqiantui1;
MCAModelRenderer youqiantui1;
MCAModelRenderer zuozhongtui1;
MCAModelRenderer youzhongtui1;
MCAModelRenderer zuohoutui1;
MCAModelRenderer youhoutui1;
MCAModelRenderer zuohouchi1;
MCAModelRenderer youhouchi1;
MCAModelRenderer zuoqianchi1;
MCAModelRenderer youqianchi1;
MCAModelRenderer wei1;
MCAModelRenderer zuoqiantui2;
MCAModelRenderer youqiantui2;
MCAModelRenderer zuozhongtui2;
MCAModelRenderer youzhongtui2;
MCAModelRenderer zuohoutui2;
MCAModelRenderer youhoutui2;
MCAModelRenderer zuohouchi2;
MCAModelRenderer zuohouchi11;
MCAModelRenderer zuohouchi12;
MCAModelRenderer youhouchi2;
MCAModelRenderer youhouchi11;
MCAModelRenderer youhouchi12;
MCAModelRenderer zuoqianchi2;
MCAModelRenderer zuoqianchi11;
MCAModelRenderer zuoqianchi12;
MCAModelRenderer zuoqianchi13;
MCAModelRenderer zuoqianchi14;
MCAModelRenderer youqianchi2;
MCAModelRenderer youqianchi11;
MCAModelRenderer youqianchi12;
MCAModelRenderer youqianchi14;
MCAModelRenderer youqianchi13;
MCAModelRenderer wei2;
MCAModelRenderer zuoqiantui3;
MCAModelRenderer youqiantui3;
MCAModelRenderer zuozhongtui3;
MCAModelRenderer youzhongtui3;
MCAModelRenderer zuohoutui3;
MCAModelRenderer youhoutui3;
MCAModelRenderer zuohouchi21;
MCAModelRenderer zuohouchi22;
MCAModelRenderer zuohouchi23;
MCAModelRenderer zuohouchi24;
MCAModelRenderer youhouchi21;
MCAModelRenderer youhouchi22;
MCAModelRenderer youhouchi23;
MCAModelRenderer youhouchi24;
MCAModelRenderer zuoqianchi21;
MCAModelRenderer zuoqianchi22;
MCAModelRenderer zuoqianchi23;
MCAModelRenderer zuoqianchi24;
MCAModelRenderer youqianchi22;
MCAModelRenderer youqianchi23;
MCAModelRenderer youqianchi24;
MCAModelRenderer youqianchi21;

public ModelDijiang()
{
MCAVersionChecker.checkForLibraryVersion(getClass(), MCA_MIN_REQUESTED_VERSION);

textureWidth = 128;
textureHeight = 256;

body1 = new MCAModelRenderer(this, "body1", 0, 206);
body1.mirror = false;
body1.addBox(-11.0F, -10.0F, -30.0F, 22, 20, 30);
body1.setInitialRotationPoint(0.0F, 2.0F, 2.0F);
body1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body1.setTextureSize(128, 256);
parts.put(body1.boxName, body1);

body2 = new MCAModelRenderer(this, "body2", 0, 151);
body2.mirror = false;
body2.addBox(-9.0F, -12.0F, -30.0F, 18, 24, 30);
body2.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
body2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body2.setTextureSize(128, 256);
parts.put(body2.boxName, body2);
body1.addChild(body2);

body3 = new MCAModelRenderer(this, "body3", 0, 132);
body3.mirror = false;
body3.addBox(-8.0F, -8.0F, -3.0F, 16, 16, 3);
body3.setInitialRotationPoint(0.0F, 2.0F, 3.0F);
body3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body3.setTextureSize(128, 256);
parts.put(body3.boxName, body3);
body1.addChild(body3);

body4 = new MCAModelRenderer(this, "body4", 47, 128);
body4.mirror = false;
body4.addBox(-9.0F, -9.0F, -5.0F, 18, 18, 5);
body4.setInitialRotationPoint(0.0F, 0.0F, -28.0F);
body4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body4.setTextureSize(128, 256);
parts.put(body4.boxName, body4);
body1.addChild(body4);

zuoqiantui1 = new MCAModelRenderer(this, "zuoqiantui1", 51, 21);
zuoqiantui1.mirror = false;
zuoqiantui1.addBox(-3.0F, -10.0F, -3.0F, 6, 10, 6);
zuoqiantui1.setInitialRotationPoint(9.0F, -7.0F, -1.0F);
zuoqiantui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuoqiantui1.setTextureSize(128, 256);
parts.put(zuoqiantui1.boxName, zuoqiantui1);
body1.addChild(zuoqiantui1);

youqiantui1 = new MCAModelRenderer(this, "youqiantui1", 51, 21);
youqiantui1.mirror = false;
youqiantui1.addBox(-3.0F, -10.0F, -3.0F, 6, 10, 6);
youqiantui1.setInitialRotationPoint(-9.0F, -7.0F, -1.0F);
youqiantui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youqiantui1.setTextureSize(128, 256);
parts.put(youqiantui1.boxName, youqiantui1);
body1.addChild(youqiantui1);

zuozhongtui1 = new MCAModelRenderer(this, "zuozhongtui1", 0, 84);
zuozhongtui1.mirror = false;
zuozhongtui1.addBox(-4.0F, -12.0F, -4.0F, 8, 12, 8);
zuozhongtui1.setInitialRotationPoint(9.0F, -2.0F, -13.0F);
zuozhongtui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuozhongtui1.setTextureSize(128, 256);
parts.put(zuozhongtui1.boxName, zuozhongtui1);
body1.addChild(zuozhongtui1);

youzhongtui1 = new MCAModelRenderer(this, "youzhongtui1", 0, 84);
youzhongtui1.mirror = false;
youzhongtui1.addBox(-4.0F, -12.0F, -4.0F, 8, 12, 8);
youzhongtui1.setInitialRotationPoint(-9.0F, -2.0F, -13.0F);
youzhongtui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youzhongtui1.setTextureSize(128, 256);
parts.put(youzhongtui1.boxName, youzhongtui1);
body1.addChild(youzhongtui1);

zuohoutui1 = new MCAModelRenderer(this, "zuohoutui1", 0, 0);
zuohoutui1.mirror = false;
zuohoutui1.addBox(-5.0F, -14.0F, -5.0F, 10, 14, 10);
zuohoutui1.setInitialRotationPoint(9.0F, 0.0F, -27.0F);
zuohoutui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuohoutui1.setTextureSize(128, 256);
parts.put(zuohoutui1.boxName, zuohoutui1);
body1.addChild(zuohoutui1);

youhoutui1 = new MCAModelRenderer(this, "youhoutui1", 0, 0);
youhoutui1.mirror = false;
youhoutui1.addBox(-5.0F, -14.0F, -5.0F, 10, 14, 10);
youhoutui1.setInitialRotationPoint(-9.0F, 0.0F, -27.0F);
youhoutui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youhoutui1.setTextureSize(128, 256);
parts.put(youhoutui1.boxName, youhoutui1);
body1.addChild(youhoutui1);

zuohouchi1 = new MCAModelRenderer(this, "zuohouchi1", 0, 119);
zuohouchi1.mirror = false;
zuohouchi1.addBox(0.0F, -1.0F, -3.0F, 12, 2, 3);
zuohouchi1.setInitialRotationPoint(9.0F, 6.0F, -23.0F);
zuohouchi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F)).transpose());
zuohouchi1.setTextureSize(128, 256);
parts.put(zuohouchi1.boxName, zuohouchi1);
body1.addChild(zuohouchi1);

youhouchi1 = new MCAModelRenderer(this, "youhouchi1", 0, 119);
youhouchi1.mirror = false;
youhouchi1.addBox(-12.0F, -1.0F, -3.0F, 12, 2, 3);
youhouchi1.setInitialRotationPoint(-9.0F, 6.0F, -23.0F);
youhouchi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F)).transpose());
youhouchi1.setTextureSize(128, 256);
parts.put(youhouchi1.boxName, youhouchi1);
body1.addChild(youhouchi1);

zuoqianchi1 = new MCAModelRenderer(this, "zuoqianchi1", 0, 125);
zuoqianchi1.mirror = false;
zuoqianchi1.addBox(0.0F, -2.0F, -3.0F, 20, 2, 3);
zuoqianchi1.setInitialRotationPoint(8.0F, 8.0F, -1.0F);
zuoqianchi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F)).transpose());
zuoqianchi1.setTextureSize(128, 256);
parts.put(zuoqianchi1.boxName, zuoqianchi1);
body2.addChild(zuoqianchi1);

youqianchi1 = new MCAModelRenderer(this, "youqianchi1", 0, 125);
youqianchi1.mirror = false;
youqianchi1.addBox(-20.0F, -2.0F, -3.0F, 20, 2, 3);
youqianchi1.setInitialRotationPoint(-8.0F, 8.0F, -1.0F);
youqianchi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F)).transpose());
youqianchi1.setTextureSize(128, 256);
parts.put(youqianchi1.boxName, youqianchi1);
body2.addChild(youqianchi1);

wei1 = new MCAModelRenderer(this, "wei1", 51, 41);
wei1.mirror = false;
wei1.addBox(-2.5F, -16.0F, -2.5F, 5, 16, 5);
wei1.setInitialRotationPoint(0.0F, 6.0F, -3.0F);
wei1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.38268346F, 0.0F, 0.0F, 0.9238795F)).transpose());
wei1.setTextureSize(128, 256);
parts.put(wei1.boxName, wei1);
body4.addChild(wei1);

zuoqiantui2 = new MCAModelRenderer(this, "zuoqiantui2", 72, 0);
zuoqiantui2.mirror = false;
zuoqiantui2.addBox(-2.5F, -8.0F, -5.0F, 5, 8, 5);
zuoqiantui2.setInitialRotationPoint(0.0F, -9.0F, 2.5F);
zuoqiantui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
zuoqiantui2.setTextureSize(128, 256);
parts.put(zuoqiantui2.boxName, zuoqiantui2);
zuoqiantui1.addChild(zuoqiantui2);

youqiantui2 = new MCAModelRenderer(this, "youqiantui2", 72, 0);
youqiantui2.mirror = false;
youqiantui2.addBox(-2.5F, -8.0F, -5.0F, 5, 8, 5);
youqiantui2.setInitialRotationPoint(0.0F, -9.0F, 2.5F);
youqiantui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
youqiantui2.setTextureSize(128, 256);
parts.put(youqiantui2.boxName, youqiantui2);
youqiantui1.addChild(youqiantui2);

zuozhongtui2 = new MCAModelRenderer(this, "zuozhongtui2", 48, 0);
zuozhongtui2.mirror = false;
zuozhongtui2.addBox(-3.0F, -8.0F, -6.0F, 6, 8, 6);
zuozhongtui2.setInitialRotationPoint(0.0F, -12.0F, 3.0F);
zuozhongtui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
zuozhongtui2.setTextureSize(128, 256);
parts.put(zuozhongtui2.boxName, zuozhongtui2);
zuozhongtui1.addChild(zuozhongtui2);

youzhongtui2 = new MCAModelRenderer(this, "youzhongtui2", 48, 0);
youzhongtui2.mirror = false;
youzhongtui2.addBox(-3.0F, -8.0F, -6.0F, 6, 8, 6);
youzhongtui2.setInitialRotationPoint(0.0F, -12.0F, 3.0F);
youzhongtui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
youzhongtui2.setTextureSize(128, 256);
parts.put(youzhongtui2.boxName, youzhongtui2);
youzhongtui1.addChild(youzhongtui2);

zuohoutui2 = new MCAModelRenderer(this, "zuohoutui2", 0, 45);
zuohoutui2.mirror = false;
zuohoutui2.addBox(-4.0F, -10.0F, -8.0F, 8, 10, 8);
zuohoutui2.setInitialRotationPoint(0.0F, -13.0F, 4.0F);
zuohoutui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
zuohoutui2.setTextureSize(128, 256);
parts.put(zuohoutui2.boxName, zuohoutui2);
zuohoutui1.addChild(zuohoutui2);

youhoutui2 = new MCAModelRenderer(this, "youhoutui2", 0, 45);
youhoutui2.mirror = false;
youhoutui2.addBox(-4.0F, -10.0F, -8.0F, 8, 10, 8);
youhoutui2.setInitialRotationPoint(0.0F, -13.0F, 4.0F);
youhoutui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
youhoutui2.setTextureSize(128, 256);
parts.put(youhoutui2.boxName, youhoutui2);
youhoutui1.addChild(youhoutui2);

zuohouchi2 = new MCAModelRenderer(this, "zuohouchi2", 0, 113);
zuohouchi2.mirror = false;
zuohouchi2.addBox(0.0F, -1.0F, -3.0F, 16, 2, 3);
zuohouchi2.setInitialRotationPoint(12.0F, 0.0F, 0.0F);
zuohouchi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F)).transpose());
zuohouchi2.setTextureSize(128, 256);
parts.put(zuohouchi2.boxName, zuohouchi2);
zuohouchi1.addChild(zuohouchi2);

zuohouchi11 = new MCAModelRenderer(this, "zuohouchi11", 31, 54);
zuohouchi11.mirror = false;
zuohouchi11.addBox(0.0F, -1.0F, -10.0F, 3, 1, 10);
zuohouchi11.setInitialRotationPoint(2.0F, 0.5F, -2.0F);
zuohouchi11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.043619387F, 0.0F, 0.99904823F)).transpose());
zuohouchi11.setTextureSize(128, 256);
parts.put(zuohouchi11.boxName, zuohouchi11);
zuohouchi1.addChild(zuohouchi11);

zuohouchi12 = new MCAModelRenderer(this, "zuohouchi12", 28, 51);
zuohouchi12.mirror = false;
zuohouchi12.addBox(0.0F, -1.0F, -13.0F, 3, 1, 13);
zuohouchi12.setInitialRotationPoint(6.0F, 0.5F, -2.0F);
zuohouchi12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F)).transpose());
zuohouchi12.setTextureSize(128, 256);
parts.put(zuohouchi12.boxName, zuohouchi12);
zuohouchi1.addChild(zuohouchi12);

youhouchi2 = new MCAModelRenderer(this, "youhouchi2", 0, 113);
youhouchi2.mirror = false;
youhouchi2.addBox(-16.0F, -1.0F, -3.0F, 16, 2, 3);
youhouchi2.setInitialRotationPoint(-12.0F, 0.0F, 0.0F);
youhouchi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F)).transpose());
youhouchi2.setTextureSize(128, 256);
parts.put(youhouchi2.boxName, youhouchi2);
youhouchi1.addChild(youhouchi2);

youhouchi11 = new MCAModelRenderer(this, "youhouchi11", 31, 95);
youhouchi11.mirror = false;
youhouchi11.addBox(-3.0F, -1.0F, -10.0F, 3, 1, 10);
youhouchi11.setInitialRotationPoint(-2.0F, 0.5F, -2.0F);
youhouchi11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.043619387F, 0.0F, 0.99904823F)).transpose());
youhouchi11.setTextureSize(128, 256);
parts.put(youhouchi11.boxName, youhouchi11);
youhouchi1.addChild(youhouchi11);

youhouchi12 = new MCAModelRenderer(this, "youhouchi12", 28, 92);
youhouchi12.mirror = false;
youhouchi12.addBox(-3.0F, -1.0F, -13.0F, 3, 1, 13);
youhouchi12.setInitialRotationPoint(-6.0F, 0.5F, -2.0F);
youhouchi12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F)).transpose());
youhouchi12.setTextureSize(128, 256);
parts.put(youhouchi12.boxName, youhouchi12);
youhouchi1.addChild(youhouchi12);

zuoqianchi2 = new MCAModelRenderer(this, "zuoqianchi2", 0, 107);
zuoqianchi2.mirror = false;
zuoqianchi2.addBox(0.0F, -1.0F, -3.0F, 27, 2, 3);
zuoqianchi2.setInitialRotationPoint(20.0F, -1.0F, 0.0F);
zuoqianchi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F)).transpose());
zuoqianchi2.setTextureSize(128, 256);
parts.put(zuoqianchi2.boxName, zuoqianchi2);
zuoqianchi1.addChild(zuoqianchi2);

zuoqianchi11 = new MCAModelRenderer(this, "zuoqianchi11", 25, 49);
zuoqianchi11.mirror = false;
zuoqianchi11.addBox(0.0F, -1.0F, -15.0F, 4, 1, 15);
zuoqianchi11.setInitialRotationPoint(2.0F, 0.0F, -2.0F);
zuoqianchi11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.043619387F, 0.0F, 0.99904823F)).transpose());
zuoqianchi11.setTextureSize(128, 256);
parts.put(zuoqianchi11.boxName, zuoqianchi11);
zuoqianchi1.addChild(zuoqianchi11);

zuoqianchi12 = new MCAModelRenderer(this, "zuoqianchi12", 23, 47);
zuoqianchi12.mirror = false;
zuoqianchi12.addBox(0.0F, -1.0F, -17.0F, 4, 1, 17);
zuoqianchi12.setInitialRotationPoint(6.5F, 0.0F, -1.0F);
zuoqianchi12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F)).transpose());
zuoqianchi12.setTextureSize(128, 256);
parts.put(zuoqianchi12.boxName, zuoqianchi12);
zuoqianchi1.addChild(zuoqianchi12);

zuoqianchi13 = new MCAModelRenderer(this, "zuoqianchi13", 20, 44);
zuoqianchi13.mirror = false;
zuoqianchi13.addBox(0.0F, -1.0F, -20.0F, 4, 1, 20);
zuoqianchi13.setInitialRotationPoint(11.5F, 0.0F, -2.0F);
zuoqianchi13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.1305262F, 0.0F, 0.9914449F)).transpose());
zuoqianchi13.setTextureSize(128, 256);
parts.put(zuoqianchi13.boxName, zuoqianchi13);
zuoqianchi1.addChild(zuoqianchi13);

zuoqianchi14 = new MCAModelRenderer(this, "zuoqianchi14", 17, 41);
zuoqianchi14.mirror = false;
zuoqianchi14.addBox(0.0F, -1.0F, -23.0F, 4, 1, 23);
zuoqianchi14.setInitialRotationPoint(16.5F, 0.0F, -2.0F);
zuoqianchi14.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F)).transpose());
zuoqianchi14.setTextureSize(128, 256);
parts.put(zuoqianchi14.boxName, zuoqianchi14);
zuoqianchi1.addChild(zuoqianchi14);

youqianchi2 = new MCAModelRenderer(this, "youqianchi2", 0, 107);
youqianchi2.mirror = false;
youqianchi2.addBox(-27.0F, -1.0F, -3.0F, 27, 2, 3);
youqianchi2.setInitialRotationPoint(-20.0F, -1.0F, 0.0F);
youqianchi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F)).transpose());
youqianchi2.setTextureSize(128, 256);
parts.put(youqianchi2.boxName, youqianchi2);
youqianchi1.addChild(youqianchi2);

youqianchi11 = new MCAModelRenderer(this, "youqianchi11", 25, 90);
youqianchi11.mirror = false;
youqianchi11.addBox(-4.0F, -1.0F, -15.0F, 4, 1, 15);
youqianchi11.setInitialRotationPoint(-2.0F, 0.0F, -2.0F);
youqianchi11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.043619387F, 0.0F, 0.99904823F)).transpose());
youqianchi11.setTextureSize(128, 256);
parts.put(youqianchi11.boxName, youqianchi11);
youqianchi1.addChild(youqianchi11);

youqianchi12 = new MCAModelRenderer(this, "youqianchi12", 23, 88);
youqianchi12.mirror = false;
youqianchi12.addBox(-4.0F, -1.0F, -17.0F, 4, 1, 17);
youqianchi12.setInitialRotationPoint(-6.5F, 0.0F, -1.0F);
youqianchi12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F)).transpose());
youqianchi12.setTextureSize(128, 256);
parts.put(youqianchi12.boxName, youqianchi12);
youqianchi1.addChild(youqianchi12);

youqianchi14 = new MCAModelRenderer(this, "youqianchi14", 17, 82);
youqianchi14.mirror = false;
youqianchi14.addBox(-4.0F, -1.0F, -23.0F, 4, 1, 23);
youqianchi14.setInitialRotationPoint(-16.5F, 0.0F, -2.0F);
youqianchi14.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.21643962F, 0.0F, 0.976296F)).transpose());
youqianchi14.setTextureSize(128, 256);
parts.put(youqianchi14.boxName, youqianchi14);
youqianchi1.addChild(youqianchi14);

youqianchi13 = new MCAModelRenderer(this, "youqianchi13", 20, 85);
youqianchi13.mirror = false;
youqianchi13.addBox(-4.0F, -1.0F, -20.0F, 4, 1, 20);
youqianchi13.setInitialRotationPoint(-11.5F, 0.0F, -2.0F);
youqianchi13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F)).transpose());
youqianchi13.setTextureSize(128, 256);
parts.put(youqianchi13.boxName, youqianchi13);
youqianchi1.addChild(youqianchi13);

wei2 = new MCAModelRenderer(this, "wei2", 74, 42);
wei2.mirror = false;
wei2.addBox(-2.5F, -10.0F, 0.0F, 5, 10, 4);
wei2.setInitialRotationPoint(0.0F, -16.0F, -2.5F);
wei2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.309017F, 0.0F, 0.0F, 0.95105654F)).transpose());
wei2.setTextureSize(128, 256);
parts.put(wei2.boxName, wei2);
wei1.addChild(wei2);

zuoqiantui3 = new MCAModelRenderer(this, "zuoqiantui3", 98, 0);
zuoqiantui3.mirror = false;
zuoqiantui3.addBox(-3.5F, -3.0F, -2.0F, 7, 3, 8);
zuoqiantui3.setInitialRotationPoint(0.0F, -6.5F, -3.5F);
zuoqiantui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuoqiantui3.setTextureSize(128, 256);
parts.put(zuoqiantui3.boxName, zuoqiantui3);
zuoqiantui2.addChild(zuoqiantui3);

youqiantui3 = new MCAModelRenderer(this, "youqiantui3", 98, 0);
youqiantui3.mirror = false;
youqiantui3.addBox(-3.5F, -3.0F, -2.0F, 7, 3, 8);
youqiantui3.setInitialRotationPoint(0.0F, -6.5F, -3.5F);
youqiantui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youqiantui3.setTextureSize(128, 256);
parts.put(youqiantui3.boxName, youqiantui3);
youqiantui2.addChild(youqiantui3);

zuozhongtui3 = new MCAModelRenderer(this, "zuozhongtui3", 92, 11);
zuozhongtui3.mirror = false;
zuozhongtui3.addBox(-4.0F, -4.0F, -3.0F, 8, 4, 10);
zuozhongtui3.setInitialRotationPoint(0.0F, -7.6F, -4.0F);
zuozhongtui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuozhongtui3.setTextureSize(128, 256);
parts.put(zuozhongtui3.boxName, zuozhongtui3);
zuozhongtui2.addChild(zuozhongtui3);

youzhongtui3 = new MCAModelRenderer(this, "youzhongtui3", 92, 11);
youzhongtui3.mirror = false;
youzhongtui3.addBox(-4.0F, -4.0F, -3.0F, 8, 4, 10);
youzhongtui3.setInitialRotationPoint(0.0F, -7.6F, -4.0F);
youzhongtui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youzhongtui3.setTextureSize(128, 256);
parts.put(youzhongtui3.boxName, youzhongtui3);
youzhongtui2.addChild(youzhongtui3);

zuohoutui3 = new MCAModelRenderer(this, "zuohoutui3", 84, 108);
zuohoutui3.mirror = false;
zuohoutui3.addBox(-5.0F, -5.0F, -3.0F, 10, 5, 12);
zuohoutui3.setInitialRotationPoint(0.0F, -8.4F, -6.0F);
zuohoutui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
zuohoutui3.setTextureSize(128, 256);
parts.put(zuohoutui3.boxName, zuohoutui3);
zuohoutui2.addChild(zuohoutui3);

youhoutui3 = new MCAModelRenderer(this, "youhoutui3", 84, 108);
youhoutui3.mirror = false;
youhoutui3.addBox(-5.0F, -5.0F, -3.0F, 10, 5, 12);
youhoutui3.setInitialRotationPoint(0.0F, -8.4F, -6.0F);
youhoutui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
youhoutui3.setTextureSize(128, 256);
parts.put(youhoutui3.boxName, youhoutui3);
youhoutui2.addChild(youhoutui3);

zuohouchi21 = new MCAModelRenderer(this, "zuohouchi21", 24, 47);
zuohouchi21.mirror = false;
zuohouchi21.addBox(0.0F, -1.0F, -17.0F, 3, 1, 17);
zuohouchi21.setInitialRotationPoint(-1.0F, 0.5F, -2.0F);
zuohouchi21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.3007058F, 0.0F, 0.95371693F)).transpose());
zuohouchi21.setTextureSize(128, 256);
parts.put(zuohouchi21.boxName, zuohouchi21);
zuohouchi2.addChild(zuohouchi21);

zuohouchi22 = new MCAModelRenderer(this, "zuohouchi22", 20, 43);
zuohouchi22.mirror = false;
zuohouchi22.addBox(0.0F, -1.0F, -21.0F, 3, 1, 21);
zuohouchi22.setInitialRotationPoint(3.0F, 0.5F, -2.0F);
zuohouchi22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.34202012F, 0.0F, 0.9396926F)).transpose());
zuohouchi22.setTextureSize(128, 256);
parts.put(zuohouchi22.boxName, zuohouchi22);
zuohouchi2.addChild(zuohouchi22);

zuohouchi23 = new MCAModelRenderer(this, "zuohouchi23", 16, 39);
zuohouchi23.mirror = false;
zuohouchi23.addBox(0.0F, -1.0F, -25.0F, 3, 1, 25);
zuohouchi23.setInitialRotationPoint(7.0F, 0.5F, -3.0F);
zuohouchi23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.42261827F, 0.0F, 0.90630776F)).transpose());
zuohouchi23.setTextureSize(128, 256);
parts.put(zuohouchi23.boxName, zuohouchi23);
zuohouchi2.addChild(zuohouchi23);

zuohouchi24 = new MCAModelRenderer(this, "zuohouchi24", 9, 32);
zuohouchi24.mirror = false;
zuohouchi24.addBox(0.0F, -1.0F, -30.0F, 3, 1, 32);
zuohouchi24.setInitialRotationPoint(10.0F, 0.5F, -4.0F);
zuohouchi24.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.57357645F, 0.0F, 0.81915206F)).transpose());
zuohouchi24.setTextureSize(128, 256);
parts.put(zuohouchi24.boxName, zuohouchi24);
zuohouchi2.addChild(zuohouchi24);

youhouchi21 = new MCAModelRenderer(this, "youhouchi21", 24, 88);
youhouchi21.mirror = false;
youhouchi21.addBox(-3.0F, -1.0F, -17.0F, 3, 1, 17);
youhouchi21.setInitialRotationPoint(1.0F, 0.5F, -2.0F);
youhouchi21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.3007058F, 0.0F, 0.95371693F)).transpose());
youhouchi21.setTextureSize(128, 256);
parts.put(youhouchi21.boxName, youhouchi21);
youhouchi2.addChild(youhouchi21);

youhouchi22 = new MCAModelRenderer(this, "youhouchi22", 20, 84);
youhouchi22.mirror = false;
youhouchi22.addBox(-3.0F, -1.0F, -21.0F, 3, 1, 21);
youhouchi22.setInitialRotationPoint(-3.0F, 0.5F, -2.0F);
youhouchi22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.34202012F, 0.0F, 0.9396926F)).transpose());
youhouchi22.setTextureSize(128, 256);
parts.put(youhouchi22.boxName, youhouchi22);
youhouchi2.addChild(youhouchi22);

youhouchi23 = new MCAModelRenderer(this, "youhouchi23", 16, 80);
youhouchi23.mirror = false;
youhouchi23.addBox(-3.0F, -1.0F, -25.0F, 3, 1, 25);
youhouchi23.setInitialRotationPoint(-7.0F, 0.5F, -3.0F);
youhouchi23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.42261827F, 0.0F, 0.90630776F)).transpose());
youhouchi23.setTextureSize(128, 256);
parts.put(youhouchi23.boxName, youhouchi23);
youhouchi2.addChild(youhouchi23);

youhouchi24 = new MCAModelRenderer(this, "youhouchi24", 9, 73);
youhouchi24.mirror = false;
youhouchi24.addBox(-3.0F, -1.0F, -30.0F, 3, 1, 32);
youhouchi24.setInitialRotationPoint(-10.0F, 0.5F, -4.0F);
youhouchi24.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.57357645F, 0.0F, 0.81915206F)).transpose());
youhouchi24.setTextureSize(128, 256);
parts.put(youhouchi24.boxName, youhouchi24);
youhouchi2.addChild(youhouchi24);

zuoqianchi21 = new MCAModelRenderer(this, "zuoqianchi21", 15, 39);
zuoqianchi21.mirror = false;
zuoqianchi21.addBox(0.0F, 0.0F, -25.0F, 4, 1, 25);
zuoqianchi21.setInitialRotationPoint(4.0F, 0.0F, -3.0F);
zuoqianchi21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.38268346F, 0.0F, 0.9238795F)).transpose());
zuoqianchi21.setTextureSize(128, 256);
parts.put(zuoqianchi21.boxName, zuoqianchi21);
zuoqianchi2.addChild(zuoqianchi21);

zuoqianchi22 = new MCAModelRenderer(this, "zuoqianchi22", 12, 36);
zuoqianchi22.mirror = false;
zuoqianchi22.addBox(0.0F, 0.0F, -27.1F, 4, 1, 28);
zuoqianchi22.setInitialRotationPoint(12.0F, 0.0F, -3.5F);
zuoqianchi22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.42261827F, 0.0F, 0.90630776F)).transpose());
zuoqianchi22.setTextureSize(128, 256);
parts.put(zuoqianchi22.boxName, zuoqianchi22);
zuoqianchi2.addChild(zuoqianchi22);

zuoqianchi23 = new MCAModelRenderer(this, "zuoqianchi23", 6, 30);
zuoqianchi23.mirror = false;
zuoqianchi23.addBox(0.0F, 0.0F, -34.0F, 4, 1, 34);
zuoqianchi23.setInitialRotationPoint(18.0F, 0.0F, -3.5F);
zuoqianchi23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.5F, 0.0F, 0.8660254F)).transpose());
zuoqianchi23.setTextureSize(128, 256);
parts.put(zuoqianchi23.boxName, zuoqianchi23);
zuoqianchi2.addChild(zuoqianchi23);

zuoqianchi24 = new MCAModelRenderer(this, "zuoqianchi24", 0, 24);
zuoqianchi24.mirror = false;
zuoqianchi24.addBox(0.0F, 0.0F, -38.0F, 4, 1, 40);
zuoqianchi24.setInitialRotationPoint(19.0F, 0.0F, -5.0F);
zuoqianchi24.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.6427876F, 0.0F, 0.76604444F)).transpose());
zuoqianchi24.setTextureSize(128, 256);
parts.put(zuoqianchi24.boxName, zuoqianchi24);
zuoqianchi2.addChild(zuoqianchi24);

youqianchi22 = new MCAModelRenderer(this, "youqianchi22", 12, 77);
youqianchi22.mirror = false;
youqianchi22.addBox(-4.0F, -1.0F, -27.0F, 4, 1, 28);
youqianchi22.setInitialRotationPoint(-12.0F, 0.0F, -3.5F);
youqianchi22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.42261827F, 0.0F, 0.90630776F)).transpose());
youqianchi22.setTextureSize(128, 256);
parts.put(youqianchi22.boxName, youqianchi22);
youqianchi2.addChild(youqianchi22);

youqianchi23 = new MCAModelRenderer(this, "youqianchi23", 6, 71);
youqianchi23.mirror = false;
youqianchi23.addBox(-4.0F, -1.0F, -34.0F, 4, 1, 34);
youqianchi23.setInitialRotationPoint(-18.0F, 0.0F, -3.5F);
youqianchi23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.5F, 0.0F, 0.8660254F)).transpose());
youqianchi23.setTextureSize(128, 256);
parts.put(youqianchi23.boxName, youqianchi23);
youqianchi2.addChild(youqianchi23);

youqianchi24 = new MCAModelRenderer(this, "youqianchi24", 0, 65);
youqianchi24.mirror = false;
youqianchi24.addBox(-4.0F, -1.0F, -38.0F, 4, 1, 40);
youqianchi24.setInitialRotationPoint(-19.0F, 0.0F, -5.0F);
youqianchi24.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.6427876F, 0.0F, 0.76604444F)).transpose());
youqianchi24.setTextureSize(128, 256);
parts.put(youqianchi24.boxName, youqianchi24);
youqianchi2.addChild(youqianchi24);

youqianchi21 = new MCAModelRenderer(this, "youqianchi21", 15, 80);
youqianchi21.mirror = false;
youqianchi21.addBox(-4.0F, -1.0F, -25.0F, 4, 1, 25);
youqianchi21.setInitialRotationPoint(-4.0F, 0.0F, -3.0F);
youqianchi21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.38268346F, 0.0F, 0.9238795F)).transpose());
youqianchi21.setTextureSize(128, 256);
parts.put(youqianchi21.boxName, youqianchi21);
youqianchi2.addChild(youqianchi21);

}

@Override
public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) 
{
EntityDiJiang entity = (EntityDiJiang)par1Entity;

AnimationHandler.performAnimationInModel(parts, entity);

//Render every non-child part
body1.render(par7);
}
@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {}

public MCAModelRenderer getModelRendererFromName(String name)
{
return parts.get(name);
}
}