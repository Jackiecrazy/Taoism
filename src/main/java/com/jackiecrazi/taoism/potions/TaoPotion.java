package com.jackiecrazi.taoism.potions;

import com.jackiecrazi.taoism.api.allthedamagetypes.DamageSourceBleed;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaoPotion extends Potion {

    /**
     * adds constant spin to your yaw
     */
    public static Potion DISORIENT = null;

    public static Potion HIDE = null;
    /**
     * deals 1 damage per second, +0.5 per additional layer, and prevents healing
     */
    public static Potion BLEED = null;
    /**
     * decreases max posture
     */
    public static Potion ENFEEBLE = null;
    /**
     * prevents posture regeneration
     */
    public static Potion FATIGUE = null;
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
    /**
     * adds 20% per level to incoming and outgoing damage
     */
    public static Potion ENRAGE = null;
    private int interval = 0;

    private TaoPotion(boolean isBad, int colour) {
        super(isBad, colour);
    }

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Potion> event) {
        HIDE = new TaoPotion(true, 0).setRegistryName("hide").setPotionName("hiding");
        BLEED = new TaoPotion(true, new Color(187, 10, 30).getRGB()).procInterval(20).setRegistryName("bleed").setPotionName("bleed")
                .registerPotionAttributeModifier(TaoEntities.HEAL, "CC5AF142-2BD2-4215-B636-2605AED11727", -0.3, 0);
        FATIGUE = new TaoPotion(true, new Color(250, 200, 0).getRGB()).setRegistryName("fatigue").setPotionName("fatigue")
                .registerPotionAttributeModifier(TaoEntities.POSREGEN, "CC5AF142-2BD2-4215-B636-2605AED11727", -0.3, 1);
        ENFEEBLE = new TaoPotion(true, new Color(167, 161, 155).getRGB()).setRegistryName("enfeeble").setPotionName("enfeeble")
                .registerPotionAttributeModifier(TaoEntities.MAXPOSTURE, "CC5AF142-2BD2-4215-B636-2605AED11727", -3, 0);
        RESOLUTION = new TaoPotion(false, new Color(0xFC6600).getRGB()).setRegistryName("resolution").setPotionName("resolution");
        ARMORBREAK = new TaoPotion(true, new Color(255, 233, 54).getRGB()).setRegistryName("armorBreak").setPotionName("armorBreak")
                .registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "CC5AF142-2BD2-4215-B636-2605AED11728", -2, 0);
        HEMORRHAGE = new TaoPotion(true, new Color(100, 10, 30).getRGB()).setRegistryName("internalBleed").setPotionName("internalBleed");
        LACERATION = new TaoPotion(true, new Color(140, 10, 30).getRGB()).setRegistryName("laceration").setPotionName("laceration");
        DISORIENT = new TaoPotion(true, new Color(70, 70, 70).getRGB()).setRegistryName("disorient").setPotionName("disorient");
        ENRAGE = new TaoPotion(false, new Color(255, 0, 0).getRGB()).procInterval(Integer.MAX_VALUE).setRegistryName("enrage").setPotionName("enrage")
                .registerPotionAttributeModifier(TaoEntities.POSREGEN, "CC5AF142-2BD2-4215-B636-2605AED11729", -0.3, 2);
        MobEffects.POISON
                .registerPotionAttributeModifier(TaoEntities.POSREGEN, "CC5AF142-2BD2-4215-B636-2605AED11727", -0.2, 0);
        event.getRegistry().register(BLEED);
        event.getRegistry().register(HIDE);
        event.getRegistry().register(ARMORBREAK);
        event.getRegistry().register(RESOLUTION);
        event.getRegistry().register(HEMORRHAGE);
        event.getRegistry().register(LACERATION);
        event.getRegistry().register(FATIGUE);
        event.getRegistry().register(ENFEEBLE);
        event.getRegistry().register(DISORIENT);
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
        if (current.getPotion() == MobEffects.BLINDNESS && e.getEntityLiving() instanceof EntityLiving && CombatConfig.blindMobs) {
            ((EntityLiving) e.getEntityLiving()).getNavigator().clearPath();
            ((EntityLiving) e.getEntityLiving()).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public static void pain(LivingHurtEvent e) {
        DamageSource ds = e.getSource();
        if (!isSpecialDamage(ds)) {
            if (e.getEntityLiving().getActivePotionEffect(LACERATION) != null)
                e.setAmount(e.getAmount() * 1 + ((e.getEntityLiving().getActivePotionEffect(LACERATION).getAmplifier() + 1) * 0.2f));
            if (e.getEntityLiving().getActivePotionEffect(ENRAGE) != null)
                e.setAmount(e.getAmount() * 1 + ((e.getEntityLiving().getActivePotionEffect(ENRAGE).getAmplifier() + 1) * 0.2f));
            if (ds.getTrueSource() instanceof EntityLivingBase && ((EntityLivingBase) ds.getTrueSource()).getActivePotionEffect(ENRAGE) != null)
                e.setAmount(e.getAmount() * 1 + (((EntityLivingBase) ds.getTrueSource()).getActivePotionEffect(ENRAGE).getAmplifier() + 1) * 0.2f);
        }
    }

    private static boolean isSpecialDamage(DamageSource ds) {
        return !ds.damageType.equals("bleed") && (ds.isMagicDamage() || ds.isUnblockable() || ds.isDamageAbsolute());
    }

    @SubscribeEvent
    public static void taunt(LivingSetAttackTargetEvent e) {
        if (e.getEntityLiving() instanceof EntityLiving) {
            EntityLiving el = (EntityLiving) e.getEntityLiving();
            Entity taunter = el.world.getEntityByID(TaoCasterData.getTaoCap(el).getTauntID());
            if (taunter instanceof EntityLivingBase && taunter != e.getTarget()) {
                el.setAttackTarget((EntityLivingBase) taunter);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void performEffect(EntityLivingBase l, int amplifier) {
        if (this == BLEED) {
            l.hurtResistantTime = 0;
            l.attackEntityFrom(DamageSourceBleed.causeBleedingDamage(), 1 + (amplifier / 2f));
            if (l.world instanceof WorldServer) {
                ((WorldServer) l.world).spawnParticle(EnumParticleTypes.DRIP_LAVA, l.posX, l.posY + l.height / 2, l.posZ, 20, l.width / 4, l.height / 4, l.width / 4, 0.5f);
            }
            l.hurtResistantTime = 0;
        }
        if (this == ENRAGE) {
            TaoCasterData.getTaoCap(l).tauntedBy(null);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return interval != 0 && duration % interval == 1;
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        if (this == ENRAGE)
            TaoCasterData.getTaoCap(entityLivingBaseIn).tauntedBy(null);
    }

    public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
        if (this == ENRAGE)
            return -1 + modifier.getAmount();
        return modifier.getAmount() * (double) (amplifier + 1);
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
