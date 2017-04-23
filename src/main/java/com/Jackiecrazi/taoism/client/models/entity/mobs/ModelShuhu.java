package com.Jackiecrazi.taoism.client.models.entity.mobs;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.client.MCAClientLibrary.MCAModelRenderer;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.MCAVersionChecker;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Matrix4f;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityShuHu;

public class ModelShuhu extends ModelBase {
public final int MCA_MIN_REQUESTED_VERSION = 5;
public HashMap<String, MCAModelRenderer> parts = new HashMap<String, MCAModelRenderer>();

MCAModelRenderer body11;
MCAModelRenderer body12;
MCAModelRenderer bizi1;
MCAModelRenderer rightfrontleg1;
MCAModelRenderer leftfrontleg1;
MCAModelRenderer leftbackleg1;
MCAModelRenderer rightbackleg1;
MCAModelRenderer wei1;
MCAModelRenderer rightwing1;
MCAModelRenderer lettwing1;
MCAModelRenderer bizi2;
MCAModelRenderer head;
MCAModelRenderer rightfrontleg2;
MCAModelRenderer leftfrontleg2;
MCAModelRenderer leftbackleg2;
MCAModelRenderer rightbackleg2;
MCAModelRenderer wei2;
MCAModelRenderer rightwing2;
MCAModelRenderer rightwing11;
MCAModelRenderer rightwing12;
MCAModelRenderer rightwing13;
MCAModelRenderer lefttwing11;
MCAModelRenderer leftwing13;
MCAModelRenderer lefttwing12;
MCAModelRenderer lefttwing2;
MCAModelRenderer mazong;
MCAModelRenderer jiao1;
MCAModelRenderer jiao2;
MCAModelRenderer rightfrontleg3;
MCAModelRenderer leftfrontleg3;
MCAModelRenderer leftbackleg3;
MCAModelRenderer rightbackleg3;
MCAModelRenderer wei3;
MCAModelRenderer rightwing21;
MCAModelRenderer rightwing22;
MCAModelRenderer rightwing23;
MCAModelRenderer leftwing21;
MCAModelRenderer leftwing22;
MCAModelRenderer leftwing23;
MCAModelRenderer wei4;
MCAModelRenderer wei5;
MCAModelRenderer wei6;

public ModelShuhu()
{
MCAVersionChecker.checkForLibraryVersion(getClass(), MCA_MIN_REQUESTED_VERSION);

textureWidth = 128;
textureHeight = 128;

body11 = new MCAModelRenderer(this, "body11", 0, 94);
body11.mirror = false;
body11.addBox(-7.0F, -5.0F, -24.0F, 14, 10, 24);
body11.setInitialRotationPoint(0.0F, -10.0F, 0.0F);
body11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body11.setTextureSize(128, 128);
parts.put(body11.boxName, body11);

body12 = new MCAModelRenderer(this, "body12", 0, 60);
body12.mirror = false;
body12.addBox(-6.0F, -5.0F, -24.0F, 12, 10, 24);
body12.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
body12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, -0.70710677F, 0.70710677F)).transpose());
body12.setTextureSize(128, 128);
parts.put(body12.boxName, body12);
body11.addChild(body12);

bizi1 = new MCAModelRenderer(this, "bizi1", 100, 74);
bizi1.mirror = false;
bizi1.addBox(-4.0F, -3.0F, -0.0F, 8, 15, 6);
bizi1.setInitialRotationPoint(0.0F, 2.0F, -4.0F);
bizi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
bizi1.setTextureSize(128, 128);
parts.put(bizi1.boxName, bizi1);
body11.addChild(bizi1);

rightfrontleg1 = new MCAModelRenderer(this, "rightfrontleg1", 76, 16);
rightfrontleg1.mirror = false;
rightfrontleg1.addBox(-2.0F, -11.0F, -3.0F, 4, 11, 5);
rightfrontleg1.setInitialRotationPoint(-5.5F, 3.0F, 0.5F);
rightfrontleg1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightfrontleg1.setTextureSize(128, 128);
parts.put(rightfrontleg1.boxName, rightfrontleg1);
body11.addChild(rightfrontleg1);

