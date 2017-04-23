package com.Jackiecrazi.taoism.common.entity.literaldummies;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDroppedWeapon extends Entity
{
	private String owner, thrower;
	private float rotx,roty,rotz;
	public float getRotx() {
		return rotx;
	}
	public float getRoty() {
		return roty;
	}
	public float getRotz() {
		return rotz;
	}
	public EntityDroppedWeapon(World w) {
		super(w);
	}
	public EntityDroppedWeapon(World w, double x,
			double y, double z, ItemStack is, float rx,float ry, float rz) {
		this(w);
		//System.out.println("go!");
		this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
		setEntityItemStack(is);
		setSize(0.5f,2);
	}
	
	public boolean isEntityInvulnerable()
    {
        return true;
    }
	
	public void onUpdate()
    {
        
        if (this.getEntityItem() == null)
        {
            this.setDead();
        }
        else
        {
            super.onUpdate();

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

            if (flag || this.ticksExisted % 25 == 0)
            {
                if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava)
                {
                    this.motionY = 0.20000000298023224D;
                    this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

            
            }

            float f = 0.98F;

            if (this.onGround)
            {
                f = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
                
            }else{
            	zero(rotx);
                zero(roty);
                zero(rotz);
            }

            this.motionX *= (double)f;
            this.motionY *= 0.8;
            this.motionZ *= (double)f;

            
            
            if (this.onGround)
            {
                this.motionY *= -0.5D;
            }

        }
    }

	protected boolean canTriggerWalking()
    {
        return false;
    }
	
	public ItemStack getEntityItem()
    {
        ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack p_92058_1_)
    {
        this.getDataWatcher().updateObject(10, p_92058_1_);
        this.getDataWatcher().setObjectWatched(10);
    }
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound c) {
		if (c.hasKey("Owner"))
        {
            this.owner = c.getString("Owner");
        }

        if (c.hasKey("Thrower"))
        {
            this.thrower = c.getString("Thrower");
        }

        NBTTagCompound nbttagcompound1 = c.getCompoundTag("Item");
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(nbttagcompound1));

        ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

        if (item == null || item.stackSize <= 0)
        {
        	this.setDead();
        }
        if(c.hasKey("rotx"))
        	rotx=c.getFloat("rotx");
        	else rotx=worldObj.rand.nextFloat();
        if(c.hasKey("roty"))
            roty=c.getFloat("roty");
            else roty=worldObj.rand.nextFloat();
        if(c.hasKey("rotz"))
            rotz=c.getFloat("rotz");
            else rotz=worldObj.rand.nextFloat();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound c) {
        if (this.func_145800_j() != null)
        {
            c.setString("Thrower", this.owner);
        }

        if (this.func_145798_i() != null)
        {
            c.setString("Owner", this.thrower);
        }

        if (this.getEntityItem() != null)
        {
            c.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
        
        
	}

	@Override
	public boolean attackEntityFrom(DamageSource s, float dam) {
		if (s.damageType.equals("player")) {
			EntityPlayer p = (EntityPlayer) s.getEntity();
			return changeContents(p);
		}
		return true;
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

	public boolean changeContents(Entity p) {
		if(!(p instanceof EntityPlayer))return false;
		if (getEntityItem()!= null&&((EntityPlayer)p).getHeldItem()==null) {
			((EntityPlayer)p).setCurrentItemOrArmor(0, getEntityItem());
			this.setDead();
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
	@Override
	protected void entityInit() {
		this.getDataWatcher().addObjectByDataType(10, 5);
	}
	
	public String func_145798_i()
    {
        return this.thrower;
    }

    public void func_145797_a(String p_145797_1_)
    {
        this.thrower = p_145797_1_;
    }

    public String func_145800_j()
    {
        return this.owner;
    }

    public void func_145799_b(String p_145799_1_)
    {
        this.owner = p_145799_1_;
    }
    
    private float zero(float orig){
    	if(orig<0){
    		if(orig>-0.01)orig=0;
    		else orig+=0.001;
    	}
    	else{
    		if(orig<0.01)orig=0;
    		else orig-=0.001;
    	}
    	return orig;
    }
}