package com.jackiecrazi.taoism.common.item.weapon.melee.sectional;

import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Nunchaku extends TaoWeapon {
    /*
     * A flexible blunt weapon that still hurts. High speed and defense, medium power and combo, low range
     * I'm gonna be using this to test the move system.
     * Can be either one or two handed, with different movesets for both.
     * Nunchaku will become two-handed if in main hand and offhand is empty
     * after a given attack, your nunchaku will end up on a certain side:
     * high (h) or low (l), same (s) or different (d) side
     * high is fast, low is powerful
     * right side does extra posture damage, left side applies short effects
     * From a given stance it's possible to launch a high attack by default or a low attack by pressing back
     *
     * Available moves: hl smash, hh flick, ll sweep, lh 8-spin, ?? jab
     * flick is very fast
     * smash inflicts slowness and deals 1.3x damage
     * sweep hits in radius 1 and knocks back slightly
     * 8-spin hits chip damage 3 times with no knockback
     *
     * Extra moves when two-handed: dhl backflip (default guard), dlh updraft
     * When two-handed, you can chain with rapidly switching hands, resetting one hand if the other is used.
     * backflip interrupts enemy attacks and knocks them back very slightly, by back-alt
     * updraft AoEs, hits through blocks and knocks back, done by normal alt
     *
     * Execution: if you are behind your opponent and in jab range (1), inflict slow 2/infinite, gain rooted 1/infinite (no knockback),
     * and causes the opponent to lose 5 bars of oxygen per second in a choke hold.
     * Lasts until distance > 3, at which point the opponent is tripped for 50% max posture damage
     * Alternatively, by dealing enough damage to you (threshold scales with higher chi) will end this state as well
     *
     */
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            StaticRefs.CHAIN
    };

    public Nunchaku() {
        super(0, 1.6, 5f, 0.6f);
        this.addPropertyOverride(new ResourceLocation("nuns"), (stack, w, elb) -> {
            int low = getCurrentMove(stack).isSneakPressed() ? 1 : 0;
            int dual = isTwoHanded(stack) ? 2 : 0;
            return low + dual;
        });
    }

    public boolean isTwoHanded(ItemStack is) {
        return isOffhandEmpty(is) || isDummy(is);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        updateStanceSide(is);
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected void aoe(ItemStack is, EntityLivingBase attacker, int chi) {
        if (getCurrentMove(is).isSneakPressed() && getLastMove(is).isSneakPressed()) {//low low
            //sweep!
            splash(attacker, is, 90);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.YELLOW+I18n.format("weapon.half")+TextFormatting.RESET);
        tooltip.add(I18n.format("nunchaku.dual"));
        tooltip.add(I18n.format("nunchaku.stance"));
        tooltip.add(I18n.format("nunchaku.moves"));
        tooltip.add(I18n.format("nunchaku.flick"));
        tooltip.add(I18n.format("nunchaku.smash"));
        tooltip.add(I18n.format("nunchaku.sweep"));
        tooltip.add(I18n.format("nunchaku.spin"));
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        if (!getCurrentMove(is).isSneakPressed() && !getLastMove(is).isSneakPressed()) {//high high
            //flick!
            return 0.8f;
        }
        return 0f;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {

    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getCurrentMove(item).isSneakPressed() && !getLastMove(item).isSneakPressed() ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (getCurrentMove(item).isSneakPressed() && !getLastMove(item).isSneakPressed()) {//high low
            //smash!
            return 1.5f;
        }
        return 1;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (!getCurrentMove(item).isSneakPressed() && getLastMove(item).isSneakPressed()) {//low high
            //8-spin! reduce damage
            return 0.4f;
        }
        return 1;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getCurrentMove(stack).isSneakPressed() && !getLastMove(stack).isSneakPressed()) {//high low
            //smash!
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, 10, 0));
        }
    }

    @Override
    protected void queueExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (!getCurrentMove(stack).isSneakPressed() && getLastMove(stack).isSneakPressed()) {//low high
            //8-spin!
            multiHit(attacker, stack, target, 4, 2);
        }
    }

    private void updateStanceSide(ItemStack is) {
        MoveCode mc = getCurrentMove(is);
        gettagfast(is).setBoolean("onOtherSide", mc.isValid() && !gettagfast(is).getBoolean("onOtherSide"));
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }
}
