package com.jackiecrazi.taoism.client.stupidmodels;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class ProjectileTinter implements IItemColor {
    public static final ProjectileTinter INSTANCE = new ProjectileTinter();

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        switch (stack.getItemDamage()) {
            case 1:
                return Color.BLACK.getRGB();
            default:
                return -1;
        }
    }
}
