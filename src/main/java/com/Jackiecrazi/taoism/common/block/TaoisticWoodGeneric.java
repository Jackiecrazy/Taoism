package com.Jackiecrazi.taoism.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.Jackiecrazi.taoism.Taoism;

public class TaoisticWoodGeneric extends Block {
	public TaoisticWoodGeneric() {
		super(Material.wood);
		setBlockName("TaoisticWood");
        setCreativeTab(Taoism.TabTaoistWeapon);
        setHardness(2.0F);
	}
	public int getMetadata(int damage){
		return damage;
	}
	public void onDrop(){
		/*System.out.println("hey");
		System.out.println("hey");
		System.out.println("listen");
		System.out.println("it's not done yet");*/
	}

}
