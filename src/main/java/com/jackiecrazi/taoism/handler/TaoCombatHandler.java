package com.jackiecrazi.taoism.handler;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IAmVerySmart;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICombatManipulator;
import com.jackiecrazi.taoism.api.alltheinterfaces.IStaminaPostureManipulable;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Taoism.MODID)
public class TaoCombatHandler {
    public static final HashMap<Integer, Long> lastRightClickTime = new HashMap<>();
    private static final UUID noArmor = UUID.fromString("603114fc-164b-4d43-874c-3148eebde245");
    public static boolean modCall;
    private static boolean abort = false;
    private static ItemStack defend = ItemStack.EMPTY;

    //offhand handler for puny mod weapons that don't have it already
    //"puny" here defined as weapons that can't attack from offhand
    @SubscribeEvent
    public static void pleaseKillMeOff(PlayerInteractEvent.EntityInteract e) {
        if (e.getEntityPlayer().world.isRemote) return;
        if (e.getHand() == EnumHand.OFF_HAND && TaoCombatUtils.isValidCombatItem(e.getItemStack()) && !TaoCombatUtils.isShield(e.getItemStack()) && !(e.getItemStack().getItem() instanceof TaoWeapon)) {
            if (lastRightClickTime.getOrDefault(e.getEntityPlayer().getEntityId(), 0L) + 4 < e.getEntityPlayer().world.getTotalWorldTime())
                if (TaoCombatUtils.getHandCoolDown(e.getEntityPlayer(), EnumHand.OFF_HAND) > 0.9) {
                    TaoCasterData.getTaoCap(e.getEntityPlayer()).setSwing(TaoCombatUtils.getHandCoolDown(e.getEntityPlayer(), EnumHand.OFF_HAND));
                    TaoCombatUtils.taoWeaponAttack(e.getTarget(), e.getEntityPlayer(), e.getItemStack(), false, true);
                }
            lastRightClickTime.put(e.getEntityPlayer().getEntityId(), e.getEntityPlayer().world.getTotalWorldTime());
        }
    }


