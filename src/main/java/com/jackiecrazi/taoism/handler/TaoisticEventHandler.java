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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TaoisticEventHandler {

    public static boolean modCall;
    private static boolean critted = false;
    private static boolean posBreak = false;
    private static boolean down = false;
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
    private ITaoStatCapability taoCap;

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

                EntityLivingBase elb = NeedyLittleThings.raytraceEntity(p.world, p, icr.getReach(p, i));
                if (elb != null) {
                    //System.out.println("sending packet!");
                    Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
                }
            }
        }
    }

    //cancels attack if too far
    @SubscribeEvent
    public static void pleasedontkillme(AttackEntityEvent e) {
        EntityPlayer p = e.getEntityPlayer();
        //store swing

        //cancel attack
        ItemStack i = p.getHeldItem(TaoWeapon.off ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (i.getItem() instanceof IRange) {
            IRange icr = (IRange) i.getItem();
            if (icr.getReach(p, i) * icr.getReach(p, i) < NeedyLittleThings.getDistSqCompensated(p, e.getTarget()))
                e.setCanceled(true);
        }

        if (TaoWeapon.aoe)
            //misc code for updating capability
            try {
                int swing = TaoCasterData.getTaoCap(p).getOffhandCool();
                p.getCapability(TaoCasterData.CAP, null).setSwing(TaoWeapon.off ? swing : Taoism.atk.getInt(p));
            } catch (Exception ignored) {
                //gotta catch them all!
            }
    }

    private static boolean isSpecialDamage(DamageSource ds) {
        return ds.isFireDamage() || ds.isMagicDamage() || ds.isUnblockable() || ds.isExplosion() || ds.isDamageAbsolute();
    }

    //parry code, TODO make all entities capable of parry
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void parryAndBlock(LivingAttackEvent e) {
        down = false;
        EntityLivingBase uke = e.getEntityLiving();
        TaoCasterData.syncCasterData(uke);
        if (e.getSource() == null) return;
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() instanceof EntityLivingBase) {
            if (ds.getImmediateSource() != ds.getTrueSource()) return;//only melee attacks can be parried
            EntityLivingBase seme = (EntityLivingBase) e.getSource().getTrueSource();
            //if you cannot parry, posture damage will always be applied.
            //suck it, wither.
            boolean smart = uke instanceof IAmVerySmart || uke instanceof EntityPlayer;
            if (!TaoCasterData.isEntityBlocking(uke, seme) && !TaoCasterData.isEntityParrying(uke, seme) && smart)
                return;
            ItemStack weapon = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
            ITaoStatCapability semeCap = TaoCasterData.getTaoCap(seme);
            if (ukeCap.getDownTimer() != 0) return;//downed players are defenseless
            if (weapon.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) weapon.getItem();
                icm.attackStart(ds, seme, uke, weapon, e.getAmount());
            }
            if (ds.isProjectile() || isSpecialDamage(ds))
                return;//cannot parry/block these

            float postureUse1 = TaoCasterData.requiredPostureAtk(uke, seme, weapon, e.getAmount());
            float postureUse2 = TaoCasterData.requiredPostureDef(uke, seme, weapon, e.getAmount());
            if (!ukeCap.consumePosture(postureUse1 * postureUse2, !TaoCasterData.isEntityParrying(uke, seme))) {
                //sudden stagger prevention is handled by consumePosture, so this only happens if ssp fails.
                //dismount, knock away, and set the target downed for damage*10, between 10 and 60
                uke.dismountRidingEntity();
                NeedyLittleThings.knockBack(uke, seme, e.getAmount() * 0.2F);
                int downtimer = MathHelper.clamp((int) e.getAmount() * 10, 10, 60);
                ukeCap.setDownTimer(downtimer);
                uke.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, downtimer, 100));//TODO disable movement without pots
                if (uke instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) uke;
                    p.sendStatusMessage(new TextComponentTranslation("you have been staggered for " + downtimer / 10f + " seconds!"), true);
                }
                if (seme instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) seme;
                    p.sendStatusMessage(new TextComponentTranslation("the target has been staggered for " + downtimer / 10f + " seconds!"), true);
                }
                System.out.println("target is downed for " + e.getAmount() * 10 + " ticks!");
                down = true;
                return;
            }
            if (TaoCasterData.isEntityParrying(uke, (EntityLivingBase) e.getSource().getTrueSource()) && NeedyLittleThings.isFacingEntity(seme, uke)) {
                e.setCanceled(true);
                uke.world.playSound(uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 1f, true);
                System.out.println("target has parried!");
                //parry code, execute parry special
                TaoCasterData.getTaoCap(seme).consumePosture(postureUse1, false);

                ItemStack hero = TaoCasterData.getParryingItemStack(seme, uke, e.getAmount());
                if (hero.getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) hero.getItem()).parrySkill(seme, uke, hero);
                }
                if (uke instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) uke;
                    p.sendStatusMessage(new TextComponentTranslation("you have parried! You have " + ukeCap.getPosture() + " posture left"), true);
                }
                if (seme instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) seme;
                    p.sendStatusMessage(new TextComponentTranslation("the target has parried! You have " + semeCap.getPosture() + " posture left"), true);
                }
                return;
            }
            if (TaoCasterData.isEntityBlocking(uke, (EntityLivingBase) e.getSource().getTrueSource()) && NeedyLittleThings.isFacingEntity(seme, uke)) {
                e.setCanceled(true);
                //block code, reflect posture damage
                TaoCasterData.getTaoCap(seme).consumePosture(postureUse1 * 0.4f, false);
                ((IStaminaPostureManipulable) TaoCasterData.getParryingItemStack(seme, uke, e.getAmount()).getItem()).onBlock(seme, uke, TaoCasterData.getParryingItemStack(seme, uke, e.getAmount()));
                if (uke instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) uke;
                    p.sendStatusMessage(new TextComponentTranslation("you have blocked! You have " + ukeCap.getPosture() + " posture left"), true);
                }
                if (seme instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) seme;
                    p.sendStatusMessage(new TextComponentTranslation("the target has blocked! They have " + ukeCap.getPosture() + " posture left"), true);
                }
            }
        }
    }

    //critical hit modifier
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void critModifier(CriticalHitEvent e) {
        ItemStack is = TaoWeapon.off ? e.getEntityPlayer().getHeldItemOffhand() : e.getEntityPlayer().getHeldItemMainhand();
        if (!(e.getTarget() instanceof EntityLivingBase)) return;
        if (is.getItem() instanceof ICombatManipulator) {
            ICombatManipulator icm = (ICombatManipulator) is.getItem();
            e.setDamageModifier(icm.critDamage(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), is));
        }
        if (e.getDamageModifier() > 1f) {
            e.setResult(Event.Result.ALLOW);
        } else {
            e.setResult(Event.Result.DENY);
        }
    }

    //critical hit check
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void vibeCheck(CriticalHitEvent e) {
        critted = e.getDamageModifier() > 1f && (e.getResult() == Event.Result.ALLOW || (e.isVanillaCritical() && e.getResult() == Event.Result.DEFAULT));
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
    public static void resetGeorge(LivingKnockBackEvent e) {
        modCall = false;
    }

    //absorbs, pierces and deflects are calculated before armor
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void biteMeStart(LivingHurtEvent e) {
        EntityLivingBase uke = e.getEntityLiving();
        DamageSource ds = e.getSource();
        float amnt = e.getAmount();

        //initial damage modifiers
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            ItemStack stack = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            if (stack.getItem() instanceof ICombatManipulator) {
                amnt = (((ICombatManipulator) stack.getItem()).damageStart(ds, seme, uke, stack, amnt));
            }
        }


        //if posture is broken, damage doubled, ignores deflection/absorption and resets posture
        if ((TaoCasterData.getTaoCap(uke).getDownTimer() != 0) && !down) {
            //FATALITY!
            for (int i = 0; i < 5; ++i) {
                double d0 = Taoism.unirand.nextGaussian() * 0.02D;
                double d1 = Taoism.unirand.nextGaussian() * 0.02D;
                double d2 = Taoism.unirand.nextGaussian() * 0.02D;
                uke.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, uke.posX + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, uke.posY + 1.0D + (double) (Taoism.unirand.nextFloat() * uke.height), uke.posZ + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, d0, d1, d2);
            }
            System.out.println("FATALITY!");
            amnt *= 2;
            posBreak = true;
            TaoCasterData.getTaoCap(uke).setPosture(TaoCasterData.getTaoCap(uke).getMaxPosture());
        }
        if (ds.isUnblockable()) return;//to prevent loops


        //deflect projectile damage
        if (ds.isProjectile() && ds.getImmediateSource() != null && !isSpecialDamage(ds) && !posBreak) {
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
            //e.setCanceled(true);
            //return;
            amnt = e.getAmount() / 5;
        }

        e.setAmount(amnt);
    }

    //modifies damage after armor, for reasons
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void wail(LivingDamageEvent e) {
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            ItemStack stack = TaoWeapon.off ? seme.getHeldItemOffhand() : seme.getHeldItemMainhand();
            if (stack.getItem() instanceof ICombatManipulator) {
                e.setAmount(((ICombatManipulator) stack.getItem()).damageEnd(e.getSource(), seme, e.getEntityLiving(), stack, e.getAmount()));
            }
        }
    }

    //resets posture regen if dealt damage, and gives the attacker 1 chi
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hahaNope(LivingDamageEvent e) {
        if (!e.isCanceled()) {
            //do not reset for fire, poison and arrows
            if (isSpecialDamage(e.getSource()) || e.getSource().isProjectile()) return;
            TaoCasterData.getTaoCap(e.getEntityLiving()).setPostureRechargeCD(CombatConfig.postureCD);
            if (e.getSource().getTrueSource() instanceof EntityLivingBase) {
                TaoCasterData.getTaoCap((EntityLivingBase) e.getSource().getTrueSource()).addQi(1f);
                //note that chi decays at a rate of 0.4 per second

            }
        }
    }

    //adds relevant attributes to everyone
    @SubscribeEvent
    public static void itsDangerousToGoAlone(EntityEvent.EntityConstructing e) {
        if (e.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e.getEntity();
            elb.getAttributeMap().registerAttribute(TaoEntities.DEFLECT);
            elb.getAttributeMap().registerAttribute(TaoEntities.ABLATION);
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
            TaoCasterData.syncCasterData(elb);
            TaoCasterData.getTaoCap(elb).setPosture(TaoCasterData.getTaoCap(elb).getMaxPosture());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickStuff(TickEvent.PlayerTickEvent e) throws IllegalAccessException {
        EntityPlayer p = e.player;
        if (e.phase.equals(TickEvent.Phase.END)) {
            //update max stamina, posture and ling. The other mobs don't have HUDs, so their spl only need to be recalculated when needed
            TaoCasterData.syncCasterData(p);
            //recharge weapon
            //hacky, but well...
            ItemStack mainhand = p.getHeldItemMainhand();
            ItemStack offhand = p.getHeldItemOffhand();
            //FIXME why did I make them global? Can't remember...
            if (Taoism.atk.getInt(p) == 0) {//fresh switch in, do not execute following code
                if (mainhand.getItem() instanceof ICombo && p.swingingHand != EnumHand.OFF_HAND)
                    TaoCasterData.getTaoCap(p).setSwitchIn(true);
            }
            if (Taoism.atk.getInt(p) == 1) {
                if (!TaoCasterData.getTaoCap(p).isSwitchIn()) {
                    //System.out.println("update combo main");
                    if (mainhand.getItem() instanceof ICombo && p.swingingHand != EnumHand.OFF_HAND) {
                        ICombo tw = (ICombo) mainhand.getItem();
                        long cache = tw.lastAttackTime(p, mainhand);
                        tw.updateLastAttackTime(p, mainhand);
                        long newcache = tw.lastAttackTime(p, mainhand);
                        if (newcache - cache > CombatConfig.timeBetweenAttacks) {
                            tw.setCombo(p, mainhand, 0);
                        }
                        double cd = tw.newCooldown(p, mainhand);
                        double totalSeconds = 20 / p.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
                        if (cd != 0f)
                            Taoism.atk.setInt(p, (int) (cd * totalSeconds));
                        tw.setCombo(p, mainhand, tw.getCombo(p, mainhand) + 1);
                    }
                } else {
                    if (mainhand.getItem() instanceof ISpecialSwitchIn && p.swingingHand != EnumHand.OFF_HAND) {
                        ISpecialSwitchIn issi = (ISpecialSwitchIn) mainhand.getItem();
                        issi.onSwitchIn(mainhand, p);
                    }
                    TaoCasterData.getTaoCap(p).setSwitchIn(false);
                }
            }
            if (offhand.getItem() instanceof ICombo) {
                if (TaoCasterData.getTaoCap(p).getOffhandCool() == 1) {
                    //System.out.println("update combo off");
                    ICombo tw = (ICombo) offhand.getItem();
                    long cache = tw.lastAttackTime(p, offhand);
                    tw.updateLastAttackTime(p, offhand);
                    long newcache = tw.lastAttackTime(p, offhand);
                    if (newcache - cache > CombatConfig.timeBetweenAttacks) {
                        tw.setCombo(p, offhand, 0);
                    }
                    double cd = tw.newCooldown(p, offhand);
                    double totalSeconds = 20 / NeedyLittleThings.getOffhandCD(p);
                    if (cd != 0f)
                        TaoCasterData.getTaoCap(p).setOffhandCool((int) (cd * totalSeconds));
                    tw.setCombo(p, offhand, tw.getCombo(p, offhand) + 1);
                }
            }
        }
    }
}
