package com.Jackiecrazi.taoism.common.taoistichandlers;

import net.minecraft.nbt.NBTTagCompound;

import com.Jackiecrazi.taoism.common.entity.base.EntityTaoisticCreature;

public class ExtraMobStuff {
	public final EntityTaoisticCreature e;
	/**
	 * multiplier
	 */
	public double attackBuff,defenseBuff,speedBuff,healthBuff,dropBuff,fallBuff=1;
	/**
	 * behaviour override
	 */
	public boolean gang,swim,fly,gills,fire,ninja;
	/**
	 * flat additions
	 */
	public int attackBoost=0,defenseBoost=0,speedBoost=0,healthBoost=0,dropBoost=0;
	public ExtraMobStuff(EntityTaoisticCreature etc) {
		e=etc;
	}
	
	public void readFromNBT(NBTTagCompound c) {
		if(c.hasKey("atkbuf"))
		attackBuff=c.getDouble("atkbuf");
		if(c.hasKey("defbuf"))
			defenseBuff=c.getDouble("defbuf");
		if(c.hasKey("spdbuf"))
			speedBuff=c.getDouble("spdbuf");
		if(c.hasKey("hpbuf"))
			healthBuff=c.getDouble("hpbuf");
		if(c.hasKey("dropbuf"))
			dropBuff=c.getDouble("dropbuf");
		if(c.hasKey("fallbuf"))
			fallBuff=c.getDouble("fallbuf");
		
		if(c.hasKey("atkadd"))
			attackBoost=c.getInteger("atkadd");
		if(c.hasKey("defadd"))
			defenseBoost=c.getInteger("defadd");
		if(c.hasKey("spdadd"))
			speedBoost=c.getInteger("spdadd");
		if(c.hasKey("hpadd"))
			healthBoost=c.getInteger("hpadd");
		if(c.hasKey("dropadd"))
			dropBoost=c.getInteger("dropadd");
		
		if(c.hasKey("gang"))
			gang=c.getBoolean("gang");
		if(c.hasKey("swim"))
			swim=c.getBoolean("swim");
		if(c.hasKey("fly"))
			fly=c.getBoolean("fly");
		if(c.hasKey("gills"))
			gills=c.getBoolean("gills");
		if(c.hasKey("fire"))
			fire=c.getBoolean("fire");
		if(c.hasKey("ninja"))
			ninja=c.getBoolean("ninja");
	}
	
	public void writeToNBT(NBTTagCompound c) {
		c.setDouble("atkbuf", attackBuff);
		c.setDouble("defbuf", defenseBuff);
		c.setDouble("spdbuf", speedBuff);
		c.setDouble("hpbuf", healthBuff);
		c.setDouble("dropbuf", dropBuff);
		c.setDouble("fallbuf", fallBuff);
		
		c.setBoolean("fire", fire);
		c.setBoolean("gang", gang);
		c.setBoolean("swim", swim);
		c.setBoolean("fly", fly);
		c.setBoolean("gills", gills);
		c.setBoolean("ninja", ninja);
		
		c.setInteger("atkadd", attackBoost);
		c.setInteger("defadd", defenseBoost);
		c.setInteger("spdadd", speedBoost);
		c.setInteger("hpadd", healthBoost);
		c.setInteger("dropadd", dropBoost);
		
	}
	
}
