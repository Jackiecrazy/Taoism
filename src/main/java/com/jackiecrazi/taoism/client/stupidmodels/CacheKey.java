package com.jackiecrazi.taoism.client.stupidmodels;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

class CacheKey {

    final NBTTagCompound data;

    protected CacheKey(ItemStack stack) {
        this.data = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheKey cacheKey = (CacheKey) o;

			/*if(parent != null ? parent != cacheKey.parent : cacheKey.parent != null) {
			  return false;
			}*/
        return data != null ? data.equals(cacheKey.data) : cacheKey.data == null;

    }

    @Override
    public int hashCode() {
        //int result = parent != null ? parent.hashCode() : 0;
        int result = 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
