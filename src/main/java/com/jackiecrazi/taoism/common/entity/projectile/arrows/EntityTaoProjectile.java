package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.alltheinterfaces.IDamageType;
import com.jackiecrazi.taoism.networking.PacketUpdateProjectile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.UUID;

public class EntityTaoProjectile extends EntityArrow implements IDamageType {

    public static final int maxCharge = 300;
    protected int charge = 0;
    protected int warhead = 0;//0 for nothing, 1 for incendiary, 2 for poison smoke, 3 for fragmenting
    private ItemStack arrow = new ItemStack(Items.ARROW);
    private String throwerName="";
    private EntityLivingBase thrower;
    private Field
            inTile = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_145790_g"),
            inData = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_70253_h"),
            ticksInGround = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_70252_j"),
            ticksInAir = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_70257_an"),
            xTile = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_145791_d"),
            yTile = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_145792_e"),
            zTile = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_145789_f"),
            kbStrength = ObfuscationReflectionHelper.findField(EntityArrow.class, "field_70256_ap");

    public EntityTaoProjectile(World worldIn) {
        super(worldIn);
        init();
    }

    public EntityTaoProjectile(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        init(shooter);
    }

    public EntityTaoProjectile(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter);
        init(shooter, is);
    }

    public EntityTaoProjectile(World worldIn, ItemStack is) {
        super(worldIn);
        init(is);
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getWarhead() {
        return warhead;
    }

    public void setWarhead(int warhead) {
        this.warhead = warhead;
    }

    protected void init() {

    }

    protected void init(EntityLivingBase shooter, ItemStack is) {
        init(shooter);
        init(is);
    }

    protected void init(EntityLivingBase shooter) {
        thrower=shooter;
        init();
    }

    protected void init(ItemStack is) {
        init();
        setArrow(is);
    }

    public EntityTaoProjectile setArrow(ItemStack i) {
        arrow = i.copy();
        return this;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("charge", charge);
        if (arrow != null)
            compound.setTag("taotag", arrow.writeToNBT(new NBTTagCompound()));
        if ((this.throwerName == null || this.throwerName.isEmpty()) && this.thrower instanceof EntityPlayer)
        {
            this.throwerName = this.thrower.getName();
        }

        compound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        charge = compound.getInteger("charge");
        arrow = new ItemStack(compound.getCompoundTag("taotag"));
        this.thrower = null;
        this.throwerName = compound.getString("ownerName");

        if (this.throwerName.isEmpty())
        {
            this.throwerName = null;
        }

        this.thrower = this.getThrower();
    }

    protected ItemStack getArrowStack() {
        return arrow == null ? new ItemStack(Items.ARROW) : arrow;
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();//!this.isDead;
    }

    protected Block getInTile() {
        try {
            return (Block) inTile.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return Blocks.AIR;
        }
    }

    protected void setInTile(Block b) {
        try {
            inTile.set(this, b);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getInData() {
        try {
            return inData.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setInData(int inData) {
        try {
            this.inData.setInt(this, inData);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getTicksInGround() {
        try {
            return ticksInGround.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setTicksInGround(int ticksInGround) {
        try {
            this.ticksInGround.setInt(this, ticksInGround);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getTicksInAir() {
        try {
            return ticksInAir.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setTicksInAir(int ticksInAir) {
        try {
            this.ticksInAir.setInt(this, ticksInAir);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getX() {
        try {
            return xTile.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setX(int xTile) {
        try {
            this.xTile.setInt(this, xTile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getY() {
        try {
            return yTile.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setY(int yTile) {
        try {
            this.yTile.setInt(this, yTile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getZ() {
        try {
            return zTile.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void setZ(int zTile) {
        try {
            this.zTile.setInt(this, zTile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected int getKnockbackStrenth() {
        try {
            return kbStrength.getInt(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void extinguish() {
        super.extinguish();
        charge = 0;
    }

    @Nullable
    public EntityLivingBase getThrower()
    {
        if (this.thrower == null && this.throwerName != null && !this.throwerName.isEmpty())
        {
            this.thrower = this.world.getPlayerEntityByName(this.throwerName);

            if (this.thrower == null && this.world instanceof WorldServer)
            {
                try
                {
                    Entity entity = ((WorldServer)this.world).getEntityFromUuid(UUID.fromString(this.throwerName));

                    if (entity instanceof EntityLivingBase)
                    {
                        this.thrower = (EntityLivingBase)entity;
                    }
                }
                catch (Throwable var2)
                {
                    this.thrower = null;
                }
            }
        }

        return this.thrower;
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();
        updateAngles();
        checkInGround();
        if (arrowShake > 0) {
            --arrowShake;
        }
        if (inGround) {
            updateInGround();
        } else if (this.isRiding()) {
            updateInEntity(getRidingEntity());
        } else {
            updateInAir();
        }
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            setTicksInGround(0);
        }
        super.setVelocity(x, y, z);
    }

    protected void sync(){
        Taoism.net.sendToAllAround(new PacketUpdateProjectile(this), new NetworkRegistry.TargetPoint(dimension, posX, posY, posZ, 64));
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {

    }

    /**
     * Returns the damage source this arrow will use against the entity struck
     */
    protected DamageSource getDamageSource(Entity entity) {
        return new EntityDamageSourceIndirect("arrow", this, shootingEntity).setProjectile();
    }

    /**
     * Returns whether this arrow can target the entity; used for Endermen
     */
    protected boolean canTargetEntity(Entity entity) {
        return (!(entity instanceof EntityEnderman));
    }

    /**
     * Updates yaw and pitch based on current motion
     */
    protected void updateAngles() {
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(motionY, (double) f) * 180.0D / Math.PI);
        }
    }

    /**
     * Updates the arrow's position and angles
     */
    protected void updatePosition() {
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float) (Math.atan2(motionY, (double) f) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
            ;
        }

        while (rotationPitch - prevRotationPitch >= 180.0F) {
            prevRotationPitch += 360.0F;
        }

        while (rotationYaw - prevRotationYaw < -180.0F) {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F) {
            prevRotationYaw += 360.0F;
        }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;

        float motionFactor = velocityMultiplier();

        updateMotion(motionFactor, getGravityVelocity());
        setPosition(posX, posY, posZ);
    }

    protected float velocityMultiplier() {
        if (isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f3 = 0.25F;
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * (double) f3, posY - motionY * (double) f3, posZ - motionZ * (double) f3, motionX, motionY, motionZ);
            }
            return 0.8F;
        }
        return 0.99f;
    }

    protected float getGravityVelocity() {
        return charge > 0 ? 0 : 0.05f;
    }

    /**
     * Adjusts arrow's motion: multiplies each by factor, subtracts adjustY from motionY
     */
    protected void updateMotion(float factor, float adjustY) {
        /*EntityLivingBase target = getTarget();
        if (isHomingArrow() && target != null) {
            double d0 = target.posX - this.posX;
            double d1 = target.getEntityBoundingBox().minY + (double)(target.height) - this.posY;
            double d2 = target.posZ - this.posZ;
            setThrowableHeading(d0, d1, d2, getVelocityFactor() * 2.0F, 1.0F);
        } else {*/
        motionX *= (double) factor;
        motionY *= (double) factor;
        motionZ *= (double) factor;
        motionY -= (double) adjustY;
        //}
    }

    /**
     * Checks if entity is colliding with a block and if so, sets inGround to true
     */
    protected void checkInGround() {
        BlockPos pos = new BlockPos(getX(), getY(), getZ());
        IBlockState state = world.getBlockState(pos);

        if (state.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(this.world, pos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(pos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
    }

    /**
     * If entity is in ground, updates ticks in ground or adjusts position if block no longer in world
     */
    protected void updateInGround() {
        BlockPos pos = new BlockPos(getX(), getY(), getZ());
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == getInTile() && block.getMetaFromState(state) == getInData()) {
            setTicksInGround(getTicksInGround() + 1);
            if (getTicksInGround() == 1200) {
                setDead();
            }
        } else {
            inGround = false;
            motionX *= (double) (rand.nextFloat() * 0.2F);
            motionY *= (double) (rand.nextFloat() * 0.2F);
            motionZ *= (double) (rand.nextFloat() * 0.2F);
            setTicksInGround(0);
            setTicksInAir(0);
        }
    }

    /**
     * Checks for impacts, spawns trailing particles and updates entity position
     */
    protected void updateInAir() {
        {
            //setting stuff
            this.timeInGround = 0;
            setTicksInAir(getTicksInAir() + 1);
            if (charge > 0) charge--;

            //raytracing hits
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) raytraceresult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
                    raytraceresult = null;
                }
            }

            if(raytraceresult !=null && raytraceresult.entityHit instanceof EntityTaoProjectile){
                raytraceresult=null;
                //le own check to make sure rapid waves don't hit themselves has arrived
            }

            if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }

            //spawning particles
            if (this.getIsCritical()) {
                for (int k = 0; k < 4; ++k) {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double) k / 4.0D, this.posY + this.motionY * (double) k / 4.0D, this.posZ + this.motionZ * (double) k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            //updating position
            updatePosition();
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    /**
     * does stuff while in the entity, mostly emit smoke or poison, etc.
     *
     * @param e the riding entity, passed for simplicity's sake
     */
    protected void updateInEntity(Entity e) {
        switch (getWarhead()) {
            case 2://poison smoke

                break;
            default:
                break;
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        setKnockbackStrength(charge / (maxCharge / 3));
        Entity entity = raytraceResultIn.entityHit;

        if (entity != null) {
            onHitEntity(entity);
        } else {
            onHitBlock(raytraceResultIn);
        }
    }

    protected void onHitBlock(RayTraceResult raytraceResultIn) {
        BlockPos blockpos = raytraceResultIn.getBlockPos();
        setX(blockpos.getX());
        setY(blockpos.getY());
        setZ(blockpos.getZ());
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        setInTile(iblockstate.getBlock());
        setInData(getInTile().getMetaFromState(iblockstate));
        this.motionX = (double) ((float) (raytraceResultIn.hitVec.x - this.posX));
        this.motionY = (double) ((float) (raytraceResultIn.hitVec.y - this.posY));
        this.motionZ = (double) ((float) (raytraceResultIn.hitVec.z - this.posZ));
        float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
        this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
        this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
        this.inGround = true;

        if (iblockstate.getMaterial() != Material.AIR) {
            getInTile().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
        }
    }

    protected void onHitEntity(Entity hit) {

    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        switch (getWarhead()) {
            case 1: //incendiary
                living.setFire(charge / 20);
                break;
            case 3: //explosive
                world.createExplosion(this, posX, posY, posZ, (float) charge / 100, false);
                this.setDead();
                break;
        }
    }

    @Override
    public int getDamageType(ItemStack is) {
        if (arrow == null || arrow.isEmpty()) return 1;
        return arrow.getItem() instanceof IDamageType ? ((IDamageType) arrow.getItem()).getDamageType(arrow) : 1;
    }

    public enum WARHEADS {
        NONE,
        INCENDIARY,
        POISONSMOKE,
        FRAGMENTING
    }
}
