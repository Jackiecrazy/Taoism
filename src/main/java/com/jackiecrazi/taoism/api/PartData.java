package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

import net.minecraft.nbt.NBTTagCompound;

public class PartData {
	private String oredict, part;
	private int damage;
	
	public PartData(String part,String mat, int dam) {
		this.part=part;
		oredict=mat;
		damage=dam;
	}
	
	public PartData(String part,MaterialStatWrapper mat, int dam) {
		this.part=part;
		oredict=mat.name;
		damage=dam;
	}
	
	public PartData(NBTTagCompound c){
		this(c.getString("part"),c.getString("dictname"),c.getInteger("damage"));
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound c){
		c.setString("part", part);
		c.setString("dictname", oredict);
		c.setInteger("damage", damage);
		//c.setInteger("amount", amount);
		return c;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof PartData))return false;
		PartData pd=(PartData)o;
		return oredict.equals(pd.oredict);
	}
	
	public PartData clone(){
		return new PartData(part,oredict, damage);
	}
	
	public String toString(){
		return part+" number "+damage+" made of "+oredict;
	}
	
	public int getDam(){
		return damage;
	}
	
	public String getMat(){
		return oredict;
	}
	
	public String getPart(){
		return part;
	}
	
	/**
	 * THIS IS NOT A FAILSAFE CHECK!
	 * @return whether the type of the part described and the string entry are the same
	 */
	public boolean isValid(){
		try{
			return TaoConfigs.weapc.lookup(part, damage).matType()==MaterialsConfig.findMat(oredict).msw.type;
		}catch(Exception e){
			return false;
		}
	}
}
