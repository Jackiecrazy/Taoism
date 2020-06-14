package com.jackiecrazi.taoism.moves.melee;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class MeleeMove extends EntityMove {
    private static final UUID u = UUID.fromString("ba89f1ca-e8a4-47a2-ad79-eb06a9bd0d77");

    public MeleeMove(World worldIn) {
        super(worldIn);
    }

    public MeleeMove(World worldIn, EntityLivingBase attacker, ItemStack stack) {
        super(worldIn, attacker, stack);
    }

    public MeleeMove(EntityLivingBase attacker) {
        super(attacker);
    }

    @Override
    public void attack(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration) throws IllegalAccessException {
        //AttributeModifier att=new AttributeModifier(u,"quickie",this.damageMultiplier(attacker,defender,stack,duration),2);
        //attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(att);
        if (attacker instanceof EntityPlayer) {
            //System.out.println("a");
            EntityPlayer p = ((EntityPlayer) attacker);
            EnumHand hand = p.getHeldItemMainhand() == stack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            TaoCombatUtils.rechargeHand(attacker, hand, 1f);
            defender.hurtResistantTime = 0;
            TaoCombatUtils.taoWeaponAttack(defender, p, stack, hand==EnumHand.MAIN_HAND, true);
        } else
            attacker.attackEntityAsMob(defender);
        //attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(u);

    }
}
