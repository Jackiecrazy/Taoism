package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.luoyu;

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
frame0.modelRenderersRotations.put("yugu21", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("yigu11", new Quaternion(1.3510566E-18F, 0.08715574F, 1.544265E-17F, 0.9961947F));
frame0.modelRenderersTranslations.put("yugu21", new Vector3f(-3.0F, 0.0F, -1.0F));
frame0.modelRenderersTranslations.put("yigu11", new Vector3f(3.0F, 0.0F, 0.0F));
keyFrames.put(0, frame0);

KeyFrame frame1 = new KeyFrame();
frame1.modelRenderersRotations.put("yugu21", new Quaternion(0.0F, 0.0F, -0.1305262F, 0.9914449F));
frame1.modelRenderersRotations.put("yigu11", new Quaternion(0.0F, 0.0F, 0.1305262F, 0.9914449F));
frame1.modelRenderersTranslations.put("yugu21", new Vector3f(-3.0F, 0.0F, -1.0F));
frame1.modelRenderersTranslations.put("yigu11", new Vector3f(3.0F, 0.0F, 0.0F));
keyFrames.put(1, frame1);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("yugu21", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame2.modelRenderersRotations.put("yigu11", new Quaternion(1.3510566E-18F, 0.08715574F, 1.544265E-17F, 0.9961947F));
frame2.modelRenderersTranslations.put("yugu21", new Vector3f(-3.0F, 0.0F, -1.0F));
frame2.modelRenderersTranslations.put("yigu11", new Vector3f(3.0F, 0.0F, 0.0F));
keyFrames.put(2, frame2);

KeyFrame frame3 = new KeyFrame();
frame3.modelRenderersRotations.put("yugu21", new Quaternion(-0.022665637F, -0.17216259F, 0.12854321F, 0.9763826F));
frame3.modelRenderersRotations.put("yigu11", new Quaternion(-0.022665637F, 0.17216259F, -0.12854321F, 0.9763826F));
frame3.modelRenderersTranslations.put("yugu21", new Vector3f(-3.0F, 0.0F, -1.0F));
frame3.modelRenderersTranslations.put("yigu11", new Vector3f(3.0F, 0.0F, 0.0F));
keyFrames.put(3, frame3);

KeyFrame frame4 = new KeyFrame();
frame4.modelRenderersRotations.put("yugu21", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame4.modelRenderersRotations.put("yigu11", new Quaternion(1.3510566E-18F, 0.08715574F, 1.544265E-17F, 0.9961947F));
frame4.modelRenderersTranslations.put("yugu21", new Vector3f(-3.0F, 0.0F, -1.0F));
frame4.modelRenderersTranslations.put("yigu11", new Vector3f(3.0F, 0.0F, 0.0F));
keyFrames.put(4, frame4);

}
}