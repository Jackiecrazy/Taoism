package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.dijiang;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.KeyFrame;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Vector3f;

public class ChannelBaiwei extends Channel {
	public ChannelBaiwei(String _name, float _fps, int _totalFrames, byte _mode) {
		super(_name, _fps, _totalFrames, _mode);
	}

	@Override
	protected void initializeAllFrames() {
KeyFrame frame0 = new KeyFrame();
frame0.modelRenderersRotations.put("wei1", new Quaternion(0.38268346F, 0.0F, 0.0F, 0.9238795F));
frame0.modelRenderersRotations.put("wei2", new Quaternion(-0.309017F, 0.0F, 0.0F, 0.95105654F));
frame0.modelRenderersTranslations.put("wei1", new Vector3f(0.0F, 6.0F, -3.0F));
frame0.modelRenderersTranslations.put("wei2", new Vector3f(0.0F, -16.0F, -2.5F));
keyFrames.put(0, frame0);

KeyFrame frame1 = new KeyFrame();
frame1.modelRenderersRotations.put("wei1", new Quaternion(0.5F, 0.0F, 0.0F, 0.8660254F));
frame1.modelRenderersRotations.put("wei2", new Quaternion(-0.2079117F, 0.0F, 0.0F, 0.9781476F));
frame1.modelRenderersTranslations.put("wei1", new Vector3f(0.0F, 6.0F, -3.0F));
frame1.modelRenderersTranslations.put("wei2", new Vector3f(0.0F, -16.0F, -2.5F));
keyFrames.put(1, frame1);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("wei1", new Quaternion(0.38268346F, 0.0F, 0.0F, 0.9238795F));
frame2.modelRenderersRotations.put("wei2", new Quaternion(-0.309017F, 0.0F, 0.0F, 0.95105654F));
frame2.modelRenderersTranslations.put("wei1", new Vector3f(0.0F, 6.0F, -3.0F));
frame2.modelRenderersTranslations.put("wei2", new Vector3f(0.0F, -16.0F, -2.5F));
keyFrames.put(2, frame2);

KeyFrame frame3 = new KeyFrame();
frame3.modelRenderersRotations.put("wei1", new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame3.modelRenderersRotations.put("wei2", new Quaternion(-0.40673664F, 0.0F, 0.0F, 0.9135454F));
frame3.modelRenderersTranslations.put("wei1", new Vector3f(0.0F, 6.0F, -3.0F));
frame3.modelRenderersTranslations.put("wei2", new Vector3f(0.0F, -16.0F, -2.5F));
keyFrames.put(3, frame3);

KeyFrame frame4 = new KeyFrame();
frame4.modelRenderersRotations.put("wei1", new Quaternion(0.38268346F, 0.0F, 0.0F, 0.9238795F));
frame4.modelRenderersRotations.put("wei2", new Quaternion(-0.309017F, 0.0F, 0.0F, 0.95105654F));
frame4.modelRenderersTranslations.put("wei1", new Vector3f(0.0F, 6.0F, -3.0F));
frame4.modelRenderersTranslations.put("wei2", new Vector3f(0.0F, -16.0F, -2.5F));
keyFrames.put(4, frame4);

}
}