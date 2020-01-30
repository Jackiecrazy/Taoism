package com.jackiecrazi.taoism.common.item.weapon.spear;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import com.jackiecrazi.taoism.moves.melee.MoveMultiStrike;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemQiang extends TaoWeapon {
    /*
    First two handed weapon! High reach and speed, medium power and combo, low defense potential
    left click for a normal stab, piercing enemies up to the max range.
    right click to do a bash that knocks the target a fair distance away and inflicts blunt damage, at cost of lower damage
     */

    private final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE,
            new PartDefinition("tassel", StaticRefs.STRING)
    };

    public ItemQiang() {
        super(2, (double) 1.2, 5d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float aerial = !attacker.onGround ? 1.5f : 1f;
        float bash = right ? 0.5f : 1f;
        return aerial * bash;
    }

    @Override
    public int getDamageType(ItemStack is) {
        return TaoWeapon.right ? 0 : isCharged(null, is) ? 1 : 2;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        return 0;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 5.5f;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //the next bash in 4 seconds AoEs, knocks back and briefly slows the opponents
        //the next stab in 4 seconds deals cutting damage 3 times with an interval of 1 tick
        chargeWeapon(attacker, defender, item, 80);
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.4f;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (right && isCharged(attacker, stack))
            splash(attacker, target, 2f);
        else if (!right)
            splash(attacker, NeedyLittleThings.raytraceEntities(target.world, attacker, target, getReach(attacker, stack)));
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (right) {
            if (isCharged(attacker, stack)) {
                NeedyLittleThings.knockBack(target, attacker, 1);//FIXME 1 strength?
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 0));
            }
        }
    }

    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (isCharged(attacker, stack) && !right) {
            attacker.world.spawnEntity(new MoveMultiStrike(attacker, target, 2, 4));
            dischargeWeapon(attacker, stack);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED+I18n.format("weapon.hands")+TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.stab"));
        tooltip.add(TextFormatting.ITALIC+I18n.format("qiang.stab.riposte")+TextFormatting.RESET);
        tooltip.add(I18n.format("qiang.bash"));
        tooltip.add(TextFormatting.ITALIC+I18n.format("qiang.bash.riposte")+TextFormatting.RESET);
        tooltip.add(TextFormatting.BOLD+I18n.format("qiang.riposte")+TextFormatting.RESET);
    }
}