leftfrontleg1 = new MCAModelRenderer(this, "leftfrontleg1", 76, 0);
leftfrontleg1.mirror = false;
leftfrontleg1.addBox(-2.0F, -11.0F, -3.0F, 4, 11, 5);
leftfrontleg1.setInitialRotationPoint(5.5F, 3.0F, 0.5F);
leftfrontleg1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftfrontleg1.setTextureSize(128, 128);
parts.put(leftfrontleg1.boxName, leftfrontleg1);
body11.addChild(leftfrontleg1);

leftbackleg1 = new MCAModelRenderer(this, "leftbackleg1", 0, 0);
leftbackleg1.mirror = false;
leftbackleg1.addBox(-2.5F, -11.0F, -3.0F, 5, 11, 6);
leftbackleg1.setInitialRotationPoint(5.0F, 3.0F, -18.5F);
leftbackleg1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftbackleg1.setTextureSize(128, 128);
parts.put(leftbackleg1.boxName, leftbackleg1);
body11.addChild(leftbackleg1);

rightbackleg1 = new MCAModelRenderer(this, "rightbackleg1", 0, 18);
rightbackleg1.mirror = false;
rightbackleg1.addBox(-2.5F, -11.0F, -3.0F, 5, 11, 6);
rightbackleg1.setInitialRotationPoint(-5.0F, 3.0F, -18.5F);
rightbackleg1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightbackleg1.setTextureSize(128, 128);
parts.put(rightbackleg1.boxName, rightbackleg1);
body11.addChild(rightbackleg1);

wei1 = new MCAModelRenderer(this, "wei1", 54, 42);
wei1.mirror = false;
wei1.addBox(-4.0F, -4.0F, -10.0F, 8, 8, 10);
wei1.setInitialRotationPoint(0.0F, 2.0F, -20.0F);
wei1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei1.setTextureSize(128, 128);
parts.put(wei1.boxName, wei1);
body11.addChild(wei1);

rightwing1 = new MCAModelRenderer(this, "rightwing1", 0, 46);
rightwing1.mirror = false;
rightwing1.addBox(-20.0F, -1.5F, -3.0F, 20, 3, 4);
rightwing1.setInitialRotationPoint(-3.0F, 2.0F, -6.0F);
rightwing1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.18119794F, -0.05600989F, -0.50066054F, 0.8446119F)).transpose());
rightwing1.setTextureSize(128, 128);
parts.put(rightwing1.boxName, rightwing1);
body11.addChild(rightwing1);

lettwing1 = new MCAModelRenderer(this, "lettwing1", 0, 53);
lettwing1.mirror = false;
lettwing1.addBox(0.0F, -1.5F, -3.0F, 20, 3, 4);
lettwing1.setInitialRotationPoint(3.0F, 2.0F, -6.0F);
lettwing1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.18119794F, 0.05600989F, 0.50066054F, 0.8446119F)).transpose());
lettwing1.setTextureSize(128, 128);
parts.put(lettwing1.boxName, lettwing1);
body11.addChild(lettwing1);

bizi2 = new MCAModelRenderer(this, "bizi2", 102, 54);
bizi2.mirror = false;
bizi2.addBox(-4.0F, -0.0F, -5.0F, 8, 13, 5);
bizi2.setInitialRotationPoint(0.0F, 0.0F, 0.0F);
bizi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
bizi2.setTextureSize(128, 128);
parts.put(bizi2.boxName, bizi2);
bizi1.addChild(bizi2);

