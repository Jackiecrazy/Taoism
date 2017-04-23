package com.Jackiecrazi.taoism.common.items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;

public class Fuuuu extends Item {
	private int type;
	private int effect;
	private int duration;
	private int somanyprivateints;
	private boolean damageSoul;
	private float yinyang;
	public Fuuuu()
	{
		setMaxStackSize(1);
		setCreativeTab(Taoism.TabTaoistWeapon);
		setUnlocalizedName("WrittenFu");
	}
	public ItemStack onItemRightClick(ItemStack itemstack, World worldIn, EntityPlayer player){
		System.out.println(type);
		System.out.println(effect);
		System.out.println(duration);
		System.out.println(somanyprivateints);
		System.out.println(damageSoul);
		System.out.println(yinyang);
		return itemstack;
	}
}
