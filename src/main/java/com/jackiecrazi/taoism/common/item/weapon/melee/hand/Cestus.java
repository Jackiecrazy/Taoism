package com.jackiecrazi.taoism.common.item.weapon.melee.hand;

import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Cestus extends TaoWeapon {
    final PartDefinition[] parts = {
            new PartDefinition("wrap", StaticRefs.FABRIC),
            new PartDefinition("binding", StaticRefs.STRING)
    };

    //Intimidating and relentless weapon fit for a pugilist. Short range, straightforward, decent at defense
    //naturally adds 2 armor points, applies slow 1 at 3 chi, and slow 2 at 6 chi. At 10 chi blindness. Damage scales with chi
    //to compensate for so many perks, is single target and short range, but no knockback
    public Cestus() {
        super(0, 2, 3d, 1.3f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float grounded = attacker.onGround ? 1f : 1.5f;
        float grapple = isCharged(attacker, item) ? 1.3f : 1f;
        return grounded * grapple;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3.5f;
    }

    /**
     * deals 3 damage at first, goes up by 1 every level without extra effects (3,6) then 2 for every level beyond 7
     *
     * @param stack
     * @return
     */
    @Override
    public double attackDamage(ItemStack stack) {
        double ret = 3;
        int level = gettagfast(stack).getInteger("qifloor");
        int stuff = Math.floorDiv(level, 3);
        ret += (level - stuff);
        if (level > 7) {
            ret += (level - 7);
        }
        return ret;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        int qi = attacker.getCapability(TaoCasterData.CAP, null).getQiFloored();
        if (qi >= 3) {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, Math.floorDiv(qi, 3) - 1));
        }
        if (qi >= 10) {
            target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0));
        }
        if (isCharged(attacker, stack)) {
            TaoCasterData.getTaoCap(target).consumePosture(3.5f, true);
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND || equipmentSlot == EntityEquipmentSlot.OFFHAND)
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 2, 0));
        return multimap;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //TODO grapples the enemy. The next attack pulls them into the ground for 5 posture damage and increases the damage they take by 1.3x
        TaoCasterData.getTaoCap(defender).addQi(2f);
        super.parrySkill(attacker, defender, item);
    }

    @Override
    public int getMaxChargeTime() {
        return 40;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(3.5f, true);
            ds.setDamageIsAbsolute();
        }
        dischargeWeapon(attacker, item);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("cestus.armor"));
        tooltip.add(I18n.format("cestus.leap"));
        tooltip.add(I18n.format("cestus.debuff"));
        tooltip.add(I18n.format("cestus.damage"));
        tooltip.add(I18n.format("cestus.riposte"));
    }
}
