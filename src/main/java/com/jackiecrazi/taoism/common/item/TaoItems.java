package com.jackiecrazi.taoism.common.item;

import com.jackiecrazi.taoism.common.item.arrows.TaoArrow;
import com.jackiecrazi.taoism.common.item.weapon.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.axe.ItemBanFu;
import com.jackiecrazi.taoism.common.item.weapon.dagger.ItemBalisong;
import com.jackiecrazi.taoism.common.item.weapon.dagger.ItemKarambit;
import com.jackiecrazi.taoism.common.item.weapon.hammer.ItemChui;
import com.jackiecrazi.taoism.common.item.weapon.hand.ItemCestus;
import com.jackiecrazi.taoism.common.item.weapon.sesword.ItemKampilan;
import com.jackiecrazi.taoism.common.item.weapon.desword.ItemKen;
import com.jackiecrazi.taoism.common.item.weapon.spear.ItemQiang;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TaoItems {
    public static final ItemArmor.ArmorMaterial TAO = EnumHelper.addArmorMaterial("tao", "tao", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F);
    public static final Item.ToolMaterial TAOW=EnumHelper.addToolMaterial("tao",0,1,1f,0f,1);
    public static Item
            kampilan=new ItemKampilan(),
            cestus=new ItemCestus(),
    banfu=new ItemBanFu(),
    balisong=new ItemBalisong(),
    karambit=new ItemKarambit(),
    chui=new ItemChui(),
    geom=new ItemKen(),
    qiang=new ItemQiang();

    //public static ItemDummy part = new ItemDummy();
    //public static TaoWeapon weap = new TaoWeapon();
    //public static TaoBow bow = new TaoBow();
    public static ItemBlueprint blueprint = new ItemBlueprint();
    public static TaoArrow arrow = new TaoArrow();

    //public static TaoArmor helm = new TaoArmor(EntityEquipmentSlot.HEAD), chest = new TaoArmor(EntityEquipmentSlot.CHEST), leg = new TaoArmor(EntityEquipmentSlot.LEGS), boot = new TaoArmor(EntityEquipmentSlot.FEET);
    //public static TaoArmor[] armor = {boot, leg, chest, helm};

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Item> event) {
        //base
        //event.getRegistry().register(part);
        event.getRegistry().register(blueprint);
        event.getRegistry().register(kampilan);
        event.getRegistry().register(cestus);
        event.getRegistry().register(geom);
        event.getRegistry().register(banfu);
        event.getRegistry().register(balisong);
        event.getRegistry().register(karambit);
        event.getRegistry().register(chui);
        event.getRegistry().register(qiang);
        //weapon
        //event.getRegistry().register(weap);
        //armor
//        event.getRegistry().register(helm);
//        event.getRegistry().register(chest);
//        event.getRegistry().register(leg);
//        event.getRegistry().register(boot);
        //archery
        //event.getRegistry().register(bow);
        event.getRegistry().register(arrow);
    }

}
