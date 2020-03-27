package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCatNineTail extends TaoWeapon {
    //whipping implement used on prisoners, causing grievous lacerations. High power and speed, medium defense and range, low combo potential
    //
    //Attacks inflict laceration (10-armor/2)/(5-armor/4) and bind weapon 1/1.5 with 3 cd
    //that's all. Better find something to cleave that armor off first...

    public ItemCatNineTail() {
        super(1, 1.4f, 4f, 0);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 0;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 0;
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return 0;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 0;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {

    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }
}
