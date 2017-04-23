package com.Jackiecrazi.taoism.common.block;

import net.minecraft.block.BlockSand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BlockPoisonSand extends BlockSand {

	protected BlockPoisonSand() {
		super();
		// TODO Auto-generated constructor stub
		this.setBlockName("PoisonSand");
	}
	public void onEntityWalking(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity e) {
		if(e instanceof EntityLiving){
			((EntityLiving) e).addPotionEffect(new PotionEffect(Potion.poison.id,100));
		}
	}

}
