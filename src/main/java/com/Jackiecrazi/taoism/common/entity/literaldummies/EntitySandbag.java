package com.Jackiecrazi.taoism.common.entity.literaldummies;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.api.allTheInterfaces.IBlunt;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.items.TaoItems;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;

public class EntitySandbag extends Entity {
	public float shake;
	public float shakeAnimation; // used to have an independent start for the
									// animation, otherwhise the phase of the
									// animation depends ont he damage dealt
	// used to calculate the whole damage in one tick, in case there are
	// multiple sources
	public float dir; // indicates when we started taking damage and
								// if != 0 it also means that we are currently
								// recording damage taken
	
	private ItemStack content;
	private Vec3 vec;

	public EntitySandbag(World p_i1582_1_) {
		super(p_i1582_1_);
		this.preventEntitySpawning = true;
		setSize(0.9F, 2.5F);
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onEntityUpdate() {
		// System.out.println("Sandbag exists");
		if(shake>0)
		this.shake-=0.0001F;
		else shake=0;
		if(this.worldObj.getBlock((int)this.posX, (int)this.posY+3, (int)this.posZ).getMaterial()==Material.air){
			//System.out.println("The block at "+((int)this.posX-1)+", "+((int)this.posY+3)+", "+((int)this.posZ-1)+" is air, breaking...");
			//pack(false,true);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound c) {
		if (c.hasKey("contents")) {
			NBTTagCompound cont = c.getCompoundTag("contents");
			ItemStack read = ItemStack.loadItemStackFromNBT(cont);
			content = read;
		}
		

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound c) {
		NBTTagCompound n = new NBTTagCompound();
		if (content != null) {
			content.writeToNBT(n);
			c.setTag("contents", n);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource s, float dam) {
		if (s.damageType.equals("player")) {
			EntityPlayer p = (EntityPlayer) s.getEntity();
			if (p.isSneaking())
				pack(p.capabilities.isCreativeMode,false);
			else if(p.getHeldItem()!=null){
				if(p.getHeldItem().getItem() instanceof ItemSword&&!(p.getHeldItem().getItem() instanceof IBlunt)){
					pack(p.capabilities.isCreativeMode,true);
				}
			}
			shake=WuGongHandler.getThis(p).getLevel()*0.001F;
			vec=p.getLookVec();
			//System.out.println(vec.xCoord+" "+vec.yCoord+" "+vec.zCoord);
		}
		return true;
	}

	private void pack(boolean creative,boolean uncool) {
		if (!worldObj.isRemote && !creative) {
			if(uncool)
				dropItem(Items.leather,3);
			else
			dropItem(TaoItems.sandbag, 1);
			if (content != null)
				dropItem(content.getItem(), content.stackSize);
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

	public ItemStack getContents() {
		return content;
	}
	
	public int getContentSize() {
		return content==null?0:content.stackSize;
	}

	public boolean changeContents(EntityPlayer p) {
		if (p.getHeldItem() != null) {
			Item sand = p.getHeldItem().getItem();
			if (sand instanceof ItemBlock) {
				Block sandy = Block.getBlockFromItem(sand);
				if (sandy == TaoBlocks.PoisonSand || sandy == Blocks.sand
						|| sandy == Blocks.soul_sand) {
					content = p.getHeldItem();
					p.inventory.setInventorySlotContents(
							p.inventory.currentItem, null);
				} else
					return false;
			}

		} else if (content != null) {
			p.inventory.addItemStackToInventory(content);
			content = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean hitByEntity(Entity p) {
		// if(p instanceof EntityPlayer&&changeContents((EntityPlayer)p))return
		// true;
		return super.hitByEntity(p);
	}

	public boolean interactFirst(EntityPlayer p)
    {
		
		if (changeContents(p))
			return true;
		return super.interactFirst(p);
	}
	public Vec3 getDir(){
		return vec==null?Vec3.createVectorHelper(0, 0, 0):vec;
	}
}
