package com.jackiecrazi.taoism.handler;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.*;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class TaoisticEventHandler {

    private static final UUID noArmor = UUID.fromString("603114fc-164b-4d43-874c-3148eebde245");
    public static boolean modCall;
    private static boolean abort = false;
    private static boolean downingHit = false;
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
    private static float damageCache;

    //attacks when out of range
    @SubscribeEvent
    public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e) {
        //System.out.println("hi");
        EntityPlayer p = e.getEntityPlayer();
        ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
        if (!i.isEmpty()) {
            //System.out.println("nonnull");
            if (i.getItem() instanceof IRange) {
                //System.out.println("range!");
                IRange icr = (IRange) i.getItem();

                Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, icr.getReach(p, i));
                if (elb != null) {
                    //System.out.println("sending packet!");
                    Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
                }
            }
            //todo reset cooldown here and sent packet
        }
    }

    @SubscribeEvent
    public static void updateCaps(AttackEntityEvent e) {
        if (TaoWeapon.aoe) {
            EntityPlayer p = e.getEntityPlayer();
            //misc code for updating capability
            int swing = TaoCasterData.getTaoCap(p).getOffhandCool();
            TaoCasterData.getTaoCap(p).setSwing(TaoWeapon.off ? swing : Taoism.getAtk(p));
        }
    }

    //cancels attack if too far, done here instead of AttackEntityEvent because I need to check whether the damage source is melee.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void pleasedontkillme(LivingAttackEvent e) {
        if (e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase p = (EntityLivingBase) e.getSource().getTrueSource();
            //store swing

            //cancel attack
            ItemStack i = p.getHeldItem(TaoWeapon.off ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (i.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) i.getItem();
                if (!icm.canAttack(e.getSource(), p, e.getEntityLiving(), i, e.getAmount()))
                    e.setCanceled(true);
            }
        }
        //rolling inv frames for projectiles and melee damage
        if ((e.getSource().isProjectile() || NeedyLittleThings.isMeleeDamage(e.getSource())) && TaoCasterData.getTaoCap(e.getEntityLiving()).getRollCounter() < CombatConfig.rollThreshold)
            e.setCanceled(true);
    }

    //parry code, TODO make all entities capable of parry
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void parryAndBlock(LivingAttackEvent e) {
        abort = false;
        if (e.getAmount() == 0) {
            abort = true;
            return;
        }
        downingHit = false;
        EntityLivingBase uke = e.getEntityLiving();
        TaoCasterData.updateCasterData(uke);
        if (e.getSource() == null) return;
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() instanceof EntityLivingBase) {
            if (ds.getImmediateSource() != ds.getTrueSource()) return;//only melee attacks can be parried
            EntityLivingBase seme = (EntityLivingBase) ds.getTrueSource();
            if (seme.getLastAttackedEntity() == uke && seme.getLastAttackedEntityTime() == seme.ticksExisted) {
                //attacking the same entity repeatedly in a single tick! Abort! Abort!
                abort = true;
                return;
            }
            ITaoStatCapability semeCap = TaoCasterData.getTaoCap(seme);
            //slime, I despise thee.
            if (!(seme instanceof EntityPlayer)) {
                if (semeCap.getSwing() < CombatConfig.mobForcedCooldown || semeCap.getDownTimer() > 0) {//
                    //take that, slimes, you ain't staggerin' me no more!
                    e.setCanceled(true);
                    return;
                } else
                    semeCap.setSwing(0);
            }
            //if you cannot parry, posture damage will always be applied.
            //suck it, wither.
            boolean smart = uke instanceof IAmVerySmart || uke instanceof EntityPlayer;
            boolean blocking = TaoCombatUtils.isEntityBlocking(uke);
            boolean parrying = TaoCombatUtils.isEntityParrying(uke);
            if (!blocking && !parrying && smart)
                return;
            ItemStack weapon = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
            if (ukeCap.getDownTimer() > 0) return;//downed players are defenseless
            if (weapon.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) weapon.getItem();
                icm.attackStart(ds, seme, uke, weapon, e.getAmount());
            }
            if (!NeedyLittleThings.isMeleeDamage(ds))
                return;//cannot parry/block these

            float postureUse1 = TaoCombatUtils.requiredPostureAtk(uke, seme, weapon, e.getAmount());
            float postureUse2 = TaoCombatUtils.requiredPostureDef(uke, seme, weapon, e.getAmount());
            if (ukeCap.consumePosture(postureUse1 * postureUse2, !parrying, seme, ds) > 0) {
                downingHit = true;
                return;
            }
            /*TODO clean up this crap
            1. check if blocking or parrying
            2. cancel event
            3. proc effects
            4. consume posture
             */
            ItemStack hero = TaoCombatUtils.getParryingItemStack(seme, uke, e.getAmount());
            if (parrying && NeedyLittleThings.isFacingEntity(uke, seme)) {
                e.setCanceled(true);
                //uke.world.playSound(uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 1f, true);
                uke.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1f, 1f);
                //System.out.println("target has parried!");
                //parry code, execute parry special
                semeCap.consumePosture(postureUse1, false);

                if (hero.getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) hero.getItem()).parrySkill(seme, uke, hero);
                }
