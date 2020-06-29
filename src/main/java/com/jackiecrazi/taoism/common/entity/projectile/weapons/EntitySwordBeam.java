package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySwordBeam extends EntityThrownWeapon {
    protected EnumHand h;
    protected String s;
    protected float zSpin;

    public EntitySwordBeam(World w) {
        super(w);
    }

    public EntitySwordBeam(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn, hand);
        s=is.getUnlocalizedName();
        h = hand;
    }

    public EntitySwordBeam setRenderRotation(float amnt){
        this.zSpin =amnt;
        return this;
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (shootingEntity == null || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                this.setDead();
                return;
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!world.isRemote) {
            if (getThrower() != null) {
                if (result.entityHit != null) {
                    if (result.entityHit != getThrower()) {
                        if(h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                                this.setDead();
                                return;
                            }
                        Entity e = result.entityHit;
                        TaoCombatUtils.attack(getThrower(), e, hand);
                        setDead();
                    }
                }else setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("spin", zSpin);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        zSpin =compound.getFloat("spin");
    }

    @Override
    public float zSpin() {
        return zSpin;
    }
}