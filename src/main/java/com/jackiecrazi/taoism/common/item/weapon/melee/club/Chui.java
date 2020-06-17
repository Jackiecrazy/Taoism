package com.jackiecrazi.taoism.common.item.weapon.melee.club;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Chui extends TaoWeapon {

    //a powerful crushing weapon. Brutal, somewhat defensive, with decent reach but low trickery potential
    //leap attacks deal double instead of 1.5x damage. Attacks always decrease posture,
    // and will additionally deal 1.5x damage against staggered targets for a total of triple damage
    //FIXME posture mult too high, hammers are crap at defending

    public Chui() {
        super(0, 1.4f, 7f, 1.3f);
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
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.5f;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("chui.leap"));
        tooltip.add(I18n.format("chui.stagger"));
        tooltip.add(I18n.format("chui.slow"));
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float ground = attacker.motionY < 0 ? 2f : 1f;
        float breach = TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? 1.5f : 1f;
        return ground * breach;

    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(orig * 0.5f, true, attacker);
        }
        //TaoCasterData.getTaoCap(target).consumePosture(TaoCasterData.getTaoCap(target).getMaxPosture()+11, true, attacker, ds);
        dischargeWeapon(attacker, item);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, chi * 10, chi / 3));
    }

}
