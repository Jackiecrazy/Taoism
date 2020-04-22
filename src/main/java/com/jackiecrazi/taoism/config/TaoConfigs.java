package com.jackiecrazi.taoism.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class TaoConfigs {
    public static File general, hud, weapon, material, combat;
    public static Configuration[] weap, bow, arrow, helm, chest, leg, boot;

    public static void init(File path) {// path here being config/taoism already so
        path = new File(path, "taoism");
        general = new File(path, "general.cfg");
        hud = new File(path, "hud.cfg");
        weapon = new File(path, "weapon.cfg");
        material = new File(path, "material.cfg");
        combat = new File(path, "combat.cfg");
//        GeneralConfig.init(general);
//        HudConfig.init(hud);
        MaterialsConfig.init(new File(path, "materials/"));
        //CombatConfig.init(combat);
    }

}
