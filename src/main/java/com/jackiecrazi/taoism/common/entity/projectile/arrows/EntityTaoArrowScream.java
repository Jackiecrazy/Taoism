package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class EntityTaoArrowScream extends EntityTaoProjectile {
    @Override
    public int getDamageType(ItemStack is) {
        return 0;
    }

    public EntityTaoArrowScream(World worldIn) {
        super(worldIn);
    }

    public EntityTaoArrowScream(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public EntityTaoArrowScream(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter, is);
    }

    public EntityTaoArrowScream(World worldIn, ItemStack is) {
        super(worldIn, is);
    }

    @Override
    protected void updateInAir() {
            super.updateInAir();
            float maxvel=10f,vel=(float)(motionX*motionX+motionY*motionY+motionZ*motionZ);
            this.world.playSound(this.posX,this.posY,this.posZ, SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.NEUTRAL,vel/maxvel,vel/(maxvel),true);
    }
}
