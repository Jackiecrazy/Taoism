package com.jackiecrazi.taoism.common.item;

import com.jackiecrazi.taoism.common.item.arrows.TaoArrow;
import com.jackiecrazi.taoism.common.item.weapon.melee.ItemBlueprint;
import com.jackiecrazi.taoism.common.item.weapon.melee.axe.BanFu;
import com.jackiecrazi.taoism.common.item.weapon.melee.dagger.Balisong;
import com.jackiecrazi.taoism.common.item.weapon.melee.dagger.Karambit;
import com.jackiecrazi.taoism.common.item.weapon.melee.club.Chui;
import com.jackiecrazi.taoism.common.item.weapon.melee.hand.Cestus;
import com.jackiecrazi.taoism.common.item.weapon.melee.pick.ChickenSickle;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.pollaxe.Pollaxe;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear.GouLianQiang;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear.QingLongJi;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.staff.Staff;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.svardstav.GuanDao;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.warhammer.ChangChui;
import com.jackiecrazi.taoism.common.item.weapon.melee.sesword.Kampilan;
import com.jackiecrazi.taoism.common.item.weapon.melee.desword.Ken;
import com.jackiecrazi.taoism.common.item.weapon.melee.polearm.spear.Qiang;
import com.jackiecrazi.taoism.common.item.weapon.melee.stick.Tonfa;
import com.jackiecrazi.taoism.common.item.weapon.melee.whip.CatNineTails;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TaoItems {
    public static final ItemArmor.ArmorMaterial TAO = EnumHelper.addArmorMaterial("tao", "tao", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F);
    public static final Item.ToolMaterial TAOW = EnumHelper.addToolMaterial("tao", 0, 1, 1f, 0f, 1);
    public static Item
            kampilan = new Kampilan(),
            cestus = new Cestus(),
            banfu = new BanFu(),
            balisong = new Balisong(),
            karambit = new Karambit(),
            chui = new Chui(),
            geom = new Ken(),
            qiang = new Qiang(),
            tonfa = new Tonfa(),
            chickensickle = new ChickenSickle(),
            ninetail = new CatNineTails(),
            pollaxe = new Pollaxe(),
            changchui = new ChangChui(),
            goulianqiang = new GouLianQiang(),
            qinglongji = new QingLongJi(),
            gun = new Staff(),
            yyd = new GuanDao();


    //public static ItemDummy part = new ItemDummy();
    //public static TaoWeapon weap = new TaoWeapon();
    //public static TaoBow bow = new TaoBow();
    public static ItemBlueprint blueprint = new ItemBlueprint();
    public static TaoArrow arrow = new TaoArrow();

    //public static TaoArmor helm = new TaoArmor(EntityEquipmentSlot.HEAD), chest = new TaoArmor(EntityEquipmentSlot.CHEST), leg = new TaoArmor(EntityEquipmentSlot.LEGS), boot = new TaoArmor(EntityEquipmentSlot.FEET);
    //public static TaoArmor[] armor = {boot, leg, chest, helm};

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Item> e) {
        //base
        //e.getRegistry().register(part);
        //e.getRegistry().register(blueprint);
        e.getRegistry().register(kampilan);
        e.getRegistry().register(cestus);
        e.getRegistry().register(geom);
        e.getRegistry().register(banfu);
        e.getRegistry().register(balisong);
        e.getRegistry().register(karambit);
        e.getRegistry().register(chui);
        e.getRegistry().register(qiang);
        e.getRegistry().register(tonfa);
        e.getRegistry().register(ninetail);
        e.getRegistry().register(chickensickle);
        e.getRegistry().register(pollaxe);
        e.getRegistry().register(changchui);
        e.getRegistry().register(goulianqiang);
        e.getRegistry().register(qinglongji);
        e.getRegistry().register(gun);
        e.getRegistry().register(yyd);
        //weapon
        //e.getRegistry().register(weap);
        //armor
//        e.getRegistry().register(helm);
//        e.getRegistry().register(chest);
//        e.getRegistry().register(leg);
//        e.getRegistry().register(boot);
        //archery
        //e.getRegistry().register(bow);
        //e.getRegistry().register(arrow);
    }

}
