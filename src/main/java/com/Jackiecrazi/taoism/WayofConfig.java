package com.Jackiecrazi.taoism;

import java.io.File;

import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.config.Configuration;

public class WayofConfig {
	public static final String POTS = "Potion IDs";
	public static final String IDHEADER = "IDs no one will bother to change and will complain for";
	public static final String HUDHEADER = "Fiddle with HUD elements here";
	public static final String MAXHEADER="The maximum level of each skill";
	
	public static boolean WeaponsEnabled,ArmorEnabled,CDEnabled,FancyEnabled;//modules go here
	public static int QiDWID=26, LingLiDWID=25;//DW goes here. Hopefully won't go much further
	public static int QiX,QiY,LingX,LingY;//Same as above, for render
	public static int QiSkillX,QiSkillY,WuGongSkillX,WuGongSkillY,LianDanSkillX,LianDanSkillY,LianQiSkillX,LianQiSkillY;//Skill HUD goes here
	public static int HidePotID;//Potion
	public static int QiLiMaxLvl,WuGongMaxLvl,LianDanMaxLvl;
	public static Configuration c;
	public static void init(File path){
		c=new Configuration(path,Taoism.MODVER);
		try{
			c.load();
			c.addCustomCategoryComment(HUDHEADER, "All X and Y values given with the top-left as (0,0), in pixels");
			c.addCustomCategoryComment(POTS, "I'm not sure how far up this goes for you. To be safe set it below 256, or install AntiIDConflict");
			QiLiMaxLvl=c.getInt("QiLi", MAXHEADER, 200, 0, Integer.MAX_VALUE,"");
			WuGongMaxLvl=c.getInt("WuGong", MAXHEADER, 200, 0, Integer.MAX_VALUE,"");
			LianDanMaxLvl=c.getInt("LianDan", MAXHEADER, 200, 0, Integer.MAX_VALUE,"");
			WeaponsEnabled=c.getBoolean("Enable Weapons", "toggle stuff here", true, "Disables all weapons and related items like ultimate scrolls. the skills will remain, and the needle is a weapon. Also disables all legendary weapons");
			ArmorEnabled=c.getBoolean("Enable Armour", "toggle stuff here", true, "Disables all armour. Skill buffs to movement will remain. Also disables all legendary armour");
			CDEnabled=c.getBoolean("Enable Attack Cooldown", "toggle stuff here", true, "Disables cooldown for attacks. Note spamming will likely break animations");
			FancyEnabled=c.getBoolean("Enable 3D Renders", "toggle stuff here", false, "Enables 3d models for some weapons. Note not every weapon has a 3D render, and not every one that has a 3D render will necessarily render with component textures. I.E. ridiculously WIP");
			QiDWID=c.getInt("Qi ID", IDHEADER, 26, 10, 31,"DataWatcher ID");
			LingLiDWID=c.getInt("Spirit Power ID", IDHEADER, 25, 10, 31,"DataWatcher ID");
			QiY = c.get(HUDHEADER, "Qi Y", 10).getInt();
			QiX = c.get(HUDHEADER, "Qi X", 10).getInt();
			LingY = c.get(HUDHEADER, "Ling Y", 10).getInt();
			LingX = c.get(HUDHEADER, "Ling X", 500).getInt();
			QiSkillY = c.get(HUDHEADER, "QiLi Y", 15).getInt();
			QiSkillX = c.get(HUDHEADER, "QiLi X", 30).getInt();
			WuGongSkillY = c.get(HUDHEADER, "WuGong Y", 30).getInt();
			WuGongSkillX = c.get(HUDHEADER, "WuGong X", 30).getInt();
			LianDanSkillY = c.get(HUDHEADER, "LianDan Y", 45).getInt();
			LianDanSkillX = c.get(HUDHEADER, "LianDan X", 30).getInt();
			LianQiSkillY = c.get(HUDHEADER, "LianQi Y", 60).getInt();
			LianQiSkillX = c.get(HUDHEADER, "LianQi X", 30).getInt();
			int PotID=32;
			HidePotID=c.get(POTS, "Hide Potion ID", --PotID).getInt();
		}catch(Exception e){
			LogManager.getLogger("Taoism").fatal("For whatever reason the config failed to load. I'll heartlessly throw an error and continue merrily on my way.");
			e.printStackTrace();
		}finally{
			c.save();
		}
	}
}
