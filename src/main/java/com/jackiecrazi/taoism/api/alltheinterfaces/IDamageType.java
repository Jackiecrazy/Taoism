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
                return 0.6f;
            case 1://cutting
                return 0.3f;
            case 2://piercing
                return 0.1f;
            case 3://chopping
                return 0.45f;
        }

        return 0;
    }

    /**
     * @return 0 for blunt, 1 for cutting, 2 for piercing, 3 for chopping
     */
    int getDamageType(ItemStack is);
}
