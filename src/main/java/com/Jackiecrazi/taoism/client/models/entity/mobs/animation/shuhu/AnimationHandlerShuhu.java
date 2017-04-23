
package com.Jackiecrazi.taoism.client.models.entity.mobs.animation.shuhu;

import java.util.HashMap;

import com.Jackiecrazi.taoism.common.MCACommonLibrary.IMCAnimatedEntity;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.AnimationHandler;
import com.Jackiecrazi.taoism.common.MCACommonLibrary.animation.Channel;

public class AnimationHandlerShuhu extends AnimationHandler {
	/** Map with all the animations. */
	public static HashMap<String, Channel> animChannels = new HashMap<String, Channel>();
static
{
animChannels.put("xingzou", new ChannelXingzou("xingzou", 4.0F, 5, Channel.LOOP));
animChannels.put("benpao", new ChannelBenpao("benpao", 4.0F, 5, Channel.LOOP));
animChannels.put("feixing", new ChannelFeixing("feixing", 3.0F, 5, Channel.LOOP));
}
	public AnimationHandlerShuhu(IMCAnimatedEntity entity) {
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