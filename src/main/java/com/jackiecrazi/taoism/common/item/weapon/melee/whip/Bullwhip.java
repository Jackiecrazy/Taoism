package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityKusarigamaShot;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityWhiplash;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/*
one handed, no parry, can be parried by enemy:
Standard hit knocks back a little more than usual.
if it hits near the limit of its range (that is, 7 blocks), it creates a sonic boom that... basically is an explosion.
mobs in a very small area take splash damage, is disoriented and knocked away slightly.
Shift is the other way around, focusing on drawing enemies close:
standard hit will apply negative knockback so you two collide into each other, giving the target brief slow, fatigue and bind.
striking at max range will instead disarm (bind) the enemy for a good while.
Damage is doubly reduced by armor, but it hits hard to start off, as per other whips.
note to self: make the projectile icon a loop and fiddle with the tether so it looks like the loop is traveling down the whip.
 */
public class Bullwhip extends TaoWeapon {
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            new PartDefinition("handlewrap", false, StaticRefs.STRING)
    };

    public Bullwhip() {
        super(1, 1.4, 9, 0.5f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote) {
            if (!isThrown(is)) {
                EntityKusarigamaShot eks = new EntityKusarigamaShot(elb.world, elb, getHand(is));
                eks.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, getBuff(is, "hitCharge") / (float) getMaxChargeTime(), 0.0F);
                elb.world.spawnEntity(eks);
                gettagfast(elb.getHeldItemMainhand()).setInteger("dartID", eks.getEntityId());
            } else {
                if (elb.isSneaking()) {
                    EntityWhiplash erd = getLash(is, elb);
                    if (erd != null) erd.setDead();
                }
            }
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 0;
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return false;
    }

    private boolean isThrown(ItemStack is) {
        return gettagfast(is).hasKey("dartID");
    }

    private EntityWhiplash getLash(ItemStack is, EntityLivingBase elb) {
        if (isThrown(is) && elb.world.getEntityByID(getBallID(is)) != null)
            return (EntityWhiplash) elb.world.getEntityByID(getBallID(is));
        return null;
    }

    private int getBallID(ItemStack is) {
        return gettagfast(is).getInteger("dartID");
    }
}
