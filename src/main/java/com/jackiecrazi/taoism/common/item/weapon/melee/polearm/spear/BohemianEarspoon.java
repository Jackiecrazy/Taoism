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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

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
        super(2, 1.3, 6d, 1f);
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
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && !w.isRemote) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (getLastMove(stack).isLeftClick() && elb.getLastAttackedEntity() != null) {
                EntityLivingBase target = elb.getLastAttackedEntity();
                if (NeedyLittleThings.isFacingEntity(elb, target, 90)) {
                    if(target.getDistanceSq(elb) < getLastAttackedRangeSq(stack)) {
                        if (target.getDistanceSq(elb) < getLastAttackedRangeSq(stack) / 2) {
                            //too close! Rip out innards for double damage
                            target.attackEntityFrom(DamageSourceBleed.causeBleedingDamage(), 2f * (float) elb.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                        }
                        //a new challenger is approaching!
                        Vec3d pos = elb.getPositionVector();
                        Vec3d angle = new Vec3d(target.posX - (pos.x + 0.5D), target.posY - pos.y, target.posZ - (pos.z + 0.5D));
                        double xForce = angle.x * 0.02;
                        double yForce = angle.y * 0.02;
                        double zForce = angle.z * 0.02;
                        target.motionX += xForce;
                        target.motionY += yForce;
                        target.motionZ += zForce;
                        target.velocityChanged = true;
                    }
                } else elb.setLastAttackedEntity(null);

            }
        }
    }

    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND && attacker.getLastAttackedEntity() != null) {
            splash(attacker, stack, 120);
            attacker.setLastAttackedEntity(null);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("bohear.dist"));
        tooltip.add(I18n.format("bohear.buff"));
        tooltip.add(I18n.format("bohear.bash"));
        tooltip.add(I18n.format("bohear.riposte"));
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
        float lastAttackRange = 1 + min / (max * 1.5f);
        return getHand(item) == EnumHand.MAIN_HAND ? lastAttackRange : 0.5f;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            NeedyLittleThings.knockBack(target, attacker, 1f);
            setLastAttackedRangeSq(stack, 0);
        } else {
            setLastLastAttackedRangeSq(stack, getLastAttackedRangeSq(stack));
            setLastAttackedRangeSq(stack, (float) attacker.getDistanceSq(target));
        }
    }

    private void setLastAttackedRangeSq(ItemStack item, float range) {
        if (range != 0f) {
            gettagfast(item).setFloat("lastAttackedRange", range);
        } else {
            gettagfast(item).removeTag("lastAttackedRange");
        }
    }

    private void setLastLastAttackedRangeSq(ItemStack item, float range) {
        if (range != 0f) {
            gettagfast(item).setFloat("llastAttackedRange", range);
        } else {
            gettagfast(item).removeTag("llastAttackedRange");
        }
    }

    private float getLastLastAttackedRangeSq(ItemStack is) {
        return gettagfast(is).getFloat("llastAttackedRange");
    }

    private float getLastAttackedRangeSq(ItemStack is) {
        return gettagfast(is).getFloat("lastAttackedRange");
    }
}
