/*package com.Jackiecrazi.taoism.common.items.weapons;

import com.Jackiecrazi.taoism.api.StaticRefs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Fu extends GenericTaoistWeapon {

	public Fu(ToolMaterial tmat, String suf) {
		super(tmat, "axe"+tmat.toString().toLowerCase()+suf);
	}

	@Override
	public float hungerUsed() {
		return 0.8f;
	}

	@Override
	public int swingSpd() {
		return 20;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 5;
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.EDGE;
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.HEAD);
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.EDGE);
	}

}
*/