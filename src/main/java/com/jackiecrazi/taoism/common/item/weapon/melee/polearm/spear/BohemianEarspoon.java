package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.physics.EntityOrbitDummy;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class BohemianEarspoon extends TaoWeapon {
    /*
    A weapon all about negotiating distance. High range, medium power and defense, low combo and speed
    Normal attack's crit modifier is determined by the difference between the distance of you and the target in the last 2 attacks
    Alt attack will consume this buff to do a small AoE swipe
    Riposte: for the next 7 seconds, your attacks mark a single target. The target cannot approach you unless they first exit your attack range.
        Either your movements force the target to move with you or the target's movements move you as well, depending on size
    about prime time to add relative distance manipulators, you know?

    Execution:
    circle slash knocking enemies back, then impale the next enemy hit above you, dealing 2x bleed damage per second, until they die
    others will be enraptured (bound+rooted) for the duration and spooked when the enemy dies
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE
    };

    public BohemianEarspoon() {
        super(2, 1.4, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && onHand) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (!w.isRemote) {
                if (getCurrentMove(stack).isLeftClick() && getLastAttackedEntity(w, stack) != null && getLastAttackedRangeSq(stack) != 0) {
                    Entity target = getLastAttackedEntity(w, stack);
                    if (NeedyLittleThings.isFacingEntity(elb, target, 90)) {
                        if (target.getDistanceSq(elb) < getLastAttackedRangeSq(stack)) {
                            if (target.getDistanceSq(elb) < getLastAttackedRangeSq(stack) / 2 && target instanceof EntityLivingBase) {
                                //too close! Rip out innards for double damage
                                target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(elb), 2f * (float) getDamageAgainst(elb, (EntityLivingBase) target, stack));
                                setLastAttackedRangeSq(elb, stack, 0);
                            }
                            //a new challenger is approaching!
                            target.motionX += (target.posX - (elb.posX + 0.5D)) * 0.05;
                            target.motionY += ((target.posY) - elb.posY) * 0.05;
                            target.motionZ += (target.posZ - (elb.posZ + 0.5D)) * 0.05;
                            target.velocityChanged = true;
                        }
                    } else {
                        setLastAttackedRangeSq(elb, stack, 0);
                    }
                }
            }
            if (isCharged(elb, stack)) {
                Entity target = w.getEntityByID(getBuff(stack, "flipOverID"));
                if (target instanceof EntityLivingBase) {
                    if (getBuff(stack, "stop") == 0)
                        if (!w.isRemote) {
                            if (getBuff(stack, "rotate") >= 18) {
                                setBuff(elb, stack, "stop", 1);
                            } else
                                splash(elb, stack, 30);
                            setBuff(elb, stack, "rotate", getBuff(stack, "rotate") + 1);
                        } else {
                            elb.rotationYaw += 20;
                            elb.rotationPitch = 0;
                        }
                    else {
                        if (getBuff(stack, "dummyID") == -1) {
                            EntityOrbitDummy epd = new EntityOrbitDummy(w, elb, target);
                            epd.motionY = 1;
                            setBuff(elb, stack, "dummyID", epd.getEntityId());
                            w.spawnEntity(epd);
                            //target.setPosition(attacker.posX, attacker.posY+5, attacker.posZ);
                            TaoCasterData.getTaoCap(elb).setForcedLookAt(target);
                        }
                        EntityLivingBase targ = (EntityLivingBase) target;
                        if (getBuff(stack, "align") == 1 || (Math.abs(targ.posZ - e.posZ) < 0.5 && Math.abs(targ.posX - e.posX) < 0.5)) {
                            setBuff(elb, stack, "align", 1);
                            targ.setPositionAndUpdate(e.posX, targ.posY, e.posZ);
                            final int impaleTimer = getBuff(stack, "impaleTimer");
                            if (targ.ticksExisted % 20 == 0) {
                                targ.hurtResistantTime = 0;
                                targ.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(elb), impaleTimer * (float) getDamageAgainst(elb, targ, stack));
                                TaoCombatUtils.attack(elb, targ, EnumHand.MAIN_HAND, DamageSourceBleed.causeEntityBleedingDamage(elb));
                                setBuff(elb, stack, "impaleTimer", impaleTimer + 1);
                            }
                            if (impaleTimer > 9 || targ.getHealth() <= 0) {
                                dischargeWeapon(elb, stack);
                                TaoCasterData.getTaoCap(targ).setRootTime(0);
                            } else TaoCasterData.getTaoCap(targ).setRootTime(1000);
                            if (w instanceof WorldServer) {
                                ((WorldServer) w).spawnParticle(EnumParticleTypes.DRIP_LAVA, targ.posX, targ.posY + targ.height / 2, targ.posZ, 1, targ.width / 4, targ.height / 4, targ.width / 4, 0.5f);
                            }
                            for (EntityLivingBase audience : w.getEntitiesWithinAABB(EntityLivingBase.class, e.getEntityBoundingBox().grow(16))) {
                                if (audience != e) {
                                    if (targ.getHealth() > 0 && impaleTimer < 10) {
                                        TaoCasterData.getTaoCap(audience).setBindTime(100);
                                        TaoCasterData.getTaoCap(audience).setRootTime(100);
                                    } else {
                                        TaoCasterData.getTaoCap(audience).setBindTime(0);
                                        TaoCasterData.getTaoCap(audience).setRootTime(0);
                                        TaoPotionUtils.fear(audience, elb, 100 + (20 - impaleTimer) * 10);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("bohear.dist"));
        tooltip.add(I18n.format("bohear.buff"));
        tooltip.add(I18n.format("bohear.rip"));
        tooltip.add(I18n.format("bohear.bash"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && getLastAttackedRangeSq(stack) != 0) {
            splash(attacker, stack, 120);
            setLastAttackedRangeSq(attacker, stack, 0);
        }
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : 2;
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        super.chargeWeapon(attacker, item);
        setLastAttackedRangeSq(attacker, item, 0);
        setBuff(attacker, item, "rotate", 0);
        setBuff(attacker, item, "stop", 0);
        setBuff(attacker, item, "flipOverID", -1);
        setBuff(attacker, item, "dummyID", -1);
        setBuff(attacker, item, "impaleTimer", 0);
        setBuff(attacker, item, "align", 0);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        setBuff(elb, item, "stop", 1);
        TaoCasterData.getTaoCap(elb).setForcedLookAt(null);
        TaoCasterData.getTaoCap(elb).consumeQi(4, 5);
        TaoCasterData.getTaoCap(elb).stopRecordingDamage(elb);
    }

    @Override
    protected void performScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, long l, int interval) {
    }

    @Override
    protected void endScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, int interval) {
        if (victim instanceof EntityLivingBase) {
            TaoCasterData.getTaoCap(elb).setForcedLookAt(null);
            victim.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(elb), 2f * (float) getDamageAgainst(elb, (EntityLivingBase) victim, stack));
        }
        elb.setPosition(elb.posX + (victim.posX - elb.posX) / 2, elb.posY + (victim.posY - elb.posY) / 2, elb.posZ + (victim.posZ - elb.posZ) / 2);
        dischargeWeapon(elb, stack);
    }

    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return Event.Result.DENY;
    }

//    public float newCooldown(EntityLivingBase elb, ItemStack item) {
//        if (getHand(item) == EnumHand.MAIN_HAND) {
//            float max = Math.max(getLastLastAttackedRangeSq(item), getLastAttackedRangeSq(item));
//            float min = Math.min(getLastLastAttackedRangeSq(item), getLastAttackedRangeSq(item));
//            if (max != 0)
//                return (min / max) * 0.5f;
//        }
//        return 0f;
//    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float max = Math.max((float) attacker.getDistanceSq(target), getLastAttackedRangeSq(item));
        float min = Math.min((float) attacker.getDistanceSq(target), getLastAttackedRangeSq(item));
        float lastAttackRange = 1 + min / (max * 2f);
        return (getHand(item) == EnumHand.MAIN_HAND ? lastAttackRange : 0.5f);
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack) && getHand(stack) == EnumHand.OFF_HAND) return orig * 2;
        return super.knockback(attacker, target, stack, orig);
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack)) {
            //impale and raise above
            if (getBuff(stack, "flipOverID") == -1) {
                setBuff(attacker, stack, "flipOverID", target.getEntityId());
                TaoCasterData.getTaoCap(attacker).startRecordingDamage();
            }
        }
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        }
    }
}
