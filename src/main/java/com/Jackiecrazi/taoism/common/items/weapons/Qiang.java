/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageArmorPierce;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.client.ExtendThyReachHelper;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;

public class Qiang extends GenericTaoistWeapon implements ICustomRange {
	private float atkDam;
	public Qiang(ToolMaterial tawood) {
		super(tawood,"yari" + tawood.toString().toLowerCase());
		//this.setUnlocalizedName();
		this.texName = "taoism:yari";
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.atkDam = tawood.getDamageVsEntity() + 2.5f;
		this.isEdgy=true;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 7;
	}

	@Override
	public int getUltimateTime() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	public float getUltimateCost() {
		// TODO Auto-generated method stub
		return 1500;
	}

	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public int getCDTime() {
		// TODO Auto-generated method stub
		return 600;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity targetEntity) {
		if (targetEntity.isEntityAlive() && !player.worldObj.isRemote) {
			if (player.isSneaking()) {
				targetEntity
						.addVelocity(
								-MathHelper
										.sin(player.rotationYaw * 3.141593F / 180.0F) * 1.2 * 0.5F,
								0.1D,
								MathHelper
										.cos(player.rotationYaw * 3.141593F / 180.0F) * 1.2 * 0.5F);
			} else {
				targetEntity.attackEntityFrom(
						DamageArmorPierce.punctureDirectly(player), atkDam);
				targetEntity.hurtResistantTime = 0;
				return true;
			}

		}
		return super.onLeftClickEntity(stack, player, targetEntity);
	}

	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p,
			int i) {
		super.onPlayerStoppedUsing(is, w, p, i);
		if (p.isSneaking()) {
			//onUltimate(p);
		} else {
			//onUltimateTick(p);
		}
	}

	public void onUltimateTick(EntityPlayer p) {

		if (p.worldObj.isRemote) {
			MovingObjectPosition mov = ExtendThyReachHelper.getMouseOver(0, 6);
			if (mov != null && mov.entityHit != null && mov.entityHit != p&&mov.entityHit instanceof EntityLivingBase) {
				Taoism.net.sendToServer(new PacketExtendThyReach(mov.entityHit
						.getEntityId(), false));
				//System.out.println("sent packet");
				//mov.entityHit.hurtResistantTime=1;
			} 
		}
		// this.onLeftClickEntity(stack, p, targetEntity)
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.TIP);
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.POMMEL);
		this.parts.add(StaticRefs.GUARD);
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.TIP;
	}

}
*/