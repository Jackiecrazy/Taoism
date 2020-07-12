package com.jackiecrazi.taoism.common.item.weapon.melee.sesword;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Zanbato extends TaoWeapon {
    /*
    large two-handed blade to counter cavalry. High power and defense, medium combo and range, low speed
    Two handed!
    Left and right click reset each other's attack bar to half
    Left click is a high damage chop that additionally hits all entities riding or being ridden by the target
    Right click is a 120 degree slash at half damage
    Consecutive alternating attacks crit
     */

    public Zanbato() {
        super(3, 1.2, 7, 1);
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
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }
}
