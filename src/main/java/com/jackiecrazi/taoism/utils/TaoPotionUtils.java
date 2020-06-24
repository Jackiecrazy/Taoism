package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.potions.TaoPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Map;

public class TaoPotionUtils {
    //so you think immunity to my potions is clever, eh?

    /**
     * justice prevails.
     */
    public static void forceBleed(EntityLivingBase elb, Entity attacker, int duration, int potency, POTSTACKINGMETHOD method) {
        PotionEffect pe = stackPot(elb, new PotionEffect(TaoPotion.BLEED, duration, potency, false, false), method);
        if (!attemptAddPot(elb, pe, true)) {
            elb.hurtResistantTime = 0;
            elb.attackEntityFrom(DamageSourceBleed.causeEntityBleedingDamage(attacker), (1 + potency) * duration / 10);
            elb.hurtResistantTime = 0;
        }
    }

    /**
     * increases the potion amplifier on the entity, with options on the duration
     */
    public static PotionEffect stackPot(EntityLivingBase elb, PotionEffect toAdd, POTSTACKINGMETHOD method) {
        PotionEffect pe = elb.getActivePotionEffect(toAdd.getPotion());
        if (pe == null || method == POTSTACKINGMETHOD.NONE) {
            return toAdd;
        }
        Potion p = toAdd.getPotion();
        int length = pe.getDuration();
        int potency = pe.getAmplifier() + 1 + toAdd.getAmplifier();

        switch (method) {
            case ADD:
                length += pe.getDuration();
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
        }
        return new PotionEffect(p, length, potency, false, false);
    }

    /**
     * Attempts to add the potion effect. If it fails, the function will *permanently* apply all the attribute modifiers, with the option to stack them as well
     * Take that, wither!
     */
    public static boolean attemptAddPot(EntityLivingBase elb, PotionEffect pot, boolean stackWhenFailed) {
        Potion p = pot.getPotion();
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
            return false;
        }
        return true;
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
    }
}
