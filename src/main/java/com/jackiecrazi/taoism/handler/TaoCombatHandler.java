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
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Taoism.MODID)
public class TaoCombatHandler {
    private static final UUID noArmor = UUID.fromString("603114fc-164b-4d43-874c-3148eebde245");
    public static boolean modCall;
    private static boolean abort = false;

    //cancels attack if too far, done here instead of AttackEntityEvent because I need to check whether the damage source is melee.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
        if (TaoCombatUtils.isPhysicalDamage(e.getSource()) && TaoCasterData.getTaoCap(e.getEntityLiving()).getRollCounter() < CombatConfig.rollThreshold)
            e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void projectileParry(ProjectileImpactEvent e) {
        Entity ent = e.getEntity();
        if (ent == null) return;
        if (EntityList.getKey(ent) != null && EntityList.getKey(ent).getResourceDomain().equals(Taoism.MODID))
            return;//derp derp derp
        if (e.getRayTraceResult().entityHit instanceof EntityLivingBase) {
            EntityLivingBase uke = (EntityLivingBase) e.getRayTraceResult().entityHit;
            if (TaoCasterData.getTaoCap(uke).getRollCounter() < CombatConfig.rollThreshold)
                e.setCanceled(true);
            if (!uke.world.isRemote && NeedyLittleThings.isFacingEntity(uke, ent, 120) && !TaoCombatUtils.getParryingItemStack(ent, uke, 1).isEmpty()) {
                if (TaoCasterData.getTaoCap(uke).getQi() * TaoCasterData.getTaoCap(uke).getQi() / 2f > NeedyLittleThings.getSpeedSq(ent) && TaoCasterData.getTaoCap(uke).consumePosture(CombatConfig.posturePerProjectile, false) == 0) {
                    Vec3d look = uke.getLookVec();
                    ent.motionX = look.x;
                    ent.motionY = look.y;
                    ent.motionZ = look.z;
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
        if (e.getSource() == null) return;
        DamageSource ds = e.getSource();
        if (ds.getTrueSource() instanceof EntityLivingBase && !uke.world.isRemote) {
            if (ds.getImmediateSource() != ds.getTrueSource() || !TaoCombatUtils.isMeleeDamage(ds))//
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
            if ((!smart || !defend.isEmpty()) && (ukeCap.consumePosture(atk * def, true, seme) == 0f) && smart) {
                e.setCanceled(true);
                uke.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.25f + Taoism.unirand.nextFloat() * 0.5f, (1 - (ukeCap.getPosture() / ukeCap.getMaxPosture())) + Taoism.unirand.nextFloat() * 0.5f);
//                    uke.world.playSound(uke.posX, uke.posY, uke.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 1f, true);
//                    uke.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1f, 1f);
                //parry, both parties are knocked back slightly
                float atkDef = TaoCombatUtils.postureDef(seme, uke, attack, e.getAmount());
                NeedyLittleThings.knockBack(seme, uke, Math.min(1.5f, 3 * atk * atkDef / semeCap.getMaxPosture()));
                NeedyLittleThings.knockBack(uke, seme, Math.min(1.5f, 3 * atk * def / ukeCap.getMaxPosture()));
                //reset cooldown
                TaoCombatUtils.rechargeHand(uke, uke.getHeldItemOffhand() == defend ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f);
                if (defend.getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) defend.getItem()).onParry(seme, uke, defend);
                }
                EnumHand other = uke.getHeldItemMainhand() == defend ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                if (uke.getHeldItem(other).getItem() instanceof IStaminaPostureManipulable) {
                    ((IStaminaPostureManipulable) uke.getHeldItem(other).getItem()).onOtherHandParry(seme, uke, uke.getHeldItem(other));
                }
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

    //critical hit check
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void vibeCheck(CriticalHitEvent e) {
        //critted = e.getDamageModifier() > 1f && (e.getResult() == Event.Result.ALLOW || (e.isVanillaCritical() && e.getResult() == Event.Result.DEFAULT));
    }

    //by config option, will also replace the idiotic chance to resist knock with ratio resist. Somewhat intrusive.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void knockKnockWhosThere(LivingKnockBackEvent e) {
        if (TaoCasterData.getTaoCap(e.getEntityLiving()).isRecordingDamage()) {
            e.setCanceled(true);
            return;
        }
        if (!modCall && CombatConfig.modifyKnockBackCode) {
            e.setCanceled(true);
            NeedyLittleThings.knockBack(e.getEntityLiving(), e.getAttacker(), e.getOriginalStrength(), e.getOriginalRatioX(), 0, e.getOriginalRatioZ());
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
                if (CombatConfig.taoWeaponHitEntity && stack.getItem() instanceof TaoWeapon) {
                    stack.getItem().hitEntity(stack, uke, seme);
                } else if (CombatConfig.weaponHitEntity) {
                    stack.getItem().hitEntity(stack, uke, seme);
                }
        }

        ITaoStatCapability ukeCap = TaoCasterData.getTaoCap(uke);
        boolean posBreak = false;
        //if posture is broken, damage increased, ignores deflection/absorption
        if (ukeCap.getDownTimer() > 0) {
            if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
                if(seme.world instanceof WorldServer){
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
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) ds.getTrueSource());
            int ignoreAmnt = 0;
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
        if (e.getSource().getTrueSource() != null && e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase seme = ((EntityLivingBase) e.getSource().getTrueSource());
            ItemStack stack = TaoCombatUtils.getAttackingItemStackSensitive(seme);
            if (stack.getItem() instanceof ICombatManipulator) {
                e.setAmount(((ICombatManipulator) stack.getItem()).finalDamageMods(e.getSource(), seme, uke, stack, e.getAmount()));
            }
        }
        ITaoStatCapability ukecap = TaoCasterData.getTaoCap(uke);
        if (ukecap.isRecordingDamage()) {//record damage for rainy days
            ukecap.addRecordedDamage(e.getAmount());
            e.setAmount(0);
            e.setCanceled(true);
        }
        uke.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(noArmor);
        if (!e.isCanceled()) {//do not reset when a person's downed, otherwise it gets out of hand fast
            //do not reset for fire, poison and arrows
            if (!TaoCombatUtils.isMeleeDamage(e.getSource())) return;
            if (ukecap.getDownTimer() <= 0)
                ukecap.setPostureRechargeCD(CombatConfig.postureCD);
//            else
//                ukecap.setDownTimer(ukecap.getDownTimer()-(int)(e.getAmount()*5));
        }
        TaoCasterData.forceUpdateTrackingClients(uke);
    }

}
