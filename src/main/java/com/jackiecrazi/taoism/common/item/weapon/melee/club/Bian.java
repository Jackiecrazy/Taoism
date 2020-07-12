package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Bian extends TaoWeapon {
    /*
    high power, middling defense and combo, low range and speed
    handles a little like the chui, but is more fluid, leading to higher defense and attack speed
        at the cost of less posture damage
    essentially a blunt sword, yeah.
    ignores some armor
    attacking and being parried disables opponent weapon briefly (this might be finicky)
    low posture per damage, but inflicts enfeeble, to play into its "continuous" nature
     */
    public Bian() {
        super(0, 1.4, 6, 1f);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_GREEN+I18n.format("weapon.disshield")+TextFormatting.RESET);
        tooltip.add(I18n.format("bian.stagger"));
        tooltip.add(I18n.format("bian.armpen"));
        tooltip.add(I18n.format("bian.enfeeble"));
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float ground = !attacker.onGround ? 1.5f : 1f;
        float breach = TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? 1.5f : 1f;
        return ground * breach;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return TaoCasterData.getTaoCap(attacker).getQiFloored();
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.ENFEEBLE, 40, chi / 3), false);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public int getMaxChargeTime() {
        return 0;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return attacker.isAirBorne;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.7f;
    }
}
