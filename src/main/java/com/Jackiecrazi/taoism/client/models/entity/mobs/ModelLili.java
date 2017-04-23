package com.Jackiecrazi.taoism.client.models.entity.mobs;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import com.Jackiecrazi.taoism.client.MCAClientLibrary.MCAModelRenderer;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.MCAVersionChecker;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Matrix4f;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLiLi;

public class ModelLili extends ModelBase {
public final int MCA_MIN_REQUESTED_VERSION = 5;
public HashMap<String, MCAModelRenderer> parts = new HashMap<String, MCAModelRenderer>();

MCAModelRenderer body1;
MCAModelRenderer body2;
MCAModelRenderer bozi;
MCAModelRenderer zuoqiantui1;
MCAModelRenderer youqiantui1;
MCAModelRenderer zongmao;
MCAModelRenderer wei1;
MCAModelRenderer zuohoutui1;
MCAModelRenderer youhoutui1;
MCAModelRenderer head1;
MCAModelRenderer zuoqiantui2;
MCAModelRenderer youqiantui2;
MCAModelRenderer wei2;
MCAModelRenderer zuohoutui2;
MCAModelRenderer youhoutui2;
MCAModelRenderer zui1;
MCAModelRenderer youer;
MCAModelRenderer zuoer;
MCAModelRenderer zuoqiantui3;
MCAModelRenderer youqiantui3;
MCAModelRenderer wei3;
MCAModelRenderer zuohoutui3;
MCAModelRenderer youhoutui3;
MCAModelRenderer zui2;
MCAModelRenderer zui3;
MCAModelRenderer ya11;
MCAModelRenderer ya21;
MCAModelRenderer zuoqianzhang;
MCAModelRenderer youqianzhang;
MCAModelRenderer zuohouzhang;
MCAModelRenderer youhouzhang;
MCAModelRenderer bizi1;
MCAModelRenderer ya12;
MCAModelRenderer ya22;
MCAModelRenderer zuoqianzhi1;
MCAModelRenderer zuoqianzhi2;
MCAModelRenderer zuoqianzhi3;
MCAModelRenderer zuoqianzhi4;
MCAModelRenderer youqianzhi1;
MCAModelRenderer youqianzhi2;
MCAModelRenderer youqianzhi3;
MCAModelRenderer youqianzhi4;
MCAModelRenderer zuohouzhi1;
MCAModelRenderer zuohouzhi2;
MCAModelRenderer zuohouzhi3;
MCAModelRenderer zuohouzhi4;
MCAModelRenderer youhouzhi1;
MCAModelRenderer youhouzhi2;
MCAModelRenderer youhouzhi3;
MCAModelRenderer youhouzhi4;
MCAModelRenderer ya13;
MCAModelRenderer ya14;

public ModelLili()
{
MCAVersionChecker.checkForLibraryVersion(getClass(), MCA_MIN_REQUESTED_VERSION);

textureWidth = 128;
textureHeight = 128;

body1 = new MCAModelRenderer(this, "body1", 0, 104);
body1.mirror = false;
body1.addBox(-6.0F, -6.0F, -12.0F, 12, 12, 12);
body1.setInitialRotationPoint(0.0F, -17.5F, 0.0F);
body1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body1.setTextureSize(128, 128);
parts.put(body1.boxName, body1);

body2 = new MCAModelRenderer(this, "body2", 0, 81);
body2.mirror = false;
body2.addBox(-5.0F, -5.0F, -12.0F, 10, 10, 12);
body2.setInitialRotationPoint(0.0F, 0.0F, -12.0F);
body2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
body2.setTextureSize(128, 128);
parts.put(body2.boxName, body2);
body1.addChild(body2);

bozi = new MCAModelRenderer(this, "bozi", 66, 0);
bozi.mirror = false;
bozi.addBox(-4.0F, -3.0F, -5.0F, 8, 8, 7);
bozi.setInitialRotationPoint(0.0F, 4.0F, 1.0F);
bozi.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.34202012F, 0.0F, 0.0F, 0.9396926F)).transpose());
bozi.setTextureSize(128, 128);
parts.put(bozi.boxName, bozi);
body1.addChild(bozi);

