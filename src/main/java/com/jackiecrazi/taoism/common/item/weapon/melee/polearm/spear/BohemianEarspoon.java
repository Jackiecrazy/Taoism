package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && !w.isRemote && onHand) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (getLastMove(stack).isLeftClick() && elb.getLastAttackedEntity() != null && getLastAttackedRangeSq(stack) != 0) {
                EntityLivingBase target = elb.getLastAttackedEntity();
                if (NeedyLittleThings.isFacingEntity(elb, target, 90)) {
                    if (target.getDistanceSq(elb) < getLastAttackedRangeSq(stack)) {
                        if (target.getDistanceSq(elb) < getLastAttackedRangeSq(stack) / 2) {
                            //too close! Rip out innards for double damage
                            target.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(elb), 2f * (float) elb.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                            setLastAttackedRangeSq(elb, stack, 0);
                        }
                        //a new challenger is approaching!
                        target.motionX += (target.posX - (elb.posX + 0.5D)) * 0.02;
                        target.motionY += ((target.posY) - elb.posY) * 0.02;
                        target.motionZ += (target.posZ - (elb.posZ + 0.5D)) * 0.02;
                        target.velocityChanged = true;
                    }
                }
            }
        }
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && attacker.getLastAttackedEntity() != null) {
            splash(attacker, stack, 120);
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
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : 2;
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

    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return Event.Result.DENY;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float max = Math.max((float) attacker.getDistanceSq(target), getLastAttackedRangeSq(item));
        float min = Math.min((float) attacker.getDistanceSq(target), getLastAttackedRangeSq(item));
        float lastAttackRange = 1 + min / (max * 2f);
        return getHand(item) == EnumHand.MAIN_HAND ? lastAttackRange : 0.5f;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 1f);
            setLastAttackedRangeSq(attacker, attacker.getHeldItemMainhand(), 0);
        } else {
            setLastAttackedRangeSq(attacker, stack, (float) attacker.getDistanceSq(target));
        }
    }
}
