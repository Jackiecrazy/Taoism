package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.effect.FakeExplosion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityChui extends EntityThrownWeapon implements ITetherAnchor {
    private Entity target;
    private int explosion, prevTickExplosion;
    private List<WeakReference<Entity>> hitList = new ArrayList<>();

    public EntityChui(World worldIn) {
        super(worldIn);
        setSize(1.1f, 1.1f);
    }

    public EntityChui(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude, main);
        setSize(1.1f, 1.1f);
    }

    @Override
    public Entity getTetheringEntity() {
        return target;
    }

    @Override
    public Vec3d getTetheredOffset() {
        return Vec3d.ZERO;
    }

    @Override
    public Entity getTetheredEntity() {
        return this;
    }

    @Override
    public double getTetherLength() {
        return 0;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return super.canBeAttackedWithItem();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (target != null) {
            compound.setUniqueId("hit", target.getUniqueID());
            compound.setInteger("targetID", target.getEntityId());
        }
        compound.setInteger("explosions", explosion);
        compound.setInteger("prevExplosions", prevTickExplosion);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (world instanceof WorldServer) {
            target = ((WorldServer) world).getEntityFromUuid(Objects.requireNonNull(compound.getUniqueId("hit")));
        } else target = world.getEntityByID(compound.getInteger("targetID"));
        prevTickExplosion = compound.getInteger("prevExplosions");
        explosion = compound.getInteger("explosions");
    }

    @Override
    public void onUpdate() {
        if (ticksExisted % 10 == 0)
            prevTickExplosion = explosion;
        super.onUpdate();
        //if(!world.isRemote)
        updateTetheringVelocity();
        //System.out.println("at "+getPositionVector());
        if (target != null && target.isDead) {
            target = null;
        }
        if (target instanceof EntityLivingBase && NeedyLittleThings.getDistSqCompensated(this, target) == 0) {
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).setDownTimer(200);
        }
        if (ticksExisted % 10 == 0 && prevTickExplosion != 0 && prevTickExplosion == explosion) {
            explosion = prevTickExplosion = 0;
        }
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        if (target == null && explosion < 2 && NeedyLittleThings.getSpeedSq(this) > 0.5 && rtr.sideHit != EnumFacing.UP) {
            //world.newExplosion(this, posX, posY, posZ, 3, false, true);
            FakeExplosion.explode(world, this, getThrower(), posX, posY, posZ, 4 - explosion);
            explosion++;
            if (explosion > 1) {
                motionX *= 0.5;
                motionY *= 0.5;
                motionZ *= 0.5;
            }
        } else {
            super.onHitBlock(rtr);
        }
        sync();
    }

    @Override
    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0 || world.isRemote || getThrower() == null) return;
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        super.onHitEntity(hit);
        assert is.getTagCompound() != null;
        target = hit;
        if (target instanceof EntityLivingBase) {
            TaoCasterData.getTaoCap((EntityLivingBase) target).startRecordingDamage();
        }
        hit.motionX = motionX;
        hit.motionY = motionY;
        hit.motionZ = motionZ;
        hit.velocityChanged = true;
        TaoCombatUtils.rechargeHand(getThrower(), hand, 1f, true);
        if (getThrower() instanceof EntityPlayer)
            TaoCombatUtils.taoWeaponAttack(hit, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
        sync();
    }

    @Override
    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
        if (target instanceof EntityLivingBase) {
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).setRootTime(0);
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).stopRecordingDamage(getThrower());
        }
        if (stack.getItem() instanceof IChargeableWeapon) {
            ((IChargeableWeapon) stack.getItem()).dischargeWeapon(getThrower(), stack);
        }
    }

    public void onRecall() {
        if (getThrower() != null) {
            if (target != null || world.isAirBlock(getPosition().up()))
                getThrower().setPositionAndUpdate(getX(), getY() + 1, getZ());
            else {
                Vec3d tpTo = NeedyLittleThings.getClosestAirSpot(getThrower().getPositionVector(), getPositionVector(), getThrower());
                if (Double.isNaN(tpTo.x) || Double.isNaN(tpTo.y) || Double.isNaN(tpTo.z) || tpTo.y > 256 || tpTo.y < 0) {
                    onRetrieveWeapon();
                }
                else getThrower().setPositionAndUpdate(tpTo.x, tpTo.y, tpTo.z);
            }
        }
        //onRetrieveWeapon();
    }

    @Override
    protected boolean shouldRetrieve() {
        return false;
    }

    @Override
    public float zSpin() {
        if (hitStatus > 0) return 120;
        if (world instanceof WorldClient) {
            return (Minecraft.getMinecraft().getRenderPartialTicks() + ticksExisted) * 20;
        }
        return ticksExisted * 3;
    }
}
