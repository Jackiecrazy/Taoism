package com.Jackiecrazi.taoism.api;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.Jackiecrazi.taoism.common.items.TaoItems;
import com.Jackiecrazi.taoism.common.items.armor.ClothingWushu;

public class NeedyLittleThings {
	
	public static float rad(float angle) { 
		return angle * (float) Math.PI / 180; 
	}

	public static int posmod(int i, int n){
		return ((i%n)+n)%n;
	}
	
	public static Vec3d lookVector(Entity player) {
		float rotationYaw = player.rotationYaw, rotationPitch = player.rotationPitch;
		float vx = -MathHelper.sin(rad(rotationYaw)) * MathHelper.cos(rad(rotationPitch));
		float vz = MathHelper.cos(rad(rotationYaw)) * MathHelper.cos(rad(rotationPitch));
		float vy = -MathHelper.sin(rad(rotationPitch));
		return new Vec3d(vx,vy,vz);
	}
	public static void attackWithDebuff(Entity targetEntity, EntityPlayer player,float debuff)
	  {}/*
		if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, targetEntity)))
	    {
			 //System.out.println("nein");
	      return;
	      
	    }
	    if (targetEntity.canBeAttackedWithItem())
	    {
	      if (!targetEntity.hitByEntity(player))
	      {
	        float f = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
	        int i = 0;
	        float f1 = 0.0F;

	        if ((targetEntity instanceof EntityLivingBase))
	        {
	          f1 = EnchantmentHelper.getEnchantmentLevel(player, (EntityLivingBase)targetEntity);
	          i += EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase)targetEntity);
	        }

	        if (player.isSprinting())
	        {
	          i++;
	        }

	        if ((f > 0.0F) || (f1 > 0.0F))
	        {
	          boolean flag = (player.fallDistance > 0.0F) && (!player.onGround) && (!player.isOnLadder()) && (!player.isInWater()) && (!player.isPotionActive(Potion.blindness)) && (player.ridingEntity == null) && ((targetEntity instanceof EntityLivingBase));

	          if ((flag) && (f > 0.0F))
	          {
	            f *= 1.5F;
	          }

	          f += f1;
	          boolean flag1 = false;
	          int j = EnchantmentHelper.getFireAspectModifier(player);

	          if (((targetEntity instanceof EntityLivingBase)) && (j > 0) && (!targetEntity.isBurning()))
	          {
	            flag1 = true;
	            targetEntity.setFire(1);
	          }
	          //System.out.println(targetEntity.worldObj.isRemote);
	          boolean flag2 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), f);
	          
	          //System.out.println(targetEntity.worldObj.isRemote);
	          if (flag2)
	          {
	            if (i > 0)
	            {
	              targetEntity.addVelocity(-MathHelper.sin(player.rotationYaw * 3.141593F / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 3.141593F / 180.0F) * i * 0.5F);
	              player.motionX *= 0.6D;
	              player.motionZ *= 0.6D;
	              player.setSprinting(false);
	            }

	            if (flag)
	            {
	              player.onCriticalHit(targetEntity);
	            }

	            if (f1 > 0.0F)
	            {
	              player.onEnchantmentCritical(targetEntity);
	            }

	            if (f >= 18.0F)
	            {
	              player.triggerAchievement(AchievementList.overkill);
	            }

	            player.setLastAttacker(targetEntity);

	            if ((targetEntity instanceof EntityLivingBase))
	            {
	              EnchantmentHelper.func_151384_a((EntityLivingBase)targetEntity, player);
	            }
	          }

	          ItemStack itemstack = player.getCurrentEquippedItem();
	          Object object = targetEntity;

	          if ((targetEntity instanceof EntityDragonPart))
	          {
	            IEntityMultiPart ientitymultipart = ((EntityDragonPart)targetEntity).entityDragonObj;

	            if ((ientitymultipart != null) && ((ientitymultipart instanceof EntityLivingBase)))
	            {
	              object = (EntityLivingBase)ientitymultipart;
	            }
	          }

	          if ((itemstack != null) && ((object instanceof EntityLivingBase)))
	          {
	            itemstack.hitEntity((EntityLivingBase)object, player);

	            if (itemstack.stackSize <= 0)
	            {
	              player.destroyCurrentEquippedItem();
	            }
	          }

	          if ((targetEntity instanceof EntityLivingBase))
	          {
	            player.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

	            if ((j > 0) && (flag2))
	            {
	              targetEntity.setFire(j * 4);
	            }
	            else if (flag1)
	            {
	              targetEntity.extinguish();
	            }
	          }

					player.addExhaustion(0.001F);
	        }
	      }
	    }
	  }*/
	public static boolean isUltimating(EntityPlayer player){
		return player.world.getTotalWorldTime()-player.getEntityData().getLong("HissatsuTimeStart")<player.getEntityData().getInteger("HissatsuDuration");
	}
	public static void writeToTaoisticNBT(NBTTagCompound compound,String tag, Object data){
		NBTTagCompound master=compound.getTag("Taoism")==null?new NBTTagCompound():(NBTTagCompound)compound.getTag("Taoism");
		NBTTagCompound spr=new NBTTagCompound();
		if(data instanceof ItemStack){
			ItemStack is=(ItemStack)data;
			master.setTag(tag, is.writeToNBT(spr));
		}
		else if(data == int.class){
			master.setInteger(tag, (Integer)data);
		}
		else if(data == float.class){
			master.setFloat(tag, (Float)data);
		}
		else if(data == short.class){
			master.setShort(tag, (Short)data);
		}
		else if(data == long.class){
			master.setLong(tag, (Long)data);
		}
		else if(data == String.class){
			master.setString(tag, (String)data);
		}
		compound.setTag("Taoism", master);
	}
	public static NBTTagCompound getTaoisticNBT(NBTTagCompound tc){
		return (NBTTagCompound) (tc.getTag("Taoism")==null?new NBTTagCompound():tc.getTag("Taoism"));
	}
	public static boolean isWearingFullSet(EntityPlayer p,Item reference){
		Item helm=null,chest = null,leg=null,boot=null;
		boolean ret=false;
		if(p.inventory.armorItemInSlot(0)!=null)helm=p.inventory.armorItemInSlot(0).getItem();
		if(p.inventory.armorItemInSlot(1)!=null)chest=p.inventory.armorItemInSlot(1).getItem();
		if(p.inventory.armorItemInSlot(2)!=null)leg=p.inventory.armorItemInSlot(2).getItem();
		if(p.inventory.armorItemInSlot(3)!=null)boot=p.inventory.armorItemInSlot(3).getItem();
		if(helm!=null&&chest!=null&&leg!=null&&boot!=null){
			if(reference instanceof ClothingWushu){
				ret= helm==TaoItems.wushuRibbon&&chest==TaoItems.wushuShirt&&leg==TaoItems.wushuPants&&boot==TaoItems.wushuShoes;
			}
		}
		return ret;
	}
	public static int avg(int orig, int[] additions){
		for(int a:additions)orig+=a;
		return (int)(orig/additions.length);
	}
	public static TaoistPosition[] bresenham(double x1,double y1,double z1,double x2, double y2, double z2){
		
		double p_x=x1; double p_y=y1; double p_z=z1;
		double d_x=x2-x1; double d_y=y2-y1; double d_z=z2-z1;
		int N=(int)Math.ceil(Math.max(Math.abs(d_x),Math.max(Math.abs(d_y),Math.abs(d_z))));
		double s_x=d_x/N; double s_y=d_y/N; double s_z=d_z/N;
		//System.out.println(N);
		TaoistPosition[] out=new TaoistPosition[N];
		if(out.length==0){
			System.out.println("nay!");
			return out;
		}
		out[0]=new TaoistPosition((int)p_x,(int)p_y,(int)p_z);
		for (int ii=1;ii<N;ii++){
			p_x+=s_x; p_y+=s_y; p_z+=s_z;
			out[ii]=new TaoistPosition((int)p_x,(int)p_y,(int)p_z);
		}
		return out;
	}
}
