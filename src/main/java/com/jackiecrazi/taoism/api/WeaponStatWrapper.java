package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.api.WeaponPerk.HandlePerk;

import javax.annotation.Nullable;

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
	private HandlePerk[] handleList;
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
		handleList=synthesise(acceptableHandles);
	}

    /**
     * Only used for the default config. Do not use by yourself!
     */
	public WeaponStatWrapper(String name, int cost, double range, double speed, double damage, double durmultiplier, double elem, int damageType, boolean isHard, HandlePerk... handle) {
		this.name = name;
		this.cost = cost;
		this.range = (float)range;
		this.speed = (float)speed;
		this.damage = (float)damage;
		this.durabilityMultiplier = (float)durmultiplier;
		this.dtype = damageType;
		this.type = isHard?MaterialType.HARD:MaterialType.SOFT;
		elementalMultiplier = (float)elem;
		handleList=handle;
	}
    /**
     * Only used for the default config. Do not use by yourself!
     */
    public WeaponStatWrapper(String name, int cost, double range, double speed, double damage, double durmultiplier, double elem, int damageType, boolean isHard) {
        this.name = name;
        this.cost = cost;
        this.range = (float)range;
        this.speed = (float)speed;
        this.damage = (float)damage;
        this.durabilityMultiplier = (float)durmultiplier;
        this.dtype = damageType;
        this.type = isHard?MaterialType.HARD:MaterialType.SOFT;
        elementalMultiplier = (float)elem;
    }
	
	private HandlePerk[] synthesise(String... b){
		if (b.length == 1 && b[0].equals(" ")) { return new HandlePerk[0]; }
		HandlePerk[] wp = new HandlePerk[b.length];
		for (int a = 0; a < b.length; a++) {
			if(WeaponPerk.lookUp(b[a]) instanceof HandlePerk)
			wp[a] = (HandlePerk) WeaponPerk.lookUp(b[a]);
		}
		return wp;
	}

	public WeaponStatWrapper setBehaviour(WeaponPerk... b) {
		if (b.length == 1 && (b[0]==null)) { return this; }
		behaviour = b;
		return this;
	}

	public WeaponStatWrapper setBehaviour(String... b) {
		if (b.length == 1 && b[0].equals(" ")) { return this; }
		WeaponPerk[] wp = new WeaponPerk[b.length];
		for (int a = 0; a < b.length; a++) {
			wp[a] = WeaponPerk.lookUp(b[a]);
		}
		return setBehaviour(wp);
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

	/**
	 * @return a list of stats, cost range speed damage durability element
	 */
	public Float[] toStats() {
		Float[] ret = new Float[6];
		ret[0] = (float)cost;
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
		for (HandlePerk s : getHandleList()) {
			if (wp.equals(s))
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

			if (wp != null && wp instanceof HandlePerk) return true;
		}
		return false;
	}
	
	@Nullable public HandlePerk getHandle(){
		for (WeaponPerk wp : getPerks()) {

			if (wp != null && wp instanceof HandlePerk) return (HandlePerk) wp;
		}
		return null;
	}

	/**
	 * 
	 * @return handle, head, etc.
	 */
	public String getClassification() {
		return classification;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public HandlePerk[] getHandleList() {
		return handleList;
	}
	public String[] handle(){
	    String[] ret=new String[0];
	    if(handleList==null)return ret;
	    ret=new String[handleList.length];
	    for(int a=0;a<handleList.length;a++){
	        ret[a]=handleList[a].name;
        }
        return ret;
    }
    public String[] perk(){
        String[] ret=new String[0];
        if(behaviour==null)return ret;
        ret=new String[behaviour.length];
        for(int a=0;a<behaviour.length;a++){
            ret[a]=behaviour[a].name;
        }
        return ret;
    }
}
