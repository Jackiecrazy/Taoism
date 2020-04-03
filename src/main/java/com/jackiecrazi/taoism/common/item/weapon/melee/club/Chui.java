package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Chui extends TaoWeapon {

    //a powerful crushing weapon. Brutal, somewhat defensive, with decent reach but low trickery potential
    //leap attacks deal double instead of 1.5x damage. Attacks always decrease posture,
    // and will additionally deal 1.5x damage against staggered targets for a total of triple damage

    public Chui() {
        super(0, 1.2f, 4.5f, 1.7f);
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
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float ground=attacker.onGround?1f:2f;
        float breach= TaoCasterData.getTaoCap(target).getDownTimer()!=0?1.5f:1f;
        return ground*breach;

    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 4f;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.5f;
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(orig * 0.5f, true);
        }
        dischargeWeapon(attacker,item);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("chui.leap"));
        tooltip.add(I18n.format("chui.stagger"));
        tooltip.add(I18n.format("chui.riposte"));
    }

}
