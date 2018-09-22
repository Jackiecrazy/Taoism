package com.jackiecrazi.taoism.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.jackiecrazi.taoism.api.MaterialStatWrapper;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.config.MaterialsConfig;

public class RecipeCraftPart extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		WeaponStatWrapper wsw=null;
		int mat=0;MaterialStatWrapper msw=null;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.blueprint){
					if(wsw!=null)return false;
					wsw=((ItemBlueprint)i.getItem()).toWSW(i);
					
				}
				else if(MaterialsConfig.findMat(i)!=null){
					if(MaterialsConfig.findMat(i).isAnvilWorked())return false;
					if(msw!=null&&MaterialsConfig.findMat(i).msw!=msw)return false;
					else if(msw==null)msw=MaterialsConfig.findMat(i).msw;
					mat+=MaterialsConfig.findMat(i).amount;
				}
				else return false;
			}
		}
		if(wsw==null||msw==null)return false;
		return wsw.getCost()==mat;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		WeaponStatWrapper wsw=null;
		MaterialStatWrapper msw=null;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.blueprint){
					wsw=((ItemBlueprint)i.getItem()).toWSW(i);
				}
				else if(MaterialsConfig.findMat(i)!=null){
					msw=MaterialsConfig.findMat(i).msw;
				}
			}
		}
		if(wsw==null||msw==null)return ItemStack.EMPTY;
		PartData ret=new PartData(wsw.getClassification(),msw.name,wsw.getOrdinal());
		if(ret.isValid())return ret.toStack();
		else return ItemStack.EMPTY;
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
