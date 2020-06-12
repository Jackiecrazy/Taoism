package com.jackiecrazi.taoism.handler;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.*;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.entity.ai.AIDowned;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.config.GeneralConfig;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class TaoisticEventHandler {

    private static final UUID noArmor = UUID.fromString("603114fc-164b-4d43-874c-3148eebde245");
    public static boolean modCall;
    private static boolean abort = false;
    //	@SubscribeEvent
//	public static void pleasekillmeoff(PlayerInteractEvent.RightClickItem e) {
//		//System.out.println("hi");
//		EntityPlayer p = e.getEntityPlayer();
//		if (e.getItemStack().equals(e.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND))) return;
//		ItemStack i = p.getHeldItem(EnumHand.OFF_HAND);
//		if (!i.isEmpty()) {
//			//System.out.println("nonnull");
//			if (i.getItem() instanceof ICustomRange) {
//				//System.out.println("range!");
//				ICustomRange icr = (ICustomRange) i.getItem();
//
//				EntityLivingBase elb = NeedyLittleThings.raytraceEntities(p.world, p, icr.getReach(p, i));
//				if (elb != null) {
//					//System.out.println("sending packet!");
//					Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
//				}
//			}
//		}
//	} //stop stealing other people's jobs!

    //attacks when out of range
    @SubscribeEvent
    public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e) {
        //System.out.println("hi");
        EntityPlayer p = e.getEntityPlayer();
        ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
        if (i.getItem() instanceof IRange) {
            //System.out.println("range!");
            IRange icr = (IRange) i.getItem();

            Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, icr.getReach(p, i));
            if (elb != null) {
                //System.out.println("sending packet!");
                Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
            }
        }
    }

    @SubscribeEvent
    public static void updateCaps(AttackEntityEvent e) {
        /*if (TaoWeapon.aoe) {
            EntityPlayer p = e.getEntityPlayer();
            //misc code for updating capability
            int swing = TaoCasterData.getTaoCap(p).getOffhandCool();
            TaoCasterData.getTaoCap(p).setSwing(TaoWeapon.off ? swing : Taoism.getAtk(p));
        }*/
        //moved to TaoWeapon#onEntitySwing, kept here for posterity
    }

    //cancels attack if too far, done here instead of AttackEntityEvent because I need to check whether the damage source is melee.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void pleasedontkillme(LivingAttackEvent e) {
        if (e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase p = (EntityLivingBase) e.getSource().getTrueSource();
            //cancel attack
            ItemStack i = TaoCombatUtils.getAttackingItemStackSensitive(p);
            if (i.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) i.getItem();
                if (!icm.canAttack(e.getSource(), p, e.getEntityLiving(), i, e.getAmount()))
                    e.setCanceled(true);
            }
        }
        //rolling inv frames for projectiles and melee damage
        if (NeedyLittleThings.isPhysicalDamage(e.getSource()) && TaoCasterData.getTaoCap(e.getEntityLiving()).getRollCounter() < CombatConfig.rollThreshold)
            e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void projectileParry(ProjectileImpactEvent e) {
        Entity ent = e.getEntity();
        if (EntityList.getKey(ent).getResourceDomain().equals(Taoism.MODID)) return;//derp derp derp
        if (e.getRayTraceResult().entityHit instanceof EntityLivingBase) {
            EntityLivingBase uke = (EntityLivingBase) e.getRayTraceResult().entityHit;
            if (TaoCasterData.getTaoCap(uke).getRollCounter() < CombatConfig.rollThreshold)
                e.setCanceled(true);
            if (NeedyLittleThings.isFacingEntity(uke, ent, 120) && (uke.getHeldItemMainhand().getItem() instanceof TaoWeapon || uke.getHeldItemOffhand().getItem() instanceof TaoWeapon)) {
                if(TaoCasterData.getTaoCap(uke).getQi()*TaoCasterData.getTaoCap(uke).getQi()>NeedyLittleThings.getSpeedSq(ent)&&TaoCasterData.getTaoCap(uke).consumePosture(CombatConfig.posturePerProjectile,false)==0) {
                    ent.motionX = ent.motionY = ent.motionZ = 0;
                    ent.velocityChanged = true;
                    e.setCanceled(true);//seriously, who thought loading rooftops with a ton of archers was a good idea?
                }
            }
        }
    }

    //parry code, TODO make all entities capable of parry
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void parry(LivingAttackEvent e) {
        abort = false;
        if (e.getAmount() == 0 && !e.getEntityLiving().world.isRemote) {
            abort = true;
            return;
        }
        EntityLivingBase uke = e.getEntityLiving();
        TaoCasterData.updateCasterData(uke);
        if (e.getSource() == null) return;
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() instanceof EntityLivingBase) {
            if (ds.getImmediateSource() != ds.getTrueSource() || !NeedyLittleThings.isMeleeDamage(ds))//
                return;//only physical attacks can be parried
            EntityLivingBase seme = (EntityLivingBase) ds.getTrueSource();
            TaoCasterData.updateCasterData(seme);
            if (seme.getLastAttackedEntity() == uke && seme.getLastAttackedEntityTime() == seme.ticksExisted) {
                //attacking the same entity repeatedly in a single tick! Abort! Abort!
                abort = true;
                return;
            }
            ITaoStatCapability semeCap = TaoCasterData.getTaoCap(seme);
            ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
            if (semeCap.getBindTime() > 0) return;//bound entities cannot attack
            if (ukeCap.getDownTimer() > 0 || semeCap.getDownTimer() > 0) return;//downed things are defenseless
            //slime, I despise thee.
            if (!(seme instanceof EntityPlayer)) {
                if (semeCap.getSwing() < CombatConfig.mobForcedCooldown) {//nein nein nein nein nein nein nein
                    //take that, slimes, you ain't staggerin' me no more!
                    e.setCanceled(true);
                    return;
                } else
                    semeCap.setSwing(0);
            }
            ItemStack attack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (attack.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) attack.getItem();
                icm.attackStart(ds, seme, uke, attack, e.getAmount());
            }
            //if(seme.world.isRemote)return;
            /*
            idle parry
            knockback distributed between both units depending on their max posture
            you still take posture damage for being attacked, to incentivize attacking, as per the usual block formula
            knockback taken is equal to base damage divided by defense posture multiplier divided by max posture
            so big posture=less knockback=standing tank
            while this sounds bad for low pos modifier weapons, it means they can bounce around. Hyper-mobile combat!
             */
            ItemStack defend = TaoCombatUtils.getParryingItemStack(seme, uke, e.getAmount());
            float atk = TaoCombatUtils.postureAtk(uke, seme, attack, e.getAmount());
            float def = TaoCombatUtils.postureDef(uke, seme, defend, e.getAmount());
            //some entities just can't parry.
            //suck it, wither.
            boolean smart = uke instanceof EntityPlayer || uke instanceof IAmVerySmart;
            if ((!smart || !defend.isEmpty()) && NeedyLittleThings.isFacingEntity(uke, seme, 90) && (ukeCap.consumePosture(atk * def, true, seme) == 0f) && smart) {
                e.setCanceled(true);
                uke.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.25f + Taoism.unirand.nextFloat() * 0.5f, 0.75f + Taoism.unirand.nextFloat() * 0.5f);
