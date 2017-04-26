package com.Jackiecrazi.taoism.common.crafting;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.Jackiecrazi.taoism.common.items.TaoItems;

import cpw.mods.fml.common.registry.GameRegistry;

public class TaoCrafting {
	public static void initCrafting(){
		GameRegistry.addRecipe(new ItemStack(TaoItems.SilverNeedle), "n", "n", "n", 'n', TaoItems.ResourceMetal);
		GameRegistry.addRecipe(new ItemStack(TaoItems.sandbag)," f ","l l","lll",'f',Blocks.fence,'l',Items.leather);
		GameRegistry.addRecipe(new ItemStack(TaoItems.muRenZhuang),"sfs","lfl","sfs",'f',Blocks.fence,'l',Items.stick,'s',Items.string);
		GameRegistry.addRecipe(new ItemStack(TaoItems.Bellows),"www","wls","www",'w',Blocks.planks,'s',Items.stick,'l',Items.leather);
		GameRegistry.addRecipe(new ItemStack(TaoItems.Anvil),"iii"," n ","nin",'i',Blocks.iron_block,'n',Items.iron_ingot);
		//GameRegistry.addRecipe(new ItemStack(ModItems.),"sfs","lfl","sfs",'f',Blocks.fence,'l',Items.stick,'s',Items.string);
}
}
