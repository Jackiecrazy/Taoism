package com.jackiecrazi.taoism.api.allthedamagetypes;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

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
	public static DamageSource causeElementDamageIndirectly(Entity source, Entity transmitter,TaoistElement type) {
		return new DamageElemental("taoistelement.indirect."+type.toString(), source, transmitter,type);
	}
	public static DamageSource causeElementDirectly(Entity source,TaoistElement type) {
		return new DamageElemental("taoistelement.direct."+type.toString(), source, null,type);
	}
}
