package com.jackiecrazi.taoism.crafting;

import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RecipeCraftWeapon extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private boolean isValidPart(ItemStack is) {
		if (is.isEmpty()) return false;
		if (is.getItem() != TaoItems.part) return false;
		if (!is.hasTagCompound()) return false;
		return new PartData(is.getTagCompound()).isValid();
	}

	@Nullable
	private PartData pd(ItemStack i) {
		if (!isValidPart(i)) return null;
		return new PartData(i.getTagCompound());
	}

	//0 1 2
	//3 4 5
	//6 7 8

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ArrayList<PartData> ais = new ArrayList<PartData>();
		for (int soso = 0; soso < inv.getSizeInventory(); soso++) {
			ItemStack is = inv.getStackInSlot(soso);
			if(is.isEmpty())continue;
			if (isValidPart(is)) {
				ais.add(pd(is));
			}
			else{
				//System.out.println("aiya");
				return false;
			}
		}
		if (ais.size() > 4) return false;
		boolean handle = false, head = false, fitting = false;
		//check that everything exists only once
		for (PartData pd : ais) {
			switch (pd.getWeaponSW().getClassification()) {
			case StaticRefs.HANDLE:
				if (!handle) {
					handle = true;
					break;
				} else return false;
			case StaticRefs.FITTING:
				if (!fitting) {
					fitting = true;
					break;
				} else return false;
			case StaticRefs.HEAD:
				if (!head) {
					head = true;
					break;
				} else return false;

			}
		}
		return handle&head&fitting;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {

		ArrayList<PartData> ais = new ArrayList<PartData>();
		for (int soso = 0; soso < inv.getSizeInventory(); soso++) {
			ItemStack is = inv.getStackInSlot(soso);
			if (isValidPart(is)) {
				ais.add(pd(is));
			}
		}
		if (ais.size() > 4) return ItemStack.EMPTY;
		boolean handle = false, head = false, fitting = false;
		//check that everything exists only once
		for (PartData pd : ais) {
			sw: switch (pd.getWeaponSW().getClassification()) {
			case StaticRefs.HANDLE:
				if (!handle) {
					handle = true;
					break sw;
				} else return ItemStack.EMPTY;
			case StaticRefs.FITTING:
				if (!fitting) {
					fitting = true;
					break sw;
				} else return ItemStack.EMPTY;
			case StaticRefs.HEAD:
				if (!head) {
					head = true;
					break sw;
				} else return ItemStack.EMPTY;

			}
		}
		//System.out.println("prelimary check passed");
		ais = new ArrayList<PartData>();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) {
				ItemStack is = inv.getStackInSlot(i);
				if (is.hasTagCompound()&&is.getItem()==TaoItems.part) {
					PartData pd = new PartData(is.getTagCompound());
					if (pd.isValid()) ais.add(pd);
				}
			}
		}
		return TaoWeapon.createWeapon(null, ais);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 4;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