head = new MCAModelRenderer(this, "head", 94, 17);
head.mirror = false;
head.addBox(-4.5F, -6.0F, -0.0F, 9, 10, 8);
head.setInitialRotationPoint(0.0F, 10.0F, 0.0F);
head.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
head.setTextureSize(128, 128);
parts.put(head.boxName, head);
bizi1.addChild(head);

rightfrontleg2 = new MCAModelRenderer(this, "rightfrontleg2", 48, 0);
rightfrontleg2.mirror = false;
rightfrontleg2.addBox(-1.5F, -10.0F, -4.0F, 3, 10, 4);
rightfrontleg2.setInitialRotationPoint(0.0F, -10.0F, 1.5F);
rightfrontleg2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightfrontleg2.setTextureSize(128, 128);
parts.put(rightfrontleg2.boxName, rightfrontleg2);
rightfrontleg1.addChild(rightfrontleg2);

leftfrontleg2 = new MCAModelRenderer(this, "leftfrontleg2", 62, 0);
leftfrontleg2.mirror = false;
leftfrontleg2.addBox(-1.5F, -10.0F, -4.0F, 3, 10, 4);
leftfrontleg2.setInitialRotationPoint(0.0F, -10.0F, 1.5F);
leftfrontleg2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftfrontleg2.setTextureSize(128, 128);
parts.put(leftfrontleg2.boxName, leftfrontleg2);
leftfrontleg1.addChild(leftfrontleg2);

leftbackleg2 = new MCAModelRenderer(this, "leftbackleg2", 40, 14);
leftbackleg2.mirror = false;
leftbackleg2.addBox(-2.0F, -10.0F, -5.0F, 4, 10, 5);
leftbackleg2.setInitialRotationPoint(0.0F, -10.0F, 2.5F);
leftbackleg2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftbackleg2.setTextureSize(128, 128);
parts.put(leftbackleg2.boxName, leftbackleg2);
leftbackleg1.addChild(leftbackleg2);

rightbackleg2 = new MCAModelRenderer(this, "rightbackleg2", 58, 14);
rightbackleg2.mirror = false;
rightbackleg2.addBox(-2.0F, -10.0F, -5.0F, 4, 10, 5);
rightbackleg2.setInitialRotationPoint(0.0F, -10.0F, 2.5F);
rightbackleg2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightbackleg2.setTextureSize(128, 128);
parts.put(rightbackleg2.boxName, rightbackleg2);
rightbackleg1.addChild(rightbackleg2);

wei2 = new MCAModelRenderer(this, "wei2", 54, 42);
wei2.mirror = false;
wei2.addBox(-3.5F, -3.5F, -10.0F, 7, 7, 10);
wei2.setInitialRotationPoint(0.0F, 0.0F, -7.0F);
wei2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei2.setTextureSize(128, 128);
parts.put(wei2.boxName, wei2);
wei1.addChild(wei2);

rightwing2 = new MCAModelRenderer(this, "rightwing2", 0, 46);
rightwing2.mirror = false;
rightwing2.addBox(-20.0F, -1.5F, -4.0F, 20, 3, 4);
rightwing2.setInitialRotationPoint(-20.0F, 0.0F, 1.0F);
rightwing2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.21643962F, 0.0F, 0.976296F)).transpose());
rightwing2.setTextureSize(128, 128);
parts.put(rightwing2.boxName, rightwing2);
rightwing1.addChild(rightwing2);

rightwing11 = new MCAModelRenderer(this, "rightwing11", 15, 26);
rightwing11.mirror = false;
rightwing11.addBox(0.0F, -1.0F, -15.0F, 4, 1, 15);
rightwing11.setInitialRotationPoint(-9.0F, 1.0F, 0.0F);
rightwing11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.043619387F, 0.0F, 0.99904823F)).transpose());
rightwing11.setTextureSize(128, 128);
parts.put(rightwing11.boxName, rightwing11);
rightwing1.addChild(rightwing11);

