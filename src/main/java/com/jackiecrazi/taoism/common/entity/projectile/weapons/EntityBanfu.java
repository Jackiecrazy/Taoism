package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityBanfu extends EntityThrownWeapon implements ITetherAnchor {
    private Entity target;
    private List<WeakReference<Entity>> hitList = new ArrayList<>();
    private Vec3d offset;

    public EntityBanfu(World worldIn) {
        super(worldIn);
        setSize(1.1f, 1.1f);
    }

    public EntityBanfu(World worldIn, EntityLivingBase dude, EnumHand main) {
        super(worldIn, dude, main);
        setSize(1.1f, 1.1f);
    }

    @Override
    public Entity getTetheringEntity() {
        return this;
    }

    @Override
    public Vec3d getTetheredOffset() {
        return inGround || getTetheredEntity() != null ? offset : null;
    }

    @Override
    public Entity getTetheredEntity() {
        return hitStatus == 5 ? getThrower() : target;
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
        if (offset != null) {
            compound.setDouble("offsetx", offset.x);
            compound.setDouble("offsety", offset.y);
            compound.setDouble("offsetz", offset.z);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (world instanceof WorldServer) {
            target = ((WorldServer) world).getEntityFromUuid(Objects.requireNonNull(compound.getUniqueId("hit")));
        } else target = world.getEntityByID(compound.getInteger("targetID"));
        offset = new Vec3d(compound.getDouble(("offsetx")), compound.getDouble(("offsety")), compound.getDouble(("offsetz")));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        //if(!world.isRemote)
        //updateTetheringVelocity();
        //System.out.println("at "+getPositionVector());
        if (target != null && target.isDead) {
            target = null;
            updateHitStatus(0);
        }
        if (getTetheredEntity() instanceof EntityLivingBase) {
            //disable movement. Rework bind time to apply attribute modifier and forcibly reset velocity like downed.
            TaoPotionUtils.attemptAddPot((EntityLivingBase) getTetheredEntity(), new PotionEffect(TaoPotion.ARMORBREAK, 10, 0), true);
            TaoPotionUtils.attemptAddPot((EntityLivingBase) getTetheredEntity(), new PotionEffect(MobEffects.SLOWNESS, 10, 2), false);
        }
        //return trip ranged shock
        if (hitStatus <= 0) {
            loop:
            for (Entity e : world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(5, 5, 5))) {
                if (e != shootingEntity) {
                    for (WeakReference<Entity> wr : hitList) {
                        if (wr.get() == e) continue loop;
                    }
                    hitList.add(new WeakReference<>(e));
                }
            }
        }
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        super.onHitBlock(rtr);
        offset = this.getPositionVector();
    }

    @Override
    protected void onHitEntity(Entity hit) {
        if (hitStatus > 0 || world.isRemote || getThrower() == null) return;
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        if (hitStatus == 5) {//return trip, do nothing until it gets back into your hand
            return;
        }
        super.onHitEntity(hit);
        assert is.getTagCompound() != null;
        target = hit;
        offset = this.getPositionVector().subtract(target.getPositionVector());
        startRiding(target, true);
        is.getTagCompound().setBoolean("connected", true);
        TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
        if (getThrower() instanceof EntityPlayer)
            TaoCombatUtils.taoWeaponAttack(hit, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
    }

    @Override
    protected void onRetrieveWeapon() {
        shooketh();
        super.onRetrieveWeapon();
    }

    public void onRecall() {
        if (target != null && getThrower() instanceof EntityPlayer) {
            TaoCombatUtils.taoWeaponAttack(target, (EntityPlayer) getThrower(), stack, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
        }
        updateHitStatus(5);
        super.onRecall();
    }

    @Override
    public float zSpin() {
        if (hitStatus > 0) return 120;
        if (world instanceof WorldClient) {
            return (Minecraft.getMinecraft().getRenderPartialTicks() + ticksExisted) * 20;
        }
        return ticksExisted * 3;
    }

    private void shooketh() {
        if (getThrower() == null) return;
        for (WeakReference<Entity> wr : hitList) {
            Entity e = wr.get();
            if (e != null && getThrower() instanceof EntityPlayer) {
                TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
                TaoCombatUtils.taoWeaponAttack(target, (EntityPlayer) getThrower(), stack, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
                if (e instanceof EntityLivingBase)
                    TaoPotionUtils.attemptAddPot((EntityLivingBase) e, new PotionEffect(MobEffects.SLOWNESS, 60, 1), false);
            }
        }
        double extra = 1 + (target instanceof EntityLivingBase ? (((EntityLivingBase) target).getMaxHealth() - ((EntityLivingBase) target).getHealth()) / ((EntityLivingBase) target).getMaxHealth() : 0);
        target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(getThrower()), (float) (2 * extra * getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
    }
}
