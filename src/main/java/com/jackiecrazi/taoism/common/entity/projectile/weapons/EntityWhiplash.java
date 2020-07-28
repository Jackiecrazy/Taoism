package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityWhiplash extends EntityThrownWeapon {
    double dist;

    public EntityWhiplash(World worldIn) {
        super(worldIn);
    }

    public EntityWhiplash(World worldIn, EntityLivingBase dude, EnumHand main, double maxDist) {
        super(worldIn, dude, main);
        dist = maxDist;
        if (main != null)
            hand = main;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setDouble("maxRangeSq", dist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        dist = compound.getDouble("maxRangeSq");
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != stack.getItem() || this.getDistanceSq(getThrower()) > dist * dist * 2) {
                this.setDead();
                return;
            }
            if (this.getDistanceSq(getThrower()) > dist * dist) {
                setDead();
            } else if (this.getDistanceSq(getThrower()) > (dist - 1) * (dist - 1)) {
                //sonic boom!
                sonicBoom();
                setDead();
            }
        }
        super.onUpdate();
    }

    private void sonicBoom() {
        if(isDead)return;
        stack.setTagInfo("boomer", new NBTTagInt((byte) 1));
        if (world instanceof WorldServer)
            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, posX, posY, posZ, 1, 0f, 0f, 0f,0);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_PLAYER_BIG_FALL, SoundCategory.PLAYERS, 1.4f, 0.4f+rand.nextFloat()*0.6f);
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(1), TaoCombatUtils.VALID_TARGETS)) {
            TaoCombatUtils.attackIndirectly(getThrower(), this, e, hand);
        }
        stack.setTagInfo("boomer", new NBTTagInt((byte) 0));
        setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return hitStatus > 0 ? 0F : 0.01f;
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        onRetrieveWeapon();
    }

    @Override
    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0 || world.isRemote || getThrower() == null) return;
        super.onHitEntity(hit);
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        assert is.getTagCompound() != null;
        stack.setTagInfo("rangedAttack", new NBTTagInt(1));
        if (NeedyLittleThings.getDistSqCompensated(hit, getThrower()) > (dist - 1) * (dist - 1))
            sonicBoom();
        else TaoCombatUtils.attackIndirectly(getThrower(), this, hit, hand);
        stack.setTagInfo("rangedAttack", new NBTTagInt(0));
        onRetrieveWeapon();
    }



    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
    }
}