zuoqiantui1 = new MCAModelRenderer(this, "zuoqiantui1", 100, 84);
zuoqiantui1.mirror = false;
zuoqiantui1.addBox(0.0F, -5.0F, -4.0F, 5, 9, 8);
zuoqiantui1.setInitialRotationPoint(4.0F, 0.0F, -5.0F);
zuoqiantui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zuoqiantui1.setTextureSize(128, 128);
parts.put(zuoqiantui1.boxName, zuoqiantui1);
body1.addChild(zuoqiantui1);

youqiantui1 = new MCAModelRenderer(this, "youqiantui1", 100, 84);
youqiantui1.mirror = false;
youqiantui1.addBox(-5.0F, -5.0F, -4.0F, 5, 9, 8);
youqiantui1.setInitialRotationPoint(-4.0F, 0.0F, -5.0F);
youqiantui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
youqiantui1.setTextureSize(128, 128);
parts.put(youqiantui1.boxName, youqiantui1);
body1.addChild(youqiantui1);

zongmao = new MCAModelRenderer(this, "zongmao", 0, 0);
zongmao.mirror = false;
zongmao.addBox(-2.0F, 0.0F, -23.0F, 4, 3, 23);
zongmao.setInitialRotationPoint(0.0F, 5.0F, -1.0F);
zongmao.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.034899496F, 0.0F, 0.0F, 0.99939084F)).transpose());
zongmao.setTextureSize(128, 128);
parts.put(zongmao.boxName, zongmao);
body1.addChild(zongmao);

wei1 = new MCAModelRenderer(this, "wei1", 97, 0);
wei1.mirror = false;
wei1.addBox(-1.0F, -1.0F, -14.0F, 2, 2, 14);
wei1.setInitialRotationPoint(0.0F, 3.0F, -11.0F);
wei1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
wei1.setTextureSize(128, 128);
parts.put(wei1.boxName, wei1);
body2.addChild(wei1);

zuohoutui1 = new MCAModelRenderer(this, "zuohoutui1", 0, 60);
zuohoutui1.mirror = false;
zuohoutui1.addBox(0.0F, -4.0F, -3.0F, 5, 7, 8);
zuohoutui1.setInitialRotationPoint(4.0F, 0.0F, -8.0F);
zuohoutui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zuohoutui1.setTextureSize(128, 128);
parts.put(zuohoutui1.boxName, zuohoutui1);
body2.addChild(zuohoutui1);

youhoutui1 = new MCAModelRenderer(this, "youhoutui1", 0, 60);
youhoutui1.mirror = false;
youhoutui1.addBox(-5.0F, -4.0F, -3.0F, 5, 7, 8);
youhoutui1.setInitialRotationPoint(-3.0F, 0.0F, -8.0F);
youhoutui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
youhoutui1.setTextureSize(128, 128);
parts.put(youhoutui1.boxName, youhoutui1);
body2.addChild(youhoutui1);

head1 = new MCAModelRenderer(this, "head1", 48, 109);
head1.mirror = false;
head1.addBox(-5.0F, -5.0F, -2.0F, 10, 9, 9);
head1.setInitialRotationPoint(0.0F, 2.0F, 0.0F);
head1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
head1.setTextureSize(128, 128);
parts.put(head1.boxName, head1);
bozi.addChild(head1);

zuoqiantui2 = new MCAModelRenderer(this, "zuoqiantui2", 107, 70);
zuoqiantui2.mirror = false;
zuoqiantui2.addBox(-2.0F, -7.0F, -5.0F, 4, 7, 6);
zuoqiantui2.setInitialRotationPoint(2.5F, -4.0F, 3.0F);
zuoqiantui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
zuoqiantui2.setTextureSize(128, 128);
parts.put(zuoqiantui2.boxName, zuoqiantui2);
zuoqiantui1.addChild(zuoqiantui2);

