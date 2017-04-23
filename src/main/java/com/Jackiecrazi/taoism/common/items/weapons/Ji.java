/*package com.Jackiecrazi.taoism.common.items.weapons;

import com.Jackiecrazi.taoism.api.StaticRefs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Ji extends GenericTaoistWeapon {

	public Ji(ToolMaterial tmat,String append) {
		super(tmat, "ji"+tmat.toString().toLowerCase()+append);
	}

	@Override
	public float hungerUsed() {
		return 0.4f;
	}

	@Override
	public int swingSpd() {
		return 15;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 8;
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.TIP);
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.EDGE);
		this.parts.add(StaticRefs.POMMEL);
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.TIP;
	}

}
*/