package com.Jackiecrazi.taoism.api.allTheDamageTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DamageElemental extends EntityDamageSourceIndirect {
	public enum TaoistElement{
		METAL,
		WOOD,
		WATER,
		FIRE,
		EARTH,
		LING,
		SHA
	}
	private TaoistElement e;
	public DamageElemental(String s, Entity proxy,
			Entity seme,TaoistElement el) {
		super(s, proxy, seme);
		e=el;
	}
	public ITextComponent getDeathMessage(EntityLivingBase target)
    {
	String howtodie = "death.attack." + this.damageType+"."+e;
	ItemStack stack=null;
    ITextComponent source = getEntity() == null ? getSourceOfDamage().getDisplayName() : this.getEntity().getDisplayName();
    if(getEntity() != null && getEntity() instanceof EntityLivingBase){
    stack =  ((EntityLivingBase)getEntity()).getHeldItemMainhand();
    }
    else if(getSourceOfDamage() instanceof EntityLivingBase){
    	stack=((EntityLivingBase)getSourceOfDamage()).getHeldItemMainhand();
    }
    String deathname = howtodie + ".item";

    return stack != null && stack.hasDisplayName()? 
            new TextComponentTranslation(deathname, target.getDisplayName(), source, stack.getDisplayName()) :
            new TextComponentTranslation(howtodie, target.getDisplayName(), source);
            
    }
	
	public static DamageSource causeElementDamageIndirectly(Entity source, Entity transmitter,TaoistElement type) {
		return new DamageElemental("taoistelement.indirect."+type.toString(), source, transmitter,type);
	}
	public static DamageSource causeElementDirectly(Entity source,TaoistElement type) {
		return new DamageElemental("taoistelement.direct."+type.toString(), source, null,type);
	}
}
