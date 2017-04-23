//package com.Jackiecrazi.taoism.common.items.weapons;
//
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//
//import com.Jackiecrazi.taoism.Taoism;
//import com.Jackiecrazi.taoism.api.StaticRefs;
//import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
//import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;
//
//import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
//
//public class Chui extends GenericTaoistWeapon {
//	public Chui(ToolMaterial tmat) {
//		super(tmat, "chui" + tmat.toString().toLowerCase());
//		
//	}
//
//	@Override
//	public float hungerUsed() {
//		return 1f;
//	}
//
//	@Override
//	public int swingSpd() {
//		return 20;
//	}
//
//	@Override
//	public float getRange(EntityPlayer p, ItemStack is) {
//		return 4;
//	}
//	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
//		if (!el.worldObj.isRemote&&el instanceof EntityPlayer)
//			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
//					((ISwingSpeed) this).swingSpd()),
//					new TargetPoint(el.dimension,el.posX,el.posY,el.posZ,64));
//		return el.attackTime!=0;
//	}
//
//	@Override
//	protected void setParts() {
//		this.parts.add(StaticRefs.HANDLE);
//		this.parts.add(StaticRefs.HEAD);
//		this.parts.add(StaticRefs.POMMEL);
//		this.parts.add(StaticRefs.GUARD);
//	}
//
//	@Override
//	protected void setPointyBit() {
//		this.offensivebit=StaticRefs.HEAD;
//	}
//}
