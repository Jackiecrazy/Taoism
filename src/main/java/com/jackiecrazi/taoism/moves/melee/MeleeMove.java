package com.jackiecrazi.taoism.moves.melee;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class MeleeMove extends EntityMove {

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
            TaoCombatUtils.rechargeHand(attacker, hand, 1f, true);
            defender.hurtResistantTime = 0;
            TaoCombatUtils.taoWeaponAttack(defender, p, stack, hand==EnumHand.MAIN_HAND, true);
        } else
            attacker.attackEntityAsMob(defender);
        //attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(u);

    }
}
