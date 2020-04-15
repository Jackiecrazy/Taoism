package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.item.ItemStack;

/**
 * blunt is doubly reduced by absorption
 * cutting is doubly reduced by deflection
 */
public interface IDamageType {

    default float getDamDist(ItemStack is){
        int type=getDamageType(is);
        switch(type){
            case 0://blunt
                return 0.40f;
            case 1://cutting
                return 0.20f;
            case 2://piercing
                return 0.10f;
            case 3://chopping
                return 0.30f;
        }

        return 0;
    }

    /**
     * @return 0 for blunt, 1 for cutting, 2 for piercing, 3 for chopping
     */
    int getDamageType(ItemStack is);
}
