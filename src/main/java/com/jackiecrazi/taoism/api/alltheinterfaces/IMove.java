package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IMove {
    public enum EnumPhase{
        IDLE,
        WINDUP,
        WOUND,
        RECOVERY
    }
    /*
    a movement code is a byte that represents the series of inputs of the player.
    Forward, back, left, right, jump, sneak, lmb, rmb (in case we want to make combination attacks later, though unlikely)
    A byte is 8 bits so this fits perfectly.
     */

    public EnumPhase getPhase(final ItemStack stack);

    public EntityMove getMove(ItemStack stack, EntityLivingBase user, int qiLevel, int combo, byte code);

}
