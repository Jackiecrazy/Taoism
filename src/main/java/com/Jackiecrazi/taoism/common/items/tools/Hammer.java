package com.Jackiecrazi.taoism.common.items.tools;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IModularWeapon;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

public class Hammer extends GenericTaoistWeapon implements IModularWeapon{

	public Hammer(ToolMaterial tmat) {
		super(tmat,"smithHammer"+tmat.toString(),0f,0f,5,StaticRefs.HEAD,StaticRefs.HEAD,StaticRefs.HANDLE);
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.setUnlocalizedName("taoisthammer"+tmat.toString().toLowerCase());
	}
//TODO hammers have a head and a stick. The stick facilitates ling/dur and the head determines damage/forge level. Reflect accordingly.
//TODO zhenfa. Qiankun sancai sixiang wuxing qixin bagua
	//TODO zhenfa. Bagua has 8 gua so each reflects a different module of spell it exerts, in order energy source-form-target-direction-action-action modifier-buffx2
	
	public int getItemEnchantability()
    {
        return 0;
    }
	/**
	 * available parts are "head" and "handle"
	 */
@Override
public HashMap<String, ItemStack> getParts(ItemStack is) {
	HashMap<String, ItemStack> ret=new HashMap<String, ItemStack>();
	if(is.hasTagCompound()){
	ret.put("head", ItemStack.loadItemStackFromNBT(is.getTagCompound().getTagList("head", Constants.NBT.TAG_COMPOUND).getCompoundTagAt(0)));
	ret.put("handle", ItemStack.loadItemStackFromNBT(is.getTagCompound().getTagList("handle", Constants.NBT.TAG_COMPOUND).getCompoundTagAt(0)));
	
	}
	return ret;
}
/**
 * DO NOT CALL THIS FUNCTION FOR THE HAMMER!
 */
@Override
public HashMap<String, HashMap<String, ItemStack>> getPartsOfParts(ItemStack is) {
	return null;
}
/**
 * @param partName anything other than "head" and "handle" do nothing anyway.
 * 
 */
@Override
public boolean setPart(String partName, ItemStack base, ItemStack addition) {
	if(!base.hasTagCompound())base.setTagCompound(new NBTTagCompound());
	base.getTagCompound().setTag(partName, addition.writeToNBT(new NBTTagCompound()));
	return true;
}
/**
 * DO NOT CALL THIS FUNCTION FOR THE HAMMER!
 */
@Override
public void setPartOfPart(String partName, ItemStack base, ItemStack addition) {}
@Override
public float hungerUsed() {
	return 0;
}
@Override
public int swingSpd() {
	return 5;
}
@Override
public float getRange(EntityPlayer p, ItemStack is) {
	return 0;
}
	
}
