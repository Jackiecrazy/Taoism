package com.Jackiecrazi.taoism.api.allTheDamageTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageElemental extends EntityDamageSourceIndirect {
	public enum TaoistElement{
		METAL,
		WOOD,
		WATER,
		FIRE,
		EARTH,
		WIND,
		THUNDER,
		YIN,
		YANG,
		SHA
	}
	private TaoistElement e;
	public DamageElemental(String s, Entity proxy,
			Entity seme,TaoistElement el) {
		super(s, proxy, seme);
		e=el;
	}
	@Override
	public IChatComponent func_151519_b(EntityLivingBase target)
    {
		String howtodie = "death.attack." + this.damageType+"."+e;
		ItemStack stack=null;
	    IChatComponent source = getEntity() == null ? getSourceOfDamage().func_145748_c_() : this.getEntity().func_145748_c_();
	    if(getEntity() != null && getEntity() instanceof EntityLivingBase){
	    stack =  ((EntityLivingBase)getEntity()).getHeldItem();
	    }
	    else if(getSourceOfDamage() instanceof EntityLivingBase){
	    	stack=((EntityLivingBase)getSourceOfDamage()).getHeldItem();
	    }
	    String deathname = howtodie + ".item";

	    return stack != null && stack.hasDisplayName() && StatCollector.canTranslate(deathname) ? 
	            new ChatComponentTranslation(deathname, target.func_145748_c_(), source, stack.getDisplayName()) :
	            new ChatComponentTranslation(howtodie, target.func_145748_c_(), source);
		
    }
	public static DamageSource causeElementDamageIndirectly(Entity source, Entity transmitter,TaoistElement type) {
		return new DamageElemental("taoistelement.indirect."+type.toString(), source, transmitter,type);
	}
	public static DamageSource causeElementDirectly(Entity source,TaoistElement type) {
		return new DamageElemental("taoistelement.direct."+type.toString(), source, null,type);
	}
}
