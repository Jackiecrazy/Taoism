package com.jackiecrazi.taoism.common.item.prop;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemProjectileProp extends Item {
    public ItemProjectileProp() {
        String name="projectile_prop";
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.addPropertyOverride(new ResourceLocation("type"), (stack, w, elb) -> stack.getCount());
    }
}
