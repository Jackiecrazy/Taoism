package com.jackiecrazi.taoism.common.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class FakeExplosion extends Explosion {
    private final double x;
    private final double y;
    private final double z;
    private final float strength;
    private EntityLivingBase instigator;
    private Entity proxy;
    private World world;

    private FakeExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
        super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
        proxy = entityIn;
        world = worldIn;
        this.x = x;
        this.y = y;
        this.z = z;
        strength = size;
    }

    public static FakeExplosion explode(World w, Entity proxy, EntityLivingBase causedBy, double x, double y, double z, float strength) {
        FakeExplosion explosion = new FakeExplosion(w, proxy, x, y, z, strength, false, true).setDetonator(causedBy);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(w, explosion)) return explosion;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    private FakeExplosion setDetonator(EntityLivingBase elb) {
        instigator = elb;
        return this;
    }

    @Override
    public void doExplosionA() {
        if(world instanceof WorldServer)
        super.doExplosionA();
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
//        if(!(world instanceof WorldServer))return;
//        WorldServer ws= (WorldServer)world;
//        this.world.playSound((EntityPlayer)null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
//
//        if (this.strength >= 2.0F)
//        {
//            ws.spawnParticle().spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
//        }
//        else
//        {
//            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
//        }
        super.doExplosionB(spawnParticles);
    }

    @Nullable
    @Override
    public EntityLivingBase getExplosivePlacedBy() {
        if (instigator != null) return instigator;
        return super.getExplosivePlacedBy();
    }

    public Entity getProxy() {
        return proxy;
    }
}
