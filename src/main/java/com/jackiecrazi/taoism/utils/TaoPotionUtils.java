package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Map;

public class TaoPotionUtils {
    //so you think immunity to my potions is clever, eh?

    public static void disorient(EntityLivingBase elb, int duration) {
        if (elb instanceof EntityLiving) {
            EntityLiving el = (EntityLiving) elb;
            el.getNavigator().clearPath();
            el.setAttackTarget(null);
        }
        attemptAddPot(elb, new PotionEffect(TaoPotion.DISORIENT, duration, 0), false);
    }

    public static void blind(EntityLivingBase elb, int duration, int potency) {
        if (elb instanceof EntityLiving) {
            EntityLiving el = (EntityLiving) elb;
            el.getNavigator().clearPath();
            el.setAttackTarget(null);
        }
        attemptAddPot(elb, new PotionEffect(MobEffects.BLINDNESS, duration, potency), false);
    }

    /**
     * Attempts to add the potion effect. If it fails, the function will *permanently* apply all the attribute modifiers, with the option to stack them as well
     * Take that, wither!
     */
    public static boolean attemptAddPot(EntityLivingBase elb, PotionEffect pot, boolean stackWhenFailed) {
        Potion p = pot.getPotion();
        elb.removeActivePotionEffect(p);
        elb.addPotionEffect(pot);
        if (!elb.isPotionActive(p)) {
            for (Map.Entry<IAttribute, AttributeModifier> e : p.getAttributeModifierMap().entrySet()) {
                if (elb.getEntityAttribute(e.getKey()) != null) {
                    if (stackWhenFailed) {
                        AttributeModifier am = elb.getEntityAttribute(e.getKey()).getModifier(e.getValue().getID());
                        if (am != null && am.getOperation() == e.getValue().getOperation()) {
                            AttributeModifier apply = new AttributeModifier(e.getValue().getID(), e.getValue().getName(), am.getAmount() + e.getValue().getAmount(), am.getOperation());
                            elb.getEntityAttribute(e.getKey()).removeModifier(e.getValue().getID());
                            elb.getEntityAttribute(e.getKey()).applyModifier(apply);
                        } else elb.getEntityAttribute(e.getKey()).applyModifier(e.getValue());
                    } else {
                        elb.getEntityAttribute(e.getKey()).removeModifier(e.getValue().getID());
                        elb.getEntityAttribute(e.getKey()).applyModifier(e.getValue());
                    }
                }
            }
            //add goes here
            return false;
        }
        return true;
    }

    /**
     * justice prevails.
     */
    public static void forceBleed(EntityLivingBase elb, Entity attacker, int duration, int potency, POTSTACKINGMETHOD method) {
        PotionEffect pe = stackPot(elb, new PotionEffect(TaoPotion.BLEED, duration, potency, true, false), method);
        if (pe.getDuration() > 1200 || pe.getAmplifier() > 9) {
            elb.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), pe.getAmplifier() * 2 * Math.max((pe.getDuration() - 6000) / 20, 1));
            elb.removeActivePotionEffect(TaoPotion.BLEED);
            return;
        }
        if (!attemptAddPot(elb, pe, true)) {
            //pe = new PotionEffect(TaoPotion.BLEED, Math.min(pe.getDuration(), 6000), 99999, true, false);
            elb.hurtResistantTime = 0;
            elb.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), potency);
            elb.hurtResistantTime = 0;
            elb.getActivePotionMap().put(TaoPotion.BLEED, pe);
        }
    }

    /**
     * increases the potion amplifier on the entity, with options on the duration
     */
    public static PotionEffect stackPot(EntityLivingBase elb, PotionEffect toAdd, POTSTACKINGMETHOD method) {
        Potion p = toAdd.getPotion();
        PotionEffect pe = elb.getActivePotionEffect(p);
        if (pe == null || method == POTSTACKINGMETHOD.NONE) {
            //System.out.println("beep1");
            return toAdd;
        }
        //System.out.println(pe);
        int length = pe.getDuration();
        int potency = pe.getAmplifier() + 1 + toAdd.getAmplifier();
        //System.out.println(length);
        //System.out.println(potency);

        switch (method) {
            case ADD:
                length = toAdd.getDuration()+pe.getDuration();
                break;
            case MAXDURATION:
                length = Math.max(pe.getDuration(), toAdd.getDuration());
                break;
            case MAXPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.max(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() > toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
            case MINDURATION:
                length = Math.min(pe.getDuration(), toAdd.getDuration());
                break;
            case MINPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.min(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() < toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
            case ONLYADD:
                potency=toAdd.getAmplifier();
                length = toAdd.getDuration()+pe.getDuration();
                break;
        }
        //System.out.println(ret);
        return new PotionEffect(p, length, potency, false, false);
    }

    public static double getEffectiveLevel(EntityLivingBase elb, Potion p, IAttribute workOff) {
        IAttributeInstance iai = elb.getEntityAttribute(workOff);
        AttributeModifier am = iai.getModifier(p.getAttributeModifierMap().get(workOff).getID());

        if (am != null && p.getAttributeModifierMap().get(workOff) != null)
            return (am.getAmount()) / p.getAttributeModifierAmount(0, p.getAttributeModifierMap().get(workOff));
        return 0;
    }

    public enum POTSTACKINGMETHOD {
        NONE,
        ADD,
        MAXDURATION,
        MAXPOTENCY,
        MINDURATION,
        MINPOTENCY,
        ONLYADD
    }
}
