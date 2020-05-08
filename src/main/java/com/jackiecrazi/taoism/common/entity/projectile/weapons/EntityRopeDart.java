package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.common.item.TaoItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityRopeDart extends EntityThrowable {

    public EntityRopeDart(World w) {
        super(w);
    }

    public EntityRopeDart(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public void onUpdate() {
        if (this.thrower == null) this.setDead();
        if (this.thrower.getHeldItemMainhand().getItem() != TaoItems.ropedart) this.setDead();
        super.onUpdate();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null && thrower != null) {
            Entity e = result.entityHit;
            ItemStack is = thrower.getHeldItemMainhand();
            if (is.getItem() != TaoItems.ropedart) return;
            NeedyLittleThings.taoWeaponAttack(e, (EntityPlayer) thrower, is, true, false, DamageSource.causePlayerDamage((EntityPlayer) thrower).setProjectile());
        }
    }
}
