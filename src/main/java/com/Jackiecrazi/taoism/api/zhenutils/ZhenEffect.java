package com.Jackiecrazi.taoism.api.zhenutils;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.common.block.tile.TileZhenJiao;

public class ZhenEffect {
	private ZhenEffectShape zes;
	private ZhenEffectFilter zef;
	private ZhenEffectOperation zeo;
	private ZhenEffectModifier zem;
	private int cost;
	public ZhenEffect(ZhenEffectShape s, ZhenEffectFilter f,
			ZhenEffectOperation o, ZhenEffectModifier m) {
		zes = s;
		zef = f;
		zeo = o;
		zem = m;
		if(o.getPrice()>0)
		cost=(s.getPrice()+f.getPrice())*(o.getPrice()+m.getPrice());
		else
		cost=(o.getPrice()*m.getPrice())/(s.getPrice()+f.getPrice());
	}
	public boolean passesNullCheck(){
		return zes!=null&&zef!=null&&zeo!=null&&zem!=null;
	}

	public void performEffect(World w, TileZhenJiao source) {
		boolean entity = true;
		switch (zef.getType()) {
		case AI:
			break;
		case BLOCK:
			entity = false;
			break;
		case CREATURETYPE:
			break;
		case ENTITY:
			break;
		default:
			break;
		}
		if (entity) {
			Entity[] target=zes.performEffectEntity(w, new TaoistPosition(source.getPos(),source.getWorld()), source.getEffectStart().getX(),
					source.getEffectStart().getY(), source.getEffectStart().getZ(), source
							.getEffectEnd().getX(), source.getEffectEnd().getY(),
					source.getEffectEnd().getZ());
			for(int ent=0;ent<target.length;ent++)
			if(!zef.passesFilter(target[ent]))target[ent]=null;
			zeo.performEffect(source, target, zem);
			
		} else {
			TaoistPosition[] target=zes.performEffectBlock(w, new TaoistPosition(source.getPos(),source.getWorld()), source.getEffectStart().getX(),
					source.getEffectStart().getY(), source.getEffectStart().getZ(), source
							.getEffectEnd().getX(), source.getEffectEnd().getY(),
					source.getEffectEnd().getZ());
			for(int ent=0;ent<target.length;ent++)
			if(!zef.passesFilter(source.getWorld(), target[ent]))target[ent]=null;
			zeo.performEffect(source, target, zem);
		}
	}
	public int getCost(){return cost;}
}
