package com.jackiecrazi.taoism.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.jackiecrazi.taoism.common.item.TaoItems;

public class RecipeUpdateWeapon extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean ret=false;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					if(ret)return false;
					ret=true;;
					
				}
				else return false;
			}
		}
		return ret;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack weap=null;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					if(weap!=null)return ItemStack.EMPTY;
					weap=i;
					
				}
				else return ItemStack.EMPTY;
			}
		}
		ItemStack ret=weap.copy();
		weap.getItem().onCreated(ret, null, null);
		return ret;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width*height>=2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic(){
		return true;
	}
}