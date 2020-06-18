package com.jackiecrazi.taoism.api;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.handler.TaoisticEventHandler;
import com.jackiecrazi.taoism.networking.PacketUpdateSize;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NeedyLittleThings {
    /**
     * Copied from EntityArrow, because kek.
     */
    public static final Predicate<Entity> VALID_TARGETS = Predicates.and(EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE, e -> e != null && !(e instanceof EntityHanging) && e.canBeCollidedWith());

    public static boolean isMeleeDamage(DamageSource ds) {
        return isPhysicalDamage(ds) && !ds.isProjectile();
    }

    public static boolean isPhysicalDamage(DamageSource ds) {
        return !ds.isFireDamage() && !ds.isMagicDamage() && !ds.isUnblockable() && !ds.isExplosion() && !ds.isDamageAbsolute();
    }

    public static double getSpeedSq(Entity e) {
        return e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ;
    }

    public static void setSize(Entity e, float width, float height) {
        if (e instanceof IEntityMultiPart || e instanceof MultiPartEntityPart) {
            return; //let the sleeping dragons lie
        }
        if (width != e.width || height != e.height) {
            float f = e.width;
            e.width = width;
            e.height = height;

            if (e.width < f) {
                double d0 = (double) width / 2.0D;
                e.setEntityBoundingBox(new AxisAlignedBB(e.posX - d0, e.posY, e.posZ - d0, e.posX + d0, e.posY + (double) e.height, e.posZ + d0));
                if (!e.world.isRemote) {
//                    if(e instanceof EntityPlayerMP){
//                        Taoism.net.sendTo(new PacketUpdateSize(e.getEntityId(), e.width, e.height), (EntityPlayerMP) e);
//                    }
                    Taoism.net.sendToAllTracking(new PacketUpdateSize(e.getEntityId(), e.width, e.height), e);
                }
                return;
            }

            AxisAlignedBB axisalignedbb = e.getEntityBoundingBox();
            e.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) e.width, axisalignedbb.minY + (double) e.height, axisalignedbb.minZ + (double) e.width));

            if (!e.world.isRemote) {
                if (e.width > f) {
                    e.move(MoverType.SELF, (f - e.width), 0.0D, (f - e.width));
                }
//                if(e instanceof EntityPlayerMP){
//                    Taoism.net.sendTo(new PacketUpdateSize(e.getEntityId(), e.width, e.height), (EntityPlayerMP) e);
//                }
                Taoism.net.sendToAllTracking(new PacketUpdateSize(e.getEntityId(), e.width, e.height), e);
            }
        }
