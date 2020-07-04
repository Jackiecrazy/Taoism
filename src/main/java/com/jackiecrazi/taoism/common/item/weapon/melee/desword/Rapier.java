package com.jackiecrazi.taoism.common.item.weapon.melee.desword;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Rapier extends TaoWeapon {
    public Rapier() {
        super(2, 1.6, 5, 1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public int getMaxChargeTime() {
        return 0;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 0;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 0;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }
}
