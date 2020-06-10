package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.item.ItemStack;

public interface IMove {
    enum EnumPhase{
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

    EnumPhase getPhase(final ItemStack stack);

}
