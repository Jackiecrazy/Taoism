package com.jackiecrazi.taoism.common.item.weapon.sesword;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemKampilan extends TaoWeapon {
    private float knock = 0;

    //relentless and intimidating weapon, with a decent reach and defense but low trickery potential
    //high attack speed, has a big sweep, sinawalis at 3 chi, converts knockback to extra damage at 6 chi,
    // and follows up with 2 extra attacks at 9 chi
    public ItemKampilan() {
        super(1, 1.6d, 6d, 1f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 4;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (attacker.onGround) {
            splash(attacker, attacker.world.getEntitiesInAABBexcluding(target, target.getEntityBoundingBox().grow(4d, 1.5d, 4d), null));
        }
    }

    @Override
    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        boolean thingy = getCombo(elb, is) != getComboLength(elb, is) - 1;
        if (thingy) dischargeWeapon(elb, is);
        if (elb.getCapability(TaoCasterData.CAP, null).getQi() >= 3)
            return thingy ? 0.8f : 0;
        return 0f;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (!attacker.onGround) return 1.5f;
        else return 1f;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        if (wielder.getCapability(TaoCasterData.CAP, null).getQi() >= 9)
            return 5;
        return 3;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //resets combo, the next combo sequence in 5 seconds additionally has its knockback converted to true posture damage
        setCombo(defender, item, 0);
        chargeWeapon(attacker, defender, item, 100);
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (TaoCasterData.getTaoCap(attacker).getQiFloored() < 6)
            return orig;
        knock = orig;
        return 0;
    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(knock, true);
        }
        return orig + knock;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("kampilan.aoe"));
        tooltip.add(I18n.format("kampilan.leap"));
        tooltip.add(I18n.format("kampilan.combo"));
        tooltip.add(I18n.format("kampilan.knockback"));
        tooltip.add(I18n.format("kampilan.riposte"));
    }
}
