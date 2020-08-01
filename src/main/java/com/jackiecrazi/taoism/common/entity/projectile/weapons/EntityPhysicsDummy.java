package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.effect.FakeExplosion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class EntityPhysicsDummy extends EntityThrownWeapon implements ITetherAnchor {
    private Entity target;

    public EntityPhysicsDummy(World worldIn) {
        super(worldIn);
        setSize(0.5f, 0.5f);
    }

    public EntityPhysicsDummy(World worldIn, EntityLivingBase dude, EnumHand main, Entity attachTo) {
        super(worldIn, dude, main);
        setSize(0.5f, 0.5f);
        target = attachTo;
        setPosition(dude.posX, dude.posY+dude.height/2, dude.posZ);
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
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (target != null) {
            compound.setUniqueId("hit", target.getUniqueID());
            compound.setInteger("targetID", target.getEntityId());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (world instanceof WorldServer) {
            target = ((WorldServer) world).getEntityFromUuid(Objects.requireNonNull(compound.getUniqueId("hit")));
        } else target = world.getEntityByID(compound.getInteger("targetID"));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateTetheringVelocity();
    }

    @Override
    protected void onHitEntity(Entity hit) {
    }

    @Override
    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
        if (target instanceof EntityLivingBase) {
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).setRootTime(0);
            TaoCasterData.getTaoCap(((EntityLivingBase) target)).stopRecordingDamage(getThrower());
        }
    }

    public void onRecall() {
        //onRetrieveWeapon();
    }

    @Override
    protected boolean shouldRetrieve() {
        return false;
    }

    @Override
    protected void onHit(RayTraceResult rtr) {
        if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK || world.getTileEntity(rtr.getBlockPos()) != null)
            return;
        if (rtr.sideHit != EnumFacing.UP && target != null && NeedyLittleThings.getSpeedSq(this) > 0.5) {
            //world.newExplosion(this, posX, posY, posZ, 3, false, true);
            FakeExplosion.explode(world, this, getThrower(), posX, posY, posZ, (float) Math.min(5f, NeedyLittleThings.getSpeedSq(target) * 3 * target.width * target.height));
            motionX *= 0.7;
            motionY *= 0.7;
            motionZ *= 0.7;
            sync();
        } else {
            if (target instanceof EntityLivingBase) {
                TaoCasterData.getTaoCap((EntityLivingBase) target).stopRecordingDamage(((EntityLivingBase) target).getRevengeTarget());
            }
            setDead();
        }
    }

    /**
     * overridden to prevent it from detecting its target
     */
    @Nullable
    @Override
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity entity = null;
        float atLeastWidth = Math.min(1, width);
        float atLeastHeight = Math.min(1, height);
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(atLeastWidth, atLeastHeight, atLeastWidth), TaoCombatUtils.VALID_TARGETS);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != this.shootingEntity && entity1 != target) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                if (axisalignedbb.intersects(getEntityBoundingBox())) {
                    double d1 = start.squareDistanceTo(entity1.getPositionVector());

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return super.canBeAttackedWithItem();
    }

}
