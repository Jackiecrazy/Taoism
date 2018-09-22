package com.jackiecrazi.taoism.crafting;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;

public class RecipeReplacePart extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack weap=null;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					if(weap!=null)return false;
					weap=i;
					
				}
			}
		}
		if(weap==null)return false;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					//do nothing
				}
				else if(i.getItem()==TaoItems.part){
					if(!i.hasTagCompound())continue;
					PartData pd=new PartData(i.getTagCompound());
					if(!((TaoWeapon)weap.getItem()).isValidAddition(weap, pd.getPart(), pd))return false;
				}
				else return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ArrayList<PartData> apd=new ArrayList<PartData>();
		ItemStack weap=null;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					if(weap!=null)return ItemStack.EMPTY;
					weap=i;
					
				}
			}
		}
		if(weap==null)return ItemStack.EMPTY;
		for(int a=0;a<inv.getSizeInventory();a++){
			ItemStack i=inv.getStackInSlot(a);
			if(!i.isEmpty()){
				if(i.getItem()==TaoItems.weap){
					//do nothing
				}
				else if(i.getItem()==TaoItems.part){
					if(!i.hasTagCompound())continue;
					PartData pd=new PartData(i.getTagCompound());
					if(!((TaoWeapon)weap.getItem()).isValidAddition(weap, pd.getPart(), pd))return ItemStack.EMPTY;
					else apd.add(pd);
				}
				else return ItemStack.EMPTY;
			}
		}
		ItemStack ret=weap.copy();
		for(PartData pd:apd){
		((TaoWeapon)weap.getItem()).setPart(pd.getPart(), ret, pd);
		weap.getItem().onCreated(ret, null, null);
		}
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
