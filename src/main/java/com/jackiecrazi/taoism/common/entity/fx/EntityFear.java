package com.jackiecrazi.taoism.common.entity.fx;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntityFear extends Entity implements ITetherAnchor {

    private Entity feared;
    private EntityLivingBase wuss;

    public EntityFear(World w, double x, double y, double z, Entity afraidOf, EntityLivingBase bindTo) {
        this(w);
        setPosition(x, y, z);
        feared = afraidOf;
        wuss = bindTo;
    }

    public EntityFear(World worldIn) {
        super(worldIn);
        setSize(0.5f, 0.5f);
    }

    @Override
    public Entity getTetheringEntity() {
        return wuss;
    }

    @Nullable
    @Override
    public Vec3d getTetheredOffset() {
        return Vec3d.ZERO;
    }

    @Nullable
    @Override
    public Entity getTetheredEntity() {
        return this;
    }

    @Override
    public double getTetherLength() {
        return 2;
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!world.isRemote) {
            if (feared != null && wuss != null) {
                double x = posX - feared.posX;
                double z = posZ - feared.posZ;
                int pos = x > 0 ? 1 : -1;
                x = x == 0 ? pos : 40 * wuss.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() / x;
                double y = wuss.onGround ? 0 : 0.02;
                pos = z > 0 ? 1 : -1;
                z = z == 0 ? pos : 40 * wuss.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() / z;
                move(MoverType.SELF, x * 0.05, y * 0.05, z * 0.05);
                markVelocityChanged();
                float rotate = NeedyLittleThings.deg((float) MathHelper.atan2(x, z));
                if (wuss.getActivePotionEffect(TaoPotion.FEAR) == null)
                    setDead();
                rotationYaw = wuss.rotationYaw = -rotate;
            } else setDead();
            updateTetheringVelocity();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (world instanceof WorldServer) {
            feared = ((WorldServer) world).getEntityFromUuid(Objects.requireNonNull(compound.getUniqueId("cthulhu")));
        } else feared = world.getEntityByID(compound.getInteger("yogsothoth"));
        if (world instanceof WorldServer) {
            feared = ((WorldServer) world).getEntityFromUuid(Objects.requireNonNull(compound.getUniqueId("lovecraft")));
        } else feared = world.getEntityByID(compound.getInteger("bruh"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        if (feared != null) {
            compound.setUniqueId("cthulhu", feared.getUniqueID());
            compound.setInteger("yogsothoth", feared.getEntityId());
        }
        if (wuss != null) {
            compound.setUniqueId("lovecraft", wuss.getUniqueID());
            compound.setInteger("bruh", wuss.getEntityId());
        }
    }
}
