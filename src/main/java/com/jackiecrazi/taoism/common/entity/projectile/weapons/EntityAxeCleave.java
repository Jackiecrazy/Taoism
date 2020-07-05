package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityAxeCleave extends EntitySwordBeam {
    //first pass: crack particles
    //second pass: explosion particles
    private List<Entity> hitList = new ArrayList<>();
    private boolean detonate = false;

    public EntityAxeCleave(World w) {
        super(w);
        setSize(3, 1);
        isImmuneToFire=true;
    }

    public EntityAxeCleave(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn, hand, is);
        setSize(3, 1);
        isImmuneToFire=true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getThrower() != null && !world.isRemote)
            if (this.getDistanceSq(getThrower()) > 324 || ticksExisted > 100 || h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                if (detonate) {
                    onRetrieveWeapon();
                    return;
                } else onRecall();
            }
        if (world.isRemote) {
            //spawn crack/explosion particles
            for (int h = 0; h < 5; h++) {
                if (detonate) {
                    world.spawnParticle(EnumParticleTypes.LAVA, posX - 1.5 + rand.nextFloat() * 3, posY + rand.nextFloat() * 0.4, posZ + 1.5 - rand.nextFloat() * 3, 0, 0.2, 0);
                } else {
                    int i = MathHelper.floor(this.posX);
                    int j = MathHelper.floor(this.posY - 0.20000000298023224D);
                    int k = MathHelper.floor(this.posZ);
                    IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

                    if (iblockstate.getMaterial() != Material.AIR) {
                        this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, posX - 1.5 + rand.nextFloat() * 3, posY + rand.nextFloat() * 0.4, posZ + 1.5 - rand.nextFloat() * 3, 0, 0.2, 0, Block.getStateId(iblockstate));
                    }
                }
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return onGround ? 0 : 0.05f;
    }


    @Override
    protected void onHitEntity(Entity hit) {
        if (getThrower() != null) {
            if (hit != getThrower()) {
                if (detonate) {
                    if (!hitList.contains(hit)) {
                        hitList.add(hit);
                        TaoCombatUtils.attack(getThrower(), hit, EnumHand.OFF_HAND);
                        if (hit instanceof EntityLivingBase) {
                            TaoCasterData.getTaoCap((EntityLivingBase) hit).setRootTime(0);
                        }
                    }
                } else {
                    if (!hitList.contains(hit)) {
                        hitList.add(hit);
                        TaoCombatUtils.attack(getThrower(), hit, EnumHand.MAIN_HAND);
                        if (hit instanceof EntityLivingBase) {
                            TaoCasterData.getTaoCap((EntityLivingBase) hit).setRootTime(60);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        //compound.setBoolean("detonate", detonate);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        //detonate = compound.getBoolean("detonate");
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == -1) detonate = true;
        else super.handleStatusUpdate(id);
    }

    /**
     * necessary because it's riding on the ground rather than sticking to it
     */
    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        motionY = 0;
    }

    @Override
    protected void onRetrieveWeapon() {
        if (stack.getItem() instanceof IChargeableWeapon) {
            ((IChargeableWeapon) stack.getItem()).dischargeWeapon(getThrower(), stack);
        }
        super.onRetrieveWeapon();
    }

    @Override
    protected void onRecall() {
        if (getThrower() == null || getThrower().getHeldItem(hand).getItem() != stack.getItem()) {
            onRetrieveWeapon();
            return;
        }
        setPositionAndUpdate(getThrower().posX, getThrower().posY, getThrower().posZ);
        hitList.clear();
        detonate = true;
        world.setEntityState(this, (byte) -1);
        inGround = false;
        velocityChanged = true;
        sync();
    }
}
