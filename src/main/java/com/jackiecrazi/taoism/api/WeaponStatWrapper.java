package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.WeaponPerk.HandlePerk;

public class WeaponStatWrapper {
	//head goes on the tip of the stick 
	//(reserved for sword, dao, spear tip, spade head, hook head, trident head, langyabang)
	//handle is the stick as expected. 
	//It can be short, medium or long
	//guard does the guards/side attack weapon for short handle and side attachments for medium and long 
	//axes, hammers, both with pick on the other side, daggeraxe, ji, scythe for long/medium
	//skull, taichi, wide, flame and normal guard, yyy, sword blade, ji and claws for short
	//pommel is a nice place to put on butt spike, chains, round pommel, crescent edge (for long only)
	//TODO add some purely cosmetic variations on each part for the fashion souls look. They would be the exact same stat-wise
	//hierarchy: first two bits to store where it goes on, the rest to denote it
	private int cost, dtype, ordinal;
	private String name, classification;
	private WeaponPerk[] behaviour = new WeaponPerk[0];
	/**
	 * usually used to determine what handles accept it. OR-gated.
	 */
	private String[] whitelist = new String[0];
	private String[] blacklist = new String[0];
	private String[] handleList;
	private float range, speed, damage, durabilityMultiplier,
			elementalMultiplier = 1f;
	private MaterialType type;

	public WeaponStatWrapper(String classify, int order, MaterialType type, String[] acceptableHandles, String nam, int cost, int damageType, float range, float speed, float damage, float durmultiplier, float elem, WeaponPerk... wb) {
		this.name = nam;
		this.cost = cost;
		this.range = range;
		this.speed = speed;
		this.damage = damage;
		this.durabilityMultiplier = durmultiplier;
		this.dtype = damageType;
		this.type = type;
		elementalMultiplier = elem;
		behaviour = wb;
		classification = classify;
		ordinal = order;
		handleList=acceptableHandles;
	}

	/**
	 * OR not AND
	 * 
	 * @return
	 */
	public String[] getWhitelist() {
		return whitelist;
	}

	public WeaponStatWrapper setWhitelist(String... whitelist) {
		this.whitelist = whitelist;
		return this;
	}
	
	public String[] getBlacklist() {
		return blacklist;
	}

	public WeaponStatWrapper setBlacklist(String... blacklist) {
		this.blacklist = blacklist;
		return this;
	}

	public WeaponStatWrapper setBehaviour(WeaponPerk... b) {
		if (b.length == 1 && b[0].equals(" ")) { return this; }
		behaviour = b;
		return this;
	}

	public WeaponStatWrapper setBehaviour(String... b) {
		if (b.length == 1 && b[0].equals(" ")) { return this; }
		WeaponPerk[] wp = new WeaponPerk[b.length];
		for (int a = 0; a < b.length; a++) {
			wp[a] = WeaponPerk.lookUp(b[a]);
		}
		behaviour = wp;
		return this;
	}

	public int getCost() {
		return cost;
	}

	public WeaponPerk[] getPerks() {
		return behaviour;
	}

	/**
	 * @return 0 for blunt, 1 for cutting, 2 for stabbing
	 */
	public int getDamageType() {
		return dtype;
	}

	public float getRange() {
		return range;
	}

	public float getSpeedMultiplier() {
		return speed;
	}

	public float getDamageMultiplier() {
		return damage;
	}

	public float getDurabilityMultiplier() {
		return durabilityMultiplier;
	}

	public float getElementalMultiplier() {
		return elementalMultiplier;
	}

	public String getName() {
		return name;
	}

	public WeaponStatWrapper(String complex) {
		name = complex.substring(0, complex.indexOf(":"));
		String lesscomplex = complex.substring(complex.indexOf(":") + 1);
		//System.out.println(lesscomplex);
		String[] parsed = lesscomplex.split(",");
		try {

			cost = Integer.valueOf(parsed[0].trim());
			range = Float.valueOf(parsed[1].trim());
			speed = Float.valueOf(parsed[2].trim());
			damage = Float.valueOf(parsed[3].trim());
			durabilityMultiplier = Float.valueOf(parsed[4].trim());
			if (parsed.length > 5) elementalMultiplier = Float.valueOf(parsed[5].trim());
		} catch (Exception e) {
			Taoism.logger.warn("malformed string " + complex + " for weapon stats, assuming default of 1 on everything");
			e.printStackTrace();
			cost = 1;
			range = 1f;
			speed = 1f;
			damage = 1f;
			durabilityMultiplier = 1f;
		}
	}

	/**
	 * @return a list of stats, cost range speed damage durability element
	 */
	public Float[] toStats() {
		Float[] ret = new Float[6];
		ret[0] = Float.valueOf(cost);
		ret[1] = range;
		ret[2] = speed;
		ret[3] = damage;
		ret[4] = durabilityMultiplier;
		ret[5] = elementalMultiplier;
		return ret;
	}

	public MaterialType matType() {
		return type;
	}

	public boolean accepts(MaterialStatWrapper msw) {
		System.out.println(msw.name + " is " + msw.type);
		System.out.println(this.name + " is " + this.type);
		return this.type == msw.type;
	}

	/*private HandlePerk getHP() {
		for (WeaponPerk wp : getPerks()) {
			if (wp instanceof HandlePerk) return (HandlePerk) wp;
		}
		return null;
	}*/

	public boolean acceptsHandle(HandlePerk wp) {
		if (this.isHandle()) return true;//getHP().equals(wp);
		for (String s : getHandleList()) {
			if (wp.name.equals(s))
				return true;
		}
		
		return false;
	}

	public boolean acceptsHandle(WeaponStatWrapper wsw) {
		if (this.isHandle()) return true;
		for (WeaponPerk wp : wsw.behaviour) {
			if (wp instanceof HandlePerk){
				return acceptsHandle((HandlePerk) wp);
			}
		}
		return false;
	}

	public boolean isHandle() {
		for (WeaponPerk wp : getPerks()) {

			if (wp != null && WeaponPerk.REGISTEREDHANDLES.containsKey(wp.name)) return true;
		}
		return false;
	}

	public String getClassification() {
		return classification;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String[] getHandleList() {
		return handleList;
	}
}
