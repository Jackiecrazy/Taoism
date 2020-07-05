package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityTaoArrow extends EntityTaoProjectile {
    public EntityTaoArrow(World worldIn) {
        super(worldIn);
    }

    public EntityTaoArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public EntityTaoArrow(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter, is);
    }

    public EntityTaoArrow(World worldIn, ItemStack is) {
        super(worldIn, is);
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        super.onHitBlock(rtr);
        this.arrowShake = 7;
        this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.setIsCritical(false);

    }

    @Override
    protected void onHitEntity(Entity entity) {
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        int i = MathHelper.ceil((double) f * getDamage());

        if (this.getIsCritical()) {
            i += this.rand.nextInt(i / 2 + 2);
        }

        DamageSource damagesource;

        if (this.shootingEntity == null) {
            damagesource = DamageSource.causeArrowDamage(this, this);
        } else {
            damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
        }

        if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, (float) i)) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                this.startRiding(entity, true);//TODO cleaner implementation of arrow embedding with capability

                if (!this.world.isRemote) {
                    entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                }

                if (getKnockbackStrenth() > 0) {
                    float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (f1 > 0.0F) {
                        entitylivingbase.addVelocity(this.motionX * (double) getKnockbackStrenth() * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) getKnockbackStrenth() * 0.6000000238418579D / (double) f1);
                    }
                }

                if (this.shootingEntity instanceof EntityLivingBase) {
                    EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entitylivingbase);
                }

                this.arrowHit(entitylivingbase);

                if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                }
            }

            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                /*if (!(entity instanceof EntityEnderman)) {
                    this.setDead();
                }*/
        } else {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            setTicksInAir(0);

            if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
                if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }

                this.setDead();
            }
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean flag = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED && !player.inventory.addItemStackToInventory(this.getArrowStack())) {
                flag = false;
            }

            if (flag) {
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }
}
