package com.jackiecrazi.taoism.common.item.weapon.melee.hand;

import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
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
    //execution: throw both attacker and target into the air/AOE repel other targets.
    // Damage dealt is recorded, then instantly applied when time runs out.
    // Overflow damage culminates in a hit down and a large explosion

    //option to have projectiles pass through mounts
    //there was something else but I forgot, it was a config option that'd influence vanilla play
    public Cestus() {
        super(0, 2, 5d, 0.7f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 2f;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (isCharged(elb, stack)) {
                if (getChargedTime(elb, stack) > getMaxChargeTime() * 2)
                    dischargeWeapon(elb, stack);
                else if (getLastAttackedEntity(w, stack) instanceof EntityLivingBase) {
                    EntityLivingBase uke = (EntityLivingBase) getLastAttackedEntity(w, stack);
                    if (TaoCasterData.getTaoCap(uke).isRecordingDamage() && uke.motionY < 0) {
                        TaoCasterData.getTaoCap(uke).setRootTime(500);
                        TaoCasterData.getTaoCap(uke).setBindTime(500);
                        if (!NeedyLittleThings.isFacingEntity(elb, uke, 30)) {
                            //move you up by force
                            Vec3d reprimand = NeedyLittleThings.getPointInFrontOf(elb, uke, 2);
                            uke.setPositionAndUpdate(reprimand.x, reprimand.y, reprimand.z);
                        }
                    }
                    if (TaoCasterData.getTaoCap(elb).isRecordingDamage() && elb.motionY < 0)
                        TaoCasterData.getTaoCap(elb).setRootTime(500);
                }
            }
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND || equipmentSlot == EntityEquipmentSlot.OFFHAND)
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 2, 0));
        return multimap;
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
        int level = getQiFromStack(stack);
        int stuff = Math.floorDiv(level, 3);
        ret += (level - stuff);
        if (level > 7) {
            ret += (level - 7);
        }
        return ret;
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("cestus.armor"));
        tooltip.add(I18n.format("cestus.debuff"));
        tooltip.add(I18n.format("cestus.damage"));
        tooltip.add(I18n.format("cestus.riposte"));
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        TaoCasterData.getTaoCap(elb).setRootTime(0);
        TaoCasterData.getTaoCap(elb).setQi(5);
        TaoCasterData.getTaoCap(elb).stopRecordingDamage(elb);
        TaoCasterData.getTaoCap(elb).toggleCombatMode(true);
    }

    @Override
    public int getChargedTime(EntityLivingBase elb, ItemStack item) {
        if (gettagfast(item).getLong("startAt") != 0)
            return (int) (elb.world.getTotalWorldTime() - gettagfast(item).getLong("startAt"));
        else return super.getChargedTime(elb, item);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        TaoCasterData.getTaoCap(defender).addQi(0.3f);
        super.onParry(attacker, defender, item);
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        float grounded = attacker.motionY < 0 ? 1f : 1.5f;
        float grapple = isCharged(attacker, item) ? 1.3f : 1f;
        return grounded * grapple;
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item)) {
            if (gettagfast(item).getLong("startAt") == 0) {
                target.motionY += 1.5;
                attacker.motionY += 1.5;
                target.velocityChanged = true;
                attacker.velocityChanged = true;
                TaoCasterData.getTaoCap(target).startRecordingDamage();
                TaoCasterData.getTaoCap(attacker).startRecordingDamage();
                gettagfast(item).setLong("startAt", attacker.world.getTotalWorldTime());
            } else if (getChargedTime(attacker, item) > getMaxChargeTime()) {
                TaoCasterData.getTaoCap(target).setRootTime(0);
                TaoCasterData.getTaoCap(attacker).setRootTime(0);
                attacker.rotationPitch = -30;
                target.motionY -= 3;
                target.velocityChanged = true;
                dischargeWeapon(attacker, item);
            }
        }
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        target.world.createExplosion(attacker, target.posX, target.posY, target.posZ, (float) Math.log(Math.abs(orig - target.getHealth())), false);
        return orig;
    }

    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int qi) {
        if (qi >= 3) {
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, 30, Math.floorDiv(qi, 3) - 1), false);
        }
        if (qi >= 9) {
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.BLINDNESS, 40, 0), false);
        }
    }

    @Override
    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }
}
