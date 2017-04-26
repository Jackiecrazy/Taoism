package com.Jackiecrazi.taoism.common.entity;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.biome.BiomeGenBase;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityDroppedWeapon;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLiLi;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLingJing;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLuoYu;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityDiJiang;
import com.Jackiecrazi.taoism.common.entity.mobs.passive.EntityShuHu;
import com.Jackiecrazi.taoism.common.entity.projectile.EntityElementalProjectile;

import cpw.mods.fml.common.registry.EntityRegistry;

public class TaoEntities {
	//all resistances represented in percentage
	public static final IAttribute RESISTANCE_METAL=new RangedAttribute(Taoism.MODID+"_metalres", 0, 0, 100).setShouldWatch(true);
	public static final IAttribute RESISTANCE_WOOD=new RangedAttribute(Taoism.MODID+"_woodres", 0, 0, 100).setShouldWatch(true);
	public static final IAttribute RESISTANCE_WATER=new RangedAttribute(Taoism.MODID+"_waterres", 0, 0, 100).setShouldWatch(true);
	public static final IAttribute RESISTANCE_FIRE=new RangedAttribute(Taoism.MODID+"_fireres", 0, 0, 100).setShouldWatch(true);
	public static final IAttribute RESISTANCE_EARTH=new RangedAttribute(Taoism.MODID+"_earthres", 0, 0, 100).setShouldWatch(true);
	public static final IAttribute LING_SPEED=new RangedAttribute(Taoism.MODID+"_lingspeed", 1, 1, Integer.MAX_VALUE).setShouldWatch(true);
	public static final IAttribute LING_MAX=new RangedAttribute(Taoism.MODID+"_lingmax",0,0,Integer.MAX_VALUE).setShouldWatch(true);
	public static final IAttribute[] ALLRES={
		RESISTANCE_METAL,RESISTANCE_WOOD,RESISTANCE_WATER,RESISTANCE_FIRE,RESISTANCE_EARTH
	};
	
	public static void init() {
		int mobcount = 0;
		EntityRegistry.registerModEntity(EntitySandbag.class,
				"taoisticsandbag", ++mobcount, Taoism.inst, 64, 20, true);
		EntityRegistry.registerModEntity(EntityMuRenZhuang.class,
				"taoisticmurenzhuang", ++mobcount, Taoism.inst, 64, 20, true);
		EntityRegistry.registerModEntity(EntityDroppedWeapon.class,
				"taoisticdroppedweapon", ++mobcount, Taoism.inst, 64, 20, true);
		EntityRegistry.registerModEntity(EntityLevitatingItem.class,
				"taoisticlevitatingitem", ++mobcount, Taoism.inst, 64, 20, true);
		EntityRegistry.registerModEntity(EntityElementalProjectile.class, "eep", ++mobcount, Taoism.inst, 64, 20, true);
		
		EntityRegistry.registerModEntity(EntityLuoYu.class, "luoyu", ++mobcount, Taoism.inst, 64, 3, true);
		EntityRegistry.addSpawn(EntityLuoYu.class, 10, 2, 10, EnumCreatureType.waterCreature, BiomeGenBase.frozenOcean,BiomeGenBase.ocean,BiomeGenBase.river,BiomeGenBase.frozenRiver);
		EntityRegistry.registerModEntity(EntityShuHu.class, "shuhu", ++mobcount, Taoism.inst, 64, 3, true);
		EntityRegistry.addSpawn(EntityShuHu.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.forestHills);
		EntityRegistry.registerModEntity(EntityDiJiang.class, "dijiang", ++mobcount, Taoism.inst, 64, 3, true);
		EntityRegistry.addSpawn(EntityDiJiang.class, 3, 1, 4, EnumCreatureType.creature, BiomeGenBase.extremeHills,BiomeGenBase.extremeHillsPlus);
		EntityRegistry.registerModEntity(EntityLiLi.class, "lili", ++mobcount, Taoism.inst, 64, 3, true);
		EntityRegistry.addSpawn(EntityLiLi.class, 5, 1, 4, EnumCreatureType.creature, BiomeGenBase.extremeHills,BiomeGenBase.extremeHillsPlus);
		EntityRegistry.registerModEntity(EntityLingJing.class, "lingjing", ++mobcount, Taoism.inst, 64, 3, true);
		
		Taoism.logDebug("Forged all entities");
	}
}
