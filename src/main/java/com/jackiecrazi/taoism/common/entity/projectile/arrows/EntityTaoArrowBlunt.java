package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityTaoArrowBlunt extends EntityTaoArrow {
    public EntityTaoArrowBlunt(World worldIn) {
        super(worldIn);
    }

    public EntityTaoArrowBlunt(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public EntityTaoArrowBlunt(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter, is);
    }

    public EntityTaoArrowBlunt(World worldIn, ItemStack is) {
        super(worldIn, is);
    }

    @Override
    protected void onHit(RayTraceResult ray)
    {
        super.onHit(ray);
    }

    @Override
    public int getDamageType(ItemStack is) {
        return 0;
    }

    @Override
    protected void arrowHit(EntityLivingBase living)
    {
        TaoPotionUtils.attemptAddPot(living, new PotionEffect(MobEffects.SLOWNESS,20));
    }
}