youqiantui2 = new MCAModelRenderer(this, "youqiantui2", 107, 70);
youqiantui2.mirror = false;
youqiantui2.addBox(-2.0F, -7.0F, -5.0F, 4, 7, 6);
youqiantui2.setInitialRotationPoint(-2.5F, -4.0F, 3.0F);
youqiantui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
youqiantui2.setTextureSize(128, 128);
parts.put(youqiantui2.boxName, youqiantui2);
youqiantui1.addChild(youqiantui2);

wei2 = new MCAModelRenderer(this, "wei2", 118, 0);
wei2.mirror = false;
wei2.addBox(-1.5F, -1.5F, -2.0F, 3, 3, 2);
wei2.setInitialRotationPoint(0.0F, 0.0F, -10.0F);
wei2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
wei2.setTextureSize(128, 128);
parts.put(wei2.boxName, wei2);
wei1.addChild(wei2);

zuohoutui2 = new MCAModelRenderer(this, "zuohoutui2", 0, 47);
zuohoutui2.mirror = false;
zuohoutui2.addBox(-2.0F, -6.0F, -6.0F, 4, 6, 6);
zuohoutui2.setInitialRotationPoint(2.5F, -4.0F, 4.0F);
zuohoutui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
zuohoutui2.setTextureSize(128, 128);
parts.put(zuohoutui2.boxName, zuohoutui2);
zuohoutui1.addChild(zuohoutui2);

youhoutui2 = new MCAModelRenderer(this, "youhoutui2", 0, 47);
youhoutui2.mirror = false;
youhoutui2.addBox(-2.0F, -6.0F, -6.0F, 4, 6, 6);
youhoutui2.setInitialRotationPoint(-2.5F, -4.0F, 4.0F);
youhoutui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F)).transpose());
youhoutui2.setTextureSize(128, 128);
parts.put(youhoutui2.boxName, youhoutui2);
youhoutui1.addChild(youhoutui2);

zui1 = new MCAModelRenderer(this, "zui1", 88, 119);
zui1.mirror = false;
zui1.addBox(-3.5F, -3.0F, 0.0F, 7, 5, 3);
zui1.setInitialRotationPoint(0.0F, -1.0F, 7.0F);
zui1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zui1.setTextureSize(128, 128);
parts.put(zui1.boxName, zui1);
head1.addChild(zui1);

youer = new MCAModelRenderer(this, "youer", 79, 103);
youer.mirror = false;
youer.addBox(-6.0F, -3.0F, 0.0F, 6, 5, 1);
youer.setInitialRotationPoint(-4.0F, 1.0F, 1.0F);
youer.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.024806706F, -0.2576275F, -0.09257989F, 0.9614789F)).transpose());
youer.setTextureSize(128, 128);
parts.put(youer.boxName, youer);
head1.addChild(youer);

zuoer = new MCAModelRenderer(this, "zuoer", 96, 103);
zuoer.mirror = false;
zuoer.addBox(0.0F, -3.0F, 0.0F, 6, 5, 1);
zuoer.setInitialRotationPoint(4.0F, 1.0F, 1.0F);
zuoer.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.024806706F, 0.2576275F, 0.09257989F, 0.9614789F)).transpose());
zuoer.setTextureSize(128, 128);
parts.put(zuoer.boxName, zuoer);
head1.addChild(zuoer);

zuoqiantui3 = new MCAModelRenderer(this, "zuoqiantui3", 115, 57);
zuoqiantui3.mirror = false;
zuoqiantui3.addBox(-1.5F, -8.0F, 0.0F, 3, 8, 3);
zuoqiantui3.setInitialRotationPoint(0.0F, -6.0F, -5.0F);
zuoqiantui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
zuoqiantui3.setTextureSize(128, 128);
parts.put(zuoqiantui3.boxName, zuoqiantui3);
zuoqiantui2.addChild(zuoqiantui3);

youqiantui3 = new MCAModelRenderer(this, "youqiantui3", 115, 57);
youqiantui3.mirror = false;
youqiantui3.addBox(-1.5F, -8.0F, 0.0F, 3, 8, 3);
youqiantui3.setInitialRotationPoint(0.0F, -6.0F, -5.0F);
youqiantui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
youqiantui3.setTextureSize(128, 128);
parts.put(youqiantui3.boxName, youqiantui3);
youqiantui2.addChild(youqiantui3);

