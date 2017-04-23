package com.Jackiecrazi.taoism.common.taoistichandlers;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.networking.PacketSetInvContent;

public class PlayerEquipmentStalker implements IExtendedEntityProperties {
	private EntityPlayer p;
	private ItemStack headdress = null, mirror = null, belt = null,
			faqi1 = null, faqi2 = null, amulet = null, ring1 = null,
			ring2 = null, glove = null, back = null,cloak=null, gongfa = null,wuji=null;
	private HashMap<String,ItemStack> equips=new HashMap<String,ItemStack>();
	public PlayerEquipmentStalker(EntityPlayer pl) {
		p = pl;
	}

	@Override
	public void saveNBTData(NBTTagCompound c) {
		if (headdress != null)
			c.setTag("headdress", headdress.writeToNBT(new NBTTagCompound()));
		if (mirror != null)
			c.setTag("mirror", mirror.writeToNBT(new NBTTagCompound()));
		if (belt != null)
			c.setTag("belt", belt.writeToNBT(new NBTTagCompound()));
		if (faqi1 != null)
			c.setTag("faqi1", faqi1.writeToNBT(new NBTTagCompound()));
		if (faqi2 != null)
			c.setTag("faqi2", faqi2.writeToNBT(new NBTTagCompound()));
		if (amulet != null)
			c.setTag("amulet", amulet.writeToNBT(new NBTTagCompound()));
		if (ring1 != null)
			c.setTag("ring1", ring1.writeToNBT(new NBTTagCompound()));
		if (ring2 != null)
			c.setTag("ring2", ring2.writeToNBT(new NBTTagCompound()));
		if (glove != null)
			c.setTag("glove", glove.writeToNBT(new NBTTagCompound()));
		if (back != null)
			c.setTag("back", back.writeToNBT(new NBTTagCompound()));
		if (cloak != null)
			c.setTag("cloak", cloak.writeToNBT(new NBTTagCompound()));
		if (gongfa != null)
			c.setTag("gongfa", gongfa.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void loadNBTData(NBTTagCompound c) {
		headdress=ItemStack.loadItemStackFromNBT(c.getCompoundTag("headdress"));
		mirror=ItemStack.loadItemStackFromNBT(c.getCompoundTag("mirror"));
		belt=ItemStack.loadItemStackFromNBT(c.getCompoundTag("belt"));
		faqi1=ItemStack.loadItemStackFromNBT(c.getCompoundTag("faqi1"));
		faqi2=ItemStack.loadItemStackFromNBT(c.getCompoundTag("faqi2"));
		amulet=ItemStack.loadItemStackFromNBT(c.getCompoundTag("amulet"));
		ring1=ItemStack.loadItemStackFromNBT(c.getCompoundTag("ring1"));
		ring2=ItemStack.loadItemStackFromNBT(c.getCompoundTag("ring2"));
		glove=ItemStack.loadItemStackFromNBT(c.getCompoundTag("glove"));
		back=ItemStack.loadItemStackFromNBT(c.getCompoundTag("back"));
		cloak=ItemStack.loadItemStackFromNBT(c.getCompoundTag("cloak"));
		gongfa=ItemStack.loadItemStackFromNBT(c.getCompoundTag("gongfa"));
		
	}

	@Override
	public void init(Entity entity, World world) {
	}
	public static PlayerEquipmentStalker getEquipmentList(EntityPlayer p){
		return (PlayerEquipmentStalker)p.getExtendedProperties("taoistgear");
	}
	public ItemStack getEquipment(EnumEquipmentType e){
		ItemStack ret=null;
		switch(e){
		case HEADDRESS:
			ret=headdress;
			break;
		case MIRROR:
			ret=mirror;
			break;
		case BELT:
			ret=belt;
			break;
		case FAQI:
			ret=faqi1;
			break;
		case AMULET:
			ret=amulet;
			break;
		case RING:
			ret=ring2;
			break;
		case GLOVE:
			ret=glove;
			break;
		case BACK:
			ret=back;
			break;
		case CLOAK:
			ret=cloak;
			break;
		case GONGFA:
			ret=gongfa;
			break;
		case WUJI:
			//TODO
		}
		return ret;
	}
	public void setEquipment(EnumEquipmentType e,ItemStack n){
		switch(e){
		case HEADDRESS:
			headdress=n;
			break;
		case MIRROR:
			mirror=n;
			break;
		case BELT:
			belt=n;
			break;
		case FAQI:
			faqi1=n;
			break;
		case AMULET:
			amulet=n;
			break;
		case RING:
			ring1=n;
			break;
		case GLOVE:
			glove=n;
			break;
		case BACK:
			back=n;
			break;
		case CLOAK:
			cloak=n;
			break;
		case GONGFA:
			gongfa=n;
			break;
		case WUJI:
			//TODO
		}
		if (p instanceof EntityPlayerMP)
			Taoism.net.sendTo(new PacketSetInvContent(e,n),(EntityPlayerMP)p);
	}
}
