package com.jackiecrazi.taoism.common.entity.projectile.physics;

import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class EntityPhysicsDummy extends EntityTaoProjectile implements ITetherAnchor {
    protected Entity target;

    public EntityPhysicsDummy(World worldIn) {
        super(worldIn);
        setSize(0.5f, 0.5f);
    }

    public EntityPhysicsDummy(World worldIn, EntityLivingBase dude, Entity attachTo) {
        super(worldIn, dude);
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
    protected void onHitBlock(RayTraceResult raytraceResultIn) {

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
