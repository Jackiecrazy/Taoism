package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.shuhu;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.KeyFrame;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Quaternion;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.math.Vector3f;

public class ChannelBenpao extends Channel {
	public ChannelBenpao(String _name, float _fps, int _totalFrames, byte _mode) {
		super(_name, _fps, _totalFrames, _mode);
	}

	@Override
	protected void initializeAllFrames() {
KeyFrame frame0 = new KeyFrame();
frame0.modelRenderersRotations.put("rightwing1", new Quaternion(0.18119794F, -0.05600989F, -0.50066054F, 0.8446119F));
frame0.modelRenderersRotations.put("lettwing1", new Quaternion(0.18119794F, 0.05600989F, 0.50066054F, 0.8446119F));
frame0.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame0.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(0, frame0);

KeyFrame frame1 = new KeyFrame();
frame1.modelRenderersRotations.put("rightbackleg1", new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame1.modelRenderersRotations.put("rightwing1", new Quaternion(0.20599112F, -0.020891175F, -0.38907772F, 0.89763564F));
frame1.modelRenderersRotations.put("leftbackleg1", new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame1.modelRenderersRotations.put("lettwing1", new Quaternion(0.20599112F, 0.020891175F, 0.38907772F, 0.89763564F));
frame1.modelRenderersRotations.put("leftfrontleg1", new Quaternion(-0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame1.modelRenderersRotations.put("rightfrontleg1", new Quaternion(-0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame1.modelRenderersTranslations.put("rightbackleg1", new Vector3f(-5.0F, 3.0F, -18.5F));
frame1.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame1.modelRenderersTranslations.put("leftbackleg1", new Vector3f(5.0F, 3.0F, -18.5F));
frame1.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
frame1.modelRenderersTranslations.put("leftfrontleg1", new Vector3f(5.5F, 3.0F, 0.5F));
frame1.modelRenderersTranslations.put("rightfrontleg1", new Vector3f(-5.5F, 3.0F, 0.5F));
keyFrames.put(1, frame1);

KeyFrame frame2 = new KeyFrame();
frame2.modelRenderersRotations.put("rightwing1", new Quaternion(0.22725974F, 0.014585018F, -0.2708376F, 0.93530065F));
frame2.modelRenderersRotations.put("lettwing1", new Quaternion(0.22725974F, -0.014585018F, 0.2708376F, 0.93530065F));
frame2.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame2.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
keyFrames.put(2, frame2);

KeyFrame frame3 = new KeyFrame();
frame3.modelRenderersRotations.put("rightbackleg1", new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F));
frame3.modelRenderersRotations.put("rightwing1", new Quaternion(0.20599112F, -0.020891175F, -0.3890777F, 0.89763564F));
frame3.modelRenderersRotations.put("leftbackleg1", new Quaternion(-0.08715574F, 0.0F, 0.0F, 0.9961947F));
frame3.modelRenderersRotations.put("lettwing1", new Quaternion(0.20599112F, 0.020891175F, 0.3890777F, 0.89763564F));
frame3.modelRenderersRotations.put("leftfrontleg1", new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame3.modelRenderersRotations.put("rightfrontleg1", new Quaternion(0.25881904F, 0.0F, 0.0F, 0.9659258F));
frame3.modelRenderersTranslations.put("rightbackleg1", new Vector3f(-5.0F, 3.0F, -18.5F));
frame3.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame3.modelRenderersTranslations.put("leftbackleg1", new Vector3f(5.0F, 3.0F, -18.5F));
frame3.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
frame3.modelRenderersTranslations.put("leftfrontleg1", new Vector3f(5.5F, 3.0F, 0.5F));
frame3.modelRenderersTranslations.put("rightfrontleg1", new Vector3f(-5.5F, 3.0F, 0.5F));
keyFrames.put(3, frame3);

KeyFrame frame4 = new KeyFrame();
frame4.modelRenderersRotations.put("rightbackleg1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame4.modelRenderersRotations.put("rightwing1", new Quaternion(0.18119794F, -0.05600989F, -0.50066054F, 0.8446119F));
frame4.modelRenderersRotations.put("leftbackleg1", new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
frame4.modelRenderersRotations.put("lettwing1", new Quaternion(0.18119794F, 0.05600989F, 0.50066054F, 0.8446119F));
frame4.modelRenderersRotations.put("rightfrontleg1", new Quaternion(0.1305262F, 0.0F, 0.0F, 0.9914449F));
frame4.modelRenderersRotations.put("leftfrontleg1", new Quaternion(0.1305262F, 0.0F, 0.0F, 0.9914449F));
frame4.modelRenderersTranslations.put("rightbackleg1", new Vector3f(-5.0F, 3.0F, -18.5F));
frame4.modelRenderersTranslations.put("rightwing1", new Vector3f(-3.0F, 2.0F, -6.0F));
frame4.modelRenderersTranslations.put("leftbackleg1", new Vector3f(5.0F, 3.0F, -18.5F));
frame4.modelRenderersTranslations.put("lettwing1", new Vector3f(3.0F, 2.0F, -6.0F));
frame4.modelRenderersTranslations.put("rightfrontleg1", new Vector3f(-5.5F, 3.0F, 0.5F));
frame4.modelRenderersTranslations.put("leftfrontleg1", new Vector3f(5.5F, 3.0F, 0.5F));
keyFrames.put(4, frame4);

}
}