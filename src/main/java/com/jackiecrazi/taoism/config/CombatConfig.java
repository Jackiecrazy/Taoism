package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(modid = Taoism.MODID, name = Taoism.MODID + "/combat")
@Config.LangKey("taoism.config.combat.title")
public class CombatConfig {
    @Config.Comment("Posture multiplier when using a weapon not from Taoism.")
    public static float defaultMultiplierPostureDefend = 1f;
    @Config.Comment("Cooldown after using spirit power before it starts recharging, currently unused.")
    public static int lingCD = 20;
    @Config.Comment("Cooldown after using spirit power before it starts recharging.")
    public static int postureCD = 20;
    @Config.Comment("Number of ticks after a parry attempt for which a player is considered to be parrying.")
    public static int parryThreshold = 7;
    @Config.Comment("Number of ticks after a parry attempt for which a player cannot parry again.")
    public static int parryCooldown = 20;
    @Config.Comment("Number of ticks after a roll for which the player is considered to be rolling.")
    public static int rollThreshold = 10;
    @Config.Comment("Number of ticks after a roll for which the player cannot roll again.")
    public static int rollCooldown = 20;
    @Config.Comment("Number of ticks one should be protected for after sudden stagger prevention activates.")
    public static int ssptime = 20;
    @Config.Comment("Maximum number of ticks between two attacks for it to be considered as part of the same combo.")
    public static int timeBetweenAttacks = 50;
    @Config.Comment("Number of ticks between each forced client update of the entity's various stats.")
    public static int mobUpdateInterval = 100;
    @Config.Comment("Number of enforced ticks between each mob attack, because slimes.")
    public static int mobForcedCooldown = 10;
    @Config.Comment("Number of ticks after a qi add attempt for which your qi will not decrease.")
    public static int qiGrace = 20;
    @Config.Comment("Items eligible for parrying.")
    public static String[] parryCapableItems = {"example:thing1", "example:thing2"};
    @Config.Comment("Changes chance based knockback resist to percentage based knockback resist, which I think makes more sense.")
    public static boolean modifyKnockBackCode = true;
    @Config.Comment("Whether being hit by a staggering attack while your posture is above a quarter would protect you from sudden stagger.")
    public static boolean ssp = true;
    @Config.Comment("Whether mobs can use Taoism's weapons with all added effects (bleed, splash, etc). Disabling this will disable weaponHitEntity as well.")
    public static boolean taoWeaponHitEntity = true;
    @Config.Comment("Whether mobs can use any weapon from any mod with all its added effects. Disable if there is some issue.")
    public static boolean weaponHitEntity = true;
    @Config.Comment("Toggling this option will wipe and reload the list of parry eligible items based on items registered as swords and axes. Remember to turn it off again!")
    public static boolean wipeParryList = false;

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

//        @SubscribeEvent(priority = EventPriority.LOWEST)
//        public static void updateList(final RegistryEvent.Register<Item> e){
//            if(wipeParryList){
//                ArrayList<String> put=new ArrayList<>();
//                for(Item i:e.getRegistry().getValuesCollection()){
//                    if(i instanceof ItemSword){
//                        put.add(i.getUnlocalizedName());
//                    }
//                }
//                parryCapableItems= (String[]) put.toArray();
//            }
//        }
    }
}
