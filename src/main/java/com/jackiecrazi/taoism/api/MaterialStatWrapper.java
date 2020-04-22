package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.api.alltheinterfaces.IElemental;

import java.awt.Color;

public class MaterialStatWrapper {
    public final String name;
    public final float swingSpeed;
    public final float damageOrSpringiness;
    public final float durability;
    private float[] affinities = new float[IElemental.ELEMS.length];
    public final float drawSpeed;
    public final float arrowSpeed;
    public final Color color;
    public final int dominance;
    public final int lingAbility;
    public final MaterialType type;
    private int miningLevel = 0;
    private float digSpeed = 0;

    public static final MaterialStatWrapper FALLBACK=new MaterialStatWrapper("ingotIron", MaterialType.METAL, 1f,1f,1f,new float[0],new Color(0xFFFFFF),1,1,1f,1f);

    public MaterialStatWrapper(String name, MaterialType type, float mass, float hard, float dur, float[] elements, Color c, int dom, int la, float ds, float as) {
        this.type = type;
        swingSpeed=mass;
        this.durability = dur;
        this.damageOrSpringiness = hard;
        affinities=elements;
        this.color = c;
        this.dominance = dom;
        this.drawSpeed = ds;
        this.arrowSpeed = as;
        this.lingAbility = la;
        this.name = name;
        digSpeed=(hard + (hard - 6) * 2);
    }

    //                                name,             spd,            dam,           level,              dur,                aff m,              aff w,              aff wa,              aff f,               aff e,       r,         g,        b,           dom,              la,            draw,             aspd:traits
    public MaterialStatWrapper(String name, double mass, double hardness, int miningLevel, double durability, double affinityMetal, double affinityWood, double affinityWater, double affinityFire, double affinityEarth, int red, int green, int blue, int dominance, int lingAbility, double drawSpeed, double arrowSpeed, MaterialType type) {
        this(name, type, (float)mass, (float)hardness, (float)durability, new float[]{(float)affinityMetal, (float)affinityWood, (float)affinityWater, (float)affinityFire, (float)affinityEarth}, new Color(red,green,blue), dominance, lingAbility, (float)drawSpeed,(float)arrowSpeed);
        this.miningLevel=miningLevel;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public void setMiningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
    }

    public float getDigSpeed() {
        return digSpeed;
    }

    public float[] affinity(){
        return affinities;
    }
}
