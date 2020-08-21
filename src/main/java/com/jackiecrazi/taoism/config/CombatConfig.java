package com.jackiecrazi.taoism.config;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
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
    @Config.Comment("Posture multiplier when not defined in combatItems.")
    public static float defaultMultiplierPostureDefend = 1.4f;
    @Config.Comment("Posture multiplier when not defined in combatItems. This is multiplied by the weapon's attack damage.")
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
    @Config.Comment("Number of ticks after a shield parry for which the player will continue to parry for free.")
    public static int shieldThreshold = 16;
    @Config.Comment("Number of ticks one should be protected for after sudden stagger prevention activates.")
    public static int ssptime = 20;
    @Config.Comment("Maximum number of ticks between two attacks for it to be considered as part of the same combo.")
    public static int timeBetweenAttacks = 20;
    @Config.Comment("Number of ticks between each forced client update of the entity's various stats.")
    public static int mobUpdateInterval = 100;
    @Config.Comment("Number of enforced ticks between each mob attack, because slimes. This is modified by attack speed, so by default mobs attack twice per second.")
    public static int mobForcedCooldown = 40;
    @Config.Comment("Number of seconds after a qi add for which your qi will not decrease.")
    public static int qiGrace = 10;
    @Config.Comment("Additional items eligible for parrying. Format should be name, attack posture consumption, defense multiplier, is shield. Invalid fields will be filled in with defaultMultiplierPostureAttack and defaultMultiplierPostureDefend. See printParryList for easy registration.")
    public static String[] combatItems = {"example:sword, 3.5, 1.5, false", "example:shield, 0.3, 0.6, true"};
    @Config.Comment("Here you can define custom max posture for mobs. Format is name, max posture. Armor is still calculated.")
    public static String[] customPosture = {"example:dragon, 100", "example:ghast, 8"};
    @Config.Comment("Changes chance based knockback resist to percentage based knockback resist, which I think makes more sense.")
    public static boolean modifyKnockBackCode = true;
    @Config.Comment("Whether being hit by a staggering attack while your posture is above a quarter would protect you from sudden stagger.")
    public static boolean ssp = true;
    @Config.Comment("Whether mobs can use Taoism's weapons with all added effects (bleed, splash, etc). Disabling this will disable weaponHitEntity as well.")
    public static boolean taoWeaponHitEntity = true;
    @Config.Comment("Whether mobs can use any weapon from any mod with all its added effects. Disabled by default because some mods expect the hitter to be a player.")
    public static boolean weaponHitEntity = false;
    @Config.Comment("Toggling this option will print the list of axes, swords, and shields. It's a little expensive, so remember to turn it off again!")
    public static boolean printParryList = true;
    @Config.Comment("Whether mobs lose their attack target and planned path when blinded")
    public static boolean blindMobs = true;
    @Config.Comment("Whether mobs that kill you don't despawn so you can come back and exact some vengeance later")
    public static boolean revengePreventDespawn = true;
    @Config.Comment("oooooooo")
    public static boolean superSecretSetting = false;


    public static void printParryList() {
        if (printParryList)
            Taoism.logger.info("beginning generation of the parry list:");
        for (Item item : Item.REGISTRY) {
            if (item instanceof ItemSword || item instanceof ItemAxe||item instanceof ItemShield) {
                System.out.println(item.getRegistryName().toString());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Taoism.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Taoism.MODID)) {
                ConfigManager.sync(Taoism.MODID, Config.Type.INSTANCE);
                TaoCombatUtils.updateLists();
            }
        }
    }
}
