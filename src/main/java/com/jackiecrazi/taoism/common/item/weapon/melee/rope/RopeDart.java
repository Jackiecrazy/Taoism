package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityRopeDart;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RopeDart extends TaoWeapon {
    /*
     * A rope weapon that charges up into fast, unblockable and constricting hits. High speed and range, medium power and defense, low combo
     * Two handed.
     * Stores up charge when in hand, to a cap, released during an attack. Cannot block or parry.
     * Normal attack, notably, throws out a projectile instead of actually attacking, damage and velocity determined by charge.
     * This means it naturally ignores non-shield blocks.
     * Alt attack is an arcing overhead smash. This ignores all forms of blocking, and bind 1/4 on first hit entity.
     * The alt attack is capable of drawing bound enemies in on another click.
     * Escape velocity for bound entity and catch threshold for projectile are both based on charge.
     */
    public RopeDart() {
        super(2, 4, 5, 0);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public int getMaxChargeTime() {
        return 40;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 0;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 0;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        super.onSwitchIn(stack, elb);
        stack.setItemDamage(getMaxChargeTime());
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        is.setItemDamage(getMaxChargeTime());
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) getChargeTimeLeft(null, stack) / (double) getMaxChargeTime();
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote) {
            EntityRopeDart erd = new EntityRopeDart(elb.world, elb);
            erd.setPositionAndRotation(elb.posX, elb.posY, elb.posZ, elb.rotationYaw, elb.rotationPitch);
            erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, (getMaxChargeTime()-getChargeTimeLeft(elb,is))/20f, 0.0F);
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }
}
