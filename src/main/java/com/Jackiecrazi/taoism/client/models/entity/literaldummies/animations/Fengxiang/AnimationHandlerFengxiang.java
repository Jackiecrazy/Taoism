package com.Jackiecrazi.taoism.client.models.entity.literaldummies.animations.Fengxiang;

import java.util.HashMap;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;

public class AnimationHandlerFengxiang extends AnimationHandler {
	/** Map with all the animations. */
	public static HashMap<String, Channel> animChannels = new HashMap<String, Channel>();
static
{
animChannels.put("gufeng", new ChannelGufeng("gufeng", 1.0F, 5, Channel.LOOP));
}
	public AnimationHandlerFengxiang(IMCAnimatedEntity entity) {
		super(entity);
	}

	@Override
	public void activateAnimation(String name, float startingFrame) {
		super.activateAnimation(animChannels, name, startingFrame);
	}

	@Override
	public void stopAnimation(String name) {
		super.stopAnimation(animChannels, name);
	}

	@Override
	public void fireAnimationEventClientSide(Channel anim, float prevFrame, float frame) {
	}

	@Override
	public void fireAnimationEventServerSide(Channel anim, float prevFrame, float frame) {
	}}