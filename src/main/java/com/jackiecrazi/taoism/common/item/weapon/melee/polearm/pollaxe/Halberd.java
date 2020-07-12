package com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityAxeCleave;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Halberd extends TaoWeapon {
    /*
     * A long-handled axe with incredible burst. High range and power, medium defense and speed, low combo
     * 6 blocks of reach, 2 handed
     * Leap attacks deal double posture damage
     * Left click is a standard attack that stacks cleave 2/3
     * Right click is a stab that detonates cleave for piercing damage with 10 second cooldown
     *      Each detonated layer of cleave will reduce a second of cleave
     *
     * Execution: length 18 width 3 ravine chop that roots enemies
     *      After a short delay, shrapnel/spikes emerge from the shattered area and impale mobs
     * should look like a series of dust particles, then just place very short "spike" blocks along the way that decay eventually?
     */

    private static final boolean[] harvestList = {false, false, true, false};

    public Halberd() {
        super(3, 1, 8, 1f);
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
        return 5;
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.3f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public double attackDamage(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND && !isCharged(null, stack) ? 1 : super.attackDamage(stack);
    }

    protected double speed(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND ? 0.1 - 4d : super.speed(stack);
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return isCharged(attacker, stack)?0: super.knockback(attacker, target, stack, orig);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (isCharged(entityLiving, stack) && !entityLiving.world.isRemote) {
            EntityAxeCleave eac = new EntityAxeCleave(entityLiving.world, entityLiving, EnumHand.MAIN_HAND, stack);
            eac.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0, 0.5f, 0);
            entityLiving.world.spawnEntity(eac);
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("halberd.leap"));
        tooltip.add(I18n.format("halberd.cleave"));
        tooltip.add(I18n.format("halberd.stab"));
        tooltip.add(TextFormatting.ITALIC + I18n.format("halberd.stab.cooldown") + TextFormatting.RESET);
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 2 : 3;
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item) {
        return getHand(item) == EnumHand.MAIN_HAND;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item) == EnumHand.OFF_HAND ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.MAIN_HAND ? 2f : 1f;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        float doot = super.hurtStart(ds, attacker, target, item, orig);
        if (getHand(item) == EnumHand.OFF_HAND) {
            float effectiveLevel = (float) TaoPotionUtils.getEffectiveLevel(target, TaoPotion.ARMORBREAK, SharedMonsterAttributes.ARMOR);
            ds.setDamageBypassesArmor();
            target.removeActivePotionEffect(TaoPotion.ARMORBREAK);
            item.setTagInfo("lastDootLevel", new NBTTagFloat(effectiveLevel / 10f));
            if (isCharged(attacker, item))
                return doot + (effectiveLevel * 5f);
            return doot + (effectiveLevel * 2f);
        }
        return doot;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.MAIN_HAND) {
            TaoPotionUtils.attemptAddPot(target, TaoPotionUtils.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 80, isCharged(attacker, stack) ? 5 : 0), TaoPotionUtils.POTSTACKINGMETHOD.MAXDURATION), true);
        }
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? gettagfast(is).getFloat("lastDootLevel") : super.newCooldown(elb, is);
    }
}
