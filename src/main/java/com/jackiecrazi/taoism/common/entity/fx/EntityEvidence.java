package com.jackiecrazi.taoism.common.entity.fx;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.common.item.weapon.melee.desword.ExecutionerSword;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityEvidence extends Entity {
    private int lifeTicks;
    private EntityLivingBase caster;
    private UUID casterUuid;

    public EntityEvidence(World worldIn, EntityLivingBase sinner) {
        this(worldIn);
        setSinner(sinner);
        double xMod = rand.nextGaussian() * 3, yMod = rand.nextGaussian() * 3, zMod = rand.nextGaussian() * 3;
        BlockPos attemptLocation = new BlockPos(sinner.posX + xMod, sinner.posY + yMod, sinner.posZ + zMod);
        BlockPos origLocation = attemptLocation.up();
        while (!worldIn.isAirBlock(attemptLocation) && attemptLocation.getY() > 0 && sinner.posY - attemptLocation.getY() < 16)
            attemptLocation = attemptLocation.down();
        attemptLocation = origLocation;
        while (!worldIn.isAirBlock(attemptLocation) && attemptLocation.getY() < 256 && attemptLocation.getY() - sinner.posY < 16)
            attemptLocation = attemptLocation.up();
        if (!worldIn.isAirBlock(attemptLocation)) {
            Vec3d ray = NeedyLittleThings.getClosestAirSpot(sinner.getPositionVector(), new Vec3d(attemptLocation), this);
            attemptLocation = new BlockPos(ray);
        }
        this.setPosition(attemptLocation.getX(), attemptLocation.getY(), attemptLocation.getZ());
    }

    public EntityEvidence(World worldIn) {
        super(worldIn);
        lifeTicks = 100;
        setSize(0.5f, 0.5f);
    }

    @Override
    protected void entityInit() {

    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            double d0 = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * (double) this.width * 0.5D;
            double d1 = this.posY + 0.05D + this.rand.nextDouble() * 1.0D;
            double d2 = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * (double) this.width * 0.5D;
            //double d3 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
            double d4 = this.rand.nextDouble() * 0.1D;
            //double d5 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
            this.world.spawnParticle(ticksExisted % 2 == 0 ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0, d4, 0);
        } else if (--this.lifeTicks < 0) {
            this.setDead();
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer p) {
        ItemStack is = TaoCombatUtils.getAttackingItemStackSensitive(p);
        if (is.getItem() instanceof ExecutionerSword && ((ExecutionerSword) is.getItem()).attemptAbsorbSoul(p, is, this)) {
            world.playSound(null, posX, posY, posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.8f+rand.nextFloat()*0.4f, 0.8f+rand.nextFloat()*0.4f);
            setDead();
        }
    }

    @Override
    public void applyEntityCollision(Entity e) {
        super.applyEntityCollision(e);
        if (getSinner() == e){
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.8f+rand.nextFloat()*0.4f, 0.8f+rand.nextFloat()*0.4f);
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.8f+rand.nextFloat()*0.4f, 0.8f+rand.nextFloat()*0.4f);
        setDead();
        return false;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.casterUuid = compound.getUniqueId("OwnerUUID");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.casterUuid != null) {
            compound.setUniqueId("OwnerUUID", this.casterUuid);
        }
    }

    @Nullable
    public EntityLivingBase getSinner() {
        if (this.caster == null && this.casterUuid != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.casterUuid);

            if (entity instanceof EntityLivingBase) {
                this.caster = (EntityLivingBase) entity;
            }
        }

        return this.caster;
    }

    private void setSinner(@Nullable EntityLivingBase elb) {
        this.caster = elb;
        this.casterUuid = elb == null ? null : elb.getUniqueID();
    }
}
