package com.jackiecrazi.taoism.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.allthedamagetypes.EntityDamageSourceTaoIndirect;
import com.jackiecrazi.taoism.api.alltheinterfaces.IMove;
import com.jackiecrazi.taoism.api.alltheinterfaces.IQiPostureManipulable;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.config.CombatConfig;
import ibxm.Player;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TaoCombatUtils {
    /**
     * Copied from EntityArrow, because kek.
     */
    public static final Predicate<Entity> VALID_TARGETS = Predicates.and(EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE, e -> e != null && !(e instanceof EntityHanging) && e.canBeCollidedWith());
    private static final Gson gson = new Gson();
    public static HashMap<String, Float> customPosture;
    public static String configFolder;
    public static boolean suppress;
    private static CombatInfo DEFAULT = new CombatInfo(1, 1, false);
    private static HashMap<Item, CombatInfo> combatList = new HashMap<>();
    private static HashMap<String, CombatInfo> archetypes = new HashMap<>();

    public static void attack(EntityLivingBase elb, Entity target, EnumHand hand) {
        attack(elb, target, hand, causeLivingDamage(elb));
    }

    public static void attack(EntityLivingBase elb, Entity target, EnumHand hand, DamageSource ds) {
        attackAtStrength(elb, target, hand, 1, ds);
    }

    public static void attack(EntityLivingBase from, Entity to, boolean offhand) {
        if (offhand) {
            swapHeldItems(from);
            TaoCasterData.getTaoCap(from).setOffhandAttack(true);
        }
        if (Taoism.getAtk(from) > 0) {
            int temp = Taoism.getAtk(from);
            if (from instanceof EntityPlayer) ((EntityPlayer) from).attackTargetEntityWithCurrentItem(to);
            else from.attackEntityAsMob(to);
            Taoism.setAtk(from, temp);
        }
        if (offhand) {
            swapHeldItems(from);
            TaoCasterData.getTaoCap(from).setOffhandAttack(false);
        }
    }

    public static void attackAtStrength(EntityLivingBase elb, Entity target, EnumHand hand, float cooldownPercent, DamageSource ds) {
        float temp = getHandCoolDown(elb, hand);
        target.hurtResistantTime = 0;
        rechargeHand(elb, hand, cooldownPercent, false);
        taoWeaponAttack(target, elb, elb.getHeldItem(hand), hand == EnumHand.MAIN_HAND, true, ds);
        rechargeHand(elb, hand, temp, false);
    }

    public static void attackIndirectly(EntityLivingBase elb, Entity proxy, Entity target, EnumHand hand) {
        attack(elb, target, hand, EntityDamageSourceTaoIndirect.causeProxyDamage(elb, proxy));
    }

    public static DamageSource causeLivingDamage(EntityLivingBase elb) {
        if (elb == null) return DamageSource.GENERIC;
        if (elb instanceof EntityPlayer)
            return DamageSource.causePlayerDamage((EntityPlayer) elb);
        else return DamageSource.causeMobDamage(elb);
    }

    public static void executeMove(EntityLivingBase elb, byte moveCode) {
        ItemStack main = elb.getHeldItemMainhand();
        ItemStack off = elb.getHeldItemOffhand();
        MoveCode mc = new MoveCode(moveCode);
        //update mainhand if it's a left click or you're two-handed
        if (((!(main.getItem() instanceof ITwoHanded) || ((ITwoHanded) main.getItem()).isTwoHanded(main) || mc.isLeftClick())) && main.getItem() instanceof IMove) {
            if (!main.hasTagCompound()) main.setTagCompound(new NBTTagCompound());
            main.getTagCompound().setByte("lastMove", main.getTagCompound().getByte("currentMove"));
            main.getTagCompound().setByte("currentMove", moveCode);
        }
        //update offhand if it's a right click or main hand's two-handed
        if ((!(main.getItem() instanceof ITwoHanded) || ((ITwoHanded) main.getItem()).isTwoHanded(main) || !mc.isLeftClick()) && off.getItem() instanceof IMove) {//if it's left click, only the right hand needs to know, and if it's two-handed it'll be updated anyway
            if (!off.hasTagCompound()) off.setTagCompound(new NBTTagCompound());
            off.getTagCompound().setByte("lastMove", off.getTagCompound().getByte("currentMove"));
            off.getTagCompound().setByte("currentMove", moveCode);
        }
    }

    public static ItemStack getAttackingItemStackSensitive(EntityLivingBase elb) {
        if (elb.isHandActive()) return elb.getHeldItem(elb.getActiveHand());
        return TaoCasterData.getTaoCap(elb).isOffhandAttack() ? elb.getHeldItemOffhand() : elb.getHeldItemMainhand();
    }

    public static float getCooldownPeriod(EntityLivingBase elb) {
        return (float) (20.0D / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, EnumHand.MAIN_HAND));
    }

    public static float getCooldownPeriodOff(EntityLivingBase elb) {
        return (float) (1.0D / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, EnumHand.OFF_HAND) * 20.0D);
    }

    public static float getCooledAttackStrength(EntityLivingBase elb, float adjustTicks) {
        if (elb instanceof EntityPlayer) return ((EntityPlayer) elb).getCooledAttackStrength(adjustTicks);
        return MathHelper.clamp((TaoCasterData.getTaoCap(elb).getSwing() + adjustTicks) / getCooldownPeriod(elb), 0.0F, 1.0F);
    }

    public static float getCooledAttackStrengthOff(EntityLivingBase elb, float adjustTicks) {
        return MathHelper.clamp(((float) TaoCasterData.getTaoCap(elb).getOffhandCool() + adjustTicks) / getCooldownPeriodOff(elb), 0.0F, 1.0F);
    }

    public static float getHandCoolDown(EntityLivingBase elb, EnumHand hand) {
        if (elb instanceof EntityPlayer)
            switch (hand) {
                case OFF_HAND:
                    return getCooledAttackStrengthOff(elb, 0.5f);
                case MAIN_HAND:
                    return getCooledAttackStrength(elb, 0.5f);
            }
        return 1f;
    }

    public static ItemStack getParryingItemStack(Entity attacker, EntityLivingBase elb, float amount) {
        if (TaoCasterData.getTaoCap(elb).getBindTime() > 0) return ItemStack.EMPTY;
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        if (elb instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) elb;
            if (ep.getCooldownTracker().hasCooldown(main.getItem())) main = ItemStack.EMPTY;
            if (ep.getCooldownTracker().hasCooldown(off.getItem())) off = ItemStack.EMPTY;
        }
        boolean facing = NeedyLittleThings.isFacingEntity(elb, attacker, 120) && (elb instanceof EntityPlayer || Taoism.unirand.nextFloat() < CombatConfig.mobParryChance);
        boolean mainRec = getCooledAttackStrength(elb, 0.5f) > 0.8f, offRec = getCooledAttackStrengthOff(elb, 0.5f) > 0.8f;
        //System.out.println("main is " + mainRec + ", off is " + offRec);
        //System.out.println("main is " + getCooledAttackStrength(elb, 0.5f) + ", off is " + getCooledAttackStrengthOff(elb, 0.5f));
        float defMult = 42;//meaning of life, the universe and everything
        ItemStack ret = ItemStack.EMPTY;
        //shields
        boolean halt = false;
        if (isShield(main) && (TaoCasterData.getTaoCap(elb).getParryCounter() < CombatConfig.shieldThreshold || (mainRec && facing && defMult > postureDef(elb, attacker, main, amount)))) {
            ret = main;
            defMult = postureDef(elb, attacker, ret, amount);
            halt = true;
        }
        if (isShield(off) && (TaoCasterData.getTaoCap(elb).getParryCounter() < CombatConfig.shieldThreshold || (offRec && facing && defMult > postureDef(elb, attacker, off, amount)))) {
            ret = off;
            defMult = postureDef(elb, attacker, ret, amount);
            halt = true;
        }
        if (halt) return ret;
        //parries
        if (isValidCombatItem(main) && mainRec && facing && defMult > postureDef(elb, attacker, main, amount)) {
            ret = main;
            defMult = postureDef(elb, attacker, ret, amount);
        }
        if (isValidCombatItem(off) && offRec && facing && defMult > postureDef(elb, attacker, off, amount)) {
            ret = off;
            defMult = postureDef(elb, attacker, ret, amount);
        }
        //mainhand
        if (main.getItem() instanceof IQiPostureManipulable && ((IQiPostureManipulable) main.getItem()).canBlock(elb, attacker, main, mainRec, amount) && ((IQiPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, elb, main, amount) <= defMult) {
            //System.out.println("using main hand for parry");
            defMult = ((IQiPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, elb, main, amount);
            ret = main;
        }
        //offhand
        if (off.getItem() instanceof IQiPostureManipulable && ((IQiPostureManipulable) off.getItem()).canBlock(elb, attacker, off, offRec, amount) && ((IQiPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount) <= defMult) {
            //System.out.println("using off hand for parry");
            ret = off;
        }
        return ret;
    }

    public static ItemStack getShield(Entity attacker, EntityLivingBase elb, float amount) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        boolean mainRec = getCooledAttackStrength(elb, 0.5f) > 0.8f, offRec = getCooledAttackStrengthOff(elb, 0.5f) > 0.8f;
        boolean facing = NeedyLittleThings.isFacingEntity(elb, attacker, 120);
        ItemStack ret = ItemStack.EMPTY;
        //shields
        if (isShield(off) && offRec && facing) {
            return off;
        }
        if (isShield(main) && mainRec && facing) {
            return main;
        }
        return ret;
    }

    public static boolean isDirectDamage(DamageSource ds) {
        return "player".equals(ds.damageType) || "mob".equals(ds.damageType);
    }

    public static boolean isMeleeDamage(DamageSource ds) {
        return isPhysicalDamage(ds) && !ds.isProjectile();
    }

    public static boolean isPhysicalDamage(DamageSource ds) {
        return !ds.isFireDamage() && !ds.isMagicDamage() && !ds.isUnblockable() && !ds.isExplosion() && !ds.isDamageAbsolute();
    }

    public static boolean isShield(ItemStack i) {
        if (i.getItem().getRegistryName() == null) return false;
        return combatList.getOrDefault(i.getItem(), DEFAULT).isShield;
    }

    public static boolean isTwoHanded(ItemStack is) {
        return is != null && !is.isEmpty() && combatList.getOrDefault(is.getItem(), DEFAULT).twoHanded;
    }

    public static boolean isValidCombatItem(ItemStack i) {
        if (i.getItem().getRegistryName() == null) return false;
        return combatList.containsKey(i.getItem());
    }

    public static boolean isWeapon(ItemStack i) {
        if (i.getItem().getRegistryName() == null) return false;
        return !combatList.getOrDefault(i.getItem(), DEFAULT).isShield;
    }

    private static boolean onPlayerAttackTarget(EntityPlayer player, Entity target, EnumHand hand) {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return false;
        ItemStack stack = player.getHeldItem(hand);
        return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
    }

    @Nonnull
    private static CombatInfo parseMeleeInfo(JsonObject obj) {
        CombatInfo put = new CombatInfo(CombatConfig.defaultMultiplierPostureAttack, CombatConfig.defaultMultiplierPostureDefend, false);
        if (obj.has("attack")) put.attackPostureMultiplier = obj.get("attack").getAsFloat();
        if (obj.has("defend")) put.defensePostureMultiplier = obj.get("defend").getAsFloat();
        if (obj.has("shield")) put.isShield = obj.get("shield").getAsBoolean();
        /*SweepInfo defaultSweep = GSON.fromJson(obj, SweepInfo.class);
        put.sweeps[0] = defaultSweep;
        for (SWEEPSTATE s : SWEEPSTATE.values()) {
            int ord = s.ordinal();
            JsonElement gottem = obj.get(s.name().toLowerCase(Locale.ROOT));
            if (gottem == null || !gottem.isJsonObject()) {
                if (GeneralConfig.debug)
                    WarDance.LOGGER.debug("did not find " + s + ", generating defaults");
                //"smartly" infer what kind of falling attack is wanted:
                //cone->cleave, impact->impact, line->line, the others->none
                if (s == SWEEPSTATE.FALLING)
                    switch (defaultSweep.sweep) {
                        case CONE -> {
                            SweepInfo fresh = new SweepInfo(SWEEPTYPE.CLEAVE, defaultSweep.sweep_base, defaultSweep.sweep_scale);
                            fresh.crit = true;
                            put.sweeps[ord] = fresh;
                        }
                        case IMPACT, LINE -> put.sweeps[ord] = defaultSweep;
                        default -> put.sweeps[ord] = DEFAULT_NONE;
                    }
                else put.sweeps[ord] = put.sweeps[0];
                continue;
            }
            JsonObject sub = gottem.getAsJsonObject();
            SweepInfo sweep = GSON.fromJson(sub, SweepInfo.class);
            put.sweeps[ord] = sweep;
        }*/
        return put;
    }

    public static float postureAtk(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        float ret = attack.getItem() instanceof IQiPostureManipulable ? ((IQiPostureManipulable) attack.getItem()).postureDealtBase(attacker, defender, attack, amount) :
                combatList.containsKey(attack.getItem()) ? combatList.get(attack.getItem()).attackPostureMultiplier :
                        amount * CombatConfig.defaultMultiplierPostureAttack;
        if (attack.isEmpty()) {//bare hand 1.5x
            ret *= CombatConfig.defaultPostureKenshiro;
        }
        if (!(attacker instanceof EntityPlayer)) ret *= CombatConfig.basePostureMob;
        else ret *= TaoCasterData.getTaoCap(attacker).getSwing() * TaoCasterData.getTaoCap(attacker).getSwing();
        return ret;
    }

    public static float postureDef(EntityLivingBase defender, Entity attacker, ItemStack defend, float amount) {
        if (TaoCasterData.getTaoCap(defender).getParryCounter() < CombatConfig.shieldThreshold)
            return 0;
        return (defend.getItem() instanceof IQiPostureManipulable ? ((IQiPostureManipulable) defend.getItem()).postureMultiplierDefend(attacker, defender, defend, amount) :
                        combatList.getOrDefault(defend.getItem(), DEFAULT).defensePostureMultiplier);
    }

    public static void rechargeHand(EntityLivingBase elb, EnumHand hand, float percent, boolean syncWithClient) {
        if (!(elb instanceof EntityPlayer)) return;
        double totalSec = 20 / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, hand);
        //if (percent != 0f)//this is because this is called in tickStuff on the first tick after cooldown starts, so constant resetting would just make the weapon dysfunctional
        switch (hand) {
            case OFF_HAND:
                TaoCasterData.getTaoCap(elb).setOffhandCool((int) (percent * totalSec));
                break;
            case MAIN_HAND:
                Taoism.setAtk(elb, (int) (percent * totalSec));
                if (syncWithClient)
                    TaoCasterData.syncAttackTimer(elb);
                break;
        }
    }

    public static void swapHeldItems(EntityLivingBase e) {
        //attributes = new ArrayList<>();
        ItemStack main = e.getHeldItemMainhand(), off = e.getHeldItemOffhand();
        int tssl = Taoism.getAtk(e);
        suppress = true;
        ITaoStatCapability cap = TaoCasterData.getTaoCap(e);
        e.setHeldItem(EnumHand.MAIN_HAND, e.getHeldItemOffhand());
        e.setHeldItem(EnumHand.OFF_HAND, main);
//        int mbind = cap.getbi(EnumHand.MAIN_HAND);
//        cap.setHandBind(EnumHand.MAIN_HAND, cap.getHandBind(EnumHand.OFF_HAND));
//        cap.setHandBind(EnumHand.OFF_HAND, mbind);
        //tried really hard to make this work, but it just causes more problems.
        suppress = false;
        e.getAttributeMap().removeAttributeModifiers(main.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        e.getAttributeMap().applyAttributeModifiers(off.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        e.getAttributeMap().removeAttributeModifiers(off.getAttributeModifiers(EntityEquipmentSlot.OFFHAND));
        e.getAttributeMap().applyAttributeModifiers(main.getAttributeModifiers(EntityEquipmentSlot.OFFHAND));
//        main.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).forEach((att, mod) -> Optional.ofNullable(e.getEntityAttribute(SharedMonsterAttributes.)).ifPresent((mai) -> mai.removeModifier(mod)));
//        off.getAttributeModifiers(EntityEquipmentSlot.OFFHAND).forEach((att, mod) -> Optional.ofNullable(e.getAttribute(att)).ifPresent((mai) -> mai.removeModifier(mod)));
//        main.getAttributeModifiers(EntityEquipmentSlot.OFFHAND).forEach((att, mod) -> Optional.ofNullable(e.getAttribute(att)).ifPresent((mai) -> {
//            if (!mai.hasModifier(mod))
//                mai.addTransientModifier(mod);
//        }));
//        off.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((att, mod) -> Optional.ofNullable(e.getAttribute(att)).ifPresent((mai) -> {
//            if (!mai.hasModifier(mod))
//                mai.addTransientModifier(mod);
//        }));
        Taoism.setAtk(e, cap.getOffhandCool());
        cap.setOffhandCool(tssl);
    }

    public static void taoWeaponAttack(Entity targetEntity, EntityLivingBase elb, ItemStack stack, boolean main, boolean updateOff) {
        taoWeaponAttack(targetEntity, elb, stack, main, updateOff, causeLivingDamage(elb));
    }

    public static void taoWeaponAttack(Entity targetEntity, EntityLivingBase elb, ItemStack stack, boolean main, boolean updateOff, DamageSource ds) {
        if (elb instanceof EntityPlayer)
            taoWeaponAttack(targetEntity, (EntityPlayer) elb, stack, main, updateOff, ds);
        else
            targetEntity.attackEntityFrom(ds, (float) NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_DAMAGE, elb, main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
    }

    /*public static void sweep(EntityLivingBase e, Entity ignore, EnumHand h, double reach) {
        ItemStack stack = e.getHeldItem(h);
        WeaponStats.SWEEPSTATE s = getSweepState(e);
        WeaponStats.SweepInfo info = WeaponStats.getSweepInfo(stack, s);
        //apply instantaneous damage multiplier
        SkillUtils.modifyAttribute(e, Attributes.ATTACK_DAMAGE, main, info.getDamageScale() - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        sweep(e, ignore, h, info.getType(), reach, info.getBase(), info.getScaling());
        SkillUtils.removeAttribute(e, Attributes.ATTACK_DAMAGE, main);
    }

    public static void sweep(EntityLivingBase e, Entity ignore, EnumHand h, WeaponStats.SWEEPTYPE type, double reach, double base, double scaling) {
        //no go cases
        if (!GeneralConfig.betterSweep) return;//a shame, but alas
        if (!CombatData.getCap(e).isCombatMode()) return;
        if (CombatData.getCap(e).getHandBind(h) > 0) return;//don't even try dude
        if (h == EnumHand.OFF_HAND) {
            swapHeldItems(e);
            CombatData.getCap(e).setOffhandAttack(true);
        }
        if (!PermissionData.getCap(e).canSweep()) type = WeaponStats.SWEEPTYPE.NONE;
        double radius;

        SweepEvent sre = new SweepEvent(e, h, e.getMainHandItem(), type, base, scaling);
        MinecraftForge.EVENT_BUS.post(sre);
        base = sre.getBase();
        scaling = sre.getScaling();
        radius = sre.getFinalizedWidth();
        type = sre.getType();
        if (sre.isCanceled() || type == WeaponStats.SWEEPTYPE.NONE || radius == 0) {
            //no go, swap items back and stop
            if (h == EnumHand.OFF_HAND) {
                swapHeldItems(e);
                CombatData.getCap(e).setOffhandAttack(false);
            }
            return;
        }
        if (e.getMainHandItem().getCapability(CombatManipulator.CAP).isPresent())
            radius = e.getMainHandItem().getCapability(CombatManipulator.CAP).resolve().get().sweepArea(e, e.getMainHandItem());
        float charge = Math.max(CombatUtils.getCooledAttackStrength(e, EnumHand.MAIN_HAND, 0.5f), CombatData.getCap(e).getCachedCooldown());
        boolean hit = false;
        isSweeping = ignore != null;
        Vec3 starting = ignore == null ? GeneralUtils.raytraceAnything(e.level(), e, reach).getLocation() : ignore.position();
        //grab everyone in "range"
        for (Entity target : e.level().getEntities(e, e.getBoundingBox().inflate(reach * 2))) {
            if (target == e) continue;
            if (target.hasPassenger(e) || e.hasPassenger(target)) continue;//poor horse
            if (target == ignore) {
                if (radius > 0)
                    hit = true;
                continue;
            }
            if (!e.hasLineOfSight(target)) continue;
            //type specific sweep checks
            switch (type) {
                case CONE -> {
                    if (!GeneralUtils.isFacingEntity(e, target, (int) radius, 40)) continue;
                    if (GeneralUtils.getDistSqCompensated(e, target) > reach * reach) continue;
                }
                case CLEAVE -> {
                    if (!GeneralUtils.isFacingEntity(e, target, 40, (int) radius)) continue;
                    if (GeneralUtils.getDistSqCompensated(e, target) > reach * reach) continue;
                }
                case IMPACT -> {
                    if (GeneralUtils.getDistSqCompensated(target, starting) > radius * radius) continue;
                }
                case CIRCLE -> {
                    if (GeneralUtils.getDistSqCompensated(target, e) > radius * radius) continue;
                }
                case LINE -> {
                    Vec3 eye = e.getEyePosition(0.5F);
                    Vec3 look = e.getLookAngle();
                    Vec3 start = eye.add(look.scale(radius));
                    Vec3 end = eye.add(look.scale(reach));
                    if (!target.getBoundingBox().inflate(radius).intersects(start, end)) continue;
                }
            }

            CombatUtils.setHandCooldown(e, EnumHand.MAIN_HAND, charge, false);
            hit = true;
            if (e instanceof Player)
                ((Player) e).attack(target);
            else e.doHurtTarget(target);
            isSweeping = true;
        }
        //if (e instanceof Player && hit) {
        //play sweep particles in different ways
        ParticleType<ScalingParticleType> particle = FootworkParticles.SWEEP.get();
        Vec3 look = e.getLookAngle();
        starting = e.getEyePosition().add(look.scale(reach));
        float offset = 0;//(float) look.scale(reach).y;
        switch (type) {
            case LINE -> {
                particle = FootworkParticles.LINE.get();
                //ParticleUtils.playSweepParticle(particle, e, e.getEyePosition().add(look.normalize()), 0, radius, Color.WHITE, offset);
            }
            case CIRCLE -> {
                starting = e.position().add(look.x, 0, look.z);
                particle = FootworkParticles.CIRCLE.get();
                offset = e.getEyeHeight() / 2;
            }
            case CONE -> {
                radius = Math.tan(GeneralUtils.rad((float) radius / 2)) * reach;
                particle = h == EnumHand.OFF_HAND ? FootworkParticles.SWEEP_LEFT.get() : FootworkParticles.SWEEP.get();
            }
            case CLEAVE -> {
                particle = FootworkParticles.CLEAVE.get();
                radius = Math.tan(GeneralUtils.rad((float) radius / 2)) * reach;
            }
            case IMPACT -> {
                particle = FootworkParticles.IMPACT.get();
                offset = 0;
            }
        }
        ParticleUtils.playSweepParticle(particle, e, starting, 0, radius, sre.getColor(), offset);
        e.level().playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, e.getSoundSource(), 1.0F, 1.0F);
        //}
        isSweeping = false;
        if (h == EnumHand.OFF_HAND) {
            swapHeldItems(e);
            CombatData.getCap(e).setOffhandAttack(false);
        }
    }*/

    /**
     * copy-pasted from EntityPlayer, as-is.
     */
    public static void taoWeaponAttack(Entity targetEntity, EntityPlayer player, ItemStack stack, boolean main, boolean updateOff, DamageSource ds) {
        if (targetEntity == null) return;
        if (updateOff) {
            TaoCasterData.getTaoCap(player).setOffhandAttack(!main);
        }
        if (onPlayerAttackTarget(player, targetEntity, main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND) && targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(player)) {
                IAttributeInstance toUse = new AttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                IAttributeInstance att = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                toUse.setBaseValue(att.getBaseValue());
                final Multimap<String, AttributeModifier> exclude = player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
                for (AttributeModifier am : att.getModifiers()) {
                    if (!exclude.containsValue(am))
                        toUse.applyModifier(am);
                    //System.out.println(toUse.getAttributeValue());
                }
                for (AttributeModifier atm : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()))
                    toUse.applyModifier(atm);
                float damage = (float) toUse.getAttributeValue();
                float damageMods;

                if (targetEntity instanceof EntityLivingBase) {
                    damageMods = EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase) targetEntity).getCreatureAttribute());
                } else {
                    damageMods = EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                }

                float cooldown = main ?
                        player.getCooledAttackStrength(0.5F) :
                        getCooledAttackStrengthOff(player, 0.5f);
                //System.out.println(cooldown+String.valueOf(player.world.isRemote));
                damage = damage * (0.2F + cooldown * cooldown * 0.8F);
                damageMods = damageMods * cooldown;
                if (main)
                    player.resetCooldown();
                else TaoCasterData.getTaoCap(player).setOffhandCool(0);

                if (damage > 0.0F || damageMods > 0.0F) {
                    boolean recharged = cooldown > 0.9F;
                    boolean knockback = false;
                    int kbamnt = 0;
                    kbamnt = kbamnt + EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);

                    if (player.isSprinting() && recharged) {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        ++kbamnt;
                        knockback = true;
                    }

                    boolean crit = recharged && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
                    crit = crit && !player.isSprinting();

                    net.minecraftforge.event.entity.player.CriticalHitEvent critevent = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, targetEntity, crit, crit ? 1.5F : 1.0F);
                    crit = critevent != null;
                    if (crit) {
                        damage *= critevent.getDamageModifier();
                    }

                    damage = damage + damageMods;
                    boolean sword = false;
                    double speed = (player.distanceWalkedModified - player.prevDistanceWalkedModified);

                    if (recharged && !crit && !knockback && player.onGround && speed < (double) player.getAIMoveSpeed()) {

                        if (stack.getItem() instanceof ItemSword) {
                            sword = true;
                        }
                    }

                    float health = 0.0F;
                    boolean burning = false;
                    int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);

                    if (targetEntity instanceof EntityLivingBase) {
                        health = ((EntityLivingBase) targetEntity).getHealth();

                        if (fireLevel > 0 && !targetEntity.isBurning()) {
                            burning = true;
                            targetEntity.setFire(1);
                        }
                    }

                    double motionX = targetEntity.motionX;
                    double motionY = targetEntity.motionY;
                    double motionZ = targetEntity.motionZ;
                    boolean dealDamage = targetEntity.attackEntityFrom(ds, damage);

                    if (dealDamage) {
                        if (kbamnt > 0) {
                            if (targetEntity instanceof EntityLivingBase) {
                                NeedyLittleThings.knockBack(((EntityLivingBase) targetEntity), player, (float) kbamnt * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), 0, (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)), false);
                                //((EntityLivingBase) targetEntity).knockBack(player, (float) kbamnt * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                            } else {
                                targetEntity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float) kbamnt * 0.5F), 0.1D, (double) (MathHelper.cos(player.rotationYaw * 0.017453292F) * (float) kbamnt * 0.5F));
                            }

                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (sword) {
                            float sweepratio = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;

                            for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                                if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && NeedyLittleThings.getDistSqCompensated(player, entitylivingbase) < 9.0D) {
                                    entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                    entitylivingbase.attackEntityFrom(ds, sweepratio);
                                }
                            }

                            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                            player.spawnSweepParticles();
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                            ((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = motionX;
                            targetEntity.motionY = motionY;
                            targetEntity.motionZ = motionZ;
                        }

                        if (crit) {
                            player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                            player.onCriticalHit(targetEntity);
                        }

                        if (!crit && !sword) {
                            if (recharged) {
                                player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (damageMods > 0.0F) {
                            player.onEnchantmentCritical(targetEntity);
                        }

                        player.setLastAttackedEntity(targetEntity);

                        if (targetEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        Entity entity = targetEntity;

                        if (targetEntity instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;

                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (!stack.isEmpty() && entity instanceof EntityLivingBase) {
                            ItemStack beforeHitCopy = stack.copy();
                            stack.hitEntity((EntityLivingBase) entity, player);

                            if (stack.isEmpty()) {
                                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, EnumHand.MAIN_HAND);
                                player.setHeldItem(main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                            float dealtdamage = health - ((EntityLivingBase) targetEntity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(dealtdamage * 10.0F));

                            if (fireLevel > 0) {
                                targetEntity.setFire(fireLevel * 4);
                            }

                            if (player.world instanceof WorldServer && dealtdamage > 2.0F) {
                                int k = (int) ((double) dealtdamage * 0.5D);
                                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.addExhaustion(0.1F);
                    } else {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                        if (burning) {
                            targetEntity.extinguish();
                        }
                    }
                }
//                    if (updateOff) {
//                        TaoWeapon.off = false;
//                    }
            }
        }
        TaoCasterData.getTaoCap(player).setOffhandAttack(false);
    }

    public static void updateLists() {
        DEFAULT = new CombatInfo(CombatConfig.defaultMultiplierPostureAttack, CombatConfig.defaultMultiplierPostureDefend, false);
        combatList = new HashMap<>();
        archetypes = new HashMap<>();
        customPosture = new HashMap<>();
        if (configFolder != null) {
            File folder = new File(TaoCombatUtils.configFolder);
            String sep = System.getProperty("file.separator");
            if (!folder.exists())
                folder.mkdir();
            File stats = new File(TaoCombatUtils.configFolder + sep + "stats");
            if (!stats.exists())
                stats.mkdir();
            File tags = new File(TaoCombatUtils.configFolder + sep + "tags");
            if (!tags.exists())
                tags.mkdir();
            try {
                for (File json : stats.listFiles(a ->
                        Files.getFileExtension(a.getName()).equals("json"))) {
                    JsonObject je = gson.fromJson(new JsonReader(new FileReader(json)), JsonObject.class);
                    je.entrySet().forEach(entry -> {
                        String name = entry.getKey();
                        if (name.startsWith("#")) {//register tags separately
                            try {
                                name = name.substring(1);
                                if (name.contains(":"))
                                    name = name.substring(name.lastIndexOf(":") + 1);
                                JsonObject obj = entry.getValue().getAsJsonObject();
                                CombatInfo put = parseMeleeInfo(obj);
                                archetypes.put(name, put);
                            } catch (Exception x) {
                                Taoism.logger.error("malformed json under " + name + "!");
                                x.printStackTrace();
                            }
                            return;
                        }
                        ResourceLocation i = new ResourceLocation(name);
                        Item item = ForgeRegistries.ITEMS.getValue(i);
                        if (item == null || item == Items.AIR) {
                            return;
                        }
                        try {
                            JsonObject obj = entry.getValue().getAsJsonObject();
                            CombatInfo put = parseMeleeInfo(obj);
                            combatList.put(item, put);
                        } catch (Exception x) {
                            Taoism.logger.error("malformed json under " + name + "!");
                            x.printStackTrace();
                        }
                    });
                }
                for (File json : tags.listFiles(file -> Files.getFileExtension(file.getName()).equals("json"))) {
                    JsonObject je = gson.fromJson(new JsonReader(new FileReader(json)), JsonObject.class);
                    je.getAsJsonArray("values").forEach(a -> {
                        JsonObject entry = a.getAsJsonObject();
                        String name = entry.get("id").getAsString();
                        ResourceLocation i = new ResourceLocation(name);
                        Item item = ForgeRegistries.ITEMS.getValue(i);
                        if (item == null || item == Items.AIR) {
                            return;
                        }
                        String tag = Files.getNameWithoutExtension(json.getName());
                        if (archetypes.containsKey(tag)) {
                            CombatInfo put = archetypes.get(tag).clone();
                            combatList.put(item, put);
                        }
                        if (tag.equals("two_handed")) {
                            combatList.get(item).setTwoHanded(true);
                        }
                    });
                }
            } catch (Exception ignored) {

            }
        }
        archetypes.clear();//save space
        for (String s : CombatConfig.combatItems) {
            String[] val = s.split(",");
            String name = val[0];
            float attack = CombatConfig.defaultMultiplierPostureAttack;
            float defend = CombatConfig.defaultMultiplierPostureDefend;
            boolean shield = false;
            if (val.length > 1)
                try {
                    attack = Float.parseFloat(val[1].trim());
                } catch (NumberFormatException ignored) {
                }
            if (val.length > 2)
                try {
                    defend = Float.parseFloat(val[2].trim());
                } catch (NumberFormatException ignored) {
                }
            if (val.length > 3)
                shield = Boolean.parseBoolean(val[3].trim());
            if (Item.getByNameOrId(name) != null)
                combatList.put(Item.getByNameOrId(name), new CombatInfo(attack, defend, shield));
        }
        for (String s : CombatConfig.customPosture) {
            try {
                String[] val = s.split(",");
                customPosture.put(val[0], Float.parseFloat(val[1]));
            } catch (Exception e) {
                Taoism.logger.warn("improperly formatted custom posture definition " + s + "!");
            }
        }
    }

    public enum SWEEPTYPE {
        NONE,
        CONE,//horizontal fan area in front of the entity up to max range, base and scale add angle
        CLEAVE,//cone but vertical
        LINE,//1 block wide line up to max range, base and scale add to thickness
        IMPACT,//splash at point of impact or furthest distance if no mob aimed, base and scale add radius
        CIRCLE//splash with entity as center, ignores range, base and scale add radius
    }

    public enum SWEEPSTATE {
        STANDING,
        //RISING, //may implement some day
        FALLING,
        SNEAKING,
        SPRINTING,//also while swimming
        RIDING //no speed requirement
    }

    private static class CombatInfo {
        private float attackPostureMultiplier;
        private float defensePostureMultiplier;
        private boolean isShield;
        private boolean twoHanded;

        private CombatInfo(float attack, float defend, boolean shield) {
            attackPostureMultiplier = attack;
            defensePostureMultiplier = defend;
            isShield = shield;
        }

        public CombatInfo setTwoHanded(boolean two) {
            twoHanded = two;
            return this;
        }

        public CombatInfo clone() {
            return new CombatInfo(attackPostureMultiplier, defensePostureMultiplier, isShield).setTwoHanded(twoHanded);
        }
    }
}
