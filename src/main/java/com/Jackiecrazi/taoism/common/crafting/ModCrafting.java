package com.Jackiecrazi.taoism.common.crafting;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModCrafting {
	public static void initCrafting(){
		GameRegistry.addRecipe(new ItemStack(ModItems.SilverNeedle), "n", "n", "n", 'n', ModItems.ResourceMetal);
		GameRegistry.addRecipe(new ItemStack(ModItems.sandbag)," f ","l l","lll",'f',Blocks.fence,'l',Items.leather);
		GameRegistry.addRecipe(new ItemStack(ModItems.muRenZhuang),"sfs","lfl","sfs",'f',Blocks.fence,'l',Items.stick,'s',Items.string);
		GameRegistry.addRecipe(new ItemStack(ModItems.Bellows),"www","wls","www",'w',Blocks.planks,'s',Items.stick,'l',Items.leather);
		GameRegistry.addRecipe(new ItemStack(ModItems.Anvil),"iii"," n ","nin",'i',Blocks.iron_block,'n',Items.iron_ingot);
		//GameRegistry.addRecipe(new ItemStack(ModItems.),"sfs","lfl","sfs",'f',Blocks.fence,'l',Items.stick,'s',Items.string);
}
}
