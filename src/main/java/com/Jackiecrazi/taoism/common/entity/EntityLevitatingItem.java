package com.Jackiecrazi.taoism.common.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.common.block.tile.TaoisticInvTE;

public class EntityLevitatingItem extends EntityItem {
	//rotate around the master, moving straight to enter orbit if needed.
	//when the master requests consumption, move towards master and 
	//teleologically recombinate when touching
	private static final int XDW=5,YDW=6,ZDW=7,sac=9,rad=11;
	private int x,y,z;
	private double radius;
	private boolean beginSacrifice;
	public EntityLevitatingItem(World w) {
		super(w);
		this.noClip=true;
	}
	
	protected void entityInit()
    {
        super.entityInit();
        this.getDataWatcher().addObject(XDW, Integer.valueOf(0));//x
        this.getDataWatcher().addObject(YDW, Integer.valueOf(0));//y
        this.getDataWatcher().addObject(ZDW, Integer.valueOf(0));//z
        this.getDataWatcher().addObject(sac, Byte.valueOf((byte)0));//z
        getDataWatcher().addObject(rad, Float.valueOf((float)radius));
    }
	
	public EntityLevitatingItem(World w, double x,
			double y, double z, ItemStack is, int xc,
			int yc, int zc) {
		super(w, x, y, z, is);
		this.x=xc;
		this.y=yc;
		this.z=zc;
		//System.out.println(xc+" "+yc+" "+zc);
		//System.out.println(x+" "+y+" "+z);
		this.noClip=true;
		radius=4;
	}
	public void setX(int x) {
		this.x = x;
		getDataWatcher().updateObject(XDW, Integer.valueOf(x));
	}
	public void setY(int y) {
		this.y = y;
		getDataWatcher().updateObject(YDW, Integer.valueOf(y));
	}
	public void setZ(int z) {
		this.z = z;
		getDataWatcher().updateObject(ZDW, Integer.valueOf(z));
	}
	public void setXYZ(int x, int y, int z){
		//System.out.println("setting to "+x+" "+y+" "+z+" on side "+(worldObj.isRemote?"client":"server")+" from "+posX+" "+posY+" "+posZ);
		setX(x);
		setY(y);
		setZ(z);
	}
	public boolean isEntityInvulnerable()
    {
        return true;
    }
	public void onUpdate()
    {
		//System.out.println(this.toString()+" is on client: "+worldObj.isRemote);
		//this.setDead();
		TileEntity te=worldObj.getTileEntity(x, y, z);
		if(worldObj.isRemote)return;
		//System.out.println(x+" "+y+" "+z+" "+te);
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
        {
            if (stack.getItem().onEntityItemUpdate(this))
            {
                return;
            }
        }

        if (this.getEntityItem() == null)
        {
        	
            this.setDead();
        }
        else if(((te==null)|| !(te instanceof TaoisticInvTE))&&!worldObj.isRemote){//||worldObj.getTileEntity(te.xCoord, te.yCoord, te.zCoord)==null
        	
        	worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ,this.getEntityItem()));
        	this.setDead();
        }
        else
        {
            super.onEntityUpdate();
            if(te==null||!(te instanceof TaoisticInvTE))return;
            TaoisticInvTE tite=(TaoisticInvTE)te;
            if(this.beginSacrifice||getDataWatcher().getWatchableObjectByte(sac)==1){
            	//GO! move slowly to te and if touching sacrifice
            	if(radius>0)
            	radius-=0.05d;
            	if(radius<0)radius=0;
            	if(this.posX==x&&this.posZ==z){
            	tite.eatItem(this.getEntityItem());
            	this.setDead();
            	return;
            	}
            	//System.out.println("eat");
            }
            getDataWatcher().updateObject(rad, Float.valueOf((float)radius));
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            //this.motionY -= 0.03999999910593033D;
            this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            double mult=0.000001;
            /*if(this.getDistance(x, y, z)>radius){
            	this.motionX=mult*(x-posX);
            	this.motionY=mult*(y-posY);
            	this.motionZ=mult*(y-posZ);
            }
            else if(this.getDistance(x, y, z)<radius-2){
            	this.motionX=mult*(posX-x);
            	this.motionY=mult*(posY-y);
            	this.motionZ=mult*(posZ-z);
            }
            else{
            	this.motionX=0;
            	this.motionY=0;
            	this.motionZ=0;
            }*/
            this.posX=(radius*Math.sin(NeedyLittleThings.rad(this.ticksExisted)))+x;
            this.posY=y+(radius/2);
            this.posZ=(radius*Math.cos(NeedyLittleThings.rad(this.ticksExisted)))+z;
            /*System.out.println();
            System.out.println(worldObj.isRemote);
            System.out.println(x);
            System.out.println(y);
            System.out.println(z);
            System.out.println();*/
            
            //System.out.println("oompa loompa");
            //this.moveEntity(this.motionX, this.motionY, this.motionZ);
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

                /*if (!this.worldObj.isRemote)
                {
                    this.searchForOtherItemsNearby();
                }*/
            }
            
            float f = 0.98F;

            if (this.onGround)
            {
                f = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
            }
            
            this.motionX *= (double)f;
            this.motionY = 0;
            this.motionZ *= (double)f;

            if (this.onGround)
            {
                //this.motionY *= -0.5D;
            }

            ++this.age;

            ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);
    
            if (!this.worldObj.isRemote && this.age >= lifespan)
            {
                if (item != null)
                {   
                    ItemExpireEvent event = new ItemExpireEvent(this, (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, worldObj)));
                    if (MinecraftForge.EVENT_BUS.post(event))
                    {
                        lifespan += event.extraLife;
                    }
                    else
                    {
                        this.setDead();
                    }
                }
                else
                {
                    this.setDead();
                }
            }
    
            if (item != null && item.stackSize <= 0)
            {
                this.setDead();
            }
        }
    }
	public void onCollideWithPlayer(EntityPlayer p_70100_1_){}//you can't pick it up
	public void readEntityFromNBT(NBTTagCompound tag)
    {
		super.readEntityFromNBT(tag);
		beginSacrifice=tag.getBoolean("sacrificing");
		this.x=tag.getInteger("masterx");
		this.y=tag.getInteger("mastery");
		this.z=tag.getInteger("masterz");
		this.radius=tag.getDouble("radius");
		getDataWatcher().updateObject(XDW, Integer.valueOf(x));
		getDataWatcher().updateObject(YDW, Integer.valueOf(y));
		getDataWatcher().updateObject(ZDW, Integer.valueOf(z));
		getDataWatcher().updateObject(sac, Byte.valueOf(beginSacrifice?(byte)1:(byte)0));
    }
	public void writeEntityToNBT(NBTTagCompound tag)
    {
		super.writeEntityToNBT(tag);
		tag.setBoolean("sacrificing", beginSacrifice);
		tag.setInteger("masterx", x);
		tag.setInteger("mastery", y);
		tag.setInteger("masterz", z);
		tag.setDouble("radius", radius);
    }
	public void requestSacrifice(){
		beginSacrifice=true;
		getDataWatcher().updateObject(sac, Byte.valueOf((byte)1));
	}
	
	
	
	
}
