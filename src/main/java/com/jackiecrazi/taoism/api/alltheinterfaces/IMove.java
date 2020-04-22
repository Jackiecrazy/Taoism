package com.jackiecrazi.taoism.api.alltheinterfaces;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IMove {
    public enum EnumPhase{
        IDLE,
        WINDUP,
        WOUND,
        RECOVERY
    }
    /*
    a movement code is a byte that represents the series of inputs of the player.
    Toggle, forward, back, left, right, jump, sneak, mouse button
    A byte is 8 bits so this fits perfectly.
     */

    public EnumPhase getPhase(final ItemStack stack);

    @Nullable
    public EntityMove getMove(ItemStack stack, EntityLivingBase user, byte code);

}
