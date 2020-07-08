package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Taoism.MODID, name = Taoism.MODID+"/hud")
@Config.LangKey("taoism.config.hud.title")
public class HudConfig {

	public static final Client client = new Client();

	public static class Client {

		@Config.Comment("Whether to display enemy posture")
		public boolean displayEnemyPosture = true;

		//public final HUDPos posture = new HUDPos(427, 240);
		//public final HUDPos enemyPosture = new HUDPos(0, 240);
		public final HUDPos qi = new HUDPos(0, 0);

		public static class HUDPos {
			public HUDPos(final int x, final int y) {
				this.x = x;
				this.y = y;
			}

			@Config.Comment("The x coordinate, exceeding the current screen width will bind it to the rightmost corner")
			public int x;

			@Config.Comment("The y coordinate, exceeding the current screen height will bind it to the bottom")
			public int y;
		}
	}

	@Mod.EventBusSubscriber(modid = Taoism.MODID)
	private static class EventHandler {

		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Taoism.MODID)) {
				ConfigManager.sync(Taoism.MODID, Config.Type.INSTANCE);
			}
		}
	}
}
