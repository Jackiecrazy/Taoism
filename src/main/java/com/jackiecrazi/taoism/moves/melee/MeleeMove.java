package com.jackiecrazi.taoism.moves.melee;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.UUID;

public abstract class MeleeMove extends EntityMove {
    private static final UUID u=UUID.fromString("ba89f1ca-e8a4-47a2-ad79-eb06a9bd0d77");
    private static final Field atk= ReflectionHelper.findField(EntityLivingBase.class,"field_184617_aD","ticksSinceLastSwing","aE");

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
        if(attacker instanceof EntityPlayer){

            //System.out.println("a");
            EntityPlayer p= ((EntityPlayer)attacker);
           // ReflectionHelper.setPrivateValue(EntityLivingBase.class, p, 1600,"field_184617_aD","ticksSinceLastSwing","aE");
            atk.setInt(p,1600);
//NeedyLittleThings.taoWeaponAttack(defender,p,stack,false);
            defender.hurtResistantTime=0;
            p.attackTargetEntityWithCurrentItem(defender);
            //p.getCooldownTracker().setCooldown(stack.getItem(),20);
            //defender.hurtResistantTime=0;
            //defender.attackEntityFrom(DamageSource.causePlayerDamage(p), (float) p.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

        }
        else
            attacker.attackEntityAsMob(defender);
        //attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(u);

    }
}
