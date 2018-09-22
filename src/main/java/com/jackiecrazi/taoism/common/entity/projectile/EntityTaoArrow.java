package com.jackiecrazi.taoism.common.entity.projectile;

import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.alltheinterfaces.IAmModular;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.TaoArrow;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class EntityTaoArrow extends EntityArrow {
    private ItemStack arrow = new ItemStack(Items.ARROW);


    public EntityTaoArrow(World worldIn) {
        super(worldIn);

    }

    public EntityTaoArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        init(shooter);
    }

    private void init(EntityLivingBase shooter){
        if(arrow.getItem()instanceof IAmModular){
            HashMap<WeaponPerk,Integer> a=((IAmModular)arrow.getItem()).getPerks(arrow);
            for(WeaponPerk wp:a.keySet()){
                wp.arrowConstruct(this,shooter,a.get(wp));
            }
        }
    }

    public EntityTaoArrow setArrow(ItemStack i) {
        arrow = i.copy();
        return this;
    }

    public ItemStack getArrow() {
        return arrow;
    }

    @Override
    protected void onHit(RayTraceResult ray)
    {
        super.onHit(ray);
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        if (!arrow.isEmpty() && arrow.getItem() == TaoItems.arrow) {
            //TODO do something
            ((TaoArrow)arrow.getItem()).onHit();
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (arrow != null)
            compound.setTag("taotag", arrow.writeToNBT(new NBTTagCompound()));
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        arrow = new ItemStack(compound.getCompoundTag("taotag"));
    }

    protected ItemStack getArrowStack() {
        return arrow == null ? new ItemStack(Items.ARROW) : arrow;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    protected void setInTile(Block b){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,b,"inTile","field_145790_g","av");
    }
    protected Block getInTile(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"inTile","field_145790_g","av");
    }

    protected void setInData(int inData){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,inData,"inData","field_70253_h","aw");
    }
    protected int getInData(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"inData","field_70253_h","aw");
    }

    protected void setTicksInGround(int ticksInGround){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,ticksInGround,"ticksInGround","field_70252_j","ax");
    }
    protected int getTicksInGround(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"ticksInGround","field_70252_j","ax");
    }

    protected void setTicksInAir(int ticksInAir){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,ticksInAir,"ticksInGround","field_70257_an","ay");
    }
    protected int getTicksInAir(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"ticksInGround","field_70257_an","ay");
    }

    protected void setX(int xTile){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,xTile,"xTile","field_145791_d","h");
    }

    protected int getX(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this, "xTile","field_145791_d","h");
    }

    protected void setY(int yTile){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,yTile,"yTile","field_145792_e","at");
    }
    protected int getY(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"yTile","field_145792_e","at");
    }

    protected void setZ(int zTile){
        ReflectionHelper.setPrivateValue(EntityArrow.class,this,zTile,"zTile","field_145789_f","au");
    }
    protected int getZ(){
        return ReflectionHelper.getPrivateValue(EntityArrow.class,this,"zTile","field_145789_f","au");
    }

    @Override
    public void onUpdate() {
        // This calls the Entity class' update method directly, circumventing EntityArrow
        super.onEntityUpdate();
        updateAngles();
        checkInGround();
        if (arrowShake > 0) {
            --arrowShake;
        }
        if (inGround) {
            updateInGround();
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

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED && !player.inventory.addItemStackToInventory(this.getArrowStack()))
            {
                flag = false;
            }

            if (flag)
            {
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    /** Returns the damage source this arrow will use against the entity struck */
    protected DamageSource getDamageSource(Entity entity) {
        return new EntityDamageSourceIndirect("arrow", this, shootingEntity).setProjectile();
    }

    /** Returns whether this arrow can target the entity; used for Endermen */
    protected boolean canTargetEntity(Entity entity) {
        return (!(entity instanceof EntityEnderman));
    }

    /**
     * Updates yaw and pitch based on current motion
     */
    protected void updateAngles() {
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, (double) f) * 180.0D / Math.PI);
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
        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float)(Math.atan2(motionY, (double) f) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
        { ; }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        { prevRotationPitch += 360.0F; }

        while (rotationYaw - prevRotationYaw < -180.0F)
        { prevRotationYaw -= 360.0F; }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        { prevRotationYaw += 360.0F; }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;

        float motionFactor=updateWater();

        updateMotion(motionFactor, getGravityVelocity());
        setPosition(posX, posY, posZ);
    }

    protected float updateWater(){
        if (isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f3 = 0.25F;
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * (double) f3, posY - motionY * (double) f3, posZ - motionZ * (double) f3, motionX, motionY, motionZ);
            }

            return 0.8F;
        }
        return 0.99f;
    }

    protected float getGravityVelocity(){
        return 0.05f;
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
        BlockPos pos = new BlockPos(getX(),getY(),getZ());
        IBlockState state = world.getBlockState(pos);

        if (state.getMaterial() != Material.AIR)
        {
            AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(this.world, pos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(pos).contains(new Vec3d(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }
    }

    /**
     * If entity is in ground, updates ticks in ground or adjusts position if block no longer in world
     */
    protected void updateInGround() {
        BlockPos pos = new BlockPos(getX(),getY(),getZ());
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == getInTile() && block.getMetaFromState(state) == getInData()) {
            setTicksInGround(getTicksInGround()+1);
            if (getTicksInGround() == 1200) {
                setDead();
            }
        } else {
            inGround = false;
            motionX *= (double)(rand.nextFloat() * 0.2F);
            motionY *= (double)(rand.nextFloat() * 0.2F);
            motionZ *= (double)(rand.nextFloat() * 0.2F);
            setTicksInGround(0);
            setTicksInAir(0);
        }
    }

    /**
     * Checks for impacts, spawns trailing particles and updates entity position
     */
    protected void updateInAir() {
        {
            this.timeInGround = 0;
            setTicksInAir(getTicksInAir()+1);
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null)
            {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null)
            {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
                {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
            {
                this.onHit(raytraceresult);
            }

            if (this.getIsCritical())
            {
                for (int k = 0; k < 4; ++k)
                {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            updatePosition();
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }
}
