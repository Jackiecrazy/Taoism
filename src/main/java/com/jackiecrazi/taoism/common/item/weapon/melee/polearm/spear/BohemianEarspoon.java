package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BohemianEarspoon extends TaoWeapon {
    /*
    A weapon all about negotiating distance. High range, medium power and defense, low combo and speed
    Normal attack's crit modifier is determined by the difference between the distance of you and the target in the last 2 attacks
        its recharge time is also reduced down to a minimum of 50% depending on the difference
    Alt attack will consume this buff to do a small AoE swipe
    Riposte: for the next 7 seconds, your attacks mark a single target. The target cannot approach you unless they first exit your attack range.
        Either your movements force the target to move with you or the target's movements move you as well, depending on size
    about prime time to add relative distance manipulators, you know?
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE
    };

    public BohemianEarspoon() {
        super(2, 1.4, 6.5d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float aerial = !attacker.onGround ? 1.5f : 1f;
        float bash = getHand(item) == EnumHand.OFF_HAND ? 0.5f : 1f;
        return aerial * bash;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : isCharged(null, is) ? 1 : 2;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 6f;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (isCharged(elb, stack) && getLastMove(stack).isLeftClick() && elb.getLastAttackedEntity() != null) {
                EntityLivingBase target=elb.getLastAttackedEntity();
                if(target.getDistanceSq(elb)<getLastAttackedRangeSq(stack)){
                    //a new challenger is approaching!

                }
                Vec3d pos=elb.getPositionVector();
                Vec3d angleOfAttack = new Vec3d(target.posX - (pos.x + 0.5D), target.posY - pos.y, target.posZ - (pos.z + 0.5D));

                // we use the resultant vector to determine the force to apply.
                double xForce = angleOfAttack.x * 0.01;
                double yForce = angleOfAttack.y * 0.01;
                double zForce = angleOfAttack.z * 0.01;
                target.motionX += xForce;
                target.motionY += yForce;
                target.motionZ += zForce;
                target.velocityChanged=true;
            }
        }
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 1f);
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
    }

    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack) && getHand(stack) == EnumHand.MAIN_HAND) {
            multiHit(attacker, target, 4, 8);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.stab"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("qiang.stab.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.bash"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("qiang.bash.riposte") + TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.oscillate"));
        tooltip.add(TextFormatting.BOLD + I18n.format("qiang.riposte") + TextFormatting.RESET);
    }

    private void setLastAttackedRangeSq(ItemStack item, float range) {
        if (range != 0f) {
            gettagfast(item).setFloat("lastAttackedRange", range);
        } else {
            gettagfast(item).removeTag("lastAttackedRange");
        }
    }

    private float getLastAttackedRangeSq(ItemStack is) {
        return gettagfast(is).getFloat("lastAttackedRange");
    }
}
