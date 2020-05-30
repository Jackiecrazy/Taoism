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
     * Stores up charge when in hand, to a cap, released during an attack. Cannot block or parry.
     * Normal attack, notably, throws out a projectile instead of actually attacking, damage and velocity determined by charge.
     * This means it naturally ignores non-shield blocks.
     * Alt attack is an arcing overhead smash. This ignores all forms of blocking, and bind 1/4 on first hit entity.
     * The alt attack is capable of drawing bound enemies in on another click.
     * Escape velocity for bound entity and catch threshold for projectile are both based on charge.
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
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return (getMaxChargeTime() - getChargeTimeLeft(attacker, item)) / 10f;
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
            erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, (getMaxChargeTime() - getChargeTimeLeft(elb, is)) / 10f, 0.0F);
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
}
