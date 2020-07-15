package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.warhammer;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class ChangChui extends TaoWeapon {
    /*
     * Just a long version of a chui. High range and power, medium defense, low speed and combo
     * Leap normal attacks deal double posture damage
     * Deals 2x downed damage in addition to the normal 2x, for a grand total of quadruple downed damage
     * Right click to sweep with radius of 4 and medium knock back, and halve both hand cooldowns
     * Parry special:
     * for the next 5 seconds, opponents will always take posture damage from you and cannot recover posture
     * Sweep will instantly use up this buff to majorly knock back all targets and remove half their current posture
     * has high knockback
     * right click to nail an entity down. A nailed entity is rooted up to 1 second depending on chi
     * this can be repeated to slowly nail down a tall entity, suffocating them
     * a normal attack on a nailed entity will uproot them for extra damage
     * nailing an entity into another entity deals damage to both
     */
    //private final AttributeModifier
    public ChangChui() {
        super(0, 1f, 8f, 1.5f);
    }

//    @Override
//    protected double speed(ItemStack stack) {
//        double ret = super.speed(stack) + 4d;
//        if (getHand(stack) == EnumHand.OFF_HAND) {
//            ret /= 1.5d;
//        }
//        ret -= 4d;
//        return ret;
//    }

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
        return 4;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.6f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
//        if (getHand(stack) == EnumHand.OFF_HAND) {
//            splash(attacker, stack, 120);
//        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("maul.leap"));
        tooltip.add(I18n.format("maul.impact"));
        tooltip.add(I18n.format("maul.nail"));
        tooltip.add(I18n.format("maul.hardness"));
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        if(getHand(item)==EnumHand.OFF_HAND)return Event.Result.DENY;
        if (attacker.world.collidesWithAnyBlock(target.getEntityBoundingBox()) || TaoMovementUtils.willHitWallFrom(target, attacker)) {
            return Event.Result.ALLOW;
        }
        return super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float cdmg = 1;
        if (getHand(item) == EnumHand.MAIN_HAND && TaoMovementUtils.willHitWallFrom(target, attacker)) cdmg *= 1.5;
        if (!attacker.onGround) cdmg *= 2;
        return cdmg;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return super.damageMultiplier(attacker, target, item);
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item)) {
            target.motionY += 1.5;
            target.motionX -= MathHelper.sin(-attacker.rotationYaw * 0.017453292F - (float) Math.PI) * 5;
            target.motionZ -= MathHelper.cos(-attacker.rotationYaw * 0.017453292F - (float) Math.PI) * 5;
            target.velocityChanged = true;
            TaoCasterData.getTaoCap(target).startRecordingDamage();
            TaoCasterData.getTaoCap(target).setCannonballTime(100);
            dischargeWeapon(attacker, item);
        }
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return getHand(stack) == EnumHand.OFF_HAND ? 0 : orig * 1.5f;
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        //target.world.createExplosion(attacker, target.posX, target.posY, target.posZ, MathHelper.clamp(target.width * target.height * (float) NeedyLittleThings.getSpeedSq(target), 4f, 8f), false);
        //TaoCasterData.getTaoCap(target).setCannonballTime(0);
        TaoCasterData.getTaoCap(target).consumePosture(Float.MAX_VALUE, true, true, null);
        return orig * 4;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (attacker.world.isRemote) return;
        World w = attacker.world;
        if (getHand(stack) == EnumHand.OFF_HAND) {
            int blocksQueried = 0;
            int airBlocks = 0;
            if (target.posY < 2) return;//don't bonk enemies out of the world
            for (int x = (int) (target.posX - target.width); x <= (int) (target.posX + target.width); x++) {
                for (int z = (int) (target.posZ - target.width); z <= (int) (target.posZ + target.width); z++) {
                    blocksQueried++;
                    BlockPos bp = new BlockPos(x, target.posY - 1, z);
                    if (!w.isBlockLoaded(bp)) return;
                    IBlockState blockUnder = w.getBlockState(bp);
                    BlockPos bp2 = bp.down();
                    int MLUnder = blockUnder.getBlock().getHarvestLevel(blockUnder);
                    if (MLUnder > chi / 2) return;
                    if (w.isAirBlock(bp2)) airBlocks++;
                }
            }
            if (airBlocks == blocksQueried) return;
            target.setPositionAndUpdate(target.posX, target.posY - 1, target.posZ);
            TaoCasterData.getTaoCap(target).setRootTime(chi * 2);
        }
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack stack) {
        EnumHand other = getHand(stack) == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        TaoCombatUtils.rechargeHand(elb, other, 0.5f);
    }
}
