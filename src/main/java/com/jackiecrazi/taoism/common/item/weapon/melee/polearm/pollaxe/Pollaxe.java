package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Pollaxe extends TaoWeapon {
    /*
     * A two-handed axe that strikes on both ends. High combo and defense, medium range and speed, low power
     * Cue end can stab or be jammed into the opponent in a grapple, and won't be grabbed by the other guy
     * Business end can stab, chop, hook, or smash
     * Pruning this down:
     * cue end can stab into a grapple
     * axe end can chop into a hook or smash into a stab
     * either side being parried recharges other side, other hand cooldown halved after a hit
     * 3 blocks of reach, 1.5 handed, knockback is always converted into posture damage
     * Right click is a fast cue stab. Second hit without disengagement: trip, double posture damage
     * Left click is a heavy overhead swing with axe, disabling shield. Second hit without disengagement: stab, causing 1.3x piercing damage
     *
     * Oscillates
     * primary methods to counter it are to disengage and keep a parrying hand up
     *
     * Execution: hit an enemy far back and break posture on first hit (head cleave)
     * flash in front of them and stab with second hit, dealing extra damage with lost hp (devil's toothpick)
     * begin attracting nearby enemies:
     * third hit is a whirlwind slash that cuts enemies in half (ear cleaning)
     */

    private static final boolean[] harvestList = {false, false, true, false};

    public Pollaxe() {
        super(3, 1.2, 7, 1.3f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return isCharged(wielder, is) ? 3 : 1;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.5f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return getHand(stack) == EnumHand.MAIN_HAND && !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return isOffhandEmpty(is) || isDummy(is);
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && !w.isRemote) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (getHand(stack) != null) {
                if (elb.getLastAttackedEntity() != null && NeedyLittleThings.getDistSqCompensated(elb, elb.getLastAttackedEntity()) > getReach(elb, stack) * getReach(elb, stack)) {
                    setLastAttackedRangeSq(elb, stack, 0);
                }
                if (getCombo(elb, stack) == 2) {
                    //attract entities in a 12 block radius until they're 3 blocks around the dude
                    for (Entity a : w.getEntitiesWithinAABBExcludingEntity(elb, elb.getEntityBoundingBox().grow(getReach(elb, stack) * 2))) {
                        double distsq = NeedyLittleThings.getDistSqCompensated(elb, a);
                        Vec3d point = elb.getPositionVector();
                        //update the entity's relative position to the point
                        //if the distance is below expected, add outwards velocity
                        //if the distance is above expected, add inwards velocity
                        //otherwise do nothing
                        if (distsq > 4) {
                            a.motionX += (point.x - a.posX) * 0.02;
                            a.motionY += (point.y - a.posY) * 0.02;
                            a.motionZ += (point.z - a.posZ) * 0.02;
                        } else if (distsq < 1) {
                            a.motionX -= (point.x - a.posX) * 0.02;
                            a.motionY -= (point.y - a.posY) * 0.02;
                            a.motionZ -= (point.z - a.posZ) * 0.02;
                        }
                        a.velocityChanged = true;
                    }
                }
            } else {
                setLastAttackedRangeSq(elb, stack, 0);
            }
        }
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        if (isCharged(p, is)) {
            if (getCombo(p, is) == 1)
                return 99;
            if (getCombo(p, is) == 2)
                return 6;
        }
        return 3f;
    }

    @Override
    protected double speed(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND ? (super.speed(stack) + 4) * 2 - 4 : super.speed(stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.YELLOW + I18n.format("weapon.half") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.knockback"));
        tooltip.add(I18n.format("pollaxe.chop"));
        tooltip.add(I18n.format("pollaxe.stab"));
        tooltip.add(I18n.format("pollaxe.escape"));
        tooltip.add(I18n.format("pollaxe.oscillate"));
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public int getDamageType(ItemStack item) {
        if (getHand(item) == EnumHand.OFF_HAND && !getLastMove(item).isLeftClick() && getLastAttackedRangeSq(item) != 0) {
            return 2;
        }
        return getHand(item) == EnumHand.OFF_HAND ? 0 : 3;
    }

    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack is) {
        if (isTwoHanded(is)) {
            EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            TaoCombatUtils.rechargeHand(defender, other, 0.9f);
        }
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (getHand(item) == EnumHand.OFF_HAND && getLastMove(item).isValid() && !getLastMove(item).isLeftClick() && getLastAttackedRangeSq(item) != 0) {
            return super.postureDealtBase(attacker, defender, item, amount) * 2;
        }else if(getHand(item)==EnumHand.MAIN_HAND){
            return super.postureDealtBase(attacker, defender, item, amount)/2;
        }
        return super.postureDealtBase(attacker, defender, item, amount);
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        if (isTwoHanded(is)) {
            EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            TaoCombatUtils.rechargeHand(elb, other, 0.5f);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item) == EnumHand.MAIN_HAND && getLastAttackedRangeSq(item) != 0 && getLastMove(item).isValid() && getLastMove(item).isLeftClick() && target == attacker.getLastAttackedEntity() ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack) {
        float crit = 1;
        if (getHand(stack) == EnumHand.MAIN_HAND && getLastMove(stack).isLeftClick() && getLastAttackedRangeSq(stack) != 0) {
            crit *= 1.5f;
        }
        crit = !attacker.onGround ? crit * 1.5f : crit;
        return crit;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        return getHand(item) == EnumHand.OFF_HAND ? 0.4f : 1f;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack) && getCombo(attacker, stack) == 1) {//second hit
            return orig * (1 + (target.getMaxHealth() - target.getHealth()) / target.getMaxHealth());
        }
        return super.hurtStart(ds, attacker, target, stack, orig);
    }

    @Override
    public float finalDamageMods(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack) && getCombo(attacker, stack) == 2) {//third hit
            return orig + Math.min(target.getMaxHealth() / 2, orig * 5);
        }
        return orig;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getLastAttackedRangeSq(stack) == 0)
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        else setLastAttackedRangeSq(attacker, stack, 0);
        if (isCharged(attacker, stack) && getCombo(attacker, stack) == 1) {//second hit
            Vec3d tpTo = NeedyLittleThings.getPointInFrontOf(target, attacker, 2.5);
            attacker.setPositionAndUpdate(tpTo.x, tpTo.y, tpTo.z);
        }
    }

    @Override
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack) && getCombo(attacker, stack) == 2) {//third hit
            splash(attacker, stack, 360);
            dischargeWeapon(attacker, stack);
            TaoCasterData.getTaoCap(attacker).setQi(5);
        }
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack) && getCombo(attacker, stack) == 0) {
            return super.knockback(attacker, target, stack, orig) * 4;
        }
        TaoCasterData.getTaoCap(target).consumePosture(orig, true);
        return 0;
    }


}
