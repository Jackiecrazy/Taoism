package com.jackiecrazi.taoism.common.item.weapon.melee.sectional;

import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Nunchaku extends TaoWeapon {
    /*
     * A flexible blunt weapon that still hurts. High speed and defense, medium power and combo, low range
     * I'm gonna be using this to test the move system.
     * Can be either one or two handed, with different movesets for both.
     * Nunchaku will become two-handed if in main hand and offhand is not another set of nunchaku
     * after a given attack, your nunchaku will end up on a certain side:
     * top (t), middle (m) or low (l), same (s) or different (s) side
     * Top is fast, middle is versatile, low is powerful
     * Using an attack that starts on the same place as where you ended will cause a critical hit and halve subsequent cooldown
     *
     * Available moves (same side): tb smash, mb smash, mm flick, mb sweep, mm 8-spin, ?? jab, ?m recovery
     * flick is very fast, done when movement input is invalid
     * smash inflicts slowness and deals extra damage, atk+opposite+forward
     * sweep hits in radius 1 and knocks back slightly, opposite+attack
     * 8-spin hits chip damage 3 times with no knockback, back+attack
     * recovery is the only move that can be chained after sweep or smash, same+attack
     * jab is an extreme close range high posture move, sneak+attack
     *
     * Extra moves when two-handed: stm backflip (default guard), dmt updraft
     * When two-handed, you can chain without recovery due to rapidly switching hands
     * backflip interrupts enemy attacks and knocks them back very slightly, by forward/backward-alt for top or bottom strike
     * updraft switches sides, AoEs, hits through blocks and knocks back, done by strafe in other direction-alt
     *
     * Riposte(one handed): automatically perform a flick, canceling the incoming attack and disorienting the enemy slightly
     * Riposte(two handed): catch the opponent's weapon in your chain, binding 3.
     * your attacks instantly refill each other's gauges halfway and cannot be blocked for 6 seconds.
     * if you are behind your opponent and in jab range, consume this buff to inflict slow 2/infinite, gain rooted 1/infinite (no knockback),
     * and causes the opponent to lose 5 bars of oxygen per second in a choke hold.
     * Lasts until distance > 3, at which point the opponent is tripped for 50% max posture damage
     * Alternatively, by dealing enough damage to you (threshold scales with higher chi) will end this state as well
     */
    private final Move[] moves = {
            //smash
            new Move(new MoveCode.MoveCriteria(1, -1, 0, 0, 0, 0, 1), new Stance(SIDE.SAME, HEIGHT.TOP), new Stance(SIDE.OPPOSITE, HEIGHT.LOW)),
            //mid smash
            new Move(new MoveCode.MoveCriteria(1, -1, -1, -1, 0, 0, 1), new Stance(SIDE.SAME, HEIGHT.TOP), new Stance(SIDE.OPPOSITE, HEIGHT.LOW)),
            //smash
            new Move(new MoveCode.MoveCriteria(1, -1, -1, -1, 0, 0, 1), new Stance(SIDE.SAME, HEIGHT.TOP), new Stance(SIDE.OPPOSITE, HEIGHT.LOW)),
    };
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            StaticRefs.CHAIN
    };

    public Nunchaku() {
        super(0, 1.5, 4f, 0.6f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tonfa.main"));
        tooltip.add(I18n.format("tonfa.off"));
        tooltip.add(I18n.format("tonfa.parry"));
        tooltip.add(I18n.format("tonfa.reset"));
        tooltip.add(I18n.format("tonfa.defbreak"));
        tooltip.add(I18n.format("tonfa.riposte"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return 0f;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {

    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }

    public boolean isTwoHanded(ItemStack is) {
        return false;
    }//FIXME

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20));
        }
        if (isCharged(attacker, stack)) {
            attacker.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, getChargeTimeLeft(attacker, stack)));
            attacker.addPotionEffect(new PotionEffect(TaoPotion.RESOLUTION, 40));

        }
    }

    @Override
    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND)
            multiHit(attacker, target, 1, 1);
    }

    private enum SIDE {
        //relative to wielding hand
        SAME,
        OPPOSITE
    }

    private enum HEIGHT {
        TOP,// above shoulder
        MID,// under armpit
        LOW // nowhere
    }

    private static class Stance {

        private SIDE s;
        private HEIGHT h;

        private Stance(SIDE side, HEIGHT height) {
            s = side;
            h = height;
        }

        @Nullable
        private static Stance getStartStance(MoveCode mc, EnumHand hand) {
//            HEIGHT h = HEIGHT.MID;
//            if (mc.isValid()) {
//                boolean opposite =
//                if (mc.isLeftClick()) {
//
//                    //tops
//                    if (mc.isForwardPressed() && !mc.isBackPressed() && !mc.isLeftPressed() && !mc.isRightPressed())//smash
//                        h = HEIGHT.TOP;
//                    //mids
//                    //bots
//                    //no jg or sup!
//                } else {//alt moves
//
//                }
//            }
            return null;
        }

        @Nullable
        private static Stance getEndStance(MoveCode mc) {
            return null;
        }
    }

    private class Move {
        Stance s, f;
        MoveCode.MoveCriteria mcr;

        private Move(MoveCode.MoveCriteria criteria, Stance start, Stance finish) {
            mcr = criteria;
            s = start;
            f = finish;
        }
    }
}