wei3 = new MCAModelRenderer(this, "wei3", 118, 8);
wei3.mirror = false;
wei3.addBox(-0.5F, -0.5F, -3.0F, 1, 1, 3);
wei3.setInitialRotationPoint(0.0F, 0.0F, -4.0F);
wei3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.08715574F, 0.0F, 0.0F, 0.9961947F)).transpose());
wei3.setTextureSize(128, 128);
parts.put(wei3.boxName, wei3);
wei2.addChild(wei3);

zuohoutui3 = new MCAModelRenderer(this, "zuohoutui3", 0, 34);
zuohoutui3.mirror = false;
zuohoutui3.addBox(-1.5F, -8.0F, 0.0F, 3, 8, 4);
zuohoutui3.setInitialRotationPoint(0.0F, -6.0F, -5.0F);
zuohoutui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
zuohoutui3.setTextureSize(128, 128);
parts.put(zuohoutui3.boxName, zuohoutui3);
zuohoutui2.addChild(zuohoutui3);

youhoutui3 = new MCAModelRenderer(this, "youhoutui3", 0, 34);
youhoutui3.mirror = false;
youhoutui3.addBox(-1.5F, -8.0F, 0.0F, 3, 8, 4);
youhoutui3.setInitialRotationPoint(0.0F, -6.0F, -5.0F);
youhoutui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(-0.42261827F, 0.0F, 0.0F, 0.90630776F)).transpose());
youhoutui3.setTextureSize(128, 128);
parts.put(youhoutui3.boxName, youhoutui3);
youhoutui2.addChild(youhoutui3);

zui2 = new MCAModelRenderer(this, "zui2", 105, 114);
zui2.mirror = false;
zui2.addBox(-3.0F, -1.0F, 0.0F, 6, 2, 5);
zui2.setInitialRotationPoint(0.0F, -0.5F, 3.0F);
zui2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zui2.setTextureSize(128, 128);
parts.put(zui2.boxName, zui2);
zui1.addChild(zui2);

zui3 = new MCAModelRenderer(this, "zui3", 105, 104);
zui3.mirror = false;
zui3.addBox(-2.5F, 0.0F, 0.0F, 5, 1, 6);
zui3.setInitialRotationPoint(0.0F, -3.0F, 3.0F);
zui3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zui3.setTextureSize(128, 128);
parts.put(zui3.boxName, zui3);
zui1.addChild(zui3);

ya11 = new MCAModelRenderer(this, "ya11", 0, 76);
ya11.mirror = false;
ya11.addBox(0.0F, -1.0F, -1.0F, 5, 2, 2);
ya11.setInitialRotationPoint(2.0F, -1.0F, 2.0F);
ya11.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.32275844F, -0.35124236F, -0.20278989F, 0.8551795F)).transpose());
ya11.setTextureSize(128, 128);
parts.put(ya11.boxName, ya11);
zui1.addChild(ya11);

ya21 = new MCAModelRenderer(this, "ya21", 0, 76);
ya21.mirror = false;
ya21.addBox(-5.0F, -1.0F, -1.0F, 5, 2, 2);
ya21.setInitialRotationPoint(-2.0F, -1.0F, 2.0F);
ya21.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.32275844F, 0.35124236F, 0.20278989F, 0.8551795F)).transpose());
ya21.setTextureSize(128, 128);
parts.put(ya21.boxName, ya21);
zui1.addChild(ya21);

zuoqianzhang = new MCAModelRenderer(this, "zuoqianzhang", 107, 48);
zuoqianzhang.mirror = false;
zuoqianzhang.addBox(-2.0F, -2.0F, -3.0F, 4, 2, 6);
zuoqianzhang.setInitialRotationPoint(0.0F, -7.0F, 2.5F);
zuoqianzhang.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
zuoqianzhang.setTextureSize(128, 128);
parts.put(zuoqianzhang.boxName, zuoqianzhang);
zuoqiantui3.addChild(zuoqianzhang);

