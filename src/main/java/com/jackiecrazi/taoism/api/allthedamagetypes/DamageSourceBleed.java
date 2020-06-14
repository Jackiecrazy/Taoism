package com.jackiecrazi.taoism.api.allthedamagetypes;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceBleed extends DamageSource {

    private DamageSourceBleed() {
        super("bleed");
        this.setDamageBypassesArmor();
        this.setDamageIsAbsolute();
        //this.setMagicDamage();//why did I do this?
    }

    public static DamageSource causeBleedingDamage(){
        return new DamageSourceBleed();
    }

    public static DamageSource causeEntityBleedingDamage(Entity e){
        return new EntityDamageSource("bleed", e);
    }
}
