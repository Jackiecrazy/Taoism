/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.api.StaticRefs;

public class YanYueDao extends GenericTaoistWeapon {
	public YanYueDao(ToolMaterial tmat) {
		super(tmat, "yanyuedao" + tmat.toString().toLowerCase());
		this.isEdgy=true;
	}

	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0.6f;
	}

	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.DAOBLADE);
		this.parts.add(StaticRefs.GUARD);
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.TIP);
		this.parts.add(StaticRefs.EDGE);
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.EDGE;
	}

}
*/