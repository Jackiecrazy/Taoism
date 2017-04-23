package com.Jackiecrazi.taoism.api.allTheInterfaces;

import net.minecraft.item.ItemStack;

/**
 * @author Jackiecrazy
 * <BR>Anything that is a LianQi material should implement this. Otherwise a dummy interface.
 */
public interface ILianQiMaterial extends IElemental {
public boolean isMat(ItemStack i);
public boolean isWood(int meta);
public boolean isMetal(int meta);
public boolean isQuench(int meta);
}
