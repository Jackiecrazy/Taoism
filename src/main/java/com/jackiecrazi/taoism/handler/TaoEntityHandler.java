package com.jackiecrazi.taoism.handler;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICombo;
import com.jackiecrazi.taoism.api.alltheinterfaces.IElemental;
import com.jackiecrazi.taoism.api.alltheinterfaces.ISpecialSwitchIn;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.block.tile.TileTempExplosion;
import com.jackiecrazi.taoism.common.effect.FakeExplosion;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.entity.ai.AIDowned;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.config.GeneralConfig;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Taoism.MODID)
public class TaoEntityHandler {
    //adds relevant attributes to everyone
    @SubscribeEvent
    public static void itsDangerousToGoAlone(EntityEvent.EntityConstructing e) {
        if (e.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e.getEntity();
            elb.getAttributeMap().registerAttribute(TaoEntities.DEFLECT);
            elb.getAttributeMap().registerAttribute(TaoEntities.ABLATION);
            elb.getAttributeMap().registerAttribute(TaoEntities.QIRATE);
            elb.getAttributeMap().registerAttribute(TaoEntities.LINGREGEN);
            elb.getAttributeMap().registerAttribute(TaoEntities.POSREGEN);
            elb.getAttributeMap().registerAttribute(TaoEntities.HEAL);
            elb.getAttributeMap().registerAttribute(TaoEntities.MAXPOSTURE);
            for (int i = 0; i < IElemental.ATTRIBUTES.length; i++) {
                elb.getAttributeMap().registerAttribute(IElemental.ATTRIBUTES[i]);
            }
        }
    }

