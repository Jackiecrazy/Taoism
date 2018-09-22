package com.jackiecrazi.taoism.common.entity;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TaoEntities {
    public static void init(){
        EntityRegistry.registerModEntity(new ResourceLocation(Taoism.MODID,"arrow"),EntityTaoArrow.class,"taoarrow",0,Taoism.INST,64,20,true);
    }
}
