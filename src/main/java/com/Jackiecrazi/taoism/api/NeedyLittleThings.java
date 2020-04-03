package com.jackiecrazi.taoism.api;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.handler.TaoisticEventHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NeedyLittleThings {
    public final static UUID MODIFIER_UUID = UUID.fromString("294093da-54f0-4c1b-9dbb-13b77534a84c");
    /**
     * Copied from EntityArrow, because kek.
     */
    private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
        public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_.canBeCollidedWith();
        }
    });

    public static double getAttributeModifierHandSensitive(IAttribute ia, EntityLivingBase elb, EnumHand hand) {
        IAttributeInstance a = elb.getEntityAttribute(ia);
        for(AttributeModifier am: a.getModifiers()){
            if(!am.getName().equals("Weapon Modifier")){
                a.applyModifier(am);
            }
        }
        for(AttributeModifier am:elb.getHeldItem(hand).getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(ia.getName())){
            a.applyModifier(am);
        }
        return a.getAttributeValue();
    }

    /**
     * increases the potion amplifier on the entity, with options on the duration
     */
    public static PotionEffect stackPot(EntityLivingBase elb, PotionEffect toAdd, POTSTACKINGMETHOD method) {
        PotionEffect pe = elb.getActivePotionEffect(toAdd.getPotion());
        if (pe == null) {
            return toAdd;
        }
        Potion p = toAdd.getPotion();
        int length = pe.getDuration();
        int potency = pe.getAmplifier() + 1 + toAdd.getAmplifier();

        switch (method) {
            case ADD:
                length += pe.getDuration();
                break;
            case MAXDURATION:
                length = Math.max(pe.getDuration(), toAdd.getDuration());
                break;
            case MAXPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.max(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() > toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
            case MINDURATION:
                length = Math.min(pe.getDuration(), toAdd.getDuration());
                break;
            case MINPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.min(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() < toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
        }
        return new PotionEffect(p, length, potency);
    }

    /**
     * knocks the target back, simplified call of the other knockback function because I'm too lazy to type.
     */
    public static void knockBack(EntityLivingBase to, Entity from, float strength) {
        knockBack(to, from, strength, MathHelper.sin(rad(from.rotationYaw)), -MathHelper.cos(rad(from.rotationYaw)));
    }

    /**
     * knockback in EntityLivingBase except it makes sense and the resist is factored into the event
     */
    public static void knockBack(EntityLivingBase to, Entity from, float strength, double xRatio, double zRatio) {
        TaoisticEventHandler.modCall = true;
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(to, from, strength * (float) (1 - to.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()), xRatio, zRatio);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        xRatio = event.getRatioX();
        zRatio = event.getRatioZ();
        if (strength != 0) {
            to.isAirBorne = true;
            float pythagora = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
            to.motionX /= 2.0D;
            to.motionZ /= 2.0D;
            to.motionX -= xRatio / (double) pythagora * (double) strength;
            to.motionZ -= zRatio / (double) pythagora * (double) strength;

            if (to.onGround) {
                to.motionY /= 2.0D;
                to.motionY += strength;

                if (to.motionY > 0.4000000059604645D) {
                    to.motionY = 0.4000000059604645D;
                }
            }
        }
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Entity to) {
        double x = from.posX - to.posX;
        x = Math.abs(x) - from.width / 2 - to.width / 2;
        double y = from.posY - to.posY;
        y = Math.abs(y) - from.height / 2 - to.height / 2;
        double z = from.posZ - to.posZ;
        z = Math.abs(z) - from.width / 2 - to.width / 2;
        return x * x + y * y + z * z;
    }

    public static double getOffhandAttackSpeed(EntityLivingBase attacker) {
        IAttributeInstance toUse = new AttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        IAttributeInstance att = attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        toUse.setBaseValue(att.getBaseValue());
        for (AttributeModifier am : att.getModifiers()) {
            if (!am.getName().equals("Weapon modifier"))
                toUse.applyModifier(am);
        }
        for (AttributeModifier atm : attacker.getHeldItemOffhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName()))
            toUse.applyModifier(atm);
        return toUse.getAttributeValue();
    }

    public static float getCooledAttackStrengthOff(EntityLivingBase elb, float adjustTicks) {
        return MathHelper.clamp(((float) TaoCasterData.getTaoCap(elb).getOffhandCool() + adjustTicks) / getCooldownPeriodOff(elb), 0.0F, 1.0F);
    }

    public static float getCooledAttackStrength(EntityLivingBase elb, float adjustTicks) {
        return MathHelper.clamp(((float) Taoism.getAtk(elb) + adjustTicks) / getCooldownPeriod(elb), 0.0F, 1.0F);
    }

    public static float getCooldownPeriodOff(EntityLivingBase elb) {
        return (float) (1.0D / getOffhandAttackSpeed(elb) * 20.0D);
    }

    public static float getCooldownPeriod(EntityLivingBase elb) {
        return (float) (1.0D / elb.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);
    }

    /**
     * swaps held items and their cooldowns
     */
    public static void swapItemInHands(EntityLivingBase operator) {
        ItemStack main = operator.getHeldItemMainhand().copy();
        ItemStack off = operator.getHeldItemOffhand().copy();
        operator.setHeldItem(EnumHand.MAIN_HAND, off);
        operator.setHeldItem(EnumHand.OFF_HAND, main);
        for (AttributeModifier am : main.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            operator.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(am);
        }
        for (AttributeModifier am : off.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            operator.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(am);
        }
        int offCD = TaoCasterData.getTaoCap(operator).getOffhandCool();
        int mainCD = Taoism.getAtk(operator);
        Taoism.setAtk(operator, offCD);
        TaoCasterData.getTaoCap(operator).setOffhandCool(mainCD);
    }

    /**
     * returns true if entity is within a 90 degree sector in front of the target
     */
    public static boolean isFacingEntity(EntityLivingBase entity, Entity target) {
        Vec3d posVec = entity.getPositionVector();
        Vec3d lookVec = target.getLook(1.0F);
        Vec3d relativePosVec = posVec.subtractReverse(target.getPositionVector()).normalize();
        relativePosVec = new Vec3d(relativePosVec.x, 0.0D, relativePosVec.z);

        double dotsq = ((relativePosVec.dotProduct(lookVec) * Math.abs(relativePosVec.dotProduct(lookVec))) / (relativePosVec.lengthSquared() * lookVec.lengthSquared()));
        return dotsq < -0.5D;
    }

    /**
     * returns true if entity is within a 90 degree sector behind the target
     */
    public static boolean isBehindEntity(EntityLivingBase entity, Entity target) {
        Vec3d posVec = entity.getPositionVector();
        Vec3d lookVec = target.getLook(1.0F);
        Vec3d relativePosVec = posVec.subtractReverse(target.getPositionVector()).normalize();
        relativePosVec = new Vec3d(relativePosVec.x, 0.0D, relativePosVec.z);
        double dotsq = ((relativePosVec.dotProduct(lookVec) * Math.abs(relativePosVec.dotProduct(lookVec))) / (relativePosVec.lengthSquared() * lookVec.lengthSquared()));
        return dotsq > 0.5D;
    }

    public static DamageSource causeLivingDamage(EntityLivingBase elb) {
        if (elb instanceof EntityPlayer)
            return DamageSource.causePlayerDamage((EntityPlayer) elb);
        else return DamageSource.causeMobDamage(elb);
    }

    /**
     * copy-pasted from EntityPlayer, as-is.
     */
    public static void taoWeaponAttack(Entity targetEntity, EntityPlayer player, ItemStack stack, boolean main, boolean updateOff) {
        {
            if (updateOff) {
                TaoWeapon.off = !main;
            }
            if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) return;
            if (targetEntity.canBeAttackedWithItem()) {
                if (!targetEntity.hitByEntity(player)) {
                    IAttributeInstance toUse = new AttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                    IAttributeInstance att = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                    toUse.setBaseValue(att.getBaseValue());
                    for (AttributeModifier am : att.getModifiers()) {
                        if (!am.getName().equals("Weapon modifier"))
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
                            NeedyLittleThings.getCooledAttackStrengthOff(player, 0.5f);
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
                        kbamnt = kbamnt + EnchantmentHelper.getKnockbackModifier(player);

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
                        double speed = (double) (player.distanceWalkedModified - player.prevDistanceWalkedModified);

                        if (recharged && !crit && !knockback && player.onGround && speed < (double) player.getAIMoveSpeed()) {

                            if (stack.getItem() instanceof ItemSword) {
                                sword = true;
                            }
                        }

                        float health = 0.0F;
                        boolean burning = false;
                        int fireLevel = EnchantmentHelper.getFireAspectModifier(player);

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
                        boolean dealDamage = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);

                        if (dealDamage) {
                            if (kbamnt > 0) {
                                if (targetEntity instanceof EntityLivingBase) {
                                    knockBack(((EntityLivingBase) targetEntity), player, (float) kbamnt * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
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
                                        entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), sweepratio);
                                    }
                                }

                                player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
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
                            player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                            if (burning) {
                                targetEntity.extinguish();
                            }
                        }
                    }
                    if (updateOff) {
                        TaoWeapon.off = false;
                    }
                }
            }
        }
    }

    public static TaoistPosition[] bresenham(double x1, double y1, double z1, double x2, double y2, double z2) {

        double p_x = x1;
        double p_y = y1;
        double p_z = z1;
        double d_x = x2 - x1;
        double d_y = y2 - y1;
        double d_z = z2 - z1;
        int N = (int) Math.ceil(Math.max(Math.abs(d_x), Math.max(Math.abs(d_y), Math.abs(d_z))));
        double s_x = d_x / N;
        double s_y = d_y / N;
        double s_z = d_z / N;
        //System.out.println(N);
        TaoistPosition[] out = new TaoistPosition[N];
        if (out.length == 0) {
            //System.out.println("nay!");
            return out;
        }
        out[0] = new TaoistPosition((int) p_x, (int) p_y, (int) p_z);
        for (int ii = 1; ii < N; ii++) {
            p_x += s_x;
            p_y += s_y;
            p_z += s_z;
            out[ii] = new TaoistPosition((int) p_x, (int) p_y, (int) p_z);
        }
        return out;
    }

    public static int firstCap(String str) {
        for (int i = 0; i < str.length(); i--) {
            if (Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public static String firstOredictName(ItemStack i) {
        if (i == null) return null;
        try {
            return OreDictionary.getOreName(OreDictionary.getOreIDs(i)[0]);
        } catch (IndexOutOfBoundsException e) {
            return "ingotIron";
        }
    }

    public static EntityLivingBase raytraceEntity(World world, EntityLivingBase attacker, double range) {
        Vec3d start = attacker.getPositionEyes(0.5f);
        Vec3d look = attacker.getLookVec().scale(range);
        Vec3d end = start.add(look);
        EntityLivingBase entity = null;
        List<Entity> list = world.getEntitiesInAABBexcluding(attacker, attacker.getEntityBoundingBox().expand(look.x, look.y, look.z).grow(1.0D), ARROW_TARGETS::test);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != attacker && entity1 instanceof EntityLivingBase) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox();
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
                if (raytraceresult != null) {
                    double d1 = getDistSqCompensated(entity1, attacker);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = (EntityLivingBase) entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    public static List<Entity> raytraceEntities(World world, EntityLivingBase attacker, EntityLivingBase target, double range) {
        Vec3d start = attacker.getPositionEyes(0.5f);
        Vec3d look = attacker.getLookVec().scale(range);
        Vec3d end = start.add(look);
        ArrayList<Entity> ret = new ArrayList<>();
        List<Entity> list = world.getEntitiesInAABBexcluding(attacker, attacker.getEntityBoundingBox().expand(look.x, look.y, look.z).grow(1.0D), ARROW_TARGETS::test);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != attacker && entity1 != target) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox();
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
                if (raytraceresult != null) {
                    ret.add(entity1);
                }
            }
        }
        return ret;
    }

    public static float rad(float angle) {
        return angle * (float) Math.PI / 180;
    }

    public enum POTSTACKINGMETHOD {
        ADD,
        MAXDURATION,
        MAXPOTENCY,
        MINDURATION,
        MINPOTENCY,
    }

    public static class HitResult {
        private RayTraceResult blockHit;

        private List<EntityLivingBase> entities = new ArrayList<>();

        public RayTraceResult getBlockHit() {
            return blockHit;
        }

        public void setBlockHit(RayTraceResult blockHit) {
            this.blockHit = blockHit;
        }

        public void addEntityHit(EntityLivingBase entity) {
            entities.add(entity);
        }
    }
}