//                    uke.world.playSound(uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 1f, true);
//                    uke.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1f, 1f);
                //parry, both parties are knocked back slightly
                float atkDef = TaoCombatUtils.postureDef(seme, uke, attack, e.getAmount());
                NeedyLittleThings.knockBack(seme, uke, Math.min(2, e.getAmount() * atkDef) / semeCap.getMaxPosture());
                NeedyLittleThings.knockBack(uke, seme, Math.min(2, e.getAmount() * def) / ukeCap.getMaxPosture());
                if (defend.getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) defend.getItem()).parrySkill(seme, uke, defend);
                }
                //reset cooldown
                TaoCombatUtils.rechargeHand(uke, uke.getHeldItemOffhand() == defend ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f);
            }
        }
        TaoCasterData.forceUpdateTrackingClients(uke);
    }

    //critical hit modifier
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void critModifier(CriticalHitEvent e) {
        if (abort) return;
        ItemStack is = TaoCombatUtils.getAttackingItemStackSensitive(e.getEntityLiving());
        if (!(e.getTarget() instanceof EntityLivingBase)) return;

        if (is.getItem() instanceof ICombatManipulator) {
            ICombatManipulator icm = (ICombatManipulator) is.getItem();
            e.setResult(icm.critCheck(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), is, e.getDamageModifier(), e.isVanillaCritical()));
            if (e.getResult() == Event.Result.ALLOW || (e.getResult() == Event.Result.DEFAULT && e.isVanillaCritical()))
                e.setDamageModifier(icm.critDamage(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), is));
        }
    }

    //critical hit check
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void vibeCheck(CriticalHitEvent e) {
        //critted = e.getDamageModifier() > 1f && (e.getResult() == Event.Result.ALLOW || (e.isVanillaCritical() && e.getResult() == Event.Result.DEFAULT));
    }

    //by config option, will also replace the idiotic chance to resist knock with ratio resist. Somewhat intrusive.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void knockKnockWhosThere(LivingKnockBackEvent e) {
        if (!modCall && CombatConfig.modifyKnockBackCode) {
            e.setCanceled(true);
            NeedyLittleThings.knockBack(e.getEntityLiving(), e.getAttacker(), e.getOriginalStrength(), e.getOriginalRatioX(), e.getOriginalRatioZ());
            return;
        }

        if (e.getOriginalAttacker() instanceof EntityLivingBase) {
            EntityLivingBase seme = (EntityLivingBase) e.getOriginalAttacker();
            EntityLivingBase uke = e.getEntityLiving();
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator && TaoCasterData.getTaoCap(uke).getDownTimer() == 0) {//down timer check needed to prevent loops
                e.setStrength(((ICombatManipulator) stack.getItem()).knockback(seme, uke, stack, e.getOriginalStrength()));
            }
        }
        //if (e.getStrength() == 0) e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void resetGeorge(LivingKnockBackEvent e) {//what? George?
        modCall = false;
    }

    //absorbs, pierces and deflects are calculated before armor
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void biteMe(LivingHurtEvent e) {
        if (abort) return;
        EntityLivingBase uke = e.getEntityLiving();
        DamageSource ds = e.getSource();
        float amnt = e.getAmount();

        //initial damage modifiers
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator) {
                amnt = (((ICombatManipulator) stack.getItem()).hurtStart(ds, seme, uke, stack, amnt * (((ICombatManipulator) stack.getItem()).damageMultiplier(seme, uke, stack))));
            }
            if (seme instanceof EntityLiving)
                if (CombatConfig.taoWeaponHitEntity) {
                    if (stack.getItem() instanceof TaoWeapon) {
                        stack.getItem().hitEntity(stack, uke, seme);
                    } else if (CombatConfig.weaponHitEntity) {
                        stack.getItem().hitEntity(stack, uke, seme);
                    }
                }
        }

        ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
        boolean posBreak = false;
        //if posture is broken, damage increased, ignores deflection/absorption and resets posture
        if (ukeCap.getDownTimer() > 0) {
            amnt*=1.5;
            if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
                for (int i = 0; i < 5; ++i) {
                    double d0 = Taoism.unirand.nextGaussian() * 0.02D;
                    double d1 = Taoism.unirand.nextGaussian() * 0.02D;
                    double d2 = Taoism.unirand.nextGaussian() * 0.02D;
                    seme.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, uke.posX + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, uke.posY + 1.0D + (double) (Taoism.unirand.nextFloat() * uke.height), uke.posZ + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, d0, d1, d2);
                }
            }
            //System.out.println("FATALITY!");
            posBreak = true;
        }
        if (ds.isUnblockable()) return;//to prevent loops
        //deflect projectile damage
        if (NeedyLittleThings.isPhysicalDamage(ds) && ds.getImmediateSource() != null && !posBreak) {
            double dp = ds.getImmediateSource().getLookVec().dotProduct(uke.getLookVec());
            double dist = ds.getImmediateSource().getLookVec().lengthVector() * uke.getLookVec().lengthVector();
            double cos = dp / dist;
            amnt *= (1 - cos * uke.getEntityAttribute(TaoEntities.DEFLECT).getAttributeValue());
            //uke.world.playSound(uke.posX,uke.posY,uke.posZ,);
            //uke.world.playEvent(1031, new BlockPos(uke.posX,uke.posY,uke.posZ), 0);
        }
        //absorb
        if (!posBreak)
            amnt -= uke.getEntityAttribute(TaoEntities.ABLATION).getAttributeValue();
        if (amnt <= e.getAmount() / 5) {
            //minimum damage
            amnt = e.getAmount() / 5;
        }

        e.setAmount(amnt);
    }

    //modifies damage after armor, since some items naturally ignore some armor.
    //♂ boy ♂ next ♂ door ♂
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void spite(LivingHurtEvent e) {
        EntityLivingBase uke = e.getEntityLiving();
        if (abort) return;
        /*
        properties of downed targets:
        armor is reduced by 9 when calculating damage
         */
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            int ignoreAmnt = 0;
            if (TaoCasterData.getTaoCap(uke).getDownTimer() > 0) {
                //oof argh ouch my lack of armor
                ignoreAmnt += 9;
                //at high levels of armor this roughly doubles damage taken
            }
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator) {
                ignoreAmnt += ((ICombatManipulator) stack.getItem()).armorIgnoreAmount(e.getSource(), seme, uke, stack, e.getAmount());
            }
            uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(noArmor);//just in case...
            uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier(noArmor, "ouches", -ignoreAmnt, 0));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void wail(LivingDamageEvent e) {

    }

    //resets posture regen if dealt damage, and gives the attacker 1 chi
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hahaNope(LivingDamageEvent e) {
        if (abort) return;
        EntityLivingBase uke = e.getEntityLiving();
        uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(noArmor);
        if (!e.isCanceled() && TaoCasterData.getTaoCap(uke).getDownTimer() <= 0) {//do not reset when a person's downed, otherwise it gets out of hand fast
            //do not reset for fire, poison and arrows
            if (!NeedyLittleThings.isMeleeDamage(e.getSource())) return;
            TaoCasterData.getTaoCap(uke).setPostureRechargeCD(CombatConfig.postureCD);
        }
        TaoCasterData.forceUpdateTrackingClients(uke);
    }

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
            for (int i = 0; i < IElemental.ATTRIBUTES.length; i++) {
                elb.getAttributeMap().registerAttribute(IElemental.ATTRIBUTES[i]);
            }
        }
    }

    @SubscribeEvent
    public static void resetStat(EntityJoinWorldEvent ev) {
        Entity e = ev.getEntity();
        if (e == null) return;
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            TaoCasterData.updateCasterData(elb);
            TaoCasterData.getTaoCap(elb).setPosture(TaoCasterData.getTaoCap(elb).getMaxPosture());
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
            }
        }
    }

    @SubscribeEvent
    public static void youJumpIJump(LivingEvent.LivingJumpEvent e) {
        EntityLivingBase elb = e.getEntityLiving();
        float qi = TaoCasterData.getTaoCap(elb).getQi();
        if (qi > 0) {
            elb.motionY *= 1 + (qi / 10);
            if (qi > 3 && elb.isSprinting()) {//long jump
                elb.motionX *= 1 + ((qi - 3) / 14);
                elb.motionZ *= 1 + ((qi - 3) / 14);
            }
            elb.velocityChanged = true;
        }
    }

    @SubscribeEvent
    public static void ugh(LivingEvent.LivingUpdateEvent e) {
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(e.getEntityLiving());
        boolean mustUpdate = itsc.getRollCounter() < CombatConfig.rollThreshold || itsc.getDownTimer() > 0;
        if (e.getEntityLiving().ticksExisted % CombatConfig.mobUpdateInterval == 0 || mustUpdate) {
            TaoCasterData.updateCasterData(e.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickStuff(TickEvent.PlayerTickEvent e) {
        EntityPlayer p = e.player;
        ITaoStatCapability cap = TaoCasterData.getTaoCap(p);
        if (e.phase.equals(TickEvent.Phase.START)) {
            if (cap.getJumpState() == ITaoStatCapability.JUMPSTATE.JUMPING || cap.getJumpState() == ITaoStatCapability.JUMPSTATE.DODGING) {
                //spawn jump/dodge particles
                double d0 = Taoism.unirand.nextGaussian() * 0.02D;
                double d1 = Taoism.unirand.nextGaussian() * 0.02D;
                double d2 = Taoism.unirand.nextGaussian() * 0.02D;
                p.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, p.posX + (double) (Taoism.unirand.nextFloat() * p.width * 2.0F) - (double) p.width, p.posY + p.width / 2, p.posZ + (double) (Taoism.unirand.nextFloat() * p.width * 2.0F) - (double) p.width, d0, d1, d2);
            }
            if (cap.getDownTimer() <= 0 && cap.getQi() > 0 && !p.isSneaking()) {
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
            if (cap.getDownTimer() <= 0 && cap.getQi() > 0) {
                //fall speed is slowed by a factor from 0.9 to 0.4, depending on qi and movement speed
                if (cap.getQi() > 3 && !p.isSneaking()) {
                    if (TaoMovementUtils.isTouchingWall(p) && cap.getJumpState() == ITaoStatCapability.JUMPSTATE.CLINGING) {//TODO only when sprinting?
                        //vertical motion enabling, and shut off attempts to run off the wall
                        double speed = p.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2;
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
                    } else if (p.motionY < 0)
                        p.motionY *= ((MathHelper.clamp(2 - (p.motionX * p.motionX + p.motionZ * p.motionZ), 1f, 2f) * 1.5f / cap.getQi()));//

                }
                //
                p.fallDistance = 0.1f;//for da critz
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
