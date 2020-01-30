package com.jackiecrazi.taoism.moves.melee;

import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MoveSanMiguel extends MeleeMove {
    private float length=4f;

    public MoveSanMiguel(World worldIn) {
        super(worldIn);
    }

    public MoveSanMiguel(World worldIn, EntityLivingBase attacker, ItemStack stack) {
        super(worldIn, attacker, stack);
        if(stack.getItem() instanceof IRange){
            IRange icr=(IRange)stack.getItem();

        }
    }

    public MoveSanMiguel(EntityLivingBase attacker) {
        super(attacker);
    }
    //deals damage, from right to left, top to bottom. 90º in front of the user, 45 to each side

    @Override
    public ArrayList<Entity> compileList(EntityLivingBase attacker, ItemStack stack, int duration) {
        //System.out.println(duration);
        if(duration!=5)return new ArrayList<>();
        //System.out.println("executing san miguel");
        ArrayList<EntityLivingBase> list = (ArrayList<EntityLivingBase>) attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(length,0.25,length));
        ArrayList<Entity> ret= (ArrayList<Entity>)list.clone();
        for (Entity e:list){
            //System.out.println("found entity "+e.toString());
            e.hurtResistantTime=0;
            boolean scrub=false;
            if(e==attacker){
                //System.out.println("cannot attack self!");
                scrub=true;
            }
            else if(e.getDistanceSq(attacker)>length*length){
                //System.out.println("too far! "+length);
                scrub=true;
            }
            else {
                Vec3d distvec = attacker.getPositionVector().subtract(e.getPositionVector());
                if(-attacker.getLookVec().dotProduct(distvec)/(distvec.lengthVector()*attacker.getLookVec().lengthVector())<0.6){
                    //System.out.println("angle wrong! "+attacker.getLookVec().dotProduct(distvec)/(distvec.lengthVector()*attacker.getLookVec().lengthVector()));
                    scrub=true;
                }
            }
            if(scrub){
                //System.out.println("potato");
                ret.remove(e);
            }
        }
        return ret;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration) {
        return 1f;
    }

    @Override
    public int duration(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack) {
        return 10;
    }
}
