package com.Jackiecrazi.taoism.client.models.entity.literaldummies.animations.Fengxiang;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.KeyFrame;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Vector3f;

public class ChannelGufeng extends Channel {
	public ChannelGufeng(String _name, float _fps, int _totalFrames, byte _mode) {
		super(_name, _fps, _totalFrames, _mode);
	}

	@Override
	protected void initializeAllFrames() {
KeyFrame frame0 = new KeyFrame();
frame0.modelRenderersRotations.put("xiangba1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame0.modelRenderersTranslations.put("xiangba1", new Vector3f(0.0F, 3.0F, 9.0F));
keyFrames.put(0, frame0);

KeyFrame frame1 = new KeyFrame();
frame1.modelRenderersRotations.put("xiangba1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame1.modelRenderersTranslations.put("xiangba1", new Vector3f(0.0F, 3.0F, 13.0F));
keyFrames.put(1, frame1);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("xiangba1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame2.modelRenderersTranslations.put("xiangba1", new Vector3f(0.0F, 3.0F, 17.0F));
keyFrames.put(2, frame2);

KeyFrame frame3 = new KeyFrame();
frame3.modelRenderersRotations.put("xiangba1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame3.modelRenderersTranslations.put("xiangba1", new Vector3f(0.0F, 3.0F, 13.0F));
keyFrames.put(3, frame3);

KeyFrame frame4 = new KeyFrame();
frame4.modelRenderersRotations.put("xiangba1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame4.modelRenderersTranslations.put("xiangba1", new Vector3f(0.0F, 3.0F, 9.0F));
keyFrames.put(4, frame4);

}
}