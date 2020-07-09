package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.melee.rope.RopeDart;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class EntityRopeDart extends EntityThrownWeapon {

    private boolean onRecallTriggered = false;

    public EntityRopeDart(World w) {
        super(w);
    }

    public EntityRopeDart(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude, main);
        if (main != null)
            hand = main;
        //origin = dude.getPositionVector().addVector(0, dude.getEyeHeight() - 0.1, 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
//        if (origin != null) {
//            compound.setDouble("homeX", origin.x);
//            compound.setDouble("homeY", origin.y);
//            compound.setDouble("homeZ", origin.z);
//        }
        compound.setInteger("charge", charge);
        compound.setBoolean("recalling", onRecallTriggered);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        //origin = new Vec3d(compound.getDouble("homeX"), compound.getDouble("homeY"), compound.getDouble("homeZ"));
        charge = compound.getInteger("charge");
        onRecallTriggered = compound.getBoolean("recalling");
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (this.getThrower() == null || this.getThrower().getHeldItem(hand).getItem() != TaoItems.ropedart || this.getDistanceSq(getThrower()) > RopeDart.MAXRANGESQ * 2) {
                this.setDead();
                return;
            }
            if (NeedyLittleThings.getSpeedSq(this) == 0) {
                onRetrieveWeapon();
                return;
            }
            //upon returning to the origin point, if player is not there, turn hurtbox on and go at them again
            Vec3d dudeVec = getThrower().getPositionVector().addVector(0, getThrower().getEyeHeight() - 0.1, 0);
            if (dudeVec.subtract(this.getPositionVector()).lengthSquared() < 1) {
                if (hitStatus > 0) {
                    motionX /= 10;
                    motionY /= 10;
                    motionZ /= 10;
                    updateHitStatus(-2);
                    markVelocityChanged();
                    onRecallTriggered = false;
                }
            } else if (hitStatus == -2) {
                motionX *= 8;
                motionY *= 8;
                motionZ *= 8;
                updateHitStatus(0);
                markVelocityChanged();
            }
            //System.out.println(posX + " " + posY + " " + posZ);
            if (this.getDistanceSq(getThrower()) > RopeDart.MAXRANGESQ) {
                onRecallTriggered = false;
                updateHitStatus(3);
            } else if ((getThrower().getDistanceSq(this) > 4 && hitStatus == -1)) {
                updateHitStatus(0);
            }
            if ((hitStatus > 0 && !onRecallTriggered) || inGround) {
                onRecall();
            }
        }
        super.onUpdate();
    }

    @Override
    protected float getGravityVelocity() {
        return 0;//(float) (1.69-NeedyLittleThings.getSpeedSq(this))/20f;
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        super.onHitBlock(rtr);
        charge = 0;
        inGround = false;
        sync();
    }

    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0 || world.isRemote || getThrower() == null) return;
        if (hit == getThrower() && !getThrower().isSneaking()) return;
        super.onHitEntity(hit);
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        assert is.getTagCompound() != null;
        is.getTagCompound().setBoolean("dartAttack", true);
        TaoCombatUtils.attack(getThrower(), hit, hand);
        is.getTagCompound().setBoolean("dartAttack", false);
        charge++;
        onRecall();
    }

    protected void onRetrieveWeapon() {
        super.onRetrieveWeapon();
    }

    @Override
    protected void onRecall() {
        onRecallTriggered = true;
        super.onRecall();
    }

    @Override
    protected float getReturnVelocity() {
        float retvel = MathHelper.sqrt(NeedyLittleThings.getSpeedSq(this));
        return Math.max(0.5f, retvel);
    }

    @Override
    protected boolean shouldRetrieve() {
        return super.shouldRetrieve() && Objects.requireNonNull(getThrower()).isSneaking();
    }

    @Override
    //you can't ground me!
    protected void checkInGround() {

    }

    @Override
    public boolean hitByEntity(Entity seme) {
        if (getThrower() != null && seme == getThrower()) {
            Vec3d newDir = getThrower().getLookVec();
            //Vec3d newDir = getPositionVector().subtract(getThrower().getPositionVector().addVector(0, getThrower().getEyeHeight() - 0.1, 0)).normalize();
            shoot(newDir, Math.min(5, charge) * 0.1f + 0.5f, 0);//speed increases over 5 hits
            updateHitStatus(0);
            if (TaoCasterData.getTaoCap((EntityLivingBase) seme).isOffhandAttack()) {
                charge++;
            }
            velocityChanged = true;
            onRecallTriggered = false;
            sync();
        }
        return true;
    }
}