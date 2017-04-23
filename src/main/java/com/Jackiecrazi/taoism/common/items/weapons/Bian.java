/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class Bian extends GenericTaoistWeapon {
	
	public Bian(ToolMaterial tmat) {
		super(tmat, "bian" + tmat.toString().toLowerCase(),5f,0.2f,10,null,StaticRefs.HANDLE,StaticRefs.POMMEL,StaticRefs.GUARD,StaticRefs.SHAFT,StaticRefs.KNOTS);
		
	}

	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0.2f;
	}

	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		// TODO Auto-generated method stub
		return 5;
	}
	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
		if (!el.worldObj.isRemote&&el instanceof EntityPlayer)
			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
					((ISwingSpeed) this).swingSpd()),
					new TargetPoint(el.dimension,el.posX,el.posY,el.posZ,64));
		return el.attackTime!=0;
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.HANDLE);
		this.parts.add(StaticRefs.POMMEL);
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.GUARD);
		this.parts.add(StaticRefs.KNOTS);
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.SHAFT;
	}
}
*/