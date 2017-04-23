//package com.Jackiecrazi.taoism.common.items.weapons;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//
//import com.Jackiecrazi.taoism.api.StaticRefs;
//
//public class Cha extends GenericTaoistWeapon {
//	public Cha(ToolMaterial tmat) {
//		super(tmat, "cha" + tmat.toString().toLowerCase());
//	}
//
//	@Override
//	public float hungerUsed() {
//		return 0.6f;
//	}
//
//	@Override
//	public int swingSpd() {
//		return 15;
//	}
//
//	@Override
//	public float getRange(EntityPlayer p, ItemStack is) {
//		return 7;
//	}
//
//	@Override
//	protected void setParts() {
//		this.parts.add(StaticRefs.POMMEL);
//		this.parts.add(StaticRefs.SHAFT);
//		this.parts.add(StaticRefs.PRONGS);
//		this.parts.add(StaticRefs.HEAD);
//	}
//
//	@Override
//	protected void setPointyBit() {
//		this.offensivebit=StaticRefs.PRONGS;
//	}
//
//}
