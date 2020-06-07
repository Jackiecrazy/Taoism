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
import net.minecraft.util.EnumHand;
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
     */

    private static final boolean[] harvestList = {false, false, true, false};

    public Pollaxe() {
        super(3, 1.2, 6, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return getHand(stack) == EnumHand.MAIN_HAND && !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return isOffhandEmpty(is);
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && !w.isRemote) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (elb.getLastAttackedEntity() != null && getLastAttackedRangeSq(stack) < NeedyLittleThings.getDistSqCompensated(elb, elb.getLastAttackedEntity())) {
                setLastAttackedRangeSq(stack, 0);
            }
        }
    }

    @Override
    protected double speed(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND ? (super.speed(stack)+4) * 3-4 : super.speed(stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.half") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.leap"));
        tooltip.add(I18n.format("pollaxe.cleave"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("pollaxe.cleave.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.swipe"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("pollaxe.swipe.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("pollaxe.riposte"));
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
        if(getHand(item) == EnumHand.OFF_HAND && !getLastMove(item).isLeftClick() && getLastAttackedRangeSq(item) != 0){
            return 2;
        }
        return getHand(item) == EnumHand.OFF_HAND ? 0 : 3;
    }

    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack is) {
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(defender, other, 0.9f);
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (getHand(item) == EnumHand.OFF_HAND && !getLastMove(item).isLeftClick() && getLastAttackedRangeSq(item) != 0) {
            return super.postureDealtBase(attacker, defender, item, amount) * 2;
        }
        return super.postureDealtBase(attacker, defender, item, amount);
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        super.afterSwing(elb, is);
        EnumHand other = getHand(is) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item) == EnumHand.OFF_HAND ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack) {
        float crit = 1;
        if (getHand(stack) == EnumHand.MAIN_HAND && getLastMove(stack).isLeftClick() && getLastAttackedRangeSq(stack) != 0) {
            crit *= 1.5f;
        }
        crit = attacker.motionY < 0 ? crit * 1.5f : crit;
        return crit;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        //nerf offhand damage
        return getHand(item) == EnumHand.OFF_HAND ? 0.4f : 1f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        TaoCasterData.getTaoCap(target).consumePosture(orig, true);
        return 0;
    }

    private float getLastAttackedRangeSq(ItemStack is) {
        return gettagfast(is).getFloat("lastAttackedRange");
    }

    private void setLastAttackedRangeSq(ItemStack item, float range) {
        if (range != 0f) {
            gettagfast(item).setFloat("lastAttackedRange", range);
        } else {
            gettagfast(item).removeTag("lastAttackedRange");
        }
    }


}
