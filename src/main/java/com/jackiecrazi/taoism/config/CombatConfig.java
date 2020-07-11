package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Taoism.MODID, name = Taoism.MODID + "/combat")
@Config.LangKey("taoism.config.combat.title")
public class CombatConfig {
    @Config.Comment("Posture consumed per projectile parried.")
    public static float posturePerProjectile = 0.5f;
    @Config.Comment("Posture multiplier when using a weapon not from Taoism.")
    public static float defaultMultiplierPostureDefend = 1.4f;
    @Config.Comment("Posture multiplier when using a shield as defined in shieldItems.")
    public static float defaultMultiplierPostureShield = 1f;
    @Config.Comment("Posture multiplier when using a weapon not from Taoism. This is multiplied by the weapon's attack damage.")
    public static float defaultMultiplierPostureAttack = 0.15f;
    @Config.Comment("Posture damage from punching with an empty hand.")
    public static float defaultPostureKenshiro = 0.5f;
    @Config.Comment("Posture multiplier for mobs and NPCs, as they don't deal much per hit.")
    public static float basePostureMob = 2f;
    @Config.Comment("Cooldown after using spirit power before it starts recharging, currently unused.")
    public static int lingCD = 20;
    @Config.Comment("Cooldown after using posture before it starts recharging.")
    public static int postureCD = 20;
    @Config.Comment("Number of ticks after a roll for which the player is considered to be rolling.")
    public static int rollThreshold = 15;
    @Config.Comment("Number of ticks after a roll for which the player cannot roll again.")
    public static int rollCooldown = 20;
    @Config.Comment("Number of ticks one should be protected for after sudden stagger prevention activates.")
    public static int ssptime = 20;
    @Config.Comment("Maximum number of ticks between two attacks for it to be considered as part of the same combo.")
    public static int timeBetweenAttacks = 20;
    @Config.Comment("Number of ticks between each forced client update of the entity's various stats.")
    public static int mobUpdateInterval = 100;
    @Config.Comment("Number of enforced ticks between each mob attack, because slimes.")
    public static int mobForcedCooldown = 10;
    @Config.Comment("Number of seconds after a qi add for which your qi will not decrease.")
    public static int qiGrace = 10;
    @Config.Comment("Additional items eligible for parrying. See printParryList for easy registration.")
    public static String[] parryCapableItems = {"example:thing1", "example:thing2"};
    @Config.Comment("Shields. Shields are prioritized over other items when parrying. See printShieldList for easy registration.")
    public static String[] shieldItems = {"example:thing1", "example:thing2"};
    @Config.Comment("Changes chance based knockback resist to percentage based knockback resist, which I think makes more sense.")
    public static boolean modifyKnockBackCode = true;
    @Config.Comment("Whether being hit by a staggering attack while your posture is above a quarter would protect you from sudden stagger.")
    public static boolean ssp = true;
    @Config.Comment("Whether mobs can use Taoism's weapons with all added effects (bleed, splash, etc). Disabling this will disable weaponHitEntity as well.")
    public static boolean taoWeaponHitEntity = true;
    @Config.Comment("Whether mobs can use any weapon from any mod with all its added effects. Disabled by default because some mods expect the hitter to be a player.")
    public static boolean weaponHitEntity = false;
    @Config.Comment("Toggling this option will print the list of parry eligible items based on items registered as swords and axes. It's a little expensive, so remember to turn it off again!")
    public static boolean printParryList = true;
    @Config.Comment("Toggling this option will print the list of parry eligible shields. It's a little expensive, so remember to turn it off again!")
    public static boolean printShieldList = true;

    public static void printParryList() {
        if (printParryList)
            Taoism.logger.info("beginning generation of the parry list:");
        for (Item item : Item.REGISTRY) {
            if (item instanceof ItemSword || item instanceof ItemAxe) {
                System.out.println(item.getRegistryName().toString());
            }
        }
        if (printShieldList)
            Taoism.logger.info("beginning generation of the shield list:");
        for (Item item : Item.REGISTRY) {
            if (item instanceof ItemShield) {
                System.out.println(item.getRegistryName().toString());
            }
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
