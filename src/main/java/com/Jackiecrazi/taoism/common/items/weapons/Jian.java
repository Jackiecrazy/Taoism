/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageConcussion;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IBlunt;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class Jian extends GenericTaoistWeapon implements IBlunt,ICustomRange{
private float atkDam;
public Jian(ToolMaterial material) {
		super(material,"jian");
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		//this.setUnlocalizedName("jian");
		this.texName="taoism:jian";
		this.atkDam=material.getDamageVsEntity();
		
	}

	@Override
	public int getUltimateTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getUltimateCost() {
		// TODO Auto-generated method stub
		return 500;
	}
	@Override
	public void onUltimate(EntityPlayer p) {
		super.onUltimate(p);
		if(p.isClientWorld()){
			MovingObjectPosition mov = ExtendThyReachHelper.getMouseOver(0, 6);
			if (mov != null && mov.entityHit != null && mov.entityHit != p&&mov.entityHit instanceof EntityLivingBase) {
			Taoism.net.sendToServer(new PacketExtendThyReach(mov.entityHit
					.getEntityId(), false));
			}
		}
	}
	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0.03F;
	}

	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public int getCDTime() {
		// TODO Auto-generated method stub
		return 100;
	}
	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p,
			int i) {
		super.onPlayerStoppedUsing(is, w, p, i);
		if (p.isSneaking()) {
			//onUltimate(p);
		} 
		
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity targetEntity) {
		if (targetEntity.isEntityAlive() && !player.worldObj.isRemote) {
				targetEntity.attackEntityFrom(
						DamageConcussion.causeBrainDamageDirectly(player), atkDam);
				targetEntity.hurtResistantTime = 0;
				if(targetEntity instanceof EntityLivingBase){
					((EntityLivingBase)targetEntity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,20,1));
				}
		}
		return super.onLeftClickEntity(stack, player, targetEntity);
	}
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
		//player.isBlocking()=true;
    }

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		// TODO Auto-generated method stub
		return 4;
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
		
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.SHAFT;
	}
}
*/