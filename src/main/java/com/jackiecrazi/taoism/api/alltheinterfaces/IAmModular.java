package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.api.PartDefinition;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface IAmModular {
    default HashMap<String,String> getParts(ItemStack is){
        HashMap<String,String> hash=new HashMap<>();
        if(!is.hasTagCompound())return hash;
        for(PartDefinition s:getPartNames(is)){
            hash.put(s.name,is.getTagCompound().getString(s.name));
        }
        return hash;
    }
     PartDefinition[] getPartNames(ItemStack is);


}
