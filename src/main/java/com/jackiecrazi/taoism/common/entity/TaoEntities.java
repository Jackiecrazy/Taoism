package com.jackiecrazi.taoism.common.entity;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityAxeCleave;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityRopeDart;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityBanfu;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntitySwordBeam;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class TaoEntities {
    //TODO change these to taoism.xxx once 1.16+ rolls out
    public static final IAttribute DEFLECT = (new RangedAttribute(null, "generic.armorDeflect", 0.0D, 0, 1.0D)).setDescription("Deflection").setShouldWatch(true);
    public static final IAttribute ABLATION = (new RangedAttribute(null, "generic.armorAbsorption", 0.0D, 0, 10D)).setDescription("Hard Absorption").setShouldWatch(true);
    public static final IAttribute POSREGEN = (new RangedAttribute(null, "generic.postureRegen", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE)).setDescription("Posture Regeneration").setShouldWatch(true);
    public static final IAttribute HEAL = (new RangedAttribute(null, "generic.healRate", 1.0D, 0, Double.MAX_VALUE)).setDescription("Health Regeneration Multiplier").setShouldWatch(true);
    public static final IAttribute LINGREGEN = (new RangedAttribute(null, "generic.lingRegen", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE)).setDescription("Ling Regeneration").setShouldWatch(true);
    public static final IAttribute QIRATE = (new RangedAttribute(null, "generic.qiGen", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE)).setDescription("Qi Accumulation Rate").setShouldWatch(true);
    public static final IAttribute MAXPOSTURE = (new RangedAttribute(null, "taoism.maxPosture", 10.0D, 0, Double.MAX_VALUE)).setDescription("Max Posture").setShouldWatch(true);
    private static int id=0;
    @SubscribeEvent
    public static void init(RegistryEvent.Register<EntityEntry> e){
//        e.getRegistry().register(factoryArrow(EntityTaoArrowBlunt.class));
//        e.getRegistry().register(factoryArrow(EntityTaoArrowScream.class));
//        e.getRegistry().register(factoryArrow(EntityTaoArrowHarpoon.class));
//        e.getRegistry().register(factoryMove(MoveSanMiguel.class));
//        e.getRegistry().register(factoryMove(MoveCleave.class));
//        e.getRegistry().register(factoryMove(MoveMultiStrike.class));
        e.getRegistry().register(factoryArrow(EntityRopeDart.class));
        e.getRegistry().register(factoryArrow(EntityBanfu.class));
        e.getRegistry().register(factoryArrow(EntitySwordBeam.class));
        e.getRegistry().register(factoryArrow(EntityAxeCleave.class));
    }
    private static EntityEntry factoryMove(Class<?extends EntityMove> move){
        String name = move.getSimpleName().toLowerCase().substring(6);
        return EntityEntryBuilder.create().entity(move).name(name).tracker(64,20,false).id(name,id++).build();
    }
    private static EntityEntry factoryArrow(Class<?extends EntityTaoProjectile> arr){
        String name = arr.getSimpleName().toLowerCase().substring(6);
        return EntityEntryBuilder.create().entity(arr).name(name).tracker(64,20,false).id(name,id++).build();
    }
    private static EntityEntry factoryProjectile(Class<?extends EntityThrowable> projectile){
        String name = projectile.getSimpleName().toLowerCase().substring(6);
        return EntityEntryBuilder.create().entity(projectile).name(name).tracker(64,5,true).id(name,id++).build();
    }
}