youqianzhang = new MCAModelRenderer(this, "youqianzhang", 107, 48);
youqianzhang.mirror = false;
youqianzhang.addBox(-2.0F, -2.0F, -3.0F, 4, 2, 6);
youqianzhang.setInitialRotationPoint(0.0F, -7.0F, 2.5F);
youqianzhang.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
youqianzhang.setTextureSize(128, 128);
parts.put(youqianzhang.boxName, youqianzhang);
youqiantui3.addChild(youqianzhang);

zuohouzhang = new MCAModelRenderer(this, "zuohouzhang", 34, 0);
zuohouzhang.mirror = false;
zuohouzhang.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 6);
zuohouzhang.setInitialRotationPoint(0.0F, -6.0F, 0.0F);
zuohouzhang.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
zuohouzhang.setTextureSize(128, 128);
parts.put(zuohouzhang.boxName, zuohouzhang);
zuohoutui3.addChild(zuohouzhang);

youhouzhang = new MCAModelRenderer(this, "youhouzhang", 34, 0);
youhouzhang.mirror = false;
youhouzhang.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 6);
youhouzhang.setInitialRotationPoint(0.0F, -6.0F, 0.0F);
youhouzhang.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.17364818F, 0.0F, 0.0F, 0.9848077F)).transpose());
youhouzhang.setTextureSize(128, 128);
parts.put(youhouzhang.boxName, youhouzhang);
youhoutui3.addChild(youhouzhang);

bizi1 = new MCAModelRenderer(this, "bizi1", 90, 112);
bizi1.mirror = false;
bizi1.addBox(-3.5F, 0.0F, 0.0F, 7, 3, 2);
bizi1.setInitialRotationPoint(0.0F, -1.0F, 5.0F);
bizi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
bizi1.setTextureSize(128, 128);
parts.put(bizi1.boxName, bizi1);
zui2.addChild(bizi1);

ya12 = new MCAModelRenderer(this, "ya12", 0, 76);
ya12.mirror = false;
ya12.addBox(0.0F, 0.0F, 0.0F, 4, 2, 2);
ya12.setInitialRotationPoint(5.0F, -1.0F, -1.0F);
ya12.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.42261827F, 0.0F, 0.90630776F)).transpose());
ya12.setTextureSize(128, 128);
parts.put(ya12.boxName, ya12);
ya11.addChild(ya12);

ya22 = new MCAModelRenderer(this, "ya22", 0, 76);
ya22.mirror = false;
ya22.addBox(-4.0F, 0.0F, 0.0F, 4, 2, 2);
ya22.setInitialRotationPoint(-5.0F, -1.0F, -1.0F);
ya22.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.42261827F, 0.0F, 0.90630776F)).transpose());
ya22.setTextureSize(128, 128);
parts.put(ya22.boxName, ya22);
ya21.addChild(ya22);

zuoqianzhi1 = new MCAModelRenderer(this, "zuoqianzhi1", 0, 0);
zuoqianzhi1.mirror = false;
zuoqianzhi1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 5);
zuoqianzhi1.setInitialRotationPoint(1.0F, -2.0F, 2.0F);
zuoqianzhi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.21643962F, 0.0F, 0.976296F)).transpose());
zuoqianzhi1.setTextureSize(128, 128);
parts.put(zuoqianzhi1.boxName, zuoqianzhi1);
zuoqianzhang.addChild(zuoqianzhi1);

zuoqianzhi2 = new MCAModelRenderer(this, "zuoqianzhi2", 0, 0);
zuoqianzhi2.mirror = false;
zuoqianzhi2.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 5);
zuoqianzhi2.setInitialRotationPoint(0.0F, -2.0F, 3.0F);
zuoqianzhi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zuoqianzhi2.setTextureSize(128, 128);
parts.put(zuoqianzhi2.boxName, zuoqianzhi2);
zuoqianzhang.addChild(zuoqianzhi2);

