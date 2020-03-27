package com.jackiecrazi.taoism.common.item.weapon.melee.pick;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemChickenSickle extends TaoWeapon {
    /*
     * An unassuming weapon until you fight back. High power and speed, medium combo and defense, low range
     * Attacks hook enemy in and stack hemorrhage 2/5
     * Riposte: for 3 seconds, next attack stacks hemorrhage 3/5 and detonates hemorrhage into layer% of hp and stacks bleed layer/10
     */
    public ItemChickenSickle(int damageType, double swingSpeed, double damage, float BasePostureConsumption) {
        super(damageType, swingSpeed, damage, BasePostureConsumption);
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