//                if (uke instanceof EntityPlayer) {
//                    EntityPlayer p = (EntityPlayer) uke;
//                    p.sendStatusMessage(new TextComponentTranslation("you have parried! You have " + ukeCap.getPosture() + " posture left"), true);
//                }
//                if (seme instanceof EntityPlayer) {
//                    EntityPlayer p = (EntityPlayer) seme;
//                    p.sendStatusMessage(new TextComponentTranslation("the target has parried! You have " + semeCap.getPosture() + " posture left"), true);
//                }
                return;
            }
            if (blocking && NeedyLittleThings.isFacingEntity(uke, seme)) {
                e.setCanceled(true);
                //uke.world.playSound(uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.PLAYERS, 1f, 1f, true);
                uke.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1f, 1f);
                //block code, reflect posture damage
                semeCap.consumePosture(postureUse1 * 0.4f, false);
                if (hero.getItem() instanceof IStaminaPostureManipulable)
                    ((IStaminaPostureManipulable) hero.getItem()).onBlock(seme, uke, hero);
//                if (uke instanceof EntityPlayer) {
//                    EntityPlayer p = (EntityPlayer) uke;
//                    p.sendStatusMessage(new TextComponentTranslation("you have blocked! You have " + ukeCap.getPosture() + " posture left"), true);
//                }
//                if (seme instanceof EntityPlayer) {
//                    EntityPlayer p = (EntityPlayer) seme;
//                    p.sendStatusMessage(new TextComponentTranslation("the target has blocked! They have " + ukeCap.getPosture() + " posture left"), true);
//                }
            }
        }
    }

    //critical hit modifier
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void critModifier(CriticalHitEvent e) {
        if (abort) return;
        ItemStack is = TaoWeapon.off ? e.getEntityPlayer().getHeldItemOffhand() : e.getEntityPlayer().getHeldItemMainhand();
        if (!(e.getTarget() instanceof EntityLivingBase)) return;

        if (is.getItem() instanceof ICombatManipulator) {
            ICombatManipulator icm = (ICombatManipulator) is.getItem();
            e.setDamageModifier(icm.critDamage(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), is));
            e.setResult(icm.critCheck(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), is, e.getDamageModifier(), e.isVanillaCritical()));
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
            ItemStack stack = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            if (stack.getItem() instanceof ICombatManipulator) {
                e.setStrength(((ICombatManipulator) stack.getItem()).knockback(seme, uke, stack, e.getOriginalStrength()));
            }
        }
        if (e.getStrength() == 0) e.setCanceled(true);
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
            ItemStack stack = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            if (stack.getItem() instanceof ICombatManipulator) {
                amnt = (((ICombatManipulator) stack.getItem()).hurtStart(ds, seme, uke, stack, amnt));
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
        if (ukeCap.getDownTimer() > 0 && !downingHit) {
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
        if ((ds.isProjectile() || NeedyLittleThings.isMeleeDamage(ds)) && ds.getImmediateSource() != null && !posBreak) {
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
        TaoCasterData.forceUpdateTrackingClients(uke);
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
            ItemStack stack = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
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
                if (elb.getHeldItemMainhand().isEmpty() && Taoism.unirand.nextInt(200) == 0) {
                    Item add = TaoWeapon.listOfWeapons.get(Taoism.unirand.nextInt(TaoWeapon.listOfWeapons.size()));
                    elb.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(add));
                }
            }
        }
    }

    @SubscribeEvent
    public static void ugh(LivingEvent.LivingUpdateEvent e) {
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(e.getEntityLiving());
        boolean mustUpdate = itsc.getRollCounter() < CombatConfig.rollThreshold || itsc.getDownTimer() > 0;
        if (e.getEntityLiving().ticksExisted % CombatConfig.mobUpdateInterval == 0 || mustUpdate) {
            TaoCasterData.updateCasterData(e.getEntityLiving());
            TaoCasterData.forceUpdateTrackingClients(e.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickStuff(TickEvent.PlayerTickEvent e) {
        EntityPlayer p = e.player;
        if (e.phase.equals(TickEvent.Phase.END)) {
            ITaoStatCapability cap = TaoCasterData.getTaoCap(p);
            //update max stamina, posture and ling. The other mobs don't have HUDs, so their spl only need to be recalculated when needed
            //qi 1+ gives slow fall
            if (p.motionY < 0 && cap.getDownTimer() <= 0) {
                p.motionY /= (cap.getQiFloored() + 1);
                p.fallDistance = 0f;
            }

            TaoCasterData.updateCasterData(p);
            //recharge weapon
            //hacky, but well...
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
                        TaoCombatUtils.rechargeHand(p, EnumHand.MAIN_HAND, cd);
                        tw.setCombo(p, mainhand, tw.getCombo(p, mainhand) + 1);
                        TaoWeapon.off = false;
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
                    TaoCombatUtils.rechargeHand(p, EnumHand.OFF_HAND, cd);
                    tw.setCombo(p, offhand, tw.getCombo(p, offhand) + 1);
                    TaoWeapon.off = false;
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
