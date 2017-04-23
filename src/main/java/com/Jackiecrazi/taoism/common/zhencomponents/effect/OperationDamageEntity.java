package com.Jackiecrazi.taoism.common.zhencomponents.effect;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectModifier;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectOperation;

public class OperationDamageEntity extends ZhenEffectOperation {
	private DamageSource sourc;
	private float origDam;
	public OperationDamageEntity(Block source, int lingcost,DamageSource ds,float originalPotency) {
		super(source, lingcost);
		sourc=ds;
		origDam=originalPotency;
	}

	@Override
	public void performEffect(TileEntity te, Entity[] e,ZhenEffectModifier zem) {
		for(Entity en:e){
			if(en.isEntityAlive()&&en instanceof EntityLiving){
				EntityLiving ent=(EntityLiving)en;
				ent.attackEntityFrom(sourc, origDam);
				
			}
		}
	}

	@Override
	public void performEffect(TileEntity te, TaoistPosition[] e,ZhenEffectModifier zem) {
		//do nothing. Damage blocks elementally? Whaaat?
	}

	@Override
	protected void addModifierEffect(ZhenEffectModifier zem) {
		
	}

}
