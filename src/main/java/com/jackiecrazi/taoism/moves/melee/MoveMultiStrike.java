package com.jackiecrazi.taoism.moves.melee;

import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class MoveMultiStrike extends MeleeMove {
    private Entity target;
    private int interval, durations;

    public MoveMultiStrike(World worldIn) {
        super(worldIn);
    }

    public MoveMultiStrike(EntityLivingBase attacker, Entity target, int interval, int duration) {
        super(attacker);
        this.target = target;
        this.interval = interval;
        durations = duration;
    }

    @Nonnull
    @Override
    public ArrayList<Entity> compileList(EntityLivingBase attacker, ItemStack stack, int duration) {
        ArrayList<Entity> ret = new ArrayList<>();
        if (stack.getItem() instanceof IRange) {
            if (((IRange) stack.getItem()).getReach(attacker, stack) * ((IRange) stack.getItem()).getReach(attacker, stack) < target.getDistanceSq(attacker))
                return ret;
        }
        if (ticksExisted % interval == 0) {
            attacked.clear();
            ret.add(target);
        }
        return ret;
    }


    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration) {
        return 0;
    }

    @Override
    public int duration(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack) {
        return durations;
    }
}
