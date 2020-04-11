package com.jackiecrazi.taoism.common.entity.ai;

import com.jackiecrazi.taoism.capability.TaoCasterData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class AIDowned extends EntityAIBase {
    private EntityLiving t;
    public AIDowned(EntityLiving target) {
        t=target;
        this.setMutexBits(0xFFFFFF);
    }

    @Override
    public boolean shouldExecute() {
        return TaoCasterData.getTaoCap(t).getDownTimer()>0;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
