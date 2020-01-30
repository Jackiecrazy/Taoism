package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IMove {
    public EntityMove getMove(ItemStack stack, EntityLivingBase user, int qiLevel, int combo, boolean left);

}
