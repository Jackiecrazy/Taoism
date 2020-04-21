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
     * high (h) or low (l), same (s) or different (d) side
     * high is fast, low is powerful
     * same side does extra posture damage, different side applies short effects
     * From a given stance it's possible to launch a high attack by default or a low attack by pressing back
     *
     * Available moves (same side): hl smash, hh flick, ll sweep, lh 8-spin, ?? jab
     * flick is very fast
     * smash inflicts slowness and deals extra damage
     * sweep hits in radius 1 and knocks back slightly
     * 8-spin hits chip damage 3 times with no knockback
     * jab is a close range high posture move
     *
     * Extra moves when two-handed: dhl backflip (default guard), dlh updraft
     * When two-handed, you can chain with rapidly switching hands, resetting one hand if the other is used.
     * backflip interrupts enemy attacks, switches sides and knocks them back very slightly, by back-alt
     * updraft switches sides, AoEs, hits through blocks and knocks back, done by normal alt
     *
     * Riposte(one handed): automatically perform a flick, canceling the incoming attack and disorienting the enemy slightly
     * Riposte(two handed): catch the opponent's weapon in your chain, binding 3.
     * your attacks instantly refill each other's gauges halfway and cannot be blocked for 6 seconds (so your attack speed is quadrupled).
     * if you are behind your opponent and in jab range, consume this buff to inflict slow 2/infinite, gain rooted 1/infinite (no knockback),
     * and causes the opponent to lose 5 bars of oxygen per second in a choke hold.
     * Lasts until distance > 3, at which point the opponent is tripped for 50% max posture damage
     * Alternatively, by dealing enough damage to you (threshold scales with higher chi) will end this state as well
     *
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
        super(0, 1.5, 5f, 0.6f);
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

    }

    private void setStance(ItemStack is, Stance s){

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
