/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.TaoisticPotions;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.client.ExtendThyReachHelper;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EMeiCi extends GenericTaoistWeapon implements ICustomRange {

	public EMeiCi(ToolMaterial tmat) {
		super(tmat,"emeici"+tmat.toString().toLowerCase());
	}

	@Override
	public int getUltimateTime() {
		return 0;
	}

	@Override
	public float getUltimateCost() {
		return 750;
	}

	@Override
	public int getCDTime() {
		return 600;
	}

	@Override
	public float hungerUsed() {
		return 0;
	}

	@Override
	public int swingSpd() {
		return 5;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 2;
	}
	@Override
	public void onUltimate(EntityPlayer p) {
		super.onUltimate(p);
		p.addPotionEffect(new PotionEffect(TaoisticPotions.Hide.id,400,-4));
		p.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,400,-4));
	}
	@Override
	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p,
			int i) {
		super.onPlayerStoppedUsing(is, w, p, i);
		if (p.isSneaking()) {
			//onUltimate(p);
		}
		else{
			if (p.worldObj.isRemote) {
				MovingObjectPosition mov = ExtendThyReachHelper.getMouseOver(0, 6);
				if (mov != null && mov.entityHit != null && mov.entityHit != p&&mov.entityHit instanceof EntityLivingBase) {
					Taoism.net.sendToServer(new PacketExtendThyReach(mov.entityHit
							.getEntityId(), false));
					//System.out.println("sent packet");
					//mov.entityHit.hurtResistantTime=1;
				} 
			}
		}
	}

	@Override
	protected void setParts() {
		parts.add(StaticRefs.HANDLE);
		parts.add(StaticRefs.TIP);
	}
	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
		if (!el.worldObj.isRemote&&el instanceof EntityPlayer)
			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
					((ISwingSpeed) this).swingSpd()),
					new TargetPoint(el.dimension,el.posX,el.posY,el.posZ,64));
		return el.attackTime!=0;
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.TIP;
	}
}
*/