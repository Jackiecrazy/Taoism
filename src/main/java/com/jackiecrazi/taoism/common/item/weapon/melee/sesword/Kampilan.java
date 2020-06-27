package com.jackiecrazi.taoism.common.item.weapon.melee.sesword;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntitySwordBeam;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Kampilan extends TaoWeapon {
    private float knock = 0;

    //relentless and intimidating weapon, with a decent reach and defense but low trickery potential
    //high attack speed, has a big sweep, sinawalis at 3 chi, converts knockback to extra damage at 6 chi,
    // and follows up with 2 extra attacks at 9 chi
    public Kampilan() {
        super(1, 1.6d, 6d, 1f);
        this.setQiAccumulationRate(0.20f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
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
    protected float getQiAccumulationRate(ItemStack is) {
        return qiRate;
    }

    @Override
    //default attack code to AoE
    protected void aoe(ItemStack stack, EntityLivingBase elb, int chi) {
        if (isCharged(elb, stack) && !elb.world.isRemote) {
            if (TaoCasterData.getTaoCap(elb).consumeQi(0.5f, 5))
                splash(elb, stack, 360);
            else {
                for (int i = 0; i < 5; i++) {
                    float rotation = 72 * i;
                    EntitySwordBeam esb = new EntitySwordBeam(elb.world, elb, getHand(stack), stack).setRenderRotation(-40 + Taoism.unirand.nextInt(80));
                    esb.shoot(elb, 0, elb.rotationYaw + rotation, 0.0F, 1f, 0.0F);
                    elb.world.spawnEntity(esb);
                }
            }
        } else splash(elb, stack, 120);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("kampilan.aoe"));
        tooltip.add(I18n.format("kampilan.combo"));
        tooltip.add(I18n.format("kampilan.riposte"));
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //resets combo, the next combo sequence in 5 seconds additionally has its knockback converted to true posture damage
        setCombo(defender, item, 0);
        super.parrySkill(attacker, defender, item);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (attacker.motionY < 0) return 1.5f;
        else return 1f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (TaoCasterData.getTaoCap(attacker).getQiFloored() < 5)
            return orig;
        knock = orig;
        return 0;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(knock, true, attacker);
        }
        return super.hurtStart(ds, attacker, target, item, orig) + knock;
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        boolean comboEnded = getCombo(elb, is) == getComboLength(elb, is) - 1;
        if (comboEnded) dischargeWeapon(elb, is);
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        if (TaoCasterData.getTaoCap(wielder).getQi() >= 5)
            return 3;
        return 1;
    }
}
