package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Chui extends TaoWeapon {

    //a powerful crushing weapon. Brutal, somewhat defensive, with decent reach but low trickery potential
    //leap attacks deal double instead of 1.5x damage. Attacks always decrease posture,
    // and will additionally deal 1.5x damage against staggered targets for a total of triple damage
    //execution

    public Chui() {
        super(0, 1.2f, 8f, 1f);
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
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
        return 3f;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1.6f;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_GREEN+I18n.format("weapon.disshield")+TextFormatting.RESET);
        tooltip.add(I18n.format("chui.leap"));
        tooltip.add(I18n.format("chui.stagger"));
        tooltip.add(I18n.format("chui.slow"));
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? Event.Result.ALLOW : super.critCheck(attacker, target, item, crit, vanCrit);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float ground = !attacker.onGround ? 2f : 1f;
        float breach = TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? 1.5f : 1f;
        return ground * breach;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return attacker.isAirBorne;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, chi * 10, chi / 3), false);
    }

}
