package com.jackiecrazi.taoism.common.entity.projectile.weapons;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMeidoZangetsuha extends EntitySwordBeamBase {

    public EntityMeidoZangetsuha(World w) {
        super(w);
    }

    public EntityMeidoZangetsuha(EntityLivingBase throwerIn, EnumHand hand, ItemStack is) {
        super(throwerIn.world, throwerIn, hand, is);
        int rot=rand.nextInt(360);
        setRenderRotation(rot);
        setSize(MathHelper.sin(NeedyLittleThings.rad(rot)) * 4, MathHelper.cos(NeedyLittleThings.rad(rot)) * 4);
    }


}
