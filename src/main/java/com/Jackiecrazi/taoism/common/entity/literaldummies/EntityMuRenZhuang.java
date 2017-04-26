package com.Jackiecrazi.taoism.common.entity.literaldummies;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.common.items.TaoItems;

public class EntityMuRenZhuang extends Entity {
	public int rotation;//0 south 1 east 2 north 3 west
	
	public EntityMuRenZhuang(World p_i1582_1_) {
		super(p_i1582_1_);
		this.preventEntitySpawning = true;
		setSize(0.5F, 2F);
		//this.rotation=orientation;
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound n) {
		rotation=n.getInteger("rotation");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound n) {
		n.setInteger("rotation", rotation);
	}

	@Override
	public void onEntityUpdate() {
		if (this.worldObj.getBlock((int) this.posX, (int) this.posY - 1,
				(int) this.posZ).getMaterial() == Material.air) {
			pack(false, true);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource s, float dam) {
		if (s.damageType.equals("player")) {
			EntityPlayer p = (EntityPlayer) s.getEntity();
			if (p.isSneaking())
				pack(p.capabilities.isCreativeMode, false);

		}
		return true;
	}

	private void pack(boolean creative, boolean uncool) {
		if (!worldObj.isRemote && !creative) {
			if (uncool) {
				dropItem(Items.stick, 5);
				dropItem(Items.string, 2);
			} else
				dropItem(TaoItems.muRenZhuang, 1);

		}
		this.setDead();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity p) {
		return p.getBoundingBox();
		// return AxisAlignedBB.getBoundingBox(-1, -0.5, -1, 1, 3, 1);
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
}
