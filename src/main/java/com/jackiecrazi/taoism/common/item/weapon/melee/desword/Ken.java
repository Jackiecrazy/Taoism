package com.jackiecrazi.taoism.common.item.weapon.melee.desword;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Ken extends TaoWeapon {
    //relentless.
    //normal attack chains up to 3 times before requiring cooldown (sword flowers). Small AoE
    //has low starting damage, chain up to do more damage
    public Ken() {
        super(1, 1.6, 6, 1f);
        this.setQiAccumulationRate(0.3f);//slight nerf to account for extremely high attack speed
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        if (isCharged(wielder, is)) return 9;
        return 3;
    }

    @Override
    public int getMaxChargeTime() {
        return 180;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (isAoE(attacker, stack))
            splash(attacker, stack, 90);
//            else {
//                splash(attacker, stack, 10);
//            }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("ken.combo"));
        tooltip.add(I18n.format("ken.qi"));
        tooltip.add(I18n.format("ken.aoe"));
        tooltip.add(I18n.format("ken.leap"));
        tooltip.add(I18n.format("ken.stab"));
        tooltip.add(I18n.format("ken.riposte"));
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //combo limit is raised to 9 for the next 9 seconds
        setCombo(defender, item, 0);
        super.parrySkill(attacker, defender, item);
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return isAoE(attacker, item) ? Event.Result.DENY : Event.Result.ALLOW;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float air = attacker.motionY < 0 ? 1.5f : 1f;
        float aoe = isAoE(attacker, item) ? 1f : 1.5f;
        return air * aoe;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1 + (getQiFromStack(item) / 27f);
    }

    private boolean isAoE(EntityLivingBase attacker, ItemStack is) {
        return NeedyLittleThings.raytraceEntity(attacker.world, attacker, getReach(attacker, is)) == null;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3;
    }
}
