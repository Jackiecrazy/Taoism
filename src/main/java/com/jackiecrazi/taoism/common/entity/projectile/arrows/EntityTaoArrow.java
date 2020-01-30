package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import com.jackiecrazi.taoism.api.alltheinterfaces.IDamageType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTaoArrow extends EntityArrow implements IDamageType {

    public static final int maxCharge = 300;

    public enum WARHEADS{
          NONE,
        INCENDIARY,
        POISONSMOKE,
        FRAGMENTING
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    protected int charge = 0;
    protected int warhead = 0;//0 for nothing, 1 for incendiary, 2 for poison smoke, 3 for fragmenting
    private ItemStack arrow = new ItemStack(Items.ARROW);

    public int getWarhead() {
        return warhead;
    }

    public void setWarhead(int warhead) {
        this.warhead = warhead;
    }

    public EntityTaoArrow(World worldIn) {
        super(worldIn);
        init();
    }

    public EntityTaoArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        init(shooter);
    }

    public EntityTaoArrow(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter);
        init(shooter, is);
    }


    public EntityTaoArrow(World worldIn, ItemStack is) {
        super(worldIn);
        init(is);
    }

    protected void init() {

    }

    protected void init(EntityLivingBase shooter, ItemStack is) {
        init(shooter);
        init(is);
    }

    protected void init(EntityLivingBase shooter) {
        init();
    }

    protected void init(ItemStack is) {
        init();
        setArrow(is);
    }

    public EntityTaoArrow setArrow(ItemStack i) {
        arrow = i.copy();
        return this;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("charge", charge);
        if (arrow != null)
            compound.setTag("taotag", arrow.writeToNBT(new NBTTagCompound()));
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        charge = compound.getInteger("charge");
        arrow = new ItemStack(compound.getCompoundTag("taotag"));
    }

    protected ItemStack getArrowStack() {
        return arrow == null ? new ItemStack(Items.ARROW) : arrow;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    protected Block getInTile() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "inTile", "field_145790_g", "av");
    }

    protected void setInTile(Block b) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, b, "inTile", "field_145790_g", "av");
    }

    protected int getInData() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "inData", "field_70253_h", "aw");
    }

    protected void setInData(int inData) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, inData, "inData", "field_70253_h", "aw");
    }

    protected int getTicksInGround() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "ticksInGround", "field_70252_j", "ax");
    }

    protected void setTicksInGround(int ticksInGround) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, ticksInGround, "ticksInGround", "field_70252_j", "ax");
    }

    protected int getTicksInAir() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "ticksInGround", "field_70257_an", "ay");
    }

    protected void setTicksInAir(int ticksInAir) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, ticksInAir, "ticksInGround", "field_70257_an", "ay");
    }

    protected int getX() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "xTile", "field_145791_d", "h");
    }

    protected void setX(int xTile) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, xTile, "xTile", "field_145791_d", "h");
    }

    protected int getY() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "yTile", "field_145792_e", "at");
    }

    protected void setY(int yTile) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, yTile, "yTile", "field_145792_e", "at");
    }

    protected int getZ() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "zTile", "field_145789_f", "au");
    }

    protected void setZ(int zTile) {
        ReflectionHelper.setPrivateValue(EntityArrow.class, this, zTile, "zTile", "field_145789_f", "au");
    }

    protected int getKnockbackStrenth() {
        return ReflectionHelper.getPrivateValue(EntityArrow.class, this, "knockbackStrength", "field_70256_ap", "aA");
    }

    @Override
    public void extinguish()
    {
        super.extinguish();
        charge=0;
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
        }else if(this.isRiding()){
            updateInEntity(getRidingEntity());
        }
        else {
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

    /**
     * Called by a player entity when they collide with an entity
     */
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
     * @param e the riding entity, passed for simplicity's sake
     */
    protected void updateInEntity(Entity e){
        switch(getWarhead()){
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
                    this.startRiding(entity, true);//TODO cleaner implementation with capability

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
        } else {
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
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;
            this.setIsCritical(false);

            if (iblockstate.getMaterial() != Material.AIR) {
                getInTile().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
            }
        }
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        switch(getWarhead()){
            case 1: //incendiary
                living.setFire(charge/20);
                break;
            case 3: //explosive
                world.createExplosion(this,posX,posY,posZ,(float)charge/100,false);
                this.setDead();
                break;
        }
    }

    @Override
    public int getDamageType(ItemStack is) {
        if (arrow == null || arrow.isEmpty()) return 1;
        return arrow.getItem() instanceof IDamageType ? ((IDamageType) arrow.getItem()).getDamageType(arrow) : 1;
    }
}