rightwing12 = new MCAModelRenderer(this, "rightwing12", 13, 24);
rightwing12.mirror = false;
rightwing12.addBox(0.0F, -1.0F, -17.0F, 4, 1, 17);
rightwing12.setInitialRotationPoint(-13.0F, 1.0F, 0.0F);
rightwing12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.1305262F, 0.0F, 0.9914449F)).transpose());
rightwing12.setTextureSize(128, 128);
parts.put(rightwing12.boxName, rightwing12);
rightwing1.addChild(rightwing12);

rightwing13 = new MCAModelRenderer(this, "rightwing13", 9, 20);
rightwing13.mirror = false;
rightwing13.addBox(0.0F, -1.0F, -21.0F, 4, 1, 21);
rightwing13.setInitialRotationPoint(-17.5F, 1.0F, 0.0F);
rightwing13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F)).transpose());
rightwing13.setTextureSize(128, 128);
parts.put(rightwing13.boxName, rightwing13);
rightwing1.addChild(rightwing13);

lefttwing11 = new MCAModelRenderer(this, "lefttwing11", 15, 26);
lefttwing11.mirror = false;
lefttwing11.addBox(-4.0F, -1.0F, -15.0F, 4, 1, 15);
lefttwing11.setInitialRotationPoint(9.0F, 1.0F, 0.0F);
lefttwing11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.043619387F, 0.0F, 0.99904823F)).transpose());
lefttwing11.setTextureSize(128, 128);
parts.put(lefttwing11.boxName, lefttwing11);
lettwing1.addChild(lefttwing11);

leftwing13 = new MCAModelRenderer(this, "leftwing13", 9, 20);
leftwing13.mirror = false;
leftwing13.addBox(-4.0F, -1.0F, -21.0F, 4, 1, 21);
leftwing13.setInitialRotationPoint(17.5F, 1.0F, 0.0F);
leftwing13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F)).transpose());
leftwing13.setTextureSize(128, 128);
parts.put(leftwing13.boxName, leftwing13);
lettwing1.addChild(leftwing13);

lefttwing12 = new MCAModelRenderer(this, "lefttwing12", 13, 24);
lefttwing12.mirror = false;
lefttwing12.addBox(-4.0F, -1.0F, -17.0F, 4, 1, 17);
lefttwing12.setInitialRotationPoint(13.0F, 1.0F, 0.0F);
lefttwing12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.1305262F, 0.0F, 0.9914449F)).transpose());
lefttwing12.setTextureSize(128, 128);
parts.put(lefttwing12.boxName, lefttwing12);
lettwing1.addChild(lefttwing12);

lefttwing2 = new MCAModelRenderer(this, "lefttwing2", 0, 53);
lefttwing2.mirror = false;
lefttwing2.addBox(0.0F, -1.5F, -4.0F, 20, 3, 4);
lefttwing2.setInitialRotationPoint(20.0F, 0.0F, 1.0F);
lefttwing2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.21643962F, 0.0F, 0.976296F)).transpose());
lefttwing2.setTextureSize(128, 128);
parts.put(lefttwing2.boxName, lefttwing2);
lettwing1.addChild(lefttwing2);

mazong = new MCAModelRenderer(this, "mazong", 90, 35);
mazong.mirror = false;
mazong.addBox(-2.0F, 0.0F, -1.0F, 4, 4, 15);
mazong.setInitialRotationPoint(0.0F, -2.0F, -2.0F);
mazong.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.70710677F, 0.0F, 0.0F, 0.70710677F)).transpose());
mazong.setTextureSize(128, 128);
parts.put(mazong.boxName, mazong);
bizi2.addChild(mazong);

jiao1 = new MCAModelRenderer(this, "jiao1", 94, 0);
jiao1.mirror = false;
jiao1.addBox(0.0F, -2.0F, -15.0F, 2, 2, 15);
jiao1.setInitialRotationPoint(-4.0F, 4.0F, 5.0F);
jiao1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25783417F, 0.08418597F, -0.022557564F, 0.9622502F)).transpose());
jiao1.setTextureSize(128, 128);
parts.put(jiao1.boxName, jiao1);
head.addChild(jiao1);

