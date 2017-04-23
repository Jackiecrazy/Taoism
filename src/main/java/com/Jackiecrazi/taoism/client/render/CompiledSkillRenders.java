package com.Jackiecrazi.taoism.client.render;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan.LianDanRenderer;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi.LianQiRenderer;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.SpiritRenderer;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongRenderer;

public class CompiledSkillRenders {
	public static void initLender() {
		Taoism.addSpecialEventBus(new RenderQi(WayofConfig.QiX,WayofConfig.QiY));
		Taoism.addSpecialEventBus(new RenderLing(WayofConfig.LingX,WayofConfig.LingY));
		Taoism.addSpecialEventBus(new SpiritRenderer(WayofConfig.QiSkillX,WayofConfig.QiSkillY));
		Taoism.addSpecialEventBus(new WuGongRenderer(WayofConfig.WuGongSkillX,WayofConfig.WuGongSkillY));
		Taoism.addSpecialEventBus(new LianDanRenderer(WayofConfig.LianDanSkillX,WayofConfig.LianDanSkillY));
		//Taoism.addSpecialEventBus(new LianQiRenderer(WayofConfig.LianQiSkillX,WayofConfig.LianQiSkillY));
	}
}
