package com.jackiecrazi.taoism.common.item.weapon.melee.desword;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityBouncySwordBeam;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntitySwordBeamBase;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Ken extends TaoWeapon {
    //relentless.
    //normal attack chains up to 3 times before requiring cooldown (sword flowers). Small AoE
    //has low starting damage, chain up to do more damage
    public Ken() {
        super(1, 1.6, 5, 1.5f);
        this.setQiAccumulationRate(0.18f);//slight nerf to account for extremely high attack speed
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        if (isCharged(wielder, is)) return 9;
        return 3;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (isCharged(elb, is)) {
            if (!elb.world.isRemote) {
                int numToFire = 1;
                if (!TaoCasterData.getTaoCap(elb).consumeQi(0.5f, 5)) {
                    numToFire = 3;
                    dischargeWeapon(elb, is);
                }
                Vec3d look = elb.getLookVec();
                for (int i = 0; i < numToFire; i++) {
                    elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, elb.world.rand.nextFloat() + i+1, elb.world.rand.nextFloat() + i * 1.7f);
                    float rotation = (getCombo(elb, is) + i) % 2 * 20;
                    if (rotation == 0) rotation = -40;
                    rotation += Taoism.unirand.nextInt(20);
                    EntitySwordBeamBase esb = new EntityBouncySwordBeam(elb.world, elb, getHand(is), is).setRenderRotation(rotation);
                    esb.setPositionAndRotation(elb.posX + (look.x * i * 2), elb.posY + (double) elb.getEyeHeight() - 0.10000000149011612D + (look.y * i * 2), elb.posZ + (look.z * i * 2), elb.rotationYaw, elb.rotationPitch);
                    esb.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 1f, 0.0F);
                    elb.world.spawnEntity(esb);
                }
            }
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    //default attack code to AoE
    public void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {
        if (isAoE(attacker, stack) || !isAiming(attacker, stack)) {
            splash(attacker, stack, 60);
        }
    }

    @Override
    public float getQiAccumulationRate(ItemStack is) {
        return qiRate;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("ken.combo"));
        tooltip.add(I18n.format("ken.qi"));
        tooltip.add(I18n.format("ken.aoe"));
        tooltip.add(I18n.format("ken.stab"));
        tooltip.add(I18n.format("ken.riposte"));
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        //combo limit is raised to 9 for the next 9 seconds
        setCombo(defender, item, 0);
        super.onParry(attacker, defender, item, amount);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return isAoE(attacker, item) || !isAiming(attacker, item) ? Event.Result.DENY : Event.Result.ALLOW;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float aoe = isAoE(attacker, item) || !isAiming(attacker, item) ? 1f : 1.2f;
        return aoe;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1 + (getQiFromStack(item) / 27f);
    }

    private boolean isAoE(EntityLivingBase attacker, ItemStack is) {
        boolean toggle = false;
        for (Entity target : attacker.world.getEntitiesInAABBexcluding(null, attacker.getEntityBoundingBox().grow(getReach(attacker, is)), TaoCombatUtils.VALID_TARGETS::test)) {
            if (target == attacker || attacker.isRidingOrBeingRiddenBy(target)) continue;
            if (!NeedyLittleThings.isFacingEntity(attacker, target, 60) || NeedyLittleThings.getDistSqCompensated(target, attacker) > getReach(attacker, is) * getReach(attacker, is))
                continue;
            if (toggle) {
                return true;
            } else toggle = true;
        }
        return false;
    }

    private boolean isAiming(EntityLivingBase elb, ItemStack is) {
        return NeedyLittleThings.raytraceEntity(elb.world, elb, getReach(elb, is)) != null;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 3;
    }
}
