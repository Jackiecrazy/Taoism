package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Taoism.MODID, name = Taoism.MODID + "/general")
@Config.LangKey("taoism.config.general.title")
public class GeneralConfig {

    @Config.Comment("One in this many zombies and skeletons will spawn with Taoism weapons. 0 to disable.")
    public static int weaponSpawnChance = 200;

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