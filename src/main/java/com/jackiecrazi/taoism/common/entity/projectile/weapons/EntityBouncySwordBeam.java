package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBouncySwordBeam extends EntitySwordBeamBase {
    private final int[] hitList = new int[5];
    private int hitTimes = 0;
    private Entity lastHit = null;

    public EntityBouncySwordBeam(World w) {
        super(w);
    }

    public EntityBouncySwordBeam(World worldIn, EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(worldIn, throwerIn, hand, is);
    }

    @Override
    protected void onHitEntity(Entity e) {
        if (!world.isRemote) {
            if (getThrower() != null) {
                if (e != null) {
                    if (e != getThrower() && e != lastHit) {
                        if (h == null || s == null || !getThrower().getHeldItem(h).getUnlocalizedName().equals(s)) {
                            this.setDead();
                            return;
                        }
                        for (int x = 0; x < 5; x++) {
                            if (hitList[x] == 0) {
                                hitList[x] = e.getEntityId();
                                break;
                            }
                        }
                        TaoCombatUtils.attackIndirectly(getThrower(), this, e, hand);
                        lastHit = e;
                        hitTimes++;
                        if (hitTimes > 4) {
                            setDead();
                            return;
                        }else if(hitTimes==1){
                            TaoCasterData.getTaoCap(getThrower()).addQi(0.18f);
                        }
                        ricochet();
                    }
                } else setDead();
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ, 5, 0.3d, 0.3d, 0.3d, 0.1d);
        }
        super.onHit(raytraceResultIn);
    }

    private void ricochet() {
        if (!world.isRemote) {
            if (getThrower() != null) {
                EntityLivingBase target = null;
                double dist = 999;
                EntityLivingBase secondaryTarget = null;
                double secondaryDist = 999;
                for (EntityLivingBase elb : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(8), TaoCombatUtils.VALID_TARGETS)) {
                    if (elb != getThrower() && elb != lastHit) {//clear LoS to target
                        boolean fresh = true;
                        for (int a : hitList)
                            if (elb.getEntityId() == a) {
                                fresh = false;
                            }
                        if (fresh) {
                            if (NeedyLittleThings.getDistSqCompensated(this, elb) < dist) {
                                target = elb;
                                dist = NeedyLittleThings.getDistSqCompensated(this, elb);
                            }
                        } else {
                            if (NeedyLittleThings.getDistSqCompensated(this, elb) < secondaryDist) {
                                secondaryTarget = elb;
                                dist = NeedyLittleThings.getDistSqCompensated(this, elb);
                            }
                        }
                    }
                }
                if (target != null) shootTo(target.getPositionVector().addVector(0, target.getEyeHeight(), 0), 1, 0);
                else if (secondaryTarget != null)
                    shootTo(secondaryTarget.getPositionVector().addVector(0, secondaryTarget.height / 2, 0), 1, 0);
                else {
                    if (lastHit != null && getThrower() != null) {
                        TaoCombatUtils.attackIndirectly(getThrower(), this, lastHit, hand);
                        TaoCasterData.getTaoCap(getThrower()).addQi(0.1f);
                    }
                    setDead();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(RayTraceResult rtr) {
        ricochet();
    }
}
