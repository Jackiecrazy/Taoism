/*package com.Jackiecrazi.taoism.common.items.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IBlunt;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SwordGenericWood extends GenericTaoistWeapon implements IBlunt{
	public SwordGenericWood(ToolMaterial tawood) {
		super(tawood,"fword"+tawood.toString().toLowerCase());
		//this.setUnlocalizedName("fword");
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.isEdgy=true;
	}
	public void onItemRightClick(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		par1ItemStack.setTagCompound (new NBTTagCompound());
		int nbt = par1ItemStack.getTagCompound().getInteger("hitcount");
		if(nbt == 0){
			par1ItemStack.getTagCompound().setInteger("hitcount", 0);
			System.out.println("set hitcount to 0");
		}
	}
	*//**
	@Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double) defaultBaseAttackModifier, 0));
        return multimap;
    }
	public float defaultBaseAttackModifier = 3.0f;
	 public void updateAttack(EnumSet<SwordType> swordType,NBTTagCompound tag,EntityPlayer el,ItemStack sitem){
	        float tagAttackAmplifier = this.AttackAmplifier.get(tag);


	        float baseModif = getBaseAttackModifiers(tag);
	        float attackAmplifier = 0;
	 }
	 *//*
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	  {
	    par3List.add("\u00a7a" + "Hit it with lightning!");
	    if(par1ItemStack.hasTagCompound()){
	    	int heylisten = par1ItemStack.getTagCompound().getInteger("hitcount");
	    	par3List.add("Times hit by lightning: " + heylisten);
	    }
	  }
	 
	@Override
	public int getUltimateTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getUltimateCost() {
		// TODO Auto-generated method stub
		return 50;
	}
	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 7;
	}
	//@Override
	public int getCDTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 4;
	}
	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
		if (!el.worldObj.isRemote&&el instanceof EntityPlayer)
			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
					((ISwingSpeed) this).swingSpd()),
					new TargetPoint(el.dimension,el.posX,el.posY,el.posZ,64));
		//System.out.println(el.worldObj.isRemote+" "+el.attackTime);
		return el.attackTime!=0;
	}
	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.SWORDBLADE);
		this.parts.add(StaticRefs.EDGE);
		this.parts.add(StaticRefs.HANDLE);
		this.parts.add(StaticRefs.POMMEL);
		this.parts.add(StaticRefs.GUARD);
	}
	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.EDGE;
	}
}*/