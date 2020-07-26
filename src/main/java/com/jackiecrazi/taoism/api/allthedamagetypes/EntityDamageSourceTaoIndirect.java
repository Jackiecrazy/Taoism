package com.jackiecrazi.taoism.api.allthedamagetypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

/**
 * a literal copy-paste of EntityDamageSourceIndirect to circumvent most types of mob restrictions (guardians, valkyries)
 */
public class EntityDamageSourceTaoIndirect extends EntityDamageSource {
    /** The entity who created the direct source, e.g. the shooter of an arrow */
    private final Entity indirectEntity;

    public EntityDamageSourceTaoIndirect(String damageTypeIn, Entity proxy, @Nullable Entity caster)
    {
        super(damageTypeIn, proxy);
        this.indirectEntity = caster;
    }

    /**
     * Retrieves the immediate causer of the damage, e.g. the arrow entity, not its shooter
     */
    @Nullable
    public Entity getImmediateSource()
    {
        return this.damageSourceEntity;
    }

    /**
     * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
     * etc.
     */
    @Nullable
    public Entity getTrueSource()
    {
        return this.indirectEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn)
    {
        ITextComponent itextcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
        ItemStack itemstack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItemMainhand() : ItemStack.EMPTY;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return !itemstack.isEmpty() && itemstack.hasDisplayName() && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, new Object[] {entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getTextComponent()}) : new TextComponentTranslation(s, new Object[] {entityLivingBaseIn.getDisplayName(), itextcomponent});
    }
}
