package com.jackiecrazi.taoism.potions;

import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaoPotion extends Potion {


    public static Potion HIDE = null;
    /**
     * deals 1 damage per second, +0.5 per additional layer, and prevents healing
     */
    public static Potion BLEED = null;
    /**
     * prevents posture regeneration
     */
    public static Potion ENFEEBLE = null;
    /**
     * prevents stagger
     */
    public static Potion RESOLUTION = null;
    /**
     * reduces armor by 2 per level
     */
    public static Potion ARMORBREAK = null;
    /**
     * does nothing until detonated
     */
    public static Potion HEMORRHAGE = null;
    /**
     * increases incoming posture and non-magical damage, generally paired with bleed
     */
    public static Potion LACERATION = null;
    private int interval = 0;

    private TaoPotion(boolean isBad, int colour) {
        super(isBad, colour);
    }

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Potion> event) {
        HIDE = new TaoPotion(true, 0).setRegistryName("hide").setPotionName("hiding");
        BLEED = new TaoPotion(true, new Color(187, 10, 30).getRGB()).procInterval(20).setRegistryName("bleed").setPotionName("bleed");
        ENFEEBLE = new TaoPotion(true, new Color(255, 255, 0).getRGB()).setRegistryName("enfeeble").setPotionName("enfeeble")
                .registerPotionAttributeModifier(TaoEntities.POSREGEN, "CC5AF142-2BD2-4215-B636-2605AED11727", -1, 2);
        RESOLUTION = new TaoPotion(false, new Color(0xFC6600).getRGB()).setRegistryName("resolution").setPotionName("resolution");
        ARMORBREAK = new TaoPotion(true, new Color(255, 233, 54).getRGB()).setRegistryName("armorBreak").setPotionName("armorBreak")
                .registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "CC5AF142-2BD2-4215-B636-2605AED11728", -2, 0);
        HEMORRHAGE = new TaoPotion(true, new Color(140, 10, 30).getRGB()).setRegistryName("internalBleed").setPotionName("internalBleed");
        LACERATION = new TaoPotion(true, new Color(140, 10, 30).getRGB()).setRegistryName("laceration").setPotionName("laceration");
        event.getRegistry().register(BLEED);
        event.getRegistry().register(HIDE);
        event.getRegistry().register(ARMORBREAK);
        event.getRegistry().register(RESOLUTION);
        event.getRegistry().register(HEMORRHAGE);
        event.getRegistry().register(LACERATION);
        event.getRegistry().register(ENFEEBLE);
    }

    private TaoPotion procInterval(int interval) {
        this.interval = interval;
        return this;
    }

    @SubscribeEvent
    public static void apply(PotionEvent.PotionAddedEvent e) {
        PotionEffect old = e.getOldPotionEffect();
        PotionEffect current = e.getPotionEffect();
        EntityLivingBase elb = e.getEntityLiving();
        if (current.getPotion() == RESOLUTION) {
            //DETERMINATION!
            TaoCasterData.getTaoCap(elb).setPosInvulTime(e.getPotionEffect().getDuration());
        }
        if (current.getPotion() != HEMORRHAGE && elb.getActivePotionEffect(HEMORRHAGE) != null && current.getPotion().isBadEffect()) {
            PotionEffect pe = elb.getActivePotionEffect(HEMORRHAGE);
            elb.removeActivePotionEffect(HEMORRHAGE);
            current.combine(new PotionEffect(current.getPotion(), current.getDuration() + (pe.getDuration() * (pe.getAmplifier() + 1) / 4), current.getAmplifier()));
        }
    }

    @SubscribeEvent
    public static void pain(LivingHurtEvent e) {
        DamageSource ds = e.getSource();
        if (e.getEntityLiving().getActivePotionEffect(LACERATION) != null && !isSpecialDamage(ds)) {
            e.setAmount(e.getAmount() * 1 + ((e.getEntityLiving().getActivePotionEffect(LACERATION).getAmplifier() + 1) * 0.2f));
        }
    }

    private static boolean isSpecialDamage(DamageSource ds) {
        return !ds.damageType.equals("bleed") && (ds.isMagicDamage() || ds.isUnblockable() || ds.isDamageAbsolute());
    }

    @SubscribeEvent
    public static void nope(LivingHealEvent e) {
        if (e.getEntityLiving().getActivePotionEffect(BLEED) != null) {
            float debuff = (e.getEntityLiving().getActivePotionEffect(BLEED).getAmplifier() + 1) * 0.3f;
            if (debuff >= 1) e.setCanceled(true);
            e.setAmount(e.getAmount() * 1 - debuff);
        }
    }

    @Override
    public void performEffect(EntityLivingBase l, int amplifier) {
        if (this == BLEED) {
            l.hurtResistantTime = 0;
            l.attackEntityFrom(DamageSourceBleed.causeBleedingDamage(), 1 + (amplifier / 2f));
            l.hurtResistantTime = 0;
//			if (amplifier >= 5) {
//				l.attackEntityFrom(DamageSource.GENERIC, l.getMaxHealth() / 10);
//				l.removePotionEffect(TaoPotions.Bleed);
//			}//essentially useless so archived for now
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return interval != 0 && duration % interval == 1;
    }

    public void applyAttributesModifiersToEntity(EntityLivingBase elb, AbstractAttributeMap am, int amp) {
        super.applyAttributesModifiersToEntity(elb, am, amp);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderInvText(PotionEffect effect) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderHUD(PotionEffect effect) {
        return true;
    }

    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
