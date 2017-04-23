package com.Jackiecrazi.taoism.common.items;

import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class DummySlotItem extends Item {
	//public static IIcon metal, wood, water, fire, earth, wind, thunder, yin, yang, sha;
	public static IIcon emptySlot;
	/**
	 * headdress, mirror, belt, faqi, amulet, ring, glove, back, cloak, wuji, gongfa
	 */
	public static IIcon[] mark;
	public DummySlotItem() {
		this.setUnlocalizedName("youshouldnthavethis");
	}
	@SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir)
    {
		emptySlot=ir.registerIcon("taoism:dummy/slot");
    	mark=new IIcon[EnumEquipmentType.values().length];
    	for(int x=0;x<mark.length;x++){
    		mark[x]=ir.registerIcon("taoism:dummy/empty"+EnumEquipmentType.values()[x]);
    	}
    }
}