zuoqianzhi3 = new MCAModelRenderer(this, "zuoqianzhi3", 0, 0);
zuoqianzhi3.mirror = false;
zuoqianzhi3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
zuoqianzhi3.setInitialRotationPoint(-2.0F, -2.0F, 3.0F);
zuoqianzhi3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F)).transpose());
zuoqianzhi3.setTextureSize(128, 128);
parts.put(zuoqianzhi3.boxName, zuoqianzhi3);
zuoqianzhang.addChild(zuoqianzhi3);

zuoqianzhi4 = new MCAModelRenderer(this, "zuoqianzhi4", 0, 0);
zuoqianzhi4.mirror = false;
zuoqianzhi4.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2);
zuoqianzhi4.setInitialRotationPoint(-1.0F, -2.0F, -2.0F);
zuoqianzhi4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.25881904F, 0.0F, 0.9659258F)).transpose());
zuoqianzhi4.setTextureSize(128, 128);
parts.put(zuoqianzhi4.boxName, zuoqianzhi4);
zuoqianzhang.addChild(zuoqianzhi4);

youqianzhi1 = new MCAModelRenderer(this, "youqianzhi1", 0, 0);
youqianzhi1.mirror = false;
youqianzhi1.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 4);
youqianzhi1.setInitialRotationPoint(2.0F, -2.0F, 3.0F);
youqianzhi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F)).transpose());
youqianzhi1.setTextureSize(128, 128);
parts.put(youqianzhi1.boxName, youqianzhi1);
youqianzhang.addChild(youqianzhi1);

youqianzhi2 = new MCAModelRenderer(this, "youqianzhi2", 0, 0);
youqianzhi2.mirror = false;
youqianzhi2.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 5);
youqianzhi2.setInitialRotationPoint(0.0F, -2.0F, 3.0F);
youqianzhi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
youqianzhi2.setTextureSize(128, 128);
parts.put(youqianzhi2.boxName, youqianzhi2);
youqianzhang.addChild(youqianzhi2);

youqianzhi3 = new MCAModelRenderer(this, "youqianzhi3", 0, 0);
youqianzhi3.mirror = false;
youqianzhi3.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 5);
youqianzhi3.setInitialRotationPoint(-1.0F, -2.0F, 2.0F);
youqianzhi3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.21643962F, 0.0F, 0.976296F)).transpose());
youqianzhi3.setTextureSize(128, 128);
parts.put(youqianzhi3.boxName, youqianzhi3);
youqianzhang.addChild(youqianzhi3);

youqianzhi4 = new MCAModelRenderer(this, "youqianzhi4", 0, 0);
youqianzhi4.mirror = false;
youqianzhi4.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2);
youqianzhi4.setInitialRotationPoint(1.0F, -2.0F, -2.0F);
youqianzhi4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.25881904F, 0.0F, 0.9659258F)).transpose());
youqianzhi4.setTextureSize(128, 128);
parts.put(youqianzhi4.boxName, youqianzhi4);
youqianzhang.addChild(youqianzhi4);

zuohouzhi1 = new MCAModelRenderer(this, "zuohouzhi1", 0, 0);
zuohouzhi1.mirror = false;
zuohouzhi1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
zuohouzhi1.setInitialRotationPoint(1.0F, -2.0F, 5.0F);
zuohouzhi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.21643962F, 0.0F, 0.976296F)).transpose());
zuohouzhi1.setTextureSize(128, 128);
parts.put(zuohouzhi1.boxName, zuohouzhi1);
zuohouzhang.addChild(zuohouzhi1);

zuohouzhi2 = new MCAModelRenderer(this, "zuohouzhi2", 0, 0);
zuohouzhi2.mirror = false;
zuohouzhi2.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 4);
zuohouzhi2.setInitialRotationPoint(0.0F, -2.0F, 6.0F);
zuohouzhi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
zuohouzhi2.setTextureSize(128, 128);
parts.put(zuohouzhi2.boxName, zuohouzhi2);
zuohouzhang.addChild(zuohouzhi2);

