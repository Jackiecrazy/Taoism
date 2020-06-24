package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherAnchor;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    Entity target;
    List<WeakReference<Entity>> hitList=new ArrayList<>();
    Vec3d offset;
    //on impact, hits for full damage and tethers itself to the mob, copying its velocity.
    //while tethered, disables the entity's movements
    //when recalled, flies straight back to the caster, damaging all entities on its flight path for half melee damage

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
        return target;
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
        if (hitStatus == -2) {
            loop:for(Entity e:world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(5,5,5))){
                if(e!=shootingEntity){
                    for(WeakReference<Entity> wr: hitList){
                        if(wr.get()==e)continue loop;
                    }
                    onHitEntity(e);
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
        if (hitStatus > 0 || world.isRemote) return;
        ItemStack is = getThrower().getHeldItem(hand);
        if (is.getItem() != stack.getItem() || !is.hasTagCompound()) onRetrieveWeapon();
        assert is.getTagCompound() != null;
        if (hitStatus == -2) {//return trip, shock enemies nearby for slow II
            TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
            if (getThrower() instanceof EntityPlayer)
                TaoCombatUtils.taoWeaponAttack(hit, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
            if(hit instanceof EntityLivingBase)
                TaoPotionUtils.attemptAddPot((EntityLivingBase) hit, new PotionEffect(MobEffects.SLOWNESS, 60, 1), false);
            return;
        }
        super.onHitEntity(hit);
        target = hit;
        offset = this.getPositionVector().subtract(target.getPositionVector());
        startRiding(target, true);
        is.getTagCompound().setBoolean("connected", true);
        TaoCombatUtils.rechargeHand(getThrower(), hand, 1f);
        if (getThrower() instanceof EntityPlayer)
            TaoCombatUtils.taoWeaponAttack(hit, (EntityPlayer) getThrower(), is, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
    }

    public void onRecall() {
        if (target != null && getThrower() instanceof EntityPlayer) {
            TaoCombatUtils.taoWeaponAttack(target, (EntityPlayer) getThrower(), stack, hand == EnumHand.MAIN_HAND, true, DamageSource.causePlayerDamage((EntityPlayer) getThrower()).setProjectile());
        }
        updateHitStatus(-2);
        target = getThrower();
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
}
