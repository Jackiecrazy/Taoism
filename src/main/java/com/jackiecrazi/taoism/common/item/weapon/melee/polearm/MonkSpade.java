package com.jackiecrazi.taoism.common.item.weapon.melee.polearm;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A defensive two-handed weapon that focuses on defense and disabling the enemy
 * range 5, damage 5, attack speed 1.2, posture multiplier 0.8
 * attack entity or parry their attack to shovel earth onto them, extending slowness
 * when slowness duration * 5 exceeds max posture, the enemy will be rooted and buried halfway into the ground for some number of seconds
 * attacking the entity now, if it is undead, will crit for double damage
 * right click will push a mob without damage. They cannot approach you as long as you face them, like ghiavarina.
 * the two moves work differently on terrain. Normally the spade functions as an axe and a shovel rolled into one,
 * but on right clicking, blocks will be shot out as a falling block, which stacks dirt on whatever they hit
 * <p>
 * so you can properly bury and exorcise undead even at a distance, or just dig a hole and push the mob in with the crescent
 */
public class MonkSpade extends TaoWeapon {
    public MonkSpade() {
        super(0, 1.2, 5, 1);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("monkspade.slow"));
        tooltip.add(I18n.format("monkspade.bury"));
        tooltip.add(I18n.format("monkspade.hardness"));
        tooltip.add(I18n.format("monkspade.crit"));
        tooltip.add(I18n.format("monkspade.deny"));
        tooltip.add(I18n.format("monkspade.harvest"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        if (getHand(is) == EnumHand.OFF_HAND) return 0;
        return 5;
    }

    @Override
    public double attackDamage(ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) return 0;
        return super.attackDamage(stack);
    }

    @Override
    public float postureMultiplierDefend(@Nullable Entity attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            setLastAttackedEntity(elb.getHeldItemMainhand(), NeedyLittleThings.raytraceEntity(elb.world, elb, getReach(elb, elb.getHeldItemMainhand())));
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        TaoPotionUtils.attemptAddPot(attacker, TaoPotionUtils.stackPot(attacker, new PotionEffect(MobEffects.SLOWNESS, 40), TaoPotionUtils.POTSTACKINGMETHOD.ONLYADD), false);
        if (attacker.onGround && attacker.isPotionActive(MobEffects.SLOWNESS) && attacker.getActivePotionEffect(MobEffects.SLOWNESS).getDuration() / 4 > TaoCasterData.getTaoCap(attacker).getPosture()) {
            TaoCasterData.getTaoCap(attacker).setRootTime(100);
            int blocksQueried = 0;
            int airBlocks = 0;
            double buryToY = attacker.posY;
            if (attacker.posY < 2) return;//don't bonk enemies out of the world
            for (int x = (int) (attacker.posX - attacker.width); x <= (int) (attacker.posX + attacker.width); x++) {
                for (int z = (int) (attacker.posZ - attacker.width); z <= (int) (attacker.posZ + attacker.width); z++) {
                    for (double y = attacker.posY; y > attacker.height / 2; y--) {
                        if (y < 2) break;
                        blocksQueried++;
                        BlockPos bp = new BlockPos(x, y, z);
                        if (!attacker.world.isBlockLoaded(bp)) return;
                        IBlockState blockUnder = attacker.world.getBlockState(bp);
                        BlockPos bp2 = bp.down();
                        int MLUnder = blockUnder.getBlock().getHarvestLevel(blockUnder);
                        if (MLUnder > TaoCasterData.getTaoCap(defender).getQiFloored() / 2) return;
                        if (attacker.world.isAirBlock(bp2)) airBlocks++;
                        buryToY = Math.min(buryToY, y);
                    }
                }
            }
            if (airBlocks == blocksQueried) return;
            attacker.setPositionAndUpdate(attacker.posX, Math.max(buryToY, attacker.posY - attacker.height / 2), attacker.posZ);
        }
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        TaoPotionUtils.attemptAddPot(target, TaoPotionUtils.stackPot(target, new PotionEffect(MobEffects.SLOWNESS, 30), TaoPotionUtils.POTSTACKINGMETHOD.ONLYADD), false);
        if (target.onGround && target.isPotionActive(MobEffects.SLOWNESS) && target.getActivePotionEffect(MobEffects.SLOWNESS).getDuration() / 4 > TaoCasterData.getTaoCap(target).getPosture()) {
            TaoCasterData.getTaoCap(target).setRootTime(100);
            int blocksQueried = 0;
            int airBlocks = 0;
            double buryToY = target.posY;
            if (target.posY < 2) return;//don't bonk enemies out of the world
            for (int x = (int) (target.posX - target.width); x <= (int) (target.posX + target.width); x++) {
                for (int z = (int) (target.posZ - target.width); z <= (int) (target.posZ + target.width); z++) {
                    for (double y = target.posY; y > target.height / 2; y--) {
                        if (y < 2) break;
                        blocksQueried++;
                        BlockPos bp = new BlockPos(x, y, z);
                        if (!target.world.isBlockLoaded(bp)) return;
                        IBlockState blockUnder = target.world.getBlockState(bp);
                        BlockPos bp2 = bp.down();
                        int MLUnder = blockUnder.getBlock().getHarvestLevel(blockUnder);
                        if (MLUnder > chi / 2) return;
                        if (target.world.isAirBlock(bp2)) airBlocks++;
                        buryToY = Math.min(buryToY, y);
                    }
                }
            }
            if (airBlocks == blocksQueried) return;
            target.setPositionAndUpdate(target.posX, Math.max(buryToY, attacker.posY - attacker.height / 2), target.posZ);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD && TaoCasterData.getTaoCap(target).getRootTime() > 0 ? Event.Result.ALLOW : Event.Result.DEFAULT;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2;
    }

    @Override
    protected boolean[] harvestable(ItemStack is) {
        return new boolean[]{false, true, true, false};
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && onHand) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (!w.isRemote) {
                if (!getCurrentMove(stack).isLeftClick() && getLastAttackedEntity(w, stack) != null) {
                    Entity target = getLastAttackedEntity(w, stack);
                    if (NeedyLittleThings.isFacingEntity(elb, target, 90)) {
                        if (target.getDistanceSq(elb) < getReach(elb, elb.getHeldItemMainhand()) * getReach(elb, elb.getHeldItemMainhand())) {
                            //a new challenger is approaching!
                            target.motionX += (target.posX - (elb.posX + 0.5D)) * 0.05;
                            target.motionY += ((target.posY) - elb.posY) * 0.05;
                            target.motionZ += (target.posZ - (elb.posZ + 0.5D)) * 0.05;
                            target.velocityChanged = true;
                        }
                    } else {
                        setLastAttackedEntity(elb.getHeldItemMainhand(), null);
                    }
                }
            }

        }
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }
}