    @SubscribeEvent
    public static void resetStat(EntityJoinWorldEvent ev) {
        Entity e = ev.getEntity();
        if (e == null) return;
        if (e instanceof EntityThrowable) {
            EntityThrowable et = (EntityThrowable) e;
            EntityLivingBase elb = et.getThrower();
            if (elb != null && TaoCasterData.getTaoCap(elb).getDownTimer() > 0) {
                ev.setCanceled(true);
            }
        }
        if (e instanceof EntityFireball) {
            EntityFireball et = (EntityFireball) e;
            EntityLivingBase elb = et.shootingEntity;
            if (elb != null && TaoCasterData.getTaoCap(elb).getDownTimer() > 0) {
                ev.setCanceled(true);
            }
        }
        if (e instanceof EntityArrow) {
            EntityArrow et = (EntityArrow) e;
            if (et.shootingEntity instanceof EntityLivingBase && TaoCasterData.getTaoCap((EntityLivingBase) et.shootingEntity).getDownTimer() > 0) {
                ev.setCanceled(true);
            }
        }
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            TaoCasterData.updateCasterData(elb);
            elb.getEntityAttribute(TaoEntities.MAXPOSTURE).setBaseValue(Math.ceil(elb.width) * Math.ceil(elb.height) * 10);
            TaoCasterData.getTaoCap(elb).setPosture(TaoCasterData.getTaoCap(elb).getMaxPosture());
            TaoCasterData.getTaoCap(elb).setForcedLookAt(null);
            if (elb instanceof EntityZombie || elb instanceof EntitySkeleton) {
                if (GeneralConfig.weaponSpawnChance > 0 && elb.getHeldItemMainhand().isEmpty() && Taoism.unirand.nextInt(GeneralConfig.weaponSpawnChance) == 0) {
                    Item add = TaoWeapon.listOfWeapons.get(Taoism.unirand.nextInt(TaoWeapon.listOfWeapons.size()));
                    elb.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(add));
                }
            }
            if (elb instanceof EntityLiving && !((EntityLiving) elb).isAIDisabled()) {
                EntityLiving el = (EntityLiving) e;
                el.tasks.addTask(-1, new AIDowned(el));
                el.targetTasks.addTask(-1, new AIDowned(el));
                el.tasks.addTask(0, new AIDowned(el));
                el.targetTasks.addTask(0, new AIDowned(el));
            }
        }
    }

    @SubscribeEvent
    public static void sike(LivingHealEvent e) {
        e.setAmount((float) (e.getEntityLiving().getEntityAttribute(TaoEntities.HEAL).getAttributeValue() * e.getAmount()));
        if (e.getAmount() <= 0) e.setCanceled(true);
        if (e.getAmount() < 0) {
            e.getEntityLiving().attackEntityFrom(DamageSourceBleed.causeBleedingDamage(), -e.getAmount());
        }
    }

    @SubscribeEvent
    public static void youJumpIJump(LivingEvent.LivingJumpEvent e) {
        EntityLivingBase elb = e.getEntityLiving();
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        //...or not
        if (itsc.getDownTimer() > 0 || itsc.getRootTime() > 0) {
            elb.motionY = 0;
            return;
        }
        float qi = TaoCasterData.getTaoCap(elb).getQi();
        if (itsc.isInCombatMode() && qi > 0) {
            elb.motionY *= 1 + (qi / 15);
            if (qi > 3) {//long jump
                elb.motionX *= 1 + ((qi - 3) / 14);
                elb.motionZ *= 1 + ((qi - 3) / 14);
            }
            elb.velocityChanged = true;
        }
    }

    @SubscribeEvent
    public static void interceptTeleport(EnderTeleportEvent e) {
        if (TaoCasterData.getTaoCap(e.getEntityLiving()).getRootTime() > 0) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void dramatic(LivingFallEvent e) {
        if (TaoCasterData.getTaoCap(e.getEntityLiving()).isRecordingDamage()) {
            TaoCasterData.getTaoCap(e.getEntityLiving()).stopRecordingDamage(e.getEntityLiving().getRevengeTarget());
        }
    }

    /**
     * makes fake explosions restore blocks
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void cannonball(ExplosionEvent.Detonate e) {
        if (e.getWorld().isRemote) return;
        EntityLivingBase detonator = e.getExplosion().getExplosivePlacedBy();
        if (detonator != null && ((e.getExplosion() instanceof FakeExplosion) || (TaoCasterData.getTaoCap(detonator).getCannonballTime() > 0))) {
            Entity proxy = detonator;
            if (e.getExplosion() instanceof FakeExplosion)
                proxy = ((FakeExplosion) e.getExplosion()).getProxy();
            else detonator = detonator.getRevengeTarget();
            e.getAffectedEntities().remove(detonator);
            if (!(proxy instanceof EntityLivingBase) || !TaoCasterData.getTaoCap((EntityLivingBase) proxy).isRecordingDamage()) {
                e.getAffectedEntities().remove(proxy);
            }
            List<BlockPos> hitList = e.getAffectedBlocks();
            for (Iterator<BlockPos> iter = hitList.iterator(); iter.hasNext(); ) {
                BlockPos pos = iter.next();
                if (pos.getY() > 0 && pos.getY() < 256 && e.getWorld().getTileEntity(pos) == null)
                    TileTempExplosion.replaceBlockAndBackup(e.getWorld(), pos, Math.max(20, 200 + Taoism.unirand.nextInt(20) - (int) (5 * proxy.getDistanceSq(pos))));
                iter.remove();
            }
        }
    }

    /**
     * make cannonballs phase through tiles, as tiles aren't handled by fake explosions, so they'll still be in the way afterwards
     */
    @SubscribeEvent
    public static void phaseThroughTiles(GetCollisionBoxesEvent e) {
        if (e.getWorld().isRemote) return;
        if (e.getEntity() instanceof EntityLivingBase && NeedyLittleThings.getSpeedSq(e.getEntity()) > 0.5 && TaoCasterData.getTaoCap((EntityLivingBase) e.getEntity()).getCannonballTime() > 0)
            for (Iterator<AxisAlignedBB> iter = e.getCollisionBoxesList().iterator(); iter.hasNext(); ) {
                BlockPos pos = NeedyLittleThings.posFromAABB(iter.next());
                if (pos.getY() > 0 && e.getWorld().getTileEntity(pos) != null)
                    iter.remove();
            }
    }

    @SubscribeEvent
    public static void ugh(LivingEvent.LivingUpdateEvent e) {
        final EntityLivingBase elb = e.getEntityLiving();
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        if (itsc.getRootTime() > 0) {
            elb.setVelocity(0, 0, 0);
            if (elb.posX != elb.prevPosX || elb.posY != elb.prevPosY || elb.posZ != elb.prevPosZ) {
                elb.setPositionAndUpdate(elb.prevPosX, elb.prevPosY, elb.prevPosZ);
            }
            elb.velocityChanged = true;
        }
        if(elb.world.isRemote)return;
        if (itsc.getCannonballTime() > 0 && (TaoMovementUtils.willCollide(elb))) {
            if (TaoMovementUtils.collisionStatusVelocitySensitive(elb)[3] || elb.onGround || elb.collidedVertically) {
                TaoCasterData.getTaoCap(e.getEntityLiving()).stopRecordingDamage(elb.getRevengeTarget());
            }
            if (NeedyLittleThings.getSpeedSq(elb) > 0.5) {
                //FakeExplosion.explode(elb.world, elb, elb.getRevengeTarget(), elb.posX, elb.posY, elb.posZ, (float) Math.min(5f, NeedyLittleThings.getSpeedSq(elb) * 3 * elb.width * elb.height));
                elb.world.newExplosion(elb, elb.posX, elb.posY, elb.posZ, (float) Math.min(5f, NeedyLittleThings.getSpeedSq(elb) * 3 * elb.width * elb.height), false, true);
                elb.motionX *= 0.7;
                elb.motionY *= 0.7;
                elb.motionZ *= 0.7;
            } else TaoCasterData.getTaoCap(e.getEntityLiving()).stopRecordingDamage(elb.getRevengeTarget());
        }
        boolean mustUpdate = itsc.getRollCounter() < CombatConfig.rollThreshold || itsc.getDownTimer() > 0 || itsc.getPosInvulTime() > 0 || elb.world.getClosestPlayerToEntity(elb, 16) != null;
        if (elb.ticksExisted % CombatConfig.mobUpdateInterval == 0 || mustUpdate) {
            TaoCasterData.updateCasterData(elb);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickStuff(TickEvent.PlayerTickEvent e) {
        EntityPlayer p = e.player;
        ITaoStatCapability cap = TaoCasterData.getTaoCap(p);
        if (e.phase.equals(TickEvent.Phase.START)) {
            if (cap.isInCombatMode() && (cap.getJumpState() == ITaoStatCapability.JUMPSTATE.JUMPING || cap.getJumpState() == ITaoStatCapability.JUMPSTATE.DODGING)) {
                //spawn jump/dodge particles
                double d0 = Taoism.unirand.nextGaussian() * 0.02D;
                double d1 = Taoism.unirand.nextGaussian() * 0.02D;
                double d2 = Taoism.unirand.nextGaussian() * 0.02D;
                p.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, p.posX + (double) (Taoism.unirand.nextFloat() * p.width * 2.0F) - (double) p.width, p.posY + p.width / 2, p.posZ + (double) (Taoism.unirand.nextFloat() * p.width * 2.0F) - (double) p.width, d0, d1, d2);
            }
            if (cap.isInCombatMode() && cap.getDownTimer() <= 0 && cap.getRootTime() <= 0 && cap.getQi() > 0) {
                //fall speed is slowed by a factor from 0.9 to 0.4, depending on qi and movement speed
                if (cap.getQi() > 3) {
                    if (TaoMovementUtils.shouldStick(p)) {
                        cap.setJumpState(ITaoStatCapability.JUMPSTATE.CLINGING);
                        cap.setClingDirections(new ITaoStatCapability.ClingData(TaoMovementUtils.collisionStatus(p)));
                    }
                    TaoCasterData.forceUpdateTrackingClients(p);
                }
            }
        } else {
            //update max stamina, posture and ling. The other mobs don't have HUDs, so their caster data only need to be recalculated when needed
            //qi 1+ gives slow fall
            if (cap.isInCombatMode() && cap.getDownTimer() <= 0 && cap.getRootTime() <= 0 && cap.getQi() > 0) {
                //fall speed is slowed by a factor from 0.9 to 0.4, depending on qi and movement speed
                if (cap.getQi() > 3) {
                    if (TaoMovementUtils.isTouchingWall(p) && cap.getJumpState() == ITaoStatCapability.JUMPSTATE.CLINGING) {
                        //vertical motion enabling, and shut off attempts to run off the wall
                        double speed = p.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2.5;
                        Vec3d vec = p.getLookVec();
                        p.motionY = speed * p.getLookVec().y;
                        boolean[] faces = TaoMovementUtils.collisionStatus(p);
                        //prevent you from walking off
                        if (faces[0]) {//+x
                            vec = new Vec3d(0, vec.y - vec.x, vec.z);
//                            p.posX = p.prevPosX;
//                            p.motionX = 0;
//                            p.motionX = 0;
//                            p.motionZ = speed * p.getLookVec().z * 2;
                        }
                        if (faces[1]) {//-x
                            vec = new Vec3d(0, vec.y + vec.x, vec.z);
                        }
                        if (faces[4]) {//+z
                            vec = new Vec3d(vec.x, vec.y - vec.z, 0);
//                            p.posZ = p.prevPosZ;
//                            p.motionZ = 0;
//                            p.motionZ = 0;
//                            p.motionX = speed * p.getLookVec().x * 2;
                        }
                        if (faces[5]) {//-z
                            vec = new Vec3d(vec.x, vec.y + vec.z, 0);
                        }
                        vec = vec.normalize();
                        p.motionX = vec.x * speed;
                        p.motionY = vec.y * speed;
                        p.motionZ = vec.z * speed;
                    } else if (!p.isSneaking() && p.motionY < 0) {
                        p.fallDistance = 0; //since you're being a floaty boi, I can't let you get cheap crits
                        p.motionY *= 3 / cap.getQi();//standardized!
                    } else p.fallDistance = 0.1f;//for da critz
                }
            }
            TaoCasterData.updateCasterData(p);
            //recharge weapon
            //hacky, but well...
            if (!Taoism.proxy.isBreakingBlock(p)) {
                ItemStack mainhand = p.getHeldItemMainhand();
                ItemStack offhand = p.getHeldItemOffhand();
                if (Taoism.getAtk(p) == 0) {//fresh switch in, do not execute following code
                    if (mainhand.getItem() instanceof ICombo && p.swingingHand != EnumHand.OFF_HAND)
                        cap.setSwitchIn(true);
                }
                //if (p.world.isRemote) return;
                if (Taoism.getAtk(p) == 1) {
                    if (!cap.isSwitchIn()) {
//                    if(cap.getSwing()>1/NeedyLittleThings.getCooldownPeriod(p))
//                    cap.addQi((float) NeedyLittleThings.getAttributeModifierHandSensitive(TaoEntities.QIRATE, p, EnumHand.MAIN_HAND));
                        //System.out.println("update combo main");
                        if (mainhand.getItem() instanceof ICombo && p.swingingHand != EnumHand.OFF_HAND) {
                            ICombo tw = (ICombo) mainhand.getItem();
                            long cache = tw.lastAttackTime(p, mainhand);
                            tw.updateLastAttackTime(p, mainhand);
                            long newcache = tw.lastAttackTime(p, mainhand);
                            if (newcache - cache > CombatConfig.timeBetweenAttacks) {
                                tw.setCombo(p, mainhand, 0);
                            }
                            float cd = tw.newCooldown(p, mainhand);
                            if (cd != 0)
                                TaoCombatUtils.rechargeHand(p, EnumHand.MAIN_HAND, cd);
                            tw.setCombo(p, mainhand, tw.getCombo(p, mainhand) + 1);
                            TaoCasterData.getTaoCap(p).setOffhandAttack(false);
                        }
                    } else {
                        if (mainhand.getItem() instanceof ISpecialSwitchIn && p.swingingHand != EnumHand.OFF_HAND) {
                            ISpecialSwitchIn issi = (ISpecialSwitchIn) mainhand.getItem();
                            issi.onSwitchIn(mainhand, p);
                        }
                        cap.setSwitchIn(false);
                    }
                }
                if (offhand.getItem() instanceof ICombo) {
                    if (cap.getOffhandCool() == 1) {
//                    if(cap.getSwing()>1/NeedyLittleThings.getCooldownPeriodOff(p))
//                    cap.addQi((float) NeedyLittleThings.getAttributeModifierHandSensitive(TaoEntities.QIRATE, p, EnumHand.OFF_HAND));
                        //System.out.println("update combo off");
                        ICombo tw = (ICombo) offhand.getItem();
                        long cache = tw.lastAttackTime(p, offhand);
                        tw.updateLastAttackTime(p, offhand);
                        long newcache = tw.lastAttackTime(p, offhand);
                        if (newcache - cache > CombatConfig.timeBetweenAttacks) {
                            tw.setCombo(p, offhand, 0);
                        }
                        float cd = tw.newCooldown(p, offhand);
                        if (cd != 0)
                            TaoCombatUtils.rechargeHand(p, EnumHand.OFF_HAND, cd);
                        tw.setCombo(p, offhand, tw.getCombo(p, offhand) + 1);
                        TaoCasterData.getTaoCap(p).setOffhandAttack(false);
                    }
                }
                if (cap.getOffHand().getItem() != offhand.getItem()) {
                    //new offhand item!
                    if (offhand.getItem() instanceof ISpecialSwitchIn) {
                        ISpecialSwitchIn issi = (ISpecialSwitchIn) offhand.getItem();
                        issi.onSwitchIn(offhand, p);
                    }
                    cap.setOffHand(offhand);
                }
            }
        }
    }
}
