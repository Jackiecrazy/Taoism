package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Karambit extends TaoWeapon {
    //A slashing dagger that is fast and relentless, but short in reach.
    //has no switch in cooldown, and can be switched from hand to hand without cooldown as well.
    //backstabs deal 1.5x damage, inflicts a layer of bleed if target is unarmored.
    //Each chi level allows you to ignore 6 points of armor when inflicting bleed.
    //Can be used to harvest plant blocks, for what that's worth.

    private static final boolean[] harvestList = {false, false, false, true};

    public Karambit() {
        super(1, 2, 5f, 0);
        setQiAccumulationRate(0.75f);
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
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float light = 1 + (15f - attacker.world.getLight(attacker.getPosition())) / 15f;//light bonus
        float backstab = NeedyLittleThings.isBehindEntity(attacker, target) ? 1.5f : 1f;
        return light * backstab;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    public int getMaxChargeTime() {
        return 60;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 2f;//not all that good at defense now is it...
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.isEmpty() || super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("karambit.switch"));
        tooltip.add(I18n.format("karambit.backstab"));
        //tooltip.add(I18n.format("karambit.initiative"));
        tooltip.add(I18n.format("karambit.darkness"));
        tooltip.add(I18n.format("karambit.bleed"));
        tooltip.add(I18n.format("karambit.riposte"));
        tooltip.add(I18n.format("karambit.harvest"));
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        TaoCasterData.getTaoCap(defender).setRollCounter(0);
        defender.rotationYaw = attacker.rotationYaw;
        defender.rotationPitch = attacker.rotationPitch;
        Vec3d look = attacker.getLookVec();
        defender.addVelocity(-look.x, -look.y, -look.z);
        defender.velocityChanged = true;
        super.parrySkill(attacker, defender, item);
    }

    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            Taoism.setAtk(elb, 5);
        }
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return NeedyLittleThings.isBehindEntity(attacker, target) ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if ((target.getLastDamageSource() == null || target.getLastDamageSource().getTrueSource() != attacker)) {
            return target.getTotalArmorValue();
        }
        return super.armorIgnoreAmount(ds, attacker, target, stack, orig);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (target.getTotalArmorValue() - chi * 6d <= 0 || (target.getLastDamageSource() != null && target.getLastDamageSource().getTrueSource() != attacker))
            target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.BLEED, 20, 1), NeedyLittleThings.POTSTACKINGMETHOD.MAXDURATION));
        if (isCharged(attacker, stack)) {
            target.addPotionEffect(NeedyLittleThings.stackPot(target, new PotionEffect(TaoPotion.BLEED, 60, 2), NeedyLittleThings.POTSTACKINGMETHOD.ADD));
            dischargeWeapon(attacker, stack);
        }
    }
}
