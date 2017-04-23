package com.Jackiecrazi.taoism.common.block;

import com.Jackiecrazi.taoism.Taoism;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TaoisticOreBlock extends Block {

	public TaoisticOreBlock(String name,String texName) {
		super(Material.rock);
		this.setBlockName(name);
		this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setBlockTextureName(texName);
	}

}
