package com.jackiecrazi.taoism.common.entity.projectile.arrows;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTaoArrowHarpoon extends EntityTaoProjectile {
    @Override
    public int getDamageType(ItemStack is) {
        return 2;
    }

    public EntityTaoArrowHarpoon(World worldIn) {
        super(worldIn);
    }

    public EntityTaoArrowHarpoon(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public EntityTaoArrowHarpoon(World worldIn, EntityLivingBase shooter, ItemStack is) {
        super(worldIn, shooter, is);
    }

    public EntityTaoArrowHarpoon(World worldIn, ItemStack is) {
        super(worldIn, is);
    }

    protected float velocityMultiplier() {
        if (isInWater() && !world.isRemote) {
            for (int i = 0; i < 4; ++i) {
                float f3 = 0.25F;
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * (double) f3, posY - motionY * (double) f3, posZ - motionZ * (double) f3, motionX, motionY, motionZ);
            }
            if (world.rand.nextFloat() < ((motionX * motionX + motionY * motionY + motionZ * motionZ) / 10)) {
                LootTable lt = world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING_FISH);
                LootContext lc = new LootContext.Builder((WorldServer) world).withDamageSource(DamageSource.causeArrowDamage(this, shootingEntity)).build();
                for (ItemStack is : lt.generateLootForPools(world.rand, lc)) {
                    EntityItem ei = new EntityItem(world, posX, posY, posZ, is);
                    ei.startRiding(this, true);
                }
            }
        }
        return 0.99f;
    }
}
