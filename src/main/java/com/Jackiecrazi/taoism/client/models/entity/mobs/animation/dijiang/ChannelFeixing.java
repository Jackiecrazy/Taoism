package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.dijiang;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.KeyFrame;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Vector3f;

public class ChannelFeixing extends Channel {
	public ChannelFeixing(String _name, float _fps, int _totalFrames, byte _mode) {
		super(_name, _fps, _totalFrames, _mode);
	}

	@Override
	protected void initializeAllFrames() {
KeyFrame frame0 = new KeyFrame();
frame0.modelRenderersRotations.put("zuoqianchi1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("zuohouchi1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("youhouchi1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("youqianchi1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("zuoqianchi2", new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F));
frame0.modelRenderersRotations.put("youqianchi2", new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F));
frame0.modelRenderersTranslations.put("zuoqianchi1", new Vector3f(8.0F, 8.0F, -1.0F));
frame0.modelRenderersTranslations.put("zuohouchi1", new Vector3f(9.0F, 6.0F, -23.0F));
frame0.modelRenderersTranslations.put("youhouchi1", new Vector3f(-9.0F, 6.0F, -23.0F));
frame0.modelRenderersTranslations.put("youqianchi1", new Vector3f(-8.0F, 8.0F, -1.0F));
frame0.modelRenderersTranslations.put("zuoqianchi2", new Vector3f(20.0F, -1.0F, 0.0F));
frame0.modelRenderersTranslations.put("youqianchi2", new Vector3f(-20.0F, -1.0F, 0.0F));
keyFrames.put(0, frame0);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("zuoqianchi1", new Quaternion(-0.11456661F, -0.18526384F, 0.15545481F, 0.9635276F));
frame2.modelRenderersRotations.put("zuohouchi1", new Quaternion(0.015134433F, -0.085831635F, -0.1729874F, 0.98106027F));
frame2.modelRenderersRotations.put("youhouchi1", new Quaternion(0.015134435F, 0.08583164F, 0.1729874F, 0.98106027F));
frame2.modelRenderersRotations.put("youqianchi1", new Quaternion(0.05448872F, 0.15545481F, -0.18526384F, 0.9687838F));
frame2.modelRenderersRotations.put("zuoqianchi2", new Quaternion(0.10058187F, 0.17980985F, 0.07042818F, 0.976008F));
frame2.modelRenderersRotations.put("youqianchi2", new Quaternion(-0.07042818F, -0.1648484F, -0.10058187F, 0.9786461F));
frame2.modelRenderersTranslations.put("zuoqianchi1", new Vector3f(8.0F, 8.0F, -1.0F));
frame2.modelRenderersTranslations.put("zuohouchi1", new Vector3f(9.0F, 6.0000005F, -23.0F));
frame2.modelRenderersTranslations.put("youhouchi1", new Vector3f(-9.0F, 6.0F, -23.0F));
frame2.modelRenderersTranslations.put("youqianchi1", new Vector3f(-8.0F, 8.0F, -1.0F));
frame2.modelRenderersTranslations.put("zuoqianchi2", new Vector3f(20.0F, -1.0F, 0.0F));
frame2.modelRenderersTranslations.put("youqianchi2", new Vector3f(-20.0F, -1.0F, 0.0F));
keyFrames.put(2, frame2);

KeyFrame frame6 = new KeyFrame();
frame6.modelRenderersRotations.put("zuoqianchi1", new Quaternion(0.08583164F, -0.015134435F, -0.1729874F, 0.98106027F));
frame6.modelRenderersRotations.put("zuohouchi1", new Quaternion(-0.015134432F, -0.08583163F, 0.1729874F, 0.98106027F));
frame6.modelRenderersRotations.put("youhouchi1", new Quaternion(-0.015134433F, 0.085831635F, -0.1729874F, 0.98106027F));
frame6.modelRenderersRotations.put("youqianchi1", new Quaternion(-0.08583164F, -0.015134435F, 0.1729874F, 0.98106027F));
frame6.modelRenderersRotations.put("zuoqianchi2", new Quaternion(-0.10058187F, 0.17980985F, -0.07042818F, 0.976008F));
frame6.modelRenderersRotations.put("youqianchi2", new Quaternion(0.07042818F, -0.1648484F, 0.10058187F, 0.9786461F));
frame6.modelRenderersTranslations.put("zuoqianchi1", new Vector3f(8.0F, 8.0F, -1.0F));
frame6.modelRenderersTranslations.put("zuohouchi1", new Vector3f(9.0F, 6.0F, -23.0F));
frame6.modelRenderersTranslations.put("youhouchi1", new Vector3f(-9.0F, 6.0F, -23.0F));
frame6.modelRenderersTranslations.put("youqianchi1", new Vector3f(-8.0F, 8.0F, -1.0F));
frame6.modelRenderersTranslations.put("zuoqianchi2", new Vector3f(20.0F, -1.0F, 0.0F));
frame6.modelRenderersTranslations.put("youqianchi2", new Vector3f(-20.0F, -1.0F, 0.0F));
keyFrames.put(6, frame6);

KeyFrame frame9 = new KeyFrame();
frame9.modelRenderersRotations.put("zuoqianchi1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame9.modelRenderersRotations.put("zuohouchi1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame9.modelRenderersRotations.put("youhouchi1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame9.modelRenderersRotations.put("youqianchi1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame9.modelRenderersRotations.put("zuoqianchi2", new Quaternion(0.0F, 0.17364818F, 0.0F, 0.9848077F));
frame9.modelRenderersRotations.put("youqianchi2", new Quaternion(0.0F, -0.17364818F, 0.0F, 0.9848077F));
frame9.modelRenderersTranslations.put("zuoqianchi1", new Vector3f(8.0F, 8.0F, -1.0F));
frame9.modelRenderersTranslations.put("zuohouchi1", new Vector3f(9.0F, 6.0F, -23.0F));
frame9.modelRenderersTranslations.put("youhouchi1", new Vector3f(-9.0F, 6.0F, -23.0F));
frame9.modelRenderersTranslations.put("youqianchi1", new Vector3f(-8.0F, 8.0F, -1.0F));
frame9.modelRenderersTranslations.put("zuoqianchi2", new Vector3f(20.0F, -1.0F, 0.0F));
frame9.modelRenderersTranslations.put("youqianchi2", new Vector3f(-20.0F, -1.0F, 0.0F));
keyFrames.put(9, frame9);

}
}