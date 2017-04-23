package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.shuhu;

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
frame0.modelRenderersRotations.put("rightwing1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersRotations.put("lettwing1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame0.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame0.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(0, frame0);

KeyFrame frame1 = new KeyFrame();
frame1.modelRenderersRotations.put("rightwing1", new Quaternion(-0.20396979F, 0.23935208F, -0.12994309F, 0.94033056F));
frame1.modelRenderersRotations.put("lettwing1", new Quaternion(-0.20396979F, -0.23935208F, 0.12994309F, 0.94033056F));
frame1.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame1.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(1, frame1);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("rightwing1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame2.modelRenderersRotations.put("lettwing1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame2.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame2.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(2, frame2);

KeyFrame frame3 = new KeyFrame();
frame3.modelRenderersRotations.put("rightwing1", new Quaternion(0.16338794F, -0.012179108F, 0.17830665F, 0.97023845F));
frame3.modelRenderersRotations.put("lettwing1", new Quaternion(0.16338794F, 0.012179108F, -0.17830665F, 0.97023845F));
frame3.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame3.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(3, frame3);

KeyFrame frame4 = new KeyFrame();
frame4.modelRenderersRotations.put("rightwing1", new Quaternion(0.0F, 0.08715574F, 0.0F, 0.9961947F));
frame4.modelRenderersRotations.put("lettwing1", new Quaternion(0.0F, -0.08715574F, 0.0F, 0.9961947F));
frame4.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame4.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(4, frame4);

}
}