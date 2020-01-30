package com.jackiecrazi.taoism.api.alltheinterfaces;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public interface IElemental {
    String[] ELEMS = {"metal", "wood", "water", "fire", "earth"};
    TextFormatting[] ELEMC = {TextFormatting.WHITE, TextFormatting.GREEN, TextFormatting.BLACK, TextFormatting.RED, TextFormatting.YELLOW};
    IAttribute[] ATTRIBUTES = {
            (new RangedAttribute(null, "generic.affMetal", 1.0D, 0, 1024D)).setDescription("Metal Affinity").setShouldWatch(true),
            (new RangedAttribute(null, "generic.affWood", 1.0D, 0, 1024D)).setDescription("Wood Affinity").setShouldWatch(true),
            (new RangedAttribute(null, "generic.affWater", 1.0D, 0, 1024D)).setDescription("Water Affinity").setShouldWatch(true),
            (new RangedAttribute(null, "generic.affFire", 1.0D, 0, 1024D)).setDescription("Fire Affinity").setShouldWatch(true),
            (new RangedAttribute(null, "generic.affEarth", 1.0D, 0, 1024D)).setDescription("Earth Affinity").setShouldWatch(true)

    };

    default NBTTagCompound getElementBase(ItemStack is) {
        if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
        return is.getTagCompound().getCompoundTag("TaoistParts");
    }

    /**
     * @return a list of elemental affinities, in the order metal, wood, water, fire, earth
     */
    default float[] getAffinities(ItemStack is) {
        float[] ret = new float[ELEMS.length];
        for (int a = 0; a < ELEMS.length; a++)
            ret[a] = getAffinity(is, a);
        return ret;
    }

    default void setAffinities(ItemStack is, float... affinities) {
        for (int a = 0; a < Math.min(ELEMS.length, affinities.length); a++) {
            setAffinity(is, a, affinities[a]);
        }
    }

    /**
     * @param element  0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
     * @param affinity don't set it larger than 2^32-1 mmkay?
     */
    default void setAffinity(ItemStack is, int element, float affinity) {
        getElementBase(is).setFloat("element" + element, affinity);
    }

    /**
     * @param element 0 metal 1 wood 2 water 3 fire 4 earth 5 wind 6 thunder 7 yin 8 yang 9 sha
     * @return a float representing the affinity of an element
     */
    default float getAffinity(ItemStack is, int element) {
        return 0f;
    }

    default void addAffinity(ItemStack is, float... aff) {
        for (int a = 0; a < Math.min(ELEMS.length, aff.length); a++) {
            setAffinity(is, a, aff[a] + getAffinity(is, a));
        }
    }
}