    //cancels attack if too far, done here instead of AttackEntityEvent because I need to check whether the damage source is melee.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void iHaveAWifeAndChildren(LivingAttackEvent e) {
        if (TaoCombatUtils.isDirectDamage(e.getSource()) && TaoCombatUtils.isMeleeDamage(e.getSource()) && e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase p = (EntityLivingBase) e.getSource().getTrueSource();
            if (p.isPotionActive(TaoPotion.FEAR) || p.isPotionActive(TaoPotion.DISORIENT) || (p.world.getEntityByID(TaoCasterData.getTaoCap(p).getTauntID()) != null && p.world.getEntityByID(TaoCasterData.getTaoCap(p).getTauntID()) != e.getEntityLiving())) {
                //you may only attack the target that taunted you
                e.setCanceled(true);
                return;
            }
            //cancel attack
            ItemStack i = TaoCombatUtils.getAttackingItemStackSensitive(p);
            if (i.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) i.getItem();
                if (!icm.canAttack(e.getSource(), p, e.getEntityLiving(), i, e.getAmount()))
                    e.setCanceled(true);
                return;
            }
            //reset mustDropHead tag every attack, so only the killing blow's head status is counted
            TaoCasterData.getTaoCap(e.getEntityLiving()).setMustDropHead(false);
        }
        //rolling inv frames for projectiles and melee damage
        if (TaoCombatUtils.isPhysicalDamage(e.getSource()) && TaoCasterData.getTaoCap(e.getEntityLiving()).getRollCounter() < CombatConfig.rollThreshold)
            e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void projectileParry(ProjectileImpactEvent e) {
        Entity ent = e.getEntity();
        if (ent == null) return;
        if (e.getRayTraceResult().entityHit instanceof EntityLivingBase) {
            EntityLivingBase uke = (EntityLivingBase) e.getRayTraceResult().entityHit;
            ITaoStatCapability cap = TaoCasterData.getTaoCap(uke);
            if (cap.getRollCounter() < CombatConfig.rollThreshold)
                e.setCanceled(true);
            ItemStack perrier = TaoCombatUtils.getShield(ent, uke, 1);
            if (cap.getQi() > 5) perrier = TaoCombatUtils.getParryingItemStack(ent, uke, 1);
            if (!uke.world.isRemote && NeedyLittleThings.isFacingEntity(uke, ent, 120) && !perrier.isEmpty()) {
                boolean free = cap.getParryCounter() < CombatConfig.shieldThreshold;
                if (cap.consumePosture(free ? 0 : CombatConfig.posturePerProjectile, false) == 0) {
                    Vec3d look = uke.getLookVec();
                    ent.motionX = look.x;
                    ent.motionY = look.y;
                    ent.motionZ = look.z;
                    ent.velocityChanged = true;
                    e.setCanceled(true);//seriously, who thought loading rooftops with a ton of archers was a good idea?
                    if (!free)
                        cap.setParryCounter(0);
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
        if (e.getSource() == null) return;
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() instanceof EntityLivingBase && !uke.world.isRemote) {
            if (!TaoCombatUtils.isMeleeDamage(ds))//
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
            if (semeCap.getBindTime() > 0 || semeCap.getDownTimer() > 0) {//bound and downed entities cannot attack
                e.setCanceled(true);
                return;
            }
            ItemStack attack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (attack.getItem() instanceof ICombatManipulator) {
                ICombatManipulator icm = (ICombatManipulator) attack.getItem();
                icm.attackStart(ds, seme, uke, attack, e.getAmount());
            }
            if (ds.getImmediateSource() != ds.getTrueSource())
                return;//indirect attacks, like kusarigama and rope dart, cannot be parried at this point
            if (ukeCap.getDownTimer() > 0) return;//downed things are defenseless
            if (ukeCap.isRecordingDamage()) return;//prevent parries when being executed, and makes it look better
            //slime, I despise thee.
            if (!(seme instanceof EntityPlayer)) {
                if (semeCap.getSwing() < CombatConfig.mobForcedCooldown) {//nein nein nein nein nein nein nein
                    //take that, slimes, you ain't staggerin' me no more!
                    e.setCanceled(true);
                    return;
                } else
                    semeCap.setSwing(0);
            }
            /*
            idle parry
            knockback distributed between both units depending on their max posture
            you still take posture damage for being attacked, to incentivize attacking, as per the usual block formula
            knockback taken is equal to base damage divided by defense posture multiplier divided by max posture
            so big posture=less knockback=standing tank
            while this sounds bad for low pos modifier weapons, it means they can bounce around. Hyper-mobile combat!
             */
            defend = TaoCombatUtils.getParryingItemStack(seme, uke, e.getAmount());
            //System.out.println("parrying stack is "+defend);
            float atk = TaoCombatUtils.postureAtk(uke, seme, attack, e.getAmount());
            float def = TaoCombatUtils.postureDef(uke, seme, defend, e.getAmount());
            //posture is consumed *regardless* of parry
            if (ukeCap.consumePosture(atk * def, true, seme) == 0f && !defend.isEmpty()) {
                e.setCanceled(true);
                //shield disabling
                if (TaoCombatUtils.isShield(defend) && attack.getItem().canDisableShield(attack, defend, uke, seme)) {
                    //shield is disabled
                    if (uke instanceof EntityPlayer)
                        ((EntityPlayer) uke).getCooldownTracker().setCooldown(defend.getItem(), 60);
                    uke.world.setEntityState(uke, (byte) 30);
                    uke.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.25f + Taoism.unirand.nextFloat() * 0.5f, (1 - (ukeCap.getPosture() / ukeCap.getMaxPosture())) + Taoism.unirand.nextFloat() * 0.5f);
                } else
                    uke.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.25f + Taoism.unirand.nextFloat() * 0.5f, (1 - (ukeCap.getPosture() / ukeCap.getMaxPosture())) + Taoism.unirand.nextFloat() * 0.5f);
                //reset cooldown
                TaoCombatUtils.rechargeHand(uke, uke.getHeldItemOffhand() == defend ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, true);
                if (defend.getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) defend.getItem()).onParry(seme, uke, defend, e.getAmount());
                }
                if (TaoCombatUtils.isShield(defend) && ukeCap.getParryCounter() > CombatConfig.shieldThreshold) {
                    //parry invframes
                    ukeCap.setParryCounter(0);
                }
                EnumHand other = uke.getHeldItemMainhand() == defend ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                if (uke.getHeldItem(other).getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) uke.getHeldItem(other).getItem()).onOtherHandParry(seme, uke, uke.getHeldItem(other), e.getAmount());
                }
                //parry, both parties are knocked back slightly
                float atkDef = TaoCombatUtils.postureDef(seme, uke, attack, e.getAmount());
                NeedyLittleThings.knockBack(seme, uke, Math.min(1.5f, 2 * atk * atkDef / (semeCap.getMaxPosture())), true, false);
                NeedyLittleThings.knockBack(uke, seme, Math.min(1.5f, 2 * atk * def / (ukeCap.getMaxPosture())), true, false);
            }
            TaoCasterData.forceUpdateTrackingClients(uke);
        }
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

    //by config option, will also replace the idiotic chance to resist knock with ratio resist. Somewhat intrusive.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void knockKnockWhosThere(LivingKnockBackEvent e) {
        if (TaoCasterData.getTaoCap(e.getEntityLiving()).getDownTimer() > 0) {
            e.setCanceled(true);
            return;
        }
        if (!modCall && CombatConfig.modifyKnockBackCode) {
            e.setCanceled(true);
            NeedyLittleThings.knockBack(e.getEntityLiving(), e.getAttacker(), e.getOriginalStrength(), e.getOriginalRatioX(), 0, e.getOriginalRatioZ(), false);
            return;
        }
        if (e.getOriginalAttacker() instanceof EntityLivingBase) {
            EntityLivingBase seme = (EntityLivingBase) e.getOriginalAttacker();
            EntityLivingBase uke = e.getEntityLiving();
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            float f = e.getOriginalStrength();
            if (stack.getItem() instanceof ICombatManipulator && TaoCasterData.getTaoCap(uke).getDownTimer() == 0) {//down timer check needed to prevent loops
                f = ((ICombatManipulator) stack.getItem()).onKnockingBack(seme, uke, stack, f);
            }
            if (defend.getItem() instanceof ICombatManipulator && TaoCasterData.getTaoCap(uke).getDownTimer() == 0) {//down timer check needed to prevent loops
                f = ((ICombatManipulator) defend.getItem()).onBeingKnockedBack(seme, uke, defend, f);
            }
            e.setStrength(f);
        }
        //since knockback is ignored when mounted, it becomes extra posture instead
        if (e.getEntityLiving().isRiding()) {
            int divisor = 1;
            for (Entity ride = e.getEntityLiving(); ride != null && ride.isRiding(); ride = ride.getRidingEntity()) {
                divisor++;
            }
            for (Entity ride = e.getEntityLiving(); ride != null && ride.isRiding(); ride = ride.getRidingEntity()) {
                if (ride instanceof EntityLivingBase)
                    TaoCasterData.getTaoCap((EntityLivingBase) ride).consumePosture(e.getStrength() / divisor, true);
            }
        }
        //if (e.getStrength() == 0) e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void resetGeorge(LivingKnockBackEvent e) {//what? George?
        modCall = false;
        defend = ItemStack.EMPTY;
    }

    //absorbs, pierces and deflects are calculated before armor
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void biteMe(LivingHurtEvent e) {
        if (abort) return;
        EntityLivingBase uke = e.getEntityLiving();
        DamageSource ds = e.getSource();
        float amnt = e.getAmount();

        //initial damage modifiers
        if (TaoCombatUtils.isDirectDamage(ds) && ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator) {
                amnt = (((ICombatManipulator) stack.getItem()).hurtStart(ds, seme, uke, stack, amnt * (((ICombatManipulator) stack.getItem()).damageMultiplier(seme, uke, stack))));
            }
            if (seme instanceof EntityLiving)
                if (CombatConfig.taoWeaponHitEntity && stack.getItem() instanceof TaoWeapon) {
                    stack.getItem().hitEntity(stack, uke, seme);
                } else if (CombatConfig.weaponHitEntity) {
                    stack.getItem().hitEntity(stack, uke, seme);
                }
        }

        if (uke.getHeldItemMainhand().getItem() instanceof ICombatManipulator) {
            amnt = ((ICombatManipulator) uke.getHeldItemMainhand().getItem()).onBeingHurt(e.getSource(), uke, uke.getHeldItemMainhand(), amnt);
        }
        if (uke.getHeldItemOffhand().getItem() instanceof ICombatManipulator) {
            amnt = ((ICombatManipulator) uke.getHeldItemOffhand().getItem()).onBeingHurt(e.getSource(), uke, uke.getHeldItemOffhand(), amnt);
        }

        ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
        boolean posBreak = false;
        //if posture is broken, damage increased, ignores deflection/absorption
        if (ukeCap.getDownTimer() > 0) {
            if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
                if (seme.world instanceof WorldServer) {
                    ((WorldServer) seme.world).spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, uke.posX, uke.posY, uke.posZ, 5, uke.width, uke.height, uke.width, 0.5f);
                }
                seme.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.ENTITY_GENERIC_BIG_FALL, SoundCategory.PLAYERS, 0.25f + Taoism.unirand.nextFloat() * 0.5f, 0.75f + Taoism.unirand.nextFloat() * 0.5f);
            }
            //System.out.println("FATALITY!");
            posBreak = true;
        }
        if (ds.isUnblockable()) {
            e.setAmount(amnt);
            return;//to prevent loops
        }
        //deflect projectile damage
        if (TaoCombatUtils.isPhysicalDamage(ds) && ds.getImmediateSource() != null && !posBreak) {
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
        if (TaoCombatUtils.isDirectDamage(ds) && ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            int ignoreAmnt = 0;
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (TaoCasterData.getTaoCap(uke).getDownTimer() > 0)
                ignoreAmnt += 9;
            if (stack.getItem() instanceof ICombatManipulator) {
                ignoreAmnt += ((ICombatManipulator) stack.getItem()).armorIgnoreAmount(e.getSource(), seme, uke, stack, e.getAmount());
            }
            uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(noArmor);//just in case...
            uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier(noArmor, "ouches", -ignoreAmnt, 0));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void wail(LivingDamageEvent e) {
        e.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(noArmor);
    }

    //resets posture regen if dealt damage, and gives the attacker 1 chi
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hahaNope(LivingDamageEvent e) {
        if (abort) return;
        EntityLivingBase uke = e.getEntityLiving();
        if (e.getSource().getTrueSource() != null && e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) e.getSource().getTrueSource());
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator) {
                e.setAmount(((ICombatManipulator) stack.getItem()).damageStart(e.getSource(), seme, uke, stack, e.getAmount()));
            }
        }
        if (uke.getHeldItemMainhand().getItem() instanceof ICombatManipulator) {
            e.setAmount(((ICombatManipulator) uke.getHeldItemMainhand().getItem()).onBeingDamaged(e.getSource(), uke, uke.getHeldItemMainhand(), e.getAmount()));
        }
        if (uke.getHeldItemOffhand().getItem() instanceof ICombatManipulator) {
            e.setAmount(((ICombatManipulator) uke.getHeldItemOffhand().getItem()).onBeingDamaged(e.getSource(), uke, uke.getHeldItemOffhand(), e.getAmount()));
        }
        ITaoStatCapability ukecap = TaoCasterData.getTaoCap(uke);
        if (ukecap.isRecordingDamage()) {//record damage for rainy days
            ukecap.addRecordedDamage(e.getAmount());
            e.setAmount(0);
            //e.setCanceled(true);
        }
        if (!e.isCanceled()) {//do not reset when a person's downed, otherwise it gets out of hand fast
            //do not reset for fire, poison and arrows
            if (!TaoCombatUtils.isMeleeDamage(e.getSource())) return;
            if (ukecap.getDownTimer() <= 0)
                ukecap.setPostureRechargeCD(CombatConfig.postureCD);
        }
        TaoCasterData.forceUpdateTrackingClients(uke);
    }

    @SubscribeEvent
    public static void thief(LootingLevelEvent e) {
        if (TaoCasterData.getTaoCap(e.getEntityLiving()).willDropHead() && NeedyLittleThings.dropSkull(e.getEntityLiving()) == null) {
            e.setLootingLevel(e.getLootingLevel() + 3);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void vengeance(LivingDeathEvent e) {
        if (CombatConfig.revengePreventDespawn && e.getEntityLiving() instanceof EntityPlayer && e.getSource().getTrueSource() instanceof EntityLiving) {
            ((EntityLiving) e.getSource().getTrueSource()).enablePersistence();
        }
    }

    @SubscribeEvent
    public static void dropHead(LivingDropsEvent e) {
        if (e.isRecentlyHit()) {
            if (TaoCasterData.getTaoCap(e.getEntityLiving()).willDropHead() && e.getEntityLiving().world.getGameRules().getBoolean("doMobLoot")) {
                for (EntityItem i : e.getDrops()) {
                    if (i.getItem().getItem() instanceof ItemSkull) return;
                }
                ItemStack drop = NeedyLittleThings.dropSkull(e.getEntityLiving());
                if (drop == null) return;
                EntityItem forceSkull = new EntityItem(e.getEntityLiving().world, e.getEntityLiving().posX, e.getEntityLiving().posY, e.getEntityLiving().posZ, drop);
                forceSkull.setDefaultPickupDelay();
                e.getDrops().add(forceSkull);
            }
        }
    }

}
