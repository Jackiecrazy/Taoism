package com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.networking.PacketSetSkillStuff;
import com.Jackiecrazi.taoism.networking.PacketSetUnlockSkill;

/**
 * @author wuguoxian
 *
 */
public class Skill implements IExtendedEntityProperties {
	protected int max=0;
	/**
	 * Remember to set a max level with this.max= something!
	 * @param p the player
	 */
	public Skill(EntityPlayer p) {
		this.player = p;
	}
	protected String name;
	protected Integer level = 0;
	protected Float xp = 0f;
	protected EntityPlayer player;
	protected HashMap<String, Integer> unlockedSkills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> skillNamesAndMax = new HashMap<String, Integer>();
	public static final String BATNAME = "BasicAsceticTraining",
			WMNAME = "WeaponMaintenance", APNAME = "ArtifactPreservation",
			BFNAME = "BasicFighting";

	public static void populateSkillHashMap() {
		// qili
		skillNamesAndMax.put(BATNAME, 30);
		skillNamesAndMax.put(WMNAME, 10);
		skillNamesAndMax.put(APNAME, 5);
		
		skillNamesAndMax.put(BFNAME, 50);
		/*if (WayofConfig.WeaponsEnabled) {
			Iterator<String> i = GenericTaoistWeapon.ListOfWeapons.iterator();
			while (i.hasNext()) {
				skillNamesAndMax.put("Ultimate" + i.next(), 1);
			}
		}*/
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound spr = (NBTTagCompound) compound.getTag("Taoism");
		if (spr == null)
			spr = new NBTTagCompound();
		NBTTagCompound saved = new NBTTagCompound();
		saved.setInteger("level", getLevel());
		saved.setFloat("xp", getXP());
		// TODO unlocked skills go here
		for (Entry<String, Integer> e : unlockedSkills.entrySet()) {
			saved.setInteger(e.getKey(), e.getValue());
		}
		spr.setTag(name, saved);
		compound.setTag("Taoism", spr);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound save = (NBTTagCompound) compound.getTag("Taoism");
		NBTTagCompound saved = (NBTTagCompound) save.getTag(this.name);
		level = saved.getInteger("level");
		xp = (saved.getFloat("xp"));
		// TODO unlocked skills go here

		for (Entry<String, Integer> e : skillNamesAndMax.entrySet()) {
			//System.out.println(e.getKey());
			unlockedSkills.put(e.getKey(), saved.getInteger(e.getKey()));
		}
	}

	@Override
	public void init(Entity entity, World world) {
		if (entity.getExtendedProperties(this.name) == null
				&& entity instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) entity;
			entity.registerExtendedProperties(this.name, new XiuWeiHandler(p));
			// TODO so on so forth
		}
	}

	public static Skill get(EntityPlayer p) {
		return null;
	}

	public int getLevel() {
		return this.level;
	}

	public String getSkill() {
		return this.name;
	}

	public void setLevel(int lev) {
		this.level = Math.min(lev, max);
		if (player instanceof EntityPlayerMP)
			Taoism.net.sendTo(new PacketSetSkillStuff(getSkill(), getLevel(),
					getXP()), (EntityPlayerMP) player);
	}

	public void setXP(float xp) {
		this.xp = xp;
		int inc=0;
		while (getXP() > calculateXPNeeded()) {
			setXP(getXP() - calculateXPNeeded());
			inc++;
		}
		addLevel(inc);
	}

	public void addLevel(int amnt) {
		setLevel(getLevel() + amnt);
	}

	public void subtractLevel(int amnt) {
		setLevel(getLevel() - amnt);
	}

	public float getXP() {
		return this.xp;
	}

	public void addXP(float amnt) {
		setXP(getXP() + amnt);
		if (getXP() > calculateXPNeeded()) {
			setXP(getXP() - calculateXPNeeded());
			addLevel(1);
		}
	}

	public void subtractXP(float amnt) {
		setXP(this.xp - amnt);
		float prev = Math.round(((getLevel() - 1) / 10) ^ 2);
		if (getXP() < 0) {
			subtractLevel(1);
			setXP(prev + getXP());
		}
	}

	public float calculateXPNeeded() {
		float ret = (float) Math.pow(this.level / 10, 2F);
		return ret + 0.2F;
	}

	public void setSkillAwesomeness(String key, int awesomeness) {
		assert unlockedSkills.containsKey(key) : "There is no " + key;
		assert awesomeness <= skillNamesAndMax.get(key) : awesomeness
				+ " is too big for this. The maximum is "
				+ skillNamesAndMax.get(key);
		unlockedSkills.put(key, awesomeness);
		if (player instanceof EntityPlayerMP) {
			Taoism.net.sendTo(new PacketSetUnlockSkill(key, awesomeness),
					(EntityPlayerMP) player);
		}
	}

	public int getSkillAwesomeness(String key) {
		assert unlockedSkills.containsKey(key) : "There is no " + key;
		assert unlockedSkills.get(key) <= skillNamesAndMax.get(key) : "You cheater! "
				+ unlockedSkills.get(key)
				+ " is greater than "
				+ skillNamesAndMax.get(key);
		if(unlockedSkills.containsKey(key))
		return unlockedSkills.get(key);
		else return 0;
	}

	public boolean isSkillMaxed(String key) {
		return getSkillAwesomeness(key) == skillNamesAndMax.get(key);
	}

	public static Set<String> getAllSkillNames() {
		return skillNamesAndMax.keySet();
	}

}