jiao2 = new MCAModelRenderer(this, "jiao2", 94, 0);
jiao2.mirror = false;
jiao2.addBox(-2.0F, -2.0F, -15.0F, 2, 2, 15);
jiao2.setInitialRotationPoint(4.0F, 4.0F, 5.0F);
jiao2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25783417F, -0.08418597F, 0.022557564F, 0.9622502F)).transpose());
jiao2.setTextureSize(128, 128);
parts.put(jiao2.boxName, jiao2);
head.addChild(jiao2);

rightfrontleg3 = new MCAModelRenderer(this, "rightfrontleg3", 108, 99);
rightfrontleg3.mirror = false;
rightfrontleg3.addBox(-2.5F, -4.0F, -2.5F, 5, 4, 5);
rightfrontleg3.setInitialRotationPoint(0.0F, -9.0F, -2.0F);
rightfrontleg3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightfrontleg3.setTextureSize(128, 128);
parts.put(rightfrontleg3.boxName, rightfrontleg3);
rightfrontleg2.addChild(rightfrontleg3);

leftfrontleg3 = new MCAModelRenderer(this, "leftfrontleg3", 108, 99);
leftfrontleg3.mirror = false;
leftfrontleg3.addBox(-2.5F, -4.0F, -2.5F, 5, 4, 5);
leftfrontleg3.setInitialRotationPoint(0.0F, -9.0F, -2.0F);
leftfrontleg3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftfrontleg3.setTextureSize(128, 128);
parts.put(leftfrontleg3.boxName, leftfrontleg3);
leftfrontleg2.addChild(leftfrontleg3);

leftbackleg3 = new MCAModelRenderer(this, "leftbackleg3", 104, 108);
leftbackleg3.mirror = false;
leftbackleg3.addBox(-3.0F, -4.0F, -3.5F, 6, 4, 6);
leftbackleg3.setInitialRotationPoint(0.0F, -9.0F, -2.0F);
leftbackleg3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
leftbackleg3.setTextureSize(128, 128);
parts.put(leftbackleg3.boxName, leftbackleg3);
leftbackleg2.addChild(leftbackleg3);

rightbackleg3 = new MCAModelRenderer(this, "rightbackleg3", 104, 108);
rightbackleg3.mirror = false;
rightbackleg3.addBox(-3.0F, -4.0F, -3.5F, 6, 4, 6);
rightbackleg3.setInitialRotationPoint(0.0F, -9.0F, -2.0F);
rightbackleg3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
rightbackleg3.setTextureSize(128, 128);
parts.put(rightbackleg3.boxName, rightbackleg3);
rightbackleg2.addChild(rightbackleg3);

wei3 = new MCAModelRenderer(this, "wei3", 54, 42);
wei3.mirror = false;
wei3.addBox(-3.0F, -3.0F, -10.0F, 6, 6, 10);
wei3.setInitialRotationPoint(0.0F, 0.0F, -7.0F);
wei3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei3.setTextureSize(128, 128);
parts.put(wei3.boxName, wei3);
wei2.addChild(wei3);

rightwing21 = new MCAModelRenderer(this, "rightwing21", 5, 16);
rightwing21.mirror = false;
rightwing21.addBox(0.0F, -1.0F, -25.0F, 4, 1, 25);
rightwing21.setInitialRotationPoint(-3.0F, 1.0F, 0.0F);
rightwing21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.42261827F, 0.0F, 0.90630776F)).transpose());
rightwing21.setTextureSize(128, 128);
parts.put(rightwing21.boxName, rightwing21);
rightwing2.addChild(rightwing21);

