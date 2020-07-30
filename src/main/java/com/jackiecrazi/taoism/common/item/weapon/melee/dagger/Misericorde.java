package com.jackiecrazi.taoism.common.item.weapon.melee.dagger;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Misericorde extends TaoWeapon {
    /*
    慈悲之刺
    Ignores all armor and doubles damage when striking a downed target.
    Intended for use with knockdown weapons or extensive grappling.
     */
    public Misericorde() {
        super(2, 2, 5, 0);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("misericorde.stagger"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 2;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return TaoCasterData.getTaoCap(target).getDownTimer() > 0 ? target.getTotalArmorValue() : 0;
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 2;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() < CombatConfig.rollThreshold&&collidingEntity instanceof EntityLivingBase) {
            TaoMovementUtils.kick(elb, (EntityLivingBase) collidingEntity);
            NeedyLittleThings.knockBack(elb, collidingEntity, 2, true);
            return true;
        }
        return false;
    }
}