zuohouzhi3 = new MCAModelRenderer(this, "zuohouzhi3", 0, 0);
zuohouzhi3.mirror = false;
zuohouzhi3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
zuohouzhi3.setInitialRotationPoint(-2.0F, -2.0F, 5.0F);
zuohouzhi3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.25881904F, 0.0F, 0.9659258F)).transpose());
zuohouzhi3.setTextureSize(128, 128);
parts.put(zuohouzhi3.boxName, zuohouzhi3);
zuohouzhang.addChild(zuohouzhi3);

zuohouzhi4 = new MCAModelRenderer(this, "zuohouzhi4", 0, 0);
zuohouzhi4.mirror = false;
zuohouzhi4.addBox(0.0F, 0.0F, -3.0F, 1, 1, 3);
zuohouzhi4.setInitialRotationPoint(-1.0F, -2.0F, 1.0F);
zuohouzhi4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.25881904F, 0.0F, 0.9659258F)).transpose());
zuohouzhi4.setTextureSize(128, 128);
parts.put(zuohouzhi4.boxName, zuohouzhi4);
zuohouzhang.addChild(zuohouzhi4);

youhouzhi1 = new MCAModelRenderer(this, "youhouzhi1", 0, 0);
youhouzhi1.mirror = false;
youhouzhi1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
youhouzhi1.setInitialRotationPoint(1.0F, -2.0F, 5.0F);
youhouzhi1.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.25881904F, 0.0F, 0.9659258F)).transpose());
youhouzhi1.setTextureSize(128, 128);
parts.put(youhouzhi1.boxName, youhouzhi1);
youhouzhang.addChild(youhouzhi1);

youhouzhi2 = new MCAModelRenderer(this, "youhouzhi2", 0, 0);
youhouzhi2.mirror = false;
youhouzhi2.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 4);
youhouzhi2.setInitialRotationPoint(0.0F, -2.0F, 6.0F);
youhouzhi2.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F)).transpose());
youhouzhi2.setTextureSize(128, 128);
parts.put(youhouzhi2.boxName, youhouzhi2);
youhouzhang.addChild(youhouzhi2);

youhouzhi3 = new MCAModelRenderer(this, "youhouzhi3", 0, 0);
youhouzhi3.mirror = false;
youhouzhi3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
youhouzhi3.setInitialRotationPoint(-2.0F, -2.0F, 5.0F);
youhouzhi3.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.21643962F, 0.0F, 0.976296F)).transpose());
youhouzhi3.setTextureSize(128, 128);
parts.put(youhouzhi3.boxName, youhouzhi3);
youhouzhang.addChild(youhouzhi3);

youhouzhi4 = new MCAModelRenderer(this, "youhouzhi4", 0, 0);
youhouzhi4.mirror = false;
youhouzhi4.addBox(-1.0F, 0.0F, -3.0F, 1, 1, 3);
youhouzhi4.setInitialRotationPoint(1.0F, -2.0F, 1.0F);
youhouzhi4.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, -0.25881904F, 0.0F, 0.9659258F)).transpose());
youhouzhi4.setTextureSize(128, 128);
parts.put(youhouzhi4.boxName, youhouzhi4);
youhouzhang.addChild(youhouzhi4);

ya13 = new MCAModelRenderer(this, "ya13", 0, 76);
ya13.mirror = false;
ya13.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2);
ya13.setInitialRotationPoint(4.0F, 0.0F, 0.0F);
ya13.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, 0.5F, 0.8660254F)).transpose());
ya13.setTextureSize(128, 128);
parts.put(ya13.boxName, ya13);
ya12.addChild(ya13);

ya14 = new MCAModelRenderer(this, "ya14", 0, 76);
ya14.mirror = false;
ya14.addBox(-3.0F, 0.0F, 0.0F, 3, 2, 2);
ya14.setInitialRotationPoint(-4.0F, 0.0F, 0.0F);
ya14.setInitialRotationMatrix(new Matrix4f().set(new Quaternion(0.0F, 0.0F, -0.5F, 0.8660254F)).transpose());
ya14.setTextureSize(128, 128);
parts.put(ya14.boxName, ya14);
ya22.addChild(ya14);

}

@Override
public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) 
{
EntityLiLi entity = (EntityLiLi)par1Entity;

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