//        if(!e.world.isRemote)
//        Taoism.net.sendToAllTracking(new PacketUpdateSize(e.getEntityId(),e.width,e.height),e);
    }

    /**
     * knocks the target back, with regards to the attacker's relative angle to the target, and adding y knockback
     */
    public static void knockBack(Entity to, Entity from, float strength) {
        Vec3d distVec = to.getPositionVector().subtractReverse(from.getPositionVector()).normalize();
        if (to instanceof EntityLivingBase) {
            knockBack((EntityLivingBase) to, from, strength, distVec.x, distVec.y, distVec.z);
        } else {
            //eh
            to.motionX = distVec.x * strength;
            to.motionY = distVec.y * strength;
            to.motionZ = distVec.z * strength;
            to.velocityChanged = true;
        }
        //knockBack(to, from, strength, MathHelper.sin(rad(from.rotationYaw)), -MathHelper.cos(rad(from.rotationYaw)));
    }

    /**
     * knockback in EntityLivingBase except it makes sense and the resist is factored into the event
     */
    public static void knockBack(EntityLivingBase to, Entity from, float strength, double xRatio, double yRatio, double zRatio) {
        TaoisticEventHandler.modCall = true;
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(to, from, strength * (float) (1 - to.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()), xRatio, zRatio);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        xRatio = event.getRatioX();
        zRatio = event.getRatioZ();
        if (strength != 0f) {
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
            } else if (yRatio != 0) {
                to.motionY /= 2.0D;
                to.motionY -= yRatio / (double) pythagora * (double) strength;
            }
            to.velocityChanged = true;
        }
    }

    public static void swapItemInHands(EntityLivingBase elb) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        elb.setHeldItem(EnumHand.OFF_HAND, main);
        elb.setHeldItem(EnumHand.MAIN_HAND, off);
        TaoCombatUtils.rechargeHand(elb, EnumHand.MAIN_HAND, TaoCombatUtils.getHandCoolDown(elb, EnumHand.OFF_HAND));
        TaoCombatUtils.rechargeHand(elb, EnumHand.OFF_HAND, TaoCombatUtils.getHandCoolDown(elb, EnumHand.MAIN_HAND));
    }

    public static float getCooledAttackStrength(EntityLivingBase elb, float adjustTicks) {
        if (elb instanceof EntityPlayer) return ((EntityPlayer) elb).getCooledAttackStrength(adjustTicks);
        return MathHelper.clamp(((float) Taoism.getAtk(elb) + adjustTicks) / getCooldownPeriod(elb), 0.0F, 1.0F);
    }

    public static float getCooldownPeriod(EntityLivingBase elb) {
        return (float) (1.0D / getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, EnumHand.MAIN_HAND) * 20.0D);
    }

    public static double getAttributeModifierHandSensitive(IAttribute ia, EntityLivingBase elb, EnumHand hand) {
        IAttributeInstance a = elb.getEntityAttribute(ia);
        if (a == null) return 1;
        IAttributeInstance toUse = new AttributeMap().registerAttribute(ia);
        toUse.setBaseValue(a.getBaseValue());
        for (AttributeModifier am : a.getModifiers()) {
            if (!am.getName().equals("Weapon modifier")) {
                toUse.applyModifier(am);
            }
        }
        for (AttributeModifier am : elb.getHeldItem(hand).getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(ia.getName())) {
            if (!toUse.hasModifier(am))
                toUse.applyModifier(am);
        }
        return toUse.getAttributeValue();
    }

    /**
     * returns true if entity2 is within a (angle) degree sector in front of entity1
     */
    public static boolean isFacingEntity(Entity entity1, Entity entity2, int angle) {
        Vec3d posVec = entity2.getPositionVector();
        Vec3d lookVec = entity1.getLook(1.0F);
        Vec3d relativePosVec = posVec.subtractReverse(entity1.getPositionVector().addVector(0, entity1.getEyeHeight(), 0)).normalize();
        //relativePosVec = new Vec3d(relativePosVec.x, 0.0D, relativePosVec.z);

        double dotsq = ((relativePosVec.dotProduct(lookVec) * Math.abs(relativePosVec.dotProduct(lookVec))) / (relativePosVec.lengthSquared() * lookVec.lengthSquared()));
        double cos = MathHelper.cos(rad(angle / 2));
        return dotsq < -(cos * cos);
    }

    public static float rad(float angle) {
        return angle * (float) Math.PI / 180;
    }

    /**
     * returns true if entity is within a 90 degree sector behind the target
     */
    public static boolean isBehindEntity(Entity entity, Entity target) {
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

    public static float getCooledAttackStrengthOff(EntityLivingBase elb, float adjustTicks) {
        return MathHelper.clamp(((float) TaoCasterData.getTaoCap(elb).getOffhandCool() + adjustTicks) / getCooldownPeriodOff(elb), 0.0F, 1.0F);
    }

    public static float getCooldownPeriodOff(EntityLivingBase elb) {
        return (float) (1.0D / getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, EnumHand.OFF_HAND) * 20.0D);
    }

    /**
     * knockback in EntityLivingBase except it makes sense and the resist is factored into the event
     */
    public static void knockBack(EntityLivingBase to, Entity from, float strength, double xRatio, double zRatio) {
        knockBack(to, from, strength, xRatio, 0, zRatio);
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

    public static Entity raytraceEntity(World world, EntityLivingBase attacker, double range) {
        Vec3d start = attacker.getPositionEyes(0.5f);
        Vec3d look = attacker.getLookVec().scale(range * 2);
        Vec3d end = start.add(look);
        Entity entity = null;
        List<Entity> list = world.getEntitiesInAABBexcluding(attacker, attacker.getEntityBoundingBox().expand(look.x, look.y, look.z).grow(1.0D), null);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != attacker) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox();
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
                if (raytraceresult != null) {
                    double d1 = getDistSqCompensated(entity1, attacker);

                    if ((d1 < d0 || d0 == 0.0D) && d1 < range * range) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Entity to) {
        double x = from.posX - to.posX;
        x = Math.max(Math.abs(x) - ((from.width / 2) + (to.width / 2)), 0);
        //stupid inconsistent game
        double y = (from.posY + from.height / 2) - (to.posY + to.height / 2);
        y = Math.max(Math.abs(y) - (from.height / 2 + to.height / 2), 0);
        double z = from.posZ - to.posZ;
        z = Math.max(Math.abs(z) - (from.width / 2 + to.width / 2), 0);
        double me = x * x + y * y + z * z;
        double you = from.getDistanceSq(to);
        return Math.min(me, you);
    }

    public static List<Entity> raytraceEntities(World world, EntityLivingBase attacker, double range) {
        Vec3d start = attacker.getPositionEyes(0.5f);
        Vec3d look = attacker.getLookVec().scale(range * 2);
        Vec3d end = start.add(look);
        ArrayList<Entity> ret = new ArrayList<>();
        List<Entity> list = world.getEntitiesInAABBexcluding(attacker, attacker.getEntityBoundingBox().expand(look.x, look.y, look.z).grow(1.0D), VALID_TARGETS::test);

        for (Entity entity1 : list) {
            if (entity1 != attacker && getDistSqCompensated(attacker, entity1) < range * range) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox();
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
                if (raytraceresult != null) {
                    ret.add(entity1);
                }
            }
        }
        return ret;
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
