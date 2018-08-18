package com.jackiecrazi.taoism.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.AbstractWeaponConfigOverlord;
import com.jackiecrazi.taoism.config.MaterialsConfig;

public class PartData {
	private String oredict, part;
	private int damage;

	public PartData(String part, String mat, int dam) {
		this.part = part;
		oredict = mat;
		damage = dam;
	}

	public PartData(String part, MaterialStatWrapper mat, int dam) {
		this.part = part;
		oredict = mat.name;
		damage = dam;
	}

	public PartData(NBTTagCompound c) {
		this(c.getString("part"), c.getString("dict"), c.getInteger("dam"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound c) {
		c.setString("part", part);
		c.setString("dict", oredict);
		c.setInteger("dam", damage);
		//c.setInteger("amount", amount);
		return c;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PartData)) return false;
		PartData pd = (PartData) o;
		return oredict.equals(pd.oredict);
	}

	public PartData clone() {
		return new PartData(part, oredict, damage);
	}

	public String toString() {
		return part + " number " + damage + " made of " + oredict;
	}

	public int getOrdinal() {
		return damage;
	}

	public String getMat() {
		return oredict;
	}

	public String getPart() {
		return part;
	}

	/**
	 * THIS IS NOT A FAILSAFE CHECK!
	 * 
	 * @return whether the type of the part described and the string entry are
	 *         the same
	 */
	public boolean isValid() {
		try {
			//TODO fix this so it never returns the default case. Ever.
			return AbstractWeaponConfigOverlord.lookup(part, damage).matType() == MaterialsConfig.findMat(oredict).msw.type;
		} catch (Exception e) {
			
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * creates a dummy representation of this partdata
	 */
	public ItemStack toStack() {
		ItemStack ret = new ItemStack(TaoItems.part);
		ret.setTagCompound(new NBTTagCompound());
		ret.getTagCompound().setString("dict", oredict);
		ret.getTagCompound().setString("part", part);
		ret.getTagCompound().setInteger("dam", damage);
		return ret;
	}

	public WeaponStatWrapper getWeaponSW() {
		return AbstractWeaponConfigOverlord.lookup(this);
	}

	public MaterialStatWrapper getMatSW() {
		return MaterialsConfig.findMat(getMat()).msw;
	}
}
