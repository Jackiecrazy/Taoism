package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityRopeDart;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class RopeDart extends TaoWeapon {
    public static final int MAXRANGESQ = 64;

    /*
     * A rope weapon that charges up into fast, unblockable and constricting hits. High speed and range, medium power and defense, low combo
     * Two handed.
     * Lots of wrapping motions that burst into a quick release, can also continuously throw if needed. Almost like a dance, until it kills you.
     * 镖打回头
     * Stores up charge when in hand, to a cap, released during an attack. Cannot block or parry.
     * Normal attack, notably, throws out a projectile instead of actually attacking, damage and velocity determined by charge.
     * This means it naturally ignores non-shield blocks.
     * Alt attack is an arcing overhead smash. This inflicts light bonking damage and, if not blocked or parried, will bind hit target
     *      After bind, become capable of parrying and delivers a critical punch in the main hand with range 2
     *      If binding person, lasso down with offhand, retrieves the rope and inflicts half max posture damage
     *      If parried or blocked, disarm opponent until retrieved with offhand
     *
     * On the same vein:
     * Meteor hammer: more swinging, stunning+kb hits, power+
     * Flying claws: rip and grapple, pull enemies close or grapple away, speed+
     *
     * While equipped, the dart orbits around you in a set pattern. Saves summoning and killing, and gives a passive hit aura
     * At the same time, letting anyone get close is potentially fatal.
     */
    public RopeDart() {
        super(2, 4, 5, 0);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getChargeTimeLeft(attacker, item)==0?2:1;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1+getChargeTimeLeft(attacker, item)/20;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return crit>=2 ? Event.Result.ALLOW : Event.Result.DENY;
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
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (gettagfast(stack).hasKey("dartID") && w.getEntityByID(gettagfast(stack).getInteger("dartId")) == null) {
            gettagfast(stack).removeTag("dartID");
            gettagfast(stack).removeTag("connected");
        }
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote && elb.world.getEntityByID(gettagfast(is).getInteger("dartId")) == null) {
            EntityRopeDart erd = new EntityRopeDart(elb.world, elb);
            erd.setPositionAndRotation(elb.posX, elb.posY + elb.getEyeHeight(), elb.posZ, elb.rotationYaw, elb.rotationPitch);
            erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 0.5f+(getMaxChargeTime() - getChargeTimeLeft(elb, is)) / 10f, 0.0F);
            elb.world.spawnEntity(erd);
            gettagfast(is).setInteger("dartID", erd.getEntityId());
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) getChargeTimeLeft(null, stack) / (double) getMaxChargeTime();
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        super.onSwitchIn(stack, elb);
        stack.setItemDamage(getMaxChargeTime());
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
    }

    @Override
    public boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return gettagfast(item).getBoolean("connected");
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        ds.setProjectile();
    }

    @Override
    public float finalDamageMods(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (ds.isProjectile()) {
            chargeWeapon(attacker, target, stack, getMaxChargeTime() / 2);
        }
        return orig;
    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    private void setEngage(ItemStack is, boolean engaged){
        gettagfast(is).setBoolean("engage",engaged);
    }

    private boolean isEngaged(ItemStack is){
        return gettagfast(is).getBoolean("engage");
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, ItemStack item) {
        return isEngaged(item);
    }
}
