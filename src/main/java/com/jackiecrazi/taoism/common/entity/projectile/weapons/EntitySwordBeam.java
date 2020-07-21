package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntitySwordBeam extends EntityThrownWeapon {
    protected EnumHand h;
    protected String s;
    protected float zSpin;

    public EntitySwordBeam(World w) {
        super(w);
    }

    public EntitySwordBeam(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn, hand);
        s = is.getUnlocalizedName();
        h = hand;
    }

    public EntitySwordBeam setRenderRotation(float amnt) {
        this.zSpin = amnt;
        return this;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("spin", zSpin);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        zSpin = compound.getFloat("spin");
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (shootingEntity == null || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                this.setDead();
                return;
            }
        } else {
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX + rand.nextFloat() - 0.5, posY + rand.nextFloat() - 0.5, posZ + rand.nextFloat() - 0.5, 0, -0.1, 0);
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onHitEntity(Entity e) {
        if (!world.isRemote) {
            if (getThrower() != null) {
                if (e != null) {
                    if (e != getThrower()) {
                        if (h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                            this.setDead();
                            return;
                        }
                        TaoCombatUtils.attack(getThrower(), e, hand);
                        TaoCasterData.getTaoCap(getThrower()).addQi(0.18f);
                        setDead();
                    }
                } else setDead();
            }
        }
    }

    @Override
    public float zSpin() {
        return zSpin;
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ, 5, 0.3d, 0.3d, 0.3d, 0.1d);
        }
        super.onHit(raytraceResultIn);
    }
}