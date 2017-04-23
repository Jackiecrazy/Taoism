package com.Jackiecrazi.taoism.common.items.weapons;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.client.ExtendThyReachHelper;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan.LianDanHandler;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class SilverNeedle extends ItemSword implements ICustomRange,ISwingSpeed {
	private static final Random r = new Random();
	public static final String BODY="body";
	public SilverNeedle(ToolMaterial p_i45356_1_) {
		super(p_i45356_1_);
		//this.name = ;
		setMaxStackSize(1);
		setCreativeTab(Taoism.TabTaoistWeapon);
		setUnlocalizedName("SilverNeedle");
		
	}

	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.drink;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack p_77626_1_) {
		return 36;
	}

	@Override
	public ItemStack onEaten(ItemStack is, World w, EntityPlayer p)
    {
		if (!w.isRemote&&p instanceof EntityPlayer) {
			
			AcupunctSelf((EntityPlayer)p);
			is.damageItem(1, p);
			LianDanHandler.getThis((EntityPlayer)p).addXP(r.nextFloat()/100);
		}
		return is;
	}

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 2F;
	}

	public void AcupunctSelf(EntityPlayer p) {
		if(p==null)return;
		int lvl=LianDanHandler.getThis(p).getLevel();
		if(r.nextInt(200)<=LianDanHandler.getThis(p).getLevel()){
			int poi=r.nextInt(StaticRefs.goodPotEffects.size());
			int dur=lvl*6;
			int pot=r.nextInt(3)+lvl/40;
			p.addPotionEffect(new PotionEffect(poi,dur,pot));
		}
		else{
			int poi=r.nextInt(StaticRefs.badPotEffects.size());
			int dur=600/(lvl+1);
			int pot=r.nextInt(3)+(1/(lvl+1));
			if(pot>0)
			p.addPotionEffect(new PotionEffect(poi,dur,pot));
		}
	}
	public void AcupunctOthers(EntityPlayer p,EntityLiving uke) {
		if(p==null||uke==null)return;
		int lvl=LianDanHandler.getThis(p).getLevel();
		if(r.nextInt(200)<=LianDanHandler.getThis(p).getLevel()){
			int poi=r.nextInt(StaticRefs.goodPotEffects.size());
			int dur=lvl*6;
			int pot=r.nextInt(3)+lvl/40;
			uke.addPotionEffect(new PotionEffect(poi,dur,pot));
		}
		else{
			int poi=r.nextInt(StaticRefs.badPotEffects.size());
			int dur=600/(lvl+1);
			int pot=r.nextInt(3)+(1/(lvl+1));
			if(pot>0)
			uke.addPotionEffect(new PotionEffect(poi,dur,pot));
		}
	}
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		if(ExtendThyReachHelper.getMouseOver(0, getRange(player,stack))!=null&&entity instanceof EntityLiving){
			
		AcupunctOthers(player, ((EntityLiving)entity));
		}
		return true;
	}
	public int getItemEnchantability()
    {
        return 0;
    }

	/*@Override
	public int getUltimateTime() {
		return 300;
	}

	@Override
	public float getUltimateCost() {
		return 300;
	}*/

	/*@Override
	public int getCDTime() {
		return 600;
	}*/
	//TODO ultimate needle rain
	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
		if (!el.worldObj.isRemote&&el instanceof EntityPlayer)
			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
					((ISwingSpeed) this).swingSpd()),
					new TargetPoint(el.dimension,el.posX,el.posY,el.posZ,64));
		//System.out.println(el.worldObj.isRemote+" "+el.attackTime);
		return el.attackTime!=0;
	}

	@Override
	public float hungerUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int swingSpd() {
		// TODO Auto-generated method stub
		return 5;
	}

}
