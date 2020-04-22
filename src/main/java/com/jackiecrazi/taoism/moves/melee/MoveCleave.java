package com.jackiecrazi.taoism.moves.melee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MoveCleave extends MeleeMove {
    private float length=4f;

    public MoveCleave(World worldIn) {
        super(worldIn);
    }

    public MoveCleave(World worldIn, EntityLivingBase attacker, ItemStack stack) {
        super(worldIn, attacker, stack);
    }

    public MoveCleave(EntityLivingBase attacker) {
        super(attacker);
    }
    //deals damage, from right to left, top to bottom. 90º in front of the user, 45 to each side

    @Override
    public ArrayList<Entity> compileList(EntityLivingBase attacker, ItemStack stack, int duration) {
        System.out.println("executing cleave");
        if(duration!=10)return new ArrayList<>();
        ArrayList<Entity> list = (ArrayList<Entity>) attacker.world.getEntitiesWithinAABBExcludingEntity(attacker, attacker.getEntityBoundingBox().expand(length,length,length));
        ArrayList<Entity> ret= (ArrayList<Entity>)list.clone();
        for (Entity e:list){
            System.out.println("found entity");
            e.hurtResistantTime=0;
            if(!(e instanceof EntityLivingBase))ret.remove(e);
            else if(e.getDistanceSq(attacker)>length*length)ret.remove(e);
            else {
                Vec3d distvec = attacker.getPositionVector().subtract(e.getPositionVector());
                if(Math.abs(attacker.getLookVec().dotProduct(distvec)/(distvec.lengthVector()*attacker.getLookVec().lengthVector()))<0.9)ret.remove(e);
            }
        }
        return ret;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration) {
        return 2f;
    }

    @Override
    public int duration(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack) {
        return 10;
    }
}
