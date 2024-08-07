package com.jackiecrazi.taoism.common.item.weapon.melee.desword;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.fx.EntityEvidence;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ExecutionerSword extends TaoWeapon {
    /*
    诛戮之剑
    After striking an enemy once, its sins will manifest as tormented spirits in a 8 block radius around the enemy once per 5~10 seconds (decreases with qi)
    The enemy is now "on trial". Attacking any entity will end the trial and consume the spirits for a qi boost. You'll need it, trust me.
    the spirits can be collected by walking over them, each opens one eye of judgement.
    When opponent's hp is less than eye^2, mark the enemy for judgement.
    A judged enemy instantly dies, stuns nearby enemies and is guaranteed to drop a head on attack.
    If you attack before the enemy is judged, it deals normal damage and halves the amount of time before the next spirit manifests
    Every 5 damage reduces an eye
    When looking at a spirit or the defendant, gain a speed boost
    Execution for it could also just be something simple,
    like casting a huge sweep that knocks away all enemies other than the trial target and drastically slows them with a blind,
    while if the main is hit within the sweep it immobilizes and disables them temporarily on the spot based on secondary targets affected
    and increases current eyes by 25%ish. Could be nicknamed "Order in the court"
     */

    public ExecutionerSword() {
        super(1, 1.4, 5, 2.9f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SWORD;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.9f;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (getHand(stack)!=null && e.ticksExisted % (200 - getQiFromStack(stack) * 10) == 0 && getLastAttackedEntity(w, stack) instanceof EntityLivingBase && !w.isRemote) {
            EntityLivingBase sinner = (EntityLivingBase) getLastAttackedEntity(w, stack);
            EntityEvidence ee = new EntityEvidence(w, sinner);
            w.spawnEntity(ee);
        }
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("exesword.trial"));
        tooltip.add(I18n.format("exesword.evidence"));
        tooltip.add(I18n.format("exesword.eye"));
        tooltip.add(I18n.format("exesword.decrease"));
        tooltip.add(I18n.format("exesword.addqi"));
        tooltip.add(I18n.format("exesword.execution"));
        tooltip.add(I18n.format("exesword.head"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 3;
    }

    @Override
    public boolean canCharge(EntityLivingBase wielder, ItemStack item) {
        return false;
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        super.dischargeWeapon(elb, item);
        setBuff(elb, item, "souls", 0);
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds,attacker,target,stack,orig);
        if (target.getEntityId() != getLastAttackedEntityID(stack)) {
            TaoCasterData.getTaoCap(attacker).addQi(getBuff(stack, "souls") / 10f);
            setBuff(attacker, stack, "souls", 0);
        } else if (getBuff(stack, "souls") * getBuff(stack, "souls") >= target.getHealth()*(TaoCasterData.getTaoCap(target).getDownTimer()>0?0.5:1)) {
            chargeWeapon(attacker, stack);
        }
    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (isCharged(attacker, stack)) {
            ds.setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode();
            attacker.world.addWeatherEffect(new EntityLightningBolt(attacker.world, target.posX, target.posY, target.posZ, true));
            dischargeWeapon(attacker, stack);
            TaoCasterData.getTaoCap(target).setMustDropHead(true);
            return target.getMaxHealth() * 5;
        }
        return orig;
    }

    @Override
    public float onBeingHurt(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount) {
        float cumulativeDamage = getBuff(item, "takenDamage") + amount;
        int eyes = getBuff(item, "souls");
        while (cumulativeDamage >= 5) {
            eyes--;
            cumulativeDamage -= 5;
        }
        setBuff(defender, item, "takenDamage", (int) cumulativeDamage);
        setBuff(defender, item, "souls", eyes);
        return amount;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (!attacker.world.isRemote) {
            EntityEvidence ee = new EntityEvidence(attacker.world, target);
            attacker.world.spawnEntity(ee);
        }
    }

    public boolean attemptAbsorbSoul(EntityLivingBase elb, ItemStack is, EntityEvidence soul) {
        if (getLastAttackedEntity(elb.world, is) instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) getLastAttackedEntity(elb.world, is);
            if (soul.getSinner() == target) {
                setBuff(elb, is, "souls", getBuff(is, "souls") + 1);
                if(elb instanceof EntityPlayer)
                    ((EntityPlayer)elb).sendStatusMessage(new TextComponentTranslation("exesword.count", getBuff(is, "souls")), true);
                return true;
            }
        }
        return false;
    }
}