rightwing22 = new MCAModelRenderer(this, "rightwing22", 3, 14);
rightwing22.mirror = false;
rightwing22.addBox(0.0F, -1.0F, -27.0F, 4, 1, 27);
rightwing22.setInitialRotationPoint(-11.0F, 1.0F, 0.0F);
rightwing22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.4617486F, 0.0F, 0.8870108F)).transpose());
rightwing22.setTextureSize(128, 128);
parts.put(rightwing22.boxName, rightwing22);
rightwing2.addChild(rightwing22);

rightwing23 = new MCAModelRenderer(this, "rightwing23", 0, 11);
rightwing23.mirror = false;
rightwing23.addBox(0.0F, -1.0F, -30.0F, 4, 1, 30);
rightwing23.setInitialRotationPoint(-17.0F, 1.0F, 0.0F);
rightwing23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.53729963F, 0.0F, 0.8433914F)).transpose());
rightwing23.setTextureSize(128, 128);
parts.put(rightwing23.boxName, rightwing23);
rightwing2.addChild(rightwing23);

leftwing21 = new MCAModelRenderer(this, "leftwing21", 5, 16);
leftwing21.mirror = false;
leftwing21.addBox(-4.0F, -1.0F, -25.0F, 4, 1, 25);
leftwing21.setInitialRotationPoint(3.0F, 1.0F, 0.0F);
leftwing21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.42261827F, 0.0F, 0.90630776F)).transpose());
leftwing21.setTextureSize(128, 128);
parts.put(leftwing21.boxName, leftwing21);
lefttwing2.addChild(leftwing21);

leftwing22 = new MCAModelRenderer(this, "leftwing22", 3, 14);
leftwing22.mirror = false;
leftwing22.addBox(-4.0F, -1.0F, -27.0F, 4, 1, 27);
leftwing22.setInitialRotationPoint(11.0F, 1.0F, 0.0F);
leftwing22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.4617486F, 0.0F, 0.8870108F)).transpose());
leftwing22.setTextureSize(128, 128);
parts.put(leftwing22.boxName, leftwing22);
lefttwing2.addChild(leftwing22);

leftwing23 = new MCAModelRenderer(this, "leftwing23", 0, 11);
leftwing23.mirror = false;
leftwing23.addBox(-4.0F, -1.0F, -30.0F, 4, 1, 30);
leftwing23.setInitialRotationPoint(17.0F, 1.0F, 0.0F);
leftwing23.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.53729963F, 0.0F, 0.8433914F)).transpose());
leftwing23.setTextureSize(128, 128);
parts.put(leftwing23.boxName, leftwing23);
lefttwing2.addChild(leftwing23);

wei4 = new MCAModelRenderer(this, "wei4", 54, 42);
wei4.mirror = false;
wei4.addBox(-2.5F, -2.5F, -10.0F, 5, 5, 10);
wei4.setInitialRotationPoint(0.0F, 0.0F, -8.0F);
wei4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei4.setTextureSize(128, 128);
parts.put(wei4.boxName, wei4);
wei3.addChild(wei4);

wei5 = new MCAModelRenderer(this, "wei5", 54, 42);
wei5.mirror = false;
wei5.addBox(-2.0F, -2.0F, -10.0F, 4, 4, 10);
wei5.setInitialRotationPoint(0.0F, 0.0F, -8.0F);
wei5.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei5.setTextureSize(128, 128);
parts.put(wei5.boxName, wei5);
wei4.addChild(wei5);

wei6 = new MCAModelRenderer(this, "wei6", 54, 42);
wei6.mirror = false;
wei6.addBox(-1.5F, -1.5F, -10.0F, 3, 3, 10);
wei6.setInitialRotationPoint(0.0F, 0.0F, -8.0F);
wei6.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei6.setTextureSize(128, 128);
parts.put(wei6.boxName, wei6);
wei5.addChild(wei6);

}

@Override
public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) 
{
EntityShuHu entity = (EntityShuHu)par1Entity;

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