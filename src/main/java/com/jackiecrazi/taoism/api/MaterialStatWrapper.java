package com.jackiecrazi.taoism.api;

import java.awt.Color;

public class MaterialStatWrapper {
	public final String name;
	public final float mass;
	  public final float damageOrSpringiness;
	  public final float durability;
	  public final float affinityMetal;
	  public final float affinityWood;
	  public final float affinityWater;
	  public final float affinityFire;
	  public final float affinityEarth;
	  public final float drawSpeed;
	  public final float arrowSpeed;
	  public final Color color;
	  public final int dominance;
	  public final int lingAbility;
	  public final MaterialType type;
	  private int miningLevel=0;
	  private float digSpeed=0;
	  
	  public MaterialStatWrapper(String name,MaterialType type, float m, float h, float f, float ji, float mu, float sh, float hu, float tu, Color c, int dom, int la, float ds, float as)
	  {
	    this.type=type;
	    this.mass = m;
	    this.durability = f;
	    this.damageOrSpringiness = h;
	    this.affinityMetal = ji;
	    this.affinityWood = mu;
	    this.affinityWater = sh;
	    this.affinityFire = hu;
	    this.affinityEarth = tu;
	    this.color = c;
	    this.dominance = dom;
	    this.drawSpeed = ds;
	    this.arrowSpeed = as;
	    this.lingAbility = la;
	    this.name=name;
	    this.setDigSpeed(h+(h-6)*2);
	  }
	  
	  public MaterialStatWrapper(String name,MaterialType type, float m, float h, float f, float ji, float mu, float sh, float hu, float tu, Color c, int la, int dom)
	  {
	    this(name,type, m, h, f, ji, mu, sh, hu, tu, c, dom, la, 0.0F, 0.0F);
	  }
	  
	  public MaterialStatWrapper(String name,MaterialType type, float m, float h, float f, float ji, float mu, float sh, float hu, float tu, Color c, int la)
	  {
	    this(name,type, m, h, f, ji, mu, sh, hu, tu, c, 10, la);
	  }
	  
	  public MaterialStatWrapper()
	  {
	    this(MaterialType.HARD, "ingotIron:811,6,251,1.5,1,1,1,1,67,75,77,3,1,54,5.2");
	  }
	  
	  public MaterialStatWrapper(MaterialType type, String arg)
	  {
	    this.type = type;
	    this.name=arg.substring(0, arg.indexOf(":"));
	    String noname = arg.substring(arg.indexOf(":") + 1);
	    String[] less = noname.split(",");
	    this.mass = Float.valueOf(less[0].trim()).floatValue();
	    this.damageOrSpringiness = Float.valueOf(less[1].trim()).floatValue();
	    this.durability = Float.valueOf(less[2].trim()).floatValue();
	    this.affinityMetal = Float.valueOf(less[3].trim()).floatValue();
	    this.affinityWood = Float.valueOf(less[4].trim()).floatValue();
	    this.affinityWater = Float.valueOf(less[5].trim()).floatValue();
	    this.affinityFire = Float.valueOf(less[6].trim()).floatValue();
	    this.affinityEarth = Float.valueOf(less[7].trim()).floatValue();
	    this.color = new Color(Integer.valueOf(less[8].trim()).intValue(), Integer.valueOf(less[9].trim()).intValue(), Integer.valueOf(less[10].trim()).intValue());
	    this.dominance = Integer.valueOf(less[11].trim()).intValue();
	    this.lingAbility = Integer.valueOf(less[12].trim()).intValue();
	    if (type==MaterialType.HARD)
	    {
	      this.drawSpeed = Integer.valueOf(less[13].trim()).intValue();
	      this.arrowSpeed = Integer.valueOf(less[14].trim()).intValue();
	    }
	    else
	    {
	      this.drawSpeed = 0.0F;
	      this.arrowSpeed = 0.0F;
	    }
	  }
	  
	  public boolean isHard(){
		  return type==MaterialType.HARD;
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

	public void setDigSpeed(float digSpeed) {
		this.digSpeed = digSpeed;
	}
}
