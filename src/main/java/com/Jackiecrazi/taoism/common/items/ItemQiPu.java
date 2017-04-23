package com.Jackiecrazi.taoism.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemQiPu extends Item {

	public static IIcon[] icon;
	public IIcon base,overlay;
	public ItemQiPu() {
		super();
		this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("qipu");
	}
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs xCreativeTabs, List list) {
        for(int x = 0; x < GenericTaoistWeapon.ListOfWeapons.size(); x++){
            list.add(new ItemStack(this, 1, x));
        }
	}
	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		if(pass==0)return base;
		else if(pass==1)return overlay;
        return icon[meta];
    }
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + GenericTaoistWeapon.getRealName(GenericTaoistWeapon.ListOfWeapons.get(stack.getItemDamage()).getUnlocalizedName());
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir)
    {
    	int length = GenericTaoistWeapon.ListOfWeapons.size();
		icon = new IIcon[length];
    	for(int x=0;x< length;x++)
    	icon[x]=ir.registerIcon("taoism:weapon/whole/"+GenericTaoistWeapon.ListOfWeapons.get(x).getUnlocalizedName().substring(5).replace("tairon", ""));
    	base=ir.registerIcon("taoism:weaponparts/scroll");
    	overlay=ir.registerIcon("taoism:weaponparts/scroll1");
    }
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    @Override
	public String getItemStackDisplayName(ItemStack stack) {
		String out = "";
		out+=StatCollector.translateToLocal("qipu");
		out+=StatCollector.translateToLocal(GenericTaoistWeapon.ListOfWeapons.get(stack.getItemDamage()).getUnlocalizedName()+".name");
		return out;
    }
}
