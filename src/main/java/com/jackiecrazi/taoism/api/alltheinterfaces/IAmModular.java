package com.jackiecrazi.taoism.api.alltheinterfaces;

import java.util.HashMap;
import java.util.Map;

import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.AbstractWeaponConfigOverlord;
import net.minecraft.item.ItemStack;

import com.jackiecrazi.taoism.api.PartData;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public interface IAmModular {
	/**
	 * @param is
	 * @return an array of the parts that this thing has
	 */
	String[] getPartNames(ItemStack is);

    static void hi(){

    }

	default NBTTagCompound getStorage(ItemStack is) {
		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getCompoundTag("TaoistParts");
	}

	/**
	 * The itemstack will always be an instance of {@link IAmModular}
	 * @param is itemstack
	 * @return an array made of arrays of part data
	 */
	default HashMap<String, PartData> getParts(ItemStack is){
		HashMap<String, PartData> ret = new HashMap<String, PartData>();
		NBTTagCompound ntc = getStorage(is);
		for (String s : getPartNames(is)) {
			if (ntc.hasKey(s)) ret.put(s, new PartData(ntc.getCompoundTag(s)));
		}
		return ret;
	}
	/**
	 * stores a part as a name under an itemstack.
	 * @param partName make sure you check under the individual weapons
	 * @param is original itemstack
	 * @param addition the itemstack that will be added. Should implement {@link IAmModular}, but I ain't checkin'
	 * @return whether it was successfully set
	 */
	default boolean setPart(String partName, ItemStack is, PartData addition){
        if (addition == null) {
            System.out.println("null parts what");
            return false;
        }
        if (!isValidAddition(is, partName, addition)) {
            //System.out.println("invalid");
            return false;
        }
        NBTTagCompound ntc = getStorage(is);
        ntc.setTag(partName, addition.writeToNBT(new NBTTagCompound()));
        is.getTagCompound().setTag("TaoistParts", ntc);
        //System.out.println("part set");
        return true;
	}
	
	/**
	 * call this before you set part. Used to determine whether the itemstack can accept the given partdata
	 * @param is base itemstack
	 * @param partName the subpart that pd is being added to
	 * @param pd the partdata to be added
	 * @return implement so that it determines whether setPart should proceed
	 */
	default boolean isValidAddition(ItemStack is, String partName, PartData pd){
        WeaponStatWrapper wsw = pd.getWeaponSW();
        if (wsw == null) {
            //System.out.println("proceeding");
            return false;//can throw null
        }
        //check handle
        if (pd.getWeaponSW().isHandle()) return true;
        if (!pd.getWeaponSW().acceptsHandle((WeaponPerk.HandlePerk) getPart(is, StaticRefs.HANDLE).getWeaponSW().getPerks()[0])) return false;

        return true;
	}

    default HashMap<WeaponPerk, Integer> getPerks(ItemStack is) {
        HashMap<WeaponPerk, Integer> ret = new HashMap<WeaponPerk, Integer>();
        for (PartData pd : getParts(is).values()) {
            for (WeaponPerk wp : AbstractWeaponConfigOverlord.lookup(pd).getPerks()) {
                if (!ret.containsKey(wp)) ret.put(wp, 0);
                else ret.put(wp, ret.get(wp) + 1);
            }
        }
        return ret;
    }

    @Nullable
    default PartData getPart(ItemStack is, String key) {
        NBTTagCompound ntc = getStorage(is);
        if (ntc.hasKey(key)) return new PartData(ntc.getCompoundTag(key));
        return null;
    }
	
	/**
	 * 
	 * @param is base itemstack
	 * @return whether the tool is actually legal
	 */
	default boolean isValidConfiguration(ItemStack is){


        if (is == null) return false;
        if (is.getItem() != this) return false;
        for (Map.Entry<String, PartData> e : this.getParts(is).entrySet()) {
            if (!this.isValidAddition(is, e.getKey(), e.getValue())) return false;
        }
        return true;
	}
}